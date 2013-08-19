/*
   This library is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.bushido.collections.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.tree.TreeNode;

import org.bushido.collections.queues.Queues;
import org.bushido.collections.queues.Stack;

/**
 * {@code Tree} is a widely-used data structure that emulates a hierarchical
 * tree structure with a set of linked nodes.<br>
 * 
 * Following sample shows how to construct next tree structure in memory
 * 
 * The tree structure.
 * <p>
 * +root<br>
 * ├+level1-0<br>
 * │├─level2-0<br>
 * │└+level2-1<br>
 * │ ├─level3-0<br>
 * │ └─level3-1<br>
 * └─level1-1<br>
 * </p>
 * 
 * <pre>
 * final Tree&lt;String&gt; tree = new Tree&lt;String&gt;(&quot;root&quot;);
 * // level 1
 * Tree.Node&lt;String&gt; next = tree.appendChild(tree.getRoot(), &quot;level1-0&quot;);
 * tree.appendChild(tree.getRoot(), &quot;level1-1&quot;);
 * // level 2
 * tree.appendChild(next, &quot;level2-0&quot;);
 * next = tree.appendChild(next, &quot;level2-1&quot;);
 * // level 3
 * tree.appendChild(next, &quot;level3-0&quot;);
 * tree.appendChild(next, &quot;level3-1&quot;);
 * System.out.println(tree.toString());
 * </pre>
 * 
 * @author Victor Gubin
 * 
 */
public class Tree<T> implements Iterable<Tree.Node<T>> {

	/**
	 * Root {@code Node<T>} of tree
	 */
	private final Node<T> root;

	/**
	 * Construct new {@code Tree}
	 * 
	 * @param rootValue
	 *            value of root node
	 */
	public Tree(final T rootValue) {
		this.root = new Node<T>(null, rootValue);
	}

	/**
	 * Returns root {@link Node<T>} of tree
	 * 
	 * @return root node
	 */
	public Node<T> getRoot() {
		return this.root;
	}

	/**
	 * Creates an append a child node to base node
	 * 
	 * @param parent
	 *            parent node
	 * @param value
	 *            child node value
	 * @return new node which is child of {@code parent}
	 */
	public Node<T> appendChild(final Node<T> parent, final T value) {
		final Node<T> result = new Node<T>(parent, value);
		parent.appendChild(result);
		return result;
	}

	/**
	 * Removes child node from base node.
	 * 
	 * @param parent
	 *            parent node which child will be removed
	 * @param child
	 *            child node
	 * @return removed node
	 * @throws IllegalArgumentException
	 *             if child node is not inherited from {@code child}
	 */
	public Node<T> removeChild(final Node<T> parent, final Node<T> child) {
		if (null == child) {
			throw new IllegalArgumentException("Child can't be null");
		}
		if (null == parent) {
			throw new IllegalArgumentException("Parent can't be null");
		}
		return parent.removeChild(child);
	}

	/**
	 * Pre-order walk over the tree using heap memory for stack.<br>
	 * 
	 * Call {@link Node<T>Visitor#visitNode<T>(Node<T>)} for each node inherited
	 * from {@code root}. This implementation is slower then
	 * {@link #fastForEachNode<T>(Node<T>, Node<T>Visitor)} but protected from
	 * stack overflow. This method is preferable for huge trees.
	 * 
	 * @param root
	 *            root node for sub tree
	 * @param callback
	 *            operation which will be applied for each sub node
	 */
	public void forEachNode(final Node<T> root, final NodeVisitor<T> callback) {
		callback.visitNode(root);
		final Stack<Iterator<Node<T>>> stack = Queues.stack();
		stack.push(root.iterator());
		while (!stack.isEmpty()) {
			final Iterator<Node<T>> it = stack.peek();
			if (it.hasNext()) {
				final Node<T> next = it.next();
				callback.visitNode(next);
				if (next.hasChilds()) {
					stack.push(next.iterator());
				}
			} else {
				stack.pop();
			}
		}
	}

	/**
	 * Pre-order walk over the tree using thread call stack.<br>
	 * 
	 * Call {@link Node<T>Visitor#visitNode<T>(Node<T>)} for each node inherited
	 * from {@code root}. This implementation is faster then
	 * {@link #forEachNode<T>(Node<T>Visitor)} but stack overflow is possible in
	 * case of huge trees. This method is preferable for small trees.
	 * 
	 * @param root
	 *            root node for sub tree
	 * @param callback
	 *            operation which will be applied for each sub node
	 */
	public void fastForEachNode(final Node<T> root,
			final NodeVisitor<T> callback) {
		callback.visitNode(root);
		for (Node<T> node : root) {
			this.fastForEachNode(node, callback);
		}
	}

