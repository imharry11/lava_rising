package org.server.lava_rising;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public class StartCommand implements CommandExecutor {
    Lava_rising plugin = JavaPlugin.getPlugin(Lava_rising.class);

    public boolean onCommand(@NonNull CommandSender s, org.bukkit.command.@NonNull Command c, @NonNull String label, String[] args){
        int startSize = plugin.getConfig().getInt("settings.border-start-size");
        int changeSize = plugin.getConfig().getInt("settings.border-change-size");
        int changeTick = plugin.getConfig().getInt("settings.border-time");

        if(args[0].equalsIgnoreCase("start")){
            if (plugin.isRunning){
                s.sendMessage("岩浆已开始上升");
                return true;
            }

            plugin.startLavaRise();
            World world = Bukkit.getWorld("play_world");
            WorldBorder border = null;
            if (world != null) {
                border = world.getWorldBorder();
            }
            border.changeSize(startSize,0);
            border.changeSize(changeSize, changeTick);
            border.setDamageAmount(1);
            border.setDamageBuffer(0);


            return true;
        }




        if(args[0].equalsIgnoreCase("stop")){
            if(!plugin.isRunning){
                s.sendMessage("未启动");
                return true;

            }
            plugin.stopLavaRise();
            s.sendMessage("游戏已强制停止");
        }

        return false;
    }
}
