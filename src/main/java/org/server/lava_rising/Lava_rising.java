package org.server.lava_rising;


import org.bukkit.*;
import org.bukkit.block.Block;

import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;




public class Lava_rising extends JavaPlugin {
    private static Lava_rising instance;
    public static boolean pvpEnabled = false;


    private BukkitTask rise;
    private BukkitTask end;
    private int currentY = 60;
    boolean isRunning = false;
    private Location centerLocation;
    private int pvpStartY = this.getConfig().getInt("settings.pvp-start-lava-y");
    private int lavaStopY = this.getConfig().getInt("settings.stop-y");
    private int riseSpeed = this.getConfig().getInt("settings.rise-speed");

    @Override
    public void onEnable() {
        Lava_rising.pvpEnabled = false;
        instance = this;
        this.saveDefaultConfig();

        // 注册指令
        this.getCommand("lavarise").setExecutor(new StartCommand());
        getServer().getPluginManager().registerEvents(new WorldRestrictionListener(), this);
        getServer().getPluginManager().registerEvents(new PvpListener(), this);



        //游戏开始时关闭玩家pvp



    }

    public static Lava_rising getInstance(){
        return instance;
    }




    @Override
    public void onDisable() {
        stopLavaRise();
    }


    public void startLavaRise(){
        this.currentY = this.getConfig().getInt("settings.start-y",319);
        this.isRunning = true;
        this.centerLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        this.pvpStartY = this.getConfig().getInt("settings.pvp-start-lava-y");
        this.lavaStopY = this.getConfig().getInt("settings.stop-y");
        this.riseSpeed = this.getConfig().getInt("settings.rise-speed");

        World world = centerLocation.getWorld();
        int half = 200;
        rise = new BukkitRunnable() {
            int minX = centerLocation.getBlockX() - half;
            int maxX = centerLocation.getBlockX() + half;
            int minZ = centerLocation.getBlockZ() - half;
            int maxZ = centerLocation.getBlockZ() + half;

            int currentX = minX; // 当前铺到了哪一行

            @Override
            public void run() {
                // 游戏停止或超过最高高度，自动退出
                if (!isRunning) {
                    cancel();
                    return;
                }
                if (currentY > lavaStopY) {
                    checkWinner();
                    stopLavaRise();
                    cancel();
                    return;
                }

                // 每一Tick填充30行
                int linesPerTick = 30;
                for (int i = 0; i < linesPerTick && currentX <= maxX; i++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(currentX, currentY, z);
                        // 仅替换空气和不可燃的流体，提升趣味并减少卡顿
                        if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
                            block.setType(Material.LAVA, false);
                        }
                    }
                    currentX++;
                }

                // 当 currentX超过maxX，说明当前层已经全部铺完
                if (currentX > maxX) {
                    // 1. 播报上一层完成的消息
                    getServer().broadcastMessage("当前岩浆高度已达到：Y = " + currentY);

                    // 2. 触发阶段性事件
                    if (currentY % 5 == 0) {
                        checkWinner();
                    }
                    if (currentY == 64) {
                        registerDeathEvent();
                        getServer().broadcastMessage("岩浆已上升至Y=64，现在死亡无法重生！");
                    }
                    //岩浆达到Y=100开启pvp
                    if (currentY == pvpStartY){
                        Lava_rising.pvpEnabled = true;


                    }

                    // 3. 层数加 1，重置 X 轴指针，准备下一层
                    currentY++;
                    currentX = minX;
                }
            }
        }.runTaskTimer(this, 0L, riseSpeed);
    }
    public void stopLavaRise(){
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




