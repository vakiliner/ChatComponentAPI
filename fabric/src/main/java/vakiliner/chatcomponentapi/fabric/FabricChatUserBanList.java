package vakiliner.chatcomponentapi.fabric;

import java.util.Date;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class FabricChatUserBanList extends FabricChatBanList<GameProfile, NameAndId, UserBanListEntry, UserBanList> implements ChatUserBanList {
	public FabricChatUserBanList(FabricParser parser, UserBanList list) {
		super(parser, list);
	}

	@Override
	protected NameAndId cast(GameProfile gameProfile) {
		return new NameAndId(gameProfile);
	}

	@Override
	protected ChatBanEntry cast(UserBanListEntry input) {
		return this.parser.toChatBanEntry(input);
	}

	@Override
	protected UserBanListEntry create(NameAndId key) {
		return new UserBanListEntry(key);
	}

	@Override
	protected UserBanListEntry create(NameAndId key, String reason, String source, Date expires) {
		return new UserBanListEntry(key, null, source, expires, reason);
	}

	@Override
	public boolean isBanned(GameProfile gameProfile) {
		return this.list.isBanned(this.cast(gameProfile));
	}
}