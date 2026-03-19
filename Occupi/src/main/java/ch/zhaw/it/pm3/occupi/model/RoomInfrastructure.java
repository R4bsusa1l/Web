package ch.zhaw.it.pm3.occupi.model;

import java.util.Objects;

/**
 * Enum representing different types of infrastructure available in a room.
 */
public enum RoomInfrastructure {
    /**
     * Blackboard infrastructure.
     */
    BLACKBOARD("Wandtafel"),
    /**
     * Whiteboard infrastructure.
     */
    WHITEBOARD("Whiteboard"),
    /**
     * Flipchart infrastructure.
     */
    FLIPCHART("Flipchart"),
    /**
     * Projector infrastructure.
     */
    PROJECTOR("Beamer"),
    /**
     * Air conditioner infrastructure.
     */
    AIR_CONDITIONER("Klimaanlage");

    private final String name;

    /**
     * Constructor for RoomInfrastructure enum.
     *
     * @param name the display name of the infrastructure
     */
    RoomInfrastructure(String name) {
        this.name = name;
    }

    /**
     * Gets the display name of the infrastructure.
     *
     * @return the name of the infrastructure
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves a RoomInfrastructure enum constant by its name.
     *
     * @param name the name of the infrastructure
     * @return the corresponding RoomInfrastructure enum constant
     */
    public static RoomInfrastructure getByName(String name) {
        Objects.requireNonNull(name, "Name must not be null");
        RoomInfrastructure roomInfrastructure = null;

        for (RoomInfrastructure infrastructure : RoomInfrastructure.values()) {
            if (infrastructure.name.equalsIgnoreCase(name)) {
                roomInfrastructure = infrastructure;
                break;
            }
        }

        return roomInfrastructure;
    }
}
