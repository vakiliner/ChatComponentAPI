package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;

public class ForgeChatServer implements ChatServer {
	private final ForgeParser parser;
	private final MinecraftServer server;

	public ForgeChatServer(ForgeParser parser, MinecraftServer server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public MinecraftServer getImpl() {
		return this.server;
	}

	public ChatPlayerList getPlayerList() {
		return this.parser.toChatPlayerList(this.server.getPlayerList());
	}

	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server, plugin, runnable);
	}
}