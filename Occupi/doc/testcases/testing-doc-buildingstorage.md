# Test Documentation for Class `FileBuildingStorageAPI.java`

## 1. Introduction

The class `FileBuildingStorageAPI` provides a file-based implementation of the `BuildingStorageAPI` interface. It
enables reading and writing lists of `Building` objects to and from a JSON file using the `JsonReader` utility. This
class is stateless after construction and is thread-safe.

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                                  | Description                                            |
|-----------------------------------------|--------------------------------------------------------|
| `FileBuildingStorageAPI(Path filePath)` | Constructor, sets up storage for the given file path   |
| `getBuildings()`                        | Reads and returns all buildings from the JSON file     |
| `saveBuildings(List<Building>)`         | Writes the provided list of buildings to the JSON file |

### 3.2 Equivalence Classes & Tests

#### Constructor: `FileBuildingStorageAPI(Path filePath)`

| Equivalence Class | Inputs            | Expected Outcome                  |
|-------------------|-------------------|-----------------------------------|
| valid file path   | existing          | path is saved                     |
| invalid path      | non existing path | throws `IllegalArgumentException` |
| null file path    | null              | throws `NullPointerException`     |
| directory path    | directory path    | throws `IllegalArgumentException` |

#### Method: `getBuildings()`

| Equivalence Class      | Inputs                 | Expected Outcome                                 |
|------------------------|------------------------|--------------------------------------------------|
| valid file, valid JSON | file with valid JSON   | returns list of buildings matching file contents |
| valid file, empty JSON | file with empty array  | returns empty list                               |
| corrupted JSON         | file with invalid JSON | returns empty list, logs error                   |
| file deleted           | file was deleted       | returns empty list, logs error                   |

#### Method: `saveBuildings(List<Building>)`

| Equivalence Class      | Inputs                         | Expected Outcome                                |
|------------------------|--------------------------------|-------------------------------------------------|
| valid list, valid path | non-empty list, writable file  | buildings written to file, no exception         |
| empty list, valid path | empty list, writable file      | file overwritten with empty array, no exception |
| null list, valid path  | null list, writable file       | throws `NullPointerException`                   |
| read-only file         | non-empty list, read-only file | throws `RuntimeException`                       |
| file deleted           | non-empty list, deleted file   | buildings written to new file, no exception     |

## 4. Test Coverage

Test coverage was last updated on 18.11.2025.

- Method Coverage: `100%`
- Branch Coverage: `100%`
- Line Coverage: `100%`

