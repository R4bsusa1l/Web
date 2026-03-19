package ch.zhaw.it.pm3.occupi.ui;

import ch.zhaw.it.pm3.occupi.history.simulation.FileHistoryAPI;
import ch.zhaw.it.pm3.occupi.model.Room;
import ch.zhaw.it.pm3.occupi.model.RoomEquipment;
import ch.zhaw.it.pm3.occupi.model.RoomInfrastructure;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.nio.file.Path;

import static ch.zhaw.it.pm3.occupi.model.RoomInfrastructure.AIR_CONDITIONER;
import static ch.zhaw.it.pm3.occupi.model.RoomInfrastructure.BLACKBOARD;
import static ch.zhaw.it.pm3.occupi.model.RoomInfrastructure.FLIPCHART;
import static ch.zhaw.it.pm3.occupi.model.RoomInfrastructure.PROJECTOR;
import static ch.zhaw.it.pm3.occupi.model.RoomInfrastructure.WHITEBOARD;

/**
 * The RoomDetailView class is responsible for creating a detailed display for a given room.
 * It provides an organized layout consisting of tabs that represent different types of room-related information.
 */
@SuppressWarnings("ChainedMethodCall")
public class RoomDetailView {

    private static final Path HISTORY_DATA_JSON_PATH = Path.of("src", "main", "resources", "jsonData", "historyData", "HistoryData.json");

    /**
     * Private Constructor to prevent instantiation
     */
    private RoomDetailView() {}

    /**
     * Creates a detailed view for a given room, including tabs for information, events, and notes.
     * This method initializes and structures the layout using a VBox and TabPane, adding styled child elements.
     *
     * @param room the room for which the detail view is created, containing the data to populate the tabs
     * @return a VBox representing the detailed view layout for the room
     */
    public static VBox createDetailView(Room room) {
        VBox detailView = new VBox(15);
        detailView.getStyleClass().add("detail-view");

        TabPane tabPane = new TabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMinHeight(450);
        tabPane.setPrefWidth(Double.MAX_VALUE);
        tabPane.getStyleClass().add("detail-tabs");

        // Make tabs equal width and fill the full width
        tabPane.tabMinWidthProperty().bind(tabPane.widthProperty().divide(3).subtract(20));
        tabPane.tabMaxWidthProperty().bind(tabPane.widthProperty().divide(3).subtract(20));

        // Equipment tab
        Tab informationTab = new Tab("Informationen");
        VBox infoSection = createInfoSection(room);
        informationTab.setContent(infoSection);

        // Events tab
        Tab eventTab = EventTab.createEventTab(room);

        // Notes tab
        Tab statsTab = new Tab("Statistiken");
        VBox statsSection = createStatsSection(room);
        statsTab.setContent(statsSection);

        // Add tabs to TabPane
        tabPane.getTabs().addAll(informationTab, eventTab, statsTab);

        // Add TabPane to detail view
        detailView.getChildren().add(tabPane);

        return detailView;
    }

    /**
     * Creates the information section for the room, including description, equipment, and technical equipment.
     *
     * @param room the room whose information is to be displayed
     * @return a VBox containing the information section
     */
    private static VBox createInfoSection(Room room) {
        VBox section = new VBox(15);
        section.getStyleClass().add("section-content");

        if (room.getEquipment() != null) {
            VBox descriptionSection = createDescriptionSection(room);
            VBox equipmentSection = createEquipmentSection(room);
            VBox techEquipmentSection = createTechEquipmentSection(room);

            section.getChildren().addAll(descriptionSection, equipmentSection, techEquipmentSection);
        } else {
            Label noEquipment = new Label("No equipment information available");
            noEquipment.getStyleClass().add("label-italic");
            section.getChildren().add(noEquipment);
        }
        return section;
    }

