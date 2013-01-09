package media.frontend;

import javax.swing.*; // Der Rest

import media.definitions.MediaDbInterface;
import media.frontend.components.*;

//import com.jgoodies.plaf.FontSizeHints;
//import com.jgoodies.plaf.Options;

import java.awt.*; // Fuer Layouts, Tables
import java.io.*;
import java.util.Properties;
import java.util.logging.LogManager; // Fuer PropertieFile lesen

/**
 * Main application for viewing media data using middleware to
 * access the data.
 * @author Alrik Hausdorf (eine Änderungen damit es läuft)
 */
public class Media extends JPanel {
	Properties prop = new Properties();
	/*
	 * mdi enthaelt MediaDbImpl (Media Data Base Implementation) mit deren
	 * Hilfe mit DB kommuniziert wird
	 */


	final static long serialVersionUID = "media.frontend.Media".hashCode();

	MediaDbInterface mdi;

	public Media(String propertiesFile, JFrame window) {
		super();
		ImageIcon icon;
		//  Properties - File laden
		try {
			prop.load(new FileInputStream(propertiesFile));
		}
		catch (Exception e) {
			System.err.println("Konnte Einstellungen aus " +
					propertiesFile + " nicht laden.");
			System.exit(2);
		}
		String value = prop.getProperty("modul").trim();
		// Neue MediaDbImpl - Objekt anlegen,
		// ueber welches mit der DB kommuniziert wird
		try {
			Class c = Class.forName(value);
			mdi = (MediaDbInterface) c.newInstance();
			mdi.init(prop);
		}
		catch (Exception e) {
			System.err.println("Konnte Klasse " + value + " nicht laden. ("
					+ e + ")");
			e.printStackTrace(System.err);
			System.exit(3);
		}

		window.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				mdi.finish();
				System.exit(0);
			}
		});
//		window.setFont(new Font("Verdana", Font.BOLD, 12));
//		window.setAlwaysOnTop(true);
//		window.setBackground(new Color(33, 66, 99));

		JTabbedPane tabbedPane = new JTabbedPane();

		String[] comp_names =
				{
						"SQL",//fragezeichen
						"HQL",//fragezeichen
						"Produktsuche",
						"Detailinfos",
						"Rezensionen",
						"Kategorien"
				};

		String[] comp_descr =
				{
						"SQL-Anfrageergebnisse in Tabellenform",
						"HQL-Anfrageergebnisse in Tabellenform",
						"Erlaubt das Finden von Produkten",
						"Detailinformationen ueber ein Produkt",
						"Artikelbeschreibungen",
						"Erkunden des Kategorienbaums"
				};

		String[] icons =
				{
						"media/frontend/figures/frage.gif", "media/frontend/figures/frage.gif", "", "", "", ""
				};

		//laden der komponenten
		Details details = new Details(mdi);
		JComponent[] components =
				{
						new SqlQueryComponent(mdi),
						new HqlQueryComponent(mdi),
						new SearchComp(mdi, details),
						details,
						new Reviews(mdi, details),
						new Categories(mdi, details)
				};

		JComponent[] panels = new JComponent[components.length];
		for (int i = 0; i < components.length; i++) {
			panels[i] = components[i];
//			panels[i].setBackground(new Color(33, 66, 99));
//			panels[i].setFont(new Font("Verdana", Font.BOLD, 12));
			panels[i].setName(comp_descr[i]);
		}
		;

		// Erzeuge zunaechst alle Komponenten

		for (int i = 0; i < panels.length; i++) {
			icon = new ImageIcon(icons[i]);

			tabbedPane.addTab(comp_names[i], icon, panels[i],
					comp_descr[i]);
		}
		tabbedPane.setSelectedIndex(0);
		tabbedPane.setName("KarteikartManuela Lentzschenkasten");

		setLayout(new GridLayout(1, 1));
		add(tabbedPane);
		this.setName("Anwendung");
//    UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
//    Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
//    Options.setUseNarrowButtons(true);
//    Options.setUseSystemFonts(false);
//    
//    try{
//        UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
//    }catch(Exception e){
//        e.printStackTrace(System.err);
//    }
		// tabbed pane added


	}


	public static void main(String[] args) throws IOException {
		// logging setup
		LogManager.getLogManager().readConfiguration();
		//Lese String aus Definierer Datei (kommentieren für Übergabe der File als Startparameter)
		String propertiesFile = "src/media/config/media.ini";
		//Lese String aus Übergabe als Startparameter (auskommentieren für fest Definierte Datei)
		//String propertiesFile = args[0];

		// Create the Window
		JFrame frame = new JFrame("Media Applet");
		Media media = new Media(propertiesFile, frame);
		try {
			frame.getContentPane().add(media, BorderLayout.CENTER);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		ImageIcon icn = new ImageIcon("media/frontend/figures/wand.gif");
		frame.setIconImage(icn.getImage());
		frame.setSize(1000, 500);
		frame.setVisible(true);
		Dimension paneSize = frame.getSize();
		Dimension screenSize = frame.getToolkit().getScreenSize();
		frame.setLocation((screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);

		// Window created
	}
}