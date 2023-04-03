package com.github.gameoholic.twitchrewardsplugin.Rewards.RewardsActivators;

import com.github.gameoholic.twitchrewardsplugin.TwitchRewardsPlugin;
import com.github.gameoholic.twitchrewardsplugin.tasks.NoPlacingTask;
import com.github.gameoholic.twitchrewardsplugin.tasks.WhitelistTask;
import jdk.jfr.internal.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Whitelist {
    public static void addToWhitelist(TwitchRewardsPlugin plugin, String playerUsername, Player affectedPlayer, int time) {

        //No duplicate tasks. If new, just change existing one's time
        Optional<WhitelistTask> task = WhitelistTask.whitelistTasks.stream()
                .filter(t -> t.getPlayerUsername() != null && t.getPlayerUsername().equals(playerUsername))
                .findFirst();

        if (task.isPresent())
            task.get().setTimeLeft((time + 1));
        else {
            try {
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                String command = "whitelist add " + playerUsername;
                Bukkit.dispatchCommand(console, command);
                if (!Bukkit.getWhitelistedPlayers().stream().map(s -> s.getName().toLowerCase()).
                        collect(Collectors.toList()).contains(playerUsername.toLowerCase())) {
                    throw new RuntimeException("Invalid username");
                }
                new WhitelistTask(plugin, playerUsername, affectedPlayer, time);
            }
            catch (Exception e) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "[TwitchRewards] Couldn't add player \"" + playerUsername +
                        "\" to the whitelist.");
                Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            }
        }
    }
}