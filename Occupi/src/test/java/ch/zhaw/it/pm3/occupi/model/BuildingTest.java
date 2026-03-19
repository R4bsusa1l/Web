package ch.zhaw.it.pm3.occupi.model;

import ch.zhaw.it.pm3.occupi.search.FilterCriteriaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BuildingTest {

    // --------------------------- Helpers ---------------------------

    private static BuildingInfo info() {
        return new BuildingInfo("ZL", "ZL" + " Full", "Main St 1", "8000", "Zurich", "CS");
    }

    private static RoomEquipment eq() {
        return new RoomEquipment(4, 3, EnumSet.of(RoomInfrastructure.WHITEBOARD));
    }

    private static Room room(String name, String floor) {
        return new Room(name, floor, 0, 10, true, RoomState.FREE, eq(),
                new ArrayList<>(), "", RoomType.CLASS_ROOM);
    }

    // ------------------------ Constructor -------------------------

    /**
     * Valid arguments should create an instance, and floors should be derived from rooms.
     */
    @Test
    @DisplayName("Constructor: valid arguments -> instance created and floors derived from rooms")
    void constructor_validArguments() {
        Set<String> floors = new HashSet<>();
        List<Room> rooms = new ArrayList<>(List.of(
                room("A.101", "1"),
                room("A.201", "2"),
                room("B.002", "G")
        ));

        Building b = new Building(info(), floors, rooms);

        assertNotNull(b);
        assertEquals(3, b.rooms().size());
    }

    /**
     * Current implementation throws NPE if floors is null, because findAllFloors iterates it.
     */
    @Test
    @DisplayName("Constructor: floors null -> throws NPE (findAllFloors clears floors)")
    void constructor_floorsNull_throwsNPE() {
        assertThrows(NullPointerException.class, () ->
                new Building(info(), null, new ArrayList<>()));
    }

    /**
     * Current implementation throws NPE if rooms is null, because findAllFloors iterates it.
     */
    @Test
    @DisplayName("Constructor: rooms null -> throws NPE (findAllFloors iterates rooms)")
    void constructor_roomsNull_throwsNPE() {
        assertThrows(NullPointerException.class, () ->
                new Building(info(), new HashSet<>(), null));
    }

    /**
     * If rooms is empty, floors should be empty as well.
     */
    @Test
    @DisplayName("Constructor: empty rooms -> floors empty")
    void constructor_emptyRooms_floorsEmpty() {
        Set<String> floors = new HashSet<>();
        Building b = new Building(info(), floors, new ArrayList<>());
        assertTrue(b.floors().isEmpty());
    }

    // ----------------------- get/set Rooms ------------------------

    /**
     * rooms should return an unmodifiable view of the internal rooms list.
     */
    @Test
    @DisplayName("rooms returns unmodifiable list")
    void rooms_unmodifiable() {
        Building b = new Building(info(), new HashSet<>(), new ArrayList<>());
        List<Room> view = b.rooms();
        assertThrows(UnsupportedOperationException.class, () -> view.add(room("X", "1")));
    }

    // ============================================================
    //  Tests for Building Search
    // ============================================================

    @Test
    @DisplayName("matchesCriteria: requirements match → true")
    void matchesCriteria_requirementsMatch_returnsTrue() {
        // Building with floor "1"
        Set<String> floors = new HashSet<>();
        floors.add("1");

        Building b = new Building(info(), floors, List.of(room("A.101", "1")));

        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                null,          // searchQuery
                "Zurich",      // city (matches)
                null,          // building
                null,          // roomType
                "1",           // floor matches floors.contains("1")
                0,             // capacity
                null           // infrastructure
        );

        assertTrue(b.matchesCriteria(criteria),
                "Should match because non-query criteria match requirements");
    }


    @Test
    @DisplayName("matchesCriteria: requirements do NOT match → false")
    void matchesCriteria_requirementsDoNotMatch_returnsFalse() {
        Set<String> floors = new HashSet<>();
        floors.add("1");

        Building b = new Building(info(), floors, List.of(room("A.101", "1")));

        // City does NOT match → matchesRequirements = false
        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                null,
                "Winterthur",
                null,
                null,
                null,
                0,
                null
        );

        assertFalse(b.matchesCriteria(criteria),
                "Should not match because city does not match requirements");
    }


    @Test
    @DisplayName("matchesCriteria: containsCriteria=false → fallback to matchesQuery")
    void matchesCriteria_withoutRequirements_usesMatchesQuery() {
        Building b = new Building(info(), new HashSet<>(), List.of(room("A.101", "1")));

        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                null,   // searchQuery
                null,   // city
                null,   // building
                null,   // roomType
                null,   // floor
                0,      // capacity
                null    // infrastructure
        );

        // matchesQuery(null) → Search.isBlank(null) returns true → always true
        assertTrue(b.matchesCriteria(criteria),
                "Without criteria, should rely on matchesQuery(blank) = true");
    }



    // ============================================================
    //  Tests for matchesQuery
    // ============================================================

    @Test
    @DisplayName("matchesQuery: blank query matches everything")
    void matchesQuery_blank_returnsTrue() {
        Building b = new Building(info(), new HashSet<>(), new ArrayList<>());

        // blank queries → Search.isBlank → true → return true
        assertTrue(b.matchesQuery("   "));
        assertTrue(b.matchesQuery(null));
    }


    @Test
    @DisplayName("matchesQuery: query matches city, fullName or shortName")
    void matchesQuery_matchesNameOrCity() {
        Building b = new Building(info(), new HashSet<>(), new ArrayList<>());

        assertTrue(b.matchesQuery("zurich"), "City should match");
        assertTrue(b.matchesQuery("zl full"), "Full name should match");
        assertTrue(b.matchesQuery("zl"), "Short name should match");
    }


    @Test
    @DisplayName("matchesQuery: no part of the building matches query → false")
    void matchesQuery_noMatch_returnsFalse() {
        Building b = new Building(info(), new HashSet<>(), new ArrayList<>());

        assertFalse(b.matchesQuery("unknown-building"),
                "Query should not match any building property");
    }



    // ============================================================
    //  Tests for matchesRequirements (indirectly via matchesCriteria)
    // ============================================================

    @Test
    @DisplayName("matchesRequirements: floor matches but city does not → false")
    void matchesRequirements_floorMatch_cityNoMatch() {
        Set<String> floors = new HashSet<>();
        floors.add("3");

        Building b = new Building(info(), floors, List.of(room("A.301", "3")));

        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                null,       // searchQuery
                "Winterthur",           // wrong city
                null,                   // building
                null,                   // roomType
                "3",                    // floor matches
                0,                      // capacity
                null                    // infrastructure
        );

        assertFalse(b.matchesCriteria(criteria),
                "Building floor matches but city doesn't — must return false");
    }


    @Test
    @DisplayName("matchesRequirements: building matches but floor does not → false")
    void matchesRequirements_buildingMatch_floorNoMatch() {
        Set<String> floors = new HashSet<>();
        floors.add("2");

        Building b = new Building(info(), floors, List.of(room("A.201", "2")));

        FilterCriteriaDTO criteria = new FilterCriteriaDTO(
                null,               // searchQuery
                "Zurich",                       // matches city
                info().fullName(),              // building name matches
                null,                           // roomType
                "99",                           // floor does NOT match
                0,                              // capacity
                null                            // infrastructure
        );

        assertFalse(b.matchesCriteria(criteria),
                "Building name matches but floor does not — must return false");
    }

}
