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
import org.bukkit.entity.Fish;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.swing.tree.ExpandVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FishermanSkill extends Skill implements Color, Listener {

    public FishermanSkill() {
        setItemIdentifier(Arrays.asList("FISHING_ROD"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @Override
    public void execute(PlayerInteractEvent event) {
        long now = System.currentTimeMillis();
        int seconds = config.getInt("skills.fisherman.duration") * 1000;
        long until = now + seconds;
        FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
        CooldownManager cooldownManager = new CooldownManager(fileManager);
        event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "FISHERMAN")));
        SkillPlugin.getInstance().nonPvpSkills.put(event.getPlayer(), until);
        cooldownManager.addNonPvpCooldown();
    }

    @EventHandler
    public void onHit(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FileManager fileManager = new FileManager(player.getUniqueId().toString());
        if (fileManager.hasProfile()) {
            if (fileManager.getEnabledSkill(false) != null) {
                if (fileManager.getEnabledSkill(false).equals(SkillType.FISHERMAN)) {
                    if (event.getCaught() != null && event.getCaught() instanceof Item) {
                        if (SkillPlugin.getInstance().nonPvpSkills.containsKey(player)) {
                            int level = fileManager.getLevel(SkillType.FISHERMAN.getIdentifier());
                            for (String commands : config.getStringList("skills.fisherman.levels." + level + ".rewards")) {
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
