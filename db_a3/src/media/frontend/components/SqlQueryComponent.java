package media.frontend.components;

import media.definitions.MediaDbInterface;
import media.definitions.SQLResultInterface;
import media.frontend.utils.BorderPanel;
import media.frontend.utils.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Peitzsch, Silvio Paschke, Stefan Endrullis
 */
public class SqlQueryComponent extends JComponent implements Component {

	private Table table = null;

	final static long serialVersionUID = "media.frontend.components.SqlQueryComponent"
			.hashCode();

	/*
	 * mdi enthaelt MediaDbImpl mit deren Hilfe mit DB kommuniziert wird
	 */
	private MediaDbInterface mdi;

	private JTextField sqlQuery = new JTextField();

	/**
	 * @param mdi
	 */
	public SqlQueryComponent(MediaDbInterface mdi) {
		super();
		this.mdi = mdi;

		initComponent();
	}


	public void initComponent() {

		this.setLayout(new BorderLayout());
		// textfeld fuer die queryeingabe und die umrandung erzeugen

		JComponent component = new JPanel();

		component.setLayout(new BorderLayout());
		component.add(sqlQuery);// textfeld mit listener versehen
		sqlQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queryDb(e);
			}
		});
		add(new BorderPanel(component, "SQL", 0, 0), BorderLayout.NORTH);
		// panel fuer die tabelle
		component = new JPanel();
		component.setLayout(new BorderLayout());

		this.table = new Table();
		this.table.createTable(component);
		this.add(new BorderPanel(component, "Ausgabe", 0, 0, true), BorderLayout.CENTER);

//		this.add(new Label("blaaa"));
		
	}

	private void queryDb(ActionEvent event) {
		String query = sqlQuery.getText();

		String[] meta;
		List<String[]> data = new ArrayList<String[]>();

		try {
			SQLResultInterface res = mdi.executeSqlQuery(query);

			if (res == null)
				return;
			List<String> header = res.getHeader();
			if (header == null)
				meta = new String[]{"Fehler"};
			else {
				meta = new String[header.size()];
				for (int i = 0; i < header.size(); i++)
					meta[i] = header.get(i);
			}

			String[] row;
			while((row = res.getNextRow())!=null){
				data.add(row);
			}

			if (data.size() == 0)
				data.add(new String[]{"Leerer Tabellenkoerper"});

			this.table.fillTable(meta, data);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			data.clear();
			data.add(new String[]{e.toString()});
			table.fillTable(new String[]{"Fehler"}, data);
		}
	}
}
