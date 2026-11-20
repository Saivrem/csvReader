package helpers;

/**
 * Error causes
 */
public enum Cause {
    ENCLOSURE,
    COLUMNS,
    /**
     * @deprecated Retained for compatibility with the legacy reader.
     * The current parser does not emit this cause.
     */
    @Deprecated(since = "1.9.2", forRemoval = false)
    STRUCTURE,
    LARGE_LINE
}
