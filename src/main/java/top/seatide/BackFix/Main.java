package top.seatide.BackFix;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("BackFix is enabled.");
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("backfix").setExecutor(new CommandHandler());
        Files.init(this);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("BackFix is disabled.");
    }
}

class EventListener implements Listener {
    public Main plugin;

    public EventListener(Main plugin) {
        this.plugin = plugin;
    }

    public static FileConfiguration getSetLocation(FileConfiguration data, String uuid, Location loc) {
        data.set(uuid + ".x", loc.getX());
        data.set(uuid + ".y", loc.getY());
        data.set(uuid + ".z", loc.getZ());
        data.set(uuid + ".world", loc.getWorld().getName());
        return data;
    }

    public static Location getLocationFor(String uuid) {
        var data = Files.backs;
        if (!data.contains(uuid + ".x") || !data.contains(uuid + ".y") || !data.contains(uuid + ".z")) {
            return null;
        }
        Location result = new Location(Bukkit.getWorld(data.getString(uuid + ".world")), data.getDouble(uuid + ".x"),
                data.getDouble(uuid + ".y"), data.getDouble(uuid + ".z"));
        return result;
    }

    @EventHandler
    public void recordDeathLocation(PlayerDeathEvent e) {
        var p = e.getEntity();
        var data = getSetLocation(Files.backs, p.getUniqueId().toString(), p.getLocation());
        Files.save(data, "data.yml");
    }

    @EventHandler
    public void recordTeleportLocation(PlayerTeleportEvent e) {
        var p = e.getPlayer();
        var data = getSetLocation(Files.backs, p.getUniqueId().toString(), e.getFrom());
        Files.save(data, "data.yml");
    }
}

class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("此指令只能由玩家执行。");
        }
        Player p = (Player) sender;
        var location = EventListener.getLocationFor(p.getUniqueId().toString());
        if (!p.teleport(location)) {
            sender.sendMessage("传送失败。");
        }
        return true;
    }
}