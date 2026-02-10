package vakiliner.chatcomponentapi.fabric;

import java.net.SocketAddress;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatIpBanList;

public class FabricChatIpBanList extends FabricChatStoredUserList<String, IpBanList, ChatBanEntry, IpBanListEntry> implements ChatIpBanList {
	public FabricChatIpBanList(FabricParser parser, IpBanList list) {
		super(parser, list, parser::toChatBanEntry);
	}

	public ChatBanEntry add(String key) {
		IpBanListEntry entry = new IpBanListEntry(key);
		this.list.add(entry);
		return this.i2o.apply(entry);
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