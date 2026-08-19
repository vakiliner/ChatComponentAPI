package vakiliner.chatcomponentapi.forge;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import vakiliner.chatcomponentapi.forge.mixin.ItemHoverAccessor;
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
		this.broadcastMessage(playerList, component, type, uuid, null);
	}

	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid, Predicate<? super ChatPlayer> predicate) {
		if (uuid == null) uuid = Util.NIL_UUID;
		SChatPacket packet = new SChatPacket(forge(component), forge(type), uuid);
		playerList.getServer().sendMessage(forge(component, true), uuid);
		if (predicate == null) {
			playerList.broadcastAll(packet);
		} else for (ServerPlayerEntity player : playerList.getPlayers()) {
			if (predicate.test(this.toChatPlayer(player))) {
				player.connection.send(packet);
			}
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

	public void kickPlayer(ServerPlayerEntity player, ChatComponent reason) {
		player.connection.disconnect(forge(reason));
	}

	public static ITextComponent forge(ChatComponent raw) {
		return forge(raw, false);
	}

	public static ITextComponent forge(ChatComponent raw, boolean isConsole) {
		if (raw == null) return null;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		final IFormattableTextComponent component;
		if (raw instanceof ChatTextComponent) {
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
		if (raw == null) return null;
		final ChatComponent chatComponent;
		if (raw instanceof StringTextComponent) {
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
		return StyleAccessor.newStyle(forge(chatStyle.getColor()), chatStyle.getBold(), chatStyle.getItalic(), chatStyle.getUnderlined(), chatStyle.getStrikethrough(), chatStyle.getObfuscated(), forge(chatStyle.getClickEvent()), forge(chatStyle.getHoverEvent()), chatStyle.getInsertion(), forge(chatStyle.getFont()));
	}

	public static ChatStyle forge(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		StyleAccessor accessor = (StyleAccessor) style;
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
		builder.withFont(forge(accessor.$getFont()));
		return builder.build();
	}

	public static ClickEvent forge(ChatClickEvent event) {
		if (event == null) return null;
		switch (event.action()) {
			case OPEN_URL: return new ClickEvent(ClickEvent.Action.OPEN_URL, event.value());
			case OPEN_FILE: return new ClickEvent(ClickEvent.Action.OPEN_FILE, event.value());
			case RUN_COMMAND: return new ClickEvent(ClickEvent.Action.RUN_COMMAND, event.value());
			case SUGGEST_COMMAND: return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, event.value());
			case CHANGE_PAGE: return new ClickEvent(ClickEvent.Action.CHANGE_PAGE, event.value());
			case COPY_TO_CLIPBOARD: return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, event.value());
			default: throw new IllegalArgumentException("Unknown ChatClickEvent.Action " + event.action());
		}
	}

	public static ChatClickEvent forge(ClickEvent event) {
		if (event == null) return null;
		switch (event.getAction()) {
			case OPEN_URL: return new ChatClickEvent(ChatClickEvent.Action.OPEN_URL, event.getValue());
			case OPEN_FILE: return new ChatClickEvent(ChatClickEvent.Action.OPEN_FILE, event.getValue());
			case RUN_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, event.getValue());
			case SUGGEST_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, event.getValue());
			case CHANGE_PAGE: return new ChatClickEvent(ChatClickEvent.Action.CHANGE_PAGE, event.getValue());
			case COPY_TO_CLIPBOARD: return new ChatClickEvent(ChatClickEvent.Action.COPY_TO_CLIPBOARD, event.getValue());
			default: throw new IllegalArgumentException("Unknown ClickEvent.Action " + event.getAction());
		}
	}

	public static HoverEvent forge(ChatHoverEvent<?> event) {
		if (event == null) return null;
		ChatHoverEvent.Action<?> action = event.action();
		if (action == ChatHoverEvent.Action.SHOW_TEXT) {
			return new HoverEvent(HoverEvent.Action.SHOW_TEXT, forge(event.getValue(ChatHoverEvent.Action.SHOW_TEXT)));
		} else if (action == ChatHoverEvent.Action.SHOW_ENTITY) {
			return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, forge(event.getValue(ChatHoverEvent.Action.SHOW_ENTITY)));
		} else if (action == ChatHoverEvent.Action.SHOW_ITEM) {
			return new HoverEvent(HoverEvent.Action.SHOW_ITEM, forge(event.getValue(ChatHoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown ChatHoverEvent.Action" + action);
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
			throw new IllegalArgumentException("Unknown HoverEvent.Action " + action);
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
		ItemHoverAccessor accessor = (ItemHoverAccessor) content;
		return new ChatHoverEvent.ShowItem(forge(Registry.ITEM.getKey(accessor.getItem())), accessor.getCount());
	}

	public static ResourceLocation forge(ChatId id) {
		return id != null ? new ResourceLocation(id.namespace(), id.value()) : null;
	}

	public static ChatId forge(ResourceLocation resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static ChatType forge(ChatMessageType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return ChatType.CHAT;
			case SYSTEM: return ChatType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown ChatMessageType " + type);
		}
	}

	public static ChatMessageType forge(ChatType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return ChatMessageType.CHAT;
			case SYSTEM: return ChatMessageType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown ChatType " + type);
		}
	}

	public static TextFormatting forge(ChatTextFormat format) {
		return format != null ? TextFormatting.getByName(format.getName()) : null;
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
		if (commandSource == null) return null;
		if (commandSource instanceof ServerPlayerEntity) {
			return this.toChatPlayer((ServerPlayerEntity) commandSource);
		} else if (commandSource instanceof MinecraftServer) {
			return this.toChatServer((MinecraftServer) commandSource);
		}
		return new ForgeChatCommandSender(this, commandSource);
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