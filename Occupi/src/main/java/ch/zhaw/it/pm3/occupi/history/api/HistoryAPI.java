package ch.zhaw.it.pm3.occupi.history.api;

import ch.zhaw.it.pm3.occupi.model.Room;

import java.time.LocalDateTime;
import java.util.Map;
/**
 * The {@code HistoryAPI} interface provides access to historical usage data
 * of {@link Room} instances.
 * <p>
 * Implementations of this interface are responsible for retrieving and
 * supplying information about how often a given room has been used over
 * time. This data can be leveraged for analytics, reporting, or optimizing
 * room utilization within a facility.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Provide room usage history mapped to specific dates.</li>
 *   <li>Ensure that returned usage data is consistent and reflects actual
 *       recorded usage.</li>
 *   <li>Enable consumers of this API to analyze trends in room utilization.</li>
 * </ul>
 */
@SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
public interface HistoryAPI {

    /**
     * Method to get Room usage History for a specific Room
     *
     * @param roomId the {@link Room} which usage history is requested for
     * @return a map containing the room usage history with dates as keys and usage counts as values
     */
    Map<LocalDateTime,Integer> getRoomUsageHistory(String roomId);
}