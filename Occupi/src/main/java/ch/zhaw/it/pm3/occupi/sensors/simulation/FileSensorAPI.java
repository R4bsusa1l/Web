package ch.zhaw.it.pm3.occupi.sensors.simulation;

import ch.zhaw.it.pm3.occupi.sensors.api.RoomOccupancy;
import ch.zhaw.it.pm3.occupi.sensors.api.SensorAPI;
import ch.zhaw.it.pm3.occupi.util.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * File-backed {@link SensorAPI} implementation.
 * <p>
 * This simulator reads occupancy snapshots from a given file.
 * It tries to load the resource from the classpath first
 * and falls back to the project path when running from source.
 * <p>
 * Each object is mapped to a {@code SensorEntry} and the first matching {@code roomID}
 * is converted into a {@link RoomOccupancy} value.
 * <p>
 * Error handling: IO and mapping errors are propagated as {@link java.io.IOException} to the caller,
 * allowing them to handle errors appropriately.
 */
public class FileSensorAPI implements SensorAPI {
    private final JsonReader<SensorEntry> jsonReader;
    private final List<SensorEntry> cachedEntries;
    private Path filePath;

    /**
     * Constructs an {@code FileSensorAPI} instance using a user-specified file path.
     * <p>
     * The constructor validates the provided path and attempts to locate the sensor data file.
     * If the specified path does not exist, it falls back to the default path
     * ({@code src/main/resources/jsonData/SensorData.json}).
     * <p>
     * A warning is logged to {@code System.err} if the fallback occurs.
     *
     * @param userFilePath              the path to the sensor data file (must not be null)
     * @throws FileNotFoundException    if neither the provided path nor the default path exists
     */
    public FileSensorAPI(Path userFilePath) throws IOException {
        this.jsonReader = new JsonReader<>(SensorEntry.class);
        setValidFilePath(userFilePath);
        this.cachedEntries = loadEntries();
    }

    /**
     * Retrieves the occupancy data for a specific room from the cached sensor data.
     * <p>
     * This method searches through the cached sensor entries to find the first entry
     * matching the given room ID. If found, the entry is converted to a {@link RoomOccupancy} object.
     *
     * @param roomID                    the unique identifier of the room (must not be null or blank)
     * @return an {@link Optional}      containing the {@link RoomOccupancy} if found, or empty if not found
     * @throws IllegalArgumentException if the roomID is null or blank
     */
    @Override
    public Optional<RoomOccupancy> getOccupancy(String roomID) {
        Optional<RoomOccupancy> result;
        Objects.requireNonNull(roomID, "FileSensorAPI (sensors.api): roomID must not be null");

        // Check if roomID is valid
        if (roomID.isBlank()) {
            throw new IllegalArgumentException("FileSensorAPI (sensors.api): roomID must not be blank");

        } else {
            // Search first matching entry for roomID
            Optional<SensorEntry> matchingEntry = cachedEntries.stream().filter(entry -> roomID.equals(entry.roomID)).findFirst();

            // Create RoomOccupancy from SensorEntry
            result = matchingEntry.map(this::toOccupancy);

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
        // if user file has been found successfully
        if (Files.isRegularFile(path)) {
            this.filePath = path;

        } else {
            throw new FileNotFoundException("FileSensorAPI (sensors.api): sensor data file nor default file not found: " + "\nuser path -> " + path);
        }
    }

    /**
     * Loads and parses sensor entries from the file.
     * <p>
     * This method reads the entire file and deserializes it into a list of {@link SensorEntry} objects.
     * It is called once during construction to cache the data for faster subsequent access.
     *
     * @return a list of {@link SensorEntry}    objects parsed from the given file
     * @throws IOException                      if the file cannot be read or the file format is invalid
     */
    private List<SensorEntry> loadEntries() throws IOException {
        return jsonReader.readValues(filePath);
    }

    /**
     * Converts a {@link SensorEntry} to a {@link RoomOccupancy} object.
     * <p>
     * This method maps the internal file representation to the domain model used by the sensor API.
     *
     * @param entry                     the sensor entry to convert (must not be null)
     * @return a {@link RoomOccupancy}  object containing the room ID and number of people
     */
    private RoomOccupancy toOccupancy(SensorEntry entry) {
        return new RoomOccupancy(entry.roomID, entry.peopleInRoom);
    }

    /**
     * Internal record representing a sensor data entry as read from the JSON file.
     * <p>
     * This record is used for deserialization and is then converted to a {@link RoomOccupancy}.
     *
     * @param roomID        the unique identifier of the room
     * @param peopleInRoom  the number of people currently in the room
     */
    private record SensorEntry(
            String roomID,
            int peopleInRoom
    ) {
    }
}
