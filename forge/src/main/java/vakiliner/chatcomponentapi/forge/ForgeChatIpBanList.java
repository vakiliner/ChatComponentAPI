package vakiliner.chatcomponentapi.forge;

import java.net.SocketAddress;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.server.management.IPBanList;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatIpBanList;

public class ForgeChatIpBanList extends ForgeChatStoredUserList<String, IPBanList, ChatBanEntry, IPBanEntry> implements ChatIpBanList {
	public ForgeChatIpBanList(ForgeParser parser, IPBanList list) {
		super(parser, list, parser::toChatBanEntry);
	}

	public ChatBanEntry get(SocketAddress address) {
		return this.i2o.apply(this.list.get(address));
	}

	public boolean isBanned(SocketAddress address) {
		return this.list.isBanned(address);
	}

	public boolean isBanned(String ip) {
		return this.list.isBanned(ip);
	}
}