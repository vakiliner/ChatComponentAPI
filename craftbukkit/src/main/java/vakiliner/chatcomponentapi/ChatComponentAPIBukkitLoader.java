package vakiliner.chatcomponentapi;

import org.bukkit.plugin.java.JavaPlugin;
import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;

public class ChatComponentAPIBukkitLoader extends JavaPlugin {
	public static final BukkitParser PARSER;

	static {
		BukkitParser impl = new BukkitParser();
		PARSER = impl;
	}
}