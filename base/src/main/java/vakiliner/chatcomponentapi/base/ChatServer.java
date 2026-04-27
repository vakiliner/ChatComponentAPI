package vakiliner.chatcomponentapi.base;

import com.mojang.authlib.GameProfile;

public interface ChatServer {
	ChatPlayerList getPlayerList();

	boolean isDedicatedServer();

	String getSingleplayerName();

	boolean isSingleplayer();

	boolean isSingleplayerOwner(GameProfile gameProfile);

	void execute(IChatPlugin plugin, Runnable runnable);
}