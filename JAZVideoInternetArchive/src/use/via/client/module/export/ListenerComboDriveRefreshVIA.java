package use.via.client.module.export;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;

import basic.zBasic.ReflectCodeZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.IPanelCascadedUserZZZ;
import basic.zKernelUI.component.IPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.IEventBrokerUserZZZ;
import basic.zKernelUI.component.model.KernelSenderComponentSelectionResetZZZ;

public class ListenerComboDriveRefreshVIA extends KernelUseObjectZZZ implements ActionListener, IEventBrokerUserZZZ, IPanelCascadedUserZZZ{
	private KernelSenderComponentSelectionResetZZZ objEventSender = null;
	private IPanelCascadedZZZ panelParent = null;
	private String sOld="";
	
	public ListenerComboDriveRefreshVIA(KernelZZZ objKernel){
		super(objKernel);
	}
	
	public ListenerComboDriveRefreshVIA(KernelZZZ objKernel, IPanelCascadedZZZ panelParent, KernelSenderComponentSelectionResetZZZ objEventSender ){
		super(objKernel);
		this.setSenderUsed(objEventSender);
		this.setPanelParent(panelParent);
	}

	//+++ Interface ActionListener
	public void actionPerformed(ActionEvent eventAction) {
		main:{
		//try{
			//FGL 20080815, wird nun auch über einen Refresh Button gestartet. JComboBox combo = (JComboBox) eventAction.getSource();
		    //                         Daher handle auf die ComboBox über den Alias der beim panelParent hinterlegt ist
			JComboBox combo = (JComboBox) this.getPanelParent().getComponent("combo");
			String sNew = new String((String)combo.getSelectedItem());
			
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# Dieser Listener ist an '" + eventAction.getSource().getClass().getName() + "'  registriert.");
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# Neu ausgewähltes Laufwerk: " + sNew);
			
			//Neu: 2008-03-12 Abfrage nur hier, ob das Laufwerk auch existiert. Merke: Falls file.exists auf ein CD-Rom laufwerk abgefeuert wird, das nicht existiert, kommt die Aufforderung eine CD einzulegen
			File objFile = new File(sNew);
			if(objFile.exists()== false){
				//JOptionPane.showMessageDialog(this.getPanelParent(), eventAction.getSource().getClass().getName());
				//FGL 2008-08-15: NEIN, statt dessen das Anzeigen, was auch beim Initialisieren ohne CD angezeigt wird.        combo.setSelectedItem(sOld);
				//break main; //
			}
			//Gerade das ist beim Refresh nciht gewünscht  if(sNew.equals(sOld)) break main; // DEN EVENT NICHT AUSFÜHREN, wenn der gleiche Eintrag (das gleiche Laufwerk erneut ausgewählt wurde, statt dessen soll der Refresh Button verwendet werden)
			
			//Nun einen neuen Event erzeugen, der andere Komponenten ändern soll und diesen abfeuern.
			//Merke: Die Komponenten haben sich an diesen Listerner (,der nämlich gleichzeitig auch als Sender fungiert,)  angemeldet 
			//Hier insbesondere die zahlreichen Labels und Textfields
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#EVENTEVENT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			EventComponentSelectionResetZZZ eventNew= new EventComponentSelectionResetZZZ(combo, 10002,sNew);
			this.getSenderUsed().fireEvent(eventNew);
						
			sOld = sNew;					
	}//end main:
	}

	//+++ Interface IEventBrokerUserZZZ
	public KernelSenderComponentSelectionResetZZZ getSenderUsed() {
		return this.objEventSender;
	}

	public void setSenderUsed(KernelSenderComponentSelectionResetZZZ objEventSender) {
		this.objEventSender = objEventSender;
	}

	//+++ Interface IPanelCascasdedUserZZZ
	public IPanelCascadedZZZ getPanelParent() {
		return this.panelParent;
	}

	public void setPanelParent(IPanelCascadedZZZ panelParent) {
		this.panelParent = panelParent;
	}
}
