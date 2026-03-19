package ch.zhaw.it.pm3.occupi.history.simulation;

import java.time.LocalDateTime;

/**
 * Represents a single entry of room usage with date and occupancy.
 * @param date      the timestamp of the usage entry
 * @param occupancy the number of occupied seats at the given timestamp
 */
public record UsageEntryDTO(LocalDateTime date, int occupancy) {
}