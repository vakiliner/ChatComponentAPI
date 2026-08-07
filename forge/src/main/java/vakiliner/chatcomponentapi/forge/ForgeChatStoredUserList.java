package vakiliner.chatcomponentapi.forge;

import java.util.Collection;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import vakiliner.chatcomponentapi.base.ChatStoredUserEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserList;
import vakiliner.chatcomponentapi.util.ParseCollection;

public abstract class ForgeChatStoredUserList<Key, HandleKey, Output extends ChatStoredUserEntry, Input extends StoredUserEntry<HandleKey>, List extends StoredUserList<HandleKey, Input>> implements ChatStoredUserList<Key, Output> {
	protected final ForgeParser parser;
	protected final List list;

	public ForgeChatStoredUserList(ForgeParser parser, List list) {
		this.parser = parser;
		this.list = list;
	}

	protected abstract HandleKey cast(Key key);

	protected abstract Output cast(Input input);

	@Override
	public Output get(Key key) {
		return this.cast(this.list.get(this.cast(key)));
	}

	@Override
	public void remove(Key key) {
		this.list.remove(this.cast(key));
	}

	@Override
	public Collection<Output> getEntries() {
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
			ForgeChatStoredUserList other = (ForgeChatStoredUserList) obj;
			return this.parser.equals(other.parser) && this.list.equals(other.list);
		} else {
			return false;
		}
	}
}