package use.via.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelActionCascadedZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernel.KernelZZZ;

public class PanelMain_EASTVIA  extends KernelJPanelCascadedZZZ{
	private final int iBUTTON_COLUMN_LENGTH_DEFAULT = 100;  // WIE kann man alle Buttons gleich gross machen ????
	public PanelMain_EASTVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent) throws ExceptionZZZ{
		super(objKernel, panelParent);
		
		BoxLayout objLayout = new BoxLayout((JPanel) this, BoxLayout.Y_AXIS);
		this.setLayout(objLayout);  //!!! Nur den LayoutManager zu initialisieren reicht nicht. Auch wenn das Panel-Objekt mit übergeben wird.
		
		Dimension dim4Button = new Dimension(iBUTTON_COLUMN_LENGTH_DEFAULT, 50);
		
		JButton buttonIP = new JButton("External IP");
		ActionDlgIPLaunchVIA actionIP = new ActionDlgIPLaunchVIA(objKernel, this);
		buttonIP.addActionListener(actionIP);		
		buttonIP.setPreferredSize(dim4Button);
		this.add(buttonIP);
		
		JButton buttonExportLaunch = new JButton("Export Data");
		ActionFormExportDataLaunchVIA actionExport = new ActionFormExportDataLaunchVIA(objKernel, this);
		buttonExportLaunch.addActionListener(actionExport);
		buttonExportLaunch.setPreferredSize(dim4Button);
		this.add(buttonExportLaunch);	
				
		JButton buttonAbout = new JButton("About");
		ActionFormAboutLaunchVIA actionAbout = new ActionFormAboutLaunchVIA(objKernel, this);
		buttonAbout.addActionListener(actionAbout);
		buttonAbout.setPreferredSize(dim4Button);
		this.add(buttonAbout);	
		
		JButton buttonExit = new JButton("Exit");
		ActionFormMainCloseVIA actionExit = new ActionFormMainCloseVIA(objKernel, this);
		buttonExit.addActionListener(actionExit);
		buttonExit.setPreferredSize(dim4Button);
		this.add(buttonExit);	
	}
	
	
	
