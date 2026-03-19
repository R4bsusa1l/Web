package ch.zhaw.it.pm3.occupi.model;

import ch.zhaw.it.pm3.occupi.search.FilterCriteriaDTO;
import ch.zhaw.it.pm3.occupi.search.Search;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a building containing rooms and a set of floors present in the building.
 * <p>
 * The class keeps an internal list of {@link Room} objects and a set of floor identifiers
 * (typically strings).
 * </p>
 *
 * @param buildingInfo building information
 * @param floors       set of floors in the building
 * @param rooms        list of rooms in the building
 *
 */
public record Building(BuildingInfo buildingInfo, Set<String> floors, List<Room> rooms) {

    /**
     * Constructor for Building
     *
     * @param buildingInfo building information
     * @param floors       set of floors
     * @param rooms        list of rooms
     * @throws NullPointerException if any argument is null
     */
    public Building(BuildingInfo buildingInfo, Set<String> floors, List<Room> rooms) {
        this.buildingInfo = Objects.requireNonNull(buildingInfo, "BuildingInfo must not be null");
        Objects.requireNonNull(floors, "Floors set must not be null");
        this.floors = Collections.unmodifiableSet(floors);
        Objects.requireNonNull(rooms, "Rooms list must not be null");
        this.rooms = Collections.unmodifiableList(rooms);
    }

    /**
     * Evaluates if this building matches the given filter criteria
     * <p>
     * This ensures that searching for a term that only appears in a room (for example "lab") will still
     * return the building that contains such a room even when the building's name does not contain the term.
     *
     * @param criteria filter criteria (must not be null)
     * @return true if building matches, false otherwise
     */
    public boolean matchesCriteria(FilterCriteriaDTO criteria) {
        Objects.requireNonNull(criteria, "FilterCriteria must not be null");

        boolean result = false;
        String query = criteria.searchQuery();
        boolean matchesQuery = matchesQuery(query);

        if (Search.containsCriteria(criteria)) {
            result = matchesRequirements(criteria);
        } else if (matchesQuery) {
            result = true;
        }

        return result;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private boolean matchesRequirements(FilterCriteriaDTO criteria) {
        String wantedFloor = criteria.floor();
        String wantedCity = criteria.city();
        String wantedBuilding = criteria.building();

        String fullName = buildingInfo.fullName();
        String city = buildingInfo.city();

        return (wantedFloor == null  || floors.contains(wantedFloor))
                && (wantedCity == null  || city.equals(wantedCity))
                && (wantedBuilding == null  || fullName.equals(wantedBuilding));
    }

    /**
     * Checks if any attribute of the building matches the user query.
     * If no query is provided, the building is considered as a match.
     *
     * @param query string to search for
     * @return true if query is empty or matches, false otherwise
     */
    public boolean matchesQuery(String query) {
        boolean result = true;

        if (Search.isNotBlank(query)) {
            String city = buildingInfo.city();
            String fullName = buildingInfo.fullName();
            String shortName = buildingInfo.shortName();

            result = Search.containsIgnoreCase(city, query)
                    || Search.containsIgnoreCase(fullName, query)
                    || Search.containsIgnoreCase(shortName, query);
        }

        return result;
    }

    /**
     * Returns all rooms in this building that match the given filter criteria.
     *
     * @param criteria filter criteria (must not be null)
     * @return list of matching rooms
     * @throws NullPointerException if criteria is null
     */
    public List<Room> getMatchingRooms(FilterCriteriaDTO criteria) {
        Objects.requireNonNull(criteria, "FilterCriteria must not be null");
        return rooms.stream().filter(room -> room.matchesCriteria(criteria)).toList();
    }
}
