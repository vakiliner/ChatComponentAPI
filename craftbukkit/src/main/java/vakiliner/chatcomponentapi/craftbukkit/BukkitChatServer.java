package vakiliner.chatcomponentapi.craftbukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.util.ParseCollection;

public class BukkitChatServer implements ChatServer, ChatPlayerList {
	private static final Method GET_HANDLE;
	private static final Method EXECUTE_SYNC;
	protected final BukkitParser parser;
	protected final Server server;

	static {
		Server server = Bukkit.getServer();
		try {
			GET_HANDLE = server.getClass().getMethod("getServer");
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
		Class<?> nmsClass = GET_HANDLE.getReturnType();
		if (!Executor.class.isAssignableFrom(nmsClass)) {
			throw new IllegalStateException();
		}
		try {
			EXECUTE_SYNC = nmsClass.getMethod("executeSync", Runnable.class);
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
		if (EXECUTE_SYNC.getReturnType() != void.class) {
			throw new IllegalStateException();
		}
	}

	public BukkitChatServer(BukkitParser parser, Server server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public Server getImpl() {
		return this.server;
	}

	@Override
	public ChatServer getServer() {
		return this;
	}

	public Executor getNMS() {
		try {
			return (Executor) GET_HANDLE.invoke(this.server);
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		} catch (InvocationTargetException err) {
			Throwable target = err.getTargetException();
			if (target instanceof Error) throw (Error) target;
			if (target instanceof RuntimeException) throw (RuntimeException) target;
			throw new RuntimeException(err);
		}
	}

	@Override
	public ChatPlayerList getPlayerList() {
		return this;
	}

	@Override
	public String getName() {
		return this.server.getConsoleSender().getName();
	}

	@Override
	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.server.getConsoleSender(), component, type, uuid);
	}

	@Override
	public boolean isDedicatedServer() {
		return true;
	}

	// Not supported
	@Override
	public String getSingleplayerName() {
		return null;
	}

	@Override
	public boolean isSingleplayer() {
		return this.getSingleplayerName() != null;
	}

	@Override
	public boolean isSingleplayerOwner(GameProfile gameProfile) {
		return false;
	}

	@Override
	public int getPlayerCount() {
		return this.server.getOnlinePlayers().size();
	}

	@Override
	public int getMaxPlayers() {
		return this.server.getMaxPlayers();
	}

	@Override
	public Collection<? extends ChatPlayer> getPlayers() {
		return new ParseCollection<>(this.server.getOnlinePlayers(), this.parser::toChatPlayer);
	}

	@Override
	public ChatPlayer getPlayer(UUID uuid) {
		return this.parser.toChatPlayer(this.server.getPlayer(uuid));
	}

	@Override
	public ChatPlayer getPlayer(String name) {
		return this.parser.toChatPlayer(this.server.getPlayerExact(name));
	}

	@Override
	public void execute(Runnable command) {
		this.getNMS().execute(command);
	}

	@Override
	public void executeBlocking(Runnable command) {
		try {
			EXECUTE_SYNC.invoke(this.getNMS(), command);
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		} catch (InvocationTargetException err) {
			Throwable target = err.getTargetException();
			if (target instanceof Error) throw (Error) target;
			if (target instanceof RuntimeException) throw (RuntimeException) target;
			throw new RuntimeException(err);
		}
	}

	@Override
	@Deprecated
	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server.getScheduler(), plugin, runnable);
	}

	@Override
	@Deprecated
	public void executeBlocking(IChatPlugin plugin, Runnable runnable) {
		this.parser.executeBlocking(this.server.getScheduler(), plugin, runnable);
	}

	@Override
	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid, Predicate<? super ChatPlayer> predicate) {
		this.parser.broadcastMessage(this.server, component, type, uuid, predicate);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			BukkitChatServer other = (BukkitChatServer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server);
		} else {
			return false;
		}
	}
}
