package vakiliner.chatcomponentapi.forge;

import java.util.Date;
import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.UserList;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanList;

public abstract class ForgeChatBanList<Key, HandleKey, HandleEntry extends BanEntry<HandleKey>, List extends UserList<HandleKey, HandleEntry>> extends ForgeChatStoredUserList<Key, HandleKey, ChatBanEntry, HandleEntry, List> implements ChatBanList<Key> {
	public ForgeChatBanList(ForgeParser parser, List list) {
		super(parser, list);
	}

	protected abstract HandleEntry create(Key key);

	protected abstract HandleEntry create(Key key, String reason, String source, Date expires);

	@Override
	public ChatBanEntry add(Key key) {
		HandleEntry entry = this.create(key);
		this.list.add(entry);
		return this.cast(entry);
	}

	@Override
	public ChatBanEntry add(Key key, String reason, String source, Date expires) {
		HandleEntry entry = this.create(key, reason, source, expires);
		this.list.add(entry);
		return this.cast(entry);
	}
}