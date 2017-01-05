package use.via.client.module.export;

import java.io.File;

import basic.zKernel.KernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJTextAreaListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JTextAreaListening4ComponentSelectionResetVIA extends KernelJTextAreaListening4ComponentSelectionResetZZZ  implements IListenerFileSelectedVIA{
	public static String sTEXT_ERROR = "Error: ";
	public static String sTEXT_INITIAL= "";
	
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	
	
	public JTextAreaListening4ComponentSelectionResetVIA(KernelZZZ objKernel, String sTextInitial) {
		super(objKernel, sTextInitial);		
	}
	public JTextAreaListening4ComponentSelectionResetVIA(KernelZZZ objKernel, String sTextInitial, int iRow, int iColumn) {
		super(objKernel, sTextInitial, iRow, iColumn);		
	}
	public void fileChanged(EventListFileSelectedVIA eventFileSelected) {
		main:{
			try {
				File file = eventFileSelected.getFile();
				if(file==null){
				   this.setText(JText_Listening_FileNameVIA.sTEXT_ERROR);
				   break main;
				}
				
				//zum Setzen des Root-Strings (Merke: Es wird als Workaround die Dateiliste des Roots des Laufwerks schon angezeigt, bevor der Verzeichnisbaum erstellt ist)
				//Wenn ohne Verzeichnisbaum schon eine Datei gewählt wurde, sorgt die Belegung dieser Variable dafür, dass kein "ungwolltes" Leersetzen passiert.
				this.sRootPrevious = FileEasyZZZ.getRoot(file) + File.separator;
			} catch (ExceptionZZZ ez) {
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			}
			
			this.setText(JTextAreaListening4ComponentSelectionResetVIA.sTEXT_INITIAL);
		}//end main:
	}
	public void doResetCustom(EventComponentSelectionResetZZZ event){	
		main:{
			String sRootNew = event.getComponentText();
			if(StringZZZ.isEmpty(sRootNew)) break main;
			
			//if(sRootPrevious==null && StringZZZ.isEmpty(this.getText())==false) break main; //dadurch soll verhindert werden, dass gerade eingegebener Text gelöscht wird, auch wenn z.B. noch keine Datei markiert worden ist.
			//FGL: 20080816 Darf nicht sein, sonst bekommt man den Wechsel einer CD nicht mit		if(sRootNew.equals(this.sRootPrevious)) break main;
			this.sRootPrevious = sRootNew;
			
			this.setText(JTextAreaListening4ComponentSelectionResetVIA.sTEXT_INITIAL);
		}//end main:
	}
}
