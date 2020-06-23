package me.paper.skills.skill;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Skill {

    private int duration;
    private List<String> itemIdentifier;

    public abstract void execute(PlayerInteractEvent event);

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(List<String> itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }
}
