package ch.zhaw.it.pm3.occupi.storage;

import ch.zhaw.it.pm3.occupi.model.Building;
import ch.zhaw.it.pm3.occupi.model.BuildingInfo;
import ch.zhaw.it.pm3.occupi.model.Room;
import ch.zhaw.it.pm3.occupi.model.RoomEquipment;
import ch.zhaw.it.pm3.occupi.model.RoomInfrastructure;
import ch.zhaw.it.pm3.occupi.model.RoomState;
import ch.zhaw.it.pm3.occupi.model.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the BuildingStorageAPI implementation.
 */


class BuildingStorageTest {

    private static final Path mockPath = Path.of("src", "test", "resources", "jsonData", "buildingData", "TestBuildingData.json");


    // ---- Constructor Tests ----
    @Test
    @DisplayName("Test valid path in constructor")
    void testValidPathInConstructor() {
        assertDoesNotThrow(() -> new FileBuildingStorageAPI(mockPath));
    }

    @Test
    @DisplayName("Test invalid path in constructor")
    void testInvalidPathInConstructor() {
        Path filePath = Path.of("nonexistent-", "buildings.json");
        assertThrows(IllegalArgumentException.class, () -> new FileBuildingStorageAPI(filePath));
    }

    @Test
    @DisplayName("Test null path in constructor")
    void testNullPathInConstructor() {
        assertThrows(NullPointerException.class, () -> new FileBuildingStorageAPI(null));
    }

    @Test
    @DisplayName("Test directory in constructor")
    void testDirectoryInConstructor(@TempDir Path tempPath) {
        assertThrows(IllegalArgumentException.class, () -> new FileBuildingStorageAPI(tempPath));
    }


    // --- getBuildings() Tests ----
    @Test
    @DisplayName("Test getBuildings() with valid file")
    void testGetBuildingsWithValidFile() {
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(mockPath);
        List<Building> expectedBuildings = mockBuildings();

        List<Building> buildings = storageAPI.getBuildings();

        checkIfBuildingsAreCorrect(expectedBuildings, buildings);
    }

    @Test
    @DisplayName("Test getBuildings() with empty file")
    void testGetBuildingsWithEmptyFile() {
        Path mockEmptyPath = Path.of("src", "test", "resources", "jsonData", "buildingData", "EmptyBuildingData.json");
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(mockEmptyPath);

        List<Building> buildings = storageAPI.getBuildings();

        assertEquals(0, buildings.size());
    }

    @Test
    @DisplayName("Test getBuildings() with malformed file")
    void testGetBuildingsWithMalformedFile() {
        Path malformedPath = Path.of("src", "test", "resources", "jsonData", "buildingData", "MalformedBuildingData.json");
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(malformedPath);

        List<Building> buildings = storageAPI.getBuildings();

        assertEquals(0, buildings.size());
    }

    @Test
    @DisplayName("Test getBuildings() with deleted file")
    void testGetBuildingsWithDeletedFile(@TempDir Path tempPath) throws IOException {
        Path filePath = createFileFromPath(tempPath, "deletedBuildingData.json");
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(filePath);
        filePath.toFile().delete();

        List<Building> buildings = storageAPI.getBuildings();

        assertEquals(0, buildings.size());
    }

    // ---- saveBuildings() Tests ----
    @Test
    @DisplayName("Test saveBuildings() with valid data")
    void testSaveBuildingsWithValidData(@TempDir Path tempPath) throws IOException {
        Path filePath = createFileFromPath(tempPath, "building.json");
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(filePath);
        List<Building> buildingsToSave = mockBuildings();

        assertDoesNotThrow(() -> storageAPI.saveBuildings(buildingsToSave));

        List<Building> loadedBuildings = storageAPI.getBuildings();
        checkIfBuildingsAreCorrect(buildingsToSave, loadedBuildings);
    }

    @Test
    @DisplayName("Test saveBuildings() with empty list")
    void testSaveBuildingsWithEmptyList(@TempDir Path tempPath) throws IOException {
        Path filePath = createFileFromPath(tempPath, "emptyBuildings.json");
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(filePath);
        List<Building> emptyList = new ArrayList<>();

        assertDoesNotThrow(() -> storageAPI.saveBuildings(emptyList));

        List<Building> loadedBuildings = storageAPI.getBuildings();
        assertEquals(0, loadedBuildings.size());
    }

