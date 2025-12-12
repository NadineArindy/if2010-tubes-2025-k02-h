package src.Game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import src.Ingredients.*;
import src.Item.*;

public class AssetManager {

    // ==== TILE / STATION IMAGE ====
    public static BufferedImage floor, wall, mapBackground;
    public static BufferedImage tileA, tileC, tileR, tileS, tileT, tileP, tileX;
    public static BufferedImage I_RICE, I_NORI, I_CUCUMBER, I_FISH, I_SHRIMP;
    public static BufferedImage washingSink; // W kanan
    public static BufferedImage washingRack; // W kiri 

    // ==== CHEF ICON ====
    public static BufferedImage chef_A, chef_B;

    // ==== UTENSILS ====
    public static BufferedImage iconCleanPlate;
    public static BufferedImage iconDirtyPlate;
    public static BufferedImage iconBoilingPot;
    public static BufferedImage iconFryingPan;

    // ==== INGREDIENTS ====
    public static BufferedImage iconNori;
    public static BufferedImage iconRice;
    public static BufferedImage iconShrimpRaw;
    public static BufferedImage iconShrimpChopped;
    public static BufferedImage iconShrimpCooked;
    public static BufferedImage iconCucumberRaw;
    public static BufferedImage iconCucumberChopped;
    public static BufferedImage iconFishRaw;
    public static BufferedImage iconFishChopped;

    // ==== DISH ====
    public static BufferedImage iconKappaMaki;
    public static BufferedImage iconSakanaMaki;
    public static BufferedImage iconEbiMaki;
    public static BufferedImage iconFishCucumberRoll;

