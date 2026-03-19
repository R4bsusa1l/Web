package ch.zhaw.it.pm3.occupi.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Generic JSON reader and writer for lists of objects of type T.
 * <p>
 * This class provides centralized JSON serialization/deserialization functionality
 * for the entire application. It is configured to work with pure POJOs without
 * requiring Jackson annotations in domain models.
 *
 * @param <T> the type of objects to read and write
 */
@SuppressWarnings({"ChainedMethodCall", "ClassCanBeRecord"})
public class JsonReader<T> {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final Class<T> elementClass;

    /**
     * Constructs a JsonReader for the given element class.
     *
     * @param elementClass the concrete class of the list elements
     */
    public JsonReader(Class<T> elementClass) {
        this.elementClass = elementClass;
    }

    /**
     * Reads a list of objects of type T from the specified JSON file.
     *
     * @param filePath the path to the JSON file
     * @return a list of objects of type T
     * @throws IOException if an I/O error occurs or if the JSON is malformed
     */
    public List<T> readValues(Path filePath) throws IOException {
        // Construct a concrete collection type List<elementClass> so Jackson deserializes into the correct element type
        return MAPPER.readValue(filePath.toFile(), MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass));
    }

    /**
     * Writes a list of objects of type T to the specified JSON file.
     *
     * @param filePath the path to the JSON file
     * @param values   the list of objects of type T to write
     * @throws IOException if an I/O error occurs
     */
    public void writeValues(Path filePath, List<T> values) throws IOException {
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), values);
    }
}