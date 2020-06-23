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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MinerSkill extends Skill implements Color, Listener {

    public MinerSkill() {
        setItemIdentifier(Arrays.asList("WOOD_PICKAXE", "STONE_PICKAXE", "DIAMOND_PICKAXE", "GOLD_PICKAXE", "IRON_PICKAXE"));
    }

    private FileConfiguration config = SkillPlugin.getInstance().getConfig();

    @Override
    public void execute(PlayerInteractEvent event) {
        long now = System.currentTimeMillis();
        int seconds = config.getInt("skills.miner.duration") * 1000;
        long until = now + seconds;
        FileManager fileManager = new FileManager(event.getPlayer().getUniqueId().toString());
        CooldownManager cooldownManager = new CooldownManager(fileManager);
        event.getPlayer().sendMessage(format(config.getString("messages.enabled_skill").replaceAll("%skill%", "MINER")));
        SkillPlugin.getInstance().nonPvpSkills.put(event.getPlayer(), until);
        cooldownManager.addNonPvpCooldown();

        int level = fileManager.getLevel(SkillType.MINER.getIdentifier());

        fileManager.setItem(event.getPlayer().getItemInHand());
        event.getPlayer().getItemInHand().addUnsafeEnchantment(Enchantment.DIG_SPEED, config.getInt("skills.miner.levels." + level + ".efficiency"));
        event.getPlayer().getItemInHand().addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, config.getInt("skills.miner.levels." + level + ".fortune"));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack.getType().toString().contains("PICKAXE")) {
            FileManager fileManager = new FileManager(player.getUniqueId().toString());
            if (fileManager.getEnabledSkill(false) != null) {
                if (fileManager.getEnabledSkill(false).equals(SkillType.MINER)) {
                    if (SkillPlugin.getInstance().nonPvpSkills.containsKey(player)) {
                        event.setCancelled(true);
                        SkillPlugin.getInstance().nonPvpSkills.remove(player);
                        player.setItemInHand(fileManager.getItem());
                        fileManager.setItem(null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHit(BlockBreakEvent event) {
        Player player = event.getPlayer();
        FileManager fileManager = new FileManager(player.getUniqueId().toString());
        if (fileManager.hasProfile()) {
            if (fileManager.getEnabledSkill(false) != null) {
                if (fileManager.getEnabledSkill(false).equals(SkillType.MINER)) {
                        if (SkillPlugin.getInstance().nonPvpSkills.containsKey(player)) {
                            int level = fileManager.getLevel(SkillType.MINER.getIdentifier());
                            for (String commands : config.getStringList("skills.miner.levels." + level + ".rewards")) {
                                Random random = new Random();
                                int chance = random.nextInt(100);
                                String[] split = commands.split(":");
                                int commandChance = Integer.parseInt(split[1]);
                                if (chance <= commandChance) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), split[0].replaceAll("%player%", player.getName()));
                                }
                            }
                            for (Block blocks : getBlocks(event.getBlock(), config.getInt("skills.miner.levels." + level + ".radius"))) {
                                blocks.getWorld().getBlockAt(blocks.getLocation()).breakNaturally();
                            }
                        }
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType().toString().contains("PICKAXE")) {
            FileManager fileManager = new FileManager(player.getUniqueId().toString());
            if (fileManager.getEnabledSkill(false) != null) {
                if (fileManager.getEnabledSkill(false).equals(SkillType.MINER)) {
                    if (SkillPlugin.getInstance().nonPvpSkills.containsKey(player)) {
                        player.setItemInHand(fileManager.getItem());
                        fileManager.setItem(null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSwitchItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItem(event.getPreviousSlot()) != null) {
            ItemStack itemStack = player.getInventory().getItem(event.getPreviousSlot());
            if (itemStack.getType().toString().contains("PICKAXE")) {
                FileManager fileManager = new FileManager(player.getUniqueId().toString());
                if (fileManager.getEnabledSkill(false) != null) {
                    if (fileManager.getEnabledSkill(false).equals(SkillType.MINER)) {
                        if (SkillPlugin.getInstance().nonPvpSkills.containsKey(player)) {
                            event.setCancelled(true);
                            SkillPlugin.getInstance().nonPvpSkills.remove(player);
                            player.setItemInHand(fileManager.getItem());
                            fileManager.setItem(null);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (event.getInventory() != null && event.getCurrentItem() != null) {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack.getType().toString().contains("PICKAXE")) {
                    FileManager fileManager = new FileManager(player.getUniqueId().toString());
                    if (fileManager.getEnabledSkill(false) != null) {
                        if (fileManager.getEnabledSkill(false).equals(SkillType.MINER)) {
                            if (SkillPlugin.getInstance().nonPvpSkills.containsKey(player)) {
                                event.setCancelled(true);
                                SkillPlugin.getInstance().nonPvpSkills.remove(player);
                                player.setItemInHand(fileManager.getItem());
                                fileManager.setItem(null);
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Block> getBlocks(Block start, int radius){
        ArrayList<Block> blocks = new ArrayList<Block>();
        for(double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++){
            for(double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++){
                for(double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++){
                    Location loc = new Location(start.getWorld(), x, y, z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
    }
}
