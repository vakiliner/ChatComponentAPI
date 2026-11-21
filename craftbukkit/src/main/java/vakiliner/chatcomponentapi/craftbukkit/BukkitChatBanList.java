package vakiliner.chatcomponentapi.craftbukkit;

import static java.util.Objects.requireNonNull;
import java.util.Collection;
import java.util.Date;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import com.google.common.collect.Collections2;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanList;

public abstract class BukkitChatBanList<Key> implements ChatBanList<Key> {
	protected final BukkitParser parser;
	protected final BanList banList;

	public BukkitChatBanList(BukkitParser parser, BanList banList) {
		this.parser = requireNonNull(parser);
		this.banList = requireNonNull(banList);
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
		return Collections2.transform(this.banList.getBanEntries(), this::cast);
	}

	@Override
	public boolean isEmpty() {
		return this.banList.getBanEntries().isEmpty();
	}
}
