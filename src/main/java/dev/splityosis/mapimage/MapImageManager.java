package dev.splityosis.mapimage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MapImageManager {

    public static void setup(JavaPlugin plugin){
        dataFile = new File(plugin.getDataFolder(), dataFileName);
        if (!dataFile.exists()){
            try{
                dataFile.createNewFile();
            }catch (IOException e){
            }
        }
        fileConfig = YamlConfiguration.loadConfiguration(dataFile);

        //Load images
        for (String key : getDataFileConfig().getKeys(false)){
            MapView mapView = Bukkit.getMap(Short.parseShort(key));
            if (mapView == null) continue;
            String url = getDataFileConfig().getString(String.valueOf(mapView.getId()));
            if (url == null) return;
            BufferedImage image;
            try {
                URL imageurl = new URL(url);
                image = ImageIO.read(imageurl);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            for (MapRenderer mr : mapView.getRenderers())
                mapView.removeRenderer(mr);
            mapView.addRenderer(new MapRenderer() {
                private boolean isRendered;
                private BufferedImage bufferedImage = image;
                @Override
                public void render(MapView map, MapCanvas canvas, Player player) {
                    if (isRendered) return;
                    bufferedImage = MapPalette.resizeImage(bufferedImage);
                    canvas.drawImage(0, 0, bufferedImage);
                    isRendered = true;
                }
            });
        }
    }

    public static MapView updateMapView(MapView mapView, URL imageURL){
        BufferedImage image;
        try {
            image = ImageIO.read(imageURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (MapRenderer mr : mapView.getRenderers())
            mapView.removeRenderer(mr);
        mapView.addRenderer(new imageMapRenderer(image));

        getDataFileConfig().set(String.valueOf(mapView.getId()), imageURL.toString());
        saveDataFile();

        return mapView;
    }

    public static MapView getMapView(URL imageURL){
        MapView mapView = Bukkit.createMap(Bukkit.getWorlds().get(0));
        return updateMapView(mapView, imageURL);
    }

    public static ItemStack getMapItem(URL imageURL){
        MapView mapView = getMapView(imageURL);
        ItemStack map = new ItemStack(Material.MAP, 1);
        map.setDurability(mapView.getId());
        return map;
    }

    //Data file
    private static final String dataFileName = "map-data.yml";
    private static File dataFile;
    private static FileConfiguration fileConfig;

    public static FileConfiguration getDataFileConfig(){
        return fileConfig;
    }

    private static void saveDataFile(){
        try{
            fileConfig.save(dataFile);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

    public static class imageMapRenderer extends MapRenderer{
        private boolean isRendered = false;
        private BufferedImage bufferedImage;

        public imageMapRenderer(BufferedImage bufferedImage) {
            this.bufferedImage = bufferedImage;
        }

        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
            if (isRendered) return;
            bufferedImage = MapPalette.resizeImage(bufferedImage);
            canvas.drawImage(0, 0, bufferedImage);
            isRendered = true;
        }
    }
}