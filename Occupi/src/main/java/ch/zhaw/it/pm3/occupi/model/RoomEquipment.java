package ch.zhaw.it.pm3.occupi.model;

import java.util.Objects;
import java.util.Set;

/**
 * Record representing the equipment available in a room.
 *
 * @param wallplugs          the number of wall plugs available in the room
 * @param wifiQuality        the quality of the Wi-Fi connection (0-5)
 * @param roomInfrastructure set of RoomInfrastructure available in the room
 */
public record RoomEquipment(int wallplugs, int wifiQuality, Set<RoomInfrastructure> roomInfrastructure) {

    private static final int MIN_WIFI_QUALITY = 0;
    private static final int MAX_WIFI_QUALITY = 5;
    private static final int MAX_WALLPLUGS = 50;
    private static final int MIN_WALLPLUGS = 0;

    /**
     * Constructs a RoomEquipment record and ensures that the fields have valid values.
     *
     * @param wallplugs     the number of wall plugs available in the room
     * @param wifiQuality   the quality of the Wi-Fi connection (0-5)
     * @param roomInfrastructure set of RoomInfrastructure available in the room
     */
    public RoomEquipment {
        if (wallplugs < MIN_WALLPLUGS || wallplugs > MAX_WALLPLUGS) {
            throw new IllegalArgumentException("Number of wallplugs cannot be negative or over "+ MAX_WALLPLUGS);
        }
        if (wifiQuality < MIN_WIFI_QUALITY || wifiQuality > MAX_WIFI_QUALITY) {
            throw new IllegalArgumentException("Wi-Fi quality must be between 0 and "+ MAX_WIFI_QUALITY);
        }
        Objects.requireNonNull(roomInfrastructure, "Room equipment map cannot be null");

        roomInfrastructure = Set.copyOf(roomInfrastructure);

    }
}
