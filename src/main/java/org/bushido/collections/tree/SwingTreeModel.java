package org.bushido.collections.tree;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * The {@link TreeModel} implementation for the {@link Tree} data structure.
 * 
 * Can be used to display tree with the {@link JTree}
 * 
 * @author Victor_Gubin
 * 
 * @param <T>
 *            the type of tree elements
 *            
 * @see JTree#setModel(TreeModel)
 */
public class SwingTreeModel<T> extends DefaultTreeModel {

	private static final long serialVersionUID = 6728227195819427925L;

	/**
	 * Creates a {@link TreeModel} instance from the {@link Tree} data structure
	 * 
	 * @param tree
	 *            instance of {@link Tree} data structure
	 * @return {@link TreeModel} instance
	 * @throws IllegalArgumentException
	 *             if tree is {@code null}
	 */
	public static <T> SwingTreeModel<T> fromTree(final Tree<T> tree) {
		if (null == tree) {
			throw new IllegalArgumentException("can't build model from null");
		}
		return new SwingTreeModel<T>(tree);
	}

	private SwingTreeModel(final Tree<T> tree) {
		super(tree.getRoot(), true);
	}

}
