package ch.zhaw.it.pm3.occupi.ui;

import ch.zhaw.it.pm3.occupi.Occupi;
import ch.zhaw.it.pm3.occupi.model.Event;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Dialog for creating a new event. This dialog collects event details such as title, description,
 * start time, end time, and tags. It validates the input and disables the OK button if the input is invalid.
 */
@SuppressWarnings("ChainedMethodCall")
public class EventCreationDialog extends Dialog<Event> {
    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final int DIALOG_PREF_WIDTH = 500;
    private static final int DIALOG_MIN_WIDTH = 450;
    private static final int CONTENT_SPACING = 10;
    private static final int ROW_SPACING = 5;

    private final List<TextField> requiredFields = new ArrayList<>();

    private TextField titleField;
    private TextField descriptionField;
    private TextField tagsField;
    private TextField startDateField;
    private TextField endDateField;

    /**
     * Constructs the EventCreationDialog and initializes its components.
     */
    public EventCreationDialog() {
        setupDialog();
        createContent();
        setupButtons();
    }

    /**
     * Sets up the dialog's title, header, stylesheet, and dimensions.
     */
    private void setupDialog() {
        setTitle("Neues Event erstellen");
        setHeaderText("Bitte geben Sie die Details für das neue Event ein:");
        applyStylesheet();
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(EventCreationDialog.class.getClassLoader().getResourceAsStream(Occupi.ICON_PATH)));
        setDialogDimensions();
    }

    /**
     * Applies the stylesheet to the dialog.
     */
    private void applyStylesheet() {
        try {
            String cssUrl = Objects.requireNonNull(getClass().getResource(Occupi.STYLES_CSS_PATH)).toExternalForm();
            getDialogPane().getStylesheets().add(cssUrl);
        } catch (NullPointerException e) {
            System.err.println("Could not load style.css");
        }
    }

    /**
     * Sets the preferred and minimum dimensions of the dialog.
     */
    private void setDialogDimensions() {
        getDialogPane().setPrefWidth(DIALOG_PREF_WIDTH);
        getDialogPane().setMinWidth(DIALOG_MIN_WIDTH);
    }


    /**
     * Creates the content of the dialog, including labeled text fields for event details.
     */
    private void createContent() {
        VBox content = new VBox(CONTENT_SPACING);
        getDialogPane().setContent(content);

        titleField = addFieldRow(content, "Titel:", "Geben Sie einen Titel ein...", true);
        descriptionField = addFieldRow(content, "Beschreibung:", "Geben Sie eine Beschreibung ein...", true);
        startDateField = addFieldRow(content, "Startzeit:", "z.B. 10.10.2025 14:00 (" + DATE_TIME_FORMAT + ")", true);
        endDateField = addFieldRow(content, "Endzeit:", "z.B. 10.10.2025 16:00 (" + DATE_TIME_FORMAT + ")", true);
        tagsField = addFieldRow(content, "Tags:", "Tag1, Tag2, Tag3", false);

        // When start date changes, re-evaluate end date styling due to ordering rule
        if (startDateField != null) {
            startDateField.textProperty().addListener((obs, ov, nv) -> {
                updateFieldStyle(startDateField);
                if (endDateField != null) updateFieldStyle(endDateField);
            });
        }
    }

    /**
     * Adds a labeled text field row to the given container and tracks the text field for validation.
     *
     * @param container  The VBox to which the row will be added.
     * @param labelText  The label text displayed above the field.
     * @param promptText The prompt text displayed inside the field.
     * @param required   Whether the field is required for enabling OK.
     * @return the created TextField
     */
    private TextField addFieldRow(VBox container, String labelText, String promptText, boolean required) {
        Label label = new Label(labelText);
        TextField field = new TextField();
        field.setPromptText(promptText);
        VBox row = new VBox(label, field);
        row.setSpacing(ROW_SPACING);
        container.getChildren().add(row);

        if (required) requiredFields.add(field);

        // Live validation styling
        field.textProperty().addListener((obs, oldVal, newVal) -> updateFieldStyle(field));
        // Initialize style
        updateFieldStyle(field);
        return field;
    }

    /**
     * Sets up the dialog buttons, including OK and Reset buttons.
     * The OK button is disabled if the input is invalid.
     */
    private void setupButtons() {
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Create result on OK
        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return createEvent();
            }
            return null;
        });

        Platform.runLater(() -> {
            Button okButton = (Button) getDialogPane().lookupButton(okButtonType);
            Button cancelButton = (Button) getDialogPane().lookupButton(cancelButtonType);

            okButton.disableProperty().bind(Bindings.createBooleanBinding(
                    () -> !isInputValid(),
                    requiredFields.stream()
                            .map(TextField::textProperty)
                            .toArray(ObservableValue[]::new)
            ));

            cancelButton.requestFocus();
        });
    }

    /**
     * Creates an Event object from the input fields.
     *
     * @return The created Event object, or null if parsing fails.
     */
    private Event createEvent() {
        LocalDateTime start = LocalDateTime.parse(startDateField.getText().trim(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endDateField.getText().trim(), DATE_TIME_FORMATTER);

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();

        String tagsText = tagsField.getText() == null ? "" : tagsField.getText().trim();
        List<String> tags = tagsText.isEmpty()
                ? List.of()
                : Arrays.stream(tagsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        return new Event(title, description, start, end, tags);
    }

    /**
     * Validates the input fields in the dialog.
     *
     * @return True if all required input fields are valid, false otherwise.
     */
    private boolean isInputValid() {
        return requiredFields.stream().allMatch(this::isFieldValid);
    }

    /**
     * Validates a single text field.
     * <p>
     * Required fields must be non-empty. Start/End fields must match DATE_TIME_FORMAT,
     * and End must not be before Start (if Start is parsable).
     * <p>
     * Optional fields are always valid (even if empty).
     * @param field The text field to validate.
     * @return True if the field is valid, false otherwise.
     */
    private boolean isFieldValid(TextField field) {
        String text = getTrimmedText(field);
        boolean valid = true;

        if (requiredFields.contains(field) && text.isEmpty()) {
            valid = false;
        }

        // Additional checks for start/end date fields
        if (valid && (field == startDateField || field == endDateField)) {
            LocalDateTime parsed = tryParseDate(text);

            if (text.isEmpty() || parsed == null) {
                valid = false;
            } else if (parsed.isBefore(LocalDateTime.now())) {
                valid = false;
            }

            else if (field == endDateField) {
                LocalDateTime start = tryParseDate(getTrimmedText(startDateField));
                if (start != null && parsed.isBefore(start)) {
                    valid = false;
                }
            }
        }

        return valid;
    }

    /**
     * Retrieves the trimmed text from a TextField.
     *
     * @param field The TextField to get text from.
     * @return The trimmed text, or an empty string if the text is null.
     */
    private String getTrimmedText(TextField field) {
        return field.getText() != null ? field.getText().trim() : "";
    }


    /**
     * Tries to parse a date string into a LocalDateTime using the defined formatter.
     *
     * @param text The date string to parse.
     * @return The parsed LocalDateTime, or null if parsing fails.
     */
    private LocalDateTime tryParseDate(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        String trimmed = text.trim();

        try {
            return LocalDateTime.parse(trimmed, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Applies or removes the 'invalid-field' CSS class depending on the field's validity.
     * Shows red border only if user entered something and it is invalid.
     *
     * @param field TextField to update.
     */
    private void updateFieldStyle(TextField field) {
        String text = field.getText() == null ? "" : field.getText().trim();
        boolean showInvalid = !text.isEmpty() && !isFieldValid(field);
        List<String> classes = field.getStyleClass();
        if (showInvalid) {
            if (!classes.contains("invalid-field")) {
                classes.add("invalid-field");
            }
        } else {
            classes.remove("invalid-field");
        }
    }
}
