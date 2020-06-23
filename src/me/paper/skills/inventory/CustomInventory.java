package me.paper.skills.inventory;

import me.paper.skills.SkillPlugin;
import me.paper.skills.file.FileManager;
import me.paper.skills.skill.SkillType;
import me.paper.skills.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomInventory implements Color {
    
    private FileConfiguration fileConfiguration = SkillPlugin.getInstance().getConfig();

    private Inventory inventory;
    private Player player;

    public CustomInventory(Player player) {
        this.player = player;
    }

    public void init() {
        this.inventory = Bukkit.createInventory(null, 9, format(fileConfiguration.getString("inventories.default.name")));
        ItemStack pvpItem = new ItemStack(Material.getMaterial(fileConfiguration.getString("inventories.default.items.pvp_item.material")));
        ItemMeta pvpMeta = pvpItem.getItemMeta();
        pvpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pvpMeta.setDisplayName(format(fileConfiguration.getString("inventories.default.items.pvp_item.name")));
        ArrayList<String> pvpLore = new ArrayList<>();
        for (String pvpString : fileConfiguration.getStringList("inventories.default.items.pvp_item.lore")) {
            pvpLore.add(format(pvpString));
        }
        pvpMeta.setLore(pvpLore);
        pvpItem.setItemMeta(pvpMeta);

        ItemStack nonPvpItem = new ItemStack(Material.getMaterial(fileConfiguration.getString("inventories.default.items.nonPvp_item.material")));
        ItemMeta nonPvpMeta = nonPvpItem.getItemMeta();
        nonPvpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nonPvpMeta.setDisplayName(format(fileConfiguration.getString("inventories.default.items.nonPvp_item.name")));
        ArrayList<String> nonPvpLore = new ArrayList<>();
        for (String nonPvpString : fileConfiguration.getStringList("inventories.default.items.nonPvp_item.lore")) {
            nonPvpLore.add(format(nonPvpString));
        }
        nonPvpMeta.setLore(nonPvpLore);
        nonPvpItem.setItemMeta(nonPvpMeta);

        this.inventory.setItem(3, pvpItem);
        this.inventory.setItem(5, nonPvpItem);

        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        filler.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 0);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        fillerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        filler.setItemMeta(fillerMeta);

        for (int x = 0; x <= 8; x++) {
            if (this.inventory.getItem(x) == null) {
                this.inventory.setItem(x, filler);
            }
        }

        player.openInventory(this.inventory);
    }

    public void initOverviewCombat() {
        FileManager fileManager = new FileManager(this.player.getUniqueId().toString());
        this.inventory = Bukkit.createInventory(null, 9, format(fileConfiguration.getString("inventories.unlocked_pvp.name")));

        ArrayList<ItemStack> generatedSkills = new ArrayList<>();

        for (SkillType skillType : SkillType.values()) {
            if (skillType.isCombatSkill()) {
                if (fileManager.isUnlocked(skillType.getIdentifier())) {
                    ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (fileConfiguration.getString("skills." + skillType.getIdentifier() + ".color") != null) {
                        itemMeta.setDisplayName(ChatColor.valueOf(fileConfiguration.getString("skills." + skillType.getIdentifier() + ".color")) + format(fileConfiguration.getString("inventories.unlocked_pvp.items.unlocked_item.name").replaceAll("%skill%", skillType.getIdentifier().toUpperCase())));
                    }else {
                        itemMeta.setDisplayName(ChatColor.WHITE + format(fileConfiguration.getString("inventories.unlocked_pvp.items.unlocked_item.name").replaceAll("%skill%", skillType.getIdentifier().toUpperCase())));
                    }
                    ArrayList<String> unlockedLore = new ArrayList<>();
                    for (String unlockedString : fileConfiguration.getStringList("inventories.unlocked_pvp.items.unlocked_item.lore")) {
                        unlockedLore.add(format(unlockedString).replaceAll("%level%", fileManager.getLevel(skillType.getIdentifier()) + ""));
                    }
                    itemMeta.setLore(unlockedLore);
                    itemStack.setItemMeta(itemMeta);
                    generatedSkills.add(itemStack);
                }else {
                    ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (fileConfiguration.getString("skills." + skillType.getIdentifier() + ".color") != null) {
                        itemMeta.setDisplayName(ChatColor.valueOf(fileConfiguration.getString("skills." + skillType.getIdentifier() + ".color")) + format(fileConfiguration.getString("inventories.unlocked_pvp.items.locked_item.name").replaceAll("%skill%", skillType.getIdentifier().toUpperCase())));
                    }else {
                        itemMeta.setDisplayName(ChatColor.WHITE + format(fileConfiguration.getString("inventories.unlocked_pvp.items.locked_item.name").replaceAll("%skill%", skillType.getIdentifier().toUpperCase())));
                    }
                    ArrayList<String> lockedLore = new ArrayList<>();
                    for (String unlockedString : fileConfiguration.getStringList("inventories.unlocked_pvp.items.locked_item.lore")) {
                        lockedLore.add(format(unlockedString).replaceAll("%level%", fileManager.getLevel(skillType.getIdentifier()) + ""));
                    }
                    itemMeta.setLore(lockedLore);
                    itemStack.setItemMeta(itemMeta);
                    generatedSkills.add(itemStack);
                }
            }
        }

        for (int x = 0; x <= 7; x++) {
            this.inventory.setItem(x, generatedSkills.get(x));
        }

        ItemStack backItem = new ItemStack(Material.getMaterial(fileConfiguration.getString("inventories.back_item.material")));
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(format(fileConfiguration.getString("inventories.back_item.name")));
        backItem.setItemMeta(backMeta);
        this.inventory.setItem(8, backItem);

        this.player.openInventory(this.inventory);
    }

    public void initOverviewNonCombat() {
        FileManager fileManager = new FileManager(this.player.getUniqueId().toString());
        this.inventory = Bukkit.createInventory(null, 9, format(fileConfiguration.getString("inventories.unlocked_noPvp.name")));

        ArrayList<ItemStack> generatedSkills = new ArrayList<>();

        for (SkillType skillType : SkillType.values()) {
            if (!skillType.isCombatSkill()) {
                if (fileManager.isUnlocked(skillType.getIdentifier())) {
                    ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (fileConfiguration.getString("skills." + skillType.getIdentifier() + ".color") != null) {
                        itemMeta.setDisplayName(ChatColor.valueOf(fileConfiguration.getString("skills." + skillType.getIdentifier() + ".color")) + format(fileConfiguration.getString("inventories.unlocked_noPvp.items.unlocked_item.name").replaceAll("%skill%", skillType.getIdentifier().toUpperCase())));
                    }else {
                        itemMeta.setDisplayName(ChatColor.WHITE + format(fileConfiguration.getString("inventories.unlocked_noPvp.items.unlocked_item.name").replaceAll("%skill%", skillType.getIdentifier().toUpperCase())));
                    }
                    ArrayList<String> unlockedLore = new ArrayList<>();
                    for (String unlockedString : fileConfiguration.getStringList("inventories.unlocked_noPvp.items.unlocked_item.lore")) {
                        unlockedLore.add(format(unlockedString).replaceAll("%level%", fileManager.getLevel(skillType.getIdentifier()) + ""));
                    }
                    itemMeta.setLore(unlockedLore);
                    itemStack.setItemMeta(itemMeta);
                    generatedSkills.add(itemStack);
                }else {
                    ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (fileConfiguration.getString("skills." + skillType.getIdentifier() + ".color") != null) {
                        itemMeta.setDisplayName(ChatColor.valueOf(fileConfiguration.getString("skills." + skillType.getIdentifier() + ".color")) + format(fileConfiguration.getString("inventories.unlocked_noPvp.items.locked_item.name").replaceAll("%skill%", skillType.getIdentifier().toUpperCase())));
                    }else {
                        itemMeta.setDisplayName(ChatColor.WHITE + format(fileConfiguration.getString("inventories.unlocked_noPvp.items.locked_item.name").replaceAll("%skill%", skillType.getIdentifier().toUpperCase())));
                    }
                    ArrayList<String> lockedLore = new ArrayList<>();
                    for (String unlockedString : fileConfiguration.getStringList("inventories.unlocked_noPvp.items.locked_item.lore")) {
                        lockedLore.add(format(unlockedString).replaceAll("%level%", fileManager.getLevel(skillType.getIdentifier()) + ""));
                    }
                    itemMeta.setLore(lockedLore);
                    itemStack.setItemMeta(itemMeta);
                    generatedSkills.add(itemStack);
                }
            }
        }

        for (int x = 0; x <= 3; x++) {
            this.inventory.setItem(x, generatedSkills.get(x));
        }

        ItemStack backItem = new ItemStack(Material.getMaterial(fileConfiguration.getString("inventories.back_item.material")));
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(format(fileConfiguration.getString("inventories.back_item.name")));
        backItem.setItemMeta(backMeta);
        this.inventory.setItem(8, backItem);

        this.player.openInventory(this.inventory);
    }
}
