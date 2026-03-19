/**
 * Contains the domain model classes for the Occupi application.
 * <p>
 * This package provides the core data structures that represent buildings, rooms, events, and related information for tracking room occupancy and availability. The model supports serialization to and from JSON format for persistence and data exchange.
 * </p>
 *
 * <h2>Core Entity Classes</h2>
 * <ul>
 *   <li>{@link ch.zhaw.it.pm3.occupi.model.Building} - Represents a building containing rooms and floors</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.model.BuildingInfo} - Record containing building metadata (name, address, etc.)</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.model.Room} - Represents a physical room with capacity, equipment, and events</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.model.Event} - Record representing scheduled events in rooms</li>
 * </ul>
 *
 * <h2>Support Classes</h2>
 * <ul>
 *   <li>{@link ch.zhaw.it.pm3.occupi.model.RoomEquipment} - Record describing equipment available in a room</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.model.RoomInfrastructure} - Enum for infrastructure types (projector, whiteboard, etc.)</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.model.RoomState} - Enum representing room availability states</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.model.RoomType} - Enum for different room types (classroom, meeting room, etc.)</li>
 * </ul>
 *
 * <h2>Design Notes</h2>
 * <p>
 * The model uses a mix of mutable classes ({@link ch.zhaw.it.pm3.occupi.model.Building},
 * {@link ch.zhaw.it.pm3.occupi.model.Room}) and immutable records
 * ({@link ch.zhaw.it.pm3.occupi.model.BuildingInfo}, {@link ch.zhaw.it.pm3.occupi.model.Event},
 * {@link ch.zhaw.it.pm3.occupi.model.RoomEquipment}) to balance flexibility with data integrity.
 * All model classes include Jackson annotations for JSON serialization support.
 * </p>
 *
 * @since 1.0
 */
package ch.zhaw.it.pm3.occupi.model;