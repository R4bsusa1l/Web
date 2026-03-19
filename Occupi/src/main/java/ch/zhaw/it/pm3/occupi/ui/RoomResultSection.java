package ch.zhaw.it.pm3.occupi.ui;

import ch.zhaw.it.pm3.occupi.model.BuildingInfo;
import ch.zhaw.it.pm3.occupi.model.Room;
import ch.zhaw.it.pm3.occupi.search.PaginatedSearchResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.fontawesome6.FontAwesomeBrands;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A helper class responsible for creating and managing the UI components that display room search results.
 * <p>
 * This class provides methods to create a visual representation of room search results, including:
 * - Room cards with expandable details
 * - Header sections with room names and occupancy information
 * - Progress bars indicating room occupancy levels
 * - Detail views showing equipment, events, and notes for each room
 * <p>
 * The UI components are created using JavaFX controls and layouts, with styling applied through CSS classes.
 * Each room is displayed as a card that can be expanded to show additional details.
 */
@SuppressWarnings("ChainedMethodCall")
public class RoomResultSection {
    private static final double SECTION_SPACING = 15;
    private static final double SECTION_PADDING = 15;
    private static final double HEADER_SPACING = 10;
    private static final double LEGEND_SPACING = 10;
    private static final double CARD_SPACING = 10;
    private static final double HEADER_INNER_SPACING = 20;
    private static final double LOCATION_SPACING = 5;
    private static final double FOOTER_SPACING = 15;
    private static final double LOCATION_GRAPHIC_GAP = 8;
    private static final double PROGRESS_BAR_HEIGHT = 10;
    private static final double PAGINATION_SPACING = 15;
    private static final double PAGINATION_BUTTON_SPACING = 10;

    private static final String EXPAND_TEXT = "Click to expand";
    private static final String COLLAPSE_TEXT = "Click to collapse";

    private Consumer<Integer> onPageChange;

    /**
     * Default constructor.
     */
    public RoomResultSection() {}

    /**
     * Creates a complete paginated room result section based on the provided search result.
     * <p>
     * This method generates a vertical layout container (VBox) that displays rooms
     * from the current page of search results, organized by building, with pagination controls.
     *
     * @param paginatedResult the paginated search result containing buildings and their rooms
     * @param onPageChange    callback function to handle page changes
     * @return a VBox containing room result cards and pagination controls
     */
    public VBox createPaginatedRoomResultSection(PaginatedSearchResult paginatedResult, Consumer<Integer> onPageChange) {
        this.onPageChange = onPageChange;

        VBox roomResultSection = new VBox();
        roomResultSection.setId("room-result-section");
        roomResultSection.setFillWidth(true);
        roomResultSection.setSpacing(SECTION_SPACING);
        roomResultSection.setPadding(new Insets(SECTION_PADDING, SECTION_PADDING, SECTION_PADDING, SECTION_PADDING));

        roomResultSection.getChildren().add(createPaginatedHeaderSection(paginatedResult));

        for (Map.Entry<BuildingInfo, List<Room>> entry : paginatedResult.searchResult().entrySet()) {
            BuildingInfo buildingInfo = entry.getKey();
            List<Room> searchedRooms = entry.getValue();

            for (Room room : searchedRooms) {
                HBox locationInfo = createLocationInfoSection(room, buildingInfo);
                VBox searchResultRow = createSearchResultRow(room, locationInfo);
                roomResultSection.getChildren().add(searchResultRow);
            }
        }

        // pagination controls at the bottom
        if (paginatedResult.totalPages() > 1) {
            HBox paginationControls = createPaginationControls(paginatedResult);
            roomResultSection.getChildren().add(paginationControls);
        }

        return roomResultSection;
    }


    /**
     * Creates the header section for paginated results, showing pagination info and legend.
     *
     * @param paginatedResult the paginated search result
     * @return an HBox containing the header section
     */
    private HBox createPaginatedHeaderSection(PaginatedSearchResult paginatedResult) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(HEADER_SPACING);

        int pageStart = paginatedResult.currentPage() * paginatedResult.pageSize() + 1;
        int pageEnd = Math.min(pageStart + paginatedResult.pageSize() - 1, paginatedResult.totalResults());

        String headerText = String.format("Verfügbare Räume (%d-%d von %d)",
                pageStart, pageEnd, paginatedResult.totalResults());
        Label headerLabel = new Label(headerText);
        headerLabel.getStyleClass().add("section-header-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox legend = createRoomStateLegend();

        header.getChildren().addAll(headerLabel, spacer, legend);
        return header;
    }

