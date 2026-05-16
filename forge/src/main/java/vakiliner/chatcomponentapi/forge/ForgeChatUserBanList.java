package vakiliner.chatcomponentapi.forge;

import java.util.Date;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class ForgeChatUserBanList extends ForgeChatStoredUserList<GameProfile, BanList, ChatBanEntry, ProfileBanEntry> implements ChatUserBanList {
	public ForgeChatUserBanList(ForgeParser parser, BanList list) {
		super(parser, list, parser::toChatBanEntry);
	}

	public ChatBanEntry add(GameProfile key) {
		ProfileBanEntry entry = new ProfileBanEntry(key);
		this.list.add(entry);
		return this.i2o.apply(entry);
	}

	public ChatBanEntry add(GameProfile key, String reason, String source, Date expires) {
		ProfileBanEntry entry = new ProfileBanEntry(key, null, source, expires, reason);
		this.list.add(entry);
		return this.i2o.apply(entry);
	}

	public boolean isBanned(GameProfile gameProfile) {
		return this.list.isBanned(gameProfile);
	}
}