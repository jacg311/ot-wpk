package itf.wpk.objecttracker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Util {

    public static void copyHaarCascadesFromJar() {
        List<String> cascadeFiles = List.of("haarcascade_eye.xml", "haarcascade_frontalface_alt.xml", "haarcascade_frontalface_default.xml");

        for (var file : cascadeFiles) {
            try (InputStream iStream = Main.class.getResourceAsStream("/" + file)) {
                Path path = Paths.get(".").resolve(file);

                if (iStream == null) {
                    throw new IllegalStateException("Couldn't find the cascade files in the jar!");
                }

                if (Files.notExists(path)) {
                    Files.copy(iStream, path);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
