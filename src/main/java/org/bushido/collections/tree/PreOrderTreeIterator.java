package org.bushido.collections.tree;

import java.util.Iterator;

import org.bushido.collections.queues.Queues;
import org.bushido.collections.queues.Stack;

class PreOrderTreeIterator<T> implements Iterator<Node<T>> {

	private final Stack<Node<T>> stack;

	public PreOrderTreeIterator(final Node<T> root) {
		this.stack = Queues.stack();
		stack.push(root);
	}

	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	public Node<T> next() {
		final Node<T> result = stack.pop();
		if (result.hasChilds()) {
			final Stack<Node<T>> childsStack = Queues.stack();
			for (Node<T> child : result) {
				childsStack.push(child);
			}
			while (!childsStack.isEmpty()) {
				stack.push(childsStack.pop());
			}
		}
		return result;
	}

	@Override
	public void remove() {
		final Node<T> current = stack.peek();
		final Node<T> parrent = current.getParent();
		if (null == parrent) {
			throw new IllegalStateException("Can't remove root node");
		} else {
			parrent.removeChild(current);
		}
	}

}
