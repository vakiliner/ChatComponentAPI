package vakiliner.chatcomponentapi.base;

import java.util.UUID;
import com.mojang.authlib.GameProfile;

public interface ChatOfflinePlayer {
	GameProfile getGameProfile();

	default String getName() {
		return this.getGameProfile().name();
	}

	default UUID getUniqueId() {
		return this.getGameProfile().id();
	}

	boolean isOp();

	boolean isOnline();

	ChatTeam getTeam();
}