    /**
     * Creates a legend indicating the meaning of different room occupancy status icons.
     *
     * @return an HBox containing the room state legend
     */
    private HBox createRoomStateLegend() {
        HBox legend = new HBox(LEGEND_SPACING);
        FontIcon freeIcon = new FontIcon(BootstrapIcons.CIRCLE_FILL);
        freeIcon.getStyleClass().add("icon-free");
        Label freeLabel = new Label("Frei", freeIcon);

        FontIcon occupiedIcon = new FontIcon(BootstrapIcons.CIRCLE_FILL);
        occupiedIcon.getStyleClass().add("icon-occupied");
        Label occupiedLabel = new Label("Belegt", occupiedIcon);

        FontIcon fullIcon = new FontIcon(BootstrapIcons.CIRCLE_FILL);
        fullIcon.getStyleClass().add("icon-full");
        Label fullLabel = new Label("Voll", fullIcon);

        FontIcon reservedIcon = new FontIcon(BootstrapIcons.CIRCLE_FILL);
        reservedIcon.getStyleClass().add("icon-reserved");
        Label reservedLabel = new Label("Reserviert", reservedIcon);

        FontIcon eventIcon = new FontIcon(BootstrapIcons.CIRCLE_FILL);
        eventIcon.getStyleClass().add("icon-event");
        Label eventLabel = new Label("Event", eventIcon);

        legend.getChildren().addAll(freeLabel, occupiedLabel, fullLabel, reservedLabel, eventLabel);
        return legend;
    }

    /**
     * Creates pagination controls for navigating between pages.
     *
     * @param paginatedResult the paginated search result
     * @return an HBox containing pagination controls
     */
    private HBox createPaginationControls(PaginatedSearchResult paginatedResult) {
        HBox pagination = new HBox(PAGINATION_BUTTON_SPACING);
        pagination.setAlignment(Pos.CENTER);
        pagination.setSpacing(PAGINATION_SPACING);
        pagination.setPadding(new Insets(SECTION_PADDING, 0, 0, 0));

        Button buttonPrevious = createPreviousButton(paginatedResult);

        String pageInfo = String.format("Seite %d von %d",
                paginatedResult.getDisplayPageNumber(),
                paginatedResult.totalPages());
        Label labelPageInfo = new Label(pageInfo);


        Button buttonNext = createNextButton(paginatedResult);

        pagination.getChildren().addAll(buttonPrevious, labelPageInfo, buttonNext);
        return pagination;
    }

    /** Creates the "Next" button for pagination.
     *
     * @param paginatedResult the paginated search result
     * @return a Button for navigating to the next page
     */
    private Button createNextButton(PaginatedSearchResult paginatedResult) {
        FontIcon nextIcon = new FontIcon(BootstrapIcons.CHEVRON_COMPACT_RIGHT);
        nextIcon.setIconColor(Color.WHITE);

        Button buttonNext = new Button("Weiter");

        buttonNext.setGraphic(nextIcon);
        buttonNext.setContentDisplay(ContentDisplay.RIGHT);

        buttonNext.getStyleClass().add("btn-secondary");
        buttonNext.setDisable(!paginatedResult.hasNext());
        buttonNext.setFocusTraversable(false);
        buttonNext.setOnAction(e -> {
            if (onPageChange != null && paginatedResult.hasNext()) {
                onPageChange.accept(paginatedResult.currentPage() + 1);
            }
        });
        return buttonNext;
    }

    /** Creates the "Previous" button for pagination.
     *
     * @param paginatedResult the paginated search result
     * @return a Button for navigating to the previous page
     */
    private Button createPreviousButton(PaginatedSearchResult paginatedResult) {
        FontIcon previousIcon = new FontIcon(BootstrapIcons.CHEVRON_COMPACT_LEFT);
        previousIcon.setIconColor(Color.WHITE);

        Button buttonPrevious = new Button("Zurück");

        buttonPrevious.setGraphic(previousIcon);
        buttonPrevious.getStyleClass().add("btn-secondary");
        buttonPrevious.setDisable(!paginatedResult.hasPrevious());
        buttonPrevious.setFocusTraversable(false);
        buttonPrevious.setOnAction(e -> {
            if (onPageChange != null && paginatedResult.hasPrevious()) {
                onPageChange.accept(paginatedResult.currentPage() - 1);
            }
        });

        return buttonPrevious;
    }

