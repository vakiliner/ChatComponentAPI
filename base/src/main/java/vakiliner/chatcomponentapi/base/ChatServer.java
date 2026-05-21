package vakiliner.chatcomponentapi.base;

import com.mojang.authlib.GameProfile;

public interface ChatServer extends ChatCommandSender {
	ChatPlayerList getPlayerList();

	default boolean isConsole() {
		return true;
	}

	boolean isDedicatedServer();

	String getSingleplayerName();

	boolean isSingleplayer();

	boolean isSingleplayerOwner(GameProfile gameProfile);

	void execute(IChatPlugin plugin, Runnable runnable);

	void executeBlocking(IChatPlugin plugin, Runnable runnable);
}