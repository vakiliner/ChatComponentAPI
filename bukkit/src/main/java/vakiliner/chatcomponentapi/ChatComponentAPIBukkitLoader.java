package vakiliner.chatcomponentapi;

import org.bukkit.plugin.java.JavaPlugin;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;
import vakiliner.chatcomponentapi.paper.PaperParser;
import vakiliner.chatcomponentapi.spigot.SpigotParser;

public class ChatComponentAPIBukkitLoader extends JavaPlugin implements IChatPlugin {
	public static final BukkitParser PARSER;

	static {
		BukkitParser impl;
		try {
			impl = new PaperParser();
		} catch (NoClassDefFoundError a) {
			try {
				impl = new SpigotParser();
			} catch (NoClassDefFoundError b) {
				impl = new BukkitParser();
			}
		}
		PARSER = impl;
	}
}