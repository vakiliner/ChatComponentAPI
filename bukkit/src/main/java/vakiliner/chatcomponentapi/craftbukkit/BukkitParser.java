package vakiliner.chatcomponentapi.craftbukkit;

import vakiliner.chatcomponentapi.base.BaseParser;

public class BukkitParser extends BaseParser {
	@Override
	public boolean supportsSeparatorInSelector() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsFontInStyle() {
		throw new UnsupportedOperationException();
	}
}