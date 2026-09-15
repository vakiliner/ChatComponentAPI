package vakiliner.chatcomponentapi.base;

import java.util.concurrent.Executor;
import com.mojang.authlib.GameProfile;

public interface ChatServer extends Executor, ChatCommandSender {
	ChatPlayerList getPlayerList();

	@Override
	default boolean isConsole() {
		return true;
	}

	boolean isDedicatedServer();

	String getSingleplayerName();

	boolean isSingleplayer();

	boolean isSingleplayerOwner(GameProfile gameProfile);

	@Override
	void execute(Runnable command);

	void executeBlocking(Runnable command);

	@Deprecated
	default Executor getExecutor(IChatPlugin plugin) {
		return (runnable) -> this.execute(plugin, runnable);
	}

	@Deprecated
	void execute(IChatPlugin plugin, Runnable runnable);

	@Deprecated
	void executeBlocking(IChatPlugin plugin, Runnable runnable);
}
