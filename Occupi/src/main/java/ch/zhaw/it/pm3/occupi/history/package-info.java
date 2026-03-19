/**
 * Root package for room usage history functionality in the Occupi application.
 * <p>
 * This package provides infrastructure for tracking and analyzing historical room
 * usage data. It enables the application to understand room utilization patterns
 * over time, supporting data-driven decisions about space management and resource
 * allocation.
 * </p>
 *
 * <h2>Package Structure:</h2>
 * <ul>
 *   <li>{@link ch.zhaw.it.pm3.occupi.history.api} - Defines the HistoryAPI interface
 *       for accessing historical usage data</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.history.simulation} - Contains file-based
 *       implementations and data models for history simulation</li>
 * </ul>
 *
 *
 * @see ch.zhaw.it.pm3.occupi.history.api.HistoryAPI
 * @see ch.zhaw.it.pm3.occupi.history.simulation.FileHistoryAPI
 * @see ch.zhaw.it.pm3.occupi.ui.RoomUsageAnalyzer
 */
package ch.zhaw.it.pm3.occupi.history;