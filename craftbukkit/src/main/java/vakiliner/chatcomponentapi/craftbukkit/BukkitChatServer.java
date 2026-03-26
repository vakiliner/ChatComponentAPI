package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Objects;
import org.bukkit.Server;
import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;

public class BukkitChatServer implements ChatServer {
	protected final BukkitParser parser;
	protected final Server server;

	public BukkitChatServer(BukkitParser parser, Server server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public Server getImpl() {
		return this.server;
	}

	public boolean isDedicatedServer() {
		return true;
	}

	// Not supported
	public String getSingleplayerName() {
		return null;
	}

	public boolean isSingleplayer() {
		return this.getSingleplayerName() != null;
	}

	public boolean isSingleplayerOwner(GameProfile gameProfile) {
		return false;
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