package vakiliner.chatcomponentapi.base;

public interface ChatServer {
	ChatPlayerList getPlayerList();

	public void execute(IChatPlugin plugin, Runnable runnable);
}