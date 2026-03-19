package ch.zhaw.it.pm3.occupi.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class for validating file paths and file system operations.
 * <p>
 * This class provides centralized file validation logic to ensure consistency across the application and reduce code duplication. All methods are static and throw appropriate exceptions when validation fails.
 * </p>
 * <p>
 * The validator ensures that file paths point to existing, accessible JSON files by checking file existence, file type (regular file), and file extension (.json).
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * public MyClass(Path filePath) {
 *     FileValidator.requireAccessibleFile(filePath);
 *     this.filePath = filePath;
 * }
 *
 * public void processFile() {
 *     FileValidator.requireAccessibleFile(filePath);
 *     // ... process JSON file
 * }
 * }</pre>
 *
 * @see java.nio.file.Files
 * @see java.nio.file.Path
 */
public final class FileValidator {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private FileValidator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates that the path points to an existing regular JSON file.
     * <p>
     * This method should be used in constructors or initialization code to validate that a file path is valid before proceeding with operations. The method checks that the path exists, is a regular file, and has a .json extension.
     * </p>
     *
     * @param path                      the path to validate
     * @throws NullPointerException     if path is null
     * @throws IllegalArgumentException if path does not exist, is not a regular file, or is not a JSON file
     */
    public static void requireAccessibleFile(Path path) {
        Objects.requireNonNull(path, "File path must not be null");
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException(
                    "Path input does not lead to accessible file: " + path
            );
        }
        if (!isJsonFile(path)) {
            throw new IllegalArgumentException(
                    "File is not a JSON file: " + path
            );
        }
    }

    /**
     * Checks if the given path represents a JSON file by examining its file extension.
     * <p>
     * The validation is case-insensitive and checks if the file name ends with ".json". This is a helper method used internally to validate file types.
     * </p>
     *
     * @param path          the file path to check
     * @return {@code true} if the file has a .json extension (case-insensitive), {@code false} otherwise
     */
    private static boolean isJsonFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".json");
    }
}