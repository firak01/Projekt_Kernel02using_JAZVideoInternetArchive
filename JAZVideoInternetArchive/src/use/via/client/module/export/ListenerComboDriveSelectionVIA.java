package use.via.client.module.export;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.IKernelZZZ;

import basic.zBasic.ReflectCodeZZZ;

import basic.zKernelUI.component.IPanelCascadedUserZZZ;
import basic.zKernelUI.component.IPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.IEventBrokerUserZZZ;
import basic.zKernelUI.component.model.KernelSenderComponentSelectionResetZZZ;

public class ListenerComboDriveSelectionVIA extends KernelUseObjectZZZ implements ActionListener, IEventBrokerUserZZZ, IPanelCascadedUserZZZ{
	private KernelSenderComponentSelectionResetZZZ objEventSender = null;
	private IPanelCascadedZZZ panelParent = null;
	private String sOld="";
	
	public ListenerComboDriveSelectionVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, KernelSenderComponentSelectionResetZZZ objEventSender){
		super(objKernel);
		this.panelParent = panelParent;
		this.setSenderUsed(objEventSender);
	}
	public void actionPerformed(ActionEvent eventAction) {
		main:{
		//try{
			//FGL 20080815, wird nun auch �ber einen Refresh Button gestartet. JComboBox combo = (JComboBox) eventAction.getSource();
		    //                         Daher handle auf die ComboBox �ber den Alias der beim panelParent hinterlegt ist
			JComboBox combo = (JComboBox) this.getPanelParent().getComponent("combo");
			String sNew = new String((String)combo.getSelectedItem());
			
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# Dieser Listener ist auch an '" + eventAction.getSource().getClass().getName() + "'  registriert.");
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# Neu ausgew�hltes Laufwerk: " + sNew);
			
			//Neu: 2008-03-12 Abfrage nur hier, ob das Laufwerk auch existiert. Merke: Falls file.exists auf ein CD-Rom laufwerk abgefeuert wird, das nicht existiert, kommt die Aufforderung eine CD einzulegen
			File objFile = new File(sNew);
			if(objFile.exists()== false){
				//JOptionPane.showMessageDialog(this.getPanelParent(), eventAction.getSource().getClass().getName());
				//FGL 2008-08-15: NEIN, statt dessen das Anzeigen, was auch beim Initialisieren ohne CD angezeigt wird.        combo.setSelectedItem(sOld);
				//break main; //
			}
			if(sNew.equals(sOld)) break main; // DEN EVENT NICHT AUSF�HREN, wenn der gleiche Eintrag (das gleiche Laufwerk erneut ausgew�hlt wurde, statt dessen soll der Refresh Button verwendet werden)
			
			//Nun einen neuen Event erzeugen, der andere Komponenten �ndern soll und diesen abfeuern.
			//Merke: Die Komponenten haben sich an diesen Listerner (,der n�mlich gleichzeitig auch als Sender fungiert,)  angemeldet 
			//Hier insbesondere die zahlreichen Labels und Textfields
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#EVENTEVENT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			EventComponentSelectionResetZZZ eventNew= new EventComponentSelectionResetZZZ(combo, 10002,sNew);
			this.getSenderUsed().fireEvent(eventNew);
			
			
			sOld = sNew;
			
			
	}//end main:
	}
	
//	+++ Interface IPanelCascasdedUserZZZ
	public IPanelCascadedZZZ getPanelParent() {		
		return this.panelParent;
	}
	public void setPanelParent(IPanelCascadedZZZ panelParent) {
		this.panelParent = panelParent;
	}
	
	//+++ Interface IEventBrokerUserZZZ
	public KernelSenderComponentSelectionResetZZZ getSenderUsed() {
		return this.objEventSender;
	}
	public void setSenderUsed(KernelSenderComponentSelectionResetZZZ objEventSender) {
		this.objEventSender = objEventSender;
	}

}
