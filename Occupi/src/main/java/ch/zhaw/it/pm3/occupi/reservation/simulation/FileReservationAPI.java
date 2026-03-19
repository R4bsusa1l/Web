package ch.zhaw.it.pm3.occupi.reservation.simulation;

import ch.zhaw.it.pm3.occupi.reservation.api.ReservationAPI;
import ch.zhaw.it.pm3.occupi.reservation.api.RoomReservation;
import ch.zhaw.it.pm3.occupi.reservation.api.TimeWindow;
import ch.zhaw.it.pm3.occupi.util.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of the ReservationAPI interface.
 * <p>
 * This implementation reads reservation data from a given file and caches it in memory.
 * The data file should contain an array of reservation entries, each with a room ID
 * and a list of time windows during which the room is reserved.
 * <p>
 * The class supports both a custom file path and a default path
 * (src/main/resources/jsonData/ReservationData.json).
 */
public class FileReservationAPI implements ReservationAPI {

    private final JsonReader<ReservationEntry> jsonReader;
    private final List<ReservationEntry> cachedEntries;
    private Path filePath;

    /**
     * Constructs a FileReservationAPI with a custom JSON file path.
     * <p>
     * If the provided file path is invalid or the file does not exist,
     * the implementation will fall back to the default JSON file path.
     *
     * @param userFilePath the path to the JSON file containing reservation data
     * @throws IOException if neither the provided file nor the default file can be read
     */
    public FileReservationAPI(Path userFilePath) throws IOException {
        this.jsonReader = new JsonReader<>(ReservationEntry.class);
        setValidFilePath(userFilePath);
        this.cachedEntries = loadEntries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RoomReservation> getReservations() {
        List<RoomReservation> result = new ArrayList<>();

        // Convert each cached entry to a RoomReservation
        for (ReservationEntry entry : cachedEntries) {
            result.add(toReservation(entry));
        }

        return result;
    }

    /**
     * Sets the file path to a valid file.
     * <p>
     * If the provided path does not point to a valid file,
     * a FileNotFoundException is thrown.
     *
     * @param path the path to validate
     * @throws FileNotFoundException if the file does not exist or is not a regular file
     */
    private void setValidFilePath(Path path) throws FileNotFoundException {
        // if user json file has been found successfully
        if (Files.isRegularFile(path)) {
            this.filePath = path;

        } else {
            throw new FileNotFoundException("sensor data file nor default file not found: " + "\nuser path -> " + path);
        }
    }

    /**
     * Loads reservation entries from the given file.
     *
     * @return a list of reservation entries read from the given file
     * @throws IOException if the file cannot be read or parsed
     */
    private List<ReservationEntry> loadEntries() throws IOException {
        return jsonReader.readValues(filePath);
    }

    /**
     * Converts a ReservationEntry (from File) to a RoomReservation (API object).
     *
     * @param entry the reservation entry to convert
     * @return a RoomReservation with converted time windows
     */
    private RoomReservation toReservation(ReservationEntry entry) {
        List<TimeWindow> timeWindows = new ArrayList<>();
        for (TimeWindowEntry timeWindowEntry : entry.timeWindows) {
            timeWindows.add(new TimeWindow(timeWindowEntry.start, timeWindowEntry.end));
        }
        return new RoomReservation(entry.roomID, timeWindows);
    }

    /**
     * Internal record representing a reservation entry as read from the given file.
     * <p>
     * This record is used for deserialization and is then converted to a RoomReservation.
     *
     * @param roomID      the identifier of the room
     * @param timeWindows the list of time windows during which the room is reserved
     */
    private record ReservationEntry(
            String roomID,
            List<TimeWindowEntry> timeWindows
    ) {
    }

    /**
     * Internal record representing a time window as read from the given file.
     * <p>
     * This record is used for deserialization and is then converted to a TimeWindow.
     *
     * @param start the inclusive start date-time of the time window
     * @param end   the exclusive end date-time of the time window
     */
    private record TimeWindowEntry(
            LocalDateTime start,
            LocalDateTime end
    ) {
    }
}
