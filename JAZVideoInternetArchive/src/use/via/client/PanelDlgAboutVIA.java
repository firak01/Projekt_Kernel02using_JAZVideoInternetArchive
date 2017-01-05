package use.via.client;

import javax.swing.JLabel;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJDialogExtendedZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class PanelDlgAboutVIA extends KernelJPanelCascadedZZZ{

	/**Das ist der Konstruktor, der zu verwenden ist, wenn man das Panel in einen KernelJDialogExtendedZZZ einbaut.
	 * 
	 * @param objKernel
	 * @param dialogExtended
	 */
	public PanelDlgAboutVIA(KernelZZZ objKernel, KernelJDialogExtendedZZZ dialogExtended) {
		super(objKernel, dialogExtended);
		
		//TODO GOON: Hier das Panel weiter ausgestalten
		JLabel label1 = new JLabel("Das ist ein Test ausgehende von einer Dialogbox ALS eine DIALOGBOX");
		this.add(label1);		
	}
	
	public PanelDlgAboutVIA(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameCascaded) throws ExceptionZZZ {
		super(objKernel, frameCascaded);
		
		//TODO GOON: Hier das Panel weiter ausgestalten
		JLabel label1 = new JLabel("Das ist ein Test (ausgehend von einem Frame ALS ein FRAME");
		this.add(label1);	
	}
}
