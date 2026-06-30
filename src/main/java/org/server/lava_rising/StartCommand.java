package org.server.lava_rising;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class StartCommand implements CommandExecutor {
    Lava_rising plugin = JavaPlugin.getPlugin(Lava_rising.class);

    public boolean onCommand(CommandSender s, org.bukkit.command.Command c, String label, String[] args){
        int startSize = plugin.getConfig().getInt("settings.border-start-size");
        int changeSize = plugin.getConfig().getInt("settings.border-change-size");
        int changeTick = plugin.getConfig().getInt("settings.border-time");
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("start")){
                if (plugin.isRunning){
                    s.sendMessage("岩浆已开始上升");
                    return true;
                }
                World world = Bukkit.getWorld("world");
                WorldBorder border = world.getWorldBorder();
                border.changeSize(startSize,0);
                border.changeSize(changeSize, changeTick);
                border.setDamageAmount(3);
                border.setDamageBuffer(0);
                plugin.startLavaRise();

                return true;
            }

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
