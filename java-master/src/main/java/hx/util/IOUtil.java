package hx.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class IOUtil {

    public static void copyFileFromClasspath(String source, String target) throws IOException {
        try (InputStream is = IOUtil.class.getClassLoader().getResourceAsStream(source)) {
            Path targetFilePath = Paths.get(target);
            if (! Files.exists(targetFilePath)) {
                Path directory = targetFilePath.getParent();
                if (directory != null && !Files.exists(directory))
                    Files.createDirectories(directory);
            }

            Files.copy(is, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
