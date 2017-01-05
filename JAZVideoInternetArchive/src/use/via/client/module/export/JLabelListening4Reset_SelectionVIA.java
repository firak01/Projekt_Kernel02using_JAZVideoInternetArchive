package use.via.client.module.export;

import java.io.File;

import basic.zKernel.KernelZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJLabelListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JLabelListening4Reset_SelectionVIA extends KernelJLabelListening4ComponentSelectionResetZZZ implements IListenerFileSelectedVIA{
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	
	
	public JLabelListening4Reset_SelectionVIA(KernelZZZ objKernel, String sTextInitial) {
		super(objKernel, sTextInitial);		
	}

	public void fileChanged(EventListFileSelectedVIA eventFileSelected) {
		main:{
			try {
				File file = eventFileSelected.getFile();
				if(file==null){
				   this.setText("No file-object provided by event");
				   break main;
				}
				
				//zum Setzen des Root-Strings (Merke: Es wird als Workaround die Dateiliste des Roots des Laufwerks schon angezeigt, bevor der Verzeichnisbaum erstellt ist)
				//Wenn ohne Verzeichnisbaum schon eine Datei gewählt wurde, sorgt die Belegung dieser Variable dafür, dass kein "ungwolltes" Leersetzen passiert.
		
					this.sRootPrevious = FileEasyZZZ.getRoot(file) + File.separator;
				
				
				//
				String sFilePathTotal = file.getPath();
				this.setText(sFilePathTotal);
			} catch (ExceptionZZZ e) {
				ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
			}
		}//end main:
	}
	
	/* (non-Javadoc)
	 * @see basic.zKernelUI.component.model.IListenerSelectionResetZZZ#doReset(basic.zKernelUI.component.model.EventComponentSelectionResetZZZ)
	 * 
	 * Überscrheibt die gleichnamige Methode der Standard KernelJLabelListening4ResetZZZ-Komponente
	 */
	public void doResetCustom(EventComponentSelectionResetZZZ event) {
		main:{
			String sRootNew = event.getComponentText();
			if(StringZZZ.isEmpty(sRootNew)) break main;
			
			if(sRootNew.equals(this.sRootPrevious)) break main;
			this.sRootPrevious = sRootNew;
									
//			Wenn das nicht überschreiben wird, so bekommt man vom JTree das Verzeichnis geliefert
			this.setText("Nothing selected");
		}//end main				
	}
}
