package vakiliner.chatcomponentapi.forge;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.forge.mixin.ServerPlayerAccessor;

public class ForgeChatPlayer extends ForgeChatOfflinePlayer implements ChatPlayer {
	protected final ServerPlayer player;

	public ForgeChatPlayer(ForgeParser parser, ServerPlayer player) {
		super(parser, ((ServerPlayerAccessor) player).getServer(), player.getGameProfile());
		this.player = Objects.requireNonNull(player);
	}

	public ServerPlayer getPlayer() {
		return this.player;
	}

	@Override
	public ChatServer getServer() {
		return this.parser.toChatServer(this.server);
	}

	@Override
	public ChatComponent getDisplayName() {
		return ForgeParser.forge(this.player.getDisplayName());
	}

	@Override
	public ChatGameMode getGameMode() {
		return ChatGameMode.getByValue(this.player.gameMode.getGameModeForPlayer().getId());
	}

	@Override
	public SocketAddress getAddress() {
		return this.player.connection.getRemoteAddress();
	}

	@Override
	public void kick(ChatComponent reason) {
		this.parser.kickPlayer(this.player, reason);
	}

	@Override
	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.player.commandSource(), component, type, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			ForgeChatPlayer other = (ForgeChatPlayer) obj;
			return this.parser.equals(other.parser) && this.player.equals(other.player);
		} else {
			return false;
		}
	}
}