package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import net.minecraft.server.MinecraftServer;
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

	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server, plugin, runnable);
	}

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