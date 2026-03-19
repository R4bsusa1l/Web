# Test Documentation for Class `BuildingController.java`

## 1. Introduction

The `BuildingController` class manages room searches, location retrievals,
and scheduled updates of room occupancy and reservation status.
It interacts with three main components:

- **ReservationAPI**: Provides reservation data.
- **SensorAPI**: Provides real-time occupancy readings.
- **BuildingStorageAPI**: Supplies persisted building and room structures.

Upon instantiation, the controller loads all buildings into memory and schedules a background task (`run()`)
to periodically refresh occupancy and reservation information for every room.

## 2. Test Strategy

- **Test Type:** Unit Tests using JUnit Jupiter and Mockito
- **Mock Dependencies:** `ReservationAPI`, `SensorAPI`, `BuildingStorageAPI`
- **Test Goals:** Cover all public methods, including normal flows, error handling, and edge cases.

## 3. Test Scope

### 3.1 Methods to Test

| Method                                                                                           | Description                                                                 |
|--------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `BuildingController(ReservationAPI, SensorAPI, BuildingStorageAPI)`                              | Constructor: loads buildings and starts the scheduled background task       |
| `PaginatedSearchResult searchRoomsPaginated(FilterCriteriaDTO criteria, int page, int pageSize)` | Filters and returns rooms matching the provided criteria                    |
| `Set<String> getLocations()`                                                                     | Returns all unique building locations                                       |
| `Set<String> getBuildingNames()`                                                                 | Returns all unique building names                                           |
| `Optional<Building> getBuildingByName(String name)`                                              | Retrieves a building by exact name                                          |
| `public void performUpdate()`                                                                    | Background task that updates reservation status and occupancy for all rooms |
| `public void addObserver(BuildingObserver observer)`                                             | Adds an observer                                                            |

### 3.2 Equivalence Classes & Test Cases

#### Constructor

| Equivalence Class         | Input                                                        | Expected Outcome                                           |
|---------------------------|--------------------------------------------------------------|------------------------------------------------------------|
| Valid dependencies        | Non-null `ReservationAPI`, `SensorAPI`, `BuildingStorageAPI` | Instance created; buildings list populated; task scheduled |
| Null `ReservationAPI`     | `null`                                                       | Throws `NullPointerException`                              |
| Null `SensorAPI`          | `null`                                                       | Throws `NullPointerException`                              |
| Null `BuildingStorageAPI` | `null`                                                       | Throws `NullPointerException`                              |
| Empty building list       | `getBuildings()` returns empty list                          | Instance created; `buildings` remains empty                |

#### `searchRoomsPaginated(FilterCriteriaDTO, int page, int pageSize)`

| Equivalence Class                                | Input                                                       | Expected Outcome                                                          |
|--------------------------------------------------|-------------------------------------------------------------|---------------------------------------------------------------------------|
| no criteria set                                  | empty search string, default values in criteria             | PaginatedSearchResult returned with all Rooms                             |
| matching search string                           | search string "ZL", all other criteria not set              | PaginatedSearchResult returned with all rooms with "ZL" in the name       |
| matching case insensitive search string          | search string "zl", all other criteria not set              | PaginatedSearchResult returned with same rooms as above                   |
| matching criteria                                | criteria that match at least 1 room                         | PaginatedSearchResult returned with matching Rooms                        |
| matching criteria and search string              | search string "ZL, criteria that match at least 1 room      | PaginatedSearchResult returned with matching Rooms and "ZL" in the name   |
| not matching search string                       | search string "abc123", all other criteria not set          | empty PaginatedSearchResult returned                                      |
| not matching criteria                            | criteria that match no rooms                                | empty PaginatedSearchResult returned                                      |
| matching criteria and not matching search string | search string "abc123", criteria that match at least 1 room | empty PaginatedSearchResult returned                                      |
| matching search string and not matching criteria | search string "ZL", criteria that match no rooms            | empty PaginatedSearchResult returned                                      |
| empty building list                              | empty list, any criteria                                    | empty PaginatedSearchResult returned                                      |
| null list                                        | `null`,valid FilterCriteria                                 | `NullPointerException` thrown                                             |
| null criteria                                    | valid list,`null`                                           | `NullPointerException` thrown                                             |
| null search query                                | criteria with `searchQuery=null`                            | behaves like empty query → all rooms considered before other filters      |
| blank/whitespace search query                    | criteria with `searchQuery` whitespace only                 | behaves like empty query                                                  |
| location not set                                 | `location=null` in criteria                                 | all locations considered (no filtering by location)                       |
| building name not set                            | `buildingName=null` in criteria                             | all building names considered                                             |
| floor not set                                    | `floor=null` in criteria                                    | all floors considered                                                     |
| room type not set                                | `roomType=null` in criteria                                 | all room types considered                                                 |
| infrastructure filter applied                    | infrastructure includes e.g. `WHITEBOARD`, `FLIPCHART`      | only rooms containing all requested infrastructure are returned           |
| query matches event title                        | search query contained in any current event title           | room is included                                                          |
| query matches event description                  | search query contained in any current event description     | room is included                                                          |
| query matches event tags                         | search query contained in any current event tag             | room is included                                                          |
| query contains city substring only               | query that contains city while other filters are empty      | behaves based on name/location rules; city-only subset doesn’t overfilter |

