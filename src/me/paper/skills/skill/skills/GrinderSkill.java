package me.paper.skills.skill.skills;

import me.paper.skills.SkillPlugin;
import me.paper.skills.cooldown.CooldownManager;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.Skill;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GrinderSkill extends Skill implements Color, Listener {

    public GrinderSkill() {
        setItemIdentifier(Arrays.asList("IRON_SWORD",
                "WOOD_SWORD", "STONE_SWORD", "DIAMOND_SWORD", "GOLD_SWORD",
                "WOOD_AXE", "STONE_AXE", "DIAMOND_AXE", "GOLD_AXE", "IRON_AXE"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @Override
    public void execute(PlayerInteractEvent event) {
        long now = System.currentTimeMillis();
        int seconds = config.getInt("skills.grinder.duration") * 1000;
        long until = now + seconds;
        FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
        CooldownManager cooldownManager = new CooldownManager(fileManager);
        event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "GRINDER")));
        SkillPlugin.getInstance().nonPvpSkills.put(event.getPlayer(), until);
        cooldownManager.addNonPvpCooldown();
    }

    @EventHandler
    public void onInteract(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player) && event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            FileManager fileManager = new FileManager(player.getUniqueId().toString());
                    if (fileManager.hasProfile()) {
                        if (fileManager.getEnabledSkill(false) != null) {
                            if (fileManager.getEnabledSkill(false).equals(SkillType.GRINDER)) {
                                if (SkillPlugin.getInstance().nonPvpSkills.containsKey(player)) {
                                    int level = fileManager.getLevel(SkillType.GRINDER.getIdentifier());
                                    for (String commands : config.getStringList("skills.grinder.levels." + level + ".rewards")) {
                                        Random random = new Random();
                                        int chance = random.nextInt(100);
                                        String[] split = commands.split(":");
                                        int commandChance = Integer.parseInt(split[1]);
                                        if (chance <= commandChance) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), split[0].replaceAll("%player%", player.getName()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
