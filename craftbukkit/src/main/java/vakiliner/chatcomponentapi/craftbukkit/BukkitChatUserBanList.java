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

	public ChatBanEntry add(GameProfile key) {
		return this.add(key.getName());
	}

	public ChatBanEntry add(GameProfile key, String reason, String source, Date expires) {
		return this.add(key.getName(), reason, source, expires);
	}

	public ChatBanEntry get(GameProfile key) {
		return this.get(key.getName());
	}

	public void remove(GameProfile key) {
		this.remove(key.getName());
	}

	public boolean isBanned(GameProfile gameProfile) {
		return this.isBanned(gameProfile.getName());
	}
}