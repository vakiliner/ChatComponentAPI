package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanList;
import vakiliner.chatcomponentapi.util.ParseCollection;

public abstract class BukkitChatBanList<Key> implements ChatBanList<Key> {
	protected final BukkitParser parser;
	protected final BanList banList;

	public BukkitChatBanList(BukkitParser parser, BanList banList) {
		this.parser = Objects.requireNonNull(parser);
		this.banList = Objects.requireNonNull(banList);
	}

	public BanList getImpl() {
		return this.banList;
	}

	protected abstract String cast(Key key);

	protected ChatBanEntry cast(BanEntry entry) {
		return this.parser.toChatBanEntry(entry);
	}

	@Override
	public ChatBanEntry add(Key key) {
		return this.add(key, null, null, null);
	}

	@Override
	public ChatBanEntry add(Key key, String reason, String source, Date expires) {
		return this.cast(this.banList.addBan(this.cast(key), reason, expires, source));
	}

	@Override
	public ChatBanEntry get(Key key) {
		return this.cast(this.banList.getBanEntry(this.cast(key)));
	}

	@Override
	public void remove(Key key) {
		this.banList.pardon(this.cast(key));
	}

	@Override
	public boolean isBanned(Key key) {
		return this.banList.isBanned(this.cast(key));
	}

	@Override
	public Collection<ChatBanEntry> getEntries() {
		return new ParseCollection<>(this.banList.getBanEntries(), this::cast);
	}

	@Override
	public boolean isEmpty() {
		return this.banList.getBanEntries().isEmpty();
	}
}