package media.frontend.components;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import javax.swing.*;

import media.definitions.Author;
import media.definitions.MediaDbInterface;
import media.definitions.Offer;
import media.definitions.Person;
import media.definitions.Product;
import media.definitions.Track;
import media.frontend.utils.*;

import java.net.URL;

/**
 * Hier wird die Detail-Kartei designed.
 *
 * @author Tobias Peitzsch, Silvio Paschke, Stefan Endrullis
 * @author Alrik Hausdorf: Aenderungen damit das Korrekte angezeigt wird
 */
public class Details extends JComponent implements Component {

	final static long serialVersionUID =
			"media.frontend.components.Details".hashCode();


	private MediaDbInterface mdi;

	private Clipboard clipboard =
			Toolkit.getDefaultToolkit().getSystemClipboard();

	// GUI
	private JTextField[] att;
	private JComboBox price;
	private JPanel picture;
	private JPanel detailinfos;

	private Table table = null;

	public Details(MediaDbInterface mdi) {
		this.mdi = mdi;
		initComponent();
	}


	public void initComponent() {
		try {
			this.removeAll();
		} catch (Exception e) {
		}
		this.setName("Details");
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		//grobe zeilenstruktur
		JPanel[] line = new JPanel[3];
		add(line[0] = new JPanel());
		add(line[1] = new JPanel());
		add(line[2] = new JPanel());

		//in den zeile sollen die Elemente nebeneinander stehen
		line[0].setLayout(new BoxLayout(line[0], BoxLayout.X_AXIS));
		line[1].setLayout(new BoxLayout(line[1], BoxLayout.X_AXIS));
		line[2].setLayout(new BoxLayout(line[2], BoxLayout.X_AXIS));
		//die dritte zeile soll nochmal gespalten werden

		detailinfos = new JPanel();
		detailinfos.setLayout(new BoxLayout(detailinfos, BoxLayout.Y_AXIS));
		picture = new JPanel();


		att = new JTextField[]{
				new JTextField("id"),
				new JTextField("status"),
				new JTextField("aver"),
				new JTextField("salesr"),
				new JTextField("avail"),
				new JTextField("type"),
				new JTextField("title")
		};
		JButton aktual = new JButton("ID aus Clipboard");
		aktual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadDetailsFromClipboard();
			}
		});
		price = new JComboBox();

		line[0].add(new BorderPanel(att[0], "Artikelnummer", 150, 65));
		line[0].add(new BorderPanel(att[1], "Status", 150, 65));
		line[0].add(new BorderPanel(att[2], "Durchschn. Bewertung", 150, 65));
		line[0].add(new BorderPanel(att[3], "Verkaufsrang", 150, 65));
		line[0].add(new BorderPanel(att[4], "Verfuegbarkeit", 150, 65));
		line[1].add(new BorderPanel(price, "Preis", 150, 65));
		line[1].add(new BorderPanel(att[5], "Produkttyp", 150, 65));
		line[1].add(new BorderPanel(att[6], "Produktbezeichnung", 300, 65));
		line[1].add(aktual);
		line[2].add(new BorderPanel(detailinfos, "Detailinfos", 500, 600));
		line[2].add(new BorderPanel(picture, "Bild", 500, 600));

		this.table = new Table();
		this.table.createTable(detailinfos);
	}

	private void setDetails(Product product) {
		List<Offer> offers = new ArrayList<Offer>();
		try {
			offers = mdi.getOffers(product);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}


		try {
			// change: Ausgabe der Asin
			att[0].setText("" + product.getAsin());
		} catch (Exception e) {
			att[0].setText("");
		}
		att[1].setText("New");
		try {
			att[2].setText(product.getAvgRating().toString());
		} catch (Exception e) {
			att[2].setText("");
		}
		try {
			att[3].setText(product.getSalesRank().toString());
		} catch (Exception e) {
			att[3].setText("");
		}

		att[4].setText(product.isAvailable() ? "YES" : "NO");
		if (product.isAvailable()) att[4].setBackground(java.awt.Color.GREEN);
		else att[4].setBackground(java.awt.Color.RED);
		att[5].setText(product.isBook() ? "Buch" : product.isDvd() ? "DVD" : product.isMusic() ? "Musik" : "unbek.");
		try {
			att[6].setText(product.getTitle());
		} catch (Exception e) {
			att[6].setText("");
		}

		try {
			URL url = new URL(product.getPicUrl());
			ImageIcon pic = new ImageIcon(url);
			JLabel lpic = new JLabel(pic);
			//change: damit die Bilder nur einmal angezeigt werden
			picture.removeAll();
			picture.add(lpic);
		} catch (Exception e) {
		}

		price.removeAllItems();
		for (Offer offer : offers) {
			price.addItem(String.format("%.2f %s %s", offer.getPrice(), offer.getCurrency(), offer.getLocation()));
		}
		price.updateUI();

		String[] header = null;
		List<String[]> data = new ArrayList<String[]>();
		switch (product.getType()) {
			case book:
				header = new String[]{"ISBN", "Seiten", "Publikation", "Verleger", "Autor"};
				media.definitions.Book book = mdi.getBook(product.getAsin());
				String[] dumpb = new String[5];
				try {
					dumpb[0] = book.getIsbn();
				} catch (Exception e) {
				}
				try {
					dumpb[1] = book.getPages().toString();
				} catch (Exception e) {
				}
				try {
					dumpb[2] = book.getPubDate().toString();
				} catch (Exception e) {
				}
				try {
//					dumpb[3] = book.getPublishers().iterator().next().getName();
					System.out.println("%%%%%%%%%%%%%%%% pub: "+book.getPublisher());
					dumpb[3] = book.getPublisher();
				} catch (Exception e) {
				}
				try {
					Iterator<Author> it = book.getAuthors().iterator();
					int i =0;
					dumpb[4]="";
					while(it.hasNext()){
						dumpb[4] += it.next().getName();
						if(i!=book.getAuthors().size()-1)
							dumpb[4]+=" ; ";
						i++;
					}
				} catch (Exception e) {
				}
				data.add(dumpb);
				break;


			case dvd:
				header = new String[]{"Format", "Region", "Laufzeit", "Actor", "Creator", "Direktor"};
				media.definitions.DVD dvd = mdi.getDVD(product.getAsin());
				String[] dumpd = new String[6];
				try {
					dumpd[0] = dvd.getFormat();
				} catch (Exception e) {
				}
				try {
					dumpd[1] = dvd.getRegionCode().toString();
				} catch (Exception e) {
				}
				try {
					dumpd[2] = dvd.getRunningTime().toString();
				} catch (Exception e) {
				}
				try {
					dumpd[3] = getRole(dvd.getPersons(),"actor");
				} catch (Exception e) {
				}
				try {
					dumpd[4] = getRole(dvd.getPersons(),"creator");
				} catch (Exception e) {
				}
				try {
					dumpd[5] = getRole(dvd.getPersons(),"director");
				} catch (Exception e) {
				}
				data.add(dumpd);
				break;


			case music:
				header = new String[]{"Release", "Tracks", "Labels", "Artist"};
				media.definitions.Music music = mdi.getMusic(product.getAsin());
				String[] dumpm = new String[4];
				try {
					dumpm[0] = Integer.toString(music.getReleaseDate());
				} catch (Exception e) {
				}
				try {
					Set<media.definitions.Track> tracks=music.getTracks();
					Iterator<Track> it = tracks.iterator();
					dumpm[1] ="";
					int i = 0;
					while(it.hasNext()){
						dumpm[1] += it.next();
						i++;
						if(i<=tracks.size()-1)
							dumpm[1]+= " ; ";
					}
//					for(int j=0;j<tracks.size();j++){
//							if (j != 0) dumpm[1] += ", " + tracks.get(j).getName();
//							else dumpm[1] = tracks.get(j).getName();
//					}
					/*
					 String[] tracks = music.getTracks().toArray(new String[0]);
					 

					for (int i = 0; i < tracks.length; i++)
						if (i != 0) dumpm[1] += ", " + tracks[i];
						else dumpm[1] = tracks[0];*/
				} catch (Exception e) {
				}
				try {
					dumpm[2] = music.getLabel();
				} catch (Exception e) {
				}
				try {
					dumpm[3] = music.getArtists().iterator().next().getName();
				} catch (Exception e) {
				}
				data.add(dumpm);
				break;

		}

		this.table.fillTable(header, data);
	}


	private String getRole(Set<Person> persons, String string) {
		Iterator<Person> it = persons.iterator();
		Person p=null;
		while(it.hasNext()){
			p = it.next();
			if(p.getRole().equals(string))
				return p.getName();
		}
		return null;
	}


	/**
	 * Realisierung des rebuild-Knopfes. Die Daten werden aus der Zwischenablage aktualisiert.
	 */
	public void loadDetailsFromClipboard() {
		java.awt.datatransfer.Transferable transferred = this.clipboard.getContents(null);
		java.awt.datatransfer.DataFlavor flavors[] = transferred.getTransferDataFlavors();
		java.awt.datatransfer.DataFlavor flavor = flavors[0];

		try {
			String content = transferred.getTransferData(flavor).toString();

			String productId = content;

			if (productId == null) return;

			Product product = null;
			try {
				product = mdi.getProduct(productId);
				if (product != null) {
					setDetails(product);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
				return;
			}
		} catch (Exception e) {
		}
	}

	public void loadDetailsFromProduct(Product product) {
		setDetails(product);
	}
}
