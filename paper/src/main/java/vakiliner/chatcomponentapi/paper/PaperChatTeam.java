package vakiliner.chatcomponentapi.paper;

import org.bukkit.scoreboard.Team;
import vakiliner.chatcomponentapi.craftbukkit.BukkitChatTeam;

@Deprecated
public class PaperChatTeam extends BukkitChatTeam {
	public PaperChatTeam(PaperParser parser, Team team) {
		super(parser, team);
	}
}
