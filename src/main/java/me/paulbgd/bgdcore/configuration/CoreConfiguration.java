package me.paulbgd.bgdcore.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CoreConfiguration extends ConfigurationFile {

    public CoreConfiguration(File file) {
        super(file);
    }

    public static List<String> Plugins_Not_To_Update = new ArrayList<>();

}
