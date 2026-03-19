package ch.zhaw.it.pm3.occupi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RoomEquipment}.
 * Aligned with the current record design and the (adapted) test concept.
 */
class RoomEquipmentTest {

    private static Set<RoomInfrastructure> defaultInfra() {
        return EnumSet.of(RoomInfrastructure.WHITEBOARD, RoomInfrastructure.PROJECTOR);
    }

    // -----------------------------------------------------------------------
    // Constructor & Validation (maps the constructor section of the concept)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: valid fields -> created with all components set")
    void constructor_valid_creates() {
        RoomEquipment eq = new RoomEquipment(4, 3, defaultInfra());
        assertNotNull(eq);
        assertEquals(4, eq.wallplugs());
        assertEquals(3, eq.wifiQuality());
        assertEquals(defaultInfra(), eq.roomInfrastructure());
    }

    @Test
    @DisplayName("Constructor: wallplugs < 0 -> IllegalArgumentException")
    void constructor_wallplugsNegative_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoomEquipment(-1, 3, defaultInfra()));
    }

    @Test
    @DisplayName("Constructor: wallplugs > 50 -> IllegalArgumentException")
    void constructor_wallplugsOver50_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoomEquipment(51, 3, defaultInfra()));
    }

    @Test
    @DisplayName("Constructor: wifiQuality < 0 -> IllegalArgumentException")
    void constructor_wifiBelowZero_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoomEquipment(0, -1, defaultInfra()));
    }

    @Test
    @DisplayName("Constructor: wifiQuality > 5 -> IllegalArgumentException")
    void constructor_wifiAboveFive_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoomEquipment(0, 6, defaultInfra()));
    }

    @Test
    @DisplayName("Constructor: roomInfrastructure = null -> NullPointerException")
    void constructor_infraNull_throws() {
        assertThrows(NullPointerException.class,
                () -> new RoomEquipment(0, 3, null));
    }

    @Test
    @DisplayName("Constructor: empty infrastructure set is allowed")
    void constructor_emptyInfra_ok() {
        RoomEquipment eq = new RoomEquipment(0, 0, EnumSet.noneOf(RoomInfrastructure.class));
        assertTrue(eq.roomInfrastructure().isEmpty());
    }

    @Test
    @DisplayName("Constructor: edge values (wallplugs=0) allowed")
    void constructor_edgeValueWallplug_ok() {
        assertDoesNotThrow(() -> new RoomEquipment(0, 5, defaultInfra()));
    }

    @Test
    @DisplayName("Constructor: edge values (wifi=0) allowed")
    void constructor_edgeValueWifiMin_ok() {
        assertDoesNotThrow(() -> new RoomEquipment(3, 0, defaultInfra()));
    }

    @Test
    @DisplayName("Constructor: edge values (wifi=5) allowed")
    void constructor_edgeValueWifiMax_ok() {
        assertDoesNotThrow(() -> new RoomEquipment(3, 5, defaultInfra()));
    }

    // -----------------------------------------------------------------------
    // Accessors (maps getWallplugs/getWifiQuality from the concept)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Accessors: wallplugs() return current values")
    void accessors_returnWallplugValues() {
        Set<RoomInfrastructure> infra = EnumSet.of(RoomInfrastructure.FLIPCHART);
        RoomEquipment eq = new RoomEquipment(9, 5, infra);
        assertEquals(9, eq.wallplugs());
        assertEquals(infra, eq.roomInfrastructure());
    }


    @Test
    @DisplayName("Accessors: wifi() return current values")
    void accessors_returnWifiValues() {
        Set<RoomInfrastructure> infra = EnumSet.of(RoomInfrastructure.FLIPCHART);
        RoomEquipment eq = new RoomEquipment(9, 3, infra);
        assertEquals(3, eq.wifiQuality());
        assertEquals(infra, eq.roomInfrastructure());
    }

    // -----------------------------------------------------------------------
    // RoomInfrastructure() get method tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("RoomInfrastructure: get RoomInfrastructure set")
    void roomInfrastructure_getSet() {
        Set<RoomInfrastructure> infra = EnumSet.of(RoomInfrastructure.AIR_CONDITIONER, RoomInfrastructure.FLIPCHART, RoomInfrastructure.PROJECTOR);
        RoomEquipment eq = new RoomEquipment(1, 1, infra);

        assertTrue(eq.roomInfrastructure().contains(RoomInfrastructure.AIR_CONDITIONER));
        assertTrue(eq.roomInfrastructure().contains(RoomInfrastructure.FLIPCHART));
        assertTrue(eq.roomInfrastructure().contains(RoomInfrastructure.PROJECTOR));
        assertEquals(3, eq.roomInfrastructure().size());
    }

    @Test
    @DisplayName("RoomInfrastructure: try modify returned Set -> UnsupportedOperationException")
    void roomInfrastructure_modifySetException() {
        Set<RoomInfrastructure> infra = EnumSet.of(RoomInfrastructure.AIR_CONDITIONER, RoomInfrastructure.FLIPCHART, RoomInfrastructure.PROJECTOR);
        RoomEquipment eq = new RoomEquipment(1, 1, infra);

        assertThrows(UnsupportedOperationException.class, () -> eq.roomInfrastructure().add(RoomInfrastructure.WHITEBOARD));

    }

    //-----------------------------------------------------------------------
    @Test
    @DisplayName("Infrastructure: adding AIR_CONDITIONER is reflected")
    void infrastructure_hasAirconditioner_reflected() {
        Set<RoomInfrastructure> infra = EnumSet.of(RoomInfrastructure.AIR_CONDITIONER);
        RoomEquipment eq = new RoomEquipment(1, 1, infra);

        assertTrue(eq.roomInfrastructure().contains(RoomInfrastructure.AIR_CONDITIONER));
    }

    @Test
    @DisplayName("Infrastructure: adding AIR_CONDITIONER is reflected")
    void infrastructure_hasProjector_reflected() {
        Set<RoomInfrastructure> infra = EnumSet.of(RoomInfrastructure.PROJECTOR);
        RoomEquipment eq = new RoomEquipment(1, 1, infra);

        assertTrue(eq.roomInfrastructure().contains(RoomInfrastructure.PROJECTOR));
    }
}
