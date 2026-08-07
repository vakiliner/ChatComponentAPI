package vakiliner.chatcomponentapi.craftbukkit;

import org.bukkit.Bukkit;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class BukkitChatUserBanList extends BukkitChatBanList<GameProfile, PlayerProfile> implements ChatUserBanList {
	public BukkitChatUserBanList(BukkitParser parser, ProfileBanList banList) {
		super(parser, banList);
	}

	@Override
	protected PlayerProfile cast(GameProfile gameProfile) {
		return Bukkit.createPlayerProfile(gameProfile.id(), gameProfile.name());
	}
}