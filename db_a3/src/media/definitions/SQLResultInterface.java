package media.definitions;

import java.util.List;

/** Schnittstelle fuer die Uebergabe von SQL-Anfrageergebnissen. */
public interface SQLResultInterface {
  /** Liefert eine Liste aller Spalten der Ergebnismenge.
   * @return Liste von Ergebnisspalten; die Listenobjekte sind vom Typ String;
   *         die Reihenfolge entspricht der Reihenfolge der Ergebnisspalten
   *         des ResultSet-Objektes
   * @exception MediaDbException  wenn ein Fehler auftrat
   *                            (z.B. java.sql.SQLException)
   */
	public List<String> getHeader() throws MediaDbException;

  /** Liefert den naechsten Ergebnisdatensatz.
   * @return einen kompletten Ergebnisdatensatz (alle Attribute der Anfrage);
   *         die Listenobjekte sind vom Typ String; die Reihenfolge entspricht
   *         der Reihenfolge der Spalten im ResultSet-Objekt;
   *         wenn kein weiterer Ergebnissatz existiert, wird <code>null</code>
   *         zurueckgegeben
   * @exception MediaDbException  wenn ein Fehler auftrat
   *                            (z.B. java.sql.SQLException)
   */
  public String[] getNextRow() throws MediaDbException;
}
