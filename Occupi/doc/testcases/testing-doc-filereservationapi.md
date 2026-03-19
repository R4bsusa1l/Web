# Test Documentation for Class `FileReservationAPI.java`

## 1. Introduction

`FileReservationAPI` is an implementation of the `ReservationAPI` interface that simulates the reservations using a
file.

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                     | Description                            |
|----------------------------|----------------------------------------|
| `FileReservationAPI(Path)` | constructor that loads and caches data |
| `getReservations()`        | returns all room reservations          |

### 3.2 Equivalence Classes & Tests

### Constructor: `FileReservationAPI(Path)`

| Equivalence Class | Input                                        | Expected Outcome                                  |
|-------------------|----------------------------------------------|---------------------------------------------------|
| valid values      | valid JSON file                              | `FileReservationAPI` created; entries cached      |
| empty file        | empty JSON array `[]`                        | `FileReservationAPI` created; zero entries cached |
| null path         | `null`                                       | `NullPointerException` thrown                     |
| file not found    | non-existing file path                       | `FileNotFoundException` thrown                    |
| directory path    | path to a directory                          | `FileNotFoundException` thrown                    |
| malformed JSON    | syntactically invalid or wrong types in JSON | `IOException` thrown                              |

### Method: `getReservations()`

- Returns a non-null list of `RoomReservation` objects, one per entry in the JSON file.
- Rooms with empty `timeWindows` are included.
- Time windows are validated via `TimeWindow` and `RoomReservation` invariants (non-null, ordered, non-overlapping). The
  list is unmodifiable as per API record behavior.
- Multiple calls are stable and return equal results without re-reading the file (data cached on construction).

## 4. Implemented Tests and Coverage

Test coverage was last updated on 07.12.2025.

- Method Coverage: `100%`
- Branch Coverage: `100%`
- Line Coverage: `100%`