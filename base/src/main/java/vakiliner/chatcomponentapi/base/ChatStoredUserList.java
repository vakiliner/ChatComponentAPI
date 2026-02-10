package vakiliner.chatcomponentapi.base;

import java.util.Collection;

public interface ChatStoredUserList<Key, Entry extends ChatStoredUserEntry> {
	Entry get(Key key);

	void remove(Key key);

	Collection<Entry> getEntries();

	boolean isEmpty();
}