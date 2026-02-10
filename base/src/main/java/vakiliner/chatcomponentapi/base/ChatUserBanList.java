package vakiliner.chatcomponentapi.base;

import com.mojang.authlib.GameProfile;

public interface ChatUserBanList extends ChatStoredUserList<GameProfile, ChatBanEntry> {
	boolean isBanned(GameProfile gameProfile);
}