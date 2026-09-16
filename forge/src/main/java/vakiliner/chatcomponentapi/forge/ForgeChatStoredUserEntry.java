package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import net.minecraft.server.players.StoredUserEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserEntry;

public class ForgeChatStoredUserEntry<Entry extends StoredUserEntry<?>> implements ChatStoredUserEntry {
	protected final ForgeParser parser;
	protected final Entry entry;

	public ForgeChatStoredUserEntry(ForgeParser parser, Entry entry) {
		this.parser = Objects.requireNonNull(parser);
		this.entry = Objects.requireNonNull(entry);
	}

	public Entry getImpl() {
		return this.entry;
	}

	@Override
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
