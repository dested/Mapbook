package com.sk89q.mapbook;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class RailwayCommands {

	@Command(aliases = { "railway" }, usage = "", desc = "CommandBook version information", min = 0, max = 0)
	public static void railway(CommandContext args, MapBookPlugin plugin,
			CommandSender sender) throws CommandException {
		if (args.argsLength() > 0) {
			sender.sendMessage(ChatColor.YELLOW + args.getString(0));

			if (args.getString(0).toLowerCase().equals("setstation")) {
				plugin.Railway.ReadyToSetRailwayStation = true;
				sender.sendMessage(ChatColor.YELLOW
						+ " The next block you click will be defined as a new station.");
			}
		}
	}

}
