# Test Documentation for Class `Room.java`

## 1. Introduction

The class `Room` represents a physical room in a building with properties such as capacity, equipment, and current
events.

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                                  | Description                                                |
|-----------------------------------------|------------------------------------------------------------|
| `Room(String name, int floor, ...)`     | constructor of the class                                   |
| `getName()`                             | returns the name of the room                               |
| `setName(String name)`                  | sets the name of the room                                  |
| `getFloor()`                            | returns the floor number                                   |
| `setFloor(String floor)`                | sets the floor number                                      |
| `getOccupancy()`                        | returns current occupancy count                            |
| `setOccupancy(int occupancy)`           | sets and validates the occupancy count                     |
| `getSeats()`                            | returns the number of seats                                |
| `setSeats(int seats)`                   | sets and validates the number of seats                     |
| `isAccessible()`                        | returns whether the room is accessible                     |
| `setAccessible(boolean accessible)`     | sets the accessibility status                              |
| `getStatus()`                           | returns the room status                                    |
| `setStatus(RoomState status)`           | sets the room status                                       |
| `getEquipment()`                        | returns the room equipment                                 |
| `setEquipment(RoomEquipment equipment)` | sets the room equipment                                    |
| `getCurrentEvents()`                    | returns unmodifiable list of current events                |
| `addEvent(Event event)`                 | adds an event to the room                                  |
| `removeEvent(Event event)`              | removes an event from the room                             |
| `getDescription()`                      | returns the description for the room                       |
| `setDescription(String description)`    | sets the description for the room                          |
| `getRoomType()`                         | returns the room type/classification                       |
| `matchesCriteria(FilterCriteriaDTO)`    | evaluates whether the room matches the provided criteria   |
| `equals(Object obj)`                    | compares two Room objects for equality based on all fields |
| `hashCode()`                            | returns hash code based on all fields                      |
| `toString()`                            | returns string representation of the room                  |

### 3.2 Equivalence Classes & Tests

#### Method:

`Room(String name, int floor, int occupancy, int seats, boolean accessible, RoomState status, RoomEquipment equipment, List<Event> currentEvents, String description)`

| Equivalence Class   | Inputs                    | Expected Output                                  |
|---------------------|---------------------------|--------------------------------------------------|
| valid fields        | all valid non-null values | `Room` created with all fields set               |
| null name           | `null` name               | `NullPointerException` thrown                    |
| empty name          | `""` name                 | `IllegalArgumentException` thrown                |
| too long name       | name > 50 characters      | `IllegalArgumentException` thrown                |
| negative occupancy  | occupancy < 0             | `IllegalArgumentException` thrown                |
| negative seats      | seats < 0                 | `IllegalArgumentException` thrown                |
| seats exceeds limit | seats > 5000              | `IllegalArgumentException` thrown                |
| null status         | `null` status             | `NullPointerException` thrown                    |
| null equipment      | `null` equipment          | `NullPointerException` thrown                    |
| null currentEvents  | `null` currentEvents      | `NullPointerException` thrown                    |
| null description    | `null` description        | `NullPointerException` thrown                    |
| empty description   | `""` description          | `Room` created, description returns empty string |

#### Method: `setName(String name)`

| Equivalence Class | Inputs          | Expected Output                   |
|-------------------|-----------------|-----------------------------------|
| valid name        | `"ZL 06.10"`    | name updated                      |
| empty name        | `""`            | `IllegalArgumentException` thrown |
| null name         | `null`          | `NullPointerException` thrown     |
| too long name     | name > 50 chars | `IllegalArgumentException` thrown |

#### Method: `getName()`

| Equivalence Class | Inputs  | Expected Output            |
|-------------------|---------|----------------------------|
| name returned     | nothing | returns current name value |

#### Method: `setFloor(String floor)`

| Equivalence Class | Inputs | Expected Output                   |
|-------------------|--------|-----------------------------------|
| valid floor       | `5 OG` | floor updated                     |
| ground floor      | `EG`   | floor updated                     |
| null floor        | null   | `NullPointerException` thrown     |
| empty floor       | " "    | `IllegalArgumentException` thrown |

#### Method: `getFloor()`

| Equivalence Class | Inputs  | Expected Output             |
|-------------------|---------|-----------------------------|
| floor returned    | nothing | returns current floor value |

#### Method: `setOccupancy(int occupancy)`

| Equivalence Class  | Inputs | Expected Output                   |
|--------------------|--------|-----------------------------------|
| valid occupancy    | `10`   | occupancy updated                 |
| zero occupancy     | `0`    | occupancy updated                 |
| negative occupancy | `-1`   | `IllegalArgumentException` thrown |

#### Method: `getOccupancy()`

| Equivalence Class  | Inputs  | Expected Output                 |
|--------------------|---------|---------------------------------|
| occupancy returned | nothing | returns current occupancy value |

#### Method: `setSeats(int seats)`

| Equivalence Class   | Inputs | Expected Output                   |
|---------------------|--------|-----------------------------------|
| valid seats         | `50`   | seats updated                     |
| minimum seats       | `0`    | seats updated                     |
| negative seats      | `-1`   | `IllegalArgumentException` thrown |
| seats exceeds limit | `5001` | `IllegalArgumentException` thrown |

#### Method: `getSeats()`

| Equivalence Class | Inputs  | Expected Output             |
|-------------------|---------|-----------------------------|
| seats returned    | nothing | returns current seats value |

