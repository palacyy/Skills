package me.paper.skills.listeners;

import me.paper.skills.SkillPlugin;
import me.paper.skills.cooldown.CooldownManager;
import me.paper.skills.file.FileManager;
import me.paper.skills.inventory.CustomInventory;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.FileHandler;

public class InventoryEvent implements Listener, Color {

    private FileConfiguration fileConfiguration = SkillPlugin.getInstance().getConfig();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (event.getInventory().getName() != null) {
                String name = format(event.getInventory().getName());
                if (name.equals(format(fileConfiguration.getString("inventories.default.name")))) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() != null) {
                        ItemStack itemStack = event.getCurrentItem();
                        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                            String displayName = format(itemStack.getItemMeta().getDisplayName());
                            CustomInventory customInventory = new CustomInventory(player);
                            if (displayName.equals(format(fileConfiguration.getString("inventories.default.items.pvp_item.name")))) {
                                customInventory.initOverviewCombat();
                            } else if (displayName.equals(format(fileConfiguration.getString("inventories.default.items.nonPvp_item.name")))) {
                                customInventory.initOverviewNonCombat();
                            }
                        }
                    }
                }else  if (name.equals(format(fileConfiguration.getString("inventories.unlocked_pvp.name")))) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() != null) {
                        ItemStack itemStack = event.getCurrentItem();
                        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                            String displayName = format(itemStack.getItemMeta().getDisplayName());
                            CustomInventory customInventory = new CustomInventory(player);
                            if (displayName.equals(format(fileConfiguration.getString("inventories.back_item.name")))) {
                                customInventory.init();
                            } else {
                                String skillString = ChatColor.stripColor(displayName).toLowerCase();
                                FileManager fileHandler = new FileManager(player.getUniqueId().toString());
                                CooldownManager cooldownManager = new CooldownManager(fileHandler);
                                if (itemStack.getType() != Material.AIR) {
                                    if (cooldownManager.isOnPvpCooldown()) {
                                        player.sendMessage(format(fileConfiguration.getString("messages.cannot_select_skill")));
                                        return;
                                    }else {
                                        if (skillString.contains("bleed")) {
                                            if (fileHandler.isUnlocked(SkillType.BLEED.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(true) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(true).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.BLEED.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "BLEED")));
                                            }
                                        }else if (skillString.contains("bloodthirsty")) {
                                            if (fileHandler.isUnlocked(SkillType.BLOODTHIRSTY.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(true) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(true).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.BLOODTHIRSTY.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "BLOODTHIRSTY")));
                                            }
                                        }else if (skillString.contains("harmony")) {
                                            if (fileHandler.isUnlocked(SkillType.HARMONY.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(true) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(true).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.HARMONY.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "HARMONY")));
                                            }
                                        }else if (skillString.contains("healthy")) {
                                            if (fileHandler.isUnlocked(SkillType.HEALTHY.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(true) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(true).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.HEALTHY.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "HEALTHY")));
                                            }
                                        }else if (skillString.contains("infused")) {
                                            if (fileHandler.isUnlocked(SkillType.INFUSED.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(true) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(true).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.INFUSED.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "INFUSED")));
                                            }
                                        }else if (skillString.contains("protective")) {
                                            if (fileHandler.isUnlocked(SkillType.PROTECTIVE.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(true) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(true).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.PROTECTIVE.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "PROTECTIVE")));
                                            }
                                        }else if (skillString.contains("robust")) {
                                            if (fileHandler.isUnlocked(SkillType.ROBUST.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(true) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(true).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.ROBUST.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "ROBUST")));
                                            }
                                        }else if (skillString.contains("shattered")) {
                                            if (fileHandler.isUnlocked(SkillType.SHATTERED.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(true) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(true).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.SHATTERED.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "SHATTERED")));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else  if (name.equals(format(fileConfiguration.getString("inventories.unlocked_noPvp.name")))) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() != null) {
                        ItemStack itemStack = event.getCurrentItem();
                        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                            String displayName = format(itemStack.getItemMeta().getDisplayName());
                            CustomInventory customInventory = new CustomInventory(player);
                            if (displayName.equals(format(fileConfiguration.getString("inventories.back_item.name")))) {
                                customInventory.init();
                                return;
                            }
                                String skillString = ChatColor.stripColor(displayName).toLowerCase();
                                FileManager fileHandler = new FileManager(player.getUniqueId().toString());
                                CooldownManager cooldownManager = new CooldownManager(fileHandler);
                                if (itemStack.getType() != Material.AIR) {
                                    if (cooldownManager.isOnNonPvpCooldown()) {
                                        player.sendMessage(format(fileConfiguration.getString("messages.cannot_select_skill")));
                                        return;
                                    }else {
                                        if (skillString.contains("fisherman")) {
                                            if (fileHandler.isUnlocked(SkillType.FISHERMAN.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(false) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(false).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.FISHERMAN.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "FISHERMAN")));
                                            }
                                        }else if (skillString.contains("miner")) {
                                            if (fileHandler.isUnlocked(SkillType.MINER.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(false) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(false).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.MINER.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "MINER")));
                                            }
                                        }else if (skillString.contains("farmer")) {
                                            if (fileHandler.isUnlocked(SkillType.FARMER.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(false) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(false).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.FARMER.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "FARMER")));
                                            }
                                        }else if (skillString.contains("grinder")) {
                                            if (fileHandler.isUnlocked(SkillType.GRINDER.getIdentifier())) {
                                                if (fileHandler.getEnabledSkill(false) != null) {
                                                    fileHandler.setStatus(fileHandler.getEnabledSkill(false).getIdentifier(), false);
                                                }
                                                fileHandler.setStatus(SkillType.GRINDER.getIdentifier(), true);
                                                player.sendMessage(format(fileConfiguration.getString("messages.selected_skill").replaceAll("%skill%", "GRINDER")));
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
    }
