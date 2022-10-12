package dev.splityosis.mapimage;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        MapImageManager.setup(this);
        getCommand("mapimage").setExecutor(new MapImageCMD(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
