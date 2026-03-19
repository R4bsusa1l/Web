# Test Documentation for Class `Building.java`

## 1. Introduction

The class `Building` models a building and contains metadata (`BuildingInfo`), a set of floor identifiers and a list of
rooms.

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                                                                      | Description                                                         |
|-----------------------------------------------------------------------------|---------------------------------------------------------------------|
| `Building(BuildingInfo buildingInfo, Set<String> floors, List<Room> rooms)` | constructor that initializes building metadata, floor set and rooms |
| `buildingInfo()`                                                            | returns the `BuildingInfo` instance                                 |
| `setBuildingInfo(BuildingInfo buildingInfo)`                                | replaces the building metadata                                      |

### 3.2 Equivalence Classes & Tests

#### Constructor: `Building(BuildingInfo buildingInfo, Set<String> floors, List<Room> rooms)`

| Equivalence Class   | Inputs                                         | Expected Outcome                             |
|---------------------|------------------------------------------------|----------------------------------------------|
| valid arguments     | non-null `BuildingInfo`, mutable `Set`, `List` | instance created                             |
| null `buildingInfo` | `null` buildingInfo                            | `NullPointerException` thrown                |
| null `floors`       | `null` floors                                  | `NullPointerException` thrown                |
| null `rooms`        | `null` rooms                                   | `NullPointerException` thrown                |
| empty rooms list    | empty `List<Room>`                             | instance created; rooms list should be empty |
| empty floors set    | empty `Set<String>`                            | instance created; floors set should be empty |

#### Method: `rooms()`

| Equivalence Class | Inputs              | Expected Outcome                       |
|-------------------|---------------------|----------------------------------------|
| immutable         | remove list element | `UnsupportedOperationException` thrown |

#### Method: `floors()`

| Equivalence Class | Inputs             | Expected Outcome                       |
|-------------------|--------------------|----------------------------------------|
| immutable         | remove set element | `UnsupportedOperationException` thrown |

## 5. Test Coverage

Test coverage was last updated on 17.10.2025.

- Method Coverage: `100%`
- Branch Coverage: `100%`
- Line Coverage: `100%`

## 6. Conclusion

This class has been thoroughly tested at the unit level, covering all public methods and key scenarios.

