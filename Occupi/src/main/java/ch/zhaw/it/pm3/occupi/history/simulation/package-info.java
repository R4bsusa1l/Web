/**
 * Simulation implementations for historical room usage data access.
 * <p>
 * This package provides file-based implementations of the {@link ch.zhaw.it.pm3.occupi.history.api.HistoryAPI}
 * interface, along with supporting data models. These implementations are designed for
 * development, testing, and demonstration purposes, simulating a historical data repository
 * using JSON files.
 * </p>
 *
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link ch.zhaw.it.pm3.occupi.history.simulation.FileHistoryAPI} -
 *       File-based implementation reading historical data from JSON files</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.history.simulation.RoomHistoryDTO} -
 *       Data model representing complete usage history for a single room</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.history.simulation.UsageEntryDTO} -
 *       Individual timestamp/occupancy measurement within a room's history</li>
 * </ul>
 *
 *
 * @see ch.zhaw.it.pm3.occupi.history.api.HistoryAPI
 * @see ch.zhaw.it.pm3.occupi.history.simulation.FileHistoryAPI
 * @see ch.zhaw.it.pm3.occupi.history.simulation.RoomHistoryDTO
 * @see ch.zhaw.it.pm3.occupi.history.simulation.UsageEntryDTO
 * @see ch.zhaw.it.pm3.occupi.util.JsonReader
 */
package ch.zhaw.it.pm3.occupi.history.simulation;