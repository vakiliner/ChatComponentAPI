package vakiliner.chatcomponentapi.fabric;

import net.minecraft.server.players.StoredUserEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserEntry;

public class FabricChatStoredUserEntry<Entry extends StoredUserEntry<?>> implements ChatStoredUserEntry {
	protected final FabricParser parser;
	protected final Entry entry;

	public FabricChatStoredUserEntry(FabricParser parser, Entry entry) {
		this.parser = parser;
		this.entry = entry;
	}

	public Entry getImpl() {
		return this.entry;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			@SuppressWarnings("rawtypes")
			FabricChatStoredUserEntry other = (FabricChatStoredUserEntry) obj;
			return this.parser.equals(other.parser) && this.entry.equals(other.entry);
		} else {
			return false;
		}
	}
}