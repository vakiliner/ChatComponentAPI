package vakiliner.chatcomponentapi.base;

import java.net.SocketAddress;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatPlayer extends ChatOfflinePlayer, ChatCommandSender {
	ChatServer getServer();

	@Override
	default String getName() {
		return ChatOfflinePlayer.super.getName();
	}

	ChatComponent getDisplayName();

	ChatGameMode getGameMode();
	
	SocketAddress getAddress();

	default void kick() {
		this.kick(null);
	}

	void kick(ChatComponent reason);

	@Override
	default boolean isConsole() {
		return false;
	}
}