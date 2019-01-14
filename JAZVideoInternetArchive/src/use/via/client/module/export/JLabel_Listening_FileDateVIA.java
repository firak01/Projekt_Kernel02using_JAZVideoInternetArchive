package use.via.client.module.export;

import java.io.File;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJLabelListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;
import basic.zKernel.IKernelZZZ;

public class JLabel_Listening_FileDateVIA   extends KernelJLabelListening4ComponentSelectionResetZZZ implements IListenerFileSelectedVIA{
	public static final String sTEXT_INITIAL = "";
	
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	
	
	public JLabel_Listening_FileDateVIA(IKernelZZZ objKernel, String sTextInitial) {
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
				//Wenn ohne Verzeichnisbaum schon eine Datei gew�hlt wurde, sorgt die Belegung dieser Variable daf�r, dass kein "ungwolltes" Leersetzen passiert.
				this.sRootPrevious = FileEasyZZZ.getRoot(file) + File.separator;
							
				//Datum der letzten �nderung
				String sDateLastModified = CommonUtilVIA.computeDateLastModifiedByFile(file);
				this.setText(sDateLastModified);
			} catch (ExceptionZZZ e) {
				ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
			}
		}//End main:	
	}
	public void doResetCustom(EventComponentSelectionResetZZZ event){
		main:{
			String sRootNew = event.getComponentText();
			if(StringZZZ.isEmpty(sRootNew)) break main;
			
			if(sRootNew.equals(this.sRootPrevious)) break main;
			this.sRootPrevious = sRootNew;
									
			//Wenn das nicht �berschreiben wird, so bekommt man vom JTree das Verzeichnis geliefert
			this.setText(JLabel_Listening_FileSizeVIA.sTEXT_INITIAL);
		}//end main
	}
}//End class
