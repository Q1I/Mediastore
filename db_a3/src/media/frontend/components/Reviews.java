package media.frontend.components;

import java.util.logging.Logger;
import java.util.List;
import java.util.Date;
import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import media.definitions.*;
import media.frontend.utils.*;


/**
 * @author Silvio Paschke, Stefan Endrullis
 */
public class Reviews extends JComponent implements Component {
	/** Logger. */
	private static Logger logger = Logger.getLogger(Reviews.class.getName());

	final static long serialVersionUID = "media.frontend.components.Categories"
			.hashCode();

	private MediaDbInterface mdi;

	private Details details;
	
	private String loadReviews = "loading";

	private JTree productTree = null;

	private JPanel reviewRahmen = null;

	private DefaultMutableTreeNode selectedProductNode = null;

	private JTextArea customer = null;
	private JTextArea summary = null;
	private JTextArea content = null;
	private JTextField rating = null;
	private JButton saveButton = null;
	private Review review = null;

	public Reviews(MediaDbInterface mdi, Details details) {
		super();
		this.mdi = mdi;
		this.details = details;
		
		try {
			initComponent();
		} catch (Exception e) {
			System.err
					.println("Die Komponente Reviews.class konnte nicht geladen werden.");
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Initialisiert die Komponente.
	 */
	public synchronized void initComponent() {
		// Baum fuellen
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

		productTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object userObject = node.getUserObject();

				if (userObject != null) {
					if (userObject instanceof Product) {
						selectedProductNode = node;
						addNewReview((Product) userObject, reviewRahmen);
						details.loadDetailsFromProduct((Product) userObject);
					}
					else if (userObject instanceof Review) {
						fillReviewList((Review) userObject, reviewRahmen);
					}
				}
			}
		});
		productTree.addTreeWillExpandListener(new TreeWillExpandListener() {
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
				Object userObject = node.getUserObject();

				if (userObject != null && userObject instanceof Product && node.getChildCount() == 1) {
					DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) node.getChildAt(0);
					Object userObject2 = node2.getUserObject();

					if (userObject2 == loadReviews) {
						Product product = (Product) userObject;
						String asin = product.getAsin();
//						System.out.println("@@@@@@@@@@@DEBUG: "+asin);
					
						Product prod =	mdi.getProduct(asin);
//						System.out.println("@@@@@@@@@@@DEBUG: "+prod.getAsin());
//						System.out.println("@@@@@@@@@@@DEBUG: "+prod.getReviews().size());
						loadAndInsertReviews(node, prod);
					}
				}
			}

			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
			}
		});

		reviewRahmen = new JPanel();
		reviewRahmen.setLayout(new BoxLayout(reviewRahmen, BoxLayout.Y_AXIS));
		reviewRahmen.setBorder(BorderFactory.createTitledBorder("Produkte"));

		this.customer = new JTextArea();
		this.summary = new JTextArea();
		this.content = new JTextArea();
		this.rating = new JTextField();
		reviewRahmen.add(new BorderPanel(customer, "Benutzer"));
		reviewRahmen.add(new BorderPanel(summary, "Zusammenfassung"));
		reviewRahmen.add(new BorderPanel(content, "Inhalt"));
		reviewRahmen.add(new BorderPanel(rating, "Bewertung", 200, 65, 1200, 65));

		saveButton = new JButton("speichern");
		reviewRahmen.add(saveButton);
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				logger.fine("speichere neuen Review");

				Integer rat = null;
				try {
					rat = Integer.parseInt(rating.getText());
				} catch(Exception e1) {}

				System.out.println("DEBUG REVIEWSS. product: "+review.getProduct().getTitle()+"u: "+customer.getText()+", s: "+summary.getText()+", c"+content.getText()+", r: "+rat);
				review.setKundenName(customer.getText());
				review.setSummary(summary.getText());
				review.setContent(content.getText());
				review.setRating(rat);
				if(rat==null)
				JOptionPane.showMessageDialog(new JFrame(), "Invalid Rating!", "Dialog",
				        JOptionPane.ERROR_MESSAGE);
				else if(rat > 5 || rat <0)
					JOptionPane.showMessageDialog(new JFrame(), "Invalid Rating!", "Dialog",
					        JOptionPane.ERROR_MESSAGE);
				else if (customer.getText().length()==0)
					JOptionPane.showMessageDialog(new JFrame(), "Invalid User!", "Dialog",
					        JOptionPane.ERROR_MESSAGE);
				else{
					mdi.addNewReview(review);
					JOptionPane.showMessageDialog(new JFrame(), "Add done!", "Dialog",
					        JOptionPane.INFORMATION_MESSAGE);
				}
				if (selectedProductNode != null) {
					loadAndInsertReviews(selectedProductNode, mdi.getProduct(review.getProduct().getAsin()));
				}
				
			}
		});

		splitPane.setLeftComponent(new BorderPanel(productTree, "Uebersicht", 300, 600, 300, 1000, true));
		splitPane.setRightComponent(reviewRahmen);
	}

	private void loadAndInsertReviews(DefaultMutableTreeNode productNode, Product product) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		productNode.removeAllChildren();
		for (Review review1 : product.getReviews()) {
			productNode.add(new DefaultMutableTreeNode(review1, false));
		}
		((DefaultTreeModel) productTree.getModel()).reload(productNode);
		setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * Fuellt den Baum mit [Artikelnummer] Titel + Benutzername um die Rezensionen zu gruppieren.
	 */
	private synchronized void fillTree() {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("+");
		List<Product> list = mdi.getReviewProducts();

		for (Product product : list) {
			DefaultMutableTreeNode productNode = new DefaultMutableTreeNode(product, true);
			root.add(productNode);

			productNode.add(new DefaultMutableTreeNode(loadReviews, false));
			/*
			for (Review review : product.getReviews()) {
				DefaultMutableTreeNode reviewNode = new DefaultMutableTreeNode(review, false);
				productNode.add(reviewNode);
			}
			*/
		}

		this.productTree = new JTree(root);
	}


	private void addNewReview(Product product, JComponent frame) {
		logger.fine("running addNewReview()");

		// erstelle neuen Review
		review = new Review();
		review.setProduct(product);
		review.setDate(new Date(System.currentTimeMillis()));

		this.customer.setText("Guest");
		this.summary.setText("");
		this.content.setText("");
		this.rating.setText("");
		this.saveButton.setVisible(true);
	}

	/**
	 * Erzeugt die Liste mit den Rezensionen aus dem Pfad.
	 *
	 * @param review selected review
	 * @param frame Container in dem die Liste dargestellt werden soll.
	 */
	private void fillReviewList(Review review, JComponent frame) {
		logger.fine("running fillReviewList()");

		this.customer.setText(review.getUsername().getId());
		this.summary.setText(review.getSummary());
		this.content.setText(review.getContent());
		this.rating.setText(review.getRating() == null ? "keine" : "" + review.getRating());

		this.saveButton.setVisible(false);
	}
}
