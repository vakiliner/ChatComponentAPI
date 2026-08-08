package vakiliner.chatcomponentapi.forge;

import java.util.Date;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.server.management.IPBanList;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatIpBanList;

public class ForgeChatIpBanList extends ForgeChatBanList<String, String, IPBanEntry, IPBanList> implements ChatIpBanList {
	public ForgeChatIpBanList(ForgeParser parser, IPBanList list) {
		super(parser, list);
	}

	@Override
	protected String cast(String ip) {
		return ip;
	}

	@Override
	protected ChatBanEntry cast(IPBanEntry entry) {
		return this.parser.toChatBanEntry(entry);
	}

	@Override
	protected IPBanEntry create(String ip) {
		return new IPBanEntry(this.cast(ip));
	}

	@Override
	protected IPBanEntry create(String ip, String reason, String source, Date expires) {
		return new IPBanEntry(this.cast(ip), null, source, expires, reason);
	}

	@Override
	public boolean isBanned(String ip) {
		return this.list.isBanned(this.cast(ip));
	}
}