package vakiliner.chatcomponentapi.fabric;

import java.util.Date;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class FabricChatUserBanList extends FabricChatBanList<GameProfile, GameProfile, UserBanListEntry, UserBanList> implements ChatUserBanList {
	public FabricChatUserBanList(FabricParser parser, UserBanList list) {
		super(parser, list);
	}

	@Override
	protected GameProfile cast(GameProfile gameProfile) {
		return gameProfile;
	}

	@Override
	protected ChatBanEntry cast(UserBanListEntry entry) {
		return this.parser.toChatBanEntry(entry);
	}

	@Override
	protected UserBanListEntry create(GameProfile gameProfile) {
		return new UserBanListEntry(this.cast(gameProfile));
	}

	@Override
	protected UserBanListEntry create(GameProfile gameProfile, String reason, String source, Date expires) {
		return new UserBanListEntry(this.cast(gameProfile), null, source, expires, reason);
	}

	@Override
	public boolean isBanned(GameProfile gameProfile) {
		return this.list.isBanned(this.cast(gameProfile));
	}
}