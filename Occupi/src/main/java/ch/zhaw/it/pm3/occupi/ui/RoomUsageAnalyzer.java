package ch.zhaw.it.pm3.occupi.ui;

import ch.zhaw.it.pm3.occupi.history.api.HistoryAPI;
import ch.zhaw.it.pm3.occupi.util.Weekdays;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Analyzes room usage data and generates insights and visualizations.
 * Processes historical room usage data to create heatmaps and calculate usage statistics.
 */
@SuppressWarnings("ClassCanBeRecord")
public class RoomUsageAnalyzer {
    private final HistoryAPI historyAPI;

    private static final List<String> TIME_LABELS = Arrays.asList(
            "06-08", "08-10", "10-12",
            "12-14", "14-16", "16-18", "18-20", "20-22"
    );

    /**
     * Creates a new RoomUsageAnalyzer with the specified history API.
     *
     * @param historyAPI the API to retrieve historical room usage data
     */
    public RoomUsageAnalyzer(HistoryAPI historyAPI) {
        this.historyAPI = historyAPI;
    }

    /**
     * Generates a heatmap chart showing room usage patterns.
     * X-axis: Days of the week (Monday to Saturday)
     * Y-axis: Time intervals in 2-hour blocks (06-08 to 20-22)
     *
     * @param roomId   the ID of the room to generate the usage chart for
     * @param capacity the capacity of the room for percentage calculations
     * @return a MatrixHeatMap visualizing the room usage patterns
     */
    
    public MatrixHeatMap generateRoomUsageChart(String roomId, int capacity) {
        Map<LocalDateTime, Integer> usageHistory = historyAPI.getRoomUsageHistory(roomId);

        double[][] usageMatrix = new double[TIME_LABELS.size()][Weekdays.count()];
        int[][] countMatrix = new int[TIME_LABELS.size()][Weekdays.count()];

        aggregateUsageData(usageHistory, usageMatrix, countMatrix);
        convertToAveragePercentages(usageMatrix, countMatrix, capacity);

        return new MatrixHeatMap(Weekdays.getShortLabels(), TIME_LABELS, usageMatrix);
    }

    /**
     * Calculate the least-used weekday for a room based on historical entries.
     * Returns null if no usable data or invalid capacity.
     *
     * @param roomId   the room id
     * @param capacity the room capacity
     * @return weekday name (German) with the lowest average usage percentage, or null if no data
     */
    public String calculateLeastUsedWeekday(String roomId, int capacity) {
        Objects.requireNonNull(roomId, "roomId must not be null");

        String result = null;

        if (capacity > 0) {
            Map<LocalDateTime, Integer> usageHistory = historyAPI.getRoomUsageHistory(roomId);
            if (hasUsageData(usageHistory)) {
                WeekdayUsageData weekdayData = aggregateUsageByWeekday(usageHistory);
                int leastUsedDayIndex = findLeastUsedDay(weekdayData, capacity);
                result = formatWeekdayResult(leastUsedDayIndex);
            }
        }

        return result;
    }

    /**
     * Aggregates usage data from history into the matrices.
     * <p><b>Note:</b> Modifies the provided matrices in-place for performance.</p>
     *
     * @param usageHistory historical usage data mapped by date and time
     * @param usageMatrix  matrix to accumulate usage counts (modified)
     * @param countMatrix  matrix to count entries per cell (modified)
     */
    private void aggregateUsageData(Map<LocalDateTime, Integer> usageHistory, double[][] usageMatrix, int[][] countMatrix) {
        for (Map.Entry<LocalDateTime, Integer> entry : usageHistory.entrySet()) {
            LocalDateTime dateTime = entry.getKey();
            Integer usageCount = entry.getValue();

            Weekdays weekdays = Weekdays.fromDayOfWeek(dateTime.getDayOfWeek());
            if (weekdays == null) {
                continue;
            }

            int dayIndex = weekdays.ordinal();
            int timeIndex = getTimeSlotIndex(dateTime);

            if (isInvalidTimeSlot(timeIndex)) {
                continue;
            }

            usageMatrix[timeIndex][dayIndex] += usageCount;
            countMatrix[timeIndex][dayIndex]++;
        }
    }

