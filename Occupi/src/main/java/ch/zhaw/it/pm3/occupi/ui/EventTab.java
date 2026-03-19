package ch.zhaw.it.pm3.occupi.ui;

import ch.zhaw.it.pm3.occupi.model.Event;
import ch.zhaw.it.pm3.occupi.model.Room;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller for the Event Tab in the room view.
 * This tab displays current events and allows users to create new events.
 */
@SuppressWarnings("ChainedMethodCall")
public class EventTab {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final double SECTION_SPACING = 8;
    private static final double EVENT_BOX_SPACING = 5;
    private static final double TIME_BOX_SPACING = 5;
    private static final double TAGS_BOX_SPACING = 5;
    private static final double TAG_BOX_SPACING = 3;
    private static final int MAX_DISPLAYED_EVENTS = 2;

    /**
     * Private Constructor to prevent instantiation
     */
    private EventTab(){}

    /**
     * Creates the Event tab for the room view.
     *
     * @param room the room whose events are to be displayed
     * @return the constructed Event Tab
     */
    public static Tab createEventTab(Room room) {
        Objects.requireNonNull(room, "Room cannot be null");

        Tab eventTab = new Tab("Events");
        eventTab.setId("eventTab");

        eventTab.setContent(createEventsSection(room));
        return eventTab;
    }

    /**
     * Creates the events section for the room, listing current events if available.
     * This method expects the event list to be sorted by date.
     *
     * @param room the room whose events are to be displayed
     * @return a VBox containing the events section
     */
    private static VBox createEventsSection(Room room) {
        Objects.requireNonNull(room.getCurrentEvents(), "Events list cannot be null");
        List<Event> events = room.getCurrentEvents();
        VBox section = new VBox(SECTION_SPACING);
        section.getStyleClass().add("section-content");

        // Create Event button
        Button createEventButton = new Button("Create Event");
        createEventButton.setOnAction(e -> {
            EventCreationDialog dialog = new EventCreationDialog();
            Optional<Event> optionalEvent = dialog.showAndWait();

            if (optionalEvent.isPresent()) {
                Event newEvent = optionalEvent.get();
                room.addEvent(newEvent);
                // Refresh the events section
                section.getChildren().clear();
                VBox updatedSection = createEventsSection(room);
                section.getChildren().addAll(updatedSection.getChildren());
            }
        });


        FontIcon plusIcon = new FontIcon(BootstrapIcons.PLUS);
        plusIcon.setIconColor(Color.WHITE);
        createEventButton.setGraphic(plusIcon);
        createEventButton.setContentDisplay(ContentDisplay.LEFT);
        createEventButton.getStyleClass().add("btn-primary");

        Label sectionTitle = new Label("Aktive Lerngruppen & Events");
        sectionTitle.getStyleClass().add("section-header-label");

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerRow.getChildren().addAll(sectionTitle, spacer, createEventButton);

        section.getChildren().add(headerRow);

        if (events.isEmpty()) {
            Label noEvents = new Label("No events scheduled");
            noEvents.getStyleClass().add("label-italic");
            section.getChildren().add(noEvents);
        } else {
            // Display up to two events
            int eventsToDisplay = Math.min(MAX_DISPLAYED_EVENTS, events.size());
            for (int i = 0; i < eventsToDisplay; i++) {
                VBox eventBox = createEventBox(events.get(i));
                section.getChildren().add(eventBox);
            }
        }
        return section;
    }

    private static VBox createEventBox(Event event) {
        VBox eventBox = new VBox(EVENT_BOX_SPACING);
        eventBox.getStyleClass().add("event-box");

        Label title = new Label(event.title());
        title.getStyleClass().add("event-title");

        Label description = new Label(event.description());
        description.getStyleClass().add("event-description");
        description.setWrapText(true);

        HBox timeBox = new HBox(TIME_BOX_SPACING);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon clockIcon = new FontIcon(BootstrapIcons.CLOCK);
        Label timeRange = new Label(
                event.startDate().format(TIME_FORMATTER) + " - " +
                        event.endDate().format(TIME_FORMATTER)
        );
        timeRange.getStyleClass().add("event-time");
        timeBox.getChildren().addAll(clockIcon, timeRange);

        HBox tagsBox = new HBox(TAGS_BOX_SPACING);


        for (String tag : event.tags()) {
            HBox tagBox = new HBox(TAG_BOX_SPACING);
            tagBox.getStyleClass().add("event-tag");
            tagBox.setAlignment(Pos.CENTER_LEFT);

            Label tagLabel = new Label(tag);

            FontIcon tagIcon = new FontIcon(BootstrapIcons.TAG_FILL);
            tagIcon.setIconColor(Color.DARKGOLDENROD);

            tagBox.getChildren().addAll(tagIcon, tagLabel);
            tagsBox.getChildren().add(tagBox);
        }


        eventBox.getChildren().addAll(title, description, timeBox, tagsBox);
        return eventBox;
    }
}
