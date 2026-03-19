package ch.zhaw.it.pm3.occupi.controllers;

import ch.zhaw.it.pm3.occupi.model.Building;
import ch.zhaw.it.pm3.occupi.model.BuildingInfo;
import ch.zhaw.it.pm3.occupi.model.Event;
import ch.zhaw.it.pm3.occupi.model.Room;
import ch.zhaw.it.pm3.occupi.model.RoomEquipment;
import ch.zhaw.it.pm3.occupi.model.RoomInfrastructure;
import ch.zhaw.it.pm3.occupi.model.RoomState;
import ch.zhaw.it.pm3.occupi.model.RoomType;
import ch.zhaw.it.pm3.occupi.reservation.api.ReservationAPI;
import ch.zhaw.it.pm3.occupi.reservation.api.RoomReservation;
import ch.zhaw.it.pm3.occupi.reservation.api.TimeWindow;
import ch.zhaw.it.pm3.occupi.search.FilterCriteriaDTO;
import ch.zhaw.it.pm3.occupi.search.PaginatedSearchResult;
import ch.zhaw.it.pm3.occupi.sensors.api.RoomOccupancy;
import ch.zhaw.it.pm3.occupi.sensors.api.SensorAPI;
import ch.zhaw.it.pm3.occupi.storage.BuildingStorageAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.zhaw.it.pm3.occupi.model.RoomInfrastructure.FLIPCHART;
import static ch.zhaw.it.pm3.occupi.model.RoomInfrastructure.WHITEBOARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildingControllerTest {

    private static final int PAGE_INDEX = 0;
    private static final int PAGE_SIZE = 10;
    @Mock
    private ReservationAPI reservationAPI;

    @Mock
    private SensorAPI sensorAPI;

    @Mock
    private BuildingStorageAPI buildingStorageAPI;

    private BuildingController controller;
    private List<Building> testBuildings;
    private List<Room> testRooms;
    private Room testRoom1;

    @BeforeEach
    void setUp() {
        // Setup test rooms
        testRoom1 = new Room(
                "ZL_06.01",
                "6.OG",
                50,
                30,
                true,
                RoomState.FREE,
                new RoomEquipment(10, 5, Set.of(RoomInfrastructure.PROJECTOR)),
                new ArrayList<>(),
                "",
                RoomType.CLASS_ROOM
        );

        Room testRoom2 = new Room(
                "ZL_07.02",
                "7.OG",
                20,
                15,
                false,
                RoomState.FREE,
                new RoomEquipment(5, 4, Set.of(RoomInfrastructure.WHITEBOARD, RoomInfrastructure.FLIPCHART)),
                new ArrayList<>(),
                "",
                RoomType.MEETING_ROOM
        );

        Room testRoom3 = new Room(
                "TW_01.02",
                "1.OG",
                10,
                15,
                false,
                RoomState.FREE,
                new RoomEquipment(5, 4, Set.of(RoomInfrastructure.WHITEBOARD)),
                new ArrayList<>(),
                "",
                RoomType.MEETING_ROOM
        );

        // Setup test buildings
        BuildingInfo buildingInfo = new BuildingInfo(
                "ZL",
                "ZL (Lagerstrasse)",
                "Lagerstrasse 41",
                "8004",
                "Zurich",
                "IT"
        );


        BuildingInfo buildingInfo2 = new BuildingInfo("TW", "TW (Technikumstrasse)", "Technikumstrasse 9", "8400", "Winterthur", "Engineering");
        Building building = new Building(buildingInfo, new HashSet<>(Set.of("6.OG", "7.OG")), new ArrayList<>(List.of(testRoom1, testRoom2)));
        Building building2 = new Building(buildingInfo2, new HashSet<>(Set.of("1.OG")), new ArrayList<>(List.of(testRoom3)));
        testBuildings = List.of(building, building2);
        testRooms = List.of(testRoom1, testRoom2, testRoom3);

        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);
        // We don't need executor for the tests else it can lead to race conditions
        controller.shutdown();
    }

    // Constructor Tests
    @Test
    void testConstructor_ValidDependencies_Success() {
        assertNotNull(controller);
        verify(buildingStorageAPI, times(1)).getBuildings();
    }

    @Test
    void testConstructor_NullReservationAPI_ThrowsException() {
        assertThrows(NullPointerException.class, () ->
                new BuildingController(null, sensorAPI, buildingStorageAPI)
        );
    }

    @Test
    void testConstructor_NullSensorAPI_ThrowsException() {
        assertThrows(NullPointerException.class, () ->
                new BuildingController(reservationAPI, null, buildingStorageAPI)
        );
    }

    @Test
    void testConstructor_NullBuildingStorageAPI_ThrowsException() {
        assertThrows(NullPointerException.class, () ->
                new BuildingController(reservationAPI, sensorAPI, null)
        );
    }

    @Test
    void testConstructor_EmptyBuildingList_Success() {
        when(buildingStorageAPI.getBuildings()).thenReturn(Collections.emptyList());

        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        assertNotNull(controller);
        assertTrue(controller.getLocations().isEmpty());
    }

    // searchRooms Tests
    @Test
    void testSearchRooms_NullCriteria_ThrowsException() {
        assertThrows(NullPointerException.class, () ->
                controller.searchRoomsPaginated(null, PAGE_INDEX, PAGE_SIZE)
        );
    }

    @Test
    void testSearchRooms_EmptyCriteria_ReturnsAllRooms() {
        FilterCriteriaDTO criteria = new FilterCriteriaDTO("", null, null, null, null, 0, Set.of());
        PaginatedSearchResult paginatedResult = controller.searchRoomsPaginated(criteria, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(paginatedResult);
        int totalRooms = paginatedResult.searchResult().values().stream()
                .mapToInt(List::size)
                .sum();
        assertEquals(3, totalRooms);
    }

    /**
     * Test searching rooms with null values for city, building, and floor (simulating "All..." default options)
     * This verifies the fix for the issue where default filter options weren't showing all items
     */
    @Test
    void searchRoomsWithNullFiltersShowsAllRoomsTest() {
        // Test with null city, building, and floor (as passed when "Alle..." options are selected)
        FilterCriteriaDTO fc = new FilterCriteriaDTO("", null, null, null, null, 0, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null when filters are null");
        assertFalse(res.searchResult().isEmpty(), "Search result should not be empty when filters are null");
        assertEquals(testBuildings.size(), res.searchResult().size(),
                "All buildings should be included when city and building filters are null");

        long totalRoomsInResult = res.searchResult()
                .values()
                .stream()
                .mapToLong(Collection::size)
                .sum();
        assertEquals(testRooms.size(), totalRoomsInResult,
                "All rooms should be included when filters are null (default 'All' options)");
    }

    /**
     * Test that null city filter returns rooms from all locations
     */
    @Test
    void searchRoomsWithNullLocationShowsAllLocationsTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("", null, null, null, null, 0, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null");
        assertEquals(testBuildings.size(), res.searchResult().size(),
                "Should return buildings from all locations when city is null");

        Set<String> cities = res.searchResult().keySet().stream()
                .map(BuildingInfo::city)
                .collect(Collectors.toSet());
        assertTrue(cities.contains("Zurich"), "Results should include Zurich buildings");
        assertTrue(cities.contains("Winterthur"), "Results should include Winterthur buildings");
    }

    /**
     * Test that null building filter returns rooms from all buildings
     */
    @Test
    void searchRoomsWithNullBuildingShowsAllBuildingsTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("", null, null, null, null, 0, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null");
        assertEquals(testBuildings.size(), res.searchResult().size(),
                "Should return all buildings when building filter is null");

        Set<String> buildingShortNames = res.searchResult().keySet().stream()
                .map(BuildingInfo::shortName)
                .collect(Collectors.toSet());
        assertTrue(buildingShortNames.contains("ZL"), "Results should include ZL building");
        assertTrue(buildingShortNames.contains("TW"), "Results should include TW building");
    }

    /**
     * Test that null floor filter returns rooms from all floors
     */
    @Test
    void searchRoomsWithNullFloorShowsAllFloorsTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("", null, "ZL (Lagerstrasse)", null, null, 0, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null");
        assertFalse(res.searchResult().isEmpty(), "Search result should not be empty");

        List<Room> zlRooms = res.searchResult().values().iterator().next();
        Set<String> floors = zlRooms.stream()
                .map(Room::getFloor)
                .collect(Collectors.toSet());

        assertTrue(floors.size() > 1, "Should return rooms from multiple floors when floor filter is null");
        assertTrue(floors.contains("6.OG"), "Results should include rooms from 6.OG");
        assertTrue(floors.contains("7.OG"), "Results should include rooms from 7.OG");
    }

    /**
     * Test that null RoomType filter returns rooms of all types
     */
    @Test
    void searchRoomsWithNullRoomTypeShowsAllTypesTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("", null, null, null, null, 0, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null");

        Set<RoomType> roomTypes = res.searchResult().values().stream()
                .flatMap(List::stream)
                .map(Room::getRoomType)
                .collect(Collectors.toSet());

        assertTrue(roomTypes.contains(RoomType.CLASS_ROOM), "Results should include CLASS_ROOM types");
        assertTrue(roomTypes.contains(RoomType.MEETING_ROOM), "Results should include MEETING_ROOM types");
    }

    /**
     * Test searching rooms with a specific building short name
     */
    @Test
    void searchRoomsMatchingSearchTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("ZL", null, null, null, null, 0, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null when 'ZL' is searched");
        assertFalse(res.searchResult().isEmpty(), "Search result should not be empty when 'ZL' is searched");
        res.searchResult().forEach((key, value) -> assertEquals("ZL", key.shortName(), "Buildings other than ZL matched search 'ZL'"));

        Collection<List<Room>> matchedRooms = res.searchResult().values();
        for (List<Room> roomList : matchedRooms) {
            for (Room room : roomList) {
                assertTrue(room.getName().contains("ZL"), "Matched room " + room.getName() + " does not match search 'ZL'");
            }
        }

    }

    /**
     * Test if search is case-insensitive
     */
    @Test
    void searchRoomsCaseInsensitive() {
        FilterCriteriaDTO uppercase = new FilterCriteriaDTO("ZL", null, null, null, null, 0, Set.of());
        FilterCriteriaDTO lowercase = new FilterCriteriaDTO("zl", null, null, null, null, 0, Set.of());
        PaginatedSearchResult resUpper = controller.searchRoomsPaginated(uppercase, PAGE_INDEX, PAGE_SIZE);
        PaginatedSearchResult resLower = controller.searchRoomsPaginated(lowercase, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(resUpper, "Search result should not be null when 'ZL' is searched");
        assertNotNull(resLower, "Search result should not be null when 'zl' is searched");

        List<Room> matchedUppercaseRooms = resUpper.searchResult().values().iterator().next();
        List<Room> matchedLowercaseRooms = resLower.searchResult().values().iterator().next();

        assertEquals(matchedLowercaseRooms.size(), matchedUppercaseRooms.size(), "Number of matched rooms should be the same for 'ZL' and 'zl' searches");
        for (Room room : matchedUppercaseRooms) {
            assertTrue(matchedLowercaseRooms.contains(room), "Room " + room.getName() + " matched uppercase search but not lowercase search");
        }
    }

    /**
     * Test searching rooms with criteria that should match specific rooms
     */
    @Test
    void searchRoomsMatchingCriteriaTest() {


        FilterCriteriaDTO fc = new FilterCriteriaDTO("", "Winterthur", null, RoomType.MEETING_ROOM, null, 0, Set.of());
        PaginatedSearchResult res1 = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertFalse(res1.searchResult().isEmpty(), "Search result should not be empty for criteria --> " + fc);
        assertEquals(1, res1.searchResult().size(), "Exactly one search result should be returned for criteria --> " + fc);

        Room resultRoom1 = res1.searchResult().values().iterator().next().getFirst();
        assertEquals(RoomType.MEETING_ROOM, resultRoom1.getRoomType(), "Returned room does not match requested room type for criteria --> " + fc);
        assertEquals("TW_01.02", resultRoom1.getName(), "Returned room does not match expected room for criteria --> " + fc);


        FilterCriteriaDTO fc2 = new FilterCriteriaDTO("", null, null, RoomType.MEETING_ROOM, null, 12, Set.of(WHITEBOARD));
        PaginatedSearchResult res2 = controller.searchRoomsPaginated(fc2, PAGE_INDEX, PAGE_SIZE);

        assertFalse(res2.searchResult().isEmpty(), "Search result should not be empty for criteria --> " + fc2);
        assertEquals(2, res2.searchResult().size(), "Exactly two search result should be returned for criteria --> " + fc2);

        Room resultRoom2 = res2.searchResult().values().iterator().next().getFirst();
        assertEquals(RoomType.MEETING_ROOM, resultRoom2.getRoomType(), "Returned room does not match requested room type for criteria --> " + fc2);
        assertTrue(resultRoom2.getEquipment().roomInfrastructure().contains(WHITEBOARD), "Returned room does not have required infrastructure for criteria --> " + fc2);
        assertTrue(resultRoom2.getSeats() >= 12, "Returned room does not have required capacity for criteria --> " + fc2);


        FilterCriteriaDTO fc3 = new FilterCriteriaDTO("", null, "ZL (Lagerstrasse)", RoomType.CLASS_ROOM, "6.OG", 0, Set.of());
        PaginatedSearchResult res3 = controller.searchRoomsPaginated(fc3, PAGE_INDEX, PAGE_SIZE);

        assertFalse(res3.searchResult().isEmpty(), "Search result should not be empty for criteria --> " + fc3);
        assertEquals(1, res3.searchResult().size(), "Exactly one search result should be returned for criteria --> " + fc3);

        Room resultRoom3 = res3.searchResult().values().iterator().next().getFirst();
        assertEquals(RoomType.CLASS_ROOM, resultRoom3.getRoomType(), "Returned room does not match requested room type for criteria --> " + fc3);
        assertEquals("6.OG", resultRoom3.getFloor(), "Returned room is not in the requested floor for criteria --> " + fc3);
        res3.searchResult().forEach((key, value) -> assertEquals("ZL", key.shortName(), "Returned building does not match requested building for criteria --> " + fc3));
    }

    /**
     * Test searching rooms with matching criteria and matching search string
     */
    @Test
    void searchRoomsMatchingCriteriaAndSearchStringTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("ZL", null, null, RoomType.MEETING_ROOM, null, 8, Set.of(FLIPCHART));
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null for combined matching search and criteria");
        assertFalse(res.searchResult().isEmpty(), "Search result should not be empty for combined matching search and criteria -->" + fc);
        res.searchResult().forEach((bi, roomList) -> {
            assertEquals("ZL", bi.shortName(), "Only ZL buildings should match the 'ZL' search string -->" + fc);
            for (Room room : roomList) {
                assertTrue(room.getName().contains("ZL"), "Room name should contain 'ZL'");
                assertEquals(RoomType.MEETING_ROOM, room.getRoomType(), "Room type should match criteria -->" + fc);
                assertTrue(room.getSeats() >= 8, "Room capacity should match or exceed requested capacity --> " + fc);
                assertTrue(room.getEquipment().roomInfrastructure().contains(FLIPCHART), "Room should contain required infrastructure --> " + fc);
            }
        });
    }

    /**
     * Test searching rooms with a not matching search string
     */
    @Test
    void searchRoomsNotMatchingSearchTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("abc123", null, null, null, null, 0, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null even if no rooms match the search string");
        assertTrue(res.searchResult().isEmpty(), "Search result should be empty if search string matches no rooms");
    }

    /**
     * Test searching rooms with criteria that match no rooms
     */
    @Test
    void searchRoomsNotMatchingCriteriaTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("", null, null, RoomType.MEETING_ROOM, null, 100, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null even if criteria match no rooms");
        assertTrue(res.searchResult().isEmpty(), "Search result should be empty if criteria match no rooms -->" + fc);
    }

    /**
     * Test searching rooms with matching criteria but not matching search string
     */
    @Test
    void searchRoomsMatchingCriteriaNotMatchingSearchTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("abc123", "Winterthur", null, RoomType.CLASS_ROOM, null, 0, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null for mixed matching non-matching inputs");
        assertTrue(res.searchResult().isEmpty(), "Search result should be empty if search string does not match, regardless of criteria");
    }

    /**
     * Test searching rooms with matching search string but not matching criteria
     */
    @Test
    void searchRoomsMatchingSearchNotMatchingCriteriaTest() {
        FilterCriteriaDTO fc = new FilterCriteriaDTO("ZL", null, null, RoomType.MEETING_ROOM, null, 999, Set.of());
        PaginatedSearchResult res = controller.searchRoomsPaginated(fc, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res, "Search result should not be null for mixed matching non-matching inputs");
        assertTrue(res.searchResult().isEmpty(), "Search result should be empty if criteria do not match, even if search string matches");
    }

    /**
     * Test searching rooms with null criteria
     */
    @Test
    void searchRoomsNullCriteriaTest() {
        assertThrows(NullPointerException.class, () -> controller.searchRoomsPaginated(null, PAGE_INDEX, PAGE_SIZE), "Null filter criteria should throw NullPointerException");
    }

    @Test
    void searchRoomsWithNullSearchQueryBehavesLikeEmptyQuery() {
        // null vs. "" triggers different branches in Search.isBlank(...)
        FilterCriteriaDTO emptyQueryCriteria =
                new FilterCriteriaDTO("", null, null, null, null, 0, Set.of());
        FilterCriteriaDTO nullQueryCriteria =
                new FilterCriteriaDTO(null, null, null, null, null, 0, Set.of());

        PaginatedSearchResult resultEmpty =
                controller.searchRoomsPaginated(emptyQueryCriteria, PAGE_INDEX, PAGE_SIZE);
        PaginatedSearchResult resultNull =
                controller.searchRoomsPaginated(nullQueryCriteria, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(resultEmpty);
        assertNotNull(resultNull);

        assertEquals(resultEmpty.totalResults(), resultNull.totalResults(),
                "Null and empty search string should yield the same number of results");
        assertEquals(resultEmpty.searchResult(), resultNull.searchResult(),
                "Null and empty search string should return the same rooms");
    }

    @Test
    void searchRoomsWithWhitespaceQueryBehavesLikeEmptyQuery() {
        // only whitespace — will be treated as blank by Search.isBlank(...)
        FilterCriteriaDTO emptyCriteria =
                new FilterCriteriaDTO("", null, null, null, null, 0, Set.of());
        FilterCriteriaDTO whitespaceCriteria =
                new FilterCriteriaDTO("   ", null, null, null, null, 0, Set.of());

        PaginatedSearchResult resultEmpty =
                controller.searchRoomsPaginated(emptyCriteria, PAGE_INDEX, PAGE_SIZE);
        PaginatedSearchResult resultWhitespace =
                controller.searchRoomsPaginated(whitespaceCriteria, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(resultEmpty);
        assertNotNull(resultWhitespace);

        assertEquals(resultEmpty.totalResults(), resultWhitespace.totalResults(),
                "Whitespace query should be treated like an empty query");
        assertEquals(resultEmpty.searchResult(), resultWhitespace.searchResult(),
                "Whitespace query should return the same rooms as an empty query");
    }

    @Test
    void searchRoomsWithQueryAndCityIsSubsetOfQueryOnly() {
        // Search string + additional filter -> triggers Search.containsCriteriaNoQuery(criteria) == true
        Building buildingWithRooms = testBuildings.stream()
                .filter(b -> !b.rooms().isEmpty())
                .findFirst()
                .orElseThrow();

        Room exampleRoom = buildingWithRooms.rooms().getFirst();
        String query = exampleRoom.getName();
        String city = buildingWithRooms.buildingInfo().city();

        FilterCriteriaDTO queryOnlyCriteria =
                new FilterCriteriaDTO(query, null, null, null, null, 0, Set.of());
        FilterCriteriaDTO queryAndCityCriteria =
                new FilterCriteriaDTO(query, city, null, null, null, 0, Set.of());

        PaginatedSearchResult resultQueryOnly =
                controller.searchRoomsPaginated(queryOnlyCriteria, PAGE_INDEX, PAGE_SIZE);
        PaginatedSearchResult resultQueryAndCity =
                controller.searchRoomsPaginated(queryAndCityCriteria, PAGE_INDEX, PAGE_SIZE);

        Set<Room> roomsQueryOnly = resultQueryOnly.searchResult().values().stream()
                .flatMap(List::stream)
                .collect(java.util.stream.Collectors.toSet());

        Set<Room> roomsQueryAndCity = resultQueryAndCity.searchResult().values().stream()
                .flatMap(List::stream)
                .collect(java.util.stream.Collectors.toSet());

        assertFalse(roomsQueryAndCity.isEmpty(),
                "Query + City should return at least one room");
        assertTrue(roomsQueryOnly.containsAll(roomsQueryAndCity),
                "Rooms for (Query + City) must be a subset of rooms for (Query only)");
    }

    @Test
    void searchRoomsWithInfrastructureFilterReturnsOnlyMatchingRooms() {
        // Only infrastructure filter -> triggers Search.containsCriteria(...) in Building/Room matching
        Room roomWithInfra = testBuildings.stream()
                .flatMap(b -> b.rooms().stream())
                .filter(r -> !r.getEquipment().roomInfrastructure().isEmpty())
                .findFirst()
                .orElseThrow();

        RoomInfrastructure infra = roomWithInfra.getEquipment()
                .roomInfrastructure()
                .iterator()
                .next();

        FilterCriteriaDTO criteria =
                new FilterCriteriaDTO("", null, null, null, null, 0, Set.of(infra));

        PaginatedSearchResult result =
                controller.searchRoomsPaginated(criteria, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(result);
        assertFalse(result.searchResult().isEmpty(),
                "Infrastructure filter should return matching rooms");

        result.searchResult().values().stream()
                .flatMap(List::stream)
                .forEach(room -> assertTrue(
                        room.getEquipment().roomInfrastructure().contains(infra),
                        "Each returned room must have the required infrastructure"
                ));
    }

    @Test
    void searchRoomsMatchingEventTitleTest() {
        // Create a future event with a title that should be searchable
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        Event event = new Event(
                "Java Kickoff",
                "Description",
                start,
                end,
                List.of("lecture", "java")
        );
        testRoom1.addEvent(event);

        FilterCriteriaDTO criteria =
                new FilterCriteriaDTO("kickoff", null, null, null, null, 0, Set.of());

        PaginatedSearchResult res =
                controller.searchRoomsPaginated(criteria, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res);
        assertFalse(res.searchResult().isEmpty(), "Event title should trigger a match");

        List<Room> matchedRooms = res.searchResult()
                .values()
                .stream()
                .flatMap(List::stream)
                .toList();

        assertTrue(matchedRooms.contains(testRoom1),
                "Room containing matching event title should be included");
    }

    @Test
    void searchRoomsMatchingEventDescriptionTest() {
        // Create an event that matches only through its description
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        Event event = new Event(
                "Weekly Meeting",
                "Discussion about Kotlin",
                start,
                end,
                List.of("meeting")
        );
        testRoom1.addEvent(event);

        FilterCriteriaDTO criteria =
                new FilterCriteriaDTO("kotlin", null, null, null, null, 0, Set.of());

        PaginatedSearchResult res =
                controller.searchRoomsPaginated(criteria, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res);
        assertFalse(res.searchResult().isEmpty(), "Event description should trigger a match");

        List<Room> matchedRooms = res.searchResult()
                .values()
                .stream()
                .flatMap(List::stream)
                .toList();

        assertTrue(matchedRooms.contains(testRoom1),
                "Room containing matching event description should be included");
    }

    @Test
    void searchRoomsMatchingEventTagsTest() {
        // Create event where only the tags match the search query
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        Event event = new Event(
                "Tech Talk",
                "General discussion",
                start,
                end,
                List.of("Backend", "SPRINT_REVIEW")
        );
        testRoom1.addEvent(event);

        // Query is found only in tags
        FilterCriteriaDTO criteria =
                new FilterCriteriaDTO("sprint", null, null, null, null, 0, Set.of());

        PaginatedSearchResult res =
                controller.searchRoomsPaginated(criteria, PAGE_INDEX, PAGE_SIZE);

        assertNotNull(res);
        assertFalse(res.searchResult().isEmpty(), "Event tags should trigger a match");

        List<Room> matchedRooms = res.searchResult()
                .values()
                .stream()
                .flatMap(List::stream)
                .toList();

        assertTrue(matchedRooms.contains(testRoom1),
                "Room containing matching event tags should be included");
    }



    // getLocations Tests
    @Test
    void testGetLocations_MultipleDistinctLocations_ReturnsUniqueSet() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        Set<String> locations = controller.getLocations();

        assertNotNull(locations);
        assertEquals(2, locations.size());
        assertTrue(locations.contains("Zurich"));
        assertTrue(locations.contains("Winterthur"));
    }

    @Test
    void testGetLocations_NoBuildingsLoaded_ReturnsEmptySet() {
        when(buildingStorageAPI.getBuildings()).thenReturn(Collections.emptyList());
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        Set<String> locations = controller.getLocations();

        assertNotNull(locations);
        assertTrue(locations.isEmpty());
    }

    // getBuildingNames Tests
    @Test
    void testGetBuildingNames_MultipleDistinctNames_ReturnsUniqueSet() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        Set<String> names = controller.getBuildingNames();

        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("ZL (Lagerstrasse)"));
        assertTrue(names.contains("TW (Technikumstrasse)"));
    }

    @Test
    void testGetBuildingNames_NoBuildingsLoaded_ReturnsEmptySet() {
        when(buildingStorageAPI.getBuildings()).thenReturn(Collections.emptyList());
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        Set<String> names = controller.getBuildingNames();

        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

    // getBuildingByName Tests
    @Test
    void testGetBuildingByName_ExistingName_ReturnsBuilding() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        Optional<Building> result = controller.getBuildingByName("ZL (Lagerstrasse)");

        assertTrue(result.isPresent());
        assertEquals("ZL (Lagerstrasse)", result.get().buildingInfo().fullName());
    }

    @Test
    void testGetBuildingByName_NonExistingName_ReturnsEmpty() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        Optional<Building> result = controller.getBuildingByName("NonExistent");

        assertFalse(result.isPresent());
    }

    @Test
    void testGetBuildingByName_NullName_ThrowsException() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        assertThrows(NullPointerException.class, () ->
                controller.getBuildingByName(null)
        );
    }

    // performUpdate() Tests
    @Test
    void testRun_WithReservationsAndOccupancy_UpdatesRoomStatus() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        LocalDateTime now = LocalDateTime.now();
        TimeWindow timeWindow = new TimeWindow(now.minusHours(1), now.plusHours(1));
        RoomReservation reservation = new RoomReservation("ZL_06.01", List.of(timeWindow));
        when(reservationAPI.getReservations()).thenReturn(List.of(reservation));
        when(sensorAPI.getOccupancy("ZL_06.01")).thenReturn(Optional.of(new RoomOccupancy("ZL_06.01", 10)));

        controller.performUpdate();

        assertEquals(RoomState.RESERVED, testRoom1.getStatus());
        assertEquals(10, testRoom1.getOccupancy());
    }

    @Test
    void testRun_NoReservations_UpdatesOccupancyOnly() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        when(reservationAPI.getReservations()).thenReturn(Collections.emptyList());
        when(sensorAPI.getOccupancy(anyString())).thenReturn(Optional.of(new RoomOccupancy("ZL_06.01", 5)));

        controller.performUpdate();

        assertNotEquals(RoomState.RESERVED, testRoom1.getStatus());
    }

    @Test
    void testRun_ZeroOccupancy_SetsFreeStatus() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        when(reservationAPI.getReservations()).thenReturn(Collections.emptyList());
        when(sensorAPI.getOccupancy(anyString())).thenReturn(Optional.of(new RoomOccupancy("ZL_06.01", 0)));

        controller.performUpdate();

        assertEquals(RoomState.FREE, testRoom1.getStatus());
    }

    @Test
    void testRun_Event_SetsEventStatus() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        Event ev = mock(Event.class);
        when(ev.endDate()).thenReturn(LocalDateTime.now().plusHours(1));
        testRoom1.addEvent(ev);

        when(reservationAPI.getReservations()).thenReturn(Collections.emptyList());
        when(sensorAPI.getOccupancy(anyString())).thenReturn(Optional.of(new RoomOccupancy("ZL_06.01", 0)));

        controller.performUpdate();

        assertEquals(RoomState.EVENT, testRoom1.getStatus());
    }

    @Test
    void testRun_FullRoom_SetsFullStatus() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        when(reservationAPI.getReservations()).thenReturn(Collections.emptyList());
        // Same amount of people as seats in room
        when(sensorAPI.getOccupancy(anyString())).thenReturn(Optional.of(new RoomOccupancy("ZL_06.01", 30)));

        controller.performUpdate();

        assertEquals(RoomState.FULL, testRoom1.getStatus());
    }

    @Test
    void testRun_removesPastEventsButKeepsFutureEvents() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        LocalDateTime now = LocalDateTime.now();

        Event pastEvent = new Event(
                "Past Event",
                "Already finished",
                now.minusHours(2),
                now.minusHours(1),
                List.of("old")
        );

        Event futureEvent = new Event(
                "Future Event",
                "Happens later",
                now.plusMinutes(10),
                now.plusHours(1),
                List.of("future")
        );

        testRoom1.addEvent(pastEvent);
        testRoom1.addEvent(futureEvent);

        when(reservationAPI.getReservations()).thenReturn(Collections.emptyList());
        when(sensorAPI.getOccupancy(anyString())).thenReturn(Optional.empty());

        controller.performUpdate();

        List<Event> remainingEvents = testRoom1.getCurrentEvents();

        assertFalse(remainingEvents.contains(pastEvent),
                "Past events must be removed by performUpdate()");
        assertTrue(remainingEvents.contains(futureEvent),
                "Future events must not be removed by performUpdate()");
    }

    @Test
    void testRun_reservationOverridesEventStatus() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        LocalDateTime now = LocalDateTime.now();

        Event futureEvent = new Event(
                "Talk",
                "A normal event",
                now.plusMinutes(10),
                now.plusHours(1),
                List.of("talk")
        );
        testRoom1.addEvent(futureEvent);

        // Reservation overlaps with the event -> RESERVED must override EVENT
        TimeWindow window = new TimeWindow(now.minusMinutes(30), now.plusMinutes(30));
        RoomReservation reservation =
                new RoomReservation(testRoom1.getName(), List.of(window));

        when(reservationAPI.getReservations()).thenReturn(List.of(reservation));
        when(sensorAPI.getOccupancy(anyString()))
                .thenReturn(Optional.of(new RoomOccupancy(testRoom1.getName(), 0)));

        controller.performUpdate();

        assertEquals(RoomState.RESERVED, testRoom1.getStatus(),
                "Reservation must override event status in updateRoomState()");
    }

    // addObserver Tests
    @Test
    void testAddObserver_NullObserver_ThrowsException() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);
        assertThrows(NullPointerException.class, () -> controller.addObserver(null));
    }

    @Test
    void testAddObserverValidObserver() {
        when(buildingStorageAPI.getBuildings()).thenReturn(testBuildings);
        controller = new BuildingController(reservationAPI, sensorAPI, buildingStorageAPI);

        BuildingObserver observer = mock(BuildingObserver.class);
        controller.addObserver(observer);
        controller.performUpdate();
        verify(observer).notifyBuildingUpdate();
    }

}
