package me.heroicstudios.heroicbiomecompass.managers;

import me.heroicstudios.heroicbiomecompass.HeroicBiomeCompass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CompassManager {
    private final HeroicBiomeCompass plugin;
    private final NamespacedKey compassKey;

    public CompassManager(HeroicBiomeCompass plugin) {
        this.plugin = plugin;
        this.compassKey = new NamespacedKey(plugin, "biome_compass");
    }

    public ItemStack createBiomeCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        
        meta.setDisplayName(plugin.getConfigManager().getCompassName());
        meta.setLore(plugin.getConfigManager().getCompassLore());
        
        compass.setItemMeta(meta);
        return compass;
    }

    public void registerCraftingRecipe() {
        if (!plugin.getConfig().getBoolean("crafting.enabled", true)) {
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(compassKey, createBiomeCompass());
        
        recipe.shape("DCD", "ENE", "DCD");
        
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('C', Material.COMPASS);
        recipe.setIngredient('E', Material.EMERALD);
        recipe.setIngredient('N', Material.NETHER_STAR);
        
        Bukkit.addRecipe(recipe);
    }

    public boolean isBiomeCompass(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && 
               meta.getDisplayName().equals(plugin.getConfigManager().getCompassName());
    }
}