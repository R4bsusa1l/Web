package ch.zhaw.it.pm3.occupi.search;

import ch.zhaw.it.pm3.occupi.model.BuildingInfo;
import ch.zhaw.it.pm3.occupi.model.Room;


/**
 * A Data Transfer Object (DTO) that represents the result of a search operation.
 * This record encapsulates information about a building and a specific room within that building.
 *
 * @param buildingInfo the information about the building where the room is located
 * @param room         the specific room that matches the search criteria
 */
public record SearchResultDTO(BuildingInfo buildingInfo, Room room) {
}
