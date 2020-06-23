package me.paper.skills;

import me.paper.skills.commands.SkillsCommand;
import me.paper.skills.file.FileManager;
import me.paper.skills.listeners.InventoryEvent;
import me.paper.skills.listeners.InventoryUpgradeEvent;
import me.paper.skills.listeners.InventoryUpgradeListener;
import me.paper.skills.skill.SkillManager;
import me.paper.skills.skill.SkillType;
import me.paper.skills.skill.skills.*;
import me.paper.skills.utils.Color;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SkillPlugin extends JavaPlugin implements Color {

    private static SkillPlugin instance;
    private Economy economy;

    public Map<Player, Long> pvpSkills = new HashMap<>();
    public Map<Player, Long> nonPvpSkills = new HashMap<>();

    public Map<Player, HashMap<Player, Long>> bleedMap = new HashMap<>();

    public void onEnable() {
        instance = this;
        if (!setupEconomy()) {
            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Skills requires Vault in order to work properly!");
            return;
        }
        setupBleedRunnable();
        setupRunnable();
        initConfiguration();
        initCommands();
        initListeners();
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Skills by Paper has successfully been enabled.");
    }

    private void setupRunnable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player pvpPlayers : pvpSkills.keySet()) {
                    FileManager fileManager = new FileManager(pvpPlayers.getUniqueId().toString());
                    long now = System.currentTimeMillis();
                    long until = pvpSkills.get(pvpPlayers);
                    if (until <= now) {
                        if (fileManager.getEnabledSkill(true).equals(SkillType.PROTECTIVE)) {
                            ItemStack[] armor = fileManager.getArmor();
                            pvpPlayers.getInventory().setArmorContents(armor);
                            fileManager.setArmor(null);
                            pvpSkills.remove(pvpPlayers);
                            pvpPlayers.sendMessage(format(getConfig().getString("messages.disabled_skill").replaceAll("%skill%", fileManager.getEnabledSkill(true).getIdentifier().toUpperCase())));
                        }else if (fileManager.getEnabledSkill(true).equals(SkillType.HEALTHY)){
                            pvpPlayers.setMaxHealth(20);
                            pvpSkills.remove(pvpPlayers);
                            pvpPlayers.sendMessage(format(getConfig().getString("messages.disabled_skill").replaceAll("%skill%", fileManager.getEnabledSkill(true).getIdentifier().toUpperCase())));
                        }else if (fileManager.getEnabledSkill(true).equals(SkillType.ROBUST)){
                            pvpPlayers.setWalkSpeed((float) 0.2);
                            pvpSkills.remove(pvpPlayers);
                            pvpPlayers.sendMessage(format(getConfig().getString("messages.disabled_skill").replaceAll("%skill%", fileManager.getEnabledSkill(true).getIdentifier().toUpperCase())));
                        }else if (fileManager.getEnabledSkill(true).equals(SkillType.HARMONY)){
                            pvpPlayers.setMaxHealth(20);
                            pvpSkills.remove(pvpPlayers);
                            pvpPlayers.sendMessage(format(getConfig().getString("messages.disabled_skill").replaceAll("%skill%", fileManager.getEnabledSkill(true).getIdentifier().toUpperCase())));
                        }else {
                            pvpSkills.remove(pvpPlayers);
                            pvpPlayers.sendMessage(format(getConfig().getString("messages.disabled_skill").replaceAll("%skill%", fileManager.getEnabledSkill(true).getIdentifier().toUpperCase())));
                        }
                    }
                }
                for (Player pvpPlayers : nonPvpSkills.keySet()) {
                    FileManager fileManager = new FileManager(pvpPlayers.getUniqueId().toString());
                    long now = System.currentTimeMillis();
                    long until = nonPvpSkills.get(pvpPlayers);
                    if (until <= now) {
                        if (fileManager.getEnabledSkill(false).equals(SkillType.MINER)){
                            pvpPlayers.setItemInHand(fileManager.getItem());
                            fileManager.setItem(null);
                            nonPvpSkills.remove(pvpPlayers);
                            pvpPlayers.sendMessage(format(getConfig().getString("messages.disabled_skill").replaceAll("%skill%", fileManager.getEnabledSkill(false).getIdentifier().toUpperCase())));
                        }else if (fileManager.getEnabledSkill(false).equals(SkillType.FARMER)){
                            pvpPlayers.setItemInHand(fileManager.getItem());
                            fileManager.setItem(null);
                            nonPvpSkills.remove(pvpPlayers);
                            pvpPlayers.sendMessage(format(getConfig().getString("messages.disabled_skill").replaceAll("%skill%", fileManager.getEnabledSkill(false).getIdentifier().toUpperCase())));
                        }else {
                            nonPvpSkills.remove(pvpPlayers);
                            pvpPlayers.sendMessage(format(getConfig().getString("messages.disabled_skill").replaceAll("%skill%", fileManager.getEnabledSkill(false).getIdentifier().toUpperCase())));
                        }
                    }
                }
            }
        }, 20, 20);
    }

    private void setupBleedRunnable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    if (bleedMap.containsKey(onlinePlayers)) {
                        FileManager fileManager = new FileManager(onlinePlayers.getUniqueId().toString());
                        HashMap<Player, Long> bleedingFromPlayer = bleedMap.get(onlinePlayers);
                        for (Player player : bleedingFromPlayer.keySet()) {
                            if (bleedingFromPlayer.get(player) > System.currentTimeMillis()) {
                                if (player.getHealth() - getConfig().getInt("skills.bleed.levels." + fileManager.getLevel(SkillType.BLEED.getIdentifier()) + ".damage") <= 0) {
                                    player.setHealth(0);
                                    bleedingFromPlayer.remove(player);
                                }else {
                                    player.setHealth(player.getHealth() - getConfig().getInt("skills.bleed.levels." + fileManager.getLevel(SkillType.BLEED.getIdentifier()) + ".damage"));
                                    player.playEffect(EntityEffect.HURT);
                                    player.playSound(player.getLocation(), Sound.HURT_FLESH, 1, 1);
                                }
                            }else {
                                if (bleedingFromPlayer.keySet().size() == 1) {
                                    bleedMap.remove(player);
                                }else {
                                    bleedingFromPlayer.remove(player);
                                }
                            }
                        }
                    }
                }
            }
        }, 20, 20);
    }

    public void onDisable() {
        instance = null;
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Skills by Paper has successfully been disabled.");
    }

    private void initCommands() {
        getCommand("SKILLS").setExecutor(new SkillsCommand());
    }

    private void initListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryEvent(), this);
        pluginManager.registerEvents(new SkillManager(), this);
        pluginManager.registerEvents(new BloodthirstySkill(), this);
        pluginManager.registerEvents(new InfusedSkill(), this);
        pluginManager.registerEvents(new HarmonySkill(), this);
        pluginManager.registerEvents(new BleedSkill(), this);
        pluginManager.registerEvents(new ShatteredSkill(), this);
        pluginManager.registerEvents(new FishermanSkill(), this);
        pluginManager.registerEvents(new MinerSkill(), this);
        pluginManager.registerEvents(new FarmerSkill(), this);
        pluginManager.registerEvents(new GrinderSkill(), this);
        pluginManager.registerEvents(new InventoryUpgradeListener(), this);
        pluginManager.registerEvents(new InventoryUpgradeEvent(), this);
        pluginManager.registerEvents(new HealthySkill(), this);
        pluginManager.registerEvents(new ProtectiveSkill(), this);
        pluginManager.registerEvents(new RobustSkill(), this);
    }

    private void initConfiguration() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public static SkillPlugin getInstance() {
        return instance;
    }
}
