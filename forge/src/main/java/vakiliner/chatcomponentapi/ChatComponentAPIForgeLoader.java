package vakiliner.chatcomponentapi;

import net.minecraftforge.fml.common.Mod;
import vakiliner.chatcomponentapi.forge.ForgeParser;
import vakiliner.chatcomponentapi.forge.IForgeChatPlugin;

@Mod("chatcomponentapi")
public class ChatComponentAPIForgeLoader implements IForgeChatPlugin {
	public static final ForgeParser PARSER = new ForgeParser();
}