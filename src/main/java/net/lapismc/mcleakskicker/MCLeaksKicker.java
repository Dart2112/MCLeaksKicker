package net.lapismc.mcleakskicker;

import me.gong.mcleaks.MCLeaksAPI;
import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.concurrent.TimeUnit;

public class MCLeaksKicker extends LapisCorePlugin implements Listener {

    private MCLeaksAPI api = MCLeaksAPI.builder()
            .threadCount(2)
            .expireAfter(1, TimeUnit.HOURS).build();

    @Override
    public void onEnable() {
        registerConfiguration(new LapisCoreConfiguration(this, 2, 1));
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info(getName() + " v." + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(getName() + " has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
        reloadConfig();
        if (!getConfig().getBoolean("Enabled"))
            return;
        String playerName = e.getName();
        if (getConfig().getStringList("Whitelist").contains(playerName))
            return;
        api.checkAccount(playerName, isMCLeaks -> {
            if (isMCLeaks) {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                e.setKickMessage(config.getMessage("KickMessage"));
            }
        }, Throwable::printStackTrace);
    }
}
