package vakiliner.chatcomponentapi.forge;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.scores.PlayerTeam;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.forge.mixin.StyleAccessor;

public class ForgeParser extends BaseParser {
	@Override
	public boolean supportsSeparatorInSelector() {
		return false;
	}

	@Override
	public boolean supportsFontInStyle() {
		return true;
	}

	public void sendMessage(CommandSource commandSource, ChatComponent chatComponent, ChatMessageType type, UUID uuid) {
		commandSource.sendSystemMessage(forge(chatComponent, commandSource instanceof MinecraftServer));
	}

	public void broadcastMessage(PlayerList playerList, ChatComponent chatComponent, ChatMessageType type, UUID uuid) {
		playerList.getServer().sendSystemMessage(forge(chatComponent, true));
		Component component = forge(chatComponent);
		for (ServerPlayer player : playerList.getPlayers()) {
			player.sendSystemMessage(component);
		}
	}

	public void execute(MinecraftServer server, IChatPlugin raw, Runnable runnable) {
		if (raw instanceof IForgeChatPlugin) {
			@SuppressWarnings("unused")
			IForgeChatPlugin chatPlugin = (IForgeChatPlugin) raw;
			server.execute(runnable);
		} else {
			throw new ClassCastException("Invalid plugin");
		}
	}

	public void executeBlocking(MinecraftServer server, IChatPlugin raw, Runnable runnable) {
		if (raw instanceof IForgeChatPlugin) {
			@SuppressWarnings("unused")
			IForgeChatPlugin chatPlugin = (IForgeChatPlugin) raw;
			server.executeBlocking(runnable);
		} else {
			throw new ClassCastException("Invalid plugin");
		}
	}

	public void kickPlayer(ServerPlayer player, ChatComponent reason) {
		player.connection.disconnect(forge(reason));
	}

	public static Component forge(ChatComponent raw) {
		return forge(raw, false);
	}

