package vakiliner.chatcomponentapi.component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import com.google.common.collect.Maps;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.gson.APIGson;

public class ChatHoverEvent<V> {
	private final Action<V> action;
	private final V contents;

	public ChatHoverEvent(Action<V> action, V contents) {
		this.action = Objects.requireNonNull(action);
		this.contents = Objects.requireNonNull(contents);
	}

	public ChatHoverEvent(ChatHoverEvent<V> event) {
		this.action = event.action;
		this.contents = event.contents;
	}

	public ChatHoverEvent<V> clone() {
		return new ChatHoverEvent<>(this);
	}

	public Action<V> getAction() {
		return this.action;
	}

	public V getContents() {
		return this.contents;
	}

	@Deprecated
	public ChatComponent getValue() {
		if (this.action == Action.SHOW_TEXT) {
			return (ChatComponent) this.contents;
		} else {
			return new ChatTextComponent(APIGson.builder(true).create().toJson(this.contents));
		}
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatHoverEvent)) {
			return false;
		} else {
			ChatHoverEvent<?> other = (ChatHoverEvent<?>) obj;
			return this.action == other.action && this.contents.equals(other.contents);
		}
	}

	public static class Action<V> {
		private static final Map<String, Action<?>> BY_NAME = Maps.newHashMap();
		public static final Action<ChatComponent> SHOW_TEXT = new Action<>("show_text", ChatComponent.class);
		public static final Action<ShowEntity> SHOW_ENTITY = new Action<>("show_entity", ShowEntity.class);
		public static final Action<ShowItem> SHOW_ITEM = new Action<>("show_item", ShowItem.class);
		private final String name;
		private final Class<V> type;

		private Action(String name, Class<V> type) {
			this.name = name;
			this.type = type;
			BY_NAME.put(this.name, this);
		}

		public String getName() {
			return this.name;
		}

		public Class<V> getType() {
			return this.type;
		}

		public static Action<?> getByName(String name) {
			return BY_NAME.get(name);
		}
	}

	public static class ShowEntity {
		private final ChatId type;
		private final UUID uuid;
		private final ChatComponent name;

		public ShowEntity(ChatId type, UUID uuid, ChatComponent name) {
			this.type = Objects.requireNonNull(type);
			this.uuid = Objects.requireNonNull(uuid);
			this.name = name;
		}

		public ChatId getType() {
			return this.type;
		}

		public UUID getUniqueId() {
			return this.uuid;
		}

		public ChatComponent getName() {
			return this.name;
		}

		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			} else if (obj instanceof ShowEntity) {
				ShowEntity other = (ShowEntity) obj;
				return this.type.equals(other.type) && this.uuid.equals(other.uuid) && Objects.equals(this.name, other.name);
			} else {
				return false;
			}
		}
	}

	public static class ShowItem {
		private final ChatId item;
		private final int count;

		public ShowItem(ChatId item, int count) {
			this.item = Objects.requireNonNull(item);
			this.count = count;
		}

		public ChatId getItem() {
			return this.item;
		}

		public int getCount() {
			return this.count;
		}

		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			} else if (obj instanceof ShowItem) {
				ShowItem other = (ShowItem) obj;
				return this.item.equals(other.item) && this.count == other.count;
			} else {
				return false;
			}
		}
	}
}