package vakiliner.chatcomponentapi.base;

import java.util.Date;

public interface ChatBanEntry extends ChatStoredUserEntry {
	String getSource();

	Date getExpires();

	String getReason();
}