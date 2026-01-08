package dev.mariany.arcanehand.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.mariany.arcanehand.ArcaneHand;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File file;
    private AHConfig config = new AHConfig();

    public ConfigHandler(String id) {
        this.file = new File("config/" + id + ".json5");
    }

    public AHConfig getConfig() {
        return config;
    }

    public void loadConfig() {
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                config = GSON.fromJson(reader, AHConfig.class);
            } catch (IOException error) {
                ArcaneHand.LOGGER.error("Failed to load config: {}", error.getMessage());
            }
        }

        saveConfig();
    }

    private void saveConfig() {
        try {
            if (file.getParentFile().mkdirs()) {
                ArcaneHand.LOGGER.info("Creating parent directory for {} config", ArcaneHand.MOD_ID);
            }

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException error) {
            ArcaneHand.LOGGER.error("Failed to save config: {}", error.getMessage());
        }
    }
}

