package me.paper.skills.skill.skills;

import me.paper.skills.SkillPlugin;
import me.paper.skills.cooldown.CooldownManager;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.Skill;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class HarmonySkill extends Skill implements Color, Listener {

    public HarmonySkill() {
        setItemIdentifier(Arrays.asList("IRON_SWORD",
                "WOOD_SWORD", "STONE_SWORD", "DIAMOND_SWORD", "GOLD_SWORD",
                "WOOD_AXE", "STONE_AXE", "DIAMOND_AXE", "GOLD_AXE", "IRON_AXE"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @Override
    public void execute(PlayerInteractEvent event) {
        long now = System.currentTimeMillis();
        int seconds = config.getInt("skills.harmony.duration") * 1000;
        long until = now + seconds;
        FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
        CooldownManager cooldownManager = new CooldownManager(fileManager);
        event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "HARMONY")));
        SkillPlugin.getInstance().pvpSkills.put(event.getPlayer(), until);
        cooldownManager.addPvpCooldown();

        int level = fileManager.getLevel(fileManager.getEnabledSkill(true).getIdentifier());
        event.getPlayer().setMaxHealth(event.getPlayer().getMaxHealth() + config.getInt("skills.harmony.levels." + level + ".hearts"));
        event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            FileManager fileManager = new FileManager(damager.getUniqueId().toString());
            if (fileManager.hasProfile()) {
                if (fileManager.getEnabledSkill(true) != null) {
                    if (fileManager.getEnabledSkill(true).equals(SkillType.HARMONY)) {
                        if (SkillPlugin.getInstance().pvpSkills.containsKey(damager)) {
                            int level = fileManager.getLevel(fileManager.getEnabledSkill(true).getIdentifier());
                            double damagePercent = (event.getDamage() * config.getInt("skills.harmony.levels." + level + ".damage")) * 0.01;
                            event.setDamage(event.getFinalDamage() + damagePercent);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArmor(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            FileManager fileManager = new FileManager(player.getUniqueId().toString());
            if (fileManager.hasProfile()) {
                if (fileManager.getEnabledSkill(true) != null) {
                    if (fileManager.getEnabledSkill(true).equals(SkillType.HARMONY)) {
                        if (SkillPlugin.getInstance().pvpSkills.containsKey(player)) {
                            int level = fileManager.getLevel(fileManager.getEnabledSkill(true).getIdentifier());
                            double damagePercent = (event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * config.getInt("skills.harmony.levels." + level + ".armor")) * 0.01;
                            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, event.getDamage(EntityDamageEvent.DamageModifier.ARMOR)-damagePercent);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHarmonyQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FileManager fileManager = new FileManager(player.getUniqueId().toString());
        if (fileManager.getEnabledSkill(true) != null) {
            if (fileManager.getEnabledSkill(true).equals(SkillType.HARMONY)) {
                player.setMaxHealth(20);
            }
        }
    }
}