    /**
     * Creates the technical equipment section for the room, displaying projector and writing utilities.
     *
     * @param room the room whose technical equipment is to be displayed
     * @return a VBox containing the technical equipment section
     */
    private static VBox createTechEquipmentSection(Room room) {
        VBox techEquipmentSection = new VBox(10);
        techEquipmentSection.getStyleClass().add("info-section");

        Label ausstattung = new Label("Technische Ausstattung");
        ausstattung.getStyleClass().addAll("label-bold");

        HBox equipmentTags = new HBox(8);
        equipmentTags.getStyleClass().add("equipment-tags");

        for (RoomInfrastructure infra : room.getEquipment().roomInfrastructure()) {
            HBox tagBox = new HBox(5);
            tagBox.getStyleClass().add("tag-box");
            tagBox.setAlignment(Pos.CENTER_LEFT);

            FontIcon tagIcon = new FontIcon(BootstrapIcons.DISPLAY_FILL);
            Label tagText = new Label();

            switch (infra) {
                case BLACKBOARD -> tagText.setText(BLACKBOARD.getName());
                case WHITEBOARD -> tagText.setText(WHITEBOARD.getName());
                case FLIPCHART -> tagText.setText(FLIPCHART.getName());
                case PROJECTOR -> tagText.setText(PROJECTOR.getName());
                case AIR_CONDITIONER -> {
                    continue;
                }
            }

            tagText.getStyleClass().add("tag-text");
            tagBox.getChildren().addAll(tagIcon, tagText);
            equipmentTags.getChildren().add(tagBox);
        }

        techEquipmentSection.getChildren().addAll(ausstattung, equipmentTags);
        return techEquipmentSection;
    }

    /**
     * Creates the general equipment section for the room, including WiFi, sockets, air conditioning, and capacity.
     *
     * @param room the room whose equipment is to be displayed
     * @return a VBox containing the equipment section
     */
    private static VBox createEquipmentSection(Room room) {
        VBox equipmentSection = new VBox(10);
        equipmentSection.getStyleClass().add("info-section");

        Label equipmentLabel = new Label("Ausstattung");
        equipmentLabel.getStyleClass().addAll("label-bold");

        GridPane infoGrid = getEquipmentGrid();

        HBox wifiBox = createWifiBox(room);
        HBox socketBox = createSocketBox(room);
        HBox klimaBox = createKlimaBox(room);
        HBox capacityBox = createCapacityBox(room);

        infoGrid.add(wifiBox, 0, 0);
        infoGrid.add(socketBox, 1, 0);
        infoGrid.add(klimaBox, 0, 1);
        infoGrid.add(capacityBox, 1, 1);

        equipmentSection.getChildren().addAll(equipmentLabel, infoGrid);
        return equipmentSection;
    }

    /**
     * Creates a box displaying the room's capacity.
     *
     * @param room the room whose capacity is to be displayed
     * @return an HBox containing the capacity information
     */
    private static HBox createCapacityBox(Room room) {
        HBox capacityBox = new HBox(10);
        capacityBox.getStyleClass().add("info-item");
        capacityBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon capacityIcon = new FontIcon(BootstrapIcons.PEOPLE);
        capacityIcon.setIconColor(Color.BLUE);
        capacityIcon.getStyleClass().add("info-icon");

        VBox capacityTextBox = new VBox(2);
        Label capacityLabel = new Label("Kapazität");
        capacityLabel.getStyleClass().add("info-label");

        Label capacityValue = new Label(room.getSeats() + " Personen");
        capacityValue.getStyleClass().add("info-value");

        capacityTextBox.getChildren().addAll(capacityLabel, capacityValue);
        capacityBox.getChildren().addAll(capacityIcon, capacityTextBox);
        return capacityBox;
    }

    /**
     * Creates a box displaying the room's air conditioning availability.
     *
     * @param room the room whose air conditioning info is to be displayed
     * @return an HBox containing the air conditioning information
     */
    private static HBox createKlimaBox(Room room) {

        RoomEquipment equipment = room.getEquipment();


        HBox klimaBox = new HBox(10);
        klimaBox.getStyleClass().add("info-item");
        klimaBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon klimaIcon = new FontIcon(BootstrapIcons.SNOW);
        klimaIcon.setIconColor(Color.BLUE);
        klimaIcon.getStyleClass().add("info-icon");

        VBox klimaTextBox = new VBox(2);
        Label klimaLabel = new Label("Klimaanlage");
        klimaLabel.getStyleClass().add("info-label");

        Label klimaValue = new Label(equipment.roomInfrastructure().contains(AIR_CONDITIONER) ? "Verfügbar" : "Nicht vorhanden");
        klimaValue.getStyleClass().add("info-value");

        klimaTextBox.getChildren().addAll(klimaLabel, klimaValue);
        klimaBox.getChildren().addAll(klimaIcon, klimaTextBox);
        return klimaBox;
    }

