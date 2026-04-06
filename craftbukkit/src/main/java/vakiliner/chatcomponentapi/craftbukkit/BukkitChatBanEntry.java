package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Date;
import org.bukkit.BanEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;

public class BukkitChatBanEntry implements ChatBanEntry {
	protected final BukkitParser parser;
	protected final BanEntry banEntry;

	public BukkitChatBanEntry(BukkitParser parser, BanEntry banEntry) {
		this.parser = parser;
		this.banEntry = banEntry;
	}

	public BanEntry getImpl() {
		return this.banEntry;
	}

	public String getReason() {
		return this.banEntry.getReason();
	}

	public String getSource() {
		return this.banEntry.getSource();
	}

	public Date getExpires() {
		return this.banEntry.getExpiration();
	}
}