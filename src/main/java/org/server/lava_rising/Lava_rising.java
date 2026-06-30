package org.server.lava_rising;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;


public class Lava_rising extends JavaPlugin implements CommandExecutor {
    private static Lava_rising instance;

    private BukkitTask rise;
    private BukkitTask end;
    private int currentY = 60;
    boolean isRunning = false;
    private Location centerLocation;

    @Override
    public void onEnable() {

        instance = this;
        // 注册指令
        Objects.requireNonNull(this.getCommand("lavarise")).setExecutor(this);
    }

    public static Lava_rising getInstance(){
        return instance;
    }

    @Override
    public void onDisable() {
        stopLavaRise();
    }

    public boolean onCommand(CommandSender s,Command c,String label,String[] args){
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("start")){
                if (isRunning){
                    s.sendMessage("岩浆已开始上升");
                    return true;
                }
                World world = Bukkit.getWorld("world");
                WorldBorder border = world.getWorldBorder();
                border.changeSize(500,0);
                border.changeSize(100, 12000);
                border.setDamageAmount(5);
                border.setDamageBuffer(0);
                startLavaRise(-1);

                return true;
                }

        }
        if(args[0].equalsIgnoreCase("stop")){
            if(!isRunning){
                s.sendMessage("未启动");
                return true;

            }
            stopLavaRise();
            s.sendMessage("游戏已强制停止");
        }

        return false;
    }
    private void startLavaRise(int startY){
        this.currentY = startY;
        this.isRunning = true;
        this.centerLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        World world = centerLocation.getWorld();
        int half = 120/2;
        rise = new BukkitRunnable(){
            @Override
            public void run() {
                if (!isRunning){
                    cancel();
                    return;
                }
                if(currentY > 319){
                    checkWinner();
                    stopLavaRise();
                    return;

                }
                int minX = centerLocation.getBlockX() - half;
                int maxX = centerLocation.getBlockX() + half;
                int minZ = centerLocation.getBlockZ() - half;
                int maxZ = centerLocation.getBlockZ() + half;

                // 极简且避免单Tick填充250,000个方块导致主线程卡死的双重循环。
                // 仅替换空气和不可燃的流体，保留实体建筑物，提供极限生存的趣味
                new BukkitRunnable() {
                    int currentX = minX;
                    @Override
                    public void run() {
                        if (!isRunning || currentY > 319) {
                            cancel();
                            return;
                        }

                        int linesPerTick = 25;
                        for (int i = 0; i < linesPerTick && currentX <= maxX; i++) {
                            for (int z = minZ; z <= maxZ; z++) {
                                Block block = world.getBlockAt(currentX, currentY, z);

                                block.setType(Material.LAVA, false);

                            }
                            currentX++;
                        }
                        if (currentX > maxX) {
                            cancel();
                        }
                    }
                }.runTaskTimer(Lava_rising.this, 0L, 4L);

                currentY++;
                getServer().broadcastMessage("现在岩浆高度：" + currentY);


                if (currentY % 5 == 0) {
                    checkWinner();
                }
                if (currentY == 64){
                    registerDeathEvent();
                    getServer().broadcastMessage("岩浆已上升至Y=64，现在死亡无法重生！");
                }
            }
        }.runTaskTimer(this,100L,100L);
    }
    void stopLavaRise(){
        if(rise != null){
            rise.cancel();
            rise = null;
        }
        isRunning = false;
    }
    public void checkWinner() {
        long survivalCount = Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                .count();

        if (survivalCount == 1 && isRunning) {
            Player winner = Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                    .findFirst().orElse(null);

            String winnerName = (winner != null) ? winner.getName() : "无人";

            getServer().broadcastMessage(ChatColor.YELLOW + " 游戏结束！最后的幸存者是: " + ChatColor.GREEN + winnerName);
            stopLavaRise();
        }
    }
    //用来让岩浆高度>=64时玩家死亡无法重生
    public void registerDeathEvent(){
        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);

    }
}




