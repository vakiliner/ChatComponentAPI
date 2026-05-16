package vakiliner.chatcomponentapi.fabric;

import java.util.Date;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class FabricChatUserBanList extends FabricChatStoredUserList<GameProfile, UserBanList, ChatBanEntry, UserBanListEntry> implements ChatUserBanList {
	public FabricChatUserBanList(FabricParser parser, UserBanList list) {
		super(parser, list, parser::toChatBanEntry);
	}

	public ChatBanEntry add(GameProfile key) {
		UserBanListEntry entry = new UserBanListEntry(key);
		this.list.add(entry);
		return this.i2o.apply(entry);
	}

	public ChatBanEntry add(GameProfile key, String reason, String source, Date expires) {
		UserBanListEntry entry = new UserBanListEntry(key, null, source, expires, reason);
		this.list.add(entry);
		return this.i2o.apply(entry);
	}

	public boolean isBanned(GameProfile gameProfile) {
		return this.list.isBanned(gameProfile);
	}
}