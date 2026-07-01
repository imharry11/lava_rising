package org.server.lava_rising;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WorldRestrictionListener implements Listener {

    // 1.拦截通过传送门（地狱、末地门）的进入
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getTo() == null || event.getTo().getWorld() == null) {
            return;
        }

        World.Environment environment = event.getTo().getWorld().getEnvironment();
        Player player = event.getPlayer();

        //检查目标世界是否为下界或末地
        if (environment == World.Environment.NETHER || environment == World.Environment.THE_END) {
            event.setCancelled(true); //取消传送事件
            player.sendMessage("§c该服务器已禁止进入下界和末地！"); //向玩家发送提示
        }
    }

    // 2.拦截通过指令、其他插件或特定道具（如末影珍珠、床爆炸等导致的意外跨维度）的传送
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null || event.getTo().getWorld() == null) {
            return;
        }

        //只有当玩家跨越不同世界传送时才进行判断，优化性能
        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            World.Environment environment = event.getTo().getWorld().getEnvironment();
            Player player = event.getPlayer();

            if (environment == World.Environment.NETHER || environment == World.Environment.THE_END) {
                event.setCancelled(true);
                player.sendMessage("§c你没有权限前往该维度！");
            }
        }
    }
}
