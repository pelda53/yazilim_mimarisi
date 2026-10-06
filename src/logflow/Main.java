package logflow;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Kullanım: java -cp out logflow.Main data/access-small.log");
            System.exit(1);
        }

        try {
            new OrderPipeline().run(Path.of(args[0]));
        } catch (Exception ex) {
            System.err.println("Pipeline hatası: " + ex.getMessage());
            System.exit(1);
        }
    }
}
