package ch.zhaw.it.pm3.occupi.model;

import java.util.Objects;

/**
 * Record representing building information.
 *
 * @param shortName  the short name of the building e.g. (ZL)
 * @param fullName   the full name of the building e.g. (ZL (Lagerstasse))
 * @param street     the street address of the building
 * @param postcode   the postal code of the building
 * @param city       the city where the building is located
 * @param department the department associated with the building
 */
public record BuildingInfo(String shortName, String fullName, String street, String postcode, String city, String department) {

    /**
     * Constructs a BuildingInfo record and ensures that none of the fields are null.
     *
     * @param shortName  the short name of the building e.g. (ZL)
     * @param fullName   the full name of the building e.g. (ZL (Lagerstasse))
     * @param street     the street address of the building
     * @param postcode   the postal code of the building
     * @param city       the city where the building is located
     * @param department the department associated with the building
     */
    public BuildingInfo {
        Objects.requireNonNull(shortName, "Short name cannot be null");
        Objects.requireNonNull(fullName, "Full name cannot be null");
        Objects.requireNonNull(street, "Street cannot be null");
        Objects.requireNonNull(postcode, "Postcode cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        Objects.requireNonNull(department, "Department cannot be null");

        if (shortName.isEmpty() || fullName.isEmpty() || street.isEmpty() || postcode.isEmpty() || city.isEmpty() || department.isEmpty()) {
            throw new IllegalArgumentException("None of the fields can be empty");
        }
    }
}
