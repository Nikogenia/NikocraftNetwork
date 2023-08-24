package de.nikogenia.nnmaster.utils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public abstract class Configuration {

    public static Configuration load(String path, Class<?> type) {

        Configuration configuration = null;

        Yaml yaml = new Yaml();

        String fileName = path.split("/")[path.split("/").length - 1];
        String dirPath = path.replace(fileName, "");

        try {

            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, fileName);
            if (!file.exists()) file.createNewFile();

            InputStream in = new FileInputStream(file);

            configuration = (Configuration) yaml.loadAs(in, type);

        } catch(Exception e) {
            e.printStackTrace();
        }

        if (configuration == null) {
            try {
                configuration = (Configuration) type.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return configuration;

    }

    public void save(String path) {

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(options);
        representer.addClassTag(getClass(), Tag.MAP);

        Yaml yaml = new Yaml(representer, options);

        String fileName = path.split("/")[path.split("/").length - 1];
        String dirPath = path.replace(fileName, "");

        try {

            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, fileName);
            if (!file.exists()) file.createNewFile();

            PrintWriter out = new PrintWriter(file);

            yaml.dump(this, out);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
