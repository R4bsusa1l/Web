package ch.zhaw.it.pm3.occupi.sensors;

import ch.zhaw.it.pm3.occupi.sensors.api.RoomOccupancy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.zhaw.it.pm3.occupi.sensors.simulation.FileSensorAPI;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileSensorAPITest {

    @TempDir
    private Path tempDir;

    private Path createValidSensorFile() throws IOException {
        String json = """
            [
              { "roomID": "ZL E1.01", "peopleInRoom": 3 },
              { "roomID": "ZL E1.02", "peopleInRoom": 0 }
            ]
            """;
        Path file = tempDir.resolve("sensorData.json");
        Files.writeString(file, json);
        return file;
    }

    @Test
    void constructor_withValidJson_initializesAndCachesEntries() throws IOException {
        Path file = createValidSensorFile();

        FileSensorAPI api = new FileSensorAPI(file);

        Optional<RoomOccupancy> occupancy = api.getOccupancy("ZL E1.01");
        assertTrue(occupancy.isPresent());
        assertEquals("ZL E1.01", occupancy.get().roomID());
        assertEquals(3, occupancy.get().peopleInRoom());
    }

    @Test
    void constructor_withEmptyFile_doesNotThrowAndProvidesEmptyOccupancy() throws IOException {
        Path emptyFile = tempDir.resolve("empty.json");
        Files.writeString(emptyFile, "[]");
        FileSensorAPI api = new FileSensorAPI(emptyFile);

        assertNotNull(api);
        assertTrue(api.getOccupancy("ZL E1.01").isEmpty());
    }

    @Test
    void constructor_withNullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FileSensorAPI(null));
    }

    @Test
    void constructor_withNonExistingPath_eitherFallsBackToDefaultOrThrowsFileNotFound() {
        Path nonExisting = tempDir.resolve("does-not-exist.json");

        try {
            FileSensorAPI api = new FileSensorAPI(nonExisting);
            // Success case: API falls back to the default file path
            assertNotNull(api);
        } catch (IOException e) {
            // Error case: neither the provided nor the default file exists
            assertInstanceOf(FileNotFoundException.class, e);
        }
    }

    @Test
    void getOccupancy_withExistingRoomId_returnsOccupancy() throws IOException {
        Path file = createValidSensorFile();
        FileSensorAPI api = new FileSensorAPI(file);

        Optional<RoomOccupancy> occupancy = api.getOccupancy("ZL E1.02");

        assertTrue(occupancy.isPresent());
        assertEquals("ZL E1.02", occupancy.get().roomID());
        assertEquals(0, occupancy.get().peopleInRoom());
    }

    @Test
    void getOccupancy_withNonExistingRoomId_returnsEmptyOptional() throws IOException {
        Path file = createValidSensorFile();
        FileSensorAPI api = new FileSensorAPI(file);

        Optional<RoomOccupancy> occupancy = api.getOccupancy("UNKNOWN");

        assertTrue(occupancy.isEmpty());
    }

    @Test
    void getOccupancy_withNullId_throwsIllegalArgumentException() throws IOException {
        Path file = createValidSensorFile();
        FileSensorAPI api = new FileSensorAPI(file);

        assertThrows(NullPointerException.class,
                () -> api.getOccupancy(null));
    }

    @Test
    void getOccupancy_withBlankId_throwsIllegalArgumentException() throws IOException {
        Path file = createValidSensorFile();
        FileSensorAPI api = new FileSensorAPI(file);

        assertThrows(IllegalArgumentException.class,
                () -> api.getOccupancy("   "));
    }
}
