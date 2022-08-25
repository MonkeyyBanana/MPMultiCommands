package com.stinkymonkey.monkey;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class main extends JavaPlugin implements CommandExecutor{
	@Override
	public void onEnable() {
		loadConfig();
		getCommandNames();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void loadConfig() {
		File pluginFolder = new File("plugins" + System.getProperty("file.separator") + this.getDescription().getName());
		if (pluginFolder.exists() == false) {
    		pluginFolder.mkdir();
    		System.out.println("MADE FOLDER FOR MultiCommands");
    	}
		
		File configFile = new File("plugins" + System.getProperty("file.separator") + this.getDescription().getName() + System.getProperty("file.separator") + "config.yml");
		if (configFile.exists() == false) {
    		this.saveDefaultConfig();
    		System.out.println("CREATED A CONFIG FOR MultiCommands");
		}
    	
    	try {
    		this.getConfig().load(configFile);
    		System.out.println("LOADED CONFIG FOR MultiCommands");
    	} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FAILED TO LOAD CONFIG FOR MultiCommands");
    	}
	}
	
	int n = 0;
	List<String> cmdNames = new ArrayList<String>();
	public void getCommandNames() {
		while (this.getConfig().contains("MultiCommand" + Integer.toString(n))) {
			cmdNames.add(this.getConfig().getString("MultiCommand" + Integer.toString(n) + ".cmdName"));
			n++;
		}
	}
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		Player p = (Player) sender;
		if (label.equalsIgnoreCase("multicommand")) {
			if (p.hasPermission("multiCmd.use")) {
				if (args.length == 0) { 
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Bro Use /multicommand exec or do /multicommand reload");
				} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "RELOADING Config");
					this.reloadConfig();
				} else if (args.length == 2 && args[0].equalsIgnoreCase("exec") || args[0].equalsIgnoreCase("execute")) {
					if (cmdNames.contains((args[1]).toLowerCase())) {
						int index = cmdNames.indexOf((args[1].toLowerCase()));
						List<String> cmdToExec = new ArrayList<String>(this.getConfig().getStringList("MultiCommand" + Integer.toString(index) + ".cmd"));
						int delayTime = 0;
						for (String s : cmdToExec) {
							if (s.contains("DELAY")) {
								Double miliDelay = (20.00 / 1000.00 * Double.parseDouble(s.replace("DELAY ", "")));
								int Delay = miliDelay.intValue();
								System.out.println("[MultiCmd] Delaying Task By " + Delay + " Ticks" + " // " + s.replace("DELAY ", "") + " Miliseconds");
								delayTime += Delay;
							} else {
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									public void run() {
										p.performCommand(s);
									}
								}, delayTime);
							}
						}
					}
				}
			}
		}
		return true;
	}
}
