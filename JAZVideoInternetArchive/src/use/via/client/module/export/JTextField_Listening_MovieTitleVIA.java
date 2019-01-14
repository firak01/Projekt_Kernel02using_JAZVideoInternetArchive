package use.via.client.module.export;

import java.io.File;
import java.util.Vector;

import basic.zKernel.IKernelZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJTextFieldListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JTextField_Listening_MovieTitleVIA  extends KernelJTextFieldListening4ComponentSelectionResetZZZ implements IListenerFileSelectedVIA{
	public static String sTEXT_ERROR = "Error: ";
	public static String sTEXT_INITIAL= ""; 
	
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	
	/** Merke: PanelParent ist wichtig, um berechnen zu k�nnen, ob sich die eingelegte CD ge�ndert hat.
	* lindhauer; 16.08.2008 11:44:25
	 * @param objKernel
	 * @param panelParent
	 * @param sTextInitial
	 */
	public JTextField_Listening_MovieTitleVIA(IKernelZZZ objKernel, String sTextInitial) {
		super(objKernel, sTextInitial);
	}

	public void fileChanged(EventListFileSelectedVIA eventFileSelected) {
		String sReturnUI="";
		main:{
			try{
				File file = eventFileSelected.getFile();
				if(file==null){
				   sReturnUI = "No file-object provided by event";
				   break main;
				}
				
	//			zum Setzen des Root-Strings (Merke: Es wird als Workaround die Dateiliste des Roots des Laufwerks schon angezeigt, bevor der Verzeichnisbaum erstellt ist)
				//Wenn ohne Verzeichnisbaum schon eine Datei gew�hlt wurde, sorgt die Belegung dieser Variable daf�r, dass kein "ungwolltes" Leersetzen passiert.
				this.sRootPrevious = FileEasyZZZ.getRoot(file) + File.separator;
				
			//Der Default-Titel ist der Dateiname OHNE Endung			
			//Er soll aber noch hinsichtlich der Unterstriche verarbeitet werden.
			File fileCatalogSerie = this.getKernelObject().getParameterFileByProgramAlias("use.via.client.FrmMainVIA","Export_Context", "CatalogSerieTitleFilename");	
			Vector vecTemp = CommonUtilVIA.computeMovieDetailByFile(file, fileCatalogSerie);
			sReturnUI=(String)vecTemp.get(1);
			
			
			}catch(ExceptionZZZ ez){
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());				
				sReturnUI = JTextField_Listening_MovieTitleVIA.sTEXT_ERROR;
			}
		}//End main:	
		this.setText(sReturnUI);
	}
	public void doResetCustom(EventComponentSelectionResetZZZ event){	
		main:{
			String sRootNew = event.getComponentText();
			if(StringZZZ.isEmpty(sRootNew)) break main;

			if(sRootPrevious==null && StringZZZ.isEmpty(this.getText())==false) break main; //dadurch soll verhindert werden, dass gerade eingegebener Text gel�scht wird, auch wenn z.B. noch keine Datei markiert worden ist.
			 if(sRootNew.equals(this.sRootPrevious)) break main;
			
			this.sRootPrevious = sRootNew;
			ReportLogZZZ.write(ReportLogZZZ.INFO, "Root previous = " + this.sRootPrevious);
			
			this.setText(JTextField_Listening_MovieTitleVIA.sTEXT_INITIAL);
		}//end main		
	}
}//End class


