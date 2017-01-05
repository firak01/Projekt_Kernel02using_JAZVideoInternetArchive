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


/**Wie DlgServerIPExternal, aber:
 *  Durch Verwendung des Singleton-Patterns wird dieser Frame immer nur einmal geöffnet !!!
 *  In .laucnchCustom() steht Code, der nix mit dem Starten des Frontends zu tun hat (welches in den EventQueue-Thread verlagert worden ist),
 *  sondern nebenläufig im main-Thread ausgeführt wird.
 *   
 * @author 0823
 *
 */
public class FrmExportDataHttpSingletonVIA extends KernelJFrameCascadedZZZ{
	private static FrmExportDataHttpSingletonVIA dlgSingleton = null;  //muss static sein, wg. getInstance()!!!
	
	/**Konstruktor ist private, wg. Singleton
	 * @param objKernel
	 * @param objFrame
	 * @throws ExceptionZZZ
	 */
	private FrmExportDataHttpSingletonVIA(KernelZZZ objKernel, KernelJFrameCascadedZZZ objFrame) throws ExceptionZZZ{
		super(objKernel, objFrame);
	}
	private FrmExportDataHttpSingletonVIA(){
		super(); 
	}
	
	public static FrmExportDataHttpSingletonVIA getInstance(){
		if(dlgSingleton==null){
			dlgSingleton = new FrmExportDataHttpSingletonVIA();
		}
		return dlgSingleton;		
	}
	
	public static FrmExportDataHttpSingletonVIA getInstance(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent) throws ExceptionZZZ{
		if(dlgSingleton==null){
			dlgSingleton = new FrmExportDataHttpSingletonVIA(objKernel, frameParent);
		}
		return dlgSingleton;		
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
			if(dlgSingleton==null) break main;
			
			this.setKernelObject(objKernel);
			this.setFrameParent(frameParent);
		}
		return bReturn;
	}
	
	public KernelJPanelCascadedZZZ getPaneContent(){
		PanelFrmExportDataHttpVIA objPanel=null;
		try {
			objPanel = new PanelFrmExportDataHttpVIA(this.getKernelObject(), this);
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
		}
		return objPanel;
	}
	
	
	/* (non-Javadoc)
	 * @see basic.zKernelUI.component.KernelJFrameCascadedZZZ#launchCustom()
	 */
	public boolean launchCustom(){	
		ReportLogZZZ.write(ReportLogZZZ.DEBUG, "launch - thread: " + ". doing CUSTOM....");
		return false;  //Dadurch sollen die Komponenten die Größe des Frames bestimmen.
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

}
