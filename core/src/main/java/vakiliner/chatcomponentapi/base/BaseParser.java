package vakiliner.chatcomponentapi.base;

import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.common.ChatNameAndId;

public abstract class BaseParser {
	public abstract boolean supportsSeparatorInSelector();

	public abstract boolean supportsFontInStyle();

	public static ChatNameAndId base(GameProfile gameProfile) {
		return gameProfile != null ? new ChatNameAndId(gameProfile) : null;
	}

	public static GameProfile base(ChatNameAndId nameAndId) {
		return nameAndId != null ? nameAndId.toGameProfile() : null;
	}
}