    static {
        try {
            // === TILES / STATIONS ====
            floor = load("/resources/assets/tiles/lantai.png");
            wall  = load("/resources/assets/tiles/X.png");
            mapBackground = load("/resources/assets/tiles/background.png");
            tileA = load("/resources/assets/tiles/A.png");
            tileC = load("/resources/assets/tiles/C.png");
            tileR = load("/resources/assets/tiles/R.png");
            tileS = load("/resources/assets/tiles/S.png");
            tileT = load("/resources/assets/tiles/T.png");
            washingSink = load("/resources/assets/tiles/washingSink.png");
            washingRack = load("/resources/assets/tiles/washingRack.png");
            tileP = load("/resources/assets/tiles/P.png");
            tileX = load("/resources/assets/tiles/X.png");
            
            // === INGREDIENTS STATION ===
            I_RICE      = load("/resources/assets/tiles/I_rice.png");
            I_NORI      = load("/resources/assets/tiles/I_nori.png");
            I_CUCUMBER  = load("/resources/assets/tiles/I_cucumber.png");
            I_FISH      = load("/resources/assets/tiles/I_fish.png");
            I_SHRIMP    = load("/resources/assets/tiles/I_shrimp.png");

            // === CHEF ===
            chef_A   = load("/resources/assets/chef/chef1.png");
            chef_B   = load("/resources/assets/chef/chef2.png");

            // === UTENSILS ===
            iconCleanPlate = load("/resources/assets/utensils/plate.png");
            iconDirtyPlate = load("/resources/assets/utensils/dirtyPlate.png");
            iconBoilingPot = load("/resources/assets/utensils/boiling pot.png");
            iconFryingPan  = load("/resources/assets/utensils/frying pan.png");

            // === INGREDIENTS ===
            iconNori            = load("/resources/assets/ingredients/nori.png");
            iconRice            = load("/resources/assets/ingredients/rice.png");
            iconShrimpRaw       = load("/resources/assets/ingredients/shrimp.png");
            iconShrimpChopped   = load("/resources/assets/ingredients/shrimp chopped.png");
            iconShrimpCooked    = load("/resources/assets/ingredients/shrimp cooked.png");
            iconCucumberRaw     = load("/resources/assets/ingredients/cucumber.png");
            iconCucumberChopped = load("/resources/assets/ingredients/cucumber chopped.png");
            iconFishRaw         = load("/resources/assets/ingredients/fish.png");
            iconFishChopped     = load("/resources/assets/ingredients/fish chopped.png");

            // === DISH ===
            iconKappaMaki        = load("/resources/assets/dish/kappa maki.png");
            iconSakanaMaki       = load("/resources/assets/dish/sakana maki.png");
            iconEbiMaki          = load("/resources/assets/dish/ebi maki.png");
            iconFishCucumberRoll = load("/resources/assets/dish/fish cucumber roll.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper untuk load gambar dari path resource.
    private static BufferedImage load(String path) throws IOException {
        URL url = AssetManager.class.getResource(path);
        if (url == null) {
            System.err.println("Asset not found: " + path);
            return null;
        }
        return ImageIO.read(url);
    }

    // Ambil icon untuk sebuah item
    public static BufferedImage getItemIcon(Item item) {
        if (item == null) return null;

        // === PLATE ===
        if (item instanceof Plate plate) {
            // Piring kotor
            if (!plate.isClean()) {
                return iconDirtyPlate;  
            }

            // Jika belum ada isi -> piring polos
            if (plate.getContents() == null || plate.getContents().isEmpty()) {
                return iconCleanPlate;   
            }

            // Jika ada isi -> deteksi jenis sushi
            BufferedImage dishIcon = getDishIconFromPlate(plate);
            if (dishIcon != null) {
                return dishIcon;
            }

            // fallback kalau kombinasi gak cocok resep
            return iconCleanPlate;
        }

        // === UTENSILS ===
        if (item instanceof KitchenUtensils ku) {
            String n = ku.getName().toLowerCase();
            if (n.contains("boiling")) return iconBoilingPot;
            if (n.contains("frying")) return iconFryingPan;
            // Jika tidak dikenali, pakai icon plate sebagai fallback
            return iconCleanPlate; 
        }

        // === INGREDIENT ===
        if (item instanceof Ingredient ing) {
            return getIngredientIcon(ing);
        }

        // === DISH ===
        if (item instanceof Dish dish) {
            return getDishIcon(dish);
        }

        return null;
    }

    // Mengambil icon untuk Ingredient Storage berdasarkan class ingredient
    public static BufferedImage getIngredientStorageIcon(Class<? extends Preparable> cls) {
        if (cls == Rice.class)      return I_RICE;
        if (cls == Nori.class)      return I_NORI;
        if (cls == Cucumber.class)  return I_CUCUMBER;
        if (cls == Fish.class)      return I_FISH;
        if (cls == Shrimp.class)    return I_SHRIMP;
        
        //fallback
        return I_RICE; 
    }

    // Mengambil icon ingredient berdasarkan jenis dan state
    public static BufferedImage getIngredientIcon(Ingredient ing) {
        if (ing == null) return null;

        IngredientState st = ing.getState();

        if (ing instanceof Nori) {
            return iconNori;
        }
        if (ing instanceof Rice) {
            return iconRice;
        }
        if (ing instanceof Shrimp) {
            if (st == IngredientState.CHOPPED) return iconShrimpChopped;
            if (st == IngredientState.COOKED)  return iconShrimpCooked;
            return iconShrimpRaw;
        }
        if (ing instanceof Cucumber) {
            if (st == IngredientState.CHOPPED) return iconCucumberChopped;
            return iconCucumberRaw;
        }
        if (ing instanceof Fish) {
            if (st == IngredientState.CHOPPED) return iconFishChopped;
            return iconFishRaw;
        }

        return null;
    }

    // Mengambil icon untuk dish berdasarkan nama dish
    public static BufferedImage getDishIcon(Dish dish) {
        if (dish == null) return null;

        String n = dish.getName(); 
        if (n == null) return null;

        n = n.toLowerCase();
        if (n.contains("kappa")) return iconKappaMaki;
        if (n.contains("sakana")) return iconSakanaMaki;
        if (n.contains("ebi")) return iconEbiMaki;
        if (n.contains("fish cucumber roll")) return iconFishCucumberRoll;

        return null;
    }

    // Menentukan icon sushi dari isi piring
    private static BufferedImage getDishIconFromPlate(Plate plate) {
        var contents = plate.getContents();
        if (contents == null || contents.isEmpty()) return null;

        boolean hasNoriRaw = false;
        boolean hasRiceCooked = false;
        boolean hasCucumberChopped = false;
        boolean hasFishChopped = false;
        boolean hasShrimpCooked = false;

        for (Preparable p : contents) {
            if (!(p instanceof Ingredient ing)) continue;
            if (ing instanceof Nori && ing.getState() == IngredientState.RAW) hasNoriRaw = true;
            if (ing instanceof Rice && ing.getState() == IngredientState.COOKED) hasRiceCooked = true;
            if (ing instanceof Cucumber && ing.getState() == IngredientState.CHOPPED) hasCucumberChopped = true;
            if (ing instanceof Fish && ing.getState() == IngredientState.CHOPPED) hasFishChopped = true;
            if (ing instanceof Shrimp && ing.getState() == IngredientState.COOKED) hasShrimpCooked = true;
        }

        // === Kappa Maki: Nori (RAW) + Nasi (COOKED) + Timun (CHOPPED) ===
        if (hasNoriRaw && hasRiceCooked && hasCucumberChopped) {
            return iconKappaMaki;
        }

        // === Sakana Maki: Nori (RAW) + Nasi (COOKED) + Ikan (CHOPPED) ===
        if (hasNoriRaw && hasRiceCooked && hasFishChopped){
            return iconSakanaMaki;
        }

        // === Ebi Maki: Nori (RAW) + Nasi (COOKED) + Udang (COOKED) ===
        if (hasNoriRaw && hasRiceCooked && hasShrimpCooked) {
            return iconEbiMaki;
        }

        // === Fish Cucumber Roll: Nori (RAW) + Nasi (COOKED) + Ikan (CHOPPED) + Timun (CHOPPED) ===
        if (hasNoriRaw && hasRiceCooked && hasFishChopped && hasCucumberChopped) {
            return iconFishCucumberRoll;
        }

        // Selain resep diatas, tetap pakai plate biasa
        return null;
    }
}
