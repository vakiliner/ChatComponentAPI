package vakiliner.chatcomponentapi.forge;

import java.util.Date;

import net.minecraft.server.players.BanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;

public class ForgeChatBanEntry<Entry extends BanListEntry<?>> extends ForgeChatStoredUserEntry<Entry> implements ChatBanEntry {
	public ForgeChatBanEntry(ForgeParser parser, Entry entry) {
		super(parser, entry);
	}

	@Override
	public String getReason() {
		return this.entry.getReason();
	}

	@Override
	public String getSource() {
		return this.entry.getSource();
	}

	@Override
	public Date getExpires() {
		return this.entry.getExpires();
	}
}