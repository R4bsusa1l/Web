package ch.zhaw.it.pm3.occupi.history.simulation;

import ch.zhaw.it.pm3.occupi.history.api.HistoryAPI;
import ch.zhaw.it.pm3.occupi.util.FileValidator;
import ch.zhaw.it.pm3.occupi.util.JsonReader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * File-based implementation of {@link HistoryAPI} for retrieving room usage history.
 *
 * <p>This implementation reads historical occupancy data from a persistent data file,
 * providing access to timestamped room usage information. The data is read-only;
 * this class does not modify or persist data back to the source.</p>
 *
 * <p><strong>Thread-safety:</strong> This implementation is stateless after construction
 * and therefore thread-safe for read operations.</p>
 *
 * <p><strong>Error handling:</strong> Returns empty collections when data is unavailable
 * or cannot be read, logging errors to {@code System.err}.</p>
 */
public class FileHistoryAPI implements HistoryAPI {
    private final JsonReader<RoomHistoryDTO> jsonReader;
    private final Path filePath;

    /**
     * Constructs a new history database instance using the specified file path.
     *
     * @param path                      the path to the data file containing room usage history
     * @throws IllegalArgumentException if the file path does not exist or is not a regular file
     */
    public FileHistoryAPI(Path path) {
        FileValidator.requireAccessibleFile(path);
        this.jsonReader = new JsonReader<>(RoomHistoryDTO.class);
        this.filePath = path;
    }

    /**
     * Retrieves the usage history for a specific room as a time-series mapping.
     *
     * <p>The returned map contains timestamped occupancy data, where each entry
     * represents the number of occupied seats at a specific point in time.</p>
     *
     * <p><strong>Behavior:</strong></p>
     * <ul>
     *   <li>Returns a map of timestamps to occupancy counts for the specified room</li>
     *   <li>Returns an empty map if the room ID is not found</li>
     *   <li>Returns an empty map if the data source cannot be read</li>
     *   <li>Logs errors to {@code System.err} when data access fails</li>
     * </ul>
     *
     * @param roomId the unique identifier of the room
     * @return an immutable map of {@link LocalDateTime} to occupancy count;
     *         empty if room not found or error occurs
     * @throws IllegalStateException if the data file no longer exists or is inaccessible
     */
    @Override
    public Map<LocalDateTime, Integer> getRoomUsageHistory(String roomId) {
        Map<LocalDateTime, Integer> result;
        try {
            List<RoomHistoryDTO> histories = jsonReader.readValues(filePath);
            result = findRoomHistoryAndBuildMap(histories, roomId);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read room usage history from file: " + filePath, e);
        }

        return result;
    }

    /**
     * Finds the room history for the specified room ID and converts it to a time-series map.
     *
     * @param histories the list of all room histories
     * @param roomId    the room identifier to search for
     * @return a map of timestamps to occupancy counts, or an empty map if not found
     */
    private Map<LocalDateTime, Integer> findRoomHistoryAndBuildMap(List<RoomHistoryDTO> histories, String roomId) {
        Objects.requireNonNull(histories, "Room histories list must not be null");
        Objects.requireNonNull(roomId, "Room ID must not be null");
        return histories.stream()
                .filter(history -> roomId.equals(history.roomId()))
                .findFirst()
                .map(this::buildUsageMap)
                .orElseGet(Map::of);
    }

    /**
     * Converts a room history's usage entries into a map of timestamps to occupancy counts.
     *
     * @param roomHistory the room history containing usage entries
     * @return a map of timestamps to occupancy counts
     */
    @SuppressWarnings("ChainedMethodCall")
    private Map<LocalDateTime, Integer> buildUsageMap(RoomHistoryDTO roomHistory) {
        return roomHistory.historyUsage().stream()
                .collect(Collectors.toMap(
                        UsageEntryDTO::date,
                        UsageEntryDTO::occupancy
                ));
    }
}
