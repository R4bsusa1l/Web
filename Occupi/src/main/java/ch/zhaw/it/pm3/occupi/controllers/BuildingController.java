package ch.zhaw.it.pm3.occupi.controllers;

import ch.zhaw.it.pm3.occupi.model.Building;
import ch.zhaw.it.pm3.occupi.model.BuildingInfo;
import ch.zhaw.it.pm3.occupi.model.Event;
import ch.zhaw.it.pm3.occupi.model.Room;
import ch.zhaw.it.pm3.occupi.model.RoomState;
import ch.zhaw.it.pm3.occupi.reservation.api.ReservationAPI;
import ch.zhaw.it.pm3.occupi.reservation.api.RoomReservation;
import ch.zhaw.it.pm3.occupi.search.FilterCriteriaDTO;
import ch.zhaw.it.pm3.occupi.search.PaginatedSearchResult;
import ch.zhaw.it.pm3.occupi.search.Search;
import ch.zhaw.it.pm3.occupi.search.SearchResultDTO;
import ch.zhaw.it.pm3.occupi.sensors.api.RoomOccupancy;
import ch.zhaw.it.pm3.occupi.sensors.api.SensorAPI;
import ch.zhaw.it.pm3.occupi.storage.BuildingStorageAPI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Controller class for managing buildings and API interactions.
 */
@SuppressWarnings("ChainedMethodCall")
public class BuildingController {

    private final ReservationAPI reservationAPI;
    private final SensorAPI sensorAPI;
    private final BuildingStorageAPI buildingStorageAPI;

    private final List<Building> buildings;
    private final List<BuildingObserver> observers;

    private final ScheduledExecutorService executorService;

    // Time in seconds until reservations and room occupancy are refreshed
    private static final int REFRESH_PERIOD = 30;

    /**
     * Constructor for BuildingController
     *
     * @param reservationAPI     API for managing reservations
     * @param sensorAPI          API for managing sensors
     * @param buildingStorageAPI API for managing building storage
     */
    public BuildingController(ReservationAPI reservationAPI, SensorAPI sensorAPI, BuildingStorageAPI buildingStorageAPI) {
        this.reservationAPI = Objects.requireNonNull(reservationAPI);
        this.sensorAPI = Objects.requireNonNull(sensorAPI);
        this.buildingStorageAPI = Objects.requireNonNull(buildingStorageAPI);
        this.buildings = buildingStorageAPI.getBuildings();
        this.observers = new ArrayList<>();

        // Get number of CPU cores to use in thread pool
        int cores = Runtime.getRuntime().availableProcessors();

        this.executorService = Executors.newScheduledThreadPool(cores);
        // Schedule the update job to run at fixed intervals
        this.executorService.scheduleWithFixedDelay(new UpdateJob(), 0, REFRESH_PERIOD, TimeUnit.SECONDS);

    }

    /**
     * Searches for rooms based on the given filter criteria with pagination support.
     *
     * @param criteria  The filter criteria to search rooms.
     * @param pageIndex The page number to retrieve (0-based).
     * @param pageSize  The number of results per page.
     * @return PaginatedSearchResult containing the search results with pagination metadata.
     */
    public PaginatedSearchResult searchRoomsPaginated(FilterCriteriaDTO criteria, int pageIndex, int pageSize) {
        Objects.requireNonNull(criteria);

        String query = criteria.searchQuery();
        List<Building> matchedBuildings = buildings.stream()
                .filter(building -> building.matchesCriteria(criteria))
                .collect(Collectors.toList());
        List<SearchResultDTO> searchResultDTOList = new ArrayList<>();


        // Include all rooms from buildings that match the search query in the results
        if (Search.isNotBlank(query)) {
            List<Building> matchedQueryBuildings = buildings.stream()
                    .filter(building -> building.matchesQuery(query))
                    .filter(building -> building.matchesCriteria(criteria))
                    .toList();

            for (Building building : matchedQueryBuildings) {
                BuildingInfo buildingInfo = building.buildingInfo();
                for (Room room : building.rooms()) {
                    searchResultDTOList.add(new SearchResultDTO(buildingInfo, room));
                }
            }
            matchedBuildings.removeAll(matchedQueryBuildings);
        }

        // If any criteria aside from search are set, filter the matched rooms again
        if(Search.containsCriteriaNoQuery(criteria)){
            searchResultDTOList = searchResultDTOList.stream()
                    .filter((dto -> dto.room().matchesCriteria(criteria)))
                    .collect(Collectors.toList());
        }

        for (Building building : matchedBuildings) {
            BuildingInfo buildingInfo = building.buildingInfo();
            List<Room> matchingRooms = building.getMatchingRooms(criteria);

            for (Room room : matchingRooms) {
                searchResultDTOList.add(new SearchResultDTO(buildingInfo, room));
            }
        }
        int totalResults = searchResultDTOList.size();

        return createPaginatedSearchResult(searchResultDTOList, pageIndex, pageSize, totalResults);
    }

