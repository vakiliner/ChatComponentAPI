package vakiliner.chatcomponentapi.fabric;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.PlayerTeam;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
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
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.fabric.mixin.StyleAccessor;

public class FabricParser extends BaseParser {
	public boolean supportsSeparatorInSelector() {
		return false;
	}

	public boolean supportsFontInStyle() {
		return false;
	}

	public void sendMessage(CommandSource commandSource, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (commandSource instanceof ServerPlayer) {
			((ServerPlayer) commandSource).sendMessage(fabric(component), fabric(type));
		} else {
			commandSource.sendMessage(fabric(component, commandSource instanceof MinecraftServer));
		}
	}

	@Deprecated
	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid) {
		playerList.broadcastMessage(fabric(component), type == ChatMessageType.SYSTEM);
	}

	public void execute(MinecraftServer server, IChatPlugin plugin, Runnable runnable) {
		if (plugin instanceof IFabricChatPlugin) {
			server.execute(runnable);
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
		final Component component;
		if (raw instanceof ChatComponentModified) {
			if (isConsole && raw instanceof ChatComponentWithLegacyText) {
				raw = ((ChatComponentWithLegacyText) raw).getLegacyComponent();
			} else {
				raw = ((ChatComponentModified) raw).getComponent();
			}
		}
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
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
		final ChatComponent chatComponent;
		if (raw == null) {
			return null;
		} else if (raw instanceof TextComponent) {
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
		chatComponent.setExtra(raw.getSiblings().stream().map(FabricParser::fabric).collect(Collectors.toList()));
		return chatComponent;
	}

	public static Style fabric(ChatStyle chatStyle) {
		if (chatStyle == null) return null;
		Style style = new Style();
		if (chatStyle.isEmpty()) return style;
		return style;
	}

	public static ChatStyle fabric(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		StyleAccessor accessor = (StyleAccessor) style;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(ChatNamedColor.getByFormat(fabric(accessor.getColor())));
		builder.withBold(accessor.getBold());
		builder.withItalic(accessor.getItalic());
		builder.withUnderlined(accessor.getUnderlined());
		builder.withStrikethrough(accessor.getStrikethrough());
		builder.withObfuscated(accessor.getObfuscated());
		builder.withClickEvent(fabric(accessor.getClickEvent()));
		builder.withHoverEvent(fabric(accessor.getHoverEvent()));
		builder.withInsertion(accessor.getInsertion());
		return builder.build();
	}

	public static ClickEvent fabric(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent fabric(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	@SuppressWarnings("deprecation")
	public static HoverEvent fabric(ChatHoverEvent<?> event) {
		return event != null ? new HoverEvent(HoverEvent.Action.getByName(event.getAction().getName()), fabric(event.getValue())) : null;
	}

	public static ChatHoverEvent<?> fabric(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action action = event.getAction();
		Component contents = event.getValue();
		if (action == Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, fabric(contents));
		}
		JsonElement value = new Gson().fromJson(((TextComponent) contents).getText(), JsonElement.class);
		switch (action) {
			case SHOW_ENTITY: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, ChatHoverEvent.ShowEntity.deserialize(value, true));
			case SHOW_ITEM: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, ChatHoverEvent.ShowItem.deserialize(value));
			default: throw new RuntimeException("Unknown action");
		}
	}

	public static ResourceLocation fabric(ChatId id) {
		return id != null ? new ResourceLocation(id.getNamespace(), id.getValue()) : null;
	}

	public static ChatId fabric(ResourceLocation resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static ChatType fabric(ChatMessageType type) {
		return type != null ? ChatType.valueOf(type.name()) : null;
	}

	public static ChatMessageType fabric(ChatType type) {
		return type != null ? ChatMessageType.valueOf(type.name()) : null;
	}

	public static ChatFormatting fabric(ChatTextFormat format) {
		return format != null ? ChatFormatting.getByName(format.name()) : null;
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
		if (commandSource instanceof ServerPlayer) {
			return this.toChatPlayer((ServerPlayer) commandSource);
		}
		return commandSource != null ? new FabricChatCommandSender(this, commandSource) : null;
	}

	public ChatTeam toChatTeam(PlayerTeam team) {
		return team != null ? new FabricChatTeam(this, team) : null;
	}

	public ChatServer toChatServer(MinecraftServer server) {
		return server != null ? new FabricChatServer(this, server) : null;
	}
}