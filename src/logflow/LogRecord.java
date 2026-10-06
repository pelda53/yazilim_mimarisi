package logflow;

import java.time.Instant;
import java.util.Map;

/**
 * Immutable domain record for a parsed log entry.
 */
public record LogRecord(
        Instant timestamp,
        String clientIp,
        String method,
        String path,
        int status,
        long bytes,
        String userAgent,
        Map<String, String> attributes,
        String raw
) {
    public LogRecord {
        if (timestamp == null || clientIp == null || method == null ||
            path == null || userAgent == null || attributes == null || raw == null) {
            throw new IllegalArgumentException("LogRecord alanları null olamaz.");
        }
        attributes = Map.copyOf(attributes);
    }
}
