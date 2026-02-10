package vakiliner.chatcomponentapi.fabric;

import java.util.Date;
import net.minecraft.server.players.BanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;

public class FabricChatBanEntry<Entry extends BanListEntry<?>> extends FabricChatStoredUserEntry<Entry> implements ChatBanEntry {
	public FabricChatBanEntry(FabricParser parser, Entry entry) {
		super(parser, entry);
	}

	public String getReason() {
		return this.entry.getReason();
	}

	public String getSource() {
		return this.entry.getSource();
	}

	public Date getExpires() {
		return this.entry.getExpires();
	}
}