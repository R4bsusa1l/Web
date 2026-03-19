// java
package ch.zhaw.it.pm3.occupi.reservation.api;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an immutable time window with an inclusive start time and an exclusive end time.
 * The exclusive end prevents overlapping when chaining consecutive time windows.
 * <p>
 * <b>Invariants:</b>
 * <ul>
 *   <li>Both start and end must be non-null</li>
 *   <li>Start must be strictly before end (start &lt; end)</li>
 *   <li>A time window is active when: start &lt;= current time &lt; end</li>
 * </ul>
 *
 * @param start the inclusive start date-time of the time window
 * @param end   the exclusive end date-time of the time window
 */
public record TimeWindow(
        LocalDateTime start,
        LocalDateTime end
) {
    private static final String PREFIX = "TimeWindow (reservation.api): ";

    /**
     * Compact constructor that validates the time window parameters and enforces class invariants.
     * <p>
     * Ensures that both start and end are non-null and that start occurs strictly before end.
     *
     * @param start the inclusive start date-time of the time window
     * @param end   the exclusive end date-time of the time window
     * @throws IllegalArgumentException if start is not strictly before end
     * @throws NullPointerException     if start or end is null
     */
    public TimeWindow {
        Objects.requireNonNull(start, PREFIX + "start must not be null");
        Objects.requireNonNull(end, PREFIX + "end must not be null");
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException(PREFIX + "start must be before end");
        }
    }
}
