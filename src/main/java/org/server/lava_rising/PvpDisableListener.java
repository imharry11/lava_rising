package org.server.lava_rising;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvpDisableListener implements Listener {

    // 增加一个静态或实例变量作为开关
    private boolean isPvpDisabled = true;

    // 提供修改开关的方法
    public void setPvpDisabled(boolean disabled) {
        this.isPvpDisabled = disabled;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // 如果开关被关闭（即允许PVP），则直接跳过，不拦截事件
        if (!isPvpDisabled) {
            return;
        }

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            event.setCancelled(true);
            event.getDamager().sendMessage("§c当前时间段禁止PVP！");
        }
    }
}
