package ch.zhaw.it.pm3.occupi.model;

import ch.zhaw.it.pm3.occupi.search.FilterCriteriaDTO;
import ch.zhaw.it.pm3.occupi.search.Search;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a physical room in a building with metadata and current events.
 * <p>
 * A Room has identifying information (name, floor), capacity information
 * (occupancy, seats), accessibility, current status, equipment, a list of
 * scheduled/current events. Instances are mutable via
 * setters for most fields and maintain their own list of events.
 */
public class Room {
    private String name;
    private String floor;
    private final RoomType roomType;
    private int occupancy;
    private int seats;
    private boolean accessible;
    private RoomState status;
    private RoomEquipment equipment;
    private final List<Event> currentEvents;
    private String description;

    /**
     * Constructs a new Room instance.
     *
     * @param name          the room name (must not be null)
     * @param floor         the floor identifier (must not be null)
     * @param occupancy     the maximum occupancy (non-negative expected)
     * @param seats         the number of seats (non-negative expected)
     * @param accessible    whether the room is accessible for people with reduced mobility
     * @param status        the current {@link RoomState} of the room (must not be null)
     * @param equipment     the {@link RoomEquipment} available in the room (must not be null)
     * @param currentEvents a list with the events currently associated to the room (must not be null)
     * @param description   description about the room (must not be null)
     * @param roomType      the {@link RoomType} classification of the room (must not be null)
     * @throws NullPointerException if any required parameter is null (name, floor, status, equipment, currentEvents, roomType)
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Room(@JsonProperty("name") String name,
                @JsonProperty("floor") String floor,
                @JsonProperty("occupancy") int occupancy,
                @JsonProperty("seats") int seats,
                @JsonProperty("accessible") boolean accessible,
                @JsonProperty("status") RoomState status,
                @JsonProperty("equipment") RoomEquipment equipment,
                @JsonProperty("currentEvents") List<Event> currentEvents,
                @JsonProperty("description") String description,
                @JsonProperty("roomType") RoomType roomType) {
        Objects.requireNonNull(name, "Name must not be null");
        Objects.requireNonNull(floor, "Floor must not be null");
        Objects.requireNonNull(status, "Status must not be null");
        Objects.requireNonNull(equipment, "Equipment must not be null");
        Objects.requireNonNull(currentEvents, "Current events must not be null");
        Objects.requireNonNull(roomType, "Room type must not be null");
        Objects.requireNonNull(description, "Description must not be null");

        if (occupancy < 0) {
            throw new IllegalArgumentException("Occupancy cannot be negative");
        }
        if (seats < 0) {
            throw new IllegalArgumentException("Seats cannot be negative");
        }
        if (seats > 5000) {
            throw new IllegalArgumentException("Seats cannot exceed 5000");
        }

        if (name.length() > 50) {
            throw new IllegalArgumentException("Name cannot exceed 50 characters");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }


        this.roomType = roomType;
        this.name = name;
        this.floor = floor;
        this.occupancy = occupancy;
        this.seats = seats;
        this.accessible = accessible;
        this.status = status;
        this.equipment = equipment;
        this.currentEvents = new ArrayList<>(currentEvents);
        this.description = description;
    }

    /**
     * Returns the room name.
     *
     * @return the name of the room
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the room name. The given name must not be null.
     *
     * @param name the new name for the room (must not be null) and not empty
     * @throws NullPointerException     if name is null
     * @throws IllegalArgumentException if name is empty
     */
    public void setName(String name) {
        Objects.requireNonNull(name, "Name must not be null");

        if (name.length() > 50) {
            throw new IllegalArgumentException("Name cannot exceed 50 characters");
        }

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }


