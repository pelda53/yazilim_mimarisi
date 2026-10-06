package logflow;

import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

public class ConsoleSink implements Emitter<LogRecord> {

    private static final DateTimeFormatter OUTPUT_TIME =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    @Override
    public void emit(LogRecord record) {
        System.out.printf(
                "%s | %s | %s %s | status=%d | bytes=%d | agent=\"%s\"%n",
                OUTPUT_TIME.format(record.timestamp()),
                record.clientIp(),
                record.method(),
                record.path(),
                record.status(),
                record.bytes(),
                record.userAgent()
        );
    }
}
