package vakiliner.chatcomponentapi.base;

import java.util.Date;

public interface ChatBanList<Key> extends ChatStoredUserList<Key, ChatBanEntry> {
	ChatBanEntry add(Key key, String reason, String source, Date expires);
}