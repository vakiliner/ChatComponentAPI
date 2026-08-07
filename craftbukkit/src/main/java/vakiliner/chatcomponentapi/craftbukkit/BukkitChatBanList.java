package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Collection;
import java.util.Date;
import org.bukkit.BanList;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanList;
import vakiliner.chatcomponentapi.util.ParseCollection;

public abstract class BukkitChatBanList<Key, HandleKey> implements ChatBanList<Key> {
	protected final BukkitParser parser;
	protected final BanList<HandleKey> banList;

	public BukkitChatBanList(BukkitParser parser, BanList<HandleKey> banList) {
		this.parser = parser;
		this.banList = banList;
	}

	public BanList<HandleKey> getImpl() {
		return this.banList;
	}

	protected abstract HandleKey cast(Key key);

	public ChatBanEntry add(Key key) {
		return this.add(key, null, null, null);
	}

	public ChatBanEntry add(Key key, String reason, String source, Date expires) {
		return this.parser.toChatBanEntry(this.banList.addBan(this.cast(key), reason, expires, source));
	}

	public ChatBanEntry get(Key key) {
		return this.parser.toChatBanEntry(this.banList.getBanEntry(this.cast(key)));
	}

	public void remove(Key key) {
		this.banList.pardon(this.cast(key));
	}

	public boolean isBanned(Key key) {
		return this.banList.isBanned(this.cast(key));
	}

	@Override
	public Collection<ChatBanEntry> getEntries() {
		return new ParseCollection<>(this.banList.getEntries(), this.parser::toChatBanEntry);
	}

	@Override
	public boolean isEmpty() {
		return this.banList.getEntries().isEmpty();
	}
}