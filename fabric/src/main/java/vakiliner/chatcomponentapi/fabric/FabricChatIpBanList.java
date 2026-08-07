package vakiliner.chatcomponentapi.fabric;

import java.util.Date;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatIpBanList;

public class FabricChatIpBanList extends FabricChatBanList<String, String, IpBanListEntry, IpBanList> implements ChatIpBanList {
	public FabricChatIpBanList(FabricParser parser, IpBanList list) {
		super(parser, list);
	}

	@Override
	protected String cast(String ip) {
		return ip;
	}

	@Override
	protected ChatBanEntry cast(IpBanListEntry input) {
		return this.parser.toChatBanEntry(input);
	}

	@Override
	protected IpBanListEntry create(String key) {
		return new IpBanListEntry(key);
	}

	@Override
	protected IpBanListEntry create(String key, String reason, String source, Date expires) {
		return new IpBanListEntry(key, null, source, expires, reason);
	}

	@Override
	public boolean isBanned(String ip) {
		return this.list.isBanned(ip);
	}
}