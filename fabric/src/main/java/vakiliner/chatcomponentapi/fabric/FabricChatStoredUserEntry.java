package vakiliner.chatcomponentapi.fabric;

import static java.util.Objects.requireNonNull;
import net.minecraft.server.players.StoredUserEntry;
import vakiliner.chatcomponentapi.base.ChatStoredUserEntry;

public class FabricChatStoredUserEntry<Entry extends StoredUserEntry<?>> implements ChatStoredUserEntry {
	protected final FabricParser parser;
	protected final Entry entry;

	public FabricChatStoredUserEntry(FabricParser parser, Entry entry) {
		this.parser = requireNonNull(parser);
		this.entry = requireNonNull(entry);
	}

	public Entry getImpl() {
		return this.entry;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			FabricChatStoredUserEntry<?> other = (FabricChatStoredUserEntry<?>) obj;
			return this.parser.equals(other.parser) && this.entry.equals(other.entry);
		} else {
			return false;
		}
	}
}
