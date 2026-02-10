package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.BanList.Type;
import vakiliner.chatcomponentapi.base.ChatIpBanList;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.ChatUserBanList;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.util.ParseCollection;

public class BukkitChatServer implements ChatServer, ChatPlayerList {
	protected final BukkitParser parser;
	protected final Server server;

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

	public ChatIpBanList getIpBanList() {
		return this.parser.toChatIpBanList(this.server.getBanList(Type.IP));
	}

	public ChatUserBanList getUserBanList() {
		return this.parser.toChatUserBanList(this.server.getBanList(Type.NAME));
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

	public Collection<ChatPlayer> getPlayers() {
		return new ParseCollection<>(this.server.getOnlinePlayers(), this.parser::toChatPlayer);
	}

	public ChatPlayer getPlayer(UUID uuid) {
		return this.parser.toChatPlayer(this.server.getPlayer(uuid));
	}

	public ChatPlayer getPlayer(String name) {
		return this.parser.toChatPlayer(this.server.getPlayerExact(name));
	}

	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.broadcastMessage(this.server, component, type, uuid);
	}

	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server.getScheduler(), plugin, runnable);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			BukkitChatServer other = (BukkitChatServer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server);
		} else {
			return false;
		}
	}
}