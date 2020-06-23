package me.paper.skills.inventory;

import me.paper.skills.SkillPlugin;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UpgradeInventory implements Color {

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    private Player player;
    private Inventory inventory;

    public UpgradeInventory(Player player) {
        this.player = player;
    }

    public void open() {
        FileManager fileManager = new FileManager(this.player.getUniqueId().toString());
        this.inventory = Bukkit.createInventory(null, 18, format(config.getString("inventories.upgrade.name")));

        ArrayList<ItemStack> skillItems = new ArrayList<>();

        for (SkillType skillType : SkillType.values()) {
            ItemStack itemStack = new ItemStack(Material.BOOK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.valueOf(config.getString("skills." + skillType.getIdentifier() + ".color")) + skillType.getIdentifier().toUpperCase());
            ArrayList<String> finalLore = new ArrayList<>();
            int nextLevel = fileManager.getLevel(skillType.getIdentifier())+1;
            if (nextLevel > config.getInt("skills." + skillType.getIdentifier() + ".max_level")) {
                for (String string : config.getStringList("inventories.upgrade.skill_lore")) {
                    finalLore.add(format(string).replaceAll("%level%", "MAX (" + fileManager.getLevel(skillType.getIdentifier()) + ")")
                            .replaceAll("%cost%", 0 + ""));
                }
            }else {
                for (String string : config.getStringList("inventories.upgrade.skill_lore")) {
                    finalLore.add(format(string).replaceAll("%level%", fileManager.getLevel(skillType.getIdentifier()) + "")
                            .replaceAll("%cost%", config.getInt("skills." + skillType.getIdentifier() + ".levels." + nextLevel + ".cost") + ""));
                }
            }
            itemMeta.setLore(finalLore);
            itemStack.setItemMeta(itemMeta);
            skillItems.add(itemStack);
        }
        for (int x = 0; x < SkillType.values().length; x++) {
            this.inventory.setItem(x, skillItems.get(x));
        }

        this.player.openInventory(this.inventory);
    }

}
