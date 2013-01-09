package media.frontend.components;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.datatransfer.Clipboard;


import javax.swing.*;

import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.DefaultTreeModel;

import media.definitions.Category;
import media.definitions.MediaDbInterface;
import media.definitions.Product;
import media.frontend.utils.*;

/**
 * @author Silvio Paschke, Stefan Endrullis
 */
public class Categories extends JComponent implements Component {
	/** Logger. */
	private static Logger logger = Logger.getLogger(Categories.class.getName());

	final static long serialVersionUID = "media.frontend.components.Categories"
			.hashCode();

	private MediaDbInterface mdi;

	private Details details;

	private JTree categoryTree = null;

	private final Category loadCats = new Category(null, "loading");

	private List<Product> productList = new ArrayList<Product>();

	private Table table = null;

	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	public Categories(MediaDbInterface mdi, Details details) {
		super();
		this.mdi = mdi;
		this.details = details;

		try {
			initComponent();
		} catch (Exception e) {
			System.err
					.println("Die Komponente Categories.class konnte nicht geladen werden.");
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Initialisiert die Komponente.
	 */
	public synchronized void initComponent() {
		this.setName("Kategorienbaum");
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));// aeusserer rahmen
		// untereinander

		// Baum
		try {
			fillTree();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		setLayout(new GridBagLayout());

		// add split pane
		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(false);
		splitPane.setOneTouchExpandable(true);
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		add(splitPane, gbc);


		categoryTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				Object[] ancs = path.getPath();
				Category[] categories = new Category[ancs.length];
				for (int i = 0; i < categories.length; i++) {
					categories[i] = (Category) ((DefaultMutableTreeNode) ancs[i]).getUserObject();
				}
				try {
					fillProductList(categories);
				} catch (Exception q) {
					System.err
							.println("Die Produktliste konnte nicht geladen werden (Categories.class)");
					q.printStackTrace(System.err);
				}
			}
		});
		categoryTree.addTreeWillExpandListener(new TreeWillExpandListener() {
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
				Category category = (Category) node.getUserObject();
				System.out.println("debug cat: "+category.getName());
				if (node.getChildCount() == 1) {
					DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) node.getChildAt(0);
					Category category2 = (Category) node2.getUserObject();
					System.out.println("debug cat2: "+category2.getName());
					
					// if the child is the loadCats, replace the children by the right children of the category
					if (category2 == loadCats) {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						node.removeAllChildren();
						
						for (Category category1 : category.getChildren()) {
							System.out.println("debug cat1: "+category1.getId());
							category1 = mdi.getCategoryTree(category1.getId());
							node.add(nonRecursive(category1));
						}
						((DefaultTreeModel) categoryTree.getModel()).reload(node);
						setCursor(Cursor.getDefaultCursor());
					}
				}
			}

			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
			}
		});

		JPanel produktrahmen = new JPanel();
		produktrahmen.setLayout(new BorderLayout());
		produktrahmen.setName("Kategorienbaum - Rahmen fuer Produkte");
		// JButton details = new JButton("Details");

		table = new Table();
		table.createTable(produktrahmen);
		table.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int selectionIndex = table.getTable().getSelectionModel().getMinSelectionIndex();
				if (selectionIndex >= 0) {
					Product product = productList.get(selectionIndex);
					details.loadDetailsFromProduct(product);
				}
			}
		});

		splitPane.setLeftComponent(new BorderPanel(categoryTree, "Kategorienbaum", 300, 200, 300, 1000, true));
		splitPane.setRightComponent(new BorderPanel(produktrahmen, "Produkte", true));
	}

	/**
	 * Fuellt den linken Baum mit den Kategorien.
	 *
	 * @throws Exception
	 */
	private synchronized void fillTree() throws Exception {
//		System.out.println("asdsadasd: "+mdi.getCategoryTree().getChildren().iterator().next().getId());
		
		this.categoryTree = new JTree(nonRecursive(mdi.getCategoryTree()));
		categoryTree.collapseRow(0);
	}

	/**
	 * Der Kategorienbaum ist rekursiv. Um den Baum entsprechend DefaultMutableTreeNode
	 * aufzubauen empfiehlt es sich also rekursiv vorzugehen. Diese Methode realisiert dies.
	 *
	 * @param cat Kategorie, die weiter zerlegt werden soll
	 * @return Ast, der durch die Rekursion erzeugt wurde
	 */
	private DefaultMutableTreeNode recursive(Category cat) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(cat.getName());

		Set<Category> set = cat.getChildren();
		for (Category category : set) {
			node.add(recursive(category));
		}

		return node;
	}

	/**
	 * Der Kategorienbaum ist rekursiv. Hier wird jedoch keine Rekursion durchgefuehrt.
	 *
	 * @param cat Kategorie, die weiter zerlegt werden soll
	 * @return Knoten zu der Kategorie
	 */
	private DefaultMutableTreeNode nonRecursive(Category cat) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(cat);
		node.add(new DefaultMutableTreeNode(loadCats));

		return node;
	}

	/**
	 * Fuellt die Produktliste rechts entsprechend des ausgewaehlten Pfades aus dem linken Baum.
	 *
	 * @param categories enthaelt den Pfad, der ausgewaehlt wurde.
	 */
	private synchronized void fillProductList(Category[] categories) {
		logger.fine("running fillProductList()");
		System.out.println("running fillProductList()");
		productList = mdi.getProductsByCategoryPath(categories);
		System.out.println("got categrpyPath done!");
		String[] header = new String[]{"Produktgruppe", "Artikelnummer",
				"Artikelbezeichnung"};
		System.out.println("fill rows");
		List<String[]> rows = new ArrayList<String[]>();
		for (int i = 0; i < productList.size(); i++) {
			Product prod = productList.get(i);
			String[] dump = new String[3];
			dump[0]="bla";
			try{
			dump[0] = prod.isBook() ? "Buch" : prod.isDvd() ? "DVD" : prod
					.isMusic() ? "Musik" : "unbekannt";
			}catch(Exception e){
				System.out.println("ERROR in CATEGORIES: "+e.getMessage());
			}
			dump[1] = "" + prod.getAsin();
			dump[2] = prod.getTitle();

			rows.add(dump);
		}
		this.table.fillTable(header, rows);
	}
}
