package vakiliner.chatcomponentapi.craftbukkit;

import org.bukkit.BanList;
import vakiliner.chatcomponentapi.base.ChatIpBanList;

public class BukkitChatIpBanList extends BukkitChatBanList<String> implements ChatIpBanList {
	public BukkitChatIpBanList(BukkitParser parser, BanList banList) {
		super(parser, banList);
	}
}