package io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TokenStore {
    final String name;

    public TokenStore(String name) {
        this.name = name;
    }

    public void save(String token) {
        try {
            createTempDirectory();
            Path path = Paths.get(fileName());
            Files.write(path, token.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Cannot save to " + fileName(), e);
        }
    }

    private void createTempDirectory() {
        File directory = new File("temp");
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public String load() {
        try {
            return Files.readAllLines(Paths.get(fileName())).get(0);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load from " + fileName(), e);
        }
    }

    private String fileName() {
        return "temp/" + name + ".txt";
    }
}
