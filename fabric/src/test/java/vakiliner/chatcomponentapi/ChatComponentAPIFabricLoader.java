package vakiliner.chatcomponentapi;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.fabric.FabricDevTester;
import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatcomponentapi.fabric.IFabricChatPlugin;

public class ChatComponentAPIFabricLoader implements ModInitializer, IFabricChatPlugin {
	public static final FabricParser PARSER = new FabricParser();
	public static final FabricDevTester TESTER = new FabricDevTester(PARSER);

	@Deprecated
	public static FabricParser load() {
		return PARSER;
	}
	
	public void onInitialize() {
		try {
			net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback.EVENT.register(this::register);
		} catch (NoClassDefFoundError err) {
		}
		TESTER.startTests(TESTER::startTests);
	}

	public void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		LiteralArgumentBuilder<CommandSourceStack> chatcomponentapi = LiteralArgumentBuilder.literal("chatcomponentapi");
		LiteralArgumentBuilder<CommandSourceStack> test = LiteralArgumentBuilder.literal("test");
		dispatcher.register(chatcomponentapi.requires((stack) -> {
			return stack.hasPermission(2);
		}).then(test.executes((context) -> {
			CommandSourceStack commandSourceStack = context.getSource();
			CommandSource commandSource = commandSourceStack.getEntity();
			if (commandSource == null) commandSource = commandSourceStack.getServer();
			ChatCommandSender chatCommandSender = PARSER.toChatCommandSender(commandSource);
			return TESTER.startTests(() -> TESTER.startTestsWithCommandSender(chatCommandSender));
		})));
	}
}