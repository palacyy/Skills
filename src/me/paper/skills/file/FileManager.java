package me.paper.skills.file;

import me.paper.skills.SkillPlugin;
import me.paper.skills.skill.Skill;
import me.paper.skills.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class FileManager {

    private String playerUUID;
    private File file;
    private YamlConfiguration yamlConfiguration;

    public FileManager(String playerUUID) {
        File file = new File(SkillPlugin.getInstance().getDataFolder(), File.separator + "skillProfiles");
        File skillProfile = new File(file, File.separator + playerUUID + ".yml");
        YamlConfiguration skillConfig = YamlConfiguration.loadConfiguration(skillProfile);
        this.playerUUID = playerUUID;
        this.file = skillProfile;
        this.yamlConfiguration = skillConfig;
    }

    public SkillType getEnabledSkill(boolean combat) {
        for (SkillType skillType : SkillType.values()) {
            if (combat) {
                if (skillType.isCombatSkill()) {
                    if (isEnabled( skillType.getIdentifier())) {
                        return SkillType.valueOf(skillType.getIdentifier().toUpperCase());
                    }
                }
            }else {
                if (!skillType.isCombatSkill()) {
                    if (isEnabled( skillType.getIdentifier())) {
                        return SkillType.valueOf(skillType.getIdentifier().toUpperCase());
                    }
                }
            }
        }
        return null;
    }

    public void setItem(ItemStack item) {
        this.yamlConfiguration.set("item", item);
        try {
            yamlConfiguration.save(file);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ItemStack getItem() {
        return (ItemStack) yamlConfiguration.get("item");
    }

    public void setArmor(ItemStack[] currentArmor) {
        this.yamlConfiguration.set("armor", currentArmor);
        try {
            yamlConfiguration.save(file);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ItemStack[] getArmor() {
        final ItemStack[] contents = (ItemStack[]) ((List)yamlConfiguration.get("armor")).toArray(new ItemStack[0]);
        return contents;
    }

    public boolean isUnlocked(String identifier) {
        return this.yamlConfiguration.getBoolean(identifier + ".unlocked");
    }

    public void setUnlocked(String identifier) {
        this.yamlConfiguration.set(identifier + ".unlocked", true);
        try {
            this.yamlConfiguration.save(this.file);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getLevel(String identifier) {
        return this.yamlConfiguration.getInt(identifier + ".level");
    }

    public void setLevel(String identifier, int level) {
        this.yamlConfiguration.set(identifier + ".level", level);
        try {
            this.yamlConfiguration.save(this.file);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isEnabled(String identifier) {
        return this.yamlConfiguration.getBoolean(identifier + ".enabled");
    }

    public void setStatus(String identifier, boolean status) {
        this.yamlConfiguration.set(identifier + ".enabled", status);
        try {
            this.yamlConfiguration.save(this.file);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setup() {
        this.yamlConfiguration.set("pvpCooldown", 0);
        this.yamlConfiguration.set("nonPvpCooldown", 0);
        for (SkillType skillType : SkillType.values()) {
            this.yamlConfiguration.set(skillType.getIdentifier() + ".unlocked", false);
            this.yamlConfiguration.set(skillType.getIdentifier() + ".level", 0);
            this.yamlConfiguration.set(skillType.getIdentifier() + ".enabled", false);
        }
        try {
            this.yamlConfiguration.save(this.file);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean hasProfile() {
        return this.file.exists();
    }

    public YamlConfiguration getYamlConfiguration() {
        return this.yamlConfiguration;
    }

    public File getFile() {
        return this.file;
    }

}
