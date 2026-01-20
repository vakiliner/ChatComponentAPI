package vakiliner.chatcomponentapi;

import net.minecraftforge.fml.common.Mod;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.forge.ForgeParser;

@Mod("chatcomponentapi")
public class ChatComponentAPIForgeLoader implements IChatPlugin {
	public static final ForgeParser PARSER = new ForgeParser();
}