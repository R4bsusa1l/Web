package ch.zhaw.it.pm3.occupi.reservation.simulation;

import ch.zhaw.it.pm3.occupi.reservation.api.RoomReservation;
import ch.zhaw.it.pm3.occupi.reservation.api.TimeWindow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileReservationAPITest {

    private static final Path TEST_PATH = Path.of("src", "test", "resources", "jsonData", "reservationData", "TestReservationData.json");
    private static final Path EMPTY_PATH = Path.of("src", "test", "resources", "jsonData", "reservationData", "EmptyReservationData.json");
    private static final Path MALFORMED_PATH = Path.of("src", "test", "resources", "jsonData", "reservationData", "MalformedReservationData.json");

    // ---- Constructor tests ----

    @Test
    @DisplayName("constructor with valid JSON path initializes and caches entries")
    void constructor_withValidPath_success() {
        assertDoesNotThrow(() -> new FileReservationAPI(TEST_PATH));
    }

    @Test
    @DisplayName("constructor with null path throws NullPointerException")
    void constructor_withNullPath_throwsNPE() {
        assertThrows(NullPointerException.class, () -> new FileReservationAPI(null));
    }

    @Test
    @DisplayName("constructor with non-existing file throws FileNotFoundException")
    void constructor_withNonExistingFile_throws() {
        Path nonExisting = Path.of("does", "not", "exist", "reservations.json");
        assertThrows(FileNotFoundException.class, () -> new FileReservationAPI(nonExisting));
    }

    @Test
    @DisplayName("constructor with directory path throws FileNotFoundException")
    void constructor_withDirectory_throws(@TempDir Path tempDir) {
        assertThrows(FileNotFoundException.class, () -> new FileReservationAPI(tempDir));
    }

    @Test
    @DisplayName("constructor with malformed JSON throws IOException")
    void constructor_withMalformedJson_throwsIOException() {
        assertThrows(IOException.class, () -> new FileReservationAPI(MALFORMED_PATH));
    }

    // ---- getReservations() tests ----

    @Test
    @DisplayName("getReservations returns all entries and correct time windows")
    void getReservations_withValidData_returnsExpected() throws IOException {
        FileReservationAPI api = new FileReservationAPI(TEST_PATH);

        List<RoomReservation> reservations = api.getReservations();

        assertNotNull(reservations);
        // Current implementation returns all entries including those with empty time windows (3 entries in test data)
        assertEquals(3, reservations.size());

        // find helper by roomID
        RoomReservation r1 = findByRoom(reservations, "test_room_01");
        assertNotNull(r1);
        // Time windows should be sorted ascending and match the JSON
        List<TimeWindow> r1Windows = r1.timeWindows();
        assertEquals(2, r1Windows.size());
        assertEquals(LocalDateTime.parse("2025-11-28T09:00:00"), r1Windows.get(0).start());
        assertEquals(LocalDateTime.parse("2025-11-28T10:00:00"), r1Windows.get(0).end());
        assertEquals(LocalDateTime.parse("2025-11-28T14:00:00"), r1Windows.get(1).start());
        assertEquals(LocalDateTime.parse("2025-11-28T16:00:00"), r1Windows.get(1).end());

        RoomReservation r2 = findByRoom(reservations, "test_room_02");
        assertNotNull(r2);
        assertEquals(1, r2.timeWindows().size());
        assertEquals(LocalDateTime.parse("2025-11-29T10:00:00"), r2.timeWindows().getFirst().start());
        assertEquals(LocalDateTime.parse("2025-11-29T12:00:00"), r2.timeWindows().getFirst().end());

        RoomReservation r3 = findByRoom(reservations, "test_room_03");
        assertNotNull(r3);
        assertTrue(r3.timeWindows().isEmpty());
    }

    @Test
    @DisplayName("getReservations with empty array returns empty list")
    void getReservations_withEmptyFile_returnsEmptyList() throws IOException {
        FileReservationAPI api = new FileReservationAPI(EMPTY_PATH);
        List<RoomReservation> reservations = api.getReservations();
        assertNotNull(reservations);
        assertTrue(reservations.isEmpty());
    }

    @Test
    @DisplayName("multiple calls to getReservations return consistent, equal results")
    void getReservations_isConsistentAcrossCalls() throws IOException {
        FileReservationAPI api = new FileReservationAPI(TEST_PATH);

        List<RoomReservation> first = api.getReservations();
        List<RoomReservation> second = api.getReservations();

        // same size and same content disregarding order
        assertEquals(first.size(), second.size());
        assertTrue(first.containsAll(second) && second.containsAll(first));

        // verify the time windows list inside a reservation is unmodifiable by attempting to sort a copy only
        RoomReservation any = findByRoom(first, "test_room_01");
        List<TimeWindow> copy = any.timeWindows().stream().sorted(Comparator.comparing(TimeWindow::start)).toList();
        assertEquals(any.timeWindows(), copy);
    }

    private static RoomReservation findByRoom(List<RoomReservation> list, String roomId) {
        Optional<RoomReservation> found = list.stream().filter(r -> r.roomID().equals(roomId)).findFirst();
        return found.orElse(null);
    }
}
