package org.server.lava_rising;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvpListener implements Listener {

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        // 【关键点】如果全局允许PVP，当前监听器直接罢工，不做任何拦截
        if (Lava_rising.pvpEnabled) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) return;
        Player attacker = null;

        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player) projectile.getShooter();
            }
        }

        if (attacker != null) {
            event.setCancelled(true);
            attacker.sendMessage("§c[提示] 当前阶段禁止玩家间互相攻击！");
        }
    }
}
