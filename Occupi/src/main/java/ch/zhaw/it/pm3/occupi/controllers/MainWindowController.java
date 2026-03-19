package ch.zhaw.it.pm3.occupi.controllers;

import ch.zhaw.it.pm3.occupi.model.Building;
import ch.zhaw.it.pm3.occupi.model.RoomInfrastructure;
import ch.zhaw.it.pm3.occupi.model.RoomType;
import ch.zhaw.it.pm3.occupi.search.FilterCriteriaDTO;
import ch.zhaw.it.pm3.occupi.search.PaginatedSearchResult;
import ch.zhaw.it.pm3.occupi.ui.RoomResultSection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Controller for the main window of the Occupi application.
 * <p>
 * This class manages the user interface logic for searching, displaying, and interacting with room data.
 * It handles user actions such as searching for rooms, resetting filters, and expanding/collapsing room details.
 * The controller interacts with the model to retrieve room, event, and equipment data, and updates the UI accordingly.
 * </p>
 */
@SuppressWarnings({"ClassWithTooManyFields", "ChainedMethodCall"})
public class MainWindowController {
    private static final String FILTER_SHOW_TEXT = "Filter anzeigen";
    private static final String FILTER_HIDE_TEXT = "Filter verbergen";
    private static final String ALL_FLOORS_TEXT = "Alle Stockwerke";
    private static final String ALL_LOCATIONS_TEXT = "Alle Standorte";
    private static final String ALL_BUILDINGS_TEXT = "Alle Gebäude";
    private static final String ALL_ROOM_TYPES_TEXT = "Alle Raumtypen";
    private static final String ANY_PERSON_COUNT_TEXT = "Beliebige Personenzahl";
    private static final String ANY_PERSON_COUNT_FILTER = "Beliebig";
    private static final String EMPTY_STRING = "";

    private static final String PERSON_COUNT_5 = "5";
    private static final String PERSON_COUNT_10 = "10";
    private static final String PERSON_COUNT_20 = "20";
    private static final String PERSON_COUNT_30 = "30";
    private static final String PERSON_COUNT_50 = "50";
    private static final String PERSON_COUNT_100 = "100";

    @FXML
    private TextField tfRoom;

    @FXML
    private ComboBox<String> cbLocation;

    @FXML
    private ComboBox<String> cbBuilding;

    @FXML
    private ComboBox<String> cbRoomType;

    @FXML
    private ComboBox<String> cbFloor;

    @FXML
    private ComboBox<String> cbInfrastructure;

    @FXML
    private ComboBox<String> cbPersonCount;

    @FXML
    private ScrollPane searchResultPane;

    @FXML
    private VBox filterSection;

    @FXML
    private Button btnToggleFilters;

    @FXML
    private Label notificationLabel;

    private BuildingController buildingController;

    // Pagination state
    private FilterCriteriaDTO currentCriteria;
    private int currentPage;
    private static final int PAGE_SIZE = 10;

    /**
     * Default Constructor of MainWindowController
     */
    public MainWindowController() {}

