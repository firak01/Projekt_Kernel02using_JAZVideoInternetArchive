package use.via.server;

import java.util.Vector;

import org.apache.commons.collections.map.MultiValueMap;

import custom.zNotes.kernel.KernelNotesZZZ;

import basic.zBasic.ExceptionZZZ;
import lotus.domino.Document;
import lotus.domino.Session;

/**TODO: Diese Klasse sollte in das Notes-Package verschoben werden, da sie allgemein die Erstellung von Notesdocumenten behandelt
 * @author 0823
 *
 */
public interface ICategorizableZZZ {
	
	/**Lege die Feldnamen des  Dokuments fest, die in das "Category+ApplicationKey"-Feld geschrieben werden soll.
	 * Z.B: der Dateiname eines Dateidokuments, wenn der Dateiname als Wert nach dem kategoriesiert werden soll in den Ansichten auftauchen soll. 
	 *  
	 *  MERKE: Das erste Element dieses Vectors wird defaultmäßig in den Ansichten angezeigt. Es sollte also z.B. der Titel sein.
	 *  
	 *  TODO: Momentan sind das noch die echten Feldnamen.
	 *              Besser für die Konfiguration wäre es hier die Alias-Bezeichnungen (s. MapperStore) zu verwenden.
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 16.02.2007 10:05:42
	 */
	public Vector getCategoryAllUsed();

	/** Hänge das Category und das Alias -Feld in das Dokument
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 16.02.2007 10:04:18
	*/
	public void appendFieldAllToBecomeCategorizable() throws ExceptionZZZ;

}
