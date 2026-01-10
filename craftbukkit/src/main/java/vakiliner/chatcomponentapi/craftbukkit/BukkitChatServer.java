package vakiliner.chatcomponentapi.craftbukkit;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Server;
import vakiliner.chatcomponentapi.base.ChatPlayer;
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

	public Server getImpl() {
		return this.server;
	}

	public ChatServer getServer() {
		return this;
	}

	public ChatPlayerList getPlayerList() {
		return this;
	}

	public int getPlayerCount() {
		return this.server.getOnlinePlayers().size();
	}

	public int getMaxPlayers() {
		return this.server.getMaxPlayers();
	}

	public int getViewDistance() {
		return this.server.getViewDistance();
	}

	public List<ChatPlayer> getPlayers() {
		return this.server.getOnlinePlayers().stream().map(this.parser::toChatPlayer).collect(Collectors.toList());
	}

	public ChatPlayer getPlayer(UUID uuid) {
		return this.parser.toChatPlayer(this.server.getPlayer(uuid));
	}

	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.broadcastMessage(this.server, component, type, uuid);
	}
}