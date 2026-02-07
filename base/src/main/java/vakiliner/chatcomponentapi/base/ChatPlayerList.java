package vakiliner.chatcomponentapi.base;

import java.util.Collection;
import java.util.UUID;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatPlayerList {
	ChatServer getServer();

	int getPlayerCount();

	int getMaxPlayers();

	int getViewDistance();

	Collection<ChatPlayer> getPlayers();

	public ChatPlayer getPlayer(UUID uuid);

	default void broadcastMessage(ChatComponent component) {
		this.broadcastMessage(component, ChatMessageType.SYSTEM, null);
	}

	void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid);
}