    /**
     * Creates a search result row (card) for the given room, including expandable details.
     *
     * @param room the room to display
     * @param locationInfo the location information for the room
     * @return a VBox representing the search result row
     */
    private VBox createSearchResultRow(Room room, HBox locationInfo) {
        VBox card = createCardContainer();

        // Main content that's always visible
        VBox mainContent = new VBox(CARD_SPACING);

        HBox header = createHeaderSection(room);

        ProgressBar occupancyBar = createOccupancyProgressBar(room);
        HBox footer = createFooterSection(room);

        // Add expand/collapse indicator
        Label expandIndicator = new Label(EXPAND_TEXT);
        toggleExpandIndicator(expandIndicator, false);

        mainContent.getChildren().addAll(header, locationInfo, occupancyBar, footer, expandIndicator);

        card.getChildren().add(mainContent);

        // Add click event to toggle detail view - creates detail view lazily on first expand
        card.setOnMouseClicked(event -> toggleDetailView(card, room, expandIndicator));

        return card;
    }

    /**
     * Toggles the visibility of the detail view for a room card and updates the expand/collapse indicator.
     * Creates the detail view lazily on the first expansion to improve performance.
     *
     * @param card            the VBox containing the card
     * @param room            the room to display details for
     * @param expandIndicator the label indicating expand/collapse state
     */
    private void toggleDetailView(VBox card, Room room, Label expandIndicator) {
        // Check if the detail view already exists (will be at index 1 if it exists)
        VBox detailView;
        if (card.getChildren().size() > 1) {
            detailView = (VBox) card.getChildren().get(1);
            boolean isNotVisible = !detailView.isVisible();
            detailView.setVisible(isNotVisible);
            detailView.setManaged(isNotVisible);

            toggleExpandIndicator(expandIndicator, isNotVisible);

            if (isNotVisible) {
                detailView.applyCss();
                detailView.layout();
            }
        } else {
            detailView = RoomDetailView.createDetailView(room);
            card.getChildren().add(detailView);

            toggleExpandIndicator(expandIndicator, true);

            detailView.applyCss();
            detailView.layout();
        }
    }

    /**
     * Updates the expand/collapse indicator label based on the current state.
     *
     * @param expandIndicator the label to update
     * @param isExpanded      true if the detail view is expanded, false otherwise
     */
    private void toggleExpandIndicator(Label expandIndicator, boolean isExpanded) {
        expandIndicator.setText(isExpanded ? COLLAPSE_TEXT : EXPAND_TEXT);
        FontIcon caretIcon = new FontIcon(isExpanded ? BootstrapIcons.CARET_UP_FILL : BootstrapIcons.CARET_DOWN_FILL);
        expandIndicator.setGraphic(caretIcon);
    }

    /**
     * Creates a styled card container VBox for a search result row.
     *
     * @return a VBox styled as a card
     */
    private VBox createCardContainer() {
        VBox card = new VBox(CARD_SPACING);
        card.getStyleClass().add("card");
        return card;
    }

