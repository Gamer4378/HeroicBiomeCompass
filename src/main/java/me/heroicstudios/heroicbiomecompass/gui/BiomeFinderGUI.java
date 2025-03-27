package me.heroicstudios.heroicbiomecompass.gui;

import me.heroicstudios.heroicbiomecompass.HeroicBiomeCompass;
import me.heroicstudios.heroicbiomecompass.managers.ConfigManager.BiomeMenuData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BiomeFinderGUI {
    private final HeroicBiomeCompass plugin;
    private final NamespacedKey biomeKey;
    private final NamespacedKey pageKey;
    private static final int GUI_SIZE = 54;

    public BiomeFinderGUI(HeroicBiomeCompass plugin) {
        this.plugin = plugin;
        this.biomeKey = new NamespacedKey(plugin, "biome_key");
        this.pageKey = new NamespacedKey(plugin, "page");
    }

    public void openInventory(Player player, int page) {
        // Get biomes for the current page
        List<String> pageBiomes = plugin.getConfigManager().getBiomesForPage(page);
        int totalPages = plugin.getConfigManager().getTotalPages();

        // Validate page number
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        // Create inventory with page number in title
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getMenuTitle() + " &7(Page " + page + "/" + totalPages + ")");
        Inventory inventory = Bukkit.createInventory(null, GUI_SIZE, title);

        // Fill background with glass panes
        ItemStack filler = createFillerItem();
        for (int i = 0; i < GUI_SIZE; i++) {
            inventory.setItem(i, filler);
        }

        // Add biomes to their specific slots
        for (String biomeName : pageBiomes) {
            BiomeMenuData menuData = plugin.getConfigManager().getBiomeMenuData(biomeName);
            if (menuData == null || menuData.getPage() != page) continue;

            int slot = menuData.getSlot();
            if (slot >= 0 && slot < GUI_SIZE) {
                ItemStack item = createBiomeItem(biomeName, menuData);
                if (item != null) {
                    inventory.setItem(slot, item);
                }
            }
        }

        // Add navigation items
        addNavigationItems(inventory, page, totalPages);

        player.openInventory(inventory);
    }

    private ItemStack createBiomeItem(String biomeName, BiomeMenuData menuData) {
        ItemStack item = new ItemStack(menuData.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            plugin.getLogger().warning("Failed to create item meta for biome: " + biomeName);
            return null;
        }

        meta.setDisplayName(menuData.getDisplayName());
        meta.setLore(menuData.getLore());

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(biomeKey, PersistentDataType.STRING, biomeName);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createFillerItem() {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }
        return filler;
    }

    private void addNavigationItems(Inventory inventory, int currentPage, int totalPages) {
        // Previous page button (slot 48)
        if (currentPage > 1) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
                List<String> prevLore = new ArrayList<>();
                prevLore.add(ChatColor.GRAY + "Click to go to page " + (currentPage - 1));
                prevMeta.setLore(prevLore);
                prevMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, currentPage - 1);
                prevButton.setItemMeta(prevMeta);
                inventory.setItem(48, prevButton);
            }
        }

        // Current page indicator (slot 49)
        ItemStack pageIndicator = new ItemStack(Material.PAPER);
        ItemMeta indicatorMeta = pageIndicator.getItemMeta();
        if (indicatorMeta != null) {
            indicatorMeta.setDisplayName(ChatColor.YELLOW + "Page " + currentPage + " of " + totalPages);
            pageIndicator.setItemMeta(indicatorMeta);
            inventory.setItem(49, pageIndicator);
        }

        // Next page button (slot 50)
        if (currentPage < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
                List<String> nextLore = new ArrayList<>();
                nextLore.add(ChatColor.GRAY + "Click to go to page " + (currentPage + 1));
                nextMeta.setLore(nextLore);
                nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, currentPage + 1);
                nextButton.setItemMeta(nextMeta);
                inventory.setItem(50, nextButton);
            }
        }
    }

    public NamespacedKey getBiomeKey() {
        return biomeKey;
    }

    public NamespacedKey getPageKey() {
        return pageKey;
    }
}