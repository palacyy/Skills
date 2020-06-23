package me.paper.skills.skill.skills;

import me.paper.skills.SkillPlugin;
import me.paper.skills.cooldown.CooldownManager;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.Skill;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.swing.*;
import javax.swing.tree.ExpandVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProtectiveSkill extends Skill implements Color, Listener {

    public ProtectiveSkill() {
        setItemIdentifier(Arrays.asList("IRON_SWORD",
                "WOOD_SWORD", "STONE_SWORD", "DIAMOND_SWORD", "GOLD_SWORD",
                "WOOD_AXE", "STONE_AXE", "DIAMOND_AXE", "GOLD_AXE", "IRON_AXE"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();
    public static HashMap<Player, ItemStack[]> armorSave = new HashMap<>();

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (hasArmor(player)) {
            long now = System.currentTimeMillis();
            int seconds = config.getInt("skills.protective.duration") * 1000;
            long until = now + seconds;
            FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
            CooldownManager cooldownManager = new CooldownManager(fileManager);
            event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "PROTECTED")));
            SkillPlugin.getInstance().pvpSkills.put(event.getPlayer(), until);
            cooldownManager.addPvpCooldown();

            ItemStack[] armor = player.getInventory().getArmorContents();
            fileManager.setArmor(armor);

            for (ItemStack currentArmor : player.getInventory().getArmorContents()) {
                currentArmor.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, config.getInt("skills.protective.levels." + fileManager
                .getLevel(SkillType.PROTECTIVE.getIdentifier()) + ".protection"));
            }
        }
    }

    @EventHandler
    public void onProtectiveClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            FileManager fileManager = new FileManager(player.getUniqueId().toString());
            if (fileManager.getEnabledSkill(true) != null) {
                if (fileManager.getEnabledSkill(true).equals(SkillType.PROTECTIVE)) {
                    if (SkillPlugin.getInstance().pvpSkills.containsKey(player)) {
                        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                            event.setCancelled(true);
                            SkillPlugin.getInstance().pvpSkills.remove(player);
                            player.getInventory().setArmorContents(fileManager.getArmor());
                            fileManager.setArmor(null);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
            FileManager fileManager = new FileManager(player.getUniqueId().toString());
            if (fileManager.getEnabledSkill(true) != null) {
                if (fileManager.getEnabledSkill(true).equals(SkillType.PROTECTIVE)) {
                    if (SkillPlugin.getInstance().pvpSkills.containsKey(player)) {
                        SkillPlugin.getInstance().pvpSkills.remove(player);
                        player.getInventory().setArmorContents(fileManager.getArmor());
                        fileManager.setArmor(null);
                    }
                }
            }
    }

    private boolean hasArmor(Player player) {
        int armorCount = 0;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor.getType() != Material.AIR) {
                armorCount++;
            }
        }
        if (armorCount == 0) {
            return false;
        }else {
            return true;
        }
    }
}
