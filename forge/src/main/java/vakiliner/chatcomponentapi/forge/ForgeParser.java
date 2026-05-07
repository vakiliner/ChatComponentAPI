package vakiliner.chatcomponentapi.forge;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatType;
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
import vakiliner.chatcomponentapi.forge.mixin.StyleAccessor;

public class ForgeParser extends BaseParser {
	public boolean supportsSeparatorInSelector() {
		return false;
	}

	public boolean supportsFontInStyle() {
		return false;
	}

	public void sendMessage(ICommandSource commandSource, ChatComponent chatComponent, ChatMessageType type, UUID uuid) {
		ITextComponent component = forge(chatComponent, commandSource instanceof MinecraftServer);
		if (commandSource instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) commandSource).sendMessage(component, forge(type));
		} else {
			commandSource.sendMessage(component);
		}
	}

	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid) {
		playerList.getServer().sendMessage(forge(component, true));
		playerList.broadcastAll(new SChatPacket(forge(component), forge(type)));
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
		final ITextComponent component;
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
		Style style = new Style();
		if (chatStyle.isEmpty()) return style;
		style.setColor(forge(ChatTextFormat.getFromColor(chatStyle.getColor(), null)));
		style.setBold(chatStyle.getBold());
		style.setItalic(chatStyle.getItalic());
		style.setUnderlined(chatStyle.getUnderlined());
		style.setStrikethrough(chatStyle.getStrikethrough());
		style.setObfuscated(chatStyle.getObfuscated());
		style.setClickEvent(forge(chatStyle.getClickEvent()));
		style.setHoverEvent(forge(chatStyle.getHoverEvent()));
		style.setInsertion(chatStyle.getInsertion());
		return style;
	}

	public static ChatStyle forge(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		StyleAccessor accessor = (StyleAccessor) style;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(ChatNamedColor.getByFormat(forge(accessor.getColor())));
		builder.withBold(accessor.getBold());
		builder.withItalic(accessor.getItalic());
		builder.withUnderlined(accessor.getUnderlined());
		builder.withStrikethrough(accessor.getStrikethrough());
		builder.withObfuscated(accessor.getObfuscated());
		builder.withClickEvent(forge(accessor.getClickEvent()));
		builder.withHoverEvent(forge(accessor.getHoverEvent()));
		builder.withInsertion(accessor.getInsertion());
		return builder.build();
	}

	public static ClickEvent forge(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent forge(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	@SuppressWarnings("deprecation")
	public static HoverEvent forge(ChatHoverEvent<?> event) {
		return event != null ? new HoverEvent(HoverEvent.Action.getByName(event.getAction().getName()), forge(event.getValue())) : null;
	}

	public static ChatHoverEvent<?> forge(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action action = event.getAction();
		ITextComponent contents = event.getValue();
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, forge(contents));
		}
		JsonElement value = new Gson().fromJson(((StringTextComponent) contents).getText(), JsonElement.class);
		switch (action) {
			case SHOW_ENTITY: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, ChatHoverEvent.ShowEntity.deserialize(value, true));
			case SHOW_ITEM: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, ChatHoverEvent.ShowItem.deserialize(value));
			default: throw new IllegalArgumentException("Unknown action");
		}
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