/**
 * Search and filter functionality for the Occupi application.
 * <p>
 * This package provides the search and filtering logic for rooms and buildings.
 * It contains classes for defining filter criteria, performing searches,
 * and presenting search results in various formats.
 * </p>
 *
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link ch.zhaw.it.pm3.occupi.search.FilterCriteriaDTO} - Defines search filter criteria
 *       (city, building, floor, room type, capacity, infrastructure)</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.search.Search} - Performs searches on building lists
 *       based on filter criteria</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.search.SearchResultDTO} - Encapsulates search results
 *       grouped by building</li>
 *   <li>{@link ch.zhaw.it.pm3.occupi.search.PaginatedSearchResult} - Provides paginated
 *       search results for UI display</li>
 * </ul>
 *
 * @see ch.zhaw.it.pm3.occupi.controllers.BuildingController
 * @see ch.zhaw.it.pm3.occupi.model
 */
package ch.zhaw.it.pm3.occupi.search;