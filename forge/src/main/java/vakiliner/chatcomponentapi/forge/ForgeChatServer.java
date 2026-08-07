package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class ForgeChatServer implements ChatServer {
	protected final ForgeParser parser;
	protected final MinecraftServer server;

	public ForgeChatServer(ForgeParser parser, MinecraftServer server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public MinecraftServer getImpl() {
		return this.server;
	}

	@Override
	public ChatPlayerList getPlayerList() {
		return this.parser.toChatPlayerList(this.server.getPlayerList());
	}

	@Override
	public String getName() {
		return "CONSOLE";
	}

	@Override
	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.server, component, type, uuid);
	}

	@Override
	public boolean isDedicatedServer() {
		return this.server.isDedicatedServer();
	}

	@Override
	public String getSingleplayerName() {
		GameProfile profile = this.server.getSingleplayerProfile();
		return profile != null ? profile.name() : null;
	}

	@Override
	public boolean isSingleplayer() {
		return this.server.isSingleplayer();
	}

	@Override
	public boolean isSingleplayerOwner(GameProfile gameProfile) {
		return this.server.isSingleplayerOwner(ForgeParser.forge(gameProfile));
	}

	@Override
	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server, plugin, runnable);
	}

	@Override
	public void executeBlocking(IChatPlugin plugin, Runnable runnable) {
		this.parser.executeBlocking(this.server, plugin, runnable);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			ForgeChatServer other = (ForgeChatServer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server);
		} else {
			return false;
		}
	}
}