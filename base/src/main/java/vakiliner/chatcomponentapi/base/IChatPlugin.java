package vakiliner.chatcomponentapi.base;

public interface IChatPlugin {
	default boolean isEnabled() {
		return true;
	}
}