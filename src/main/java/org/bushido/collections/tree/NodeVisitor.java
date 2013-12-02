package org.bushido.collections.tree;

/**
 * Notification for walking over tree
 * @see {@link Tree#forEachNode<T>(Node<T>, Node<T>Visitor)
 * @see {@link Tree#forEachNode<T>(Node<T>Visitor)
 * @param <T> type of tree
 */
public interface NodeVisitor<T> {
	/**
	 * Callback to be applied for each node in tree 
	 * @param node
	 */
	public void visitNode(Node<T> node);
}

