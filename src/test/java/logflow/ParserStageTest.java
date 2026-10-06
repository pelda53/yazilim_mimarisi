package logflow;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParserStageTest {

    private ParserStage parser;
    private CollectingEmitter<LogRecord> collector;

    @BeforeEach
    void setUp() {
        parser = new ParserStage();
        collector = new CollectingEmitter<>();
        parser.setNext(collector);
    }

    private String valid() {
        return "192.168.1.10 - - [06/Oct/2026:10:20:30 +0300] "
             + "\"GET /page1.html HTTP/1.1\" 200 1234 "
             + "\"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X)\"";
    }

    @Test
    void validLineIsParsed() {
        parser.accept(valid());

        assertEquals(1, collector.values().size());
        LogRecord r = collector.values().get(0);
        assertEquals("192.168.1.10", r.clientIp());
        assertEquals("GET", r.method());
        assertEquals("/page1.html", r.path());
        assertEquals(200, r.status());
        assertEquals(1234, r.bytes());
    }

    @Test
    void missingFieldIsInvalid() {
        parser.accept("192.168.1.10 - - [06/Oct/2026:10:20:30 +0300] \"GET /x HTTP/1.1\" 200");
        assertEquals(0, collector.values().size());
        assertEquals(1, parser.getInvalidCount());
    }

    @Test
    void invalidTimestampIsInvalid() {
        parser.accept("192.168.1.10 - - [not-a-time] \"GET /x HTTP/1.1\" 200 10 \"-\" \"Agent\"");
        assertEquals(1, parser.getInvalidCount());
    }

    @Test
    void invalidStatusCodeIsInvalid() {
        parser.accept("192.168.1.10 - - [06/Oct/2026:10:20:30 +0300] \"GET /x HTTP/1.1\" 999 10 \"-\" \"Agent\"");
        assertEquals(1, parser.getInvalidCount());
    }

    @Test
    void emptyLineIsInvalid() {
        parser.accept("");
        assertEquals(1, parser.getInvalidCount());
    }

    @Test
    void extraSpacesAreAccepted() {
        parser.accept("  192.168.1.10   -   -   [06/Oct/2026:10:20:30 +0300]   "
                + "\"GET /x HTTP/1.1\"   200   10   \"-\"   \"Agent\"  ");
        assertEquals(1, collector.values().size());
    }

    @Test
    void quotedUserAgentMayContainSpaces() {
        parser.accept("192.168.1.10 - - [06/Oct/2026:10:20:30 +0300] "
                + "\"GET /x HTTP/1.1\" 200 10 \"-\" "
                + "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36\"");
        assertEquals(1, collector.values().size());
        assertTrue(collector.values().get(0).userAgent().contains("Macintosh"));
    }

    @Test
    void queryStringIsKeptInPath() {
        parser.accept("192.168.1.10 - - [06/Oct/2026:10:20:30 +0300] "
                + "\"GET /search?q=java&page=2 HTTP/1.1\" 200 10 \"-\" \"Agent\"");
        assertEquals("/search?q=java&page=2", collector.values().get(0).path());
    }
}
