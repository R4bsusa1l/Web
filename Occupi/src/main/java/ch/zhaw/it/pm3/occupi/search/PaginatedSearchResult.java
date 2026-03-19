package ch.zhaw.it.pm3.occupi.search;

import ch.zhaw.it.pm3.occupi.model.BuildingInfo;
import ch.zhaw.it.pm3.occupi.model.Room;

import java.util.List;
import java.util.Map;

/**
 * Record to hold paginated search results with metadata
 *
 * @param searchResult map of BuildingInfo to list of Rooms for current page
 * @param currentPage  the current page number (0-based)
 * @param totalPages   the total number of pages
 * @param totalResults the total number of results across all pages
 * @param pageSize     the number of results per page
 */
public record PaginatedSearchResult(
        Map<BuildingInfo, List<Room>> searchResult,
        int currentPage,
        int totalPages,
        int totalResults,
        int pageSize
) {
    /**
     * Checks if there is a next page
     *
     * @return true if there is a next page, false otherwise
     */
    public boolean hasNext() {
        return currentPage < totalPages - 1;
    }

    /**
     * Checks if there is a previous page
     *
     * @return true if there is a previous page, false otherwise
     */
    public boolean hasPrevious() {
        return currentPage > 0;
    }

    /**
     * Gets the page number to display (1-based for user-facing display)
     *
     * @return the page number (1-based)
     */
    public int getDisplayPageNumber() {
        return currentPage + 1;
    }
}

