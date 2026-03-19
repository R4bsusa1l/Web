package ch.zhaw.it.pm3.occupi.model;

import ch.zhaw.it.pm3.occupi.search.Search;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Record representing an event with title, description, start and end dates, and associated tags.
 *
 * @param title       the title of the event
 * @param description the description of the event
 * @param startDate   the start date and time of the event
 * @param endDate     the end date and time of the event
 * @param tags        the list of tags associated with the event
 */
public record Event(String title, String description,
                    LocalDateTime startDate, LocalDateTime endDate,
                    List<String> tags) {

    private static final int MAX_TITLE_LENGTH = 80;

    /**
     * Constructs an Event record and ensures that none of the fields are null or empty.
     * And enforces constraints on title length and date validity.
     *
     * @param title       the title of the event
     * @param description the description of the event
     * @param startDate   the start date and time of the event
     * @param endDate     the end date and time of the event
     * @param tags        the list of tags associated with the event
     */
    public Event {
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(startDate, "Start date cannot be null");
        Objects.requireNonNull(endDate, "End date cannot be null");
        Objects.requireNonNull(tags, "Tags cannot be null");

        if (title.isEmpty() || description.isEmpty()) {
            throw new IllegalArgumentException("Title or description cannot be empty");
        }

        // Title length constraint
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title length must be ≤ " + MAX_TITLE_LENGTH);
        }

        // End must be strictly after start
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    /**
     * Checks if any event in the room matches the search query.
     *
     * @param query the search query
     * @return true if any event matches the query, false otherwise
     * @throws NullPointerException if parameters are null
     */
    public boolean eventMatchesQuery(String query) {
        Objects.requireNonNull(query, "Query must not be null");

        return Search.containsIgnoreCase(title, query)
                || Search.containsIgnoreCase(description, query)
                || tagsMatchQuery(query);
    }

    /**
     * Checks if the event tags match the search query
     *
     * @param query the search query
     * @return true if any tags match, false otherwise
     */
    @SuppressWarnings("ChainedMethodCall")
    private boolean tagsMatchQuery(String query) {
        return tags.stream()
                .anyMatch(t -> Search.containsIgnoreCase(t, query));
    }
}