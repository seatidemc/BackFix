package top.seatide.BackFix;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Files {
    public static String cwd;
    public static FileConfiguration cfg;
    public static FileConfiguration backs;

    public static void init(Main plugin) {
        cwd = plugin.getDataFolder().getPath();
        cfg = plugin.getConfig();
        backs = load(".", "data.yml");
    }

    public static File getFile(File folder, String name) throws IOException {
        File file = new File(folder, name);
        if (!folder.exists()) {
            var state = folder.mkdir();
            if (!state) {
                throw new IOException("cannot create file directory automatically");
            }
        }
        if (!file.exists()) {
            try {
                var state = file.createNewFile();
                if (!state) {
                    throw new IOException("cannot create file automatically.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static FileConfiguration load(String path, String name) {
        try {
            return YamlConfiguration.loadConfiguration(getFile(
                    new File(path.replace(path.length() == 1 ? "." : "./", path.length() == 1 ? cwd : cwd + "/")),
                    name));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void save(FileConfiguration data, String targetFile) {
        try {
            data.save(targetFile.replace("./", cwd + "/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        cfg = load(".", "config.yml");
        backs = load(".", "data.yml");
    }
}
