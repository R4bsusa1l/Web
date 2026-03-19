/**
 * Controller layer for the Occupi application.
 * <p>
 * This package contains the controller classes that orchestrate the interaction
 * between the UI layer and the business logic/data layer. Controllers manage
 * user input, coordinate data retrieval, and update the UI accordingly.
 * </p>
 *
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link ch.zhaw.it.pm3.occupi.controllers.MainWindowController} - Main UI controller
 *       that handles user interactions, filter operations, and search requests</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.controllers.BuildingController} - Business controller
 *       that manages building data, coordinates sensor/reservation APIs, and delegates
 *       search requests to the search logic</li>
 * </ul>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Orchestrate UI interactions and data flow</li>
 *   <li>Coordinate multiple data sources (storage, sensors, reservations)</li>
 *   <li>Delegate business logic to appropriate services</li>
 *   <li>Update UI based on data changes</li>
 * </ul>
 *
 * @see ch.zhaw.it.pm3.occupi.storage
 * @see ch.zhaw.it.pm3.occupi.search
 * @see ch.zhaw.it.pm3.occupi.sensors.api
 * @see ch.zhaw.it.pm3.occupi.sensors.simulation
 * @see ch.zhaw.it.pm3.occupi.reservation.api
 * @see ch.zhaw.it.pm3.occupi.reservation.simulation
 */
package ch.zhaw.it.pm3.occupi.controllers;