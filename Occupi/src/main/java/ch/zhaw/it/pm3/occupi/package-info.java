/**
 * Main application package for Occupi - Room Occupancy Management System.
 * <p>
 * This package contains the main application entry point and bootstrapping logic.
 * The application provides functionality for managing and viewing room occupancy,
 * reservations, and building information in real-time.
 * </p>
 *
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link ch.zhaw.it.pm3.occupi.Occupi} - Main application class and JavaFX entry point</li>
 * </ul>
 *
 * <h2>Architecture Overview:</h2>
 * <p>
 * The application follows a layered architecture with clear separation of concerns:
 * </p>
 * <ul>
 *   <li><strong>UI Layer:</strong> JavaFX-based user interface components</li>
 *   <li><strong>Controller Layer:</strong> Business logic and flow control</li>
 *   <li><strong>Service Layer:</strong> API implementations for data access</li>
 *   <li><strong>Model Layer:</strong> Domain objects and data structures</li>
 * </ul>
 *
 * @see ch.zhaw.it.pm3.occupi.controllers
 * @see ch.zhaw.it.pm3.occupi.model
 * @see ch.zhaw.it.pm3.occupi.storage
 * @see ch.zhaw.it.pm3.occupi.sensors.api
 * @see ch.zhaw.it.pm3.occupi.sensors.simulation
 * @see ch.zhaw.it.pm3.occupi.reservation.api
 * @see ch.zhaw.it.pm3.occupi.reservation.simulation
 */
package ch.zhaw.it.pm3.occupi;