package vakiliner.chatcomponentapi.fabric;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.server.players.PlayerList;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
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

	public ChatServer getServer() {
		return this.parser.toChatServer(this.playerList.getServer());
	}

	public int getPlayerCount() {
		return this.playerList.getPlayerCount();
	}

	public int getMaxPlayers() {
		return this.playerList.getMaxPlayers();
	}

	public int getViewDistance() {
		return this.playerList.getViewDistance();
	}

	public List<ChatPlayer> getPlayers() {
		return this.playerList.getPlayers().stream().map(this.parser::toChatPlayer).collect(Collectors.toList());
	}

	public ChatPlayer getPlayer(UUID uuid) {
		return this.parser.toChatPlayer(this.playerList.getPlayer(uuid));
	}

	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.broadcastMessage(this.playerList, component, type, uuid);
	}
}