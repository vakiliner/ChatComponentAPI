package vakiliner.chatcomponentapi.fabric;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
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
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class FabricParser extends BaseParser {
	@Override
	public boolean supportsSeparatorInSelector() {
		return false;
	}

	@Override
	public boolean supportsFontInStyle() {
		return false;
	}

	public void sendMessage(CommandSource commandSource, ChatComponent chatComponent, ChatMessageType type, UUID uuid) {
		Component component = fabric(chatComponent, commandSource instanceof MinecraftServer);
		if (commandSource instanceof ServerPlayer) {
			((ServerPlayer) commandSource).sendMessage(component, fabric(type));
		} else {
			commandSource.sendMessage(component);
		}
	}

	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid) {
		this.broadcastMessage(playerList, component, type, uuid, null);
	}

	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid, Predicate<? super ChatPlayer> predicate) {
		ClientboundChatPacket packet = new ClientboundChatPacket(fabric(component), fabric(type));
		playerList.getServer().sendMessage(fabric(component, true));
		if (predicate == null) {
			playerList.broadcastAll(packet);
		} else for (ServerPlayer player : playerList.getPlayers()) {
			if (predicate.test(this.toChatPlayer(player))) {
				player.connection.send(packet);
			}
		}
	}

	@Deprecated
	public void execute(MinecraftServer server, IChatPlugin raw, Runnable runnable) {
		if (raw instanceof IFabricChatPlugin) {
			@SuppressWarnings("unused")
			IFabricChatPlugin chatPlugin = (IFabricChatPlugin) raw;
			server.execute(runnable);
		} else {
			throw new ClassCastException("Invalid plugin");
		}
	}

	@Deprecated
	public void executeBlocking(MinecraftServer server, IChatPlugin raw, Runnable runnable) {
		if (raw instanceof IFabricChatPlugin) {
			@SuppressWarnings("unused")
			IFabricChatPlugin chatPlugin = (IFabricChatPlugin) raw;
			server.executeBlocking(runnable);
		} else {
			throw new ClassCastException("Invalid plugin");
		}
	}

	public void kickPlayer(ServerPlayer player, ChatComponent reason) {
		player.connection.disconnect(fabric(reason));
	}

	public static Component fabric(ChatComponent raw) {
		return fabric(raw, false);
	}

	public static Component fabric(ChatComponent raw, boolean isConsole) {
		if (raw == null) return null;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		final Component component;
		if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new TextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslatableComponent(chatComponent.getKey(), chatComponent.getWith().stream().map((c) -> fabric(c, isConsole)).toArray());
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			component = new SelectorComponent(chatComponent.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse Component from " + raw.getClass());
		}
		component.setStyle(fabric(raw.getStyle()));
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			component.append(fabric(chatComponent, isConsole));
		}
		return component;
	}

	public static ChatComponent fabric(Component raw) {
		if (raw == null) return null;
		final ChatComponent chatComponent;
		if (raw instanceof TextComponent) {
			TextComponent component = (TextComponent) raw;
			chatComponent = new ChatTextComponent(component.getText());
		} else if (raw instanceof TranslatableComponent) {
			TranslatableComponent component = (TranslatableComponent) raw;
			chatComponent = new ChatTranslateComponent(null, component.getKey(), Arrays.stream(component.getArgs()).map((arg) -> arg instanceof Component ? fabric((Component) arg) : new ChatTextComponent(String.valueOf(arg))).collect(Collectors.toList()));
		} else if (raw instanceof SelectorComponent) {
			SelectorComponent component = (SelectorComponent) raw;
			chatComponent = new ChatSelectorComponent(component.getPattern());
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		chatComponent.setStyle(fabric(raw.getStyle()));
		for (Component component : raw.getSiblings()) {
			chatComponent.append(fabric(component));
		}
		return chatComponent;
	}

	public static Style fabric(ChatStyle chatStyle) {
		if (chatStyle == null) return null;
		Style style = new Style();
		if (chatStyle.isEmpty()) return style;
		style.setColor(fabric(ChatTextFormat.getFromColor(chatStyle.getColor(), null)));
		style.setBold(chatStyle.getBold());
		style.setItalic(chatStyle.getItalic());
		style.setUnderlined(chatStyle.getUnderlined());
		style.setStrikethrough(chatStyle.getStrikethrough());
		style.setObfuscated(chatStyle.getObfuscated());
		style.setClickEvent(fabric(chatStyle.getClickEvent()));
		style.setHoverEvent(fabric(chatStyle.getHoverEvent()));
		style.setInsertion(chatStyle.getInsertion());
		return style;
	}

	public static ChatStyle fabric(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(ChatNamedColor.getByFormat(fabric(style.color)));
		builder.withBold(style.bold);
		builder.withItalic(style.italic);
		builder.withUnderlined(style.underlined);
		builder.withStrikethrough(style.strikethrough);
		builder.withObfuscated(style.obfuscated);
		builder.withClickEvent(fabric(style.clickEvent));
		builder.withHoverEvent(fabric(style.hoverEvent));
		builder.withInsertion(style.insertion);
		return builder.build();
	}

	public static ClickEvent fabric(ChatClickEvent event) {
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

	public static ChatClickEvent fabric(ClickEvent event) {
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

	@SuppressWarnings("deprecation")
	public static HoverEvent fabric(ChatHoverEvent<?> event) {
		if (event == null) return null;
		ChatHoverEvent.Action<?> action = event.action();
		if (action == ChatHoverEvent.Action.SHOW_TEXT) {
			return new HoverEvent(HoverEvent.Action.SHOW_TEXT, fabric(event.getValue()));
		} else if (action == ChatHoverEvent.Action.SHOW_ENTITY) {
			return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, fabric(event.getValue()));
		} else if (action == ChatHoverEvent.Action.SHOW_ITEM) {
			return new HoverEvent(HoverEvent.Action.SHOW_ITEM, fabric(event.getValue()));
		} else {
			throw new IllegalArgumentException("Unknown ChatHoverEvent.Action " + action);
		}
	}

	public static ChatHoverEvent<?> fabric(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action action = event.getAction();
		Component contents = event.getValue();
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, fabric(contents));
		}
		JsonElement value = new Gson().fromJson(contents.getContents(), JsonElement.class);
		switch (action) {
			case SHOW_ENTITY: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, ChatHoverEvent.ShowEntity.deserialize(value, true));
			case SHOW_ITEM: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, ChatHoverEvent.ShowItem.deserialize(value));
			default: throw new IllegalArgumentException("Unknown action");
		}
	}

	public static ResourceLocation fabric(ChatId id) {
		return id != null ? new ResourceLocation(id.namespace(), id.value()) : null;
	}

	public static ChatId fabric(ResourceLocation resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static ChatType fabric(ChatMessageType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return ChatType.CHAT;
			case SYSTEM: return ChatType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown ChatMessageType " + type);
		}
	}

	public static ChatMessageType fabric(ChatType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return ChatMessageType.CHAT;
			case SYSTEM: return ChatMessageType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown ChatType " + type);
		}
	}

	public static ChatFormatting fabric(ChatTextFormat format) {
		return format != null ? ChatFormatting.getByName(format.getName()) : null;
	}

	public static ChatTextFormat fabric(ChatFormatting formatting) {
		return formatting != null ? ChatTextFormat.getByName(formatting.getName()) : null;
	}

	public ChatPlayer toChatPlayer(ServerPlayer player) {
		return player != null ? new FabricChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(MinecraftServer server, GameProfile gameProfile) {
		return gameProfile != null ? new FabricChatOfflinePlayer(this, server, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSource commandSource) {
		if (commandSource == null) return null;
		if (commandSource instanceof ServerPlayer) {
			return this.toChatPlayer((ServerPlayer) commandSource);
		} else if (commandSource instanceof MinecraftServer) {
			return this.toChatServer((MinecraftServer) commandSource);
		}
		return new FabricChatCommandSender(this, commandSource);
	}

	public ChatTeam toChatTeam(PlayerTeam team) {
		return team != null ? new FabricChatTeam(this, team) : null;
	}

	public ChatServer toChatServer(MinecraftServer server) {
		return server != null ? new FabricChatServer(this, server) : null;
	}

	public ChatPlayerList toChatPlayerList(PlayerList playerList) {
		return playerList != null ? new FabricChatPlayerList(this, playerList) : null;
	}
}
