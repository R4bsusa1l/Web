package ch.zhaw.it.pm3.occupi.sensors.api;

/**
 * Immutable value object carrying occupancy information for a room.
 * <p>
 * This DTO intentionally does not derive or recalculate values; producers are responsible for providing consistent component values.
 * <p>
 * Contracts:
 * @param roomID the room identifier (non-null, non-blank)
 * @param peopleInRoom number of people currently in the room (>= 0)
 */
public record RoomOccupancy(
        String roomID,
        int peopleInRoom
) {
    /**
     * Creates a new RoomOccupancy record and validates its components.
     *
     * @param roomID the room identifier (non-null, non-blank)
     * @param peopleInRoom number of people currently in the room (>= 0)
     * @throws IllegalArgumentException if any argument violates its contract
     */
    public RoomOccupancy {
        if (roomID == null || roomID.isBlank()) {
            throw new IllegalArgumentException("RoomOccupancy (SensorAPI): roomID must not be null or blank");
        }
        if (peopleInRoom < 0) {
            throw new IllegalArgumentException("RoomOccupancy (SensorAPI): people in room must be >= 0");
        }
    }
}
