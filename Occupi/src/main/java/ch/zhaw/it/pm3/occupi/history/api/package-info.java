/**
 * API definitions for accessing historical room usage data.
 * <p>
 * This package defines the contract for retrieving and working with historical
 * room occupancy information. It provides an abstraction layer that allows different
 * implementations (file-based, database-based, remote API-based) to be used
 * interchangeably throughout the application.
 * </p>
 *
 * <h2>Core Interface:</h2>
 * <ul>
 *   <li>{@link ch.zhaw.it.pm3.occupi.history.api.HistoryAPI} - Main interface
 *       defining methods for accessing historical usage data</li>
 * </ul>
 *
 *
 * @see ch.zhaw.it.pm3.occupi.history.api.HistoryAPI
 * @see ch.zhaw.it.pm3.occupi.ui.RoomUsageAnalyzer
 * @see java.time.LocalDateTime
 */
package ch.zhaw.it.pm3.occupi.history.api;