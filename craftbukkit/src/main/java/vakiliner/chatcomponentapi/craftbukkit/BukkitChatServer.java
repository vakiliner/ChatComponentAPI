package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.Server;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitChatServer implements ChatServer, ChatPlayerList {
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
		return this;
	}

	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.broadcastMessage(this.server, component, type, uuid);
	}
}