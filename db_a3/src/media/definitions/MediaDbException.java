package media.definitions;

/** Ausnahmebehandlung fuer Operationen der MediaDb-Zugriffsschicht. */
public class MediaDbException extends Exception {

  /** Konstruktor mit spezifizierter Fehlermeldung
   * @param msg  detailierte Fehlermeldung
   */
	public MediaDbException(String msg) {super (msg); }
}
