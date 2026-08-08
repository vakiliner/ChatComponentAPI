package vakiliner.chatcomponentapi.forge;

import java.util.Date;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class ForgeChatUserBanList extends ForgeChatBanList<GameProfile, GameProfile, ProfileBanEntry, BanList> implements ChatUserBanList {
	public ForgeChatUserBanList(ForgeParser parser, BanList list) {
		super(parser, list);
	}

	@Override
	protected GameProfile cast(GameProfile gameProfile) {
		return gameProfile;
	}

	@Override
	protected ChatBanEntry cast(ProfileBanEntry entry) {
		return this.parser.toChatBanEntry(entry);
	}

	@Override
	protected ProfileBanEntry create(GameProfile gameProfile) {
		return new ProfileBanEntry(this.cast(gameProfile));
	}

	@Override
	protected ProfileBanEntry create(GameProfile gameProfile, String reason, String source, Date expires) {
		return new ProfileBanEntry(this.cast(gameProfile), null, source, expires, reason);
	}

	@Override
	public boolean isBanned(GameProfile gameProfile) {
		return this.list.isBanned(this.cast(gameProfile));
	}
}