package org.server.lava_rising;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;

public final class PlayerDeath implements Listener {
    Lava_rising plugin = JavaPlugin.getPlugin(Lava_rising.class);
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if (plugin.isRunning != true) return;
        var Player = e.getPlayer();
        Player.setGameMode(GameMode.SPECTATOR);
        long survivalCount = Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                .count();

        int survivalPlayers = (int) survivalCount;
        if (survivalCount >= 1 && plugin.isRunning) {
            Player winner = Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                    .findFirst().orElse(null);

            String winnerName = (winner != null) ? winner.getName() : "无人";

            getServer().broadcastMessage(ChatColor.YELLOW + " 游戏结束！最后的幸存者是: " + ChatColor.GREEN + winnerName);
            plugin.stopLavaRise();



        }
    }
}
