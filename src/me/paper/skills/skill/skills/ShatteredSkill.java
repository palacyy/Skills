package me.paper.skills.skill.skills;

import me.paper.skills.SkillPlugin;
import me.paper.skills.cooldown.CooldownManager;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.Skill;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class ShatteredSkill extends Skill implements Listener, Color {

    public ShatteredSkill() {
        setItemIdentifier(Arrays.asList(
                "WOOD_AXE", "STONE_AXE", "DIAMOND_AXE", "GOLD_AXE", "IRON_AXE"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @Override
    public void execute(PlayerInteractEvent event) {
        long now = System.currentTimeMillis();
        int seconds = config.getInt("skills.shattered.duration") * 1000;
        long until = now + seconds;
        FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
        CooldownManager cooldownManager = new CooldownManager(fileManager);
        event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "SHATTERED")));
        SkillPlugin.getInstance().pvpSkills.put(event.getPlayer(), until);
        cooldownManager.addPvpCooldown();
    }

    @EventHandler
    public void onArmor(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            FileManager fileManager = new FileManager(damager.getUniqueId().toString());
            if (fileManager.hasProfile()) {
                if (fileManager.getEnabledSkill(true) != null) {
                    if (fileManager.getEnabledSkill(true).equals(SkillType.SHATTERED)) {
                        if (SkillPlugin.getInstance().pvpSkills.containsKey(damager)) {
                            int level = fileManager.getLevel(fileManager.getEnabledSkill(true).getIdentifier());
                            double damagePercent = (event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * config.getInt("skills.shattered.levels." + level + ".armor")) * 0.01;
                            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, event.getDamage(EntityDamageEvent.DamageModifier.ARMOR)+damagePercent);
                        }
                    }
                }
            }
        }
    }
}