	public static Component forge(ChatComponent raw, boolean isConsole) {
		if (raw == null) return null;
		final MutableComponent component;
		if (raw instanceof ChatComponentModified) {
			Objects.requireNonNull(raw = ((ChatComponentModified) raw).getComponent(isConsole));
		}
		if (raw instanceof ChatTextComponent chatComponent) {
			component = Component.literal(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent chatComponent) {
			component = Component.translatable(chatComponent.getKey(), chatComponent.getWith().stream().map((c) -> forge(c, isConsole)).toArray());
		} else if (raw instanceof ChatSelectorComponent chatComponent) {
			component = Component.selector(EntitySelector.COMPILABLE_CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(chatComponent.getSelector())).result().orElseThrow(), Optional.ofNullable(forge(chatComponent.getSeparator())));
		} else {
			throw new IllegalArgumentException("Could not parse Component from " + raw.getClass());
		}
		component.setStyle(forge(raw.getStyle()));
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			component.append(forge(chatComponent, isConsole));
		}
		return component;
	}

	public static ChatComponent forge(Component component) {
		if (component == null) return null;
		final ComponentContents contents = component.getContents();
		final ChatComponent chatComponent;
		if (contents instanceof PlainTextContents plainTextContents) {
			chatComponent = new ChatTextComponent(plainTextContents.text());
		} else if (contents instanceof TranslatableContents translatableContents) {
			chatComponent = new ChatTranslateComponent(null, translatableContents.getKey(), Arrays.stream(translatableContents.getArgs()).map((arg) -> arg instanceof Component ? forge((Component) arg) : new ChatTextComponent(String.valueOf(arg))).collect(Collectors.toList()));
		} else if (contents instanceof SelectorContents selectorContents) {
			chatComponent = new ChatSelectorComponent(selectorContents.selector().source());
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + component.getClass());
		}
		chatComponent.setStyle(forge(component.getStyle()));
		for (Component sibling : component.getSiblings()) {
			chatComponent.append(forge(sibling));
		}
		return chatComponent;
	}

	public static Style forge(ChatStyle chatStyle) {
		if (chatStyle == null) return null;
		if (chatStyle.isEmpty()) return Style.EMPTY;
		Identifier font = forge(chatStyle.getFont());
		return StyleAccessor.newStyle(forge(chatStyle.getColor()), null, chatStyle.getBold(), chatStyle.getItalic(), chatStyle.getUnderlined(), chatStyle.getStrikethrough(), chatStyle.getObfuscated(), forge(chatStyle.getClickEvent()), forge(chatStyle.getHoverEvent()), chatStyle.getInsertion(), font != null ? new FontDescription.Resource(font) : null);
	}

	public static ChatStyle forge(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		StyleAccessor accessor = (StyleAccessor) (Object) style;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(forge(accessor.getColor()));
		builder.withBold(accessor.getBold());
		builder.withItalic(accessor.getItalic());
		builder.withUnderlined(accessor.getUnderlined());
		builder.withStrikethrough(accessor.getStrikethrough());
		builder.withObfuscated(accessor.getObfuscated());
		builder.withClickEvent(forge(accessor.getClickEvent()));
		builder.withHoverEvent(forge(accessor.getHoverEvent()));
		builder.withInsertion(accessor.getInsertion());
		if (accessor.$getFont() instanceof FontDescription.Resource font) {
			builder.withFont(forge(font.id()));
		}
		return builder.build();
	}

	public static ClickEvent forge(ChatClickEvent event) {
		String value = event.getValue();
		switch (event.getAction()) {
			case OPEN_URL: return new ClickEvent.OpenUrl(URI.create(value));
			case OPEN_FILE: return new ClickEvent.OpenFile(value);
			case RUN_COMMAND: return new ClickEvent.RunCommand(value);
			case SUGGEST_COMMAND: return new ClickEvent.SuggestCommand(value);
			case CHANGE_PAGE: return new ClickEvent.ChangePage(Integer.parseInt(value));
			case COPY_TO_CLIPBOARD: return new ClickEvent.CopyToClipboard(value);
			default: throw new IllegalArgumentException("Unknown action");
		}
	}

	public static ChatClickEvent forge(ClickEvent event) {
		switch (event.action()) {
			case OPEN_URL: return new ChatClickEvent(ChatClickEvent.Action.OPEN_URL, ((ClickEvent.OpenUrl) event).uri().toString());
			case OPEN_FILE: return new ChatClickEvent(ChatClickEvent.Action.OPEN_FILE, ((ClickEvent.OpenFile) event).path());
			case RUN_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, ((ClickEvent.RunCommand) event).command());
			case SUGGEST_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, ((ClickEvent.SuggestCommand) event).command());
			case CHANGE_PAGE: return new ChatClickEvent(ChatClickEvent.Action.CHANGE_PAGE, Integer.toString(((ClickEvent.ChangePage) event).page()));
			case COPY_TO_CLIPBOARD: return new ChatClickEvent(ChatClickEvent.Action.COPY_TO_CLIPBOARD, ((ClickEvent.CopyToClipboard) event).value());
			default: throw new IllegalArgumentException("Unknown action");
		}
	}

	public static HoverEvent forge(ChatHoverEvent<?> event) {
		if (event == null) return null;
		ChatHoverEvent.Action<?> action = event.getAction();
		if (action == ChatHoverEvent.Action.SHOW_TEXT) {
			return new HoverEvent.ShowText(forge(event.getValue(ChatHoverEvent.Action.SHOW_TEXT)));
		} else if (action == ChatHoverEvent.Action.SHOW_ENTITY) {
			return new HoverEvent.ShowEntity(forge(event.getValue(ChatHoverEvent.Action.SHOW_ENTITY)));
		} else if (action == ChatHoverEvent.Action.SHOW_ITEM) {
			return new HoverEvent.ShowItem(forge(event.getValue(ChatHoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	public static ChatHoverEvent<?> forge(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action action = event.action();
		switch (action) {
			case SHOW_TEXT: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, forge(((HoverEvent.ShowText) event).value()));
			case SHOW_ENTITY: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, forge(((HoverEvent.ShowEntity) event).entity()));
			case SHOW_ITEM: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, forge(((HoverEvent.ShowItem) event).item()));
			default: throw new IllegalArgumentException("Unknown action");
		}
	}

	public static HoverEvent.EntityTooltipInfo forge(ChatHoverEvent.ShowEntity content) {
		return content != null ? new HoverEvent.EntityTooltipInfo(BuiltInRegistries.ENTITY_TYPE.getValue(forge(content.getType())), content.getUniqueId(), forge(content.getName())) : null;
	}

	public static ChatHoverEvent.ShowEntity forge(HoverEvent.EntityTooltipInfo content) {
		return content != null ? new ChatHoverEvent.ShowEntity(forge(BuiltInRegistries.ENTITY_TYPE.getKey(content.type)), content.uuid, forge(content.name.orElse(null))) : null;
	}

	public static ItemStackTemplate forge(ChatHoverEvent.ShowItem content) {
		return content != null ? new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(forge(content.getItem())), content.getCount()) : null;
	}

	public static ChatHoverEvent.ShowItem forge(ItemStackTemplate content) {
		if (content == null) return null;
		return new ChatHoverEvent.ShowItem(forge(BuiltInRegistries.ITEM.getKey(content.item().value())), content.count());
	}

	public static Identifier forge(ChatId id) {
		return id != null ? Identifier.fromNamespaceAndPath(id.getNamespace(), id.getValue()) : null;
	}

	public static ChatId forge(Identifier resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static ChatFormatting forge(ChatTextFormat format) {
		return format != null ? ChatFormatting.getByName(format.name()) : null;
	}

	public static ChatTextFormat forge(ChatFormatting formatting) {
		return formatting != null ? ChatTextFormat.getByName(formatting.getName()) : null;
	}

	public static TextColor forge(ChatTextColor color) {
		return color != null ? TextColor.parseColor(color.toString()).getOrThrow() : null;
	}

	public static ChatTextColor forge(TextColor color) {
		return color != null ? ChatTextColor.of(color.toString()) : null;
	}

	public static NameAndId forge(GameProfile gameProfile) {
		return gameProfile != null ? new NameAndId(gameProfile) : null;
	}

	public static GameProfile forge(NameAndId nameAndId) {
		return nameAndId != null ? new GameProfile(nameAndId.id(), nameAndId.name()) : null;
	}

	public ChatPlayer toChatPlayer(ServerPlayer player) {
		return player != null ? new ForgeChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(MinecraftServer server, GameProfile gameProfile) {
		return gameProfile != null ? new ForgeChatOfflinePlayer(this, server, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSource commandSource) {
		if (commandSource == null) return null;
		if (commandSource instanceof ServerPlayer player) {
			return this.toChatPlayer(player);
		} else if (commandSource instanceof MinecraftServer server) {
			return this.toChatServer(server);
		}
		return new ForgeChatCommandSender(this, commandSource);
	}

	public ChatTeam toChatTeam(PlayerTeam team) {
		return team != null ? new ForgeChatTeam(this, team) : null;
	}

	public ChatServer toChatServer(MinecraftServer server) {
		return server != null ? new ForgeChatServer(this, server) : null;
	}

	public ChatPlayerList toChatPlayerList(PlayerList playerList) {
		return playerList != null ? new ForgeChatPlayerList(this, playerList) : null;
	}
}