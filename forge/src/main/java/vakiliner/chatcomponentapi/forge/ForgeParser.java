package vakiliner.chatcomponentapi.forge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.SelectorTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
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

public class ForgeParser extends BaseParser {
	private static final Constructor<Style> STYLE_CONSTRUCTOR;
	private static final Field STYLE_COLOR;
	private static final Field STYLE_BOLD;
	private static final Field STYLE_ITALIC;
	private static final Field STYLE_UNDERLINED;
	private static final Field STYLE_STRIKETHROUGH;
	private static final Field STYLE_OBFUSCATED;
	private static final Field STYLE_CLICK_EVENT;
	private static final Field STYLE_HOVER_EVENT;
	private static final Field STYLE_INSERTION;
	private static final Field STYLE_FONT;
	private static final Field ITEM_HOVER_ITEM;
	private static final Field ITEM_HOVER_COUNT;

	static {
		try {
			STYLE_CONSTRUCTOR = Style.class.getDeclaredConstructor(Color.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, ClickEvent.class, HoverEvent.class, String.class, ResourceLocation.class);
			STYLE_CONSTRUCTOR.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
		try {
			STYLE_COLOR = Style.class.getDeclaredField("field_150247_b");
			STYLE_COLOR.setAccessible(true);
			STYLE_BOLD = Style.class.getDeclaredField("field_150248_c");
			STYLE_BOLD.setAccessible(true);
			STYLE_ITALIC = Style.class.getDeclaredField("field_150245_d");
			STYLE_ITALIC.setAccessible(true);
			STYLE_UNDERLINED = Style.class.getDeclaredField("field_150246_e");
			STYLE_UNDERLINED.setAccessible(true);
			STYLE_STRIKETHROUGH = Style.class.getDeclaredField("field_150243_f");
			STYLE_STRIKETHROUGH.setAccessible(true);
			STYLE_OBFUSCATED = Style.class.getDeclaredField("field_150244_g");
			STYLE_OBFUSCATED.setAccessible(true);
			STYLE_CLICK_EVENT = Style.class.getDeclaredField("field_150251_h");
			STYLE_CLICK_EVENT.setAccessible(true);
			STYLE_HOVER_EVENT = Style.class.getDeclaredField("field_150252_i");
			STYLE_HOVER_EVENT.setAccessible(true);
			STYLE_INSERTION = Style.class.getDeclaredField("field_179990_j");
			STYLE_INSERTION.setAccessible(true);
			STYLE_FONT = Style.class.getDeclaredField("field_240710_l_");
			STYLE_FONT.setAccessible(true);
			ITEM_HOVER_ITEM = HoverEvent.ItemHover.class.getDeclaredField("field_240685_a_");
			ITEM_HOVER_ITEM.setAccessible(true);
			ITEM_HOVER_COUNT = HoverEvent.ItemHover.class.getDeclaredField("field_240686_b_");
			ITEM_HOVER_COUNT.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean supportsSeparatorInSelector() {
		return false;
	}

	public boolean supportsFontInStyle() {
		return true;
	}

	public void sendMessage(ICommandSource commandSource, ChatComponent chatComponent, ChatMessageType type, UUID uuid) {
		if (uuid == null) uuid = Util.NIL_UUID;
		ITextComponent component = forge(chatComponent, commandSource instanceof MinecraftServer);
		if (commandSource instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) commandSource).sendMessage(component, forge(type), uuid);
		} else {
			commandSource.sendMessage(component, uuid);
		}
	}

	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (uuid == null) uuid = Util.NIL_UUID;
		playerList.getServer().sendMessage(forge(component, true), uuid);
		playerList.broadcastAll(new SChatPacket(forge(component), forge(type), uuid));
	}

	public void execute(MinecraftServer server, IChatPlugin plugin, Runnable runnable) {
		if (plugin instanceof IForgeChatPlugin) {
			server.execute(runnable);
		} else {
			throw new ClassCastException("Invalid plugin");
		}
	}

