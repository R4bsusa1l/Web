# Test Documentation for Class `BuildingInfo.java`

## 1. Introduction

`BuildingInfo` is a simple data carrier that holds basic metadata about a building: name, street, postcode, city and
department.

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                                                                                      | Description                         |
|---------------------------------------------------------------------------------------------|-------------------------------------|
| `BuildingInfo(String department, String name, String street, String city, String postcode)` | constructor that assigns all fields |

### 3.2 Equivalence Classes & Tests

### Constructor: `BuildingInfo(...)`

| Equivalence Class | Input                | Expected Outcome                           |
|-------------------|----------------------|--------------------------------------------|
| valid values      | all non-null strings | `BuildingInfo` created with all fields set |
| null parameter    | any parameter `null` | `NullPointerException` thrown              |
| empty string      | any parameter " "    | `IllegalArgumentException` thrown          |

## 4. Test Coverage

Test coverage was last updated on 10.11.2025.

- Method Coverage: `0%`
- Branch Coverage: `0%`
- Line Coverage: `0%`

## 5. Conclusion

This class has yet to be tested.