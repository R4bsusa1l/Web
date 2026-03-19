# Test Documentation for Record `Event.java`

## 1. Introduction

The record `Event` represents the categories tasks can be assigned.

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                                        | Description                                                 |
|-----------------------------------------------|-------------------------------------------------------------|
| `Event(String title, String description,...)` | constructor of the record                                   |
| `eventMatchesQuery(String query)`             | Checks if any event in the room matches the search query.   |

### 3.2 Equivalence Classes & Tests

#### Method: `Event(String title, String description, LocalDateTime startDate, LocalDateTime endDate)`

| Equivalence Class | Inputs                                      | Expected Output                                   |
|-------------------|---------------------------------------------|---------------------------------------------------|
| valid fields      | non-empty title/description, non-null dates | `Event` created with all fields set               |
| null title        | `null` title                                | `NullPointerException` thrown                     |
| too long title    | title > 80 characters                       | `IllegalArgumentException` thrown                 |
| empty title       | `""` title                                  | `IllegalArgumentException` thrown                 |
| null description  | `null` description                          | `NullPointerException` thrown                     |
| empty description | `""` description                            | `Event` created, description returns empty string |
| null startDate    | `null` startDate                            | `NullPointerException` thrown                     |
| null endDate      | `null` endDate                              | `NullPointerException` thrown                     |

#### Method: `eventMatchesQuery(String query)`

| Equivalence Class                                | Inputs                                                                                      | Expected Output               |
|--------------------------------------------------|---------------------------------------------------------------------------------------------|-------------------------------|
| Null query → exception                           | `query = null`                                                                              | `NullPointerException` thrown |
| Matches title (case-insensitive)                 | Title contains query ignoring case (e.g., `"Project Kickoff"`, `"kickoff"`, `"PROJECT"`)    | `true`                        |
| Matches description (case-insensitive)           | Description contains query ignoring case (e.g., `"Important Meeting about JAVA"`, `"java"`) | `true`                        |
| Matches tags (case-insensitive, partial allowed) | Tags include a tag containing query ignoring case (e.g., `"Planning"`, `"plan"`)            | `true`                        |
| No fields match query                            | Query not found in title, description, or tags (e.g., `"database"`)                         | `false`                       |



## 4. Test Coverage

Test coverage was last updated on 06.12.2025.

- Method Coverage: `100%`
- Branch Coverage: `100%`
- Line Coverage: `100%`