package vakiliner.chatcomponentapi.forge;

import java.util.Date;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatIpBanList;

public class ForgeChatIpBanList extends ForgeChatBanList<String, String, IpBanListEntry, IpBanList> implements ChatIpBanList {
	public ForgeChatIpBanList(ForgeParser parser, IpBanList list) {
		super(parser, list);
	}

	@Override
	protected String cast(String ip) {
		return ip;
	}

	@Override
	protected ChatBanEntry cast(IpBanListEntry entry) {
		return this.parser.toChatBanEntry(entry);
	}

	@Override
	protected IpBanListEntry create(String ip) {
		return new IpBanListEntry(this.cast(ip));
	}

	@Override
	protected IpBanListEntry create(String ip, String reason, String source, Date expires) {
		return new IpBanListEntry(this.cast(ip), null, source, expires, reason);
	}

	@Override
	public boolean isBanned(String ip) {
		return this.list.isBanned(this.cast(ip));
	}
}
