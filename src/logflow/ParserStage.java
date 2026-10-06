package logflow;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses Apache Common/Combined Log Format lines into LogRecord objects.
 *
 * Invalid lines are skipped and counted in this increment.
 * Detailed invalid-line handling is intentionally postponed to a later week.
 */
public class ParserStage implements Stage<String, LogRecord> {

    private static final DateTimeFormatter APACHE_TIME =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", java.util.Locale.ENGLISH);

    /*
     * Combined Log Format:
     * ip ident user [timestamp] "METHOD path protocol" status bytes "referer" "user-agent"
     *
     * The final referer is optional so the parser can also accept a CLF-like
     * record with a user-agent extension.
     */
    private static final Pattern PATTERN = Pattern.compile(
            "^\\s*(\\S+)\\s+\\S+\\s+\\S+\\s+\\[([^]]+)]\\s+"
          + "\"([A-Z]+)\\s+(\\S+)\\s+([^\"]+)\"\\s+"
          + "(\\d{3})\\s+(\\d+|-)\\s+"
          + "(?:\"[^\"]*\"\\s+)?\"([^\"]*)\"\\s*$"
    );

    private Emitter<LogRecord> next = record -> {};
    private int invalidCount = 0;

    @Override
    public void setNext(Emitter<LogRecord> next) {
        this.next = next == null ? record -> {} : next;
    }

    @Override
    public void accept(String line) {
        if (line == null || line.trim().isEmpty()) {
            invalidCount++;
            return;
        }

        try {
            Matcher m = PATTERN.matcher(line);
            if (!m.matches()) {
                invalidCount++;
                return;
            }

            Instant timestamp = parseTimestamp(m.group(2));
            int status = Integer.parseInt(m.group(6));
            if (status < 100 || status > 599) {
                invalidCount++;
                return;
            }

            long bytes = m.group(7).equals("-") ? 0L : Long.parseLong(m.group(7));

            LogRecord record = new LogRecord(
                    timestamp,
                    m.group(1),
                    m.group(3),
                    m.group(4),
                    status,
                    bytes,
                    m.group(8),
                    Map.of(),
                    line
            );

            next.emit(record);
        } catch (RuntimeException ex) {
            invalidCount++;
        }
    }

    private Instant parseTimestamp(String value) {
        try {
            return ZonedDateTime.parse(value, APACHE_TIME).toInstant();
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Hatalı zaman damgası", ex);
        }
    }

    public int getInvalidCount() {
        return invalidCount;
    }
}
