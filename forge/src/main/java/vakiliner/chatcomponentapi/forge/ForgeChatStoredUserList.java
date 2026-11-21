package vakiliner.chatcomponentapi.forge;

import static java.util.Objects.requireNonNull;
import java.util.Collection;
import com.google.common.collect.Collections2;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import vakiliner.chatcomponentapi.base.ChatStoredUserEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserList;

public abstract class ForgeChatStoredUserList<Key, HandleKey, Entry extends ChatStoredUserEntry, HandleEntry extends StoredUserEntry<HandleKey>, List extends StoredUserList<HandleKey, HandleEntry>> implements ChatStoredUserList<Key, Entry> {
	protected final ForgeParser parser;
	protected final List list;

	public ForgeChatStoredUserList(ForgeParser parser, List list) {
		this.parser = requireNonNull(parser);
		this.list = requireNonNull(list);
	}

	protected abstract HandleKey cast(Key key);

	protected abstract Entry cast(HandleEntry entry);

	@Override
	public Entry get(Key key) {
		return this.cast(this.list.get(this.cast(key)));
	}

	@Override
	public void remove(Key key) {
		this.list.remove(this.cast(key));
	}

	@Override
	public Collection<Entry> getEntries() {
		return Collections2.transform(this.list.getEntries(), this::cast);
	}

	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			ForgeChatStoredUserList<?, ?, ?, ?, ?> other = (ForgeChatStoredUserList<?, ?, ?, ?, ?>) obj;
			return this.parser.equals(other.parser) && this.list.equals(other.list);
		} else {
			return false;
		}
	}
}
