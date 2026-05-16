package vakiliner.chatcomponentapi.base;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatTextComponent;

public abstract class DevTester {
	private final List<Throwable> ERRORS = new ArrayList<>();
	private int testCount = 0;
	private int failCount = 0;

	protected abstract void log(String message);

	public void startTests() {
		this.startTestsBase();
	}

	public final void startTestsBase() {
		test("ChatComponentWithLegacyText", () -> {
			ChatTextComponent legacyComponent = new ChatTextComponent("legacy");
			ChatComponent chatComponent = new ChatTextComponent("123");
			ChatComponentWithLegacyText chatComponentWithLegacyText = chatComponent.withLegacyComponent(legacyComponent);
			return chatComponentWithLegacyText.toLegacyText().equals(legacyComponent.toLegacyText()) && chatComponentWithLegacyText.getComponent() == chatComponent;
		});
		test("Looping ChatComponent extra", () -> {
			ChatComponent component1 = new ChatTextComponent("123");
			ChatComponent component2 = new ChatTextComponent("456");
			component1.append(component2);
			try {
				component2.append(component1);
			} catch (Throwable err) {
				component2.append(component1.clone());
			}
			component1.clone();
		});
	}

	public final void startTestsWithCommandSender(ChatCommandSender chatCommandSender) {
		test("Send message", () -> {
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"));
		});
		test("Send message with ChatMessageType", () -> {
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.CHAT, null);
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.SYSTEM, null);
		});
		test("Send message with ChatMessageType & UUID", () -> {
			UUID uuid = chatCommandSender instanceof ChatPlayer ? ((ChatPlayer) chatCommandSender).getUniqueId() : null;
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.CHAT, uuid);
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.SYSTEM, uuid);
		});
	}

	protected final boolean test(String name, Runnable runnable) {
		return test(name, () -> {
			runnable.run();
			return true;
		});
	}

	protected final boolean test(String name, BooleanSupplier runnable) {
		final boolean success;
		final Throwable error;
		{
			boolean output = false;
			Throwable fail = null;
			try {
				output = runnable.getAsBoolean();
			} catch (Throwable err) {
				fail = err;
			}
			success = output;
			error = fail;
		}
		testCount++;
		if (!success) {
			failCount++;
			ERRORS.add(new RuntimeException("Test failed: " + name, error));
		}
		return success;
	}

	public final int startTests(Runnable runnable) {
		this.log("Tests started");
		runnable.run();
		int tests = testCount;
		int fails = failCount;
		this.log("Fails " + fails + '/' + testCount);
		ERRORS.forEach(Throwable::printStackTrace);
		clearTests();
		return fails > 0 ? -1 : tests;
	}

	protected final void clearTests() {
		testCount = 0;
		failCount = 0;
		ERRORS.clear();
	}
}