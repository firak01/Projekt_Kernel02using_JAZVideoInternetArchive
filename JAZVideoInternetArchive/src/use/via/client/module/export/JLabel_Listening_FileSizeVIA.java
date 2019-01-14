package use.via.client.module.export;

import java.io.File;

import basic.zKernel.IKernelZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJLabelListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JLabel_Listening_FileSizeVIA  extends KernelJLabelListening4ComponentSelectionResetZZZ implements IListenerFileSelectedVIA{
	public static final String sTEXT_INITIAL = "0";
	
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	
	
	public JLabel_Listening_FileSizeVIA(IKernelZZZ objKernel, String sTextInitial) {
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
				
						
				//G��e der Datei in Byte
				Long lng = new Long(file.length());
				double d = lng.doubleValue() / 1024 / 1024;  // -> KB  -> MB
				
				//Nun die Anzahl der Stellen hinterm Komma auf 4 begrenzen
				Double dbl = new Double(d);
				String sDbl = dbl.toString();
				
				int itemp = sDbl.indexOf(".");
				if(itemp >= 0){
					String stemp = StringZZZ.left(sDbl, itemp + 4);
					this.setText(stemp);
				}else{
					this.setText(sDbl);
				}		
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
									
//			Wenn das nicht �berschreiben wird, so bekommt man vom JTree das Verzeichnis geliefert
			this.setText(JLabel_Listening_FileSizeVIA.sTEXT_INITIAL);
		}//end main		
	}
}//End class
