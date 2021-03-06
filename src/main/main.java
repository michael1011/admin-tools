package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import GUI.GUIOpener;
import GUI.InteractGui;
import commands.Day;
import commands.GlobalMute;
import commands.Gm1;
import commands.Gma;
import commands.Home;
import commands.Invsee;
import commands.MaintenanceC;
import commands.MuteC;
import commands.Ping;
import commands.Spawn;
import commands.Warps;
import listeners.MaintenanceL;
import listeners.MuteL;
import listeners.NameTags;
import listeners.PlayerJoin;
import listeners.SortedTabList;
import commands.ClearChat;


public class main extends JavaPlugin implements CommandExecutor {
	
    public static main instance;
    public static File configf, messagesf, homesf, warpsf, mutedPf;
    
    public FileConfiguration config, messages, homes, warps, mutedP;
    public Boolean updateAvailable;
    public String version;
    
    public Scoreboard sb;
    
    public static Inventory GUI, home, cheats, clearchat, allclearchat, ping, allPing, maint;
    
	@Override
	public void onEnable() {
        instance = this;
        createFiles();
        ScoreBoard();
        
        if (config.getBoolean("Settings.Updater")) {
	        getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
	            public void run() {
	                checkUpdate();
	            }
	        }, 30L, 216000L);
        }

        new GlobalMute(this);
        new MuteL(this);
        new MuteC(this);
        new Ping(this);
        new GUIOpener(this);
        new InteractGui(this);
        new SortedTabList(this);
        new ClearChat(this);
        new Warps(this);
        new MaintenanceL(this);
        new MaintenanceC(this);
		new Home(this);
		new Spawn(this);
		new Gma(this);
		new NameTags(this);
		new Invsee(this);
		new Day(this);
		new Gm1(this);
	    new PlayerJoin(this);

		Bukkit.getConsoleSender().sendMessage(PluginPrefix.Prefix+ChatColor.RED+"Plugin enabled!");
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(PluginPrefix.Prefix+ChatColor.RED+"Plugin disabled!");
	}
	
	public void ScoreBoard() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		
		sb.registerNewTeam("00Admin");
		sb.registerNewTeam("01Player");
		
		
		sb.getTeam("00Admin").setPrefix(ChatColor.translateAlternateColorCodes('&', config.getString("Settings.OPPrefix")));
	}
	
	private void checkUpdate() {
		final Updater updater = new Updater(this, 18422, false);
        final Updater.UpdateResult result = updater.getResult();
        
        switch (result) {
        case FAIL_SPIGOT: {
        	updateAvailable = false;
            break;
        }
        case NO_UPDATE: {
            updateAvailable = false;
        	break;
        }
        case UPDATE_AVAILABLE: {
            version = updater.getVersion();
            Bukkit.getConsoleSender().sendMessage(PluginPrefix.Prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Players.UpdateMessage"))+version);
            Bukkit.getConsoleSender().sendMessage(PluginPrefix.Prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Players.UpdateMessage2"))+"http://bit.ly/1QexebX");
            updateAvailable = true;
            break;
        }
        default: {
        	updateAvailable = false;
            break;
        }
    }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args) {

		String NoPermission = ChatColor.translateAlternateColorCodes('&', instance.messages.getString("Players.NoPermission"));
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if (args.length == 0) {
			
				if (p.hasPermission("admintool.reload")) {
					
					try {
			        	config.load(configf);
			            messages.load(messagesf);
			            homes.load(homesf);
			            warps.load(warpsf);
			            mutedP.load(mutedPf);
			            
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
					
					ScoreBoard();
					
					p.sendMessage(PluginPrefix.Prefix+ChatColor.translateAlternateColorCodes('&', instance.messages.getString("Players.Reload")));
				} else {
					p.sendMessage(PluginPrefix.Prefix+NoPermission);
				}
				
			} else {
				p.sendMessage(PluginPrefix.Prefix+ChatColor.translateAlternateColorCodes('&', instance.messages.getString("Players.Usage")));
				p.sendMessage(PluginPrefix.Prefix+ChatColor.translateAlternateColorCodes('&', instance.messages.getString("Players.ReloadHelp")));
			}
			
		} else {
			
			try {
	        	config.load(configf);
	            messages.load(messagesf);
	            homes.load(homesf);
	            warps.load(warpsf);
	            mutedP.load(mutedPf);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
			ScoreBoard();
			
			Bukkit.getConsoleSender().sendMessage(PluginPrefix.Prefix+ChatColor.translateAlternateColorCodes('&', instance.messages.getString("Console.Reload")));
		}
		return true;
	}
	
	
	
	public void createFiles() {

		configf = new File(getDataFolder(), "config.yml");
        messagesf = new File(getDataFolder(), "messages.yml");
        homesf = new File(getDataFolder(), "homes.yml");
        warpsf = new File(getDataFolder(), "warps.yml");
        mutedPf = new File(getDataFolder(), "mutedP.yml");
        
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            copy(getResource("config.yml"), configf);
        }
        
        if (!messagesf.exists()) {
            messagesf.getParentFile().mkdirs();
        	copy(getResource("messages.yml"), messagesf);
         }
        
        if (!homesf.exists()) {
            homesf.getParentFile().mkdirs();
        	copy(getResource("homes.yml"), homesf);
        }

        if (!warpsf.exists()) {
            warpsf.getParentFile().mkdirs();
        	copy(getResource("warps.yml"), warpsf);
        }
        
        if (!mutedPf.exists()) {
            mutedPf.getParentFile().mkdirs();
        	copy(getResource("mutedP.yml"), mutedPf);
        }
        
        config = new YamlConfiguration();
        messages = new YamlConfiguration();
        homes = new YamlConfiguration();
        warps = new YamlConfiguration();
        mutedP = new YamlConfiguration();
        
        try {
        	config.load(configf);
            messages.load(messagesf);
            homes.load(homesf);
            warps.load(warpsf);
            mutedP.load(mutedPf);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void copy(InputStream in, File file) {

        try {

            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);

            }
            out.close();
            in.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
