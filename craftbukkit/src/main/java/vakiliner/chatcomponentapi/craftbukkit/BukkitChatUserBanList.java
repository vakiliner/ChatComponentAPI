package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Date;
import org.bukkit.BanList;
import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatUserBanList;

public class BukkitChatUserBanList extends BukkitChatBanList<GameProfile> implements ChatUserBanList {
	public BukkitChatUserBanList(BukkitParser parser, BanList banList) {
		super(parser, banList);
	}

	@Override
	public ChatBanEntry add(GameProfile key) {
		return this.add(key.getName());
	}

	@Override
	public ChatBanEntry add(GameProfile key, String reason, String source, Date expires) {
		return this.add(key.getName(), reason, source, expires);
	}

	@Override
	public ChatBanEntry get(GameProfile key) {
		return this.get(key.getName());
	}

	@Override
	public void remove(GameProfile key) {
		this.remove(key.getName());
	}

	@Override
	public boolean isBanned(GameProfile gameProfile) {
		return this.isBanned(gameProfile.getName());
	}
}