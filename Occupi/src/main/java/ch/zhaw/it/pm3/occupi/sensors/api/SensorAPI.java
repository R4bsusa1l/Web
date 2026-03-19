package ch.zhaw.it.pm3.occupi.sensors.api;

import java.util.Optional;

/**
 * API abstraction for accessing sensor data for the occupancy of rooms.
 * <p>
 * Implementations may provide real hardware adapters or simulators.
 */
@SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
public interface SensorAPI {
    /**
     * Returns occupancy details for the given room.
     *
     * @param roomID the identifier of the room to query; has to be non-null, non-blank. If invalid, an empty Optional is returned.
     * @return an unmodifiable entry of occupancy entries; empty if the room is unknown or the identifier is invalid
     */
    Optional<RoomOccupancy> getOccupancy(String roomID);
}
