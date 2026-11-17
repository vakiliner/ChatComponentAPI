package vakiliner.chatcomponentapi.paper;

import org.bukkit.entity.Player;
import vakiliner.chatcomponentapi.craftbukkit.BukkitChatPlayer;

@Deprecated
public class PaperChatPlayer extends BukkitChatPlayer {
	public PaperChatPlayer(PaperParser parser, Player player) {
		super(parser, player);
	}
}
