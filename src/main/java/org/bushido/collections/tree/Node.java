package org.bushido.collections.tree;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.tree.TreeNode;

/**
 * Represent tree node
 */
public class Node<T> implements Iterable<Node<T>>, TreeNode {

	private final Lock lock;

	private T value;

	private Node<T> parent;

	private Node<T> next;

	private Node<T> firstChild;

	private Node<T> lastChild;

	Node(final Node<T> parent, final T value) {
		this.lock = new ReentrantLock();
		this.value = value;
		this.firstChild = null;
		this.lastChild = null;
	}

	/**
	 * Returns parent of this node
	 * 
	 * @return parent node
	 */
	public Node<T> getParent() {
		return parent;
	}

	/**
	 * Returns next child node of this node parent if any
	 * 
	 * @return next child node of this node parent or {@code null} if there
	 *         is no next child
	 */
	public Node<T> getNext() {
		return next;
	}

	/**
	 * Returns node value
	 * 
	 * @return node value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Set node value
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}

	void appendChild(final Node<T> node) {
		lock.lock();
		try {
			if (null == this.firstChild) {
				this.firstChild = node;
				this.lastChild = this.firstChild;
			} else {
				lastChild.next = node;
				lastChild = node;
			}
			node.parent = this;
		} finally {
			lock.unlock();
		}
	}

	private Node<T> findPervNode(Node<T> node) {
		Node<T> result = this.firstChild;
		while ((node != result.next) || (null != result.next)) {
			result = result.next;
		}
		return result;
	}

	Node<T> removeChild(final Node<T> node) {
		lock.lock();
		try {
			if (node == this.firstChild) {
				this.firstChild = this.firstChild.next;
			} else {
				final Node<T> prev = this.findPervNode(node);
				if (null == prev) {
					return null;
				}
				prev.next = node.next;
			}
		} finally {
			lock.unlock();
		}
		return node;
	}

	/**
	 * Returns whether this node has child's
	 * 
	 * @return whether node has child
	 */
	public boolean hasChilds() {
		return null != this.firstChild;
	}

	/**
	 * Count this node children
	 * 
	 * @return child's count
	 */
	public int getChildCount() {
		int result = 0;
		Node<T> child = this.firstChild;
		while (child != null) {
			++result;
			child = child.next;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLeaf() {
		return null == this.firstChild;
	}

	/**
	 * Return {@code true} if this node is last child of parent node
	 * 
	 * @return whether this node is last child of it parent
	 */
	public boolean isLastChild() {
		return this.next == null;
	}

	/**
	 * Return {@code true} if this node is last child of {@code node}
	 * 
	 * @param node
	 *            an parent node
	 * @return whether this node is last child of {@code node}
	 */
	public boolean isLastChildOf(final Node<T> node) {
		return this.parent == node && null == this.next;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Node<T>> iterator() {
		final Node<T> begin = new Node<T>(this.parent, null);
		begin.next = this.firstChild;
		return new NodeIterator<T>(begin);
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		Node<T> child = this.firstChild;
		for (int i = 0; i != childIndex && child.next != null; i++) {
			child = child.next;
		}
		return child;
	}

	@Override
	public int getIndex(TreeNode node) {
		@SuppressWarnings("unchecked")
		final Node<T> parent = (Node<T>) node;
		int result = -1;
		if (parent != this.parent) {
			result = -1;
		} else {
			result = 0;
			for (Node<T> it : parent) {
				++result;
				if (it == this) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public boolean getAllowsChildren() {
		return !isLeaf();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Enumeration<Node<T>> children() {
		return (Enumeration<Node<T>>) iterator();
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

}