	/**
	 * Pre-order walk over the tree<br>
	 * 
	 * Call {@link Node<T>Visitor#visitNode<T>(Node<T>)} for each node inherited
	 * from root node
	 * 
	 * @param callback
	 *            operation which will be applied for each sub node
	 * @see #forEachNode<T>(Node<T>, Node<T>Visitor)
	 */
	public void forEachNode(final NodeVisitor<T> callback) {
		this.forEachNode(this.root, callback);
	}

	/**
	 * Returns pre-order tree nodes iterator. The iterator behavior is the same
	 * as {@link #forEachNode()}
	 */
	@Override
	public Iterator<Node<T>> iterator() {
		return new PreOrderTreeIterator<T>(this.root);
	}

	private class CopyHolder {
		private Iterator<Node<T>> iterator;
		private Node<T> root;

		CopyHolder(final Iterator<Node<T>> iterator, final Node<T> root) {
			this.iterator = iterator;
			this.root = root;
		}
	}

	/**
	 * Returns sub tree from node, each elements from this tree will be copied
	 * into sub tree
	 * 
	 * @param node
	 *            the root node for sub tree
	 * @return new tree, which is sub tree for nodes
	 */
	public Tree<T> subTree(final Node<T> node) {
		if (null == node) {
			throw new IllegalArgumentException("node can be null");
		}
		final Tree<T> result = new Tree<T>(node.getValue());
		final Stack<CopyHolder> stack = Queues.stack();
		stack.push(new CopyHolder(node.iterator(), result.getRoot()));
		while (!stack.isEmpty()) {
			final CopyHolder holder = stack.peek();
			if (holder.iterator.hasNext()) {
				final Node<T> src = holder.iterator.next();
				final Node<T> dest = result.appendChild(holder.root,
						src.getValue());
				if (src.hasChilds()) {
					stack.push(new CopyHolder(src.iterator(), dest));
				}
			} else {
				stack.pop();
			}
		}
		return result;
	}

	/**
	 * Append another tree into this tree
	 * 
	 * @param node
	 *            parent node for sub tree
	 * @param tree
	 *            sub tree to be appended
	 */
	public void appendTree(final Node<T> node, final Tree<T> tree) {
		final Stack<CopyHolder> stack = Queues.stack();
		stack.push(new CopyHolder(tree.getRoot().iterator(), this.appendChild(
				node, tree.getRoot().getValue())));
		while (!stack.isEmpty()) {
			final CopyHolder holder = stack.peek();
			if (holder.iterator.hasNext()) {
				final Node<T> src = holder.iterator.next();
				final Node<T> dest = this.appendChild(holder.root,
						src.getValue());
				if (src.hasChilds()) {
					stack.push(new CopyHolder(src.iterator(), dest));
				}
			} else {
				stack.pop();
			}
		}
	}

	/**
	 * Check that current tree contains node
	 * 
	 * @param node
	 *            searching node
	 * @return whether current tree contains this node
	 */
	public boolean contains(final Node<T> node) {
		Node<T> root = node;
		while (null != root.parent) {
			root = root.parent;
		}
		return root == this.root;
	}

	/**
	 * Finds set of nodes with compares evaluation. If there aren't such nodes
	 * set would be empty
	 * 
	 * @param value
	 * 
	 * @param comparator
	 *            values evaluation expression
	 * 
	 * @return set of nodes with such value,
	 */
	public Set<Node<T>> findNodeByValue(final T value,
			final Comparator<T> comparator) {
		final Set<Node<T>> result = new HashSet<Node<T>>();
		this.forEachNode(new NodeVisitor<T>() {
			@Override
			public void visitNode(final Node<T> node) {
				if (0 == comparator.compare(node.value, value)) {
					result.add(node);
				}
			}
		});
		return result;
	}

