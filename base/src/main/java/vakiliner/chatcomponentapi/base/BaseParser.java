package vakiliner.chatcomponentapi.base;

public abstract class BaseParser {
	public abstract boolean supportsFallbackInTranslate();

	public abstract boolean supportsSeparatorInSelector();

	public abstract boolean supportsFontInStyle();
}