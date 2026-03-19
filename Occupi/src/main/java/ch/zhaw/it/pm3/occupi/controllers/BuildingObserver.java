package ch.zhaw.it.pm3.occupi.controllers;

/**
 * Observer interface for building updates.
 */
public interface BuildingObserver {

    /**
     * Notifies the observer of a building update.
     */
    void notifyBuildingUpdate();
}
