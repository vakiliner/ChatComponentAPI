package vakiliner.chatcomponentapi.fabric;

import java.util.Objects;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;

public class FabricChatServer implements ChatServer {
	protected final FabricParser parser;
	protected final MinecraftServer server;

	public FabricChatServer(FabricParser parser, MinecraftServer server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public MinecraftServer getImpl() {
		return this.server;
	}

	public boolean isDedicatedServer() {
		return this.server.isDedicatedServer();
	}

	public String getSingleplayerName() {
		return this.server.getSingleplayerName();
	}

	public boolean isSingleplayer() {
		return this.server.isSingleplayer();
	}

	public boolean isSingleplayerOwner(GameProfile gameProfile) {
		return this.server.isSingleplayerOwner(gameProfile);
	}

	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server, plugin, runnable);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			FabricChatServer other = (FabricChatServer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server);
		} else {
			return false;
		}
	}
}