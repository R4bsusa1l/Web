# Test Documentation for Class `RoomEquipment.java`

## 1. Introduction

The class `RoomEquipment` represents the technical equipment available in a room, including wall plugs, WiFi
quality, and RoomInfrastructure(Projector, Airconditioner, Flipchart, Blackboard, Whiteboard).

## 2. Test Strategy

- **Test Level:** Unit Test
- **Test Tool:** JUnit Jupiter
- **Test Automation:** Yes

## 3. Test Scope

### 3.1 Methods to be Tested

| Method                                                            | Description                                    |
|-------------------------------------------------------------------|------------------------------------------------|
| `RoomEquipment(int wallplugs, ...)`                               | constructor of the class                       |
| `getWallplugs()`                                                  | returns the number of wall plugs               |
| `hasProjector()`                                                  | returns whether a projector is available       |
| `getWifiQuality()`                                                | returns the WiFi quality (0-5)                 |
| `hasAirConditioning()`                                            | returns whether air conditioning is available  |
| `roomInfrastructure()`                                            | returns unmodifiable set of RoomInfrastructure |
| `addRoomInfrastructure(RoomInfrastructure roomInfrastructure)`    | adds a roomInfrastructure to the set           |
| `removeRoomInfrastructure(RoomInfrastructure roomInfrastructure)` | removes a roomInfrastructure from the set      |

### 3.2 Equivalence Classes & Tests

#### Method:
`RoomEquipment(int wallplugs, boolean projector, int wifiQuality, boolean airConditioning, Set<RoomInfrastructure> roomInfrastructure)`

| Equivalence Class       | Inputs                    | Expected Output                             |
|-------------------------|---------------------------|---------------------------------------------|
| valid fields            | all valid non-null values | `RoomEquipment` created with all fields set |
| negative wallplugs      | wallplugs < 0             | `IllegalArgumentException` thrown           |
| excessive wallplugs     | wallplugs > 50            | `IllegalArgumentException` thrown           |
| negative wifiQuality    | wifiQuality < 0           | `IllegalArgumentException` thrown           |
| excessive wifiQuality   | wifiQuality > 5           | `IllegalArgumentException` thrown           |
| null RoomInfrastructure | `null` RoomInfrastructure | `NullPointerException` thrown               |
| empty RoomInfrastructure  | empty set                 | `RoomEquipment` created with empty set      |
| zero wallplugs          | wallplugs = 0             | `RoomEquipment` created                     |
| zero wifiQuality        | wifiQuality = 0           | `RoomEquipment` created                     |
| max wifiQuality         | wifiQuality = 5           | `RoomEquipment` created                     |

#### Method: `getWallplugs()`

| Equivalence Class  | Inputs  | Expected Output                 |
|--------------------|---------|---------------------------------|
| wallplugs returned | nothing | returns current wallplugs value |

#### Method: `hasProjector()`

| Equivalence Class  | Inputs  | Expected Output                 |
|--------------------|---------|---------------------------------|
| projector returned | nothing | returns current projector value |

#### Method: `getWifiQuality()`

| Equivalence Class    | Inputs  | Expected Output                   |
|----------------------|---------|-----------------------------------|
| wifiQuality returned | nothing | returns current wifiQuality value |

#### Method: `hasAirConditioning()`

| Equivalence Class        | Inputs  | Expected Output                       |
|--------------------------|---------|---------------------------------------|
| airConditioning returned | nothing | returns current airConditioning value |

#### Method: `roomInfrastructure()  for getting room infrastructure`

| Equivalence Class               | Inputs                         | Expected Output                               |
|---------------------------------|--------------------------------|-----------------------------------------------|
| RoomInfrastructure set returned | nothing                        | returns unmodifiable set of RoomInfrastructure|
| unmodifiable set                | attempt to modify returned set | `UnsupportedOperationException` thrown        |

## 4. Test Coverage

Test coverage was last updated on 10.11.2025.

- Method Coverage: `100%`
- Branch Coverage: `100%`
- Line Coverage: `100%`

## 5. Conclusion

This class has been thoroughly tested to ensure that all methods function as expected, including validation of inputs and correct handling of edge cases. The tests confirm that the `RoomEquipment` class behaves correctly under various scenarios.