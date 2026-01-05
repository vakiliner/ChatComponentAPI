package vakiliner.chatcomponentapi.base;

import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatPlayer extends ChatOfflinePlayer, ChatCommandSender {
	ChatComponent getDisplayName();

	ChatGameMode getGameMode();

	default void kick() {
		this.kick(null);
	}

	void kick(ChatComponent reason);

	default boolean isConsole() {
		return false;
	}
}