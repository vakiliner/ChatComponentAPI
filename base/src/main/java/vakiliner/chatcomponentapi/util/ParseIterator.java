package vakiliner.chatcomponentapi.util;

import java.util.Iterator;
import java.util.function.Function;

public class ParseIterator<Output, Input> implements Iterator<Output> {
	protected final Iterator<? extends Input> original;
	protected final Function<Input, Output> i2o;

	public ParseIterator(Iterator<? extends Input> original, Function<Input, Output> i2o) {
		this.original = original;
		this.i2o = i2o;
	}

	public Iterator<? extends Input> getImpl() {
		return this.original;
	}

	public boolean hasNext() {
		return this.original.hasNext();
	}

	public Output next() {
		return this.i2o.apply(this.original.next());
	}

	public void remove() {
		this.original.remove();
	}
}