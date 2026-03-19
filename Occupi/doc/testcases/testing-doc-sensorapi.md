# Test Documentation for Class `FileSensorAPI.java`

## 1. Introduction

`FileSensorAPI` is an implementation of the `SensorAPI` interface that simulates the sensor data using a file.

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                 | Description                                                                                      |
|------------------------|--------------------------------------------------------------------------------------------------|
| `FileSensorAPI(File)`  | constructor that assigns all fields                                                              |
| `getOccupancy(String)` | returns an Optional<RoomOccupancy> object containing the room name and num of people in the room |

> Note: This class is implemented using the Jackson Library.
> The tests assume that the used library functions work correctly.

### 3.2 Equivalence Classes & Tests

### Constructor: `FileSensorAPI(File)`

| Equivalence Class | Input                  | Expected Outcome                                                                                                                 |
|-------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| valid values      | valid JSON file        | `FileSensorAPI` created with all fields set                                                                                      |
| empty file        | empty file             | `FileSensorAPI` created with all fields set                                                                                      |
| null file         | `null`                 | `NullPointerException` thrown                                                                                                    |
| file not found    | non existing file path | Constructor either: (1) falls back to default file and succeeds, or (2) throws FileNotFoundException if no fallback is available |

### Constructor: `FileSensorAPI()`

| Equivalence Class | Input    | Expected Outcome                                                                                                                 |
|-------------------|----------|----------------------------------------------------------------------------------------------------------------------------------|
| initialize        | no Input | Constructor either: (1) falls back to default file and succeeds, or (2) throws FileNotFoundException if no fallback is available |



### Method: `getOccupancy(String roomId)`

| Equivalence Class   | Input                       | Expected Outcome                    |
|---------------------|-----------------------------|-------------------------------------|
| valid roomId        | valid room name in the file | RoomOccupancy returned              |
| non existing roomId | "ZL O7.01"                  | empty Optional returned             |
| null id             | `null`                      | `NullPointerException` thrown       |
| blank id            | "  " -> empty character     | `IllegalArgumentException` thrown   |

## 4. Test Coverage

Test coverage was last updated on 01.12.2025.

- Method Coverage: `100%`
- Branch Coverage: `83%`
- Line Coverage: `95%`

## 5. Conclusion

This class has been thoroughly tested for all specified equivalence classes. The tests ensure that the `FileSensorAPI` behaves as expected under various conditions.