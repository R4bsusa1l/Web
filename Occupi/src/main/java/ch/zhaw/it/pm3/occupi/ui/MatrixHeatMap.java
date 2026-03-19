package ch.zhaw.it.pm3.occupi.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * A heat map visualization component that displays data in a matrix format.
 * Each cell's color intensity represents the value at that position.
 */
@SuppressWarnings("ChainedMethodCall")
public class MatrixHeatMap extends GridPane {
    /**
     * Min color (Green) for the heat map
     */
    public static final Color MIN_COLOR = Color.rgb(27, 187, 35); // Green
    /**
     * Mid color (Orange) for the heat map
     */
    public static final Color MID_COLOR = Color.rgb(255, 165, 0); // Orange
    /**
     * Max color (Red) for the heat map
     */
    public static final Color MAX_COLOR = Color.rgb(220, 53, 69); // Red
    private static final int CELL_SIZE = 40;
    private static final double ORANGE_THRESHOLD = 0.25;
    private static final double RED_THRESHOLD = 0.60;

    private final List<String> xLabels;
    private final List<String> yLabels;
    private final double[][] data;
    private final double minValue;
    private final double maxValue;

    /**
     * Creates a new MatrixHeatMap.
     *
     * @param xLabels labels for the x-axis (columns)
     * @param yLabels labels for the y-axis (rows)
     * @param data    2D array of values where data[row][col] corresponds to yLabels[row] and xLabels[col]
     */
    public MatrixHeatMap(List<String> xLabels, List<String> yLabels, double[][] data) {
        this.xLabels = xLabels;
        this.yLabels = yLabels;
        this.data = data;

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double[] row : data) {
            for (double value : row) {
                if (value < min) min = value;
                if (value > max) max = value;
            }
        }
        this.minValue = min;
        this.maxValue = max;

        buildHeatMap();
    }

    /**
     * Builds the heat map grid with headers and colored cells.
     */
    private void buildHeatMap() {
        this.getStyleClass().add("matrix-heatmap");

        // Add column headers
        for (int col = 0; col < xLabels.size(); col++) {
            Label header = new Label(xLabels.get(col));
            header.getStyleClass().add("heatmap-column-header");
            header.setMinWidth(CELL_SIZE);
            this.add(header, col + 1, 0);
        }

        // Add row headers and data cells
        for (int row = 0; row < yLabels.size(); row++) {
            // Row header
            Label rowHeader = new Label(yLabels.get(row));
            rowHeader.getStyleClass().add("heatmap-row-header");
            rowHeader.setMinWidth(CELL_SIZE);
            this.add(rowHeader, 0, row + 1);

            // Data cells
            for (int col = 0; col < xLabels.size(); col++) {
                double value = data[row][col];
                StackPane cell = createCell(value);
                this.add(cell, col + 1, row + 1);
            }
        }
    }

    /**
     * Creates a single cell in the heat map with appropriate color and label.
     *
     * @param value the value to represent in the cell
     * @return a StackPane representing the cell
     */
    private StackPane createCell(double value) {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("heatmap-cell");
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);

        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
        rect.getStyleClass().add("heatmap-cell-rect");
        Color cellColor = interpolateColor(value);
        rect.setFill(cellColor);

        Label valueLabel = new Label(String.format("%.0f", value) + "%");
        valueLabel.getStyleClass().add("heatmap-cell-label");

        cell.getChildren().addAll(rect, valueLabel);

        return cell;
    }

    /**
     * Piecewise color interpolation:
     * 0%   -> 25%  : Green to Orange
     * 25%  -> 60%  : Orange to Red
     * >60%         : Solid Red
     */
    private Color interpolateColor(double value) {
        if (Math.abs(maxValue - minValue) < 1e-9) {
            return MIN_COLOR;
        }

        double normalized = (value - minValue) / (maxValue - minValue);

        Color returnColor;
        if (normalized <= ORANGE_THRESHOLD) {
            double t = normalized / ORANGE_THRESHOLD;
            returnColor = createColorForCell(MIN_COLOR, MID_COLOR, t);

        } else if (normalized <= RED_THRESHOLD) {
            double t = (normalized - ORANGE_THRESHOLD) / (RED_THRESHOLD - ORANGE_THRESHOLD);
            returnColor = createColorForCell(MID_COLOR, MAX_COLOR, t);

        } else {
            returnColor = MAX_COLOR;
        }

        return returnColor;
    }

    /**
     * Linearly interpolates between two colors.
     *
     * @param a the start color
     * @param b the end color
     * @param t interpolation factor (0.0 to 1.0)
     * @return the interpolated color
     */
    private Color createColorForCell(Color a, Color b, double t) {
        double red = a.getRed() + (b.getRed() - a.getRed()) * t;
        double green = a.getGreen() + (b.getGreen() - a.getGreen()) * t;
        double blue = a.getBlue() + (b.getBlue() - a.getBlue()) * t;
        double opacity = a.getOpacity() + (b.getOpacity() - a.getOpacity()) * t;
        return new Color(red, green, blue, opacity);
    }
}