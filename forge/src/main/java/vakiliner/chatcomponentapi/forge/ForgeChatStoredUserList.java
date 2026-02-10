package vakiliner.chatcomponentapi.forge;

import java.util.Collection;
import java.util.function.Function;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserList;
import vakiliner.chatcomponentapi.util.ParseCollection;

public abstract class ForgeChatStoredUserList<Key, List extends UserList<Key, Input>, Output extends ChatStoredUserEntry, Input extends UserListEntry<Key>> implements ChatStoredUserList<Key, Output> {
	protected final ForgeParser parser;
	protected final List list;
	protected final Function<Input, Output> i2o;

	public ForgeChatStoredUserList(ForgeParser parser, List list, Function<Input, Output> i2o) {
		this.parser = parser;
		this.list = list;
		this.i2o = i2o;
	}

	public Output get(Key key) {
		return this.i2o.apply(this.list.get(key));
	}

	public Collection<Output> getEntries() {
		return new ParseCollection<>(this.list.getEntries(), this.i2o);
	}

	public boolean isEmpty() {
		return this.list.isEmpty();
	}

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