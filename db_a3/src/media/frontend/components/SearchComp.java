package media.frontend.components;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import media.definitions.MediaDbInterface;
import media.definitions.Product;
import media.frontend.utils.*;

/**
 * @author Silvio Paschke, Stefan Endrullis
 */
public class SearchComp extends JComponent implements Component {
	/** Logger. */
	private static Logger logger = Logger.getLogger(SearchComp.class.getName());

	final static long serialVersionUID = "media.frontend.components.SearchComp"
			.hashCode();

	private MediaDbInterface mdi;

	private Details details;
	
	private Table table = null;

	private List<Product> productList = new ArrayList<Product>();

	private JTextField prodSuche = new JTextField("%");

	private JCheckBox c_books = new JCheckBox("Buecher", true);

	private JCheckBox c_dvds = new JCheckBox("DVDs", true);

	private JCheckBox c_musix = new JCheckBox("Musik", true);

	public SearchComp(MediaDbInterface mdi, Details details) {
		super();
		this.mdi = mdi;
		this.details = details;
		initComponent();
	}

	/**
	 * Initialisiert die Komponente.
	 */
	public synchronized void initComponent() {
		this.setName("Produktsuche");
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));// aeusserer rahmen
		// untereinander

		// Produktsuche
		JPanel rahmen = new JPanel();

		rahmen.setLayout(new BoxLayout(rahmen, BoxLayout.X_AXIS));// ob.
		// rahmen,
		// nebeneinander
		this.add(new BorderPanel(rahmen, "Suche", 1000, 60));
		logger.fine("Breite: " + rahmen.getWidth() + "\tHoehe: " + rahmen.getHeight());

		rahmen.add(prodSuche);
		rahmen.add(c_books);
		rahmen.add(c_dvds);
		rahmen.add(c_musix);

		JButton produktSuchen = new JButton("suchen");

		rahmen.add(produktSuchen);

		/*
		 * Tabelle erzeugen und darstellen.
		 */
		JPanel rahmenTabelle = new JPanel();
		rahmenTabelle.setLayout(new BoxLayout(rahmenTabelle, BoxLayout.Y_AXIS));

		rahmenTabelle.setName("Produktsuche - Rahmen fuer die Produkte");
		table = new Table();
		table.createTable(rahmenTabelle);
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

		JPanel abstandhalter = new JPanel();
		rahmenTabelle.add(abstandhalter);
		this.add(new BorderPanel(rahmenTabelle, "Produktuebersicht"), true);

		// actionListen fuer den Suchen-Knopf
		produktSuchen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fillProductTable(e);
			}
		});
		prodSuche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fillProductTable(e);
			}
		});

	}

	/**
	 * Fuellt die Produkttabelle nach dem Titel, der im Textfeld eingegeben wurde.
	 *
	 * @param e Actionevent, um an den eingegebenen Text zu gelangen.
	 */
	private synchronized void fillProductTable(ActionEvent e) {
		String pattern = prodSuche.getText();
		if (pattern == null || pattern.equals("")) {
			pattern = "%";
		}

		String[] header = new String[]{"Produktgruppe", "Artikelnummer", "Artikelbezeichnung"};

		List<Product> pList = mdi.getProducts(pattern);
		productList.clear();
		List<String[]> data = new ArrayList<String[]>();
		for (int i = 0; i < pList.size(); i++) {
			Product prod = pList.get(i);
			String[] dump = new String[3];
			dump[0] = prod.isBook() ? "Buch" : prod.isDvd() ? "DVD" : prod.isMusic() ? "Musik" : "unbekannt";
			dump[1] = "" + prod.getAsin();
			dump[2] = prod.getTitle();

			if (prod.isBook() && !c_books.isSelected())
				continue;
			if (prod.isDvd() && !c_dvds.isSelected())
				continue;
			if (prod.isMusic() && !c_musix.isSelected())
				continue;
			data.add(dump);
			productList.add(prod);
		}
		this.table.fillTable(header, data);
		this.table.resizeColumns(new int[]{30, 30});
	}
}
