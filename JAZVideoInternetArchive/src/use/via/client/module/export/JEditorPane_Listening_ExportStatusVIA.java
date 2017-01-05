package use.via.client.module.export;

import java.io.File;

import basic.zKernel.KernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJEditorPaneListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JEditorPane_Listening_ExportStatusVIA extends KernelJEditorPaneListening4ComponentSelectionResetZZZ  implements IListenerFileSelectedVIA{
	private String sRootPrevious=null;
	
	public JEditorPane_Listening_ExportStatusVIA(KernelZZZ objKernel, String sInitial){
		super(objKernel, sInitial);
	}
	
	public void fileChanged(EventListFileSelectedVIA eventFileSelected) {
		String sReturnUI = "";
		main:{
			try {
				File file = eventFileSelected.getFile();
				if(file==null){
				   sReturnUI = "No file-object provided by event";
				   break main;
				}
				
				//	zum Setzen des Root-Strings (Merke: Es wird als Workaround die Dateiliste des Roots des Laufwerks schon angezeigt, bevor der Verzeichnisbaum erstellt ist)
					//Wenn ohne Verzeichnisbaum schon eine Datei gewählt wurde, sorgt die Belegung dieser Variable dafür, dass kein "ungwolltes" Leersetzen passiert.
					this.sRootPrevious = FileEasyZZZ.getRoot(file) + File.separator;
				} catch (ExceptionZZZ e) {
					ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
				}			
		}//End main:	
		this.setText(sReturnUI);
	}

	public void doResetCustom(EventComponentSelectionResetZZZ eventSelectionResetNew) {
		this.setText("");
	}

}
