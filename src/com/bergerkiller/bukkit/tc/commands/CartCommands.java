package com.bergerkiller.bukkit.tc.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.utils.EnumUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.CartProperties;
import com.bergerkiller.bukkit.tc.MinecartMember;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;

public class CartCommands {
	
	public static boolean execute(Player p, CartProperties prop, String cmd, String[] args) throws NoPermissionException {
		if (cmd.equals("info") || cmd.equals("i")) {
			info(p, prop);
		} else if (cmd.equals("mobenter") || cmd.equals("mobsenter")) {
			Permission.COMMAND_MOBENTER.handle(p);
			if (args.length == 1) {
				prop.allowMobsEnter = StringUtil.getBool(args[0]);
			}
			p.sendMessage(ChatColor.YELLOW + "Can be entered by mobs: " + ChatColor.WHITE + " " + prop.allowMobsEnter);
		} else if (cmd.equals("playerenter")) {
			if (args.length == 1) {
				Permission.COMMAND_PLAYERENTER.handle(p);
				prop.allowPlayerEnter = StringUtil.getBool(args[0]);
			}
			p.sendMessage(ChatColor.YELLOW + "Players can enter this minecart: " + ChatColor.WHITE + " " + prop.allowPlayerEnter);
		} else if (cmd.equals("playerleave") || cmd.equals("playerexit")) {
			if (args.length == 1) {
				Permission.COMMAND_PLAYEREXIT.handle(p);
				prop.allowPlayerExit = StringUtil.getBool(args[0]);
			}
			p.sendMessage(ChatColor.YELLOW + "Players can exit this minecart: " + ChatColor.WHITE + " " + prop.allowPlayerExit);
		} else if (cmd.equals("claim")) {
			Permission.COMMAND_SETOWNERS.handle(p);
			prop.clearOwners();
			prop.setOwner(p, true);
			p.sendMessage(ChatColor.YELLOW + "You claimed this minecart your own!");
		} else if (cmd.equals("addowner") || cmd.equals("addowners")) {
			Permission.COMMAND_SETOWNERS.handle(p);
			if (args.length == 0) {
				p.sendMessage(ChatColor.YELLOW + "Please specify the player names to set as owner!");
			} else {
				for (String owner : args) {
					prop.setOwner(owner.toLowerCase());
				}
				p.sendMessage(ChatColor.YELLOW + "You added " + ChatColor.WHITE + StringUtil.combineNames(args) + ChatColor.YELLOW + " as owners of this minecart!");
			}
		} else if (cmd.equals("setowner") || cmd.equals("setowners")) {
			Permission.COMMAND_SETOWNERS.handle(p);
			prop.clearOwners();
			if (args.length == 0) {
				p.sendMessage(ChatColor.YELLOW + "All owners for this minecart have been cleared!");
			} else {
				for (String owner : args) {
					prop.setOwner(owner.toLowerCase());
				}
				p.sendMessage(ChatColor.YELLOW + "You set " + ChatColor.WHITE + StringUtil.combineNames(args) + ChatColor.YELLOW + " as owners of this minecart!");
			}
		} else if (cmd.equals("addtags") || cmd.equals("addtag")) {
			Permission.COMMAND_SETTAGS.handle(p);
			if (args.length == 0) {
				p.sendMessage(ChatColor.RED + "You need to give at least one tag to add!");
			} else {
				prop.addTags(args);
				p.sendMessage(ChatColor.YELLOW + "You added " + ChatColor.WHITE + StringUtil.combineNames(args) + ChatColor.YELLOW + " as tags for this minecart!");
			}
		} else if (cmd.equals("settags") || cmd.equals("settag") || cmd.equals("tags") || cmd.equals("tag")) {
			Permission.COMMAND_SETTAGS.handle(p);
			prop.clearTags();
			if (args.length == 0) {
				p.sendMessage(ChatColor.YELLOW + "All tags for this minecart have been cleared!");
			} else {
				prop.addTags(args);
				p.sendMessage(ChatColor.YELLOW + "You set " + ChatColor.WHITE + StringUtil.combineNames(args) + ChatColor.YELLOW + " as tags for this minecart!");
			}
		} else if (cmd.equals("dest") || cmd.equals("destination")) {
			Permission.COMMAND_SETDESTINATION.handle(p);
			if (args.length == 0) {
				prop.destination = "";
				p.sendMessage(ChatColor.YELLOW + "The destination for this minecart has been cleared!");
			} else {
				prop.destination = StringUtil.combine(" ", args[0]);
				p.sendMessage(ChatColor.YELLOW + "You set " + ChatColor.WHITE + args[0] + ChatColor.YELLOW + " as destination for this minecart!");
			}
		} else if (cmd.equals("remove") || cmd.equals("destroy")) {
			Permission.COMMAND_DESTROY.handle(p);
			MinecartMember mm = prop.getMember();
			if (mm == null) {
				prop.remove();
			} else {
				mm.die();
			}
			p.sendMessage(ChatColor.YELLOW + "The selected minecart has been destroyed!");
		} else if (cmd.equals("public")) {
			Permission.COMMAND_SETPUBLIC.handle(p);
			if (args.length == 0) {
				prop.isPublic = true;
			} else {
				prop.isPublic = StringUtil.getBool(args[0]);
			}
			p.sendMessage(ChatColor.YELLOW + "The selected minecart can be used by everyone: " + ChatColor.WHITE + prop.isPublic);
		} else if (cmd.equals("private") || cmd.equals("locked") || cmd.equals("lock")) {
			Permission.COMMAND_SETPUBLIC.handle(p);
			if (args.length == 0) {
				prop.isPublic = false;
			} else {
				prop.isPublic = !StringUtil.getBool(args[0]);
			}
			p.sendMessage(ChatColor.YELLOW + "The selected minecart can only be used by you: " + ChatColor.WHITE + !prop.isPublic);
		} else if (cmd.equals("pickup")) {
			Permission.COMMAND_PICKUP.handle(p);
			if (args.length == 0) {
				prop.pickUp = true;
			} else {
				prop.pickUp = StringUtil.getBool(args[0]);
			}			
			p.sendMessage(ChatColor.YELLOW + "The selected minecart picks up nearby items: " + ChatColor.WHITE + prop.pickUp);
		} else if (cmd.equals("break")) {
			Permission.COMMAND_BREAKBLOCK.handle(p);
			if (args.length == 0) {
				p.sendMessage(ChatColor.YELLOW + "This cart breaks: " + ChatColor.WHITE + StringUtil.combineNames(prop.blockBreakTypes));
			} else {
				if (StringUtil.isBool(args[0]) && !StringUtil.getBool(args[0])) {
					prop.blockBreakTypes.clear();
					p.sendMessage(ChatColor.YELLOW + "Block break types have been cleared!");
				} else {
					boolean asBreak = true;
					boolean lastIsBool = StringUtil.isBool(args[args.length - 1]);
				    if (lastIsBool) asBreak = StringUtil.getBool(args[args.length - 1]);
				    int count = lastIsBool ? args.length - 1 : args.length;
				    Set<Material> mats = new HashSet<Material>();
					for (int i = 0; i < count; i++) {
						Material mat = EnumUtil.parseMaterial(args[i], null);
						if (mat != null) {
							if (p.hasPermission("train.command.break.admin") || TrainCarts.canBreak(mat)) {
								mats.add(mat);
							} else {
								p.sendMessage(ChatColor.RED + "You are not allowed to make this cart break '" + mat.toString() + "'!");
							}
						}
					}
					if (mats.isEmpty()) {
						p.sendMessage(ChatColor.RED + "Failed to find possible and allowed block types in the list given.");
						return true;
					}
					if (asBreak) {
						prop.blockBreakTypes.addAll(mats);
						p.sendMessage(ChatColor.YELLOW + "This cart can now (also) break: " + ChatColor.WHITE + StringUtil.combineNames(mats));
					} else {
						prop.blockBreakTypes.removeAll(mats);
						p.sendMessage(ChatColor.YELLOW + "This cart can no longer break: " + ChatColor.WHITE + StringUtil.combineNames(mats));
					}
				}
			}
		} else {
			if (!cmd.equals("help") && !cmd.equals("?")) {
				p.sendMessage(ChatColor.RED + "Unknown cart command: '" + cmd + "'!");
			}
			help(new MessageBuilder()).send(p);
			return true;
		}
		prop.tryUpdate();
		return true;
	}
	
