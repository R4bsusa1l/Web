package ch.zhaw.it.pm3.occupi.history.simulation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the FileHistoryAPI implementation.
 * Tests verify the correct behavior of reading room usage history from JSON files,
 * including edge cases, error handling, and data validation.
 */
class FileHistoryAPITest {

    private static final Path MOCK_PATH = Path.of("src", "test", "resources", "jsonData", "historyData", "TestHistoryData.json");
    private static final Path EMPTY_PATH = Path.of("src", "test", "resources", "jsonData", "historyData", "EmptyHistoryData.json");
    private static final Path SINGLE_ENTRY_PATH = Path.of("src", "test", "resources", "jsonData", "historyData", "SingleEntryHistoryData.json");
    private static final Path MALFORMED_PATH = Path.of("src", "test", "resources", "jsonData", "historyData", "MalformedHistoryData.json");
    // ---- Constructor Tests ----
    @Test
    @DisplayName("Test valid path in constructor")
    void testValidPathInConstructor() {
        assertDoesNotThrow(() -> new FileHistoryAPI(MOCK_PATH));
    }

    @Test
    @DisplayName("Test non-existent file in constructor")
    void testNonExistentFileInConstructor() {
        Path nonExistentPath = Path.of("nonexistent", "history.json");
        assertThrows(IllegalArgumentException.class, () -> new FileHistoryAPI(nonExistentPath));
    }

    @Test
    @DisplayName("Test null path in constructor")
    void testNullPathInConstructor() {
        assertThrows(NullPointerException.class, () -> new FileHistoryAPI(null));
    }

    @Test
    @DisplayName("Test directory path in constructor")
    void testDirectoryPathInConstructor(@TempDir Path tempDir) {
        assertThrows(IllegalArgumentException.class, () -> new FileHistoryAPI(tempDir));
    }

    @Test
    void testNotJsonPathInConstructor() throws IOException {
        Path tempFile = Files.createTempFile("notJsonFile", ".txt");
        assertThrows(IllegalArgumentException.class, () -> new FileHistoryAPI(tempFile));
        Files.deleteIfExists(tempFile);
    }

    // ---- getRoomUsageHistory() Tests ----

    @Test
    @DisplayName("Test getRoomUsageHistory() with valid room containing multiple entries")
    void testGetRoomUsageHistoryWithValidRoom() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> history = historyAPI.getRoomUsageHistory("Room A");

        assertNotNull(history);
        assertEquals(3, history.size());
        assertEquals(15, history.get(LocalDateTime.parse("2025-11-15T10:00:00")));
        assertEquals(20, history.get(LocalDateTime.parse("2025-11-15T14:00:00")));
        assertEquals(10, history.get(LocalDateTime.parse("2025-11-16T09:00:00")));
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with another valid room")
    void testGetRoomUsageHistoryWithAnotherValidRoom() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> history = historyAPI.getRoomUsageHistory("Room B");

        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(8, history.get(LocalDateTime.parse("2025-11-16T09:00:00")));
        assertEquals(12, history.get(LocalDateTime.parse("2025-11-16T13:00:00")));
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with non-existent room")
    void testGetRoomUsageHistoryWithNonExistentRoom() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> history = historyAPI.getRoomUsageHistory("Non Existent Room");

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with null roomId")
    void testGetRoomUsageHistoryWithNullRoomId() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        assertThrows(NullPointerException.class, () -> historyAPI.getRoomUsageHistory(null));
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with empty roomId")
    void testGetRoomUsageHistoryWithEmptyRoomId() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> history = historyAPI.getRoomUsageHistory("");

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with empty JSON array")
    void testGetRoomUsageHistoryWithEmptyJsonArray() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(EMPTY_PATH);

        Map<LocalDateTime, Integer> history = historyAPI.getRoomUsageHistory("Any Room");

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with room having empty history")
    void testGetRoomUsageHistoryWithEmptyHistory() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> history = historyAPI.getRoomUsageHistory("Room C");

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with single entry")
    void testGetRoomUsageHistoryWithSingleEntry() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(SINGLE_ENTRY_PATH);

        Map<LocalDateTime, Integer> history = historyAPI.getRoomUsageHistory("Single Room");

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(5, history.get(LocalDateTime.parse("2025-12-01T10:00:00")));
    }
    
    @Test
    @DisplayName("Test getRoomUsageHistory() with multiple entries")
    void testGetRoomUsageHistoryWithMultipleEntries() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> history = historyAPI.getRoomUsageHistory("Room A");

        assertNotNull(history);
        assertEquals(3, history.size());
        assertEquals(10, history.get(LocalDateTime.parse("2025-11-16T09:00:00")));
        assertEquals(15, history.get(LocalDateTime.parse("2025-11-15T10:00:00")));
        assertEquals(20, history.get(LocalDateTime.parse("2025-11-15T14:00:00")));
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with corrupted JSON")
    void testGetRoomUsageHistoryWithCorruptedJson() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MALFORMED_PATH);

        assertThrows(IllegalStateException.class, () -> historyAPI.getRoomUsageHistory("Any Room"));
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with deleted file after construction")
    void testGetRoomUsageHistoryWithDeletedFile(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("deletedHistory.json");
        Files.writeString(tempFile, "[]");

        FileHistoryAPI historyAPI = new FileHistoryAPI(tempFile);
        Files.delete(tempFile);

        assertThrows(IllegalStateException.class, () -> historyAPI.getRoomUsageHistory("Any Room"));
    }


    @Test
    @DisplayName("Test multiple calls to getRoomUsageHistory() return consistent results")
    void testMultipleCallsReturnConsistentResults() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> firstCall = historyAPI.getRoomUsageHistory("Room A");
        Map<LocalDateTime, Integer> secondCall = historyAPI.getRoomUsageHistory("Room A");

        assertEquals(firstCall, secondCall);
    }

    @Test
    @DisplayName("Test getRoomUsageHistory() with different rooms from same file")
    void testGetRoomUsageHistoryWithDifferentRooms() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> roomAHistory = historyAPI.getRoomUsageHistory("Room A");
        Map<LocalDateTime, Integer> roomBHistory = historyAPI.getRoomUsageHistory("Room B");

        assertNotNull(roomAHistory);
        assertNotNull(roomBHistory);
        assertEquals(3, roomAHistory.size());
        assertEquals(2, roomBHistory.size());
    }

    @Test
    @DisplayName("Test FileHistoryAPI is stateless across multiple operations")
    void testStatelessBehavior() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        historyAPI.getRoomUsageHistory("Room A");
        historyAPI.getRoomUsageHistory("Room B");
        historyAPI.getRoomUsageHistory("Non Existent");
        Map<LocalDateTime, Integer> finalResult = historyAPI.getRoomUsageHistory("Room A");

        assertEquals(3, finalResult.size());
    }

    @Test
    @DisplayName("Test case sensitivity of roomId")
    void testRoomIdCaseSensitivity() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> roomA = historyAPI.getRoomUsageHistory("Room A");
        Map<LocalDateTime, Integer> roomLowerCase = historyAPI.getRoomUsageHistory("room a");

        assertEquals(3, roomA.size());
        assertTrue(roomLowerCase.isEmpty());
    }

    @Test
    @DisplayName("Test with whitespace in roomId")
    void testRoomIdWithWhitespace() {
        FileHistoryAPI historyAPI = new FileHistoryAPI(MOCK_PATH);

        Map<LocalDateTime, Integer> withWhitespace = historyAPI.getRoomUsageHistory(" Room A ");

        assertTrue(withWhitespace.isEmpty());
    }
}

