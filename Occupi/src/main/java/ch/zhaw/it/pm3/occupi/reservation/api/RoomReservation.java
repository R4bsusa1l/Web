package ch.zhaw.it.pm3.occupi.reservation.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Immutable room reservation consisting of a room identifier and a list of time windows during which the room is reserved.
 * <p>
 * Contract:
 * <p>
 * - Non-null: roomID, timeWindows <p>
 * - timeWindows elements are non-null and must not overlap
 *
 * @param roomID the identifier of the room (non-null)
 * @param timeWindows the list of reservation windows for the room (non-null, no overlaps)
 */
public record RoomReservation(
        String roomID,
        List<TimeWindow> timeWindows
) {
    private static final String PREFIX = "RoomReservation (reservation.api): ";

    /**
     * Canonical constructor with validation and defensive copying.
     *
     * @param roomID the identifier of the room (non-null)
     * @param timeWindows the list of reservation windows for the room (non-null, no overlaps)
     * @throws NullPointerException if any parameter or list element is null
     * @throws IllegalArgumentException if the time windows overlap
     */
    public RoomReservation {
        Objects.requireNonNull(roomID, PREFIX + "roomID must not be null");
        Objects.requireNonNull(timeWindows, PREFIX + "timeWindows must not be null");

        // copy and sort by start time for deterministic order
        List<TimeWindow> copy = new ArrayList<>(timeWindows);
        for (TimeWindow timeWindow : copy) {
            if (timeWindow == null) {
                throw new NullPointerException(PREFIX + "timeWindows must not contain null elements");
            }
        }
        copy.sort(Comparator.comparing(TimeWindow::start));

        // validate non-overlap: a.end <= b.start is allowed, a.end > b.start is overlap
        for (int i = 0; i < copy.size() - 1; i++) {
            TimeWindow a = copy.get(i);
            TimeWindow b = copy.get(i + 1);
            LocalDateTime aEnd = a.end();
            LocalDateTime bStart = b.start();
            if (aEnd.isAfter(bStart)) {
                throw new IllegalArgumentException(PREFIX + "time windows must not overlap");
            }
        }
        timeWindows = Collections.unmodifiableList(copy);
    }
}