    /**
     * Creates the header section for a room card, including the room name and occupancy label.
     *
     * @param room the room to display
     * @return an HBox containing the header section
     */
    private HBox createHeaderSection(Room room) {
        HBox header = new HBox(HEADER_INNER_SPACING);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(room.getName());
        title.getStyleClass().add("label-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label occupancy = createOccupancyLabel(room);

        header.getChildren().addAll(title, spacer, occupancy);
        return header;
    }

    /**
     * Creates a label displaying the occupancy percentage and applies a style based on room status.
     *
     * @param room the room whose occupancy is displayed
     * @return a styled Label for occupancy
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    private Label createOccupancyLabel(Room room) {
        int occupancyPercentage = (int) (((double) room.getOccupancy() / room.getSeats()) * 100);
        String occupancyText = "Auslastung " + occupancyPercentage + "%";
        Label occupancy = new Label();
        occupancy.getStyleClass().add("occupancy-label");

        // Add specific style class based on room status
        switch (room.getStatus()) {
            case FREE -> occupancy.getStyleClass().add("occupancy-free");
            case RESERVED -> {
                occupancy.getStyleClass().add("occupancy-reserved");
                occupancyText = "Reserviert";
            }
            case OCCUPIED -> occupancy.getStyleClass().add("occupancy-occupied");
            case FULL -> occupancy.getStyleClass().add("occupancy-full");
            case EVENT -> occupancy.getStyleClass().add("occupancy-event");
        }

        occupancy.setText(occupancyText);
        return occupancy;
    }

    /**
     * Creates the location info section for a room card, including building, floor, and seat count.
     *
     * @param room the room to display
     * @param buildingInfo the building information
     * @return an HBox containing location information
     */
    private HBox createLocationInfoSection(Room room, BuildingInfo buildingInfo) {
        HBox locationInfo = new HBox(LOCATION_SPACING);
        locationInfo.setAlignment(Pos.CENTER_LEFT);

        FontIcon locationIcon = new FontIcon(BootstrapIcons.HOUSE_FILL);

        String locationLabelText = String.format("Gebäude %s, Stockwerk %s", buildingInfo.shortName(), room.getFloor());
        Label location = new Label();
        location.setText(locationLabelText);
        location.setGraphic(locationIcon);
        location.setGraphicTextGap(LOCATION_GRAPHIC_GAP);
        location.getStyleClass().add("label-accent");


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label seats = new Label(room.getOccupancy() + "/" + room.getSeats());
        seats.getStyleClass().add("label-accent");

        locationInfo.getChildren().addAll(location, spacer, seats);
        return locationInfo;
    }

    /**
     * Creates a progress bar representing the occupancy ratio of a room and applies a style based on status.
     *
     * @param room the room whose occupancy is displayed
     * @return a styled ProgressBar for occupancy
     */
    private ProgressBar createOccupancyProgressBar(Room room) {
        double occupancyRatio = (double) room.getOccupancy() / room.getSeats();

        ProgressBar progress = new ProgressBar(occupancyRatio);
        progress.setPrefWidth(Double.MAX_VALUE);
        progress.setMinHeight(PROGRESS_BAR_HEIGHT);
        progress.setMaxHeight(PROGRESS_BAR_HEIGHT);

        // Add specific style class based on room status
        switch (room.getStatus()) {
            case FREE -> progress.getStyleClass().add("progress-bar-free");
            case RESERVED -> progress.getStyleClass().add("progress-bar-reserved");
            case OCCUPIED -> progress.getStyleClass().add("progress-bar-occupied");
            case FULL -> progress.getStyleClass().add("progress-bar-full");
            case EVENT -> progress.getStyleClass().add("progress-bar-event");
        }

        return progress;
    }

    /**
     * Creates the footer section for a room card, including WiFi quality and accessibility.
     *
     * @param room the room to display
     * @return an HBox containing the footer section
     */
    private HBox createFooterSection(Room room) {
        HBox footer = new HBox(FOOTER_SPACING);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label wifi = createWifiQualityLabel(room);
        footer.getChildren().add(wifi);

        if (room.isAccessible()) {
            Label accessibility = createAccessibilityLabel();
            footer.getChildren().add(accessibility);
        }

        return footer;
    }

    /**
     * Creates a label representing the WiFi quality of a room as a star rating.
     *
     * @param room the room whose WiFi quality is displayed
     * @return a Label with WiFi quality stars
     */
    private Label createWifiQualityLabel(Room room) {
        int wifiQuality = 0;
        if (room.getEquipment() != null) {
            wifiQuality = room.getEquipment().wifiQuality();
        }

        Label wifi = new Label();
        wifi.getStyleClass().add("info-label");

        HBox starsBox = new HBox(2);
        starsBox.setAlignment(Pos.CENTER_LEFT);

        wifi.setText("WLAN");
        wifi.setContentDisplay(ContentDisplay.RIGHT);
        wifi.setGraphicTextGap(8);

        for (int i = 0; i < 5; i++) {
            FontIcon starIcon = new FontIcon(i < wifiQuality ? BootstrapIcons.STAR_FILL : BootstrapIcons.STAR);
            if (i < wifiQuality) {
                starIcon.setIconColor(Color.DARKGOLDENROD);
            } else {
                starIcon.setIconColor(Color.LIGHTGRAY);
            }
            starsBox.getChildren().add(starIcon);
        }


        wifi.setGraphic(starsBox);

        return wifi;
    }

    /**
     * Creates a label indicating that the room is accessible.
     *
     * @return a Label indicating accessibility
     */
    private Label createAccessibilityLabel() {
        FontIcon accessibilityIcon = new FontIcon(FontAwesomeBrands.ACCESSIBLE_ICON);
        accessibilityIcon.setIconColor(Color.DODGERBLUE);
        Label accessibility = new Label("Barrierefrei");
        accessibility.getStyleClass().add("info-label");

        accessibility.setGraphic(accessibilityIcon);
        accessibility.setContentDisplay(ContentDisplay.RIGHT);
        accessibility.setGraphicTextGap(8);
        return accessibility;
    }
}
