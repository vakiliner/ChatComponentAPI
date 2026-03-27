package vakiliner.chatcomponentapi.base;

public interface ChatServer {
	public void execute(IChatPlugin plugin, Runnable runnable);
}