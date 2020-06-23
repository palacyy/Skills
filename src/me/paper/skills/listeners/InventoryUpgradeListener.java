package me.paper.skills.listeners;

import jdk.Exported;
import me.paper.skills.SkillPlugin;
import me.paper.skills.file.FileManager;
import me.paper.skills.inventory.UpgradeInventory;
import me.paper.skills.utils.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InventoryUpgradeListener implements Listener, Color {

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        FileManager fileManager = new FileManager(player.getUniqueId().toString());
        if (event.getRightClicked() != null && event.getRightClicked().getCustomName() != null) {
            if (fileManager.hasProfile()) {
                String entityName = format(entity.getCustomName());
                String requiredName = format(config.getString("upgrade_npc.name"));
                if (entityName.equalsIgnoreCase(requiredName)) {
                    UpgradeInventory upgradeInventory = new UpgradeInventory(player);
                    upgradeInventory.open();
                }
            } else {
                fileManager.setup();
            }
        }
    }
}
