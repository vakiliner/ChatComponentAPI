package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Objects;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;

public class BukkitChatServer implements ChatServer {
	private final BukkitParser parser;
	private final Server server;

	public BukkitChatServer(BukkitParser parser, Server server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public Server getImpl() {
		return this.server;
	}

	public void execute(IChatPlugin plugin, Runnable runnable) {
		Objects.requireNonNull(plugin);
		if (plugin instanceof Plugin) {
			this.server.getScheduler().runTask((Plugin) plugin, runnable);
		} else {
			throw new IllegalArgumentException("Invalid plugin");
		}
	}
}