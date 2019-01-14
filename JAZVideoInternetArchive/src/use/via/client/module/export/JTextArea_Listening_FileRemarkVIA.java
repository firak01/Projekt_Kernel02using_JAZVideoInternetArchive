package use.via.client.module.export;

import java.io.File;
import java.util.Vector;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelZZZ;

public class JTextArea_Listening_FileRemarkVIA extends JTextAreaListening4ComponentSelectionResetVIA{
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird. Das ist das Eingestllte Laufwerk. 
	//Merke: Wenn ohne Verzeichnisbaum schon eine Datei gew�hlt wurde, sorgt die Belegung dieser Variable daf�r, dass kein "ungwolltes" Leersetzen passiert.
	
	public JTextArea_Listening_FileRemarkVIA(IKernelZZZ objKernel, String sTextInitial, int iRow, int iColumn) {
		super(objKernel, sTextInitial, iRow, iColumn);		
	}
	
	/* (non-Javadoc)
	 * Es wird ggf. ein Kommentar aus dem Filenamen ausgelesen
	 * @see use.via.client.module.export.JTextAreaListening4ComponentSelectionResetVIA#fileChanged(use.via.client.module.export.EventListFileSelectedVIA)
	 */
	public void fileChanged(EventListFileSelectedVIA eventFileSelected) {
		main:{
			String sReturnUI="";
			try {
				File file = eventFileSelected.getFile();
				if(file==null){
				   this.setText(JText_Listening_FileNameVIA.sTEXT_ERROR);
				   break main;
				}
				
				
				//zum Setzen des Root-Strings (Merke: Es wird als Workaround die Dateiliste des Roots des Laufwerks schon angezeigt, bevor der Verzeichnisbaum erstellt ist)
				//Wenn ohne Verzeichnisbaum schon eine Datei gew�hlt wurde, sorgt die Belegung dieser Variable daf�r, dass kein "ungwolltes" Leersetzen passiert.
				String stemp = FileEasyZZZ.getRoot(file) + File.separator;
				this.sRootPrevious = stemp; 
				
				/* Fgl 20080111 der Bemerkungsteil einer Datei, wird in die Textarea zur "Dateibemerkung" geschrieben.*/
//				Der Default-Titel ist der Dateiname OHNE Endung			
				//Hier sollen ggf. noch Kommentare und Bemerkungen ausgefiltert werden
				File fileCatalogSerie = this.getKernelObject().getParameterFileByProgramAlias("use.via.client.FrmMainVIA","Export_Context", "CatalogSerieTitleFilename");	
				Vector vecTemp = CommonUtilVIA.computeMovieDetailByFile(file, fileCatalogSerie);
				sReturnUI=(String)vecTemp.get(2);
				
			} catch (ExceptionZZZ ez) {
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());				
				sReturnUI = JTextArea_Listening_FileRemarkVIA.sTEXT_ERROR;
			}
			
			//Nun wird ein ggf. gefundener Kommentar angezeigt   this.setText(JTextAreaListening4ComponentSelectionResetVIA.sTEXT_INITIAL);
			this.setText(sReturnUI);			
		}//end main:
	}
}
