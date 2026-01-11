package vakiliner.chatcomponentapi.fabric;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentFormat;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.fabric.mixin.StyleMixin;

public class FabricParser extends BaseParser {
	public boolean supportsSeparatorInSelector() {
		return false;
	}

	public void sendMessage(CommandSource commandSource, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (uuid == null) uuid = Util.NIL_UUID;
		if (commandSource instanceof ServerPlayer) {
			((ServerPlayer) commandSource).sendMessage(fabric(component), fabric(type), uuid);
		} else {
			commandSource.sendMessage(fabric(component, commandSource instanceof MinecraftServer), uuid);
		}
	}

	@Deprecated
	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid) {
		playerList.broadcastMessage(fabric(component), fabric(type), uuid);
	}

	public static Component fabric(ChatComponent raw) {
		return fabric(raw, false);
	}

	public static Component fabric(ChatComponent raw, boolean isConsole) {
		final BaseComponent component;
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
		return chatStyle != null ? StyleMixin.newStyle(fabric(chatStyle.getColor()), chatStyle.getBold(), chatStyle.getItalic(), chatStyle.getUnderlined(), chatStyle.getStrikethrough(), chatStyle.getObfuscated(), fabric(chatStyle.getClickEvent()), fabric(chatStyle.getHoverEvent()), chatStyle.getInsertion(), fabric(chatStyle.getFont())) : null;
	}

	public static ChatStyle fabric(Style style) {
		if (style == null) return null;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(fabric(style.getColor()));
		builder.withBold(((StyleMixin) style).getBold());
		builder.withItalic(((StyleMixin) style).getItalic());
		builder.withUnderlined(((StyleMixin) style).getUnderlined());
		builder.withStrikethrough(((StyleMixin) style).getStrikethrough());
		builder.withObfuscated(((StyleMixin) style).getObfuscated());
		builder.withClickEvent(fabric(style.getClickEvent()));
		builder.withInsertion(style.getInsertion());
		builder.withHoverEvent(fabric(style.getHoverEvent()));
		builder.withFont(fabric(((StyleMixin) style).getFont()));
		return builder.build();
	}

	public static ClickEvent fabric(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent fabric(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	@SuppressWarnings("unchecked")
	public static HoverEvent fabric(ChatHoverEvent<?> event) {
		return event != null ? new HoverEvent(HoverEvent.Action.getByName(event.getAction().getName()), fabricContent(event.getContents())) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> ChatHoverEvent<V> fabric(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action<?> action = event.getAction();
		return new ChatHoverEvent<>((ChatHoverEvent.Action<V>) ChatHoverEvent.Action.getByName(action.getName()), (V) fabricContent2(event.getValue(action)));
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

	@Deprecated
	public static Style fabricStyle(ChatComponent component) {
		Objects.requireNonNull(component);
		Style style = Style.EMPTY;
		TextColor color = fabric(component.getColorRaw());
		if (color != null) {
			style = style.withColor(color);
		}
		for (Map.Entry<ChatComponentFormat, Boolean> entry : component.getFormatsRaw().entrySet()) {
			Boolean isSetted = entry.getValue();
			if (isSetted != null && isSetted) {
				style = style.applyFormat(FabricParser.fabric(entry.getKey().asTextFormat()));
			}
		}
		ChatClickEvent clickEvent = component.getClickEvent();
		if (clickEvent != null) style = style.withClickEvent(FabricParser.fabric(clickEvent));
		ChatHoverEvent<?> hoverEvent = component.getHoverEvent();
		if (hoverEvent != null) style = style.withHoverEvent(FabricParser.fabric(hoverEvent));
		return style;
	}

	public static ChatFormatting fabric(ChatTextFormat format) {
		return format != null ? ChatFormatting.getByName(format.name()) : null;
	}

	public static ChatTextFormat fabric(ChatFormatting formatting) {
		return formatting != null ? ChatTextFormat.getByName(formatting.getName()) : null;
	}

	public static Object fabricContent(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatComponent) {
			ChatComponent content = (ChatComponent) raw;
			return FabricParser.fabric(content);
		} else if (raw instanceof ChatHoverEvent.ShowEntity) {
			ChatHoverEvent.ShowEntity content = (ChatHoverEvent.ShowEntity) raw;
			return new HoverEvent.EntityTooltipInfo(Registry.ENTITY_TYPE.get(FabricParser.fabric(content.getType())), content.getUniqueId(), FabricParser.fabric(content.getName()));
		} else if (raw instanceof ChatHoverEvent.ShowItem) {
			ChatHoverEvent.ShowItem content = (ChatHoverEvent.ShowItem) raw;
			return new HoverEvent.ItemStackInfo(new ItemStack(Registry.ITEM.get(FabricParser.fabric(content.getItem())), content.getCount()));
		} else {
			throw new IllegalArgumentException("Could not parse Content from " + raw.getClass());
		}
	}

	public static Object fabricContent2(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof Component) {
			Component content = (Component) raw;
			return FabricParser.fabric(content);
		} else if (raw instanceof HoverEvent.EntityTooltipInfo) {
			HoverEvent.EntityTooltipInfo content = (HoverEvent.EntityTooltipInfo) raw;
			return new ChatHoverEvent.ShowEntity(FabricParser.fabric(Registry.ENTITY_TYPE.getKey(content.type)), content.id, FabricParser.fabric(content.name));
		} else if (raw instanceof HoverEvent.ItemStackInfo) {
			HoverEvent.ItemStackInfo content = (HoverEvent.ItemStackInfo) raw;
			ItemStack itemStack = content.getItemStack();
			return new ChatHoverEvent.ShowItem(FabricParser.fabric(Registry.ITEM.getKey(itemStack.getItem())), itemStack.getCount());
		} else {
			throw new IllegalArgumentException("Could not parse ChatContent from " + raw.getClass());
		}
	}

	public static TextColor fabric(ChatTextColor color) {
		return color != null ? TextColor.parseColor(color.toString()) : null;
	}

	public static ChatTextColor fabric(TextColor color) {
		return color != null ? ChatTextColor.of(color.toString()) : null;
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
}