	/**
	 * Finds set of nodes with compares evaluation. If there aren't such nodes
	 * set will be empty
	 * <p>
	 * This method uses default comparator. In case if T implements comparable
	 * instances would be compared by {@link Comparable#compareTo(Object)}
	 * method. Elsewhere instances will be compared using equals method.
	 * </p>
	 * 
	 * @param value
	 * 
	 * @param comparator
	 *            values evaluation expression
	 * 
	 * @return set of nodes with such value,
	 */
	public Set<Node<T>> findNodeByValue(final T value) {
		Comparator<T> comparator = null;
		if ((Object) value instanceof Comparable<?>) {
			comparator = new Comparator<T>() {
				@Override
				@SuppressWarnings("unchecked")
				public int compare(T o1, T o2) {
					return ((Comparable<T>) o1).compareTo(o2);
				}
			};
		} else {
			comparator = new Comparator<T>() {
				public int compare(T o1, T o2) {
					return o1.equals(o2) ? 0 : -1;
				}
			};
		}
		return this.findNodeByValue(value, comparator);
	}

	/**
	 * Finds lowest (least) common ancestor for two tree nodes
	 * 
	 * @return LCA node
	 * @throws IllegalArgumentException
	 *             if nodes outside from this tree
	 */
	public Node<T> findLCA(Node<T> first, Node<T> second) {
		if (null == first || !this.contains(first)) {
			throw new IllegalArgumentException("fist can't be " + first);
		}
		if (null == second || !this.contains(second)) {
			throw new IllegalArgumentException("second can't be " + second);
		}
		// TODO: move on Farah-Kolon, Bender algorithm instead of this
		int h1 = this.depth(first);
		int h2 = this.depth(second);
		while (h1 != h2) {
			if (h1 > h2) {
				first = first.getParent();
				--h1;
			} else {
				second = second.getParent();
				--h2;
			}
		}
		while (first != second) {
			first = first.getParent();
			second = second.getParent();
		}
		return first;
	}

	/**
	 * Convert tree elements to list. Sequence is from left to right
	 * 
	 * @return new list from tree
	 */
	public List<T> asList() {
		final ArrayList<T> result = new ArrayList<T>();
		this.forEachNode(new NodeVisitor<T>() {
			@Override
			public void visitNode(Node<T> value) {
				result.add(value.getValue());
			}
		});
		result.trimToSize();
		return result;
	}

	/**
	 * Count all tree node elements
	 * 
	 * @return tree nodes count
	 */
	public int size() {
		final AtomicInteger result = new AtomicInteger(0);
		final NodeVisitor<T> callback = new NodeVisitor<T>() {
			@Override
			public void visitNode(Node<T> node) {
				result.incrementAndGet();
			}
		};
		this.forEachNode(callback);
		return result.get();
	}

	/**
	 * Finds nesting level for node
	 * 
	 * @param node
	 *            tree node
	 * @return node nesting level
	 */
	public int depth(final Node<T> node) {
		int result = 0;
		Node<T> next = node;
		while (next != this.root) {
			next = next.getParent();
			++result;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		this.forEachNode(root, new NodeVisitor<T>() {
			@Override
			public void visitNode(Node<T> next) {
				if (next != root) {
					final StringBuilder borders = new StringBuilder();
					Node<T> it = next.getParent();
					while (it != root) {
						if (!it.isLastChild()) {
							borders.append('│');
						} else {
							borders.append(' ');
						}
						it = it.getParent();
					}
					result.append(borders.reverse());
					if (next.isLastChild()) {
						result.append('└');
					} else {
						result.append('├');
					}
				}
				if (next.hasChilds()) {
					result.append('+');
				} else {
					result.append('─');
				}
				result.append(next.getValue());
				result.append('\n');
			}
		});
		// '+' '│' '└' '├' '─'
		return result.toString();
	}

/**
	 * Notification for walking over tree
	 * @see {@link Tree#forEachNode<T>(Node<T>, Node<T>Visitor)
	 * @see {@link Tree#forEachNode<T>(Node<T>Visitor)
	 * @param <T> type of tree
	 */
	public static interface NodeVisitor<T> {
		public void visitNode(Tree.Node<T> node);
	}

	/**
	 * Represent tree node
	 */
	public static class Node<T> implements Iterable<Node<T>>, TreeNode {

		private final Lock lock;

		private T value;

		private Node<T> parent;

		private Node<T> next;

		private Node<T> firstChild;

		private Node<T> lastChild;

		private Node(final Node<T> parent, final T value) {
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

		private void appendChild(final Node<T> node) {
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

		private Node<T> removeChild(final Node<T> node) {
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

	private static class NodeIterator<T> implements Iterator<Node<T>>,
			Enumeration<Node<T>> {

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

	private static class PreOrderTreeIterator<T> implements Iterator<Node<T>> {

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

}
