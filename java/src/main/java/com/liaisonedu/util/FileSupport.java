package com.liaisonedu.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSupport {

    private final Logger LOGGER = LoggerFactory.getLogger(FileSupport.class);

    private final Path outputDir;
    private final JsonSupport jsonSupport;

    public FileSupport(String dir, JsonSupport jsonSupport) {
        this.outputDir = Paths.get(dir);
        this.jsonSupport = jsonSupport;
    }

    void writeObjList(String name, List<?> collection) {
        try {
            Files.write(outputDir.resolve(name + ".json"),
                    jsonSupport.write(collection).getBytes());
        } catch (IOException e) {
            LOGGER.error("Failed to save object collection for [{}]", name, e);
        }
    }

    public void writeObj(String name, Object obj) {
        try {
            Files.write(outputDir.resolve(name + ".json"),
                    jsonSupport.write(obj).getBytes());
        } catch (IOException e) {
            LOGGER.error("Failed to save object response for [{}]", name, e);
        }
    }

    void writePdf(String name, byte[] bytes) {
        try {
            Files.write(outputDir.resolve(name + ".pdf"), bytes);
        } catch (IOException e) {
            LOGGER.error("Failed to save file response for [{}]", name, e);
        }
    }
}
