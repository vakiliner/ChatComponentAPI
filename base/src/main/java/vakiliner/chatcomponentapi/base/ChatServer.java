package vakiliner.chatcomponentapi.base;

import java.util.concurrent.Executor;
import com.mojang.authlib.GameProfile;

public interface ChatServer extends ChatCommandSender {
	ChatPlayerList getPlayerList();

	@Override
	default boolean isConsole() {
		return true;
	}

	boolean isDedicatedServer();

	String getSingleplayerName();

	boolean isSingleplayer();

	boolean isSingleplayerOwner(GameProfile gameProfile);

	default Executor getExecutor(IChatPlugin plugin) {
		return (runnable) -> this.execute(plugin, runnable);
	}

	void execute(IChatPlugin plugin, Runnable runnable);

	void executeBlocking(IChatPlugin plugin, Runnable runnable);
}