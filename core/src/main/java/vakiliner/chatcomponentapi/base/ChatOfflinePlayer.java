package vakiliner.chatcomponentapi.base;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.common.ChatNameAndId;

public interface ChatOfflinePlayer {
	GameProfile getGameProfile();

	default ChatNameAndId getNameAndId() {
		return BaseParser.base(this.getGameProfile());
	}

	default String getName() {
		return this.getGameProfile().getName();
	}

	default UUID getUniqueId() {
		return this.getGameProfile().getId();
	}

	boolean isOp();

	boolean isOnline();

	ChatTeam getTeam();
}
