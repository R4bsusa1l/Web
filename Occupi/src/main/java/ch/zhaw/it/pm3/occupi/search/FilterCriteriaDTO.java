package ch.zhaw.it.pm3.occupi.search;

import ch.zhaw.it.pm3.occupi.model.RoomInfrastructure;
import ch.zhaw.it.pm3.occupi.model.RoomType;

import java.util.Set;

/**
 * Record to hold filter criteria for room search
 *
 * @param searchQuery    the search query string
 * @param city       the city(city) that the room is in
 * @param building       the building that the room is in
 * @param roomType       the room type
 * @param floor          the floor that the room is on
 * @param capacity       the minimum number of seats in a room
 * @param infrastructure map containing required infrastructure and their quantities
 */
public record FilterCriteriaDTO(String searchQuery,
                                String city,
                                String building,
                                RoomType roomType,
                                String floor,
                                int capacity,
                                Set<RoomInfrastructure> infrastructure) {

    private static final char SEPARATION_CHAR = '\'';

    @Override
    public String toString() {
        return "FilterCriteriaDTO{" +
                "searchQuery='" + searchQuery + SEPARATION_CHAR +
                ", city='" + city + SEPARATION_CHAR +
                ", building='" + building + SEPARATION_CHAR +
                ", roomType=" + roomType +
                ", floor='" + floor + SEPARATION_CHAR +
                ", capacity=" + capacity +
                ", infrastructure=" + infrastructure +
                '}';
    }
}