package vakiliner.chatcomponentapi.base;

import java.util.Collection;

public interface ChatStoredUserList<K, V extends ChatStoredUserEntry> {
	V get(K key);

	void remove(K key);

	Collection<V> getEntries();

	boolean isEmpty();
}