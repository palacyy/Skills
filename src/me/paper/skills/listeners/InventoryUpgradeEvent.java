package me.paper.skills.listeners;

import me.paper.skills.SkillPlugin;
import me.paper.skills.file.FileManager;
import me.paper.skills.inventory.UpgradeInventory;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUpgradeEvent implements Listener, Color {

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @EventHandler
    public void onUpgrade(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && event.getInventory() != null) {
            Player player = (Player) event.getWhoClicked();
            Inventory inventory = event.getInventory();
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                ItemStack itemStack = event.getCurrentItem();
                String name = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).toLowerCase();
                if (inventory.getName() != null) {
                    String upgradeName = format(config.getString("inventories.upgrade.name"));
                    if (format(inventory.getName()).equalsIgnoreCase(upgradeName)) {
                        event.setCancelled(true);
                        FileManager fileManager = new FileManager(player.getUniqueId().toString());
                        for (SkillType skillType : SkillType.values()) {
                            if (name.contains(skillType.getIdentifier())) {
                                if (config.getInt("skills." + skillType.getIdentifier() + ".max_level") != fileManager.getLevel(skillType.getIdentifier())) {
                                    int nextLevel = fileManager.getLevel(skillType.getIdentifier()) + 1;
                                    int cost = config.getInt("skills." + skillType.getIdentifier() + ".levels." + nextLevel + ".cost");
                                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                                    if (SkillPlugin.getInstance().getEconomy().getBalance(offlinePlayer) >= cost) {
                                        if (!fileManager.isUnlocked(skillType.getIdentifier())) {
                                            fileManager.setUnlocked(skillType.getIdentifier());
                                        }
                                        SkillPlugin.getInstance().getEconomy().withdrawPlayer(offlinePlayer, cost);
                                        fileManager.setLevel(skillType.getIdentifier(), nextLevel);
                                        UpgradeInventory upgradeInventory = new UpgradeInventory(player);
                                        upgradeInventory.open();
                                        player.sendMessage(format(config.getString("messages.bought_upgrade").replaceAll("%skill%", skillType.getIdentifier().toUpperCase()))
                                        .replaceAll("%level%", fileManager.getLevel(skillType.getIdentifier()) + ""));
                                    }else {
                                        player.sendMessage(format(config.getString("messages.cannot_afford")));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
