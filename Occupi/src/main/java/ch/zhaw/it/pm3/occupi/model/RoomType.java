package ch.zhaw.it.pm3.occupi.model;

import java.util.Objects;

/**
 * Enumeration of room types available in the building.
 */
public enum RoomType {
    /**
     * Classroom type.
     */
    CLASS_ROOM("Klassenzimmer"),
    /**
     * Meeting room type.
     */
    MEETING_ROOM("Besprechungsraum");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the room type.
     *
     * @return the localized display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a display name string to a RoomType enum value.
     *
     * @param name the display name to convert
     * @return the matching RoomType, or null if not found
     */
    public static RoomType getByName(String name) {
        Objects.requireNonNull(name, "Input name must not be null");
        RoomType roomType = null;

        for (RoomType type : RoomType.values()) {
            if (type.displayName.equalsIgnoreCase(name)) {
                roomType = type;
                break;
            }
        }

        return roomType;
    }
}
