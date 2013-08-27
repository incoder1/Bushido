package org.bushido.collections.tree;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

class NodeIterator<T> implements Iterator<Node<T>>, Enumeration<Node<T>> {

	private Node<T> current;

	public NodeIterator(final Node<T> current) {
		this.current = current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return (null != this.current) && (this.current.getNext() != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Node<T> next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException();
		}
		this.current = current.getNext();
		return current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		this.current.getParent().removeChild(this.current);
		this.current = current.getNext();
	}

	@Override
	public boolean hasMoreElements() {
		return hasNext();
	}

	@Override
	public Node<T> nextElement() {
		return next();
	}

}
