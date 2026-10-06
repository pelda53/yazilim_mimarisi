package logflow;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Pipeline for Increment 2:
 * file -> ParserStage -> ConsoleSink
 */
public class OrderPipeline {

    private final FileLineSource source;
    private final ParserStage parser;
    private final ConsoleSink sink;

    public OrderPipeline() {
        source = new FileLineSource();
        parser = new ParserStage();
        sink = new ConsoleSink();

        parser.setNext(sink);
    }

    public void run(Path input) throws IOException {
        source.read(input, parser::accept);
        System.out.println("Invalid lines: " + parser.getInvalidCount());
    }

    public ParserStage parser() {
        return parser;
    }
}
