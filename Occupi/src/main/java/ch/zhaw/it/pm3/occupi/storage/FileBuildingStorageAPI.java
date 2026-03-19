package ch.zhaw.it.pm3.occupi.storage;

import ch.zhaw.it.pm3.occupi.model.Building;
import ch.zhaw.it.pm3.occupi.util.FileValidator;
import ch.zhaw.it.pm3.occupi.util.JsonReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * File-based implementation of the {@link BuildingStorageAPI} interface.
 * <p>
 * This implementation uses a JSON file to store and retrieve building data.
 * It leverages the {@link JsonReader} utility for reading and writing JSON data.
 * <p>
 * Error handling: IO exceptions during reading return an empty list,
 * while exceptions during writing are propagated as runtime exceptions.
 * <p>
 * Thread-safety: This implementation is stateless (after construction) and therefore thread-safe.
 */
public class FileBuildingStorageAPI implements BuildingStorageAPI {
    private final JsonReader<Building> jsonReader;
    private final Path filePath;

    /**
     * Constructs a {@code FileBuildingStorageApi} with the specified file path.
     *
     * @param filePath the path to the JSON file for storing building data
     * @throws NullPointerException     if {@code filePath} is null
     * @throws IllegalArgumentException if {@code filePath} does not exist or is a directory
     */
    public FileBuildingStorageAPI(Path filePath) {
        FileValidator.requireAccessibleFile(filePath);
        this.jsonReader = new JsonReader<>(Building.class);
        this.filePath = filePath;
    }

    /**
     * Retrieves a list of all buildings from the JSON file.
     *
     * @return List of Building objects, or an empty list if an error occurs
     */
    @Override
    public List<Building> getBuildings() {
        List<Building> buildings;

        try {
            buildings = jsonReader.readValues(filePath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            buildings = Collections.emptyList();
        }

        return buildings;
    }

    /**
     * Saves a list of buildings to the JSON file.
     *
     * @param buildings List of Building objects to be saved
     * @throws UncheckedIOException if an I/O error occurs during saving
     */
    @Override
    public void saveBuildings(List<Building> buildings) {
        Objects.requireNonNull(buildings, "buildings list must not be null");
        try {
            jsonReader.writeValues(filePath, buildings);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save buildings to file: " + filePath, e);
        }
    }
}
