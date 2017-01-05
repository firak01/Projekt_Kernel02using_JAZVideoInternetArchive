package use.via.client.module.export;

import java.io.File;
import java.util.EventObject;

/**Diesr Event wird im Listener für die Auswahl aus der JList Komponente (, welche die Dateinamen enthält) erzeugt.
 *  und abgefeuert (also in: ListenerFileListSelectionVIA.valueChanged())
 *  
 * @author lindhaueradmin
 *
 */
public class EventListFileSelectedVIA extends EventObject{
	protected int id;
	protected File file;
	
	public EventListFileSelectedVIA(Object source, int id, File file){
		super(source);
		this.id = id;
		this.file = file;
	}
	
	int getID(){
		return this.id;
	}
	File getFile(){
		return this.file;
	}
}
