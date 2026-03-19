package ch.zhaw.it.pm3.occupi.model;

import ch.zhaw.it.pm3.occupi.search.FilterCriteriaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    // ----------------------- Helpers -----------------------

    private static RoomEquipment defaultEquipment() {
        Set<RoomInfrastructure> infra = EnumSet.of(RoomInfrastructure.WHITEBOARD);
        // RoomEquipment record: (int wallplugs, int wifiQuality[0..5], Set<RoomInfrastructure>)
        return new RoomEquipment(4, 3, infra);
    }

    private static List<Event> defaultEvents() {
        return new ArrayList<>();
    }

    private static Event event(String title, int startMinFromNow, int endMinFromNow) {
        return new Event(title, "desc",
                LocalDateTime.now().plusMinutes(startMinFromNow),
                LocalDateTime.now().plusMinutes(endMinFromNow),
                List.of("tag"));
    }

    private static Room newValidRoom() {
        return new Room(
                "ZL 06.10",
                "5 OG",
                0,
                50,
                true,
                RoomState.FREE,
                defaultEquipment(),
                defaultEvents(),
                "",
                RoomType.CLASS_ROOM
        );
    }

    private static String repeat(char c, int len) {
        return String.valueOf(c).repeat(len);
    }

    // ------------------ Constructor tests ------------------

    @Test
    @DisplayName("Constructor: valid fields -> Room created with all fields set")
    void constructor_valid_creates() {
        Room r = new Room("A-101", "EG", 10, 20, true,
                RoomState.FREE, defaultEquipment(), new ArrayList<>(), "", RoomType.CLASS_ROOM);
        assertNotNull(r);
        assertEquals("A-101", r.getName());
        assertEquals("EG", r.getFloor());
        assertEquals(10, r.getOccupancy());
        assertEquals(20, r.getSeats());
        assertTrue(r.isAccessible());
        assertEquals(RoomState.FREE, r.getStatus());
        assertNotNull(r.getEquipment());
        assertNotNull(r.getCurrentEvents());
        assertEquals("", r.getDescription());
    }

    @Test
    @DisplayName("Constructor: null name -> NPE")
    void constructor_nullName_throws() {
        assertThrows(NullPointerException.class, () ->
                new Room(null, "1 OG", 0, 0, false, RoomState.FREE, defaultEquipment(), defaultEvents(), "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: empty name -> IAE")
    void constructor_emptyName_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Room("", "1 OG", 0, 0, false, RoomState.FREE, defaultEquipment(), defaultEvents(), "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: name > 50 chars -> IAE")
    void constructor_tooLongName_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Room(repeat('X', 51), "1 OG", 0, 0, false, RoomState.FREE, defaultEquipment(), defaultEvents(), "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: negative occupancy -> IAE")
    void constructor_negativeOccupancy_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Room("A", "1 OG", -1, 0, false, RoomState.FREE, defaultEquipment(), defaultEvents(), "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: negative seats -> IAE")
    void constructor_negativeSeats_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Room("A", "1 OG", 0, -1, false, RoomState.FREE, defaultEquipment(), defaultEvents(), "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: seats > 5000 -> IAE")
    void constructor_seatsTooHigh_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Room("A", "1 OG", 0, 5001, false, RoomState.FREE, defaultEquipment(), defaultEvents(), "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: null status -> NPE")
    void constructor_nullStatus_throws() {
        assertThrows(NullPointerException.class, () ->
                new Room("A", "1 OG", 0, 0, false, null, defaultEquipment(), defaultEvents(), "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: null equipment -> NPE")
    void constructor_nullEquipment_throws() {
        assertThrows(NullPointerException.class, () ->
                new Room("A", "1 OG", 0, 0, false, RoomState.FREE, null, defaultEvents(), "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: null currentEvents -> NPE")
    void constructor_nullEvents_throws() {
        assertThrows(NullPointerException.class, () ->
                new Room("A", "1 OG", 0, 0, false, RoomState.FREE, defaultEquipment(), null, "", RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: null description -> NPE")
    void constructor_nullDescription_throws() {
        assertThrows(NullPointerException.class, () ->
                new Room("A", "1 OG", 0, 0, false, RoomState.FREE, defaultEquipment(), defaultEvents(), null, RoomType.CLASS_ROOM));
    }

    @Test
    @DisplayName("Constructor: empty description -> creates Room")
    void constructor_emptyDescription_creates() {
        Room r = new Room("A", "1 OG", 0, 0, false, RoomState.FREE, defaultEquipment(), defaultEvents(), "", RoomType.CLASS_ROOM);
        assertNotNull(r);
        assertEquals("", r.getDescription());
    }


    // ---------------------- setName/getName ----------------------

    @Test
    @DisplayName("setName: valid -> updated")
    void setName_valid_updates() {
        Room r = newValidRoom();
        r.setName("ZL 06.10");
        assertEquals("ZL 06.10", r.getName());
    }

    @Test
    @DisplayName("setName: empty -> IAE")
    void setName_empty_throws() {
        Room r = newValidRoom();
        assertThrows(IllegalArgumentException.class, () -> r.setName(""));
    }

    @Test
    @DisplayName("setName: null -> NPE")
    void setName_null_throws() {
        Room r = newValidRoom();
        assertThrows(NullPointerException.class, () -> r.setName(null));
    }

    @Test
    @DisplayName("setName: length > 50 -> IAE")
    void setName_tooLong_throws() {
        Room r = newValidRoom();
        assertThrows(IllegalArgumentException.class, () -> r.setName(repeat('Y', 51)));
    }

    @Test
    @DisplayName("getName returns current name")
    void getName_returns() {
        Room r = newValidRoom();
        assertEquals("ZL 06.10", r.getName());
    }

    // ---------------------- setFloor/getFloor ----------------------

    @Test
    @DisplayName("setFloor: valid")
    void setFloor_valid() {
        Room r = newValidRoom();
        r.setFloor("5 OG");
        assertEquals("5 OG", r.getFloor());
    }

    @Test
    @DisplayName("setFloor: EG accepted")
    void setFloor_ground_ok() {
        Room r = newValidRoom();
        r.setFloor("EG");
        assertEquals("EG", r.getFloor());
    }

    @Test
    @DisplayName("setFloor: null -> NPE")
    void setFloor_null_throws() {
        Room r = newValidRoom();
        assertThrows(NullPointerException.class, () -> r.setFloor(null));
    }

    @Test
    @DisplayName("setFloor: empty -> IAE")
    void setFloor_empty_throws() {
        Room r = newValidRoom();
        assertThrows(IllegalArgumentException.class, () -> r.setFloor(" "));
    }

    @Test
    @DisplayName("getFloor returns current floor")
    void getFloor_returns() {
        Room r = newValidRoom();
        assertEquals("5 OG", r.getFloor());
    }

    // ---------------------- setOccupancy/getOccupancy ----------------------

    @Test
    @DisplayName("setOccupancy: valid-> updated")
    void setOccupancy_valid() {
        Room r = newValidRoom();
        r.setOccupancy(10);
        assertEquals(10, r.getOccupancy());
    }

    @Test
    @DisplayName("setOccupancy: zero-> updated")
    void setOccupancy_zero_updates() {
        Room r = newValidRoom();
        r.setOccupancy(0);
        assertEquals(0, r.getOccupancy());
    }

    @Test
    @DisplayName("setOccupancy: negative -> IAE")
    void setOccupancy_negative_throws() {
        Room r = newValidRoom();
        assertThrows(IllegalArgumentException.class, () -> r.setOccupancy(-1));
    }

    @Test
    @DisplayName("getOccupancy returns current occupancy")
    void getOccupancy_returns() {
        Room r = newValidRoom();
        assertEquals(0, r.getOccupancy());
    }

    // ---------------------- setSeats/getSeats ----------------------

    @Test
    @DisplayName("setSeats: valid -> updated")
    void setSeats_valid() {
        Room r = newValidRoom();
        r.setSeats(50);
        assertEquals(50, r.getSeats());
    }

    @Test
    @DisplayName("setSeats: min -> updated")
    void setSeats_min() {
        Room r = newValidRoom();
        r.setSeats(0);
        assertEquals(0, r.getSeats());
    }

    @Test
    @DisplayName("setSeats: negative -> IAE")
    void setSeats_negative_throws() {
        Room r = newValidRoom();
        assertThrows(IllegalArgumentException.class, () -> r.setSeats(-1));
    }

    @Test
    @DisplayName("setSeats: > 5000 -> IAE")
    void setSeats_tooHigh_throws() {
        Room r = newValidRoom();
        assertThrows(IllegalArgumentException.class, () -> r.setSeats(5001));
    }

    @Test
    @DisplayName("getSeats returns current seats")
    void getSeats_returns() {
        Room r = newValidRoom();
        assertEquals(50, r.getSeats());
    }

    // ---------------------- accessible/isAccessible ----------------------

    @Test
    @DisplayName("setAccessible true/false updates flag")
    void setAccessible_updates() {
        Room r = newValidRoom();
        r.setAccessible(false);
        assertFalse(r.isAccessible());
        r.setAccessible(true);
        assertTrue(r.isAccessible());
    }

    @Test
    @DisplayName("isAccessible returns current value")
    void isAccessible_returns() {
        Room r = newValidRoom();
        assertTrue(r.isAccessible());
    }

    // ---------------------- setStatus/getStatus ----------------------

    @Test
    @DisplayName("setStatus: valid -> updated")
    void setStatus_valid_updates() {
        Room r = newValidRoom();
        r.setStatus(RoomState.OCCUPIED);
        assertEquals(RoomState.OCCUPIED, r.getStatus());
    }

    @Test
    @DisplayName("setStatus: null -> NPE")
    void setStatus_null_throws() {
        Room r = newValidRoom();
        assertThrows(NullPointerException.class, () -> r.setStatus(null));
    }

    @Test
    @DisplayName("getStatus returns current status")
    void getStatus_returns() {
        Room r = newValidRoom();
        assertEquals(RoomState.FREE, r.getStatus());
    }

    // ---------------------- setEquipment/getEquipment ----------------------

    @Test
    @DisplayName("setEquipment: valid -> updated")
    void setEquipment_valid() {
        Room r = newValidRoom();
        RoomEquipment eq = new RoomEquipment(2, 5, EnumSet.of(RoomInfrastructure.PROJECTOR));
        r.setEquipment(eq);
        assertEquals(eq, r.getEquipment());
    }

    @Test
    @DisplayName("setEquipment: null -> NPE")
    void setEquipment_null_throws() {
        Room r = newValidRoom();
        assertThrows(NullPointerException.class, () -> r.setEquipment(null));
    }

    @Test
    @DisplayName("getEquipment returns current equipment")
    void getEquipment_returns() {
        Room r = newValidRoom();
        assertNotNull(r.getEquipment());
    }

    // ---------------------- getCurrentEvents/addEvent/removeEvent ----------------------

    @Test
    @DisplayName("getCurrentEvents returns unmodifiable list")
    void getCurrentEvents_unmodifiable() {
        Room r = newValidRoom();
        List<Event> view = r.getCurrentEvents();
        assertThrows(UnsupportedOperationException.class, () ->
                view.add(event("X", 10, 20)));
    }

    @Test
    @DisplayName("addEvent: valid event -> added")
    void addEvent_valid_added() {
        Room r = newValidRoom();
        Event e = event("E1", 10, 20);
        r.addEvent(e);
        assertTrue(r.getCurrentEvents().contains(e));
    }

    @Test
    @DisplayName("addEvent: null -> NPE")
    void addEvent_null_throws() {
        Room r = newValidRoom();
        assertThrows(NullPointerException.class, () -> r.addEvent(null));
    }

    @Test
    @DisplayName("addEvent: duplicate -> rejected (no second add)")
    void addEvent_duplicate_rejected() {
        Room r = newValidRoom();
        Event e = event("E1", 10, 20);
        r.addEvent(e);
        int before = r.getCurrentEvents().size();
        r.addEvent(e);
        assertEquals(before, r.getCurrentEvents().size(), "duplicate should be rejected");
    }

    @Test
    @DisplayName("addEvent: overlapping -> rejected")
    void addEvent_overlapping_rejected() {
        Room r = newValidRoom();
        Event e1 = event("E1", 10, 30);
        Event e2 = event("E2", 20, 40); // overlaps E1
        r.addEvent(e1);
        int before = r.getCurrentEvents().size();
        r.addEvent(e2);
        assertEquals(before, r.getCurrentEvents().size(), "overlapping event should be rejected");
    }

    @Test
    @DisplayName("removeEvent: existing -> removed")
    void removeEvent_existing_removed() {
        Room r = newValidRoom();
        Event e = event("E1", 10, 20);
        r.addEvent(e);
        r.removeEvent(e);
        assertFalse(r.getCurrentEvents().contains(e));
    }

    @Test
    @DisplayName("removeEvent: null -> NPE")
    void removeEvent_null_throws() {
        Room r = newValidRoom();
        assertThrows(NullPointerException.class, () -> r.removeEvent(null));
    }

    @Test
    @DisplayName("removeEvent: non-existing -> no change, no exception")
    void removeEvent_nonExisting_noChange() {
        Room r = newValidRoom();
        int before = r.getCurrentEvents().size();
        r.removeEvent(event("nope", 10, 20));
        assertEquals(before, r.getCurrentEvents().size());
    }

    // ---------------------- setDescription/getDescription ----------------------

    @Test
    @DisplayName("setDescription: valid and empty -> updated")
    void setDescription_updates() {
        Room r = newValidRoom();
        r.setDescription("Note text");
        assertEquals("Note text", r.getDescription());
        r.setDescription("");
        assertEquals("", r.getDescription());
    }

    @Test
    @DisplayName("setDescription: null -> NPE")
    void setDescription_null_throws() {
        Room r = newValidRoom();
        assertThrows(NullPointerException.class, () -> r.setDescription(null));
    }

    @Test
    @DisplayName("getDescription returns current description")
    void getDescription_returns() {
        Room r = newValidRoom();
        assertEquals("", r.getDescription());
    }

    // ---------------------- equals/hashCode ----------------------

    @Test
    @DisplayName("equals: same reference -> true")
    void equals_sameReference_true() {
        Room r = newValidRoom();
        assertEquals(r, r);
    }

    @Test
    @DisplayName("equals: equal fields -> true")
    void equals_equalFields_true() {
        Room a = newValidRoom();
        Room b = new Room(a.getName(), a.getFloor(), a.getOccupancy(), a.getSeats(), a.isAccessible(),
                a.getStatus(), a.getEquipment(), new ArrayList<>(a.getCurrentEvents()), a.getDescription(), RoomType.CLASS_ROOM);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("equals: single differing fields -> false")
    void equals_differentFields_false() {
        Room base = newValidRoom();

        Room diffName = new Room("X", base.getFloor(), base.getOccupancy(), base.getSeats(), base.isAccessible(),
                base.getStatus(), base.getEquipment(), new ArrayList<>(base.getCurrentEvents()), base.getDescription(), RoomType.CLASS_ROOM);
        assertNotEquals(base, diffName);

        Room diffFloor = new Room(base.getName(), "X", base.getOccupancy(), base.getSeats(), base.isAccessible(),
                base.getStatus(), base.getEquipment(), new ArrayList<>(base.getCurrentEvents()), base.getDescription(), RoomType.CLASS_ROOM);
        assertNotEquals(base, diffFloor);

        Room diffOcc = new Room(base.getName(), base.getFloor(), 99, base.getSeats(), base.isAccessible(),
                base.getStatus(), base.getEquipment(), new ArrayList<>(base.getCurrentEvents()), base.getDescription(), RoomType.CLASS_ROOM);
        assertNotEquals(base, diffOcc);

        Room diffSeats = new Room(base.getName(), base.getFloor(), base.getOccupancy(), 999, base.isAccessible(),
                base.getStatus(), base.getEquipment(), new ArrayList<>(base.getCurrentEvents()), base.getDescription(), RoomType.CLASS_ROOM);
        assertNotEquals(base, diffSeats);

        Room diffAccess = new Room(base.getName(), base.getFloor(), base.getOccupancy(), base.getSeats(), !base.isAccessible(),
                base.getStatus(), base.getEquipment(), new ArrayList<>(base.getCurrentEvents()), base.getDescription(), RoomType.CLASS_ROOM);
        assertNotEquals(base, diffAccess);

        Room diffStatus = new Room(base.getName(), base.getFloor(), base.getOccupancy(), base.getSeats(), base.isAccessible(),
                RoomState.OCCUPIED, base.getEquipment(), new ArrayList<>(base.getCurrentEvents()), base.getDescription(), RoomType.CLASS_ROOM);
        assertNotEquals(base, diffStatus);

        Room diffEquip = new Room(base.getName(), base.getFloor(), base.getOccupancy(), base.getSeats(), base.isAccessible(),
                base.getStatus(), new RoomEquipment(1, 0, EnumSet.of(RoomInfrastructure.PROJECTOR)),
                new ArrayList<>(base.getCurrentEvents()), base.getDescription(), RoomType.CLASS_ROOM);
        assertNotEquals(base, diffEquip);

        List<Event> differentEvents = new ArrayList<>();
        differentEvents.add(event("E", 10, 20));
        Room diffEvents = new Room(base.getName(), base.getFloor(), base.getOccupancy(), base.getSeats(), base.isAccessible(),
                base.getStatus(), base.getEquipment(), differentEvents, base.getDescription(), RoomType.CLASS_ROOM);
        assertNotEquals(base, diffEvents);

        Room diffDescription = new Room(base.getName(), base.getFloor(), base.getOccupancy(), base.getSeats(), base.isAccessible(),
                base.getStatus(), base.getEquipment(), new ArrayList<>(base.getCurrentEvents()), "other", RoomType.CLASS_ROOM);
        assertNotEquals(base, diffDescription);
    }

    // ---------------------- toString ----------------------

    @Test
    @DisplayName("toString: contains all fields")
    void toString_containsAllFields() {
        Room r = newValidRoom();
        String s = r.toString();
        assertNotNull(s);
        assertTrue(s.contains("name"));
        assertTrue(s.contains("floor"));
        assertTrue(s.contains("occupancy"));
        assertTrue(s.contains("seats"));
        assertTrue(s.contains("accessible"));
        assertTrue(s.contains("status"));
        assertTrue(s.contains("equipment"));
        assertTrue(s.contains("currentEvents"));
        assertTrue(s.contains("description"));
    }

    // ---------------------- roomType (get/set) ----------------------

    @Test
    @DisplayName("getRoomType returns the type passed to the constructor")
    void getRoomType_returnsConstructorType() {
        Room room = new Room(
                "Lab 1",
                "EG",
                0,
                10,
                false,
                RoomState.FREE,
                defaultEquipment(),
                defaultEvents(),
                "",
                RoomType.MEETING_ROOM
        );

        assertEquals(RoomType.MEETING_ROOM, room.getRoomType(),
                "getRoomType must return the room type that was set in the constructor");
    }

    // ---------------------- matchesCriteria ----------------------

    @Test
    @DisplayName("matchesCriteria: null criteria -> NPE")
    void matchesCriteria_null_throws() {
        Room room = newValidRoom();
        assertThrows(NullPointerException.class,
                () -> room.matchesCriteria(null),
                "matchesCriteria(null) must throw NullPointerException");
    }

    @Test
    @DisplayName("matchesCriteria: blank query and no criteria -> always matches")
    void matchesCriteria_blankQuery_noCriteria_returnsTrue() {
        Room room = newValidRoom();

        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                "   ",      // blank search query
                null,       // city
                null,       // building
                null,       // roomType
                null,       // floor
                0,          // capacity
                null        // infrastructure
        );

        assertTrue(room.matchesCriteria(criteria),
                "Room must match when no criteria are specified and the query is blank");
    }

    @Test
    @DisplayName("matchesCriteria: criteria and requirements satisfied -> room matches")
    void matchesCriteria_criteriaMet_returnsTrue() {
        Room room = newValidRoom(); // name: "ZL 06.10", floor: "5 OG", seats: 50, type: CLASS_ROOM
        Set<RoomInfrastructure> wantedInfra = EnumSet.of(RoomInfrastructure.WHITEBOARD);

        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                "",                         // blank query -> matchesQuery = true
                null,                       // city
                null,                       // building
                RoomType.CLASS_ROOM,        // same as room
                "5 OG",                     // same floor as room
                10,                         // <= seats (50)
                wantedInfra                 // subset of room equipment
        );

        assertTrue(room.matchesCriteria(criteria),
                "Room must match when floor, type, capacity and infrastructure all match");
    }

    @Test
    @DisplayName("matchesCriteria: required capacity too high -> room does not match")
    void matchesCriteria_capacityTooHigh_returnsFalse() {
        Room room = newValidRoom();
        Set<RoomInfrastructure> wantedInfra = EnumSet.of(RoomInfrastructure.WHITEBOARD);

        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                "",                         // blank query -> matchesQuery = true
                null,
                null,
                RoomType.CLASS_ROOM,
                "5 OG",
                5000,                       // > seats (50)
                wantedInfra
        );

        assertFalse(room.matchesCriteria(criteria),
                "Room must not match when required capacity is higher than available seats");
    }

    @Test
    @DisplayName("matchesCriteria: query does not match room -> room does not match")
    void matchesCriteria_queryDoesNotMatch_returnsFalse() {
        Room room = newValidRoom();

        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                "THIS-DOES-NOT-MATCH",          // non-blank, so containsCriteria = true
                null,                                       // city
                null,                                       // building
                null,                                       // roomType
                null,                                       // floor
                0,                                          // capacity
                EnumSet.noneOf(RoomInfrastructure.class)    // infrastructure
        );

        assertFalse(room.matchesCriteria(criteria),
                "Room must not match when the free-text query does not match any field");
    }
}


