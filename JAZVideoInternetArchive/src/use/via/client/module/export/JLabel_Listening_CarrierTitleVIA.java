package use.via.client.module.export;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import basic.zKernel.KernelZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJLabelListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JLabel_Listening_CarrierTitleVIA  extends KernelJLabelListening4ComponentSelectionResetZZZ implements IListenerFileSelectedVIA{
	public static String sTEXT_INITIAL = "No drive selected";
	public static String sTEXT_NO_DRIVE = "No disk in drive";
	public static String sTEXT_ERROR = "No correct root provided by event";
	
	private String sRootPrevious=null;
	private String sTextPrevious = null;
	
	public JLabel_Listening_CarrierTitleVIA(KernelZZZ objKernel, String sTextInitial) {
		super(objKernel, sTextInitial);
	}
	
	public String getTextPrevious(){
		return this.sTextPrevious;
	}

	/* (non-Javadoc)
	 * @see basic.zKernelUI.component.KernelJLabelListening4ComponentSelectionResetZZZ#doResetCustom(basic.zKernelUI.component.model.EventComponentSelectionResetZZZ)
	 * 
	 * Bei der Änderung des Laufwerks in der Combo-Box den Datenträgertitel ändern
	 */
	public void doResetCustom(EventComponentSelectionResetZZZ event){	
		String sReturnUI = new String(JLabel_Listening_CarrierTitleVIA.sTEXT_NO_DRIVE);
		main:{	
			try{
				
				String sDrive = event.getComponentText();				
				if(StringZZZ.isEmpty(sDrive)){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "EventComponentSelectionResetZZZ received: sDrive=Empty");
					break main;
				}else{
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "EventComponentSelectionResetZZZ received: sDrive="+ sDrive);
				}
				
				File fileRoot = new File(sDrive);				
//				!!! if(fileRoot.exists()==false)break main;    //Nur bei der Erzeugung des events abprüfen.... sonst kommt in jedem Listener ggf. Meldung hoch. "Bitte Datenträger einlegen." 
				
				this.sTextPrevious=this.getText();
				sReturnUI = CommonUtilVIA.computeDriveTitleByRootFile(fileRoot);	
			}catch(ExceptionZZZ ez){
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
				sReturnUI = JLabel_Listening_CarrierTitleVIA.sTEXT_ERROR;
			}		
		}//end main:
		this.setText(sReturnUI);	
	}

	/* (non-Javadoc)
	 * @see use.via.client.module.export.IListenerFileSelectedVIA#fileChanged(use.via.client.module.export.EventListFileSelectedVIA)
	 * 
	 * Bei der Änderung der Dateiauswahl den Datenträgertitel ändern.
	 * Hintergrund: Bei neu eingelegten CDs/DVDs erkennt Windows den Titel nicht sofort, sondern benennt sie z.B. "CD-DVD-ROM Laufwerk". 
	 *                   Diese Methode soll sicherstellen, dass nach einer Dateiauswahl der Datenträgertitel korrekt ist, also vor der Übertragung der Daten an das Servlet.
	 */
	public void fileChanged(EventListFileSelectedVIA eventFileSelected) {
		String sReturnUI = new String(JLabel_Listening_CarrierTitleVIA.sTEXT_NO_DRIVE);
		main:{
			try{
				File file = eventFileSelected.getFile();
				if(file==null){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "EventListFileSelectedVIA received: File=null");
					break main;
				}else{
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "EventListFileSelectedVIA received: Weiterverarbeitet wird der absolute Pfad von File='"+ file.getAbsolutePath() +"'");
				}
			
				String sRoot = FileEasyZZZ.getRoot(file);
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "EventListFileSelectedVIA received: Weiterverarbeitet wird auf dem Root sRoot='"+ sRoot +"'");
				File fileRoot = new File(sRoot);
				//!!! if(fileRoot.exists()==false)break main;    //Nur bei der Erzeugung des events abprüfen.... sonst kommt in jedem Listener ggf. Meldung hoch. "Bitte Datenträger einlegen." 
				
				sReturnUI = CommonUtilVIA.computeDriveTitleByRootFile(fileRoot);	
			}catch(ExceptionZZZ ez){
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
				sReturnUI = JLabel_Listening_CarrierTitleVIA.sTEXT_ERROR;
			}
		}//End main:	
		this.setText(sReturnUI);	
	}
}//End class
