package vakiliner.chatcomponentapi.fabric;

import java.util.Date;
import net.minecraft.server.players.BanListEntry;
import net.minecraft.server.players.StoredUserList;
import vakiliner.chatcomponentapi.base.ChatBanEntry;
import vakiliner.chatcomponentapi.base.ChatBanList;

public abstract class FabricChatBanList<Key, HandleKey, Input extends BanListEntry<HandleKey>, List extends StoredUserList<HandleKey, Input>> extends FabricChatStoredUserList<Key, HandleKey, ChatBanEntry, Input, List> implements ChatBanList<Key> {
	public FabricChatBanList(FabricParser parser, List list) {
		super(parser, list);
	}

	protected abstract Input create(HandleKey key);

	protected abstract Input create(HandleKey key, String reason, String source, Date expires);

	@Override
	public ChatBanEntry add(Key key) {
		Input entry = this.create(this.cast(key));
		this.list.add(entry);
		return this.cast(entry);
	}

	@Override
	public ChatBanEntry add(Key key, String reason, String source, Date expires) {
		Input entry = this.create(this.cast(key), reason, source, expires);
		this.list.add(entry);
		return this.cast(entry);
	}
}