	public void kickPlayer(ServerPlayerEntity player, ChatComponent reason) {
		player.connection.disconnect(forge(reason));
	}

	public static ITextComponent forge(ChatComponent raw) {
		return forge(raw, false);
	}

	public static ITextComponent forge(ChatComponent raw, boolean isConsole) {
		final IFormattableTextComponent component;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new StringTextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslationTextComponent(chatComponent.getKey(), chatComponent.getWith().stream().map((c) -> forge(c, isConsole)).toArray());
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			component = new SelectorTextComponent(chatComponent.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse ITextComponent from " + raw.getClass());
		}
		component.setStyle(forge(raw.getStyle()));
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			component.append(forge(chatComponent, isConsole));
		}
		return component;
	}

	public static ChatComponent forge(ITextComponent raw) {
		final ChatComponent chatComponent;
		if (raw == null) {
			return null;
		} else if (raw instanceof StringTextComponent) {
			StringTextComponent component = (StringTextComponent) raw;
			chatComponent = new ChatTextComponent(component.getText());
		} else if (raw instanceof TranslationTextComponent) {
			TranslationTextComponent component = (TranslationTextComponent) raw;
			chatComponent = new ChatTranslateComponent(null, component.getKey(), Arrays.stream(component.getArgs()).map((arg) -> arg instanceof ITextComponent ? forge((ITextComponent) arg) : new ChatTextComponent(String.valueOf(arg))).collect(Collectors.toList()));
		} else if (raw instanceof SelectorTextComponent) {
			SelectorTextComponent component = (SelectorTextComponent) raw;
			chatComponent = new ChatSelectorComponent(component.getPattern());
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		chatComponent.setStyle(forge(raw.getStyle()));
		for (ITextComponent component : raw.getSiblings()) {
			chatComponent.append(forge(component));
		}
		return chatComponent;
	}

	public static Style forge(ChatStyle chatStyle) {
		if (chatStyle == null) return null;
		if (chatStyle.isEmpty()) return Style.EMPTY;
		try {
			return STYLE_CONSTRUCTOR.newInstance(forge(chatStyle.getColor()), chatStyle.getBold(), chatStyle.getItalic(), chatStyle.getUnderlined(), chatStyle.getStrikethrough(), chatStyle.getObfuscated(), forge(chatStyle.getClickEvent()), forge(chatStyle.getHoverEvent()), chatStyle.getInsertion(), forge(chatStyle.getFont()));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	public static ChatStyle forge(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		try {
			builder.withColor(forge((Color) STYLE_COLOR.get(style)));
			builder.withBold((Boolean) STYLE_BOLD.get(style));
			builder.withItalic((Boolean) STYLE_ITALIC.get(style));
			builder.withUnderlined((Boolean) STYLE_UNDERLINED.get(style));
			builder.withStrikethrough((Boolean) STYLE_STRIKETHROUGH.get(style));
			builder.withObfuscated((Boolean) STYLE_OBFUSCATED.get(style));
			builder.withClickEvent(forge((ClickEvent) STYLE_CLICK_EVENT.get(style)));
			builder.withHoverEvent(forge((HoverEvent) STYLE_HOVER_EVENT.get(style)));
			builder.withInsertion((String) STYLE_INSERTION.get(style));
			builder.withFont(forge((ResourceLocation) STYLE_FONT.get(style)));
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		return builder.build();
	}

	public static ClickEvent forge(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent forge(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static HoverEvent forge(ChatHoverEvent<?> event) {
		if (event == null) return null;
		ChatHoverEvent.Action<?> action = event.getAction();
		if (action == ChatHoverEvent.Action.SHOW_TEXT) {
			return new HoverEvent(HoverEvent.Action.SHOW_TEXT, forge(event.getValue(ChatHoverEvent.Action.SHOW_TEXT)));
		} else if (action == ChatHoverEvent.Action.SHOW_ENTITY) {
			return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, forge(event.getValue(ChatHoverEvent.Action.SHOW_ENTITY)));
		} else if (action == ChatHoverEvent.Action.SHOW_ITEM) {
			return new HoverEvent(HoverEvent.Action.SHOW_ITEM, forge(event.getValue(ChatHoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	public static ChatHoverEvent<?> forge(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action<?> action = event.getAction();
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, forge(event.getValue(HoverEvent.Action.SHOW_TEXT)));
		} else if (action == HoverEvent.Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, forge(event.getValue(HoverEvent.Action.SHOW_ENTITY)));
		} else if (action == HoverEvent.Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, forge(event.getValue(HoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	@SuppressWarnings("deprecation")
	public static HoverEvent.EntityHover forge(ChatHoverEvent.ShowEntity content) {
		return content != null ? new HoverEvent.EntityHover(Registry.ENTITY_TYPE.get(forge(content.getType())), content.getUniqueId(), forge(content.getName())) : null;
	}

	@SuppressWarnings("deprecation")
	public static ChatHoverEvent.ShowEntity forge(HoverEvent.EntityHover content) {
		return content != null ? new ChatHoverEvent.ShowEntity(forge(Registry.ENTITY_TYPE.getKey(content.type)), content.id, forge(content.name)) : null;
	}

	@SuppressWarnings("deprecation")
	public static HoverEvent.ItemHover forge(ChatHoverEvent.ShowItem content) {
		return content != null ? new HoverEvent.ItemHover(new ItemStack(Registry.ITEM.get(forge(content.getItem())), content.getCount())) : null;
	}

	@SuppressWarnings("deprecation")
	public static ChatHoverEvent.ShowItem forge(HoverEvent.ItemHover content) {
		if (content == null) return null;
		final Item item;
		final int count;
		try {
			item = (Item) ITEM_HOVER_ITEM.get(content);
			count = (Integer) ITEM_HOVER_COUNT.get(content);
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		}
		return new ChatHoverEvent.ShowItem(forge(Registry.ITEM.getKey(item)), count);
	}

	public static ResourceLocation forge(ChatId id) {
		return id != null ? new ResourceLocation(id.getNamespace(), id.getValue()) : null;
	}

	public static ChatId forge(ResourceLocation resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static ChatType forge(ChatMessageType type) {
		return type != null ? ChatType.valueOf(type.name()) : null;
	}

	public static ChatMessageType forge(ChatType type) {
		return type != null ? ChatMessageType.valueOf(type.name()) : null;
	}

	public static TextFormatting forge(ChatTextFormat format) {
		return format != null ? TextFormatting.getByName(format.name()) : null;
	}

	public static ChatTextFormat forge(TextFormatting formatting) {
		return formatting != null ? ChatTextFormat.getByName(formatting.getName()) : null;
	}

	public static Color forge(ChatTextColor color) {
		return color != null ? Color.parseColor(color.toString()) : null;
	}

	public static ChatTextColor forge(Color color) {
		return color != null ? ChatTextColor.of(color.toString()) : null;
	}

	public ChatPlayer toChatPlayer(ServerPlayerEntity player) {
		return player != null ? new ForgeChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(MinecraftServer server, GameProfile gameProfile) {
		return gameProfile != null ? new ForgeChatOfflinePlayer(this, server, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(ICommandSource commandSource) {
		if (commandSource instanceof ServerPlayerEntity) {
			return this.toChatPlayer((ServerPlayerEntity) commandSource);
		}
		return commandSource != null ? new ForgeChatCommandSender(this, commandSource) : null;
	}

	public ChatTeam toChatTeam(ScorePlayerTeam team) {
		return team != null ? new ForgeChatTeam(this, team) : null;
	}

	public ChatServer toChatServer(MinecraftServer server) {
		return server != null ? new ForgeChatServer(this, server) : null;
	}

	public ChatPlayerList toChatPlayerList(PlayerList playerList) {
		return playerList != null ? new ForgeChatPlayerList(this, playerList) : null;
	}
}