#### Method: `setAccessible(boolean accessible)`

| Equivalence Class | Inputs  | Expected Output    |
|-------------------|---------|--------------------|
| set true          | `true`  | accessible updated |
| set false         | `false` | accessible updated |

#### Method: `isAccessible()`

| Equivalence Class   | Inputs  | Expected Output                  |
|---------------------|---------|----------------------------------|
| accessible returned | nothing | returns current accessible value |

#### Method: `setStatus(RoomState status)`

| Equivalence Class | Inputs                | Expected Output               |
|-------------------|-----------------------|-------------------------------|
| valid status      | `RoomState.AVAILABLE` | status updated                |
| null status       | `null`                | `NullPointerException` thrown |

#### Method: `getStatus()`

| Equivalence Class | Inputs  | Expected Output              |
|-------------------|---------|------------------------------|
| status returned   | nothing | returns current status value |

#### Method: `setEquipment(RoomEquipment equipment)`

| Equivalence Class | Inputs                 | Expected Output               |
|-------------------|------------------------|-------------------------------|
| valid equipment   | `RoomEquipment` object | equipment updated             |
| null equipment    | `null`                 | `NullPointerException` thrown |

#### Method: `getEquipment()`

| Equivalence Class  | Inputs  | Expected Output                 |
|--------------------|---------|---------------------------------|
| equipment returned | nothing | returns current equipment value |

#### Method: `getCurrentEvents()`

| Equivalence Class    | Inputs                          | Expected Output                             |
|----------------------|---------------------------------|---------------------------------------------|
| events list returned | nothing                         | returns unmodifiable list of current events |
| unmodifiable list    | attempt to modify returned list | `UnsupportedOperationException` thrown      |

#### Method: `addEvent(Event event)`

| Equivalence Class | Inputs                                    | Expected Output               |
|-------------------|-------------------------------------------|-------------------------------|
| valid event       | `Event` object                            | event added to list           |
| null event        | `null`                                    | `NullPointerException` thrown |
| duplicate event   | same event twice                          | event rejected                |
| overlapping event | event in same timeframe as existing event | event rejected                |

#### Method: `removeEvent(Event event)`

| Equivalence Class  | Inputs            | Expected Output               |
|--------------------|-------------------|-------------------------------|
| existing event     | `Event` object    | event removed from list       |
| null event         | `null`            | `NullPointerException` thrown |
| non-existing event | event not in list | no change, no exception       |

#### Method: `matchesCriteria(FilterCriteriaDTO criteria)`

| Equivalence Class                       | Inputs                                          | Expected Output               |
|-----------------------------------------|-------------------------------------------------|-------------------------------|
| null criteria                           | `null`                                          | `NullPointerException` thrown |
| blank/whitespace query, no requirements | `searchQuery` blank, no other requirements set  | returns `true`                |
| criteria met                            | required capacity ≤ seats, name/query matches   | returns `true`                |
| required capacity too high              | required capacity > seats                       | returns `false`               |
| query does not match                    | query not found in name/description/tags/events | returns `false`               |

#### Method: `setDescription(String description)`

| Equivalence Class | Inputs               | Expected Output                     |
|-------------------|----------------------|-------------------------------------|
| valid description | `"Description text"` | description updated                 |
| empty description | `""`                 | description updated to empty string |
| null description  | `null`               | `NullPointerException` thrown       |

#### Method: `getDescription()`

| Equivalence Class    | Inputs  | Expected Output                   |
|----------------------|---------|-----------------------------------|
| description returned | nothing | returns current description value |

#### Method: `equals(Object object)`

| Equivalence Class     | Inputs                                 | Expected Output |
|-----------------------|----------------------------------------|-----------------|
| same reference        | `room.equals(room)`                    | `true`          |
| equal fields          | two `Room` with identical field values | `true`          |
| different name        | only name differs                      | `false`         |
| different floor       | only floor differs                     | `false`         |
| different occupancy   | only occupancy differs                 | `false`         |
| different seats       | only seats differs                     | `false`         |
| different accessible  | only accessible differs                | `false`         |
| different status      | only status differs                    | `false`         |
| different equipment   | only equipment differs                 | `false`         |
| different events      | only currentEvents differs             | `false`         |
| different description | only description differs               | `false`         |
| null comparison       | `room.equals(null)`                    | `false`         |
| different class       | `room.equals(someNonRoomObject)`       | `false`         |

#### Method: `hashCode()`

| Equivalence Class       | Inputs                                 | Expected Output                     |
|-------------------------|----------------------------------------|-------------------------------------|
| equal objects same hash | two `Room` with identical field values | hash codes are equal                |
| consistent with equals  | use in a `HashSet`                     | set contains equal object after add |
| consistent calls        | multiple calls on same object          | returns same hash code              |

#### Method: `toString()`

| Equivalence Class   | Inputs               | Expected Output                                                                                          |
|---------------------|----------------------|----------------------------------------------------------------------------------------------------------|
| contains all fields | any populated `Room` | string contains name, floor, occupancy, seats, accessible, status, equipment, currentEvents, description |
| not null string     | any `Room`           | returns non-null string                                                                                  |

## 4. Test Coverage

Test coverage was last updated on 07.12.2025.

- Method Coverage: `100%`
- Branch Coverage: `92%`
- Line Coverage: `100%`

## 5. Conclusion

This class has been thoroughly tested with high coverage across methods, branches, and lines.