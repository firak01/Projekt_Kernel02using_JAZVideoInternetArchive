package use.via.client.module.export;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJTextFieldListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;
import basic.zKernel.KernelZZZ;

public class JTextField_Listening_CarrierDateVIA extends KernelJTextFieldListening4ComponentSelectionResetZZZ {
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	
	
	public JTextField_Listening_CarrierDateVIA(KernelZZZ objKernel, String sTextInitial) {
		super(objKernel, sTextInitial);		
	}

	
	/* (non-Javadoc)
	 * @see basic.zKernelUI.component.model.IListenerSelectionResetZZZ#doReset(basic.zKernelUI.component.model.EventComponentSelectionResetZZZ)
	 * 
	 * Überscrheibt die gleichnamige Methode der Standard KernelJLabelListening4ResetZZZ-Komponente
	 */
	public void doResetCustom(EventComponentSelectionResetZZZ event) {
		String sReturnUI = new String("");
		main:{
			String sDrive = event.getComponentText();
			if(StringZZZ.isEmpty(sDrive)) break main;
			
			//Darf nicht abgeprüft werden, soll immer auf den Event der "Dateiauswahl" hören.
			//if(sRootPrevious==null && StringZZZ.isEmpty(this.getText())==false) break main; //dadurch soll verhindert werden, dass gerade eingegebener Text gelöscht wird, auch wenn z.B. noch keine Datei markiert worden ist.
			//if(sRootNew.equals(this.sRootPrevious)) break main;
			
			
			
			//FGL 20080311 Warte solange, bis es das File gibt. Wurde z.b. eine CD-Rom noch nicht eingelegt oder noch nicht erkannt, 
			//						  so wird der dialog, der zum einlegen der CD auffordert angezeigt.
			File fileRoot = new File(sDrive);
			//Nur bei der Erzeugung des Events abprüfen, sonst kommt in jedem Listener die Meldung hoch .... if(fileRoot.exists()==false)break main;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
			}
			sReturnUI = CommonUtilVIA.computeDateLastModifiedByFile(fileRoot); 
			
		}//end main:
		this.setText(sReturnUI);
	}
}
