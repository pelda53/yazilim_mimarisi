package logflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileLineSource {

    public void read(Path path, Emitter<String> emitter) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(emitter::emit);
        }
    }
}
