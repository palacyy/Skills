package me.paper.skills.commands;

import me.paper.skills.file.FileManager;
import me.paper.skills.inventory.CustomInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;
            String playerID = player.getUniqueId().toString();
            FileManager fileManager = new FileManager(playerID);

            if (!fileManager.hasProfile()) {
                fileManager.setup();
            }

            CustomInventory customInventory = new CustomInventory(player);
            customInventory.init();

        }

        return false;
    }
}
