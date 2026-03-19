package ch.zhaw.it.pm3.occupi.storage;

import ch.zhaw.it.pm3.occupi.model.Building;

import java.util.List;

/**
 * API for storing and retrieving Building data.
 */
public interface BuildingStorageAPI {

    /**
     * Retrieves a list of all buildings.
     *
     * @return List of Building objects.
     */
    List<Building> getBuildings();

    /**
     * Saves a list of buildings.
     *
     * @param buildings List of Building objects to be saved.
     */
    void saveBuildings(List<Building> buildings);
}
