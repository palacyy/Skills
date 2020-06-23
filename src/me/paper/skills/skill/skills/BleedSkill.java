package me.paper.skills.skill.skills;

import me.paper.skills.SkillPlugin;
import me.paper.skills.cooldown.CooldownManager;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.Skill;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BleedSkill extends Skill implements Color, Listener {

    public BleedSkill() {
        setItemIdentifier(Arrays.asList("BOW"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @Override
    public void execute(PlayerInteractEvent event) {
        long now = System.currentTimeMillis();
        int seconds = config.getInt("skills.bleed.duration") * 1000;
        long until = now + seconds;
        FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
        CooldownManager cooldownManager = new CooldownManager(fileManager);
        event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "BLEED")));
        SkillPlugin.getInstance().pvpSkills.put(event.getPlayer(), until);
        cooldownManager.addPvpCooldown();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Arrow) {
                Player damaged = (Player) event.getEntity();
                Arrow arrow = (Arrow) event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Player damager = (Player) arrow.getShooter();
                    FileManager fileManager = new FileManager(damager.getUniqueId().toString());
                    ArrayList<Player> dList = new ArrayList<>();
                    if (fileManager.hasProfile()) {
                        if (fileManager.getEnabledSkill(true) != null) {
                            if (fileManager.getEnabledSkill(true).equals(SkillType.BLEED)) {
                                if (SkillPlugin.getInstance().pvpSkills.containsKey(damager)) {
                                    if (!SkillPlugin.getInstance().bleedMap.containsKey(damager)) {
                                        if (!isBleeding(damaged)) {
                                            HashMap<Player, Long> bleedingPlayersFromPlayer = new HashMap<>();
                                            bleedingPlayersFromPlayer.put(damaged, System.currentTimeMillis() + (1000*config.getInt("skills.bleed.bleed_duration")));
                                            SkillPlugin.getInstance().bleedMap.put(damager, bleedingPlayersFromPlayer);
                                        }
                                    }else {
                                        HashMap<Player, Long> bleedingPlayers = SkillPlugin.getInstance().bleedMap.get(damager);
                                        if (bleedingPlayers.keySet().size()+1 <= config.getInt("skills.bleed.max_players")) {
                                            bleedingPlayers.put(damaged, System.currentTimeMillis() + (1000*config.getInt("skills.bleed.bleed_duration")));
                                            SkillPlugin.getInstance().bleedMap.put(damager, bleedingPlayers);
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

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (SkillPlugin.getInstance().bleedMap.containsKey(players)) {
                HashMap<Player, Long> bleeding = SkillPlugin.getInstance().bleedMap.get(players);
                if (bleeding.containsKey(player)) {
                    if (bleeding.keySet().size() == 1) {
                        SkillPlugin.getInstance().bleedMap.remove(player);
                    }else {
                        bleeding.remove(players);
                    }
                }
            }
        }
    }

    private boolean isBleeding(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (SkillPlugin.getInstance().bleedMap.containsKey(players)) {
                HashMap<Player, Long> bleedPlayersFromPlayer = SkillPlugin.getInstance().bleedMap.get(players);
                for (Player bleedingPlayers : bleedPlayersFromPlayer.keySet()) {
                    if (player.equals(bleedingPlayers)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
