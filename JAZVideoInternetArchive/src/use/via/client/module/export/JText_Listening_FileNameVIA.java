package use.via.client.module.export;

import java.io.File;
import java.util.HashMap;

import basic.zKernel.KernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJTextFieldListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JText_Listening_FileNameVIA  extends KernelJTextFieldListening4ComponentSelectionResetZZZ implements IListenerFileSelectedVIA{
	public static String sTEXT_INITIAL = "No file selected";
	public static String sTEXT_ERROR = "No file-object provided by event";
	
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	
	public JText_Listening_FileNameVIA(KernelZZZ objKernel, String sTextInitial) {
		super(objKernel, sTextInitial);
	}

	public void fileChanged(EventListFileSelectedVIA eventFileSelected) {
		main:{
			try{
				File file = eventFileSelected.getFile();
				if(file==null){
				   this.setText(JText_Listening_FileNameVIA.sTEXT_ERROR);
				   break main;
				}
				
				//zum Setzen des Root-Strings (Merke: Es wird als Workaround die Dateiliste des Roots des Laufwerks schon angezeigt, bevor der Verzeichnisbaum erstellt ist)
				//Wenn ohne Verzeichnisbaum schon eine Datei gewählt wurde, sorgt die Belegung dieser Variable daf�r, dass kein "ungwolltes" Leersetzen passiert.
				this.sRootPrevious = FileEasyZZZ.getRoot(file) + File.separator;
							
				//Dateiname
				String sName = file.getName();
				this.setText(sName);
				this.setCaretPosition(0); //Falls ein String l�nger ist, dann wird wieder nach vorne gescrollt
			}catch(ExceptionZZZ ez){
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			}
			
		}//End main:	
	}
	public void doResetCustom(EventComponentSelectionResetZZZ event){
		main:{
			String sRootNew = event.getComponentText();
			if(StringZZZ.isEmpty(sRootNew)) break main;
			
			if(sRootNew.equals(this.sRootPrevious)) break main;
			this.sRootPrevious = sRootNew;
			ReportLogZZZ.write(ReportLogZZZ.INFO, "Root previous = " + this.sRootPrevious);
			
						
			//Wenn das nicht �berschreiben wird, so bekommt man vom JCombo das Laufwerk geliefert
			this.setText(JText_Listening_FileNameVIA.sTEXT_INITIAL);
		}//end main
	}
}//End class
