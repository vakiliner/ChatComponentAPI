package vakiliner.chatcomponentapi;

import net.fabricmc.api.ModInitializer;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.fabric.FabricParser;

public class ChatComponentAPIFabricLoader implements ModInitializer, IChatPlugin {
	public static final FabricParser PARSER = new FabricParser();

	public void onInitialize() {
	}
}