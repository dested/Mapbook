// $Id$
/*
 * MapBook
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 * Copyright (C) 2011 dested <>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.mapbook;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.map.MapInitializeEvent;
import org.bukkit.event.map.MapListener;
import org.bukkit.maps.RenderPriority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import com.sk89q.bukkit.migration.PermissionsResolverManager;
import com.sk89q.bukkit.migration.PermissionsResolverServerListener;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import com.sk89q.worldedit.commands.WorldEditCommands;

/**
 * Base plugin class for MapBook.
 * 
 * @author sk89q
 */
public class MapBookPlugin extends JavaPlugin {

	protected static final Logger logger = Logger
			.getLogger("Minecraft.MapBook");

	private PermissionsResolverManager perms;
	protected CommandsManager<CommandSender> commands;

	public SubwayBase Railway;

	/**
	 * Called when the plugin is enabled. This is where configuration is loaded,
	 * and the plugin is setup.
	 */
	public void onEnable() {

		logger.info(getDescription().getName() + " "
				+ getDescription().getVersion() + " enabled.");

		// Make the data folder for the plugin where configuration files
		// and other data files will be stored
		getDataFolder().mkdirs();

		createDefaultConfiguration("config.yml");

		// Load configuration
		populateConfiguration();

		// Prepare permissions
		perms = new PermissionsResolverManager(getConfiguration(), getServer(),
				getDescription().getName(), logger);
		perms.load();

		// Register the commands that we want to use
		final MapBookPlugin plugin = this;
		commands = new CommandsManager<CommandSender>() {
			@Override
			public boolean hasPermission(CommandSender player, String perm) {
				return plugin.hasPermission(player, perm);
			}
		};

		commands.register(RailwayCommands.class);

		// commands.register(GeneralCommands.class);

		// Register events
		registerEvents();

		// The permissions resolver has some hooks of its own
		(new PermissionsResolverServerListener(perms)).register(this);
	}

	/**
	 * Register the events that are used.
	 */
	protected void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		final MapBookPlugin plugin = this;
		
		pm.registerEvent(Event.Type.MAP_INITIALIZE, new MapListener() {
			@Override
			public void onMapInitialize(MapInitializeEvent event) {
				if (event.getMapView().getId() == 11) {
					event.getMapView().registerVirtualBase(
							new BeautifulHouseBase(), plugin);
					return;
				}
				if (event.getMapView().getId() == 4) {
					event.getMapView().registerVirtualBase(Railway=new SubwayBase(plugin), plugin);
					return;
				}
				if (event.getMapView().getId() == 17) {
					event.getMapView().registerVirtualBase(new LiquidBase(),plugin);
					return;
				}
				if (event.getMapView().getId() == 8) {
					event.getMapView().registerOverlay(new RedrawOverlay(),RenderPriority.High, plugin);
					return;
				}
				if (event.getMapView().getId() == 12) {
					event.getMapView().registerVirtualBase(new NyanCatBase(),plugin);
					return;
				}
				if (event.getMapView().getId() == 15) {
					event.getMapView().registerVirtualBase(new ChestWatcherBase(plugin), plugin);
					return;
				}

				if (!event.getMapView().isVirtual()) {
					event.getMapView().registerOverlay(new GlitteringOceanOverlay(), RenderPriority.Low,plugin);
				}
			}
		}, Priority.Normal, this);
	}

	/**
	 * Called when the plugin is disabled. Shutdown and clearing of any
	 * temporary data occurs here.
	 */
	public void onDisable() {
	}

	/**
	 * Called on a command.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		try {
			commands.execute(cmd.getName(), args, sender, this, sender);
		} catch (CommandPermissionsException e) {
			sender.sendMessage(ChatColor.RED + "You don't have permission.");
		} catch (MissingNestedCommandException e) {
			sender.sendMessage(ChatColor.RED + e.getUsage());
		} catch (CommandUsageException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			sender.sendMessage(ChatColor.RED + e.getUsage());
		} catch (WrappedCommandException e) {
			if (e.getCause() instanceof NumberFormatException) {
				sender.sendMessage(ChatColor.RED
						+ "Number expected, string received instead.");
			} else {
				sender.sendMessage(ChatColor.RED
						+ "An error has occurred. See console.");
				e.printStackTrace();
			}
		} catch (CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}

		return true;
	}

	/**
	 * Register an event.
	 * 
	 * @param type
	 * @param listener
	 * @param priority
	 */
	protected void registerEvent(Event.Type type, Listener listener,
			Priority priority) {
		getServer().getPluginManager().registerEvent(type, listener, priority,
				this);
	}

	/**
	 * Register an event at normal priority.
	 * 
	 * @param type
	 * @param listener
	 */
	protected void registerEvent(Event.Type type, Listener listener) {
		getServer().getPluginManager().registerEvent(type, listener,
				Priority.Normal, this);
	}

	/**
	 * Loads the configuration.
	 */
	public void populateConfiguration() {
		Configuration config = getConfiguration();
		config.load();
	}

	/**
	 * Create a default configuration file from the .jar.
	 * 
	 * @param name
	 */
	protected void createDefaultConfiguration(String name) {
		File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {

			InputStream input = this.getClass().getResourceAsStream(
					"/defaults/" + name);
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}

					logger.info(getDescription().getName()
							+ ": Default configuration file written: " + name);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {
					}

					try {
						if (output != null)
							output.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * Checks permissions.
	 * 
	 * @param sender
	 * @param perm
	 * @return
	 */
	public boolean hasPermission(CommandSender sender, String perm) {
		if (!(sender instanceof Player)) {
			return true;
		}

		if (sender.isOp()) {
			return true;
		}

		// Invoke the permissions resolver
		if (sender instanceof Player) {
			return perms.hasPermission(((Player) sender).getName(), perm);
		}

		return false;
	}

	/**
	 * Checks permissions and throws an exception if permission is not met.
	 * 
	 * @param sender
	 * @param perm
	 * @throws CommandPermissionsException
	 */
	public void checkPermission(CommandSender sender, String perm)
			throws CommandPermissionsException {
		if (!hasPermission(sender, perm)) {
			throw new CommandPermissionsException();
		}
	}
}