    /**
     * Sets the BuildingController instance for this controller.
     *
     * @param buildingController the BuildingController to set
     */
    public void setBuildingController(BuildingController buildingController) {
        this.buildingController = buildingController;
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     */
    public void initializeUI() {
        Objects.requireNonNull(buildingController, "BuildingController must be set before initializing UI");
        Platform.runLater(() -> {
            if (btnToggleFilters != null) {
                btnToggleFilters.requestFocus();
            }
        });
        cbFloor.setDisable(true);

        populateLocationComboBox();
        populateBuildingComboBox();
        populateRoomTypeComboBox();
        populatePersonCountComboBox();
        populateInfrastructureComboBox();

        notificationLabel.setOnMouseClicked(event -> refreshSearchResults());

        // on exit shutdown the building controller to free threads
        Runtime.getRuntime().addShutdownHook(new Thread(buildingController::shutdown));
    }

    /**
     * Toggles the visibility of the filter section and updates the toggle button text.
     */
    @FXML
    private void toggleFilters() {
        if (filterSection != null) {
            boolean isVisible = filterSection.isVisible();

            filterSection.setManaged(!isVisible);
            filterSection.setVisible(!isVisible);

            if (btnToggleFilters != null) {
                btnToggleFilters.setText(isVisible ? FILTER_SHOW_TEXT : FILTER_HIDE_TEXT);
            }
        }
    }

    /**
     * Sets up the search action to be triggered when the Enter key is pressed in the room text field.
     */
    @FXML
    private void searchOnEnterPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            searchRooms();
        }
    }

    /**
     * Searches for rooms based on the current filter criteria and displays the results in the search result pane.
     */
    @FXML
    private void searchRooms() {
        currentPage = 0; // Reset to first page on new search
        performSearch();
    }

    /**
     * Performs the actual search and displays results.
     */
    private void performSearch() {
        currentCriteria = buildFilterCriteria();
        System.out.println("Search rooms with criteria: " + currentCriteria);
        PaginatedSearchResult paginatedResult = buildingController.searchRoomsPaginated(currentCriteria, currentPage, PAGE_SIZE);

        setupSearchObserver();
        displaySearchResults(paginatedResult);
        hideFiltersIfVisible();
    }

    /**
     * Builds filter criteria from the current UI selections.
     *
     * @return the filter criteria based on current UI state
     */
    private FilterCriteriaDTO buildFilterCriteria() {
        String searchQuery = tfRoom.getText();

        String location = normalizeFilterValue(cbLocation.getSelectionModel().getSelectedItem(), ALL_LOCATIONS_TEXT);
        String building = normalizeFilterValue(cbBuilding.getSelectionModel().getSelectedItem(), ALL_BUILDINGS_TEXT);
        String floor = normalizeFilterValue(cbFloor.getSelectionModel().getSelectedItem(), ALL_FLOORS_TEXT);
        RoomType roomType = RoomType.getByName(cbRoomType.getSelectionModel().getSelectedItem());
        int capacity = parseCapacity(cbPersonCount.getSelectionModel().getSelectedItem());
        Set<RoomInfrastructure> roomInfrastructures = parseInfrastructure(cbInfrastructure.getSelectionModel().getSelectedItem());

        return new FilterCriteriaDTO(
                searchQuery,
                location,
                building,
                roomType,
                floor,
                capacity,
                roomInfrastructures
        );
    }

    /**
     * Normalizes the filter value by converting the "all" option to null.
     *
     * @param value     the selected filter value
     * @param allOption the text representing the "all" option
     * @return null if the value is the "all" option, otherwise returns the original value
     */
    private String normalizeFilterValue(String value, String allOption) {
        return allOption.equals(value) ? null : value;
    }

    /**
     * Parses the capacity from the person count selection.
     *
     * @param personCountStr the selected person count string
     * @return the parsed capacity, or 0 if no specific capacity is selected
     */
    private int parseCapacity(String personCountStr) {
        int result = 0;

        if (personCountStr != null && !personCountStr.contains(ANY_PERSON_COUNT_FILTER)) {
            result = Integer.parseInt(personCountStr);
        }

        return result;
    }

    /**
     * Parses the infrastructure from the infrastructure selection.
     *
     * @param selectedInfrastructure the selected infrastructure string
     * @return a set containing the selected infrastructure, or an empty set if none selected
     */
    private Set<RoomInfrastructure> parseInfrastructure(String selectedInfrastructure) {
        Set<RoomInfrastructure> roomInfrastructures = EnumSet.noneOf(RoomInfrastructure.class);
        if (selectedInfrastructure != null && !selectedInfrastructure.isEmpty()) {
            RoomInfrastructure infrastructure = RoomInfrastructure.getByName(selectedInfrastructure);
            roomInfrastructures.add(infrastructure);
        }
        return roomInfrastructures;
    }

    /**
     * Sets up an observer to automatically update results when building data changes.
     */
    private void setupSearchObserver() {
        buildingController.addObserver(() -> Platform.runLater(this::showDataUpdateNotification));
    }

    /**
     * Displays the search results in the UI.
     */
    private void displaySearchResults(PaginatedSearchResult paginatedResult) {
        updateSearchResultPane(paginatedResult);
        searchResultPane.setVvalue(0);
    }

    /**
     * Updates the search result pane with the given paginated results.
     *
     * @param paginatedResult the search results to display
     */
    private void updateSearchResultPane(PaginatedSearchResult paginatedResult) {
        RoomResultSection sectionBuilder = new RoomResultSection();
        VBox resultUi = sectionBuilder.createPaginatedRoomResultSection(paginatedResult, this::onPageChange);
        searchResultPane.setContent(resultUi);
    }

    /**
     * Hides the filter section if it's currently visible.
     */
    private void hideFiltersIfVisible() {
        if (filterSection != null && filterSection.isVisible()) {
            toggleFilters();
        }
    }

    /**
     * Handles page change events from pagination controls.
     *
     * @param newPage the new page to navigate to
     */
    private void onPageChange(int newPage) {
        currentPage = newPage;

        if (currentCriteria != null) {
            PaginatedSearchResult paginatedResult = buildingController.searchRoomsPaginated(currentCriteria, currentPage, PAGE_SIZE);
            updateSearchResultPane(paginatedResult);
            searchResultPane.setVvalue(0);
        }
    }

    /**
     * Handles the reset action, clearing all filters and search results.
     */
    @FXML
    private void handleReset() {
        System.out.println("Reset");

        tfRoom.clear();

        cbLocation.getSelectionModel().clearSelection();
        cbBuilding.getSelectionModel().clearSelection();
        cbRoomType.getSelectionModel().clearSelection();
        cbFloor.getSelectionModel().clearSelection();
        cbInfrastructure.getSelectionModel().clearSelection();
        cbPersonCount.getSelectionModel().clearSelection();

        populateLocationComboBox();
        populateBuildingComboBox();
        populateRoomTypeComboBox();
        populatePersonCountComboBox();
        populateInfrastructureComboBox();

        cbFloor.setDisable(true);
        cbFloor.getItems().clear();
    }

    /**
     * Filters buildings based on the selected city in the city combo box.
     */
    @FXML
    private void filterBuildingsByLocation() {
        String selectedLocation = cbLocation.getSelectionModel().getSelectedItem();

        if (isLocationSelected(selectedLocation)) {
            resetBuildingComboBox();
            populateBuildingsForLocation(selectedLocation);
        } else {
            populateBuildingComboBox();
        }
    }

    /**
     * Checks if a specific location is selected (not null or empty).
     *
     * @param location the location to check
     * @return true if a specific location is selected, false otherwise
     */
    private boolean isLocationSelected(String location) {
        return location != null && !location.isEmpty();
    }

    /**
     * Resets the building combo box with the "All Buildings" option selected.
     */
    private void resetBuildingComboBox() {
        cbBuilding.getItems().clear();
        cbBuilding.getItems().add(ALL_BUILDINGS_TEXT);
        cbBuilding.getSelectionModel().selectFirst();
    }

    /**
     * Populates the building combo box with buildings that belong to the specified location.
     *
     * @param selectedLocation the location to filter buildings by
     */
    private void populateBuildingsForLocation(String selectedLocation) {
        buildingController.getBuildingNames().forEach(name ->
                addBuildingIfInLocation(name, selectedLocation)
        );
    }

    /**
     * Adds a building to the combo box if it belongs to the specified location.
     *
     * @param buildingName     the name of the building to check
     * @param selectedLocation the location to match against
     */
    private void addBuildingIfInLocation(String buildingName, String selectedLocation) {
        Optional<Building> optionalBuilding = buildingController.getBuildingByName(buildingName);

        optionalBuilding.ifPresent(building -> {
            String city = building.buildingInfo().city();

            if (selectedLocation.equals(city)) {
                cbBuilding.getItems().add(buildingName);
            }
        });
    }

    /**
     * Filters floors based on the selected building in the building combo box.
     */
    @FXML
    private void filterFloorsByBuilding() {
        String selectedBuilding = cbBuilding.getSelectionModel().getSelectedItem();

        if (selectedBuilding == null || selectedBuilding.isEmpty()) {
            cbFloor.getItems().clear();
            cbFloor.setDisable(true);

        } else {
            cbFloor.setDisable(false);
            cbFloor.getItems().clear();
            cbFloor.getSelectionModel().selectFirst();
            cbFloor.getItems().add(ALL_FLOORS_TEXT);

            Optional<Building> buildingOptional = buildingController.getBuildingByName(selectedBuilding);
            buildingOptional.ifPresent(building -> building.floors().forEach(floor ->
                    cbFloor.getItems().add(floor)));

            //sort cbFloor items except for "Alle Stockwerke"
            cbFloor.getItems().sort((floor1, floor2) -> {
                if (ALL_FLOORS_TEXT.equals(floor1)) {
                    return -1;
                } else if (ALL_FLOORS_TEXT.equals(floor2)) {
                    return 1;
                } else {
                    return floor1.compareTo(floor2);
                }
            });
        }
    }

    /**
     * Populates the city combo box with available locations from the building controller.
     */
    private void populateLocationComboBox() {
        cbLocation.getItems().clear();

        cbLocation.getItems().add(ALL_LOCATIONS_TEXT);
        cbLocation.getSelectionModel().selectFirst();

        Set<String> locations = buildingController.getLocations();
        locations.forEach(cbLocation.getItems()::add);
    }

    /**
     * Populates the building combo box with available building names from the building controller.
     */
    private void populateBuildingComboBox() {
        cbBuilding.getItems().clear();

        cbBuilding.getItems().add(ALL_BUILDINGS_TEXT);
        cbBuilding.getSelectionModel().selectFirst();

        Set<String> buildingNames = buildingController.getBuildingNames();
        buildingNames.forEach(cbBuilding.getItems()::add);
    }

    /**
     * Populates the room type combo box with available room types.
     */
    private void populateRoomTypeComboBox() {
        cbRoomType.getItems().clear();
        cbRoomType.getItems().add(ALL_ROOM_TYPES_TEXT);
        cbRoomType.getSelectionModel().selectFirst();

        Arrays.stream(RoomType.values())
                .map(RoomType::getDisplayName)
                .forEach(cbRoomType.getItems()::add);
    }

    /**
     * Populates the person count combo box with predefined options.
     */
    private void populatePersonCountComboBox() {
        cbPersonCount.getItems().clear();
        cbPersonCount.getItems().add(ANY_PERSON_COUNT_TEXT);
        cbPersonCount.getSelectionModel().selectFirst();
        cbPersonCount.getItems().addAll(PERSON_COUNT_5, PERSON_COUNT_10, PERSON_COUNT_20, PERSON_COUNT_30, PERSON_COUNT_50, PERSON_COUNT_100);
    }

    /**
     * Populates the infrastructure combo box with available room infrastructures.
     */
    private void populateInfrastructureComboBox() {
        cbInfrastructure.getItems().clear();
        cbInfrastructure.getItems().add(EMPTY_STRING);
        cbInfrastructure.getSelectionModel().selectFirst();

        for (RoomInfrastructure infrastructure : RoomInfrastructure.values()) {
            cbInfrastructure.getItems().add(infrastructure.getName());
        }
    }

    /**
     * Shows the notification badge to indicate new data is available.
     */
    private void showDataUpdateNotification() {
        if (notificationLabel != null && currentCriteria != null) {
            notificationLabel.setVisible(true);
            notificationLabel.setManaged(true);
        }
    }

    /**
     * Hides the notification badge.
     */
    private void hideDataUpdateNotification() {
        if (notificationLabel != null) {
            notificationLabel.setVisible(false);
            notificationLabel.setManaged(false);
        }
    }

    /**
     * Refreshes the search results with the current criteria and hides the notification.
     */
    private void refreshSearchResults() {
        hideDataUpdateNotification();
        performSearch();
    }
}