#### `getLocations()`

| Equivalence Class           | Input                                      | Expected Outcome                    |
|-----------------------------|--------------------------------------------|-------------------------------------|
| Multiple distinct locations | Buildings with different `location` values | Returns set of all unique locations |
| Duplicate locations         | Two buildings share the same `location`    | Each location appears only once     |
| No buildings loaded         | Empty `buildings` list                     | Returns empty set                   |

#### `getBuildingNames()`

| Equivalence Class       | Input                             | Expected Outcome                |
|-------------------------|-----------------------------------|---------------------------------|
| Multiple distinct names | Buildings with different names    | Returns set of all unique names |
| Duplicate names         | Two buildings share the same name | Each name appears only once     |
| No buildings loaded     | Empty `buildings` list            | Returns empty set               |

#### `getBuildingByName(String)`

| Equivalence Class | Input                        | Expected Outcome                |
|-------------------|------------------------------|---------------------------------|
| Existing name     | Exact existing building name | Returns `Optional.of(building)` |
| Non-existing name | Name not present             | Returns `Optional.empty()`      |
| Null name         | `null`                       | Throws `NullPointerException`   |

#### `performUpdate()`

Normal operation: call `performUpdate()` directly in tests to simulate scheduled execution.

| Equivalence Class                  | Setup / Input                                                                    | Expected Outcome                                                  |
|------------------------------------|----------------------------------------------------------------------------------|-------------------------------------------------------------------|
| Reservations and occupancy present | `getReservations()` returns list of room IDs <br> `getOccupancy(id)` returns > 0 | Reserved rooms have status `RESERVED`; occupancy counters updated |
| No reservations                    | `getReservations()` returns empty list                                           | No rooms marked `RESERVED`; occupancy still updated               |
| Zero occupancy reported            | `getOccupancy(id)` returns `0`                                                   | Rooms remain or become `FREE`                                     |
| Sensor error                       | `getOccupancy(id)` throws `RuntimeException`                                     | Exception is propagated (matches current behavior)                |
| Room has ongoing event             | Room has current event during update                                             | Room status set to `EVENT`                                        |
| Room occupancy equals seats        | `peopleInRoom == seats`                                                          | Room status set to `FULL`                                         |
| Past events removed                | Room has past and future events                                                  | Past events removed; future events kept                           |
| Reservation overrides event status | Room has both reservation and current event                                      | Room status `RESERVED` takes precedence over `EVENT`              |

#### `addObserver(BuildingObserver)`

| Equivalence Class     | Input  | Expected Outcome              |
|-----------------------|--------|-------------------------------|
| Null BuildingObserver | `null` | Throws `NullPointerException` |
| Valid observer        | valid  | Observer added without error  |

## 4. Test Coverage

Test coverage was last updated on 07.12.2025.

- Method Coverage: `100%`
- Branch Coverage: `100%`
- Line Coverage: `100%`

## 5. Conclusion

This test plan ensures comprehensive unit test coverage for `BuildingController.java`,
documenting normal flows, error handling, and edge cases.