    @Test
    @DisplayName("Test saveBuildings() with null list")
    void testSaveBuildingsWithNullList(@TempDir Path tempPath) throws IOException {
        Path filePath = createFileFromPath(tempPath, "nullBuildings.json");
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(filePath);

        assertThrows(NullPointerException.class, () -> storageAPI.saveBuildings(null));
    }

    @Test
    @DisplayName("Test saveBuildings() to read-only file")
    void testSaveBuildingsToReadOnlyFile(@TempDir Path tempPath) throws IOException {
        Path filePath = createFileFromPath(tempPath, "readonlyBuildings.json");
        filePath.toFile().setReadOnly();
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(filePath);
        List<Building> buildingsToSave = mockBuildings();

        assertThrows(RuntimeException.class, () -> storageAPI.saveBuildings(buildingsToSave));
    }

    @Test
    @DisplayName("Test saveBuildings() deleted file")
    void testSaveBuildingsDeletedFile(@TempDir Path tempPath) throws IOException {
        Path filePath = createFileFromPath(tempPath, "deletedBuildings.json");
        FileBuildingStorageAPI storageAPI = new FileBuildingStorageAPI(filePath);
        List<Building> buildingsToSave = mockBuildings();
        filePath.toFile().delete();

        assertDoesNotThrow(() -> storageAPI.saveBuildings(buildingsToSave));

        List<Building> loadedBuildings = storageAPI.getBuildings();
        checkIfBuildingsAreCorrect(buildingsToSave, loadedBuildings);
    }

    private void checkIfBuildingsAreCorrect(List<Building> expectedBuildings, List<Building> buildings) {
        assertEquals(expectedBuildings.size(), buildings.size());
        for (int i = 0; i < buildings.size(); i++) {
            assertEquals(expectedBuildings.get(i).buildingInfo().shortName(), buildings.get(i).buildingInfo().shortName());
            assertEquals(expectedBuildings.get(i).rooms().size(), buildings.get(i).rooms().size());
        }
    }


    private Path createFileFromPath(Path tempPath, String name) throws IOException {
        Path filePath = tempPath.resolve(name);
        filePath.toFile().createNewFile();
        filePath.toFile().deleteOnExit();
        return filePath;
    }

    private List<Building> mockBuildings() {
        // Building ZL
       Building zl = createZLBuilding();

        // Building TR
        Building tr = createTRBuilding();

        return List.of(zl, tr);
    }

    private static Building createTRBuilding() {
        BuildingInfo infoTR = new BuildingInfo("TR", "TR (Technikumstrasse)", "Technikumstrasse 9", "8400", "Winterthur", "Department X");
        RoomEquipment trEq = new RoomEquipment(4, 3, Set.of(RoomInfrastructure.WHITEBOARD));
        Room trR1 = new Room("TR 1.01", "1. OG", 10, 12, false, RoomState.FREE, trEq, new ArrayList<>(), "Kleiner Unterrichtsraum", RoomType.CLASS_ROOM);
        List<Room> trRooms = new ArrayList<>();
        trRooms.add(trR1);
        Set<String> trFloors = new HashSet<>();
        trFloors.add("1. OG");

        return new Building(infoTR, trFloors, trRooms);
    }

    private static Building createZLBuilding() {
        BuildingInfo infoZL = new BuildingInfo("ZL", "ZL (Lagerstrasse)", "Lagerstrasse 45", "8004", "Zürich", "Department T");
        RoomEquipment zlEq1 = new RoomEquipment(8, 4, Set.of(RoomInfrastructure.WHITEBOARD, RoomInfrastructure.PROJECTOR));

        Room zlR1 = new Room("ZL O3.01", "3. OG", 30, 42, true, RoomState.FREE, zlEq1, new ArrayList<>(), "Großer Hörsaal, geeignet für Vorlesungen", RoomType.CLASS_ROOM);
        RoomEquipment zlEq2 = new RoomEquipment(2, 4, Set.of(RoomInfrastructure.WHITEBOARD));
        Room zlR2 = new Room("ZL 02.10", "2. OG", 3, 8, true, RoomState.FREE, zlEq2, new ArrayList<>(), "Kleiner Besprechungsraum", RoomType.MEETING_ROOM);
        List<Room> zlRooms = new ArrayList<>();
        zlRooms.add(zlR1);
        zlRooms.add(zlR2);
        Set<String> zlFloors = new HashSet<>();
        zlFloors.add("2. OG");
        zlFloors.add("3. OG");

        return new Building(infoZL, zlFloors, zlRooms);
    }
}
