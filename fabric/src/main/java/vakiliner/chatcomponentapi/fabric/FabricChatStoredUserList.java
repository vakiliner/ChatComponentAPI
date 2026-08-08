package vakiliner.chatcomponentapi.fabric;

import java.util.Collection;
import java.util.Objects;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import vakiliner.chatcomponentapi.base.ChatStoredUserEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserList;
import vakiliner.chatcomponentapi.util.ParseCollection;

public abstract class FabricChatStoredUserList<Key, HandleKey, Entry extends ChatStoredUserEntry, HandleEntry extends StoredUserEntry<HandleKey>, List extends StoredUserList<HandleKey, HandleEntry>> implements ChatStoredUserList<Key, Entry> {
	protected final FabricParser parser;
	protected final List list;

	public FabricChatStoredUserList(FabricParser parser, List list) {
		this.parser = Objects.requireNonNull(parser);
		this.list = Objects.requireNonNull(list);
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
		return new ParseCollection<>(this.list.getEntries(), this::cast);
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
			@SuppressWarnings("rawtypes")
			FabricChatStoredUserList other = (FabricChatStoredUserList) obj;
			return this.parser.equals(other.parser) && this.list.equals(other.list);
		} else {
			return false;
		}
	}
}