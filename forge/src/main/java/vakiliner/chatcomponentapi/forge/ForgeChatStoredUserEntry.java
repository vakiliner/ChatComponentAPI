package vakiliner.chatcomponentapi.forge;

import net.minecraft.server.management.UserListEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserEntry;

public class ForgeChatStoredUserEntry<Entry extends UserListEntry<?>> implements ChatStoredUserEntry {
	protected final ForgeParser parser;
	protected final Entry entry;

	public ForgeChatStoredUserEntry(ForgeParser parser, Entry entry) {
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
			ForgeChatStoredUserEntry other = (ForgeChatStoredUserEntry) obj;
			return this.parser.equals(other.parser) && this.entry.equals(other.entry);
		} else {
			return false;
		}
	}
}