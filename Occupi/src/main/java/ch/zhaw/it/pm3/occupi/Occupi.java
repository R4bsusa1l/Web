package ch.zhaw.it.pm3.occupi;

import ch.zhaw.it.pm3.occupi.controllers.BuildingController;
import ch.zhaw.it.pm3.occupi.controllers.MainWindowController;
import ch.zhaw.it.pm3.occupi.reservation.api.ReservationAPI;
import ch.zhaw.it.pm3.occupi.reservation.simulation.FileReservationAPI;
import ch.zhaw.it.pm3.occupi.sensors.api.SensorAPI;
import ch.zhaw.it.pm3.occupi.sensors.simulation.FileSensorAPI;
import ch.zhaw.it.pm3.occupi.storage.BuildingStorageAPI;
import ch.zhaw.it.pm3.occupi.storage.FileBuildingStorageAPI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Main application class for Occupi.
 */
@SuppressWarnings("HardcodedFileSeparator")
public class Occupi extends Application {
    /**
     * Path to the application icon.
     */
    public static final String ICON_PATH = "logo-occupi.png";

    /**
     * Path to the CSS styles file.
     */
    public static final String STYLES_CSS_PATH = "/css/styles.css";
    private static final Path BUILDINGS_FILE_PATH = Path.of("src", "main", "resources", "jsonData", "buildingData", "BuildingData.json");
    private static final Path RESERVATION_FILE_PATH = Path.of("src", "main", "resources", "jsonData", "reservationData", "ReservationData.json");
    private static final Path SENSOR_FILE_PATH = Path.of("src", "main", "resources", "jsonData", "sensorData", "SensorData.json");
    private static final String MAIN_WINDOW_FXML = "/ch/zhaw/it/pm3/occupi/MainWindow.fxml";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;
    private static final String WINDOW_TITLE = "Occupi";


    /**
     * Default constructor.
     */
    public Occupi(){}

    @SuppressWarnings("ChainedMethodCall")
    @Override
    public void start(Stage stage) throws IOException {
        // Load FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
        Parent root = loader.load();

        MainWindowController mainWindowController = getMainWindowController(loader);
        mainWindowController.initializeUI();

        // Create scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Load CSS
        String css = Objects.requireNonNull(getClass().getResource(STYLES_CSS_PATH)).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setOnCloseRequest(event -> {
            stage.close();
            Platform.exit();
            System.exit(0);
        });

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Occupi.class.getClassLoader().getResourceAsStream(ICON_PATH)));
        stage.show();

    }

    /**
     * Initializes and returns the MainWindowController with its dependencies.
     *
     * @param loader  the FXMLLoader used to load the FXML file
     * @return the initialized MainWindowController
     * @throws IOException if an I/O error occurs
     */
    private MainWindowController getMainWindowController(FXMLLoader loader) throws IOException {
        BuildingStorageAPI buildingStorageAPI = new FileBuildingStorageAPI(BUILDINGS_FILE_PATH);
        ReservationAPI reservationAPI = new FileReservationAPI(RESERVATION_FILE_PATH);
        SensorAPI sensorAPI = new FileSensorAPI(SENSOR_FILE_PATH);

        BuildingController buildingController = new BuildingController(reservationAPI,sensorAPI,buildingStorageAPI);
        MainWindowController mainWindowController = loader.getController();
        mainWindowController.setBuildingController(buildingController);
        return mainWindowController;
    }

    /**
     * Application entry point.
     *
     * @param args  command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
