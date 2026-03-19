package ch.zhaw.it.pm3.occupi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class RoomInfrastructureTest {

    @Test
    @DisplayName("getName: each constant returns expected display name")
    void getName_returnsDisplayName() {
        assertEquals("Wandtafel", RoomInfrastructure.BLACKBOARD.getName());
        assertEquals("Whiteboard", RoomInfrastructure.WHITEBOARD.getName());
        assertEquals("Flipchart", RoomInfrastructure.FLIPCHART.getName());
        assertEquals("Beamer", RoomInfrastructure.PROJECTOR.getName());
        assertEquals("Klimaanlage", RoomInfrastructure.AIR_CONDITIONER.getName());
    }

    @Test
    @DisplayName("getByName: exact display name returns corresponding enum")
    void getByName_exact_matches() {
        assertEquals(RoomInfrastructure.WHITEBOARD, RoomInfrastructure.getByName("Whiteboard"));
        assertEquals(RoomInfrastructure.PROJECTOR, RoomInfrastructure.getByName("Beamer"));
    }

    @Test
    @DisplayName("getByName: case-insensitive lookup")
    void getByName_caseInsensitive_matches() {
        assertEquals(RoomInfrastructure.AIR_CONDITIONER, RoomInfrastructure.getByName("klimaanlage"));
        assertEquals(RoomInfrastructure.BLACKBOARD, RoomInfrastructure.getByName("WANdtafEL"));
    }

    @Test
    @DisplayName("getByName: unknown name")
    void getByName_unknown_throws() {
        assertNull(RoomInfrastructure.getByName("UnknownInfra"));
    }

    @Test
    @DisplayName("getByName: null input -> NullPointerException")
    void getByName_null_throws() {
        assertThrows(NullPointerException.class, () -> RoomInfrastructure.getByName(null));
    }
}
