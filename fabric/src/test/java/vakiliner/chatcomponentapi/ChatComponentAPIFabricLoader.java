package vakiliner.chatcomponentapi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.fabric.FabricParser;

public class ChatComponentAPIFabricLoader implements ModInitializer {
	public static final FabricParser PARSER = new FabricParser();
	public static final Logger LOGGER = LogManager.getLogger("chatcomponentapi");
	private static final List<Throwable> ERRORS = new ArrayList<>();
	private static int failCount = 0;

	public void onInitialize() {
		LOGGER.info("Tests started");
		test(() -> {
			Component input = new TextComponent("123");
			ChatComponent output = FabricParser.fabric(input);
			Component result = FabricParser.fabric(output);
			return input.equals(result);
		});
		LOGGER.info("Fails " + failCount);
		ERRORS.forEach(Throwable::printStackTrace);
	}

	@SuppressWarnings("unused")
	private static boolean test(Runnable runnable) {
		return test(() -> {
			runnable.run();
			return true;
		});
	}

	private static boolean test(BooleanSupplier runnable) {
		final boolean success;
		try {
			success = runnable.getAsBoolean();
		} catch (Throwable err) {
			ERRORS.add(err);
			failCount++;
			return false;
		}
		if (!success) {
			failCount++;
			ERRORS.add(new RuntimeException("Test failed"));
		}
		return success;
	}
}