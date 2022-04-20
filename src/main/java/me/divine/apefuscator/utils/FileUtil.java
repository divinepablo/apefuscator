package me.divine.apefuscator.utils;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;
// i didnt make this class, i just used it
public class FileUtil {

    private static final Logger LOGGER = LogManager.getLogger(FileUtil.class);

    public static Map<String, byte[]> loadFilesFromZip(String file) {
        Map<String, byte[]> files = new HashMap<>();

        try (ZipFile zipFile = new ZipFile(file)) {
            zipFile.entries().asIterator().forEachRemaining(zipEntry -> {
                try {
                    files.put(zipEntry.getName(), zipFile.getInputStream(zipEntry).readAllBytes());
                } catch (Exception e) {
                    LOGGER.error("Could not load ZipEntry: {}", zipEntry.getName());
                    LOGGER.debug("Error", e);
                }
            });
        } catch (Exception e) {
            LOGGER.error("Could not load file: {}", file);
            LOGGER.debug("Error", e);
        }

        return files;
    }
}
//


//}
