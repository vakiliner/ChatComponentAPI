package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.management.PlayerList;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class ForgeChatPlayerList implements ChatPlayerList {
	private final ForgeParser parser;
	private final PlayerList playerList;

	public ForgeChatPlayerList(ForgeParser parser, PlayerList playerList) {
		this.parser = Objects.requireNonNull(parser);
		this.playerList = Objects.requireNonNull(playerList);
	}

	public PlayerList getPlayerList() {
		return this.playerList;
	}

	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.broadcastMessage(this.playerList, component, type, uuid);
	}
}