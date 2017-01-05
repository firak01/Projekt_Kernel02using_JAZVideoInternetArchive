/**
 * 
 */
package use.via.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelActionCascadedZZZ;
import basic.zKernelUI.component.KernelJDialogExtendedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelDialogButtonDefaultZZZ;


/**Dies Klasse dient als Beispiel dafür, wie der ActionListener ausgetauscht wird, so dass beim OK-Button 
 *  noch anderer Code ausgeführt wird als nur die Dialogbox zu schliessen.
 * @author 0823
 *
 */
public class PanelDlgAboutButtonAlternativeVIA extends KernelJPanelDialogButtonDefaultZZZ{
	public PanelDlgAboutButtonAlternativeVIA(KernelZZZ objKernel, KernelJDialogExtendedZZZ dialogExtended, boolean bIsButtonOkAvailable, boolean bIsButtonCancelAvailable){
		super(objKernel, dialogExtended, bIsButtonOkAvailable, bIsButtonCancelAvailable);
	}
	
	//#######################################################
	//### Zugriff auf den alternativen Button
	public KernelActionCascadedZZZ getActionListenerButtonOk(KernelJPanelCascadedZZZ panelButton){
		return new ActionListenerDlgAboutButtonOk(this.getKernelObject(), panelButton);
	}	
	class ActionListenerDlgAboutButtonOk extends KernelActionCascadedZZZ implements ActionListener {
		public ActionListenerDlgAboutButtonOk(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent) {
			super(objKernel, panelParent);
		}
		
		/**Durch überschreiben dieser Methoden können erbende Klassen noch anderen Code ausführen
		* @param ActionEvent
		* @return true ==> es wird der weitere Code ausgeführt
		* 
		* lindhaueradmin; 09.01.2007 09:03:32
		 */
		public boolean actionPerformQueryCustom(ActionEvent ae){
			return true;
		}
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult){
			System.out.println("Danke, dass Sie sich dafür interessieren.");								
			return true;
		}
		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult){
			JDialog dialogParent = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this.getPanelParent());
			dialogParent.dispose();
		}

		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
			// TODO Auto-generated method stub
			
		}
	}//END class actionListenerButtonCancelDefault
}//END class PanelDLgAboutButtonAlternativeVIA
