/**
 * Diese Klasse erzeugt JTables
 */
package media.frontend.utils;

import java.util.List;
import java.awt.*;

import javax.swing.*;
import java.awt.datatransfer.*;
import javax.swing.table.*;


/**
 * @author Silvio Pascke, Stefan Endrullis
 */
public class Table implements ClipboardOwner {

	private JTable table = null;
	private DefaultTableModel model = null;
	private String[] header = null;

	private Clipboard sysClip =
			Toolkit.getDefaultToolkit().getSystemClipboard();

	private JComponent frame = null;

	private boolean created = false;
	private boolean empty = true;


	/**
	 * Erzeugt Tabelle im uebergebenen Rahmen.
	 *
	 * @param frame Rahmen
	 */
	public void createTable(JComponent frame) {
		this.model = new DefaultTableModel();
		this.table = new javax.swing.JTable(this.model);
		this.frame = frame;
//		this.table.setFont(new Font("Verdana", Font.BOLD, 11));
//		this.table.setBackground(new Color(200, 200, 200));
//		this.table.setForeground(new Color(10, 10, 100));
		this.table.setAutoCreateColumnsFromModel(true);
		this.table.setCellSelectionEnabled(false);
		frame.add(this.table.getTableHeader(), BorderLayout.NORTH);//tabellenkopf
		JScrollPane pane = new JScrollPane(table);
		pane.setAutoscrolls(true);
		frame.add(pane, BorderLayout.CENTER);
		this.created = true;
		this.table.addKeyListener(new java.awt.event.KeyListener() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() != java.awt.event.KeyEvent.VK_ENTER) return;
				fillClipboard();
			}

			public void keyReleased(java.awt.event.KeyEvent e) {
			}

			public void keyTyped(java.awt.event.KeyEvent e) {
			}
		});

		this.table.addMouseListener(new java.awt.event.MouseListener() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
			}

			public void mouseReleased(java.awt.event.MouseEvent e) {
			}

			public void mouseEntered(java.awt.event.MouseEvent e) {
			}

			public void mousePressed(java.awt.event.MouseEvent e) {
				if (e.getButton() != java.awt.event.MouseEvent.BUTTON1 ||
						e.getClickCount() != 2) return;
				fillClipboard();
			}

			public void mouseExited(java.awt.event.MouseEvent e) {
			}
		});
	}


	/**
	 * Fuellt die Tabelle.
	 *
	 * @param metadata
	 * @param data
	 */
	public void fillTable(String[] metadata, List<String[]> data) {
		if (!this.created) {
			System.err.println("Keine Tabelle erzeugt. Kann diese demnach nicht fuellen. Abbruch.");
			return;
		}
		this.header = metadata.clone();
		if (!this.empty) {
			clearTable();
		}
		if (metadata == null || data == null || metadata.length == 0) {
			System.err.println("Fehlerhafte Uebergabe bei fillTable. Abbruch.");
			return;
		}
		// Spaltenueberschrift erzeugen
		try {
			for (int i = 0; i < metadata.length; i++) model.addColumn(metadata[i]);
		} catch (Exception e) {
			System.err.println("Fehlerhafte Metadaten. Abbruch.");
			return;
		}

		// Daten einspeisen
		try {
			for (int i = 0; i < data.size(); i++) { // alle Zeilen
				Object[] dump = (Object[]) data.get(i);
				String[] row = new String[dump.length];
				for (int j = 0; j < metadata.length; j++)
					if (dump[j] != null) row[j] = dump[j].toString(); //in Stringarray
					else row[j] = "";
				this.model.addRow(dump);
			}
		} catch (Exception e) {
			System.err.println("Fehlerhafte Daten. Abbruch.");
		}
		this.empty = false;


	}


	/**
	 * Entfernt den Inhalt der Tabelle.
	 */
	public void clearTable() {
		this.model.setRowCount(0);
		this.model.setColumnIdentifiers((Object[]) null);
		this.empty = true;
	}


	public void resizeColumns(int[] widths) {
		int cols = widths.length;
		int mcol = this.table.getColumnModel().getColumnCount();
		if (cols > mcol) {
			System.err.println("resizeColumns kann nicht angewendet werden," +
					"da zuviele Werte uebergeben wurden.");
		}
		for (int i = 0; i < cols; i++)
			this.table.getColumnModel().getColumn(i).setWidth(widths[i]);
	}

	private void fillClipboard() {
		if (this.header.length != 3 ||
				this.header[1] != "Artikelnummer" ||
				this.header[2] != "Artikelbezeichnung") return;

		int prod_id = -1;
		try {
			prod_id = Integer.parseInt(this.table.getValueAt(this.table.getSelectedRow(), 1).toString());
		} catch (Exception e) {
		}
		if (prod_id == -1) return;
		StringSelection cont = new StringSelection(Integer.toString(prod_id));
		this.sysClip.setContents(cont, this);
	}

	/**
	 * Kompatibl. impl.
	 *
	 * @param clip
	 * @param cont
	 */
	public void lostOwnership(Clipboard clip, Transferable cont) {
	}

	public JTable getTable() {
		return table;
	}
}
