package vakiliner.chatcomponentapi.common;

import java.util.UUID;
import com.mojang.authlib.GameProfile;

public final class ChatNameAndId {
	private final UUID id;
	private final String name;

	public ChatNameAndId(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

	public ChatNameAndId(GameProfile gameProfile) {
		this(gameProfile.getId(), gameProfile.getName());
	}

	public UUID id() {
		return this.id;
	}

	public String name() {
		return this.name;
	}

	public GameProfile toGameProfile() {
		return new GameProfile(this.id, this.name);
	}
}