    /**
     * Creates a box displaying the room's socket availability.
     *
     * @param room the room whose socket info is to be displayed
     * @return an HBox containing the socket information
     */
    private static HBox createSocketBox(Room room) {
        HBox socketBox = new HBox(10);
        socketBox.getStyleClass().add("info-item");
        socketBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon socketIcon = new FontIcon(BootstrapIcons.LIGHTNING_CHARGE);
        socketIcon.setIconColor(Color.DARKORANGE);
        socketIcon.getStyleClass().add("info-icon");

        VBox socketTextContainer = new VBox(2);
        Label socketsLabel = new Label("Steckdosen");
        socketsLabel.getStyleClass().add("info-label");

        Label socketValue = new Label(room.getEquipment().wallplugs() > 0 ? room.getEquipment().wallplugs() + " Verfügbar" : "Nicht verfügbar");
        socketValue.getStyleClass().add("info-value");

        socketTextContainer.getChildren().addAll(socketsLabel, socketValue);
        socketBox.getChildren().addAll(socketIcon, socketTextContainer);
        return socketBox;
    }

    /**
     * Creates a box displaying the room's WiFi quality.
     *
     * @param room the room whose WiFi quality is to be displayed
     * @return an HBox containing the WiFi information
     */
    private static HBox createWifiBox(Room room) {
        HBox wifiBox = new HBox(10);
        wifiBox.getStyleClass().add("info-item");
        wifiBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon wifiIcon = new FontIcon(BootstrapIcons.WIFI);
        wifiIcon.setIconColor(Color.BLUE);
        wifiIcon.getStyleClass().add("info-icon");

        VBox wifiTextBox = new VBox(2);
        Label wifiLabel = new Label("WLAN");
        wifiLabel.getStyleClass().add("info-label");

        int wifiQuality = room.getEquipment().wifiQuality();
        StringBuilder wifiStars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            wifiStars.append(i < wifiQuality ? "★" : "☆");
        }

        Label wifiValue = new Label(wifiStars + " Qualität");
        wifiValue.getStyleClass().add("info-value");

