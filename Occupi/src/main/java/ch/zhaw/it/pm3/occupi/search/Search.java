package ch.zhaw.it.pm3.occupi.search;

/**
 * Utility class for searching rooms based on filter criteria
 */
public class Search {

    private Search() {
        // Private constructor to prevent instantiation
    }

    /**
     * Checks if filter criteria are specified.
     *
     * @param criteria the filter criteria
     * @return true if no criteria are specified, false otherwise
     */
    public static boolean containsCriteria(FilterCriteriaDTO criteria) {
        return isNotBlank(criteria.searchQuery())
                || isNotBlank(criteria.city())
                || isNotBlank(criteria.building())
                || isNotBlank(criteria.floor())
                || criteria.roomType() != null
                || criteria.capacity() > 0
                || (criteria.infrastructure() != null && !criteria.infrastructure().isEmpty());
    }

    /**
     * Checks if filter criteria are specified excluding search query.
     *
     * @param criteria the filter criteria
     * @return true if no criteria are specified, false otherwise
     */
    public static boolean containsCriteriaNoQuery(FilterCriteriaDTO criteria) {
        return isNotBlank(criteria.city())
                || isNotBlank(criteria.building())
                || isNotBlank(criteria.floor())
                || criteria.roomType() != null
                || criteria.capacity() > 0
                || (criteria.infrastructure() != null && !criteria.infrastructure().isEmpty());
    }

    /**
     * Checks if a string is not null, not empty, and not just whitespace.
     *
     * @param s the string to check
     * @return true if the string is not blank, false otherwise
     */
    public static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
     * Checks if a haystack string contains a needle string, ignoring case.
     *
     * @param haystack the string to search in
     * @param needle   the string to search for
     * @return true if haystack contains needle (case-insensitive), false otherwise
     */
    public static boolean containsIgnoreCase(String haystack, String needle) {
        boolean result = false;

        if (haystack != null && isNotBlank(needle)) {
            result = haystack.toLowerCase().contains(needle.toLowerCase());
        }
        return result;
    }
}