    /**
     * Creates a PaginatedSearchResult from the list of SearchResultDTOs.
     *
     * @param searchResultDTOList List of SearchResultDTOs.
     * @param pageIndex           The page number to retrieve (0-based).
     * @param pageSize            The number of results per page.
     * @param totalResults        The total number of results.
     * @return PaginatedSearchResult containing the paginated search results.
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    private PaginatedSearchResult createPaginatedSearchResult(List<SearchResultDTO> searchResultDTOList, int pageIndex, int pageSize, int totalResults) {
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);

        int startIndex = pageIndex * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalResults);

        List<SearchResultDTO> pageResults = searchResultDTOList.subList(startIndex, endIndex);
        Map<BuildingInfo, List<Room>> groupedResults = new HashMap<>();

        for (SearchResultDTO searchResult : pageResults) {
            BuildingInfo buildingInfo = searchResult.buildingInfo();
            Room room = searchResult.room();

            groupedResults
                    .computeIfAbsent(buildingInfo, key -> new ArrayList<>())
                    .add(room);
        }

        return new PaginatedSearchResult(groupedResults, pageIndex, totalPages, totalResults, pageSize);
    }

    /**
     * Retrieves a set of all available locations from the buildings.
     *
     * @return A set of city names.
     */
    public Set<String> getLocations() {
        Set<String> locations = new TreeSet<>();
        for (Building building : buildings) {
            locations.add(building.buildingInfo().city());
        }
        return locations;
    }

    /**
     * Retrieves a set of all building names.
     *
     * @return A set of building names.
     */
    public Set<String> getBuildingNames() {
        Set<String> names = new TreeSet<>();
        for (Building building : buildings) {
            names.add(building.buildingInfo().fullName());
        }
        return names;
    }

    /**
     * Retrieves a building by its name.
     *
     * @param name the name of the building to search for
     * @return Optional containing the building if found, or empty Optional if not found
     * @throws NullPointerException if name is null
     */
    public Optional<Building> getBuildingByName(String name) {
        Objects.requireNonNull(name);
        Optional<Building> requiredBuilding = Optional.empty();

        for (Building building : buildings) {
            BuildingInfo buildingInfo = building.buildingInfo();
            if (buildingInfo.fullName().equals(name)) {
                requiredBuilding = Optional.of(building);
                break;
            }
        }
        return requiredBuilding;
    }

    /**
     * Adds an observer to the building controller.
     *
     * @param observer the observer to add
     * @throws NullPointerException if observer is null
     */
    public void addObserver(BuildingObserver observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        observers.add(observer);
    }

    /**
     * Shutdown the controller and its background executor. Call this when the
     * controller is no longer needed (e.g., application shutdown) to free threads.
     */
    public void shutdown() {
        executorService.shutdownNow();
    }

    /**
     * Performs an update of room occupancies and states.
     * This method is called periodically by the scheduled executor.
     */
    public void performUpdate() {
        // Recompute reservations each run so the job uses fresh data
        List<String> reservedRooms = toRoomName(reservationAPI.getReservations());

        for (Building building : buildings) {
            for (Room room : building.rooms()) {
                updateRoomOccupancy(room);
                deletePastEvents(room);
                updateRoomState(room, reservedRooms);
            }
        }

        try {
            buildingStorageAPI.saveBuildings(buildings);
        } catch (Exception e) {
            System.err.println("Failed to save buildings: " + e.getMessage());
        }

        observers.forEach(BuildingObserver::notifyBuildingUpdate);
    }


    /**
     * Gets the number of people in a room from the sensors and updates the value and {@link RoomState}
     *
     * @param room room to update
     */
    private void updateRoomOccupancy(Room room) {
        Optional<RoomOccupancy> occupancy = sensorAPI.getOccupancy(room.getName());

        synchronized (this) {
            occupancy.ifPresent(o -> room.setOccupancy(o.peopleInRoom()));
        }
    }

    /**
     * Removes events from a room that have finished
     *
     * @param room room to delete events from
     */
    private void deletePastEvents(Room room) {
        synchronized (this) {
            List<Event> eventsInRoom = room.getCurrentEvents();

            // Get all events that ended before the current date
            List<Event> oldEvents = eventsInRoom.stream()
                    .filter(e -> e.endDate().isBefore(LocalDateTime.now()))
                    .toList();
            oldEvents.forEach(room::removeEvent);
        }
    }

    /**
     * Updates the state of a room based on its occupancy and reservations
     *
     * @param room room to update
     */
    private void updateRoomState(Room room, List<String> reservedRooms) {
        synchronized (this) {
            if (reservedRooms.contains(room.getName())) {
                room.setStatus(RoomState.RESERVED);
            } else if (!room.getCurrentEvents().isEmpty()) {
                room.setStatus(RoomState.EVENT);
            } else if (room.getOccupancy() >= room.getSeats()) {
                room.setStatus(RoomState.FULL);
            } else if (room.getOccupancy() > 0) {
                room.setStatus(RoomState.OCCUPIED);
            } else {
                room.setStatus(RoomState.FREE);
            }
        }
    }

    /**
     * Utility method to extract the room names from RoomReservations
     *
     * @param reservations list of reservations
     * @return list of room names
     */
    private List<String> toRoomName(List<RoomReservation> reservations) {
        return reservations.stream().map(RoomReservation::roomID).toList();
    }

    /**
     * Inner class responsible for updating the room statuses and occupancy.
     * Removes old events and saves buildings after job completes.
     */
    private class UpdateJob implements Runnable {
        @Override
        public void run() {
            performUpdate();
        }
    }
}
