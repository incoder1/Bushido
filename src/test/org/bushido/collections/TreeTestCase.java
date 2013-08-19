/**
 * 
 */
package org.bushido.collections;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bushido.tree.Tree;
import org.bushido.tree.Tree.NodeVisitor;
import org.junit.Test;

/**
 * Test {@link Tree} functionality
 * 
 * @author Victor Gubin
 * 
 */
public class TreeTestCase {

	private static final String[] TREE = { "root", "level1-0", "level2-0",
			"level2-1", "level3-0", "level3-1", "level1-1" };

	private static final String TOSTRING = "+root\n├+level1-0\n│├─level2-0\n│└+level2-1\n│ ├+level3-0\n"
			+ "│ │├─level4-0\n│ │├+level4-1\n│ ││└─level5-0\n│ │└─level4-2\n│ └─level3-1\n├─level1-1\n"
			+ "├+level1-2\n│└─level2-2\n└+level1-3\n └─level2-3\n";

	private final String[] FOR_EACH_PATH = { "root", "level1-0", "level2-0",
			"level3-0", "level4-0", "level1-1", "level1-2" };
	
	private Tree<String> createTestTree() {
		final Tree<String> tree = new Tree<String>("root");
		Tree.Node<String> next = tree.appendChild(tree.getRoot(), "level1-0");
		next = tree.appendChild(next, "level2-0");
		next = tree.appendChild(next, "level3-0");
		next = tree.appendChild(next, "level4-0");
		next = tree.appendChild(tree.getRoot(), "level1-1");
		next = tree.appendChild(tree.getRoot(), "level1-2");
		return tree;
	}

	@Test
	public void testForEachNode() throws Exception {
		final Tree<String> tree = createTestTree();
		final List<String> path = new ArrayList<String>(7);
		tree.forEachNode(tree.getRoot(), new NodeVisitor<String>() {
			@Override
			public void visitNode(Tree.Node<String> node) {
				path.add(node.getValue());
			}
		});
		final String actualPaht[] = new String[7];
		path.toArray(actualPaht);
		assertArrayEquals("Wrong visit path", FOR_EACH_PATH, actualPaht);
	}



	@Test
	public void testFastForEachNode() throws Exception {
		final Tree<String> tree = createTestTree();
		final List<String> path = new ArrayList<String>(7);
		tree.fastForEachNode(tree.getRoot(), new NodeVisitor<String>() {
			@Override
			public void visitNode(Tree.Node<String> node) {
				path.add(node.getValue());
			}
		});
		final String actualPaht[] = new String[7];
		path.toArray(actualPaht);
		assertArrayEquals("Wrong visit path", FOR_EACH_PATH, actualPaht);
	}

	@Test
	public void testPerorderIterator() throws Exception {
		final Tree<String> tree = createTestTree();
		final List<String> path = new ArrayList<String>(7);
		for (Tree.Node<String> node : tree) {
			path.add(node.getValue());
		}
		final String actualPaht[] = new String[7];
		path.toArray(actualPaht);
		assertArrayEquals("Wrong visit path", FOR_EACH_PATH, actualPaht);
	}

	@Test
	public void testSize() {
		final Tree<String> tree = createTestTree();
		assertEquals("Issue with size detection", 7, tree.size());
	}

	@Test
	public void testAsList() {
		final Tree<String> tree = new Tree<String>("root");
		// level 1
		Tree.Node<String> next = tree.appendChild(tree.getRoot(), "level1-0");
		tree.appendChild(tree.getRoot(), "level1-1");
		// level 2
		tree.appendChild(next, "level2-0");
		next = tree.appendChild(next, "level2-1");
		// level 3
		tree.appendChild(next, "level3-0");
		tree.appendChild(next, "level3-1");

		assertEquals("Structure not the same", Arrays.asList(TREE),
				tree.asList());
	}

	@Test
	public void testToString() {
		final Tree<String> tree = new Tree<String>("root");
		// level 1
		Tree.Node<String> next = tree.appendChild(tree.getRoot(), "level1-0");
		tree.appendChild(tree.getRoot(), "level1-1");
		tree.appendChild(tree.appendChild(tree.getRoot(), "level1-2"),
				"level2-2");
		tree.appendChild(tree.appendChild(tree.getRoot(), "level1-3"),
				"level2-3");
		// level 2
		tree.appendChild(next, "level2-0");
		next = tree.appendChild(next, "level2-1");
		Tree.Node<String> level2_1 = next;
		// level 3
		next = tree.appendChild(next, "level3-0");
		Tree.Node<String> level3_0 = next;
		tree.appendChild(next, "level4-0");
		next = tree.appendChild(next, "level4-1");
		next = tree.appendChild(next, "level5-0");
		tree.appendChild(level3_0, "level4-2");
		tree.appendChild(level2_1, "level3-1");

		// System.out.println(tree.toString());
		// System.out.println(TOSTRING);
		assertEquals("To string is corrupt", TOSTRING, tree.toString());
	}

	@Test
	public void testSubtree() throws Exception {
		final Tree<String> tree = new Tree<String>("root");
		tree.appendChild(tree.getRoot(), "level1-0");
		tree.appendChild(tree.getRoot(), "level1-1");
		final Tree<String> subTree = tree.subTree(tree.getRoot());
		assertEquals("Sub tree returing wrong structure", tree.toString(),
				subTree.toString());
	}

	@Test
	public void testAppendTree() throws Exception {
		final Tree<String> tree = new Tree<String>("OrignialRoot");
		final Tree<String> appended = new Tree<String>("AppendedRoot");
		Tree.Node<String> next = appended.appendChild(appended.getRoot(),
				"Appended Level1 0");
		appended.appendChild(next, "Appended Level2 0");
		tree.appendTree(tree.getRoot(), appended);
		assertEquals("Append tree not working", 4, tree.size());
	}

	@Test
	public void testLCA() throws Exception {
		final Tree<String> tree = new Tree<String>("root");
		// level 1
		Tree.Node<String> next = tree.appendChild(tree.getRoot(), "level1-0");
		final Tree.Node<String> first = tree.appendChild(tree.getRoot(),
				"level1-1");
		// level 2
		tree.appendChild(next, "level2-0");
		next = tree.appendChild(next, "level2-1");
		// level 3
		final Tree.Node<String> second = tree.appendChild(next, "level3-0");
		assertEquals("LCA is incorret", tree.getRoot(),
				tree.findLCA(first, second));
	}

	@Test
	public void testTestFindNodeByValue() throws Exception {
		final Tree<String> tree = new Tree<String>("root");
		// level 1
		Tree.Node<String> level = tree.appendChild(tree.getRoot(), "A");
		level = tree.appendChild(level, "B");
		level = tree.appendChild(tree.getRoot(), "C");
		tree.appendChild(level, "A");
		// System.out.println(tree.toString());
		assertEquals("Ivalid node count", 2, tree.findNodeByValue("A").size());
	}

}
