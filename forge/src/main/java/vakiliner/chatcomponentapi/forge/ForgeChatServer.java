package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;

public class ForgeChatServer implements ChatServer {
	private final ForgeParser parser;
	private final MinecraftServer server;

	public ForgeChatServer(ForgeParser parser, MinecraftServer server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public ChatPlayerList getPlayerList() {
		return this.parser.toChatPlayerList(this.server.getPlayerList());
	}
}