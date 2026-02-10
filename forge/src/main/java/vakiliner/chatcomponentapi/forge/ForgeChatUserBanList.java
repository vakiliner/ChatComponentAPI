package vakiliner.chatcomponentapi.forge;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class ForgeChatUserBanList extends ForgeChatStoredUserList<GameProfile, BanList, ChatBanEntry, ProfileBanEntry> implements ChatUserBanList {
	public ForgeChatUserBanList(ForgeParser parser, BanList list) {
		super(parser, list, parser::toChatBanEntry);
	}

	public boolean isBanned(GameProfile gameProfile) {
		return this.list.isBanned(gameProfile);
	}
}