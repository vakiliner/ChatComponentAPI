package vakiliner.chatcomponentapi.spigot;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;

public class SpigotParser extends BukkitParser {
	public void sendMessage(CommandSender sender, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (uuid != null) {
				player.spigot().sendMessage(spigot(type), uuid, spigot(component));
			} else {
				player.spigot().sendMessage(spigot(type), spigot(component));
			}
		} else {
			boolean isConsole = sender instanceof ConsoleCommandSender;
			if (uuid != null) {
				sender.spigot().sendMessage(uuid, spigot(component, isConsole));
			} else {
				sender.spigot().sendMessage(spigot(component, isConsole));
			}
		}
	}

	public static BaseComponent spigot(ChatComponent raw) {
		return spigot(raw, false);
	}

	public static BaseComponent spigot(ChatComponent raw, boolean isConsole) {
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
			component = new TranslatableComponent(chatComponent.getKey(), chatComponent.getWith().stream().map((c) -> spigot(c, isConsole)).toArray());
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			component = new SelectorComponent(chatComponent.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse BaseComponent from " + raw.getClass());
		}
		component.setColor(spigot(raw.getColorRaw()));
		component.setBold(raw.isBoldRaw());
		component.setItalic(raw.isItalicRaw());
		component.setUnderlined(raw.isUnderlinedRaw());
		component.setStrikethrough(raw.isStrikethroughRaw());
		component.setObfuscated(raw.isObfuscatedRaw());
		component.setClickEvent(spigot(raw.getClickEvent()));
		component.setHoverEvent(spigot(raw.getHoverEvent()));
		List<ChatComponent> children = raw.getExtra();
		if (children != null) {
			component.setExtra(children.stream().map((c) -> spigot(c, isConsole)).collect(Collectors.toList()));
		}
		return component;
	}

	public static ChatComponent spigot(BaseComponent raw) {
		final ChatComponent chatComponent;
		if (raw == null) {
			return null;
		} else if (raw instanceof TextComponent) {
			TextComponent component = (TextComponent) raw;
			chatComponent = new ChatTextComponent(component.getText());
		} else if (raw instanceof TranslatableComponent) {
			TranslatableComponent component = (TranslatableComponent) raw;
			chatComponent = new ChatTranslateComponent(null, component.getTranslate(), component.getWith().stream().map(SpigotParser::spigot).collect(Collectors.toList()));
		} else if (raw instanceof SelectorComponent) {
			SelectorComponent component = (SelectorComponent) raw;
			chatComponent = new ChatSelectorComponent(component.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		chatComponent.setColor(spigot(raw.getColorRaw()));
		chatComponent.setBold(raw.isBoldRaw());
		chatComponent.setItalic(raw.isItalicRaw());
		chatComponent.setUnderlined(raw.isUnderlinedRaw());
		chatComponent.setStrikethrough(raw.isStrikethroughRaw());
		chatComponent.setObfuscated(raw.isObfuscatedRaw());
		chatComponent.setClickEvent(spigot(raw.getClickEvent()));
		chatComponent.setHoverEvent(spigot(raw.getHoverEvent()));
		List<BaseComponent> extra = raw.getExtra();
		if (extra != null) {
			chatComponent.setExtra(extra.stream().map(SpigotParser::spigot).collect(Collectors.toList()));
		}
		return chatComponent;
	}

	public static ClickEvent spigot(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.valueOf(event.getAction().name()), event.getValue()) : null;
	}

	public static ChatClickEvent spigot(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().name().toLowerCase()), event.getValue()) : null;
	}

	public static HoverEvent spigot(ChatHoverEvent<?> event) {
		return event != null ? new HoverEvent(HoverEvent.Action.valueOf(event.getAction().getName().toUpperCase()), spigotContent(event.getContents())) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> ChatHoverEvent<V> spigot(HoverEvent event) {
		return event != null ? new ChatHoverEvent<>((ChatHoverEvent.Action<V>) ChatHoverEvent.Action.getByName(event.getAction().name().toLowerCase()), (V) spigotContent2(event.getContents().get(0))) : null;
	}

	public static Content spigotContent(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatComponent) {
			ChatComponent content = (ChatComponent) raw;
			return new Text(new BaseComponent[] { SpigotParser.spigot(content) });
		} else if (raw instanceof ChatHoverEvent.ShowEntity) {
			ChatHoverEvent.ShowEntity content = (ChatHoverEvent.ShowEntity) raw;
			return new Entity(content.getType().toString(), content.getUniqueId().toString(), SpigotParser.spigot(content.getName()));
		} else if (raw instanceof ChatHoverEvent.ShowItem) {
			ChatHoverEvent.ShowItem content = (ChatHoverEvent.ShowItem) raw;
			return new Item(content.getItem().toString(), content.getCount(), null);
		} else {
			throw new IllegalArgumentException("Could not parse Content from " + raw.getClass());
		}
	}

	public static Object spigotContent2(Content raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof Text) {
			Text content = (Text) raw;
			Object value = content.getValue();
			if (value instanceof String) {
				return new ChatTextComponent((String) value);
			} else if (value instanceof BaseComponent[]) {
				return SpigotParser.spigot(((BaseComponent[]) value)[0]);
			} else {
				throw new IllegalArgumentException("Could not parse ChatTextContent from " + value.getClass());
			}
		} else if (raw instanceof Entity) {
			Entity content = (Entity) raw;
			return new ChatHoverEvent.ShowEntity(ChatId.parse(content.getType()), UUID.fromString(content.getId()), SpigotParser.spigot(content.getName()));
		} else if (raw instanceof Item) {
			Item content = (Item) raw;
			return new ChatHoverEvent.ShowItem(ChatId.parse(content.getId()), content.getCount());
		} else {
			throw new IllegalArgumentException("Could not parse ChatContent from " + raw.getClass());
		}
	}

	public static net.md_5.bungee.api.ChatMessageType spigot(ChatMessageType type) {
		return type != null ? net.md_5.bungee.api.ChatMessageType.valueOf(type.name()) : null;
	}

	public static ChatMessageType spigot(net.md_5.bungee.api.ChatMessageType type) {
		return type != null ? ChatMessageType.valueOf(type.name()) : null;
	}

	public static ChatColor spigot(ChatTextColor color) {
		return color != null ? ChatColor.of(color.toString()) : null;
	}

	public static ChatTextColor spigot(ChatColor color) {
		return color != null ? ChatTextColor.of(color.getName()) : null;
	}
}