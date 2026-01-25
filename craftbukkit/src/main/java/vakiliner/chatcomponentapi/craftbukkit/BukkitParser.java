package vakiliner.chatcomponentapi.craftbukkit;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Team;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitParser extends BaseParser {
	public boolean supportsSeparatorInSelector() {
		return false;
	}

	public boolean supportsFontInStyle() {
		return true;
	}

	public void sendMessage(CommandSender sender, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (type == ChatMessageType.CHAT) {
			sender.sendMessage(uuid, component.toLegacyText());
		} else {
			sender.sendMessage(component.toLegacyText());
		}
	}

	public void execute(BukkitScheduler scheduler, IChatPlugin plugin, Runnable runnable) {
		if (plugin instanceof Plugin) {
			if (!Bukkit.isPrimaryThread()) {
				scheduler.runTask((Plugin) plugin, runnable);
			} else {
				runnable.run();
			}
		} else {
			throw new IllegalArgumentException("Invalid plugin");
		}
	}

	public void kickPlayer(Player player, ChatComponent reason) {
		player.kickPlayer(reason != null ? reason.toLegacyText() : null);
	}

	public static ChatColor bukkit(ChatTextFormat color) {
		return color != null ? ChatColor.getByChar(color.getChar()) : null;
	}

	public static ChatTextFormat bukkit(ChatColor color) {
		return color != null ? ChatTextFormat.getByChar(color.getChar()) : null;
	}

	public ChatPlayer toChatPlayer(Player player) {
		return player != null ? new BukkitChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(OfflinePlayer player) {
		if (player instanceof Player) {
			return this.toChatPlayer((Player) player);
		}
		return player != null ? new BukkitChatOfflinePlayer(this, player) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSender sender) {
		if (sender instanceof Player) {
			return this.toChatPlayer((Player) sender);
		}
		return sender != null ? new BukkitChatCommandSender(this, sender) : null;
	}

	public ChatTeam toChatTeam(Team team) {
		return team != null ? new BukkitChatTeam(this, team) : null;
	}

	public ChatServer toChatServer(Server server) {
		return server != null ? new BukkitChatServer(this, server) : null;
	}
}