package vakiliner.chatcomponentapi.craftbukkit;

import org.bukkit.BanList;
import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class BukkitChatUserBanList extends BukkitChatBanList<GameProfile> implements ChatUserBanList {
	public BukkitChatUserBanList(BukkitParser parser, BanList banList) {
		super(parser, banList);
	}

	@Override
	protected String cast(GameProfile gameProfile) {
		return gameProfile.getName();
	}
}