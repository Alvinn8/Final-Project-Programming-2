package alvin.slutprojekt.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for getting resources that works both if the program is running
 * from a jar or from the IDE.
 */
public class Resources {
    private Resources() {}

    /**
     * Get a resource from the jar or project depending on how the project is being
     * run.
     *
     * @param path The path of the resources. Use slashes and not backslashes.
     * @return The {@link InputStream} of the resource.
     * @throws IOException If an IO error occurs while reading the file.
     * @throws ResourceNotFoundException If the resource was not found.
     */
    public static InputStream getResource(String path) throws IOException {
        // Attempt to get the resource from the jar (running as a jar)
        InputStream stream = Resources.class.getResourceAsStream('/' + path);
        if (stream != null) {
            return stream;
        }

        // Attempt to get the resource from the working directory (running in ide)
        Path path2 = Paths.get("src/main/resources", path);
        if (Files.exists(path2)) {
            return Files.newInputStream(path2);
        }

        throw new ResourceNotFoundException("The requested resource \"" + path + "\" was not found.");
    }
}
