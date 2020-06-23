package me.paper.skills.skill;

import me.paper.skills.SkillPlugin;
import me.paper.skills.cooldown.CooldownManager;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.skills.*;
import me.paper.skills.utils.Color;
import me.paper.skills.utils.TimeUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class SkillManager implements Listener, Color {

    private Map<SkillType, Skill> skills = new HashMap<>();
    private FileConfiguration fileConfiguration = SkillPlugin.getInstance().getConfig();

    public SkillManager() {
        skills.put(SkillType.BLOODTHIRSTY, new BloodthirstySkill());
        skills.put(SkillType.INFUSED, new InfusedSkill());
        skills.put(SkillType.PROTECTIVE, new ProtectiveSkill());
        skills.put(SkillType.HEALTHY, new HealthySkill());
        skills.put(SkillType.ROBUST, new RobustSkill());
        skills.put(SkillType.HARMONY, new HarmonySkill());
        skills.put(SkillType.BLEED, new BleedSkill());
        skills.put(SkillType.SHATTERED, new ShatteredSkill());
        skills.put(SkillType.FISHERMAN, new FishermanSkill());
        skills.put(SkillType.MINER, new MinerSkill());
        skills.put(SkillType.FARMER, new FarmerSkill());
        skills.put(SkillType.GRINDER, new GrinderSkill());
    }

    @EventHandler
    public void onExecute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        FileManager fileManager = new FileManager(player.getUniqueId().toString());

        if (event.getAction().name().contains("RIGHT")) {
            SkillType combatSkill = fileManager.getEnabledSkill(true);
            SkillType notCombatSkill = fileManager.getEnabledSkill(false);
            Skill combatType = getSkillFromType(combatSkill);
            Skill notCombatType = getSkillFromType(notCombatSkill);
            if (combatType != null) {
                if (event.getItem() != null) {
                    for (String identifiers : combatType.getItemIdentifier()) {
                        if (event.getItem().getType().toString().equalsIgnoreCase(identifiers)) {
                            CooldownManager cooldownManager = new CooldownManager(fileManager);
                            if (cooldownManager.isOnPvpCooldown()) {
                                if (!SkillPlugin.getInstance().pvpSkills.containsKey(player)) {
                                    player.sendMessage(format(fileConfiguration.getString("messages.skill_cooldown.pvp").replaceAll("%time%",
                                            TimeUtils.format(cooldownManager.getCooldown(true) - System.currentTimeMillis()))));
                                    return;
                                }
                            }else {
                                combatType.execute(event);
                                return;
                            }
                        }else {
                            continue;
                        }
                    }
                }
            }
            if (notCombatType != null) {
                if (event.getItem() != null) {
                    for (String identifiers : notCombatType.getItemIdentifier()) {
                        if (event.getItem().getType().toString().equalsIgnoreCase(identifiers)) {
                            CooldownManager cooldownManager = new CooldownManager(fileManager);
                            if (cooldownManager.isOnNonPvpCooldown()) {
                                if (!SkillPlugin.getInstance().nonPvpSkills.containsKey(player)) {
                                    player.sendMessage(format(fileConfiguration.getString("messages.skill_cooldown.nonPvp").replaceAll("%time%",
                                            TimeUtils.format(cooldownManager.getCooldown(false) - System.currentTimeMillis()))));
                                    return;
                                }
                            }else {
                                notCombatType.execute(event);
                                return;
                            }
                        }else {
                            continue;
                        }
                    }
                }
            }
        }
    }

    public Skill getSkillFromType(SkillType skillType) {
        return skills.get(skillType);
    }
}
