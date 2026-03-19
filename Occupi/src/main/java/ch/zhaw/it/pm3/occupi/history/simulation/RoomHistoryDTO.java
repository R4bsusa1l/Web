package ch.zhaw.it.pm3.occupi.history.simulation;

import java.util.List;
/**
 * Represents historical usage data for a specific room.
 * Contains a list of usage entries at different timestamps.
 * <p>
 * Example JSON structure:
 * <pre>{@code
 * {
 *   "roomId": "Room A",
 *   "historyUsage": [
 *     {
 *       "timestamp": "2024-01-15T10:00:00",
 *       "occupiedSeats": 15
 *     },
 *     {
 *       "timestamp": "2024-01-15T14:00:00",
 *       "occupiedSeats": 20
 *     }
 *   ]
 * }
 * }</pre>
 *
 * @param roomId       the unique identifier (name) of the room
 * @param historyUsage the list of usage entries for the room at different timestamps
 */
public record RoomHistoryDTO(String roomId, List<UsageEntryDTO> historyUsage) {}