package vakiliner.chatcomponentapi.base;

import com.mojang.authlib.GameProfile;

public interface ChatUserBanList extends ChatBanList<GameProfile> {
	boolean isBanned(GameProfile gameProfile);
}