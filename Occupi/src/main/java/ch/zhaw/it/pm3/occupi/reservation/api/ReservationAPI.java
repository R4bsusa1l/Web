package ch.zhaw.it.pm3.occupi.reservation.api;

import java.util.List;

/**
 * Public API to query reservations for rooms.
 * <p>
 * Contract:
 * - Idempotent, read-only lookup methods.
 * - Non-null inputs: parameters must not be null; otherwise a {@link NullPointerException} may be thrown.
 * - Time window semantics: a reservation is considered active when {@code start <= at < end}.
 */
@SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
public interface ReservationAPI {

    /**
     * Returns all reservations for all rooms in the system.
     * <p>
     * Each element in the returned list represents the reservation data for a single room
     * that has at least one reservation. Rooms without reservations are not included in the list.
     *
     * @return a list of {@link RoomReservation} objects, one for each room with reservations;
     * never null, but may be empty if no rooms have reservations
     */
    List<RoomReservation> getReservations();
}
