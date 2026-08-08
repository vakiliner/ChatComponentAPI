package vakiliner.chatcomponentapi.fabric;

import java.util.Date;
import net.minecraft.server.players.BanListEntry;
import net.minecraft.server.players.StoredUserList;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanList;

public abstract class FabricChatBanList<Key, HandleKey, HandleEntry extends BanListEntry<HandleKey>, List extends StoredUserList<HandleKey, HandleEntry>> extends FabricChatStoredUserList<Key, HandleKey, ChatBanEntry, HandleEntry, List> implements ChatBanList<Key> {
	public FabricChatBanList(FabricParser parser, List list) {
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