        this.name = name;
    }

    /**
     * Returns the floor identifier for this room.
     *
     * @return the floor of the room
     */
    public String getFloor() {
        return floor;
    }

    /**
     * Sets the floor identifier for this room.
     *
     * @param floor the floor to set (must not be null) and not empty
     * @throws NullPointerException     if floor is null
     * @throws IllegalArgumentException if floor is empty
     */
    public void setFloor(String floor) {
        Objects.requireNonNull(floor, "Floor must not be null");
        if (floor.trim().isEmpty()) {
            throw new IllegalArgumentException("Floor cannot be empty");
        }
        this.floor = floor;
    }

    /**
     * Returns the maximum occupancy for this room.
     *
     * @return the occupancy limit
     */
    public int getOccupancy() {
        return occupancy;
    }

    /**
     * Sets the occupancy limit for this room.
     *
     * @param occupancy the occupancy to set
     */
    public void setOccupancy(int occupancy) {
        if (occupancy < 0) {
            throw new IllegalArgumentException("Occupancy cannot be negative");
        }
        this.occupancy = occupancy;
    }

    /**
     * Returns the number of seats in this room.
     *
     * @return seat count
     */
    public int getSeats() {
        return seats;
    }

    /**
     * Sets the number of seats in this room.
     *
     * @param seats number of seats to set
     */
    public void setSeats(int seats) {
        if (seats < 0) {
            throw new IllegalArgumentException("Seats cannot be negative");
        }
        if (seats > 5000) {
            throw new IllegalArgumentException("Seats cannot exceed 5000");
        }
        this.seats = seats;
    }

    /**
     * Returns whether the room is accessible for people with reduced mobility.
     *
     * @return true if accessible, false otherwise
     */
    public boolean isAccessible() {
        return accessible;
    }

    /**
     * Sets whether the room is accessible for people with reduced mobility.
     *
     * @param accessible true if accessible, false otherwise
     */
    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    /**
     * Returns the current status of the room.
     *
     * @return the room {@link RoomState}
     */
    public RoomState getStatus() {
        return status;
    }

    /**
     * Sets the status of the room.
     *
     * @param status the new {@link RoomState} (must not be null)
     * @throws NullPointerException if status is null
     */
    public void setStatus(RoomState status) {
        Objects.requireNonNull(status, "Status must not be null");
        this.status = status;
    }

    /**
     * Returns the equipment available in this room.
     *
     * @return the {@link RoomEquipment} of the room
     */
    public RoomEquipment getEquipment() {
        return equipment;
    }

    /**
     * Sets the equipment available in this room.
     *
     * @param equipment the {@link RoomEquipment} to set (must not be null)
     * @throws NullPointerException if equipment is null
     */
    public void setEquipment(RoomEquipment equipment) {
        Objects.requireNonNull(equipment, "Equipment must not be null");
        this.equipment = equipment;
    }

    /**
     * Returns an unmodifiable view of the events currently associated with this room.
     * Modifications must be done via {@link #addEvent(Event)} and {@link #removeEvent(Event)}.
     *
     * @return an unmodifiable list of current events
     */
    public List<Event> getCurrentEvents() {
        return Collections.unmodifiableList(currentEvents);
    }

    /**
     * Adds an event to the room's current events. The event is only added if
     * it does not duplicate an existing event and does not overlap in time
     *
     * @param event the event to add (must not be null)
     * @throws NullPointerException if event is null
     */
    @SuppressWarnings("ChainedMethodCall")
    public void addEvent(Event event) {
        Objects.requireNonNull(event);

        boolean noDuplicate = !currentEvents.contains(event);
        boolean noOverlaps = currentEvents.stream().noneMatch(existing ->
                existing.startDate().isBefore(event.endDate()) &&
                        event.startDate().isBefore(existing.endDate())
        );

        if (noDuplicate && noOverlaps) {
            currentEvents.add(event);
        }
    }

    /**
     * Removes an event from the room's current events.
     * If the event is not present this method has no effect.
     *
     * @param event the event to remove (must not be null)
     * @throws NullPointerException if event is null
     */
    public void removeEvent(Event event) {
        Objects.requireNonNull(event, "Event must not be null");
        this.currentEvents.remove(event);
    }

    /**
     * Returns description of the room.
     *
     * @return the description of the room
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the room.
     *
     * @param description the description to set (must not be null)
     * @throws NullPointerException if description is null
     */
    public void setDescription(String description) {
        Objects.requireNonNull(description, "Description must not be null");
        this.description = description;
    }


    /**
     * Returns the room type/classification.
     *
     * @return the {@link RoomType}
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * Evaluates if this room matches the given filter criteria
     *
     * @param criteria filter criteria
     * @return true if room matches, false otherwise
     * @throws NullPointerException if criteria is null
     */
    public boolean matchesCriteria(FilterCriteriaDTO criteria) {
        Objects.requireNonNull(criteria, "Criteria must not be null");
        boolean result = false;
        String query = criteria.searchQuery();
        boolean matchesQuery = matchesQuery(query);
        boolean containsCriteria = Search.containsCriteria(criteria);

        if (containsCriteria && matchesQuery) {
            result = matchesRequirements(criteria);
        } else if (!containsCriteria && matchesQuery) {
            result = true;
        }
        return result;
    }

    /**
     * Checks if a room has all required infrastructure.
     *
     * @param required the set of required infrastructure
     * @return true if the room has all required infrastructure, false otherwise
     */
    private boolean roomHasInfrastructure(Set<RoomInfrastructure> required) {
        boolean result = false;
        if (required != null && !required.isEmpty()) {
            Set<RoomInfrastructure> have = equipment.roomInfrastructure();
            result = have.containsAll(required);
        }
        return result;
    }

    /**
     * Checks if the room matches the non-query requirements of the filter criteria.
     *
     * @param criteria the filter criteria
     * @return true if the room matches the requirements, false otherwise
     */
    @SuppressWarnings("OverlyComplexBooleanExpression")
    private boolean matchesRequirements(FilterCriteriaDTO criteria) {
        String wantedFloor = criteria.floor();
        RoomType wantedRoomType = criteria.roomType();
        int wantedCapacity = criteria.capacity();
        Set<RoomInfrastructure> wantedInfrastructure = criteria.infrastructure();

        return (wantedInfrastructure.isEmpty() || roomHasInfrastructure(wantedInfrastructure))
                && (wantedFloor == null || floor.equals(wantedFloor))
                && (wantedRoomType == null || roomType == wantedRoomType)
                && seats >= wantedCapacity;
    }

    /**
     * Checks if the room matches the search query. Blank queries match all rooms.
     * <p>
     * Made public so callers outside this class (for example {@link Building}) can
     * determine whether a room matches a freetext query without applying full criteria.
     *
     * @param query the search query
     * @return true if the room matches the query, false otherwise
     */
    private boolean matchesQuery(String query) {
        boolean result = true;

        if (Search.isNotBlank(query)) {
            String equipmentString = equipment.roomInfrastructure().toString();

            result = Search.containsIgnoreCase(name, query)
                    || Search.containsIgnoreCase(floor, query)
                    || Search.containsIgnoreCase(equipmentString, query)
                    || currentEvents.stream().anyMatch(e -> e.eventMatchesQuery(query));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Room{" +
                "name=" + name +
                ", floor=" + floor +
                ", occupancy=" + occupancy +
                ", seats=" + seats +
                ", accessible=" + accessible +
                ", status=" + status +
                ", equipment=" + equipment +
                ", currentEvents=" + currentEvents +
                ", description='" + description + "}";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (this == object) {
            result = true;
        } else if (object instanceof Room room) {
            result = sameAs(room);
        }
        return result;
    }

    /**
     * Compares this room with another room for equality.
     *
     * @param room the room to compare with
     * @return true if the rooms are equal, false otherwise
     */
    private boolean sameAs(Room room) {
        return Objects.equals(floor, room.floor) &&
                occupancy == room.occupancy &&
                seats == room.seats &&
                accessible == room.accessible &&
                Objects.equals(name, room.name) &&
                status == room.status &&
                Objects.equals(equipment, room.equipment) &&
                Objects.equals(currentEvents, room.currentEvents) &&
                Objects.equals(description, room.description) &&
                roomType == room.roomType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, floor, occupancy, seats, accessible, status, equipment, currentEvents, description, roomType);
    }
}
