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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class HealthySkill extends Skill implements Color, Listener {

    public HealthySkill() {
        setItemIdentifier(Arrays.asList("IRON_SWORD",
                "WOOD_SWORD", "STONE_SWORD", "DIAMOND_SWORD", "GOLD_SWORD",
                "WOOD_AXE", "STONE_AXE", "DIAMOND_AXE", "GOLD_AXE", "IRON_AXE"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @Override
    public void execute(PlayerInteractEvent event) {
        long now = System.currentTimeMillis();
        int seconds = config.getInt("skills.healthy.duration") * 1000;
        long until = now + seconds;
        FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
        CooldownManager cooldownManager = new CooldownManager(fileManager);
        event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "HEALTHY")));
        SkillPlugin.getInstance().pvpSkills.put(event.getPlayer(), until);
        cooldownManager.addPvpCooldown();

        int hearts = config.getInt("skills.healthy.levels." + fileManager.getLevel(SkillType.HEALTHY.getIdentifier()) + ".hearts");
        event.getPlayer().setMaxHealth(event.getPlayer().getMaxHealth() + hearts);
        event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
    }

    @EventHandler
    public void onHealthyQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FileManager fileManager = new FileManager(player.getUniqueId().toString());
        if (fileManager.getEnabledSkill(true) != null) {
            if (fileManager.getEnabledSkill(true).equals(SkillType.HEALTHY)) {
                player.setMaxHealth(20);
            }
        }
    }
}
