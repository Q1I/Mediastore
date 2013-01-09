package media.frontend.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Panel mit Rahmen und Titel, dass eine AWT-Komponente enthaelt.
 *
 * @author Stefan Endrullis
 */
public class BorderPanel extends JPanel {
	public BorderPanel (JComponent component, String title) {
		this(component, title, false);
	}

	public BorderPanel (JComponent component, String title, boolean scroll) {
		this.setLayout(new BorderLayout(0, 0));

		this.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)),
				component.getBorder()));

		if (scroll) {
			this.add(new JScrollPane(component), BorderLayout.CENTER);
		} else {
			this.add(component, BorderLayout.CENTER);
		}
	}

	public BorderPanel(JComponent component, String title, int maxX, int maxY) {
		this(component, title, maxX, maxY, false);
	}

	public BorderPanel(JComponent component, String title, int maxX, int maxY, boolean scroll) {
		this(component, title, scroll);

		this.setMaximumSize(new Dimension(maxX, maxY));
	}

	public BorderPanel (JComponent component, String title, int minX, int minY, int maxX, int maxY) {
		this(component, title, minX, minY, maxX, maxY, false);
	}

	public BorderPanel (JComponent component, String title, int minX, int minY, int maxX, int maxY, boolean scroll) {
		this(component, title, scroll);

		this.setMinimumSize(new Dimension(minX, minY));
		this.setMaximumSize(new Dimension(maxX, maxY));
	}
}
