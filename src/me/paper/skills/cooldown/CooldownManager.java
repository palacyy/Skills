package me.paper.skills.cooldown;

import me.paper.skills.SkillPlugin;
import me.paper.skills.file.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class CooldownManager {

    private FileManager fileManager;
    private FileConfiguration fileConfiguration = SkillPlugin.getInstance().getConfig();

    public CooldownManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public boolean isOnPvpCooldown() {
        long until = fileManager.getYamlConfiguration().getLong("pvpCooldown");
        long now = System.currentTimeMillis();
        if (until <= now) {
            return false;
        }else {
            return true;
        }
    }

    public boolean isOnNonPvpCooldown() {
        long until = fileManager.getYamlConfiguration().getLong("nonPvpCooldown");
        long now = System.currentTimeMillis();
        if (until <= now) {
            return false;
        }else {
            return true;
        }
    }

    public void addPvpCooldown() {
        Long now = System.currentTimeMillis();
        int duration = fileConfiguration.getInt("skills.pvpCooldown");
        int amountInMili = (duration*60000);
        Long until = now + amountInMili;
        fileManager.getYamlConfiguration().set("pvpCooldown", until);
        try {
            fileManager.getYamlConfiguration().save(fileManager.getFile());
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addNonPvpCooldown() {
        Long now = System.currentTimeMillis();
        int duration = fileConfiguration.getInt("skills.nonPvpCooldown");
        int amountInMili = (duration*60000);
        Long until = now + amountInMili;
        fileManager.getYamlConfiguration().set("nonPvpCooldown", until);
        try {
            fileManager.getYamlConfiguration().save(fileManager.getFile());
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void resetCooldown(boolean pvp) {
        if (pvp) {
            fileManager.getYamlConfiguration().set("pvpCooldown", 0);
            try {
                fileManager.getYamlConfiguration().save(fileManager.getFile());
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }else {
            fileManager.getYamlConfiguration().set("nonPvpCooldown", 0);
            try {
                fileManager.getYamlConfiguration().save(fileManager.getFile());
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Long getCooldown(boolean pvp) {
        if (pvp) {
            return fileManager.getYamlConfiguration().getLong("pvpCooldown");
        } else {
            return fileManager.getYamlConfiguration().getLong("nonPvpCooldown");
        }
    }
}