        wifiTextBox.getChildren().addAll(wifiLabel, wifiValue);
        wifiBox.getChildren().addAll(wifiIcon, wifiTextBox);
        return wifiBox;
    }

    /**
     * Creates and configures the grid layout for equipment information.
     *
     * @return a GridPane configured for equipment information
     */
    private static GridPane getEquipmentGrid() {
        GridPane infoGrid = new GridPane();
        infoGrid.getStyleClass().add("info-grid");
        infoGrid.setHgap(10);
        infoGrid.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        infoGrid.getColumnConstraints().addAll(col1, col2);
        return infoGrid;
    }

    /**
     * Creates the description section for the room.
     *
     * @param room the room whose description is to be displayed
     * @return a VBox containing the description section
     */
    private static VBox createDescriptionSection(Room room) {
        VBox descriptionSection = new VBox(5);
        descriptionSection.getStyleClass().add("info-section");

        Label descriptionLabel = new Label("Beschreibung");
        descriptionLabel.getStyleClass().addAll("label-bold");

        Label descriptionValue = new Label(room.getDescription());
        descriptionValue.getStyleClass().add("label-value");
        descriptionValue.setWrapText(true);

        descriptionSection.getChildren().addAll(descriptionLabel, descriptionValue);
        return descriptionSection;
    }

    /**
     * Creates a legend for the heatmap, indicating the meaning of different colors.
     *
     * @return a VBox containing the heatmap legend
     */
    private static VBox createHeatMapLegend() {
        VBox legend = new VBox(8);
        legend.setAlignment(Pos.TOP_LEFT);

        Label legendTitle = new Label("Legende");
        legendTitle.getStyleClass().add("label-bold");
        legend.getChildren().add(legendTitle);

        legend.getChildren().add(createLegendEntry(MatrixHeatMap.MIN_COLOR, "Niedrige Auslastung"));
        legend.getChildren().add(createLegendEntry(MatrixHeatMap.MID_COLOR, "Mittlere Auslastung"));
        legend.getChildren().add(createLegendEntry(MatrixHeatMap.MAX_COLOR, "Hohe Auslastung"));
        return legend;
    }

    /**
     * Creates a single entry for the heatmap legend.
     *
     * @param color the color representing the usage level
     * @param text  the text description of the usage level
     * @return an HBox containing the legend entry
     */
    private static HBox createLegendEntry(Color color, String text) {
        HBox box = new HBox(6);
        box.setAlignment(Pos.CENTER_LEFT);
        Rectangle rect = new Rectangle(22, 22);
        rect.setArcWidth(4);
        rect.setArcHeight(4);
        rect.setFill(color);
        rect.setStroke(Color.GRAY);
        Label label = new Label(text);
        box.getChildren().addAll(rect, label);
        return box;
    }

    /**
     * Creates the notes section for the room, displaying additional information if available.
     *
     * @param room the room whose notes are to be displayed
     * @return a VBox containing the notes section
     */
    private static VBox createStatsSection(Room room) {
        VBox section = new VBox(5);
        section.getStyleClass().add("section-content");

        Label title = new Label("Wöchentliche Auslastung");
        title.getStyleClass().add("label-bold");
        section.getChildren().add(title);
        if (room.getDescription() != null && !room.getDescription().isEmpty()) {
            HBox hBox = new HBox(20);
            hBox.setAlignment(Pos.TOP_LEFT);
            // Graph and legend
            FileHistoryAPI historyFile = new FileHistoryAPI(HISTORY_DATA_JSON_PATH);
            RoomUsageAnalyzer roomUsageAnalyzer = new RoomUsageAnalyzer(historyFile);
            MatrixHeatMap matrixHeatMap = roomUsageAnalyzer.generateRoomUsageChart(room.getName(), room.getSeats());


            VBox legendBox = createHeatMapLegend();
            hBox.getChildren().add(matrixHeatMap);
            hBox.getChildren().add(legendBox);

            String least = roomUsageAnalyzer.calculateLeastUsedWeekday(room.getName(), room.getSeats());
            if (least != null) {
                VBox recommendationBox = new VBox(4);
                recommendationBox.getStyleClass().add("stats-info");

                HBox iconLabelBox = new HBox(5);
                iconLabelBox.setAlignment(Pos.CENTER_LEFT);
                FontIcon icon = new FontIcon(BootstrapIcons.LIGHTBULB_FILL);
                icon.setIconColor(Color.DARKGOLDENROD);

                Label recTitle = new Label("Empfehlung");
                recTitle.getStyleClass().add("stats-info-title");

                iconLabelBox.getChildren().addAll(icon, recTitle);

                TextFlow recTextFlow = new TextFlow();
                recTextFlow.getStyleClass().add("stats-info-text");
                Text t1 = new Text("Dieser Raum ist am ");
                Text t2 = new Text(least);
                t2.getStyleClass().add("stats-strong");
                Text t3 = new Text(" am wenigsten ausgelastet. Ideal für ruhiges Lernen!");
                recTextFlow.getChildren().addAll(t1, t2, t3);

                recommendationBox.getChildren().addAll(iconLabelBox, recTextFlow);
                legendBox.getChildren().add(recommendationBox);
            } else {
                Label noHist = new Label("Keine historischen Daten vorhanden");
                noHist.getStyleClass().add("label-italic");
                legendBox.getChildren().add(noHist);
            }

            section.getChildren().add(hBox);
        } else {
            Label noNotes = new Label("No additional information available");
            noNotes.getStyleClass().add("label-italic");
            section.getChildren().add(noNotes);
        }
        return section;
    }
}