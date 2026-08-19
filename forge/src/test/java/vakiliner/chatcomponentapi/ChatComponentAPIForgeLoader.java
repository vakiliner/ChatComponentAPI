package vakiliner.chatcomponentapi;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.forge.ForgeDevTester;
import vakiliner.chatcomponentapi.forge.ForgeParser;
import vakiliner.chatcomponentapi.forge.IForgeChatPlugin;

@Mod("chatcomponentapi")
@EventBusSubscriber(modid = "chatcomponentapi", bus = Bus.FORGE)
public class ChatComponentAPIForgeLoader implements IForgeChatPlugin {
	public static final ForgeParser PARSER = new ForgeParser();
	public static final ForgeDevTester TESTER = new ForgeDevTester(PARSER);

	public ChatComponentAPIForgeLoader() {
		MinecraftForge.EVENT_BUS.register(this);
		TESTER.startTests(TESTER::startTests);
	}

	@Deprecated
	public static ForgeParser load() {
		return PARSER;
	}

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		dispatcher.register(testCommand());
	}

	private static LiteralArgumentBuilder<CommandSourceStack> testCommand() {
		LiteralArgumentBuilder<CommandSourceStack> chatcomponentapi = LiteralArgumentBuilder.literal("chatcomponentapi");
		LiteralArgumentBuilder<CommandSourceStack> test = LiteralArgumentBuilder.literal("test");
		return chatcomponentapi.requires((stack) -> {
			return stack.hasPermission(2);
		}).then(test.executes((context) -> {
			CommandSourceStack stack = context.getSource();
			CommandSource commandSource = stack.getEntity();
			if (commandSource == null) {
				commandSource = stack.getServer();
			}
			ChatCommandSender chatCommandSender = PARSER.toChatCommandSender(commandSource);
			return TESTER.startTests(() -> TESTER.startTestsWithCommandSender(chatCommandSender));
		}));
	}
}