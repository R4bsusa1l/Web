package ch.zhaw.it.pm3.occupi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTypeTest {

    @Test
    @DisplayName("getDisplayName: returns localized name for each enum")
    void getDisplayName_returns() {
        assertEquals("Klassenzimmer", RoomType.CLASS_ROOM.getDisplayName());
        assertEquals("Besprechungsraum", RoomType.MEETING_ROOM.getDisplayName());
    }

    @Test
    @DisplayName("fromString: exact match returns enum")
    void getByName_exact_matches() {
        assertEquals(RoomType.CLASS_ROOM, RoomType.getByName("Klassenzimmer"));
        assertEquals(RoomType.MEETING_ROOM, RoomType.getByName("Besprechungsraum"));
    }

    @Test
    @DisplayName("fromString: case-insensitive match")
    void getByName_caseInsensitive_matches() {
        assertEquals(RoomType.CLASS_ROOM, RoomType.getByName("klassenzimmer"));
        assertEquals(RoomType.MEETING_ROOM, RoomType.getByName("BESPRECHUNGSRAUM"));
    }

    @Test
    @DisplayName("fromString: unknown returns null")
    void getByName_unknown_returnsNull() {
        assertNull(RoomType.getByName("Unknown"));
    }

    @Test
    @DisplayName("fromString: null input throws NullPointerException")
    void getByName_null_throws() {
        assertThrows(NullPointerException.class, () -> RoomType.getByName(null));
    }
}

