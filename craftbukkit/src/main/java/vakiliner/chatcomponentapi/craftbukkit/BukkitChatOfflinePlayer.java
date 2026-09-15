package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatNameAndId;

public class BukkitChatOfflinePlayer implements ChatOfflinePlayer {
	protected final BukkitParser parser;
	protected final OfflinePlayer player;

	public BukkitChatOfflinePlayer(BukkitParser parser, OfflinePlayer player) {
		this.parser = Objects.requireNonNull(parser);
		this.player = Objects.requireNonNull(player);
	}

	public OfflinePlayer getPlayer() {
		return this.player;
	}

	@Override
	public ChatNameAndId getNameAndId() {
		return new ChatNameAndId(this.player.getUniqueId(), this.player.getName());
	}

	@Override
	public GameProfile getGameProfile() {
		return this.getNameAndId().toGameProfile();
	}

	@Override
	public String getName() {
		return this.player.getName();
	}

	@Override
	public UUID getUniqueId() {
		return this.player.getUniqueId();
	}

	@Override
	public boolean isOp() {
		return this.player.isOp();
	}

	@Override
	public boolean isOnline() {
		return this.player.isOnline();
	}

	@Override
	public ChatTeam getTeam() {
		return this.parser.toChatTeam(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(this.getName()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			BukkitChatOfflinePlayer other = (BukkitChatOfflinePlayer) obj;
			return this.parser.equals(other.parser) && this.player.equals(other.player);
		} else {
			return false;
		}
	}
}
