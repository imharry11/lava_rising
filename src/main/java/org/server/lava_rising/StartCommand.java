package org.server.lava_rising;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class StartCommand implements CommandExecutor {
    Lava_rising plugin = JavaPlugin.getPlugin(Lava_rising.class);
    public StartCommand(JavaPlugin plugin){
        plugin.getCommand("lavarise").setExecutor(plugin);
    }
    public boolean onCommand(CommandSender s, org.bukkit.command.Command c, String label, String[] args){
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("start")){
                if (plugin.isRunning){
                    s.sendMessage("岩浆已开始上升");
                    return true;
                }
                World world = Bukkit.getWorld("world");
                WorldBorder border = world.getWorldBorder();
                border.changeSize(1000,0);
                border.changeSize(50, 12000);
                border.setDamageAmount(3);
                border.setDamageBuffer(0);
                plugin.startLavaRise(-64);

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
