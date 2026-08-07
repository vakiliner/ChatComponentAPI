package vakiliner.chatcomponentapi;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;
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

	@Override
	public void onInitialize() {
		try {
			net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register(this::register);
		} catch (NoClassDefFoundError err) {
		}
		TESTER.startTests(TESTER::startTests);
	}

	public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
		LiteralArgumentBuilder<CommandSourceStack> chatcomponentapi = LiteralArgumentBuilder.literal("chatcomponentapi");
		LiteralArgumentBuilder<CommandSourceStack> test = LiteralArgumentBuilder.literal("test");
		dispatcher.register(chatcomponentapi.requires((stack) -> {
			return stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
		}).then(test.executes((context) -> {
			CommandSourceStack stack = context.getSource();
			Entity entity = stack.getEntity();
			CommandSource commandSource = entity != null ? entity instanceof ServerPlayer player ? player.commandSource() : CommandSource.NULL : stack.getServer();
			ChatCommandSender chatCommandSender = PARSER.toChatCommandSender(commandSource);
			return TESTER.startTests(() -> TESTER.startTestsWithCommandSender(chatCommandSender));
		})));
	}
}