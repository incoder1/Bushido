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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
public class Tree<T> implements Iterable<Node<T>> {

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
	 * Creates and append a child node to base node
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
		while (null != root.getParent()) {
			root = root.getParent();
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
				if (0 == comparator.compare(node.getValue(), value)) {
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
		final LinkedList<T> result = new LinkedList<T>();
		this.forEachNode(new NodeVisitor<T>() {
			@Override
			public void visitNode(Node<T> value) {
				result.add(value.getValue());
			}
		});
		return result;
	}

	/**
	 * Count all tree node elements
	 * 
	 * @return tree nodes count
	 */
	public int size() {
		final int result[] = {0};
		final NodeVisitor<T> callback = new NodeVisitor<T>() {
			@Override
			public void visitNode(Node<T> node) {
				result[0] += 1;
			}
		};
		this.forEachNode(callback);
		return result[0];
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

}
