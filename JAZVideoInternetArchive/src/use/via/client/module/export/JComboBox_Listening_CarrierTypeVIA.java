package use.via.client.module.export;

import java.io.File;

import basic.zKernel.IKernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.file.DriveEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJComboBoxListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JComboBox_Listening_CarrierTypeVIA extends KernelJComboBoxListening4ComponentSelectionResetZZZ {

	public JComboBox_Listening_CarrierTypeVIA(IKernelZZZ objKernel, Object objInitial) {
		super(objKernel, objInitial);	
	}

	public void doResetCustom(EventComponentSelectionResetZZZ event) {
		String stemp = event.getComponentText();
		File fileRoot = new File(stemp);		
		try{
			String sDriveAlias = JComboBox_Listening_CarrierTypeVIA.computeDriveAliasByRootFile(fileRoot);
			this.setSelectedItem(sDriveAlias);
		} catch (ExceptionZZZ ez) {
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Keine DETAILS �ber das ausgew�hlte Laufwerk ermittelbar. " + ez.getDetailAllLast());
		}
	}
	
	
	public static String computeDriveAliasByRootFile(File fileRoot) throws ExceptionZZZ{
		String sReturn = "";
		main:{
			if(fileRoot==null) break main;			
			if(fileRoot.exists()==false){
				sReturn = "CD";   //Falls also keine CD/DVD im Laufwerk eingelegt ist
				break main;
			}
			
			
	//		Anhand der "Beschreibung" des Datentr�gers eine Vorbelegung ggf. durchf�hren
			//Merke: L�nge ist immer 0, so kann man also nicht differenzieren. fileRoot.length());  //Man m�sste die L�nge der einzelnen Dateien aufsummieren. ODER mit COM aggieren.
			sReturn = "DVD";				
			String stemp = DriveEasyZZZ.getTypeDescriptonString(fileRoot);
			ReportLogZZZ.write( ReportLogZZZ.DEBUG, "DETAILS �ber das ausgew�hlte Laufwerk: " +stemp);
			if(stemp.toLowerCase().equals("cd-rom laufwerk")){
				sReturn = "CD";
			}else if(stemp.toLowerCase().equals("lokaler datentr�ger")){
				sReturn = "HD";
			}
		}//end main:
		return sReturn;
	}
}//END class
