package vakiliner.chatcomponentapi.craftbukkit;

import java.net.InetAddress;
import org.bukkit.ban.IpBanList;
import com.google.common.net.InetAddresses;
import vakiliner.chatcomponentapi.base.ChatIpBanList;

public class BukkitChatIpBanList extends BukkitChatBanList<String, InetAddress> implements ChatIpBanList {
	public BukkitChatIpBanList(BukkitParser parser, IpBanList banList) {
		super(parser, banList);
	}

	@Override
	protected InetAddress cast(String ip) {
		return InetAddresses.forString(ip);
	}
}