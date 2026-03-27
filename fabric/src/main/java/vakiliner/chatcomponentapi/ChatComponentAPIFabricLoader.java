package vakiliner.chatcomponentapi;

import net.fabricmc.api.ModInitializer;
import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatcomponentapi.fabric.IFabricChatPlugin;

public class ChatComponentAPIFabricLoader implements ModInitializer, IFabricChatPlugin {
	public static final FabricParser PARSER = new FabricParser();

	public void onInitialize() {
	}
}