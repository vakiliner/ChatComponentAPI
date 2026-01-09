package vakiliner.chatcomponentapi.fabric;

import java.util.Objects;
import java.util.UUID;

import net.minecraft.server.players.PlayerList;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class FabricChatPlayerList implements ChatPlayerList {
	private final FabricParser parser;
	private final PlayerList playerList;

	public FabricChatPlayerList(FabricParser parser, PlayerList playerList) {
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