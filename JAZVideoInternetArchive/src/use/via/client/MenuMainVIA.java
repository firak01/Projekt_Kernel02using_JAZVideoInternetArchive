/**
 * 
 */
package use.via.client;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernelUI.component.KernelActionJMenuZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernel.IKernelZZZ;
import custom.zKernel.LogZZZ;




/**
 * @author 0823
 *
 */
public class MenuMainVIA extends JMenuBar implements IConstantZZZ, IObjectZZZ, IKernelUserZZZ {
	private IKernelZZZ objKernel;
	private LogZZZ objLog;
	private KernelJFrameCascadedZZZ frameParent;
	
	public MenuMainVIA(IKernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent){
		super();
		this.setKernelObject(objKernel);
		this.setLogObject(this.getKernelObject().getLogObject());
		this.setFrameParent(frameParent);
		
		
		
		JMenu menuHelp = new JMenu("Help");
		JMenu menuConnection = new JMenu("Connection");
					
		JMenuItem mitemAbout = new JMenuItem("About");
		ActionMenuHelpAboutVIA listenerAbout = new ActionMenuHelpAboutVIA(this.getKernelObject(), this.getFrameParent());
		mitemAbout.addActionListener( listenerAbout);
		menuHelp.add(mitemAbout);
		
		JMenuItem mitemIPExternal = new JMenuItem("IP External Current");
		ActionMenuConnectionIPExternalVIA listenerIPExternal = new ActionMenuConnectionIPExternalVIA(this.getKernelObject(), this.getFrameParent());
		mitemIPExternal.addActionListener( listenerIPExternal);
		menuConnection.add(mitemIPExternal);
		
		this.add(menuConnection);
		this.add(menuHelp);			
	}
	
	public KernelJFrameCascadedZZZ getFrameParent(){
		return this.frameParent;
	}
	public void setFrameParent(KernelJFrameCascadedZZZ frameParent){
		this.frameParent = frameParent;
	}
	
	//######################
	//### Interfaces
	/* (non-Javadoc)
	 * @see basic.zBasic.IObjectZZZ#getExceptionObject()
	 */
	public ExceptionZZZ getExceptionObject() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see basic.zBasic.IObjectZZZ#setExceptionObject(basic.zBasic.ExceptionZZZ)
	 */
	public void setExceptionObject(ExceptionZZZ objException) {
		// TODO Auto-generated method stub	
	}

	/* (non-Javadoc)
	 * @see basic.zKernel.IKernelZZZ#getKernelObject()
	 */
	public IKernelZZZ getKernelObject() {
		return this.objKernel;
	}

	/* (non-Javadoc)
	 * @see basic.zKernel.IKernelZZZ#setKernelObject(custom.zKernel.KernelZZZ)
	 */
	public void setKernelObject(IKernelZZZ objKernel) {
		this.objKernel = objKernel;
	}

	/* (non-Javadoc)
	 * @see basic.zKernel.IKernelZZZ#getLogObject()
	 */
	public LogZZZ getLogObject() {
		return this.objLog;
	}

	/* (non-Javadoc)
	 * @see basic.zKernel.IKernelZZZ#setLogObject(custom.zKernel.LogZZZ)
	 */
	public void setLogObject(LogZZZ objLog) {
		this.objLog = objLog;
	}
	
	
//	Innere Klassen, welche eine Action behandelt
//	########### ActionListener #################################
	private class ActionMenuHelpAboutVIA extends KernelActionJMenuZZZ{
		private DlgAboutVIA dlgAbout=null;
		
		public ActionMenuHelpAboutVIA(IKernelZZZ objKernel, KernelJFrameCascadedZZZ frmParent) {
			super(objKernel, frmParent);			
		}
		
		public void actionPerformed(ActionEvent e){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing Action: 'Help/About'");
			if(this.dlgAbout==null){
				this.dlgAbout = new DlgAboutVIA(this.getKernelObject(), this.getFrameParent());
			}
			try {
				dlgAbout.showDialog(this.getFrameParent(), "Help/About");
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Ended Action: 'Help/About'");
			} catch (ExceptionZZZ ez) {					
				System.out.println(ez.getDetailAllLast()+"\n");
				ez.printStackTrace();
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());			
			}
		}
		
	}//End class ActionMenuHelpAbout
	
	private class ActionMenuConnectionIPExternalVIA extends KernelActionJMenuZZZ{
		private DlgIPExternalVIA dlgIPExternal=null;
				
		public ActionMenuConnectionIPExternalVIA(IKernelZZZ objKernel, KernelJFrameCascadedZZZ frmParent){
			super(objKernel, frmParent);
		}
		
		public void actionPerformed(ActionEvent e){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing Action: 'Connection/IP External Current'");
			if(this.dlgIPExternal==null){
				HashMap<String, Boolean> hmFlag = new HashMap<String, Boolean>();
				hmFlag.put("isKernelProgram", true);
				
				this.dlgIPExternal = new DlgIPExternalVIA(this.getKernelObject(), this.getFrameParent(), hmFlag);		
				this.dlgIPExternal.setText4ButtonOk("USE VALUE");
			}
			try {
				dlgIPExternal.showDialog(this.getFrameParent(), "Connection/IP External Current");
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Ended Action: 'Connection/IP External Current'");
			} catch (ExceptionZZZ ez) {					
				System.out.println(ez.getDetailAllLast()+"\n");
				ez.printStackTrace();
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());			
			}
		}
	}//End class ActionMenuConnectionIPExternalVIA
}
