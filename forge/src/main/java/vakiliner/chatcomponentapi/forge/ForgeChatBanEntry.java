package vakiliner.chatcomponentapi.forge;

import java.util.Date;
import net.minecraft.server.management.BanEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;

public class ForgeChatBanEntry<Entry extends BanEntry<?>> extends ForgeChatStoredUserEntry<Entry> implements ChatBanEntry {
	public ForgeChatBanEntry(ForgeParser parser, Entry entry) {
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