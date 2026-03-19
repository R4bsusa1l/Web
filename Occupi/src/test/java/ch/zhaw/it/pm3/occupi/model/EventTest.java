package ch.zhaw.it.pm3.occupi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code Event} according to the provided test concept,
 * adapted to a Java record with accessors: title(), description(), startDate(), endDate().
 * Expected validation rules (from the concept):
 *  - title: not null, not empty, max length 80
 *  - description: not null (empty allowed)
 *  - startDate: not null, must not be in the past
 *  - endDate: not null, must not be in the past, and strictly after startDate
 * <p>
 * Note: The implementation also has a fifth component 'tags'. The concept doesn't
 * define any rules for tags, so tests provide a neutral default list.
 */
class EventTest {

    // ---------- Helpers ------------------------------------------------------

    private static LocalDateTime now() {
        return LocalDateTime.now();
    }

    private static LocalDateTime minutesFromNow(long minutes) {
        return now().plusMinutes(minutes);
    }

    private static List<String> defaultTags() {
        List<String> t = new ArrayList<>();
        t.add("default");
        return t;
    }

    // ---------- Constructor tests -------------------------------------------

    @Test
    @DisplayName("Constructor: valid fields -> Event created with all fields set")
    void constructor_valid_creates() {
        Event e = new Event("Meeting", "Kickoff", minutesFromNow(5), minutesFromNow(65), defaultTags());
        assertNotNull(e);
        assertEquals("Meeting", e.title());
        assertEquals("Kickoff", e.description());
        assertNotNull(e.startDate());
        assertNotNull(e.endDate());
    }

    @Test
    @DisplayName("Constructor: null title -> NullPointerException")
    void constructor_nullTitle_throws() {
        assertThrows(NullPointerException.class,
                () -> new Event(null, "Desc", minutesFromNow(5), minutesFromNow(10), defaultTags()));
    }

    @Test
    @DisplayName("Constructor: title longer than 80 -> IllegalArgumentException")
    void constructor_tooLongTitle_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("X".repeat(81), "Desc", minutesFromNow(5), minutesFromNow(10), defaultTags()));
    }

    @Test
    @DisplayName("Constructor: empty title -> IllegalArgumentException")
    void constructor_emptyTitle_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("", "Desc", minutesFromNow(5), minutesFromNow(10), defaultTags()));
    }

    @Test
    @DisplayName("Constructor: null description -> NullPointerException")
    void constructor_nullDescription_throws() {
        assertThrows(NullPointerException.class,
                () -> new Event("Title", null, minutesFromNow(5), minutesFromNow(10), defaultTags()));
    }

    @Test
    @DisplayName("Constructor: empty description not allowed -> IllegalArgumentException")
    void constructor_emptyDescription_allowed() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("Title", "", minutesFromNow(5), minutesFromNow(10), defaultTags()));
    }

    @Test
    @DisplayName("Constructor: null startDate -> NullPointerException")
    void constructor_nullStart_throws() {
        assertThrows(NullPointerException.class,
                () -> new Event("Title", "Desc", null, minutesFromNow(10), defaultTags()));
    }

    @Test
    @DisplayName("Constructor: null endDate -> NullPointerException")
    void constructor_nullEnd_throws() {
        assertThrows(NullPointerException.class,
                () -> new Event("Title", "Desc", minutesFromNow(5), null, defaultTags()));
    }

    @Test
    @DisplayName("Constructor: endDate before startDate -> IllegalArgumentException")
    void constructor_endBeforeStart_throws() {
        LocalDateTime start = minutesFromNow(30);
        LocalDateTime end = minutesFromNow(10); // before start
        assertThrows(IllegalArgumentException.class,
                () -> new Event("Title", "Desc", start, end, defaultTags()));
    }

    


    // ---------- Accessors (record components) --------------------------------

    @Test
    @DisplayName("title(): returns current title value")
    void title_returns() {
        Event e = new Event("Alpha", "Desc", minutesFromNow(5), minutesFromNow(10), defaultTags());
        assertEquals("Alpha", e.title());
    }

    @Test
    @DisplayName("description(): returns current description")
    void description_returns() {
        Event e = new Event("Title", "D", minutesFromNow(5), minutesFromNow(10), defaultTags());
        assertEquals("D", e.description());
    }

    @Test
    @DisplayName("startDate(): returns current startDate")
    void startDate_returns() {
        var start = minutesFromNow(15);
        Event e = new Event("T", "D", start, minutesFromNow(30), defaultTags());
        assertEquals(start, e.startDate());
    }

    @Test
    @DisplayName("endDate(): returns current endDate")
    void endDate_returns() {
        var end = minutesFromNow(25);
        Event e = new Event("T", "D", minutesFromNow(5), end, defaultTags());
        assertEquals(end, e.endDate());
    }

    // ---------- eventMatchesQuery tests --------------------------------------

    @Test
    @DisplayName("eventMatchesQuery: null query -> NullPointerException")
    void eventMatchesQuery_nullQuery_throws() {
        Event e = new Event("Meeting", "Kickoff", minutesFromNow(5), minutesFromNow(10), defaultTags());
        assertThrows(NullPointerException.class, () -> e.eventMatchesQuery(null));
    }

    @Test
    @DisplayName("eventMatchesQuery: matches title (case-insensitive)")
    void eventMatchesQuery_matchesTitle_caseInsensitive() {
        Event e = new Event("Project Kickoff", "Some description",
                minutesFromNow(5), minutesFromNow(10), defaultTags());

        assertTrue(e.eventMatchesQuery("kickoff"));
        assertTrue(e.eventMatchesQuery("PROJECT"));
        assertTrue(e.eventMatchesQuery("PrOjEcT"));
    }

    @Test
    @DisplayName("eventMatchesQuery: matches description (case-insensitive)")
    void eventMatchesQuery_matchesDescription_caseInsensitive() {
        Event e = new Event("Title", "Important Meeting about JAVA",
                minutesFromNow(5), minutesFromNow(10), defaultTags());

        assertTrue(e.eventMatchesQuery("meeting"));
        assertTrue(e.eventMatchesQuery("java"));
        assertTrue(e.eventMatchesQuery("IMPORTANT"));
    }

    @Test
    @DisplayName("eventMatchesQuery: matches tags (case-insensitive)")
    void eventMatchesQuery_matchesTags_caseInsensitive() {
        List<String> tags = new ArrayList<>();
        tags.add("Planning");
        tags.add("Sprint");

        Event e = new Event("Title", "Desc",
                minutesFromNow(5), minutesFromNow(10), tags);

        assertTrue(e.eventMatchesQuery("plan"));
        assertTrue(e.eventMatchesQuery("SPRINT"));
    }

    @Test
    @DisplayName("eventMatchesQuery: no field matches query -> false")
    void eventMatchesQuery_noMatch_returnsFalse() {
        List<String> tags = new ArrayList<>();
        tags.add("backend");
        tags.add("server");

        Event e = new Event("Frontend Meeting", "Discuss UI",
                minutesFromNow(5), minutesFromNow(10), tags);

        assertFalse(e.eventMatchesQuery("database"));
    }



}