//	#######################################
	//Innere Klassen, welche eine Action behandelt	
	class ActionFormExportDataLaunchVIA extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{
		//private JPanel panelParent;
				
		public ActionFormExportDataLaunchVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			try {
				 
				//Einige Test- /Protokollausgaben					
				//System.out.println("Anzahl der Componenten im Parent-Panel: " + panelSubSouth.getComponentCount());
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'Export Data via Http' - Formular'");
				
				KernelJPanelCascadedZZZ panelEast = (KernelJPanelCascadedZZZ) this.getPanelParent();
				KernelJPanelCascadedZZZ panelDLG = (KernelJPanelCascadedZZZ)panelEast.getPanelParent();
				KernelJFrameCascadedZZZ frameParent = panelDLG.getFrameParent();
				
				//Lösung: Singleton, damit man nur eine Dialogbox öffnen kann 
				FrmExportDataHttpSingletonVIA frameInfo = FrmExportDataHttpSingletonVIA.getInstance(objKernel, frameParent);
				boolean bLaunched = frameInfo.launch(this.getKernelObject().getApplicationKey() + " - Client (Export Data)");
				if(bLaunched == true){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'Export Data via Http' - Formular', was successfull");
					
					boolean bCentered = frameInfo.centerOnParent();
					if(bCentered==true){
						ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: CenterOnParent 'Export Data via Http' - Formular', was successfull");					
					}else{
						ReportLogZZZ.write(ReportLogZZZ.ERROR, "Performing action: CenterOnParent 'Export Data via Http' - Formular', was NOT successfull");	
					}
					
				}
				
				/*Merke: Dieser Code wird vor dem Fensterstart ausgeführt. Nur möglich, weil der EventDispatcher-Code nebenläufig ausgeführt.... wird.
				//            Und das ist nur möglich, wenn das der "Erste Frame/ der Hauptframe" der Applikation ist.
				try{			
					for(int icount = 0; icount <= 10; icount++){
						ReportLogZZZ.write(ReportLogZZZ.DEBUG, "main - thread (actionPerformed): " + icount + ". doing something....");
						Thread.sleep(10);
					}
				}catch(InterruptedException ie){			
				} */
				

			} catch (ExceptionZZZ ez) {				
				this.getLogObject().WriteLineDate(ez.getDetailAllLast());
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			}
			return true;
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {			
		}

		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
			// TODO Auto-generated method stub
			
		}
	}//END Class "ActionExportDataLaunchVIA
	
	class ActionDlgIPLaunchVIA extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{
		private DlgIPExternalVIA dlgIPExternal = null;
				
		public ActionDlgIPLaunchVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			try {
				 
				//Einige Test- /Protokollausgaben					
				//System.out.println("Anzahl der Componenten im Parent-Panel: " + panelSubSouth.getComponentCount());
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'External IP' - AS DIALOG'");
				
				if(this.dlgIPExternal==null){
					HashMap<String, Boolean> hmFlag = new HashMap<String, Boolean>();
					hmFlag.put("isKernelProgram", true);
					
					this.dlgIPExternal = new DlgIPExternalVIA(this.getKernelObject(), this.getFrameParent(), hmFlag);		
					this.dlgIPExternal.setText4ButtonOk("USE VALUE (overwritten)");
				}
					dlgIPExternal.showDialog(this.getFrameParent(), "Connection/IP External Current");
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Ended Action: Launch 'External IP' - AS DIALOG'");
					
			} catch (ExceptionZZZ ez) {			
				System.out.println(ez.getDetailAllLast()+"\n");
				ez.printStackTrace();				
				this.getLogObject().WriteLineDate(ez.getDetailAllLast());
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			}
			return true;
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {			
		}

		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
			// TODO Auto-generated method stub
			
		}
	}//END Class "ActionExportDataLaunchZZZ
	
	class ActionFormMainCloseVIA extends KernelActionCascadedZZZ{
		public ActionFormMainCloseVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {			
			return true;
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			this.getFrameParent().dispose();
			System.exit(0);  //Ziel: Auch alle anderen Fenster und Programme beenden
		}

		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
			// TODO Auto-generated method stub
			
		}
		
	}
	//private JPanel panelParent;
	
	
	
	class ActionFormAboutLaunchVIA extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{				
		public ActionFormAboutLaunchVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			try {
				 
				//Einige Test- /Protokollausgaben					
				//System.out.println("Anzahl der Componenten im Parent-Panel: " + panelSubSouth.getComponentCount());
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'Export Data via Http' - Formular'");
				
				KernelJPanelCascadedZZZ panelEast = (KernelJPanelCascadedZZZ) this.getPanelParent();
				KernelJPanelCascadedZZZ panelDLG = (KernelJPanelCascadedZZZ)panelEast.getPanelParent();
				KernelJFrameCascadedZZZ frameParent = panelDLG.getFrameParent();
				
				/*Nachteil: man kann beliebig viele Dialogboxen öffnen
				FrmAboutVIA frameAbout = new FrmAboutVIA(this.getKernelObject(), frameParent);
				boolean bLaunched = frameAbout.launch();
				if(bLaunched==true){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'About' - Formular', was successfull");
					
					boolean bCentered = frameAbout.centerOnParent();
					if(bCentered==true){
						ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: CenterOnParent 'About' - Formular', was successfull");					
					}else{
						ReportLogZZZ.write(ReportLogZZZ.ERROR, "Performing action: CenterOnParent 'About' - Formular', was NOT successfull");	
					}
				}
				*/
				
				
				//Lösung: Singleton
				FrmAboutSingletonVIA frameAbout = FrmAboutSingletonVIA.getInstance(objKernel, frameParent);
				boolean bLaunched = frameAbout.launch(this.getKernelObject().getApplicationKey() + " - Client (About)");
				if(bLaunched==true){ 
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'About' - Formular', was successfull");
					
					boolean bCentered = frameAbout.centerOnParent();
					if(bCentered==true){
						ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: CenterOnParent 'About' - Formular', was successfull");					
					}else{
						ReportLogZZZ.write(ReportLogZZZ.ERROR, "Performing action: CenterOnParent 'About' - Formular', was NOT successfull");	
					}
				}
				

			} catch (ExceptionZZZ ez) {				
				this.getLogObject().WriteLineDate(ez.getDetailAllLast());
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			}
			return true;
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {		
		}

		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
			// TODO Auto-generated method stub
			
		}
	}//END Class "ActionExportDataLaunchZZZ
}//END class
