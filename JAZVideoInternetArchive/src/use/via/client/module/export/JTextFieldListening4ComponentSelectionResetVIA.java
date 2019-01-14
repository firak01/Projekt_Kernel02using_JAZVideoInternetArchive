package use.via.client.module.export;

import java.io.File;

import javax.swing.JOptionPane;

import basic.zKernel.IKernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IFlagZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJTextFieldListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JTextFieldListening4ComponentSelectionResetVIA extends KernelJTextFieldListening4ComponentSelectionResetZZZ  implements IListenerFileSelectedVIA{
    private String sTextPrevious = null;  //Der Text, der zuvor einmal eingegeben worden war
	private String sRootPrevious = null;   //mit dem File.Seperator am Ende, wie es in der Dialogbox erwartet wird.
	private KernelJPanelCascadedZZZ panelParent = null;
	
	public JTextFieldListening4ComponentSelectionResetVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelCascaded, String sTextInitial) {
		super(objKernel, sTextInitial);	
		this.setPanelParent(panelCascaded);
	}
	
	public KernelJPanelCascadedZZZ getPanelParent() {
		return this.panelParent;
	}

	public void setPanelParent(KernelJPanelCascadedZZZ objPanel) {
		this.panelParent = objPanel;
	}
	
	public String getTextPrevious(){
		return this.sTextPrevious;
	}
	
	public void doResetCustom(EventComponentSelectionResetZZZ event){
		String sReturnUI="";
		main:{
	//		try{
				String sRootNew = event.getComponentText();
				//JOptionPane.showConfirmDialog(this.getPanelParent(), event.getSource().getClass().getName();   //.getComponentText());
				if(StringZZZ.isEmpty(sRootNew)) break main;
				
	//			dadurch soll verhindert werden, dass gerade eingegebener Text gel�scht wird, auch wenn z.B. noch keine Datei markiert worden ist.
				//Das reicht nicht aus, um zu erkkennen, ob eine neue CD eingelegt worden ist:
			//RAUS ??????????????????????? 20080816	if(sRootNew.equals(this.sRootPrevious)) break main;
				

	//			FGL 20080816 L�sungsversuch "neue CD"  �ber eine Hilfmethode ... .isDriveChanged();
	//			JOptionPane.showConfirmDialog(this.getPanelParent(),  "alter Wert: " + this.sTextPrevious);
	//			boolean btemp = CommonUtilVIA.isDriveChanged(this.getPanelParent(), sRootNew, this.sRootPrevious);
				
				this.sTextPrevious = this.getText();
				this.sRootPrevious = sRootNew;  //damit es beim n�chsten mal gefüllt ist
				ReportLogZZZ.write(ReportLogZZZ.INFO, "Root previous = " + this.sRootPrevious);
				
				//if(btemp==false) break main; //es gibt also kein neues Laufwerk/keinen neuen Datentr�ger, ergo nix aktualisieren.
			
				
							
				//Wenn das nicht �berschreiben wird, so bekommt man vom JCombo das Verzeichnis geliefert
				this.setText(sReturnUI);				
		/*}catch(ExceptionZZZ ez){
				this.setText(ez.getDetailAllLast());
		}*/
		}//end main		
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
					//Wenn ohne Verzeichnisbaum schon eine Datei gew�hlt wurde, sorgt die Belegung dieser Variable daf�r, dass kein "ungwolltes" Leersetzen passiert.
					this.sRootPrevious = FileEasyZZZ.getRoot(file) + File.separator;
				} catch (ExceptionZZZ e) {
					ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
				}			
		}//End main:	
		this.setText(sReturnUI);
	}

	@Override
	public String[] getFlagZ(boolean bFlagValueToSearchFor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFlagZ() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFlagZ_passable(boolean bValueToSearchFor,
			IFlagZZZ objUsingFlagZ) throws ExceptionZZZ {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFlagZ_passable(IFlagZZZ objUsingFlagZ)
			throws ExceptionZZZ {
		// TODO Auto-generated method stub
		return null;
	}

}
