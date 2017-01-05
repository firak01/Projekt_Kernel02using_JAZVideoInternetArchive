/**
 * 
 */
package use.via.client;

import javax.swing.JComponent;
import javax.swing.JFrame;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.util.JFrameHelperZZZ;
import basic.zKernel.KernelZZZ;

/**
 * @author 0823
 *
 */
public class FrmAboutSingletonVIA  extends KernelJFrameCascadedZZZ{
	private static KernelJFrameCascadedZZZ frmCascadedSingleton = null;  //muss static sein, wg. getInstance()!!!
	
	/**Konstruktor ist private, wg. Singleton
	 * @param objKernel
	 * @param objFrame
	 * @throws ExceptionZZZ
	 */
	private FrmAboutSingletonVIA(KernelZZZ objKernel, KernelJFrameCascadedZZZ objFrame) throws ExceptionZZZ{
		super(objKernel, objFrame);
	} 
	private FrmAboutSingletonVIA(){
		super(); 
	}
	
	public static FrmAboutSingletonVIA getInstance(){
		if(frmCascadedSingleton==null){
			frmCascadedSingleton = new FrmAboutSingletonVIA();
		}
		return (FrmAboutSingletonVIA) frmCascadedSingleton;		
	}
	
	public static FrmAboutSingletonVIA getInstance(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent) throws ExceptionZZZ{
		if(frmCascadedSingleton==null){
			frmCascadedSingleton = new FrmAboutSingletonVIA(objKernel, frameParent);
		}
		return  (FrmAboutSingletonVIA) frmCascadedSingleton;		
	}
	
	/**Falls ...Singleton.getInstance() zum Holen der Instanz genutzt wurde, so ist das die Möglichkeit anschliessend noch wichtige Paramter an das Singleton-Objekt zu übertragen.
	 * @param objKernel
	 * @param frameParent
	 * @return boolean
	 *
	 * javadoc created by: 0823, 10.01.2007 - 15:17:37
	 */
	public boolean loadContext(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent){
		boolean bReturn = false;
		main:{
			if(frmCascadedSingleton==null) break main;
			
			this.setKernelObject(objKernel);
			this.setFrameParent(frameParent);
		}
		return bReturn;
	}
	
	
	
	public KernelJPanelCascadedZZZ getPaneContent(){
		PanelDlgAboutVIA objPanel=null;
		try {
			objPanel = new PanelDlgAboutVIA(this.getKernelObject(), this);
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
		}
		return objPanel;
	}
	public boolean launchCustom() throws ExceptionZZZ {		
		return false;
	}
	public JComponent getPaneContent(String sAlias) throws ExceptionZZZ {
		// TODO Auto-generated method stub
		//Hier wird nix in einen anderen Pane als den ContentPane gestellt.
		return null;
	}
	@Override
	public boolean setSizeDefault() throws ExceptionZZZ {
		JFrameHelperZZZ.setSizeDefault(this);
		return true;
	} 
}//End class FrmAboutSingletonVIA
