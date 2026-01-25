package vakiliner.chatcomponentapi.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public class ParseCollection<Output, Input> extends AbstractCollection<Output> {
	protected final Collection<? extends Input> original;
	protected final Function<Input, Output> i2o;

	public ParseCollection(Collection<? extends Input> original, Function<Input, Output> i2o) {
		this.original = original;
		this.i2o = i2o;
	}

	public Collection<? extends Input> getImpl() {
		return original;
	}

	public Iterator<Output> iterator() {
		return new ParseIterator<>(this.original.iterator(), this.i2o);
	}

	public int size() {
		return this.original.size();
	}

	public boolean isEmpty() {
		return this.original.isEmpty();
	}

	public boolean add(Output e) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		this.original.clear();
	}
}