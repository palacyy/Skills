package me.paper.skills.skill.skills;

import me.paper.skills.SkillPlugin;
import me.paper.skills.cooldown.CooldownManager;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.Skill;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class BloodthirstySkill extends Skill implements Color, Listener {

    public BloodthirstySkill() {
        setItemIdentifier(Arrays.asList("IRON_SWORD",
                "WOOD_SWORD", "STONE_SWORD", "DIAMOND_SWORD", "GOLD_SWORD",
                "WOOD_AXE", "STONE_AXE", "DIAMOND_AXE", "GOLD_AXE", "IRON_AXE"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @Override
    public void execute(PlayerInteractEvent event) {
        long now = System.currentTimeMillis();
        int seconds = config.getInt("skills.bloodthirsty.duration") * 1000;
        long until = now + seconds;
        FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
        CooldownManager cooldownManager = new CooldownManager(fileManager);
        event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "BLOODTHIRSTY")));
        SkillPlugin.getInstance().pvpSkills.put(event.getPlayer(), until);
        cooldownManager.addPvpCooldown();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            FileManager fileManager = new FileManager(damager.getUniqueId().toString());
            if (fileManager.hasProfile()) {
                if (fileManager.getEnabledSkill(true) != null) {
                    if (fileManager.getEnabledSkill(true).equals(SkillType.BLOODTHIRSTY)) {
                        if (SkillPlugin.getInstance().pvpSkills.containsKey(damager)) {
                            double health = damaged.getHealth();
                            int level = fileManager.getLevel(fileManager.getEnabledSkill(true).getIdentifier());
                            if (health <= config.getInt("skills.bloodthirsty.health_percentage")) {
                                double damagePercent = (event.getDamage() * config.getInt("skills.bloodthirsty.levels." + level + ".damage")) * 0.01;
                                event.setDamage(event.getFinalDamage() + damagePercent);
                            }
                        }
                    }
                }
            }
        }
    }
}
