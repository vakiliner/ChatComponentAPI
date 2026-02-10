package vakiliner.chatcomponentapi.fabric;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class FabricChatUserBanList extends FabricChatStoredUserList<GameProfile, UserBanList, ChatBanEntry, UserBanListEntry> implements ChatUserBanList {
	public FabricChatUserBanList(FabricParser parser, UserBanList list) {
		super(parser, list, parser::toChatBanEntry);
	}

	public boolean isBanned(GameProfile gameProfile) {
		return this.list.isBanned(gameProfile);
	}
}