    /**
     * Converts the aggregated usage data into average percentages of capacity.
     *
     * @param usageMatrix the matrix with summed usage counts
     * @param countMatrix the matrix with counts of entries per cell
     * @param capacity    the room capacity for percentage calculations
     */
    private void convertToAveragePercentages(double[][] usageMatrix, int[][] countMatrix, int capacity) {
        for (int timeSlot = 0; timeSlot < TIME_LABELS.size(); timeSlot++) {
            for (int weekday = 0; weekday < Weekdays.count(); weekday++) {
                if (countMatrix[timeSlot][weekday] > 0) {
                    // Calculate average usage
                    double averageUsage = usageMatrix[timeSlot][weekday] / countMatrix[timeSlot][weekday];
                    // Convert to percentage of capacity
                    double percentageOfCapacity = (averageUsage / capacity) * 100.0;
                    usageMatrix[timeSlot][weekday] = percentageOfCapacity;
                }
            }
        }
    }

    /**
     * Gets the time slot index based on the hour (06-22 range, 2-hour blocks).
     *
     * @param dateTime the date and time
     * @return index 0=06-08, 1=08-10, ..., 7=20-22
     */
    private int getTimeSlotIndex(LocalDateTime dateTime) {
        return (dateTime.getHour() - 6) / 2;
    }

    /**
     * Checks if the time slot index is within the valid range.
     *
     * @param timeIndex the time slot index
     * @return true if valid, false otherwise
     */
    private boolean isInvalidTimeSlot(int timeIndex) {
        return timeIndex < 0 || timeIndex >= TIME_LABELS.size();
    }

    /**
     * Checks if usage history contains any data.
     *
     * @param usageHistory the usage history map
     * @return true if data exists, false otherwise
     */
    private boolean hasUsageData(Map<LocalDateTime, Integer> usageHistory) {
        return usageHistory != null && !usageHistory.isEmpty();
    }

    /**
     * Aggregates usage data by weekday (Monday-Saturday).
     *
     * @param usageHistory historical usage data mapped by date and time
     * @return aggregated usage data per weekday
     */
    private WeekdayUsageData aggregateUsageByWeekday(Map<LocalDateTime, Integer> usageHistory) {
        int numberOfWeekdays = Weekdays.count();
        double[] totalUsagePerDay = new double[numberOfWeekdays];
        int[] entryCountPerDay = new int[numberOfWeekdays];

        usageHistory.forEach((dateTime, usageCount) -> {
            if (usageCount == null) {
                return;
            }

            Weekdays weekdays = Weekdays.fromDayOfWeek(dateTime.getDayOfWeek());
            if (weekdays == null) {
                return;
            }

            int dayIndex = weekdays.ordinal();
            int timeIndex = getTimeSlotIndex(dateTime);
            if (isInvalidTimeSlot(timeIndex)) {
                return;
            }

            totalUsagePerDay[dayIndex] += Math.max(0, usageCount);
            entryCountPerDay[dayIndex]++;
        });

        return new WeekdayUsageData(totalUsagePerDay, entryCountPerDay);
    }

    /**
     * Finds the index of the weekday with the lowest average usage percentage.
     *
     * @param data     aggregated usage data per weekday
     * @param capacity the room capacity for percentage calculations
     * @return the index of the least used day, or -1 if no valid data exists
     */
    private int findLeastUsedDay(WeekdayUsageData data, int capacity) {
        double minAveragePercentage = Double.POSITIVE_INFINITY;
        int leastUsedDayIndex = -1;

        for (int dayIndex = 0; dayIndex < data.totalUsage.length; dayIndex++) {
            if (data.entryCount[dayIndex] == 0) {
                continue;
            }

            double averagePercentage = calculateAveragePercentage(data.totalUsage[dayIndex], data.entryCount[dayIndex], capacity);

            if (averagePercentage < minAveragePercentage) {
                minAveragePercentage = averagePercentage;
                leastUsedDayIndex = dayIndex;
            }
        }

        return leastUsedDayIndex;
    }

    /**
     * Calculates the average usage as a percentage of capacity.
     */
    private double calculateAveragePercentage(double totalUsage, int entryCount, int capacity) {
        double averageUsage = totalUsage / entryCount;
        return (averageUsage / capacity) * 100.0;
    }

    /**
     * Formats the result by converting day index to German weekday name.
     *
     * @param dayIndex the index of the weekday (0-6)
     * @return German weekday name, or null if invalid index
     */
    private String formatWeekdayResult(int dayIndex) {
        String result = null;

        if (dayIndex >= 0 && dayIndex < Weekdays.count()) {
            Weekdays weekdayIndex = Weekdays.values()[dayIndex];
            result = weekdayIndex.getFullLabel();
        }

        return result;
    }

    /**
     * Data structure to hold aggregated usage data per weekday.
     *
     * @param totalUsage total usage per weekday
     * @param entryCount number of entries per weekday
     */
    private record WeekdayUsageData(double[] totalUsage, int[] entryCount) {
    }
}