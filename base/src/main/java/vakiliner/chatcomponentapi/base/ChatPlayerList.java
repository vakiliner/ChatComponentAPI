package vakiliner.chatcomponentapi.base;

import java.util.UUID;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatPlayerList {
	void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid);
}