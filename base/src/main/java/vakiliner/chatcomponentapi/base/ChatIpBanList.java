package vakiliner.chatcomponentapi.base;

public interface ChatIpBanList extends ChatBanList<String> {
	boolean isBanned(String ip);
}