package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Collection;
import org.bukkit.BanList;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserList;
import vakiliner.chatcomponentapi.util.ParseCollection;

public abstract class BukkitChatBanList<K> implements ChatStoredUserList<K, ChatBanEntry> {
	protected final BukkitParser parser;
	protected final BanList banList;

	public BukkitChatBanList(BukkitParser parser, BanList banList) {
		this.parser = parser;
		this.banList = banList;
	}

	public BanList getImpl() {
		return this.banList;
	}

	public ChatBanEntry get(String key) {
		return this.parser.toChatBanEntry(this.banList.getBanEntry(key));
	}

	public void remove(String key) {
		this.banList.pardon(key);
	}

	public boolean isBanned(String key) {
		return this.banList.isBanned(key);
	}

	public Collection<ChatBanEntry> getEntries() {
		return new ParseCollection<>(this.banList.getBanEntries(), this.parser::toChatBanEntry);
	}

	public boolean isEmpty() {
		return this.banList.getBanEntries().isEmpty();
	}
}