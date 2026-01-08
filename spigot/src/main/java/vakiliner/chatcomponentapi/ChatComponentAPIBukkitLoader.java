package vakiliner.chatcomponentapi;

import org.bukkit.plugin.java.JavaPlugin;
import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;
import vakiliner.chatcomponentapi.spigot.SpigotParser;

public class ChatComponentAPIBukkitLoader extends JavaPlugin {
	public static final BukkitParser PARSER;

	static {
		BukkitParser impl;
		try {
			impl = new SpigotParser();
		} catch (NoClassDefFoundError a) {
			impl = new BukkitParser();
		}
		PARSER = impl;
	}
}