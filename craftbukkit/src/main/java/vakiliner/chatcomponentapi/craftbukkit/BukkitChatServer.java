package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Objects;
import org.bukkit.Server;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;

public class BukkitChatServer implements ChatServer {
	private final BukkitParser parser;
	private final Server server;

	public BukkitChatServer(BukkitParser parser, Server server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public Server getServer() {
		return this.server;
	}

	public ChatPlayerList getPlayerList() {
		return this.parser.toChatPlayerList(this.server);
	}
}