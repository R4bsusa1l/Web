# Test Documentation for Class `FileHistoryAPI.java`

## 1. Introduction

The class `FileHistoryAPI` provides a file-based implementation of the `HistoryAPI` interface. It enables reading
historical room usage data from a JSON file, providing access to timestamped occupancy information. The data is
read-only; this class does not modify or persist data back to the source. This implementation is stateless after
construction and therefore thread-safe for read operations.

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                               | Description                                                           |
|--------------------------------------|-----------------------------------------------------------------------|
| `FileHistoryAPI(Path path)`          | Constructor, validates and sets up file path for reading history data |
| `getRoomUsageHistory(String roomId)` | Retrieves usage history for a specific room as a time-series mapping  |

### 3.2 Equivalence Classes & Tests

#### Constructor: `FileHistoryAPI(Path path)`

| Equivalence Class | Inputs                     | Expected Outcome                  |
|-------------------|----------------------------|-----------------------------------|
| valid file path   | existing regular file      | path is saved, instance created   |
| non-existent file | non-existent path          | throws `IllegalArgumentException` |
| null file path    | null                       | throws `NullPointerException`     |
| directory path    | directory instead of file  | throws `IllegalArgumentException` |
| not json path     | path not ending with .json | throws `IllegalArgumentException` |

#### Method: `getRoomUsageHistory(String roomId)`

| Equivalence Class               | Inputs                              | Expected Outcome                               |
|---------------------------------|-------------------------------------|------------------------------------------------|
| valid room with history         | existing roomId with usage data     | returns map of timestamps to occupancy counts  |
| non-existent room               | roomId not in file                  | returns empty map                              |
| null roomId                     | null roomId                         | throws `NullPointerException`                  |
| empty roomId                    | empty string ""                     | returns empty map                              |
| valid file, empty array         | file with empty JSON array          | returns empty map                              |
| room with empty history         | roomId with empty historyUsage list | returns empty map                              |
| room with single entry          | roomId with one usage entry         | returns map with one timestamp-occupancy pair  |
| room with multiple entries      | roomId with multiple usage entries  | returns map with all timestamp-occupancy pairs |
| corrupted JSON                  | file with invalid JSON format       | throws `IllegalStateException`                 |
| file deleted after construction | file removed after object creation  | throws `IllegalStateException`                 |
| multiple calls consistency      | same roomId called multiple times   | returns consistent results on each call        |
| different rooms same file       | multiple different roomIds          | returns correct history for each room          |
| stateless behavior              | multiple operations on same API     | API remains stateless, results consistent      |
| case sensitive roomId           | roomId with different case          | returns empty map (case-sensitive matching)    |
| roomId with whitespace          | roomId with leading/trailing spaces | returns empty map (exact match required)       |

## 4. Test Coverage

Test coverage was last updated on 01.12.2025.

- Method Coverage: `100%`
- Branch Coverage: `100%`
- Line Coverage: `100%`