	public static MessageBuilder help(MessageBuilder builder) {
		builder.green("Available commands: ").yellow("/cart ").red("[info");
		builder.setSeparator(ChatColor.WHITE, "/").setIndent(10);
		builder.red("mobenter").red("playerenter").red("playerexit").red("claim").red("addowners").red("setowners");
		builder.red("addtags").red("settags").red("destination").red("destroy").red("public").red("private");
		builder.red("pickup").red("break");
		return builder.setSeparator(null).red("]");
	}
		
	public static void info(Player player, CartProperties prop) {
		//warning message not taken
		if (!prop.hasOwners()) {
			player.sendMessage(ChatColor.YELLOW + "Note: This minecart is not owned, claim it using /cart claim!");
		}
		if (prop.allowMobsEnter) {
			if (prop.allowPlayerEnter) {
				player.sendMessage(ChatColor.YELLOW + "Can be entered by: " + ChatColor.WHITE + " Mobs and Players");
			} else {
				player.sendMessage(ChatColor.YELLOW + "Can be entered by: " + ChatColor.WHITE + " Mobs");
			}
		} else if (prop.allowPlayerEnter) {
			player.sendMessage(ChatColor.YELLOW + "Can be entered by: " + ChatColor.WHITE + " Players");
		} else {
			player.sendMessage(ChatColor.YELLOW + "Can be entered by: " + ChatColor.RED + " No one");
		}
		player.sendMessage(ChatColor.YELLOW + "Can be exited by players: " + ChatColor.WHITE + prop.allowPlayerExit);
		player.sendMessage(ChatColor.YELLOW + "Picks up nearby items: " + ChatColor.WHITE + prop.pickUp);
		if (!prop.blockBreakTypes.isEmpty()) {
			player.sendMessage(ChatColor.YELLOW + "Breaks blocks: " + ChatColor.WHITE + StringUtil.combineNames(prop.blockBreakTypes));
		}
		player.sendMessage(ChatColor.YELLOW + "Enter message: " + ChatColor.WHITE + (prop.enterMessage == null ? "None" : prop.enterMessage));
		player.sendMessage(ChatColor.YELLOW + "Tags: " + ChatColor.WHITE + (prop.hasTags() ? StringUtil.combineNames(prop.getTags()) : "None"));
		if (prop.hasDestination()){					
			player.sendMessage(ChatColor.YELLOW + "This minecart will ignore tag switchers and will attempt to reach " + ChatColor.WHITE + prop.destination);
		}
		player.sendMessage(ChatColor.YELLOW + "Owned by: " + ChatColor.WHITE + (prop.hasOwners() ? StringUtil.combineNames(prop.getOwners()) : "None"));
		// Location
		BlockLocation loc = prop.getLocation();
		if (loc != null) {
			player.sendMessage(ChatColor.YELLOW + "Current location: " + ChatColor.WHITE + "[" + loc.x + "/" + loc.y + "/" + loc.z + "] in world " + loc.world);
		}
	}
	
}
