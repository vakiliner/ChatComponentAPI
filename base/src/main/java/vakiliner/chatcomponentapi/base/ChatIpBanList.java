package vakiliner.chatcomponentapi.base;

public interface ChatIpBanList extends ChatStoredUserList<String, ChatBanEntry> {
	boolean isBanned(String ip);
}