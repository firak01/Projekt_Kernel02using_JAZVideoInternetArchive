package use.via.client;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasic.util.start.GetOpt;
import basic.zKernel.GetOptZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.util.JFrameHelperZZZ;
import basic.zKernel.KernelZZZ;

public class FrmMainVIA extends KernelJFrameCascadedZZZ{
	public FrmMainVIA(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent) throws ExceptionZZZ{
		super(objKernel, frameParent);	
	}
	public JMenuBar getMenuContent(){
		MenuMainVIA menu = new MenuMainVIA(this.getKernelObject(), this);	
		return menu;
	}
	public KernelJPanelCascadedZZZ getPaneContent(){
		PanelMainVIA objPanel=null;
		try {
			objPanel = new PanelMainVIA(this.getKernelObject(), this);
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
		}
		this.setPanelSub("ContentPane", objPanel);
		return objPanel;
	}
	/* (non-Javadoc)
	 * @see basic.zKernelUI.component.KernelJFrameCascadedZZZ#launchCustom()
	 */
	public boolean launchCustom(){
		return false; //false=es wird ein Frame.pack() ausgeführt. Damit hängt die Größe des Frames von den Komponenten ab. 
	}
	
	
	//##########################################################################
	public static void main(String[] saArg){
		try {		
			//TODO GOON: Hier eine KernelFactory einbauen, die basierend auf den Startparametern ein KernelObjekt liefert.
			//                    Falls die Parameter nicht gefüllt sind, so wird eine default Hashmap übergeben.....
			
			//Beim Start werden Parameter mitgegeben
			//-- ApplikationKey, -- SystemNumber, -- Konfigurationsdirectory, -- Konfigurationsfile
			//Den Argumentpatternstring übergeben. Dabei sind die Optionen auf 1 Zeichen beschränkt und ein Doppelpunkt besagt, dass ein Wert folgt.
			/*
			GetOptZZZ opt = new GetOptZZZ( "k:s:d:f:", saArg);
			String sApplicationKey = opt.readValue("k");
			if(StringZZZ.isEmpty(sApplicationKey)) sApplicationKey = "VIA";
			
			String sSystemNr = opt.readValue("s");
			if(StringZZZ.isEmpty(sSystemNr)) sSystemNr = "01";
			
			String sDir = opt.readValue("d");
			if(StringZZZ.isEmpty(sDir)) sDir = "";
			
			String sFile = opt.readValue("f");
			if(StringZZZ.isEmpty(sFile)) sFile = "ZKernelConfigVideoArchiveClient.ini";
			*/
			ConfigVIA objConfig = new ConfigVIA(saArg);
			
			//---- Nun das eigentliche KernelObjekt initiieren. Dabei können z.B. Debug-Einstellungen ausgwählt worden sein.
			//KernelZZZ objKernel = new KernelZZZ(sApplicationKey, sSystemNr, sDir, sFile,(String)null);
			KernelZZZ objKernel = new KernelZZZ(objConfig, (String)null);
			FrmMainVIA frmMain = new FrmMainVIA(objKernel, null);
			
			/*/Nur Debug: Auslesen einiger Einstellungen. Übergeben werden: Modul, Alias der Section, Section Property
			//Hier muss das Kernel Objekt den Section wert aus dem Alias nehmen, der für den System Key-Definiert ist. Und das ist im Debug Fall 03.
			String stemp = objKernel.getParameterByProgramAlias(frmMain.getClass().getName(), "use.via.client.DlgIPExternalVIA", "IPExternal");
			System.out.println(stemp);
			//END DEBUG*/
			
			/*
			FrameMainRunnerVIA runnerMain = frmMain.new FrameMainRunnerVIA(frmMain);
			SwingUtilities.invokeLater(runnerMain);
			System.out.println("TEST, this should be printed earlier than the print in run() of the later invoked thread ?");//*/
			//*Alternativ dazu dauert es 2 Sekunden länger ....
			frmMain.launch(objKernel.getApplicationKey() + " - Main Frame"); 
			//*/

			//Starten der Protokollierung, parallel in dem normalen Thread
			//Merke1: Weil die ReportLogZZZ-Klasse keinen Konstruktor hat, wird ein ContextProvider verwendet
			//Merke2: Dieser Code wird im main-Thread ausgeführt, also eher als der Start des Frames selbst (der im Event Dispatch Thread ausgeführt wird).
			
			//KernelReportContextProviderZZZ objContext = new KernelReportContextProviderZZZ(objKernel, frmMain.getClass().getName(), frmMain.getClass().getName());					
			KernelReportContextProviderZZZ objContext = new KernelReportContextProviderZZZ(objKernel, frmMain.getClass().getName());  //Damit ist das ein Context Provider, der die Informationen auf "Modulebene" sucht.
			
			//FGL TODO GOON HIER KOMMT ES NOCH ZU EINEM FEHLER !!!!
			ReportLogZZZ.loadKernelContext(objContext, true);  //Mit dem true bewirkt man, dass das file immer neu aus dem ConfigurationsPattern erzeugt wird.
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Start of main-frame");				
													
		} catch (ExceptionZZZ ez) {
			System.out.println(ez.getDetailAllLast()+"\n");
			ez.printStackTrace();
		}		
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
	
	/*
	private class FrameMainRunnerVIA implements Runnable{
		private FrmMainVIA frmMain;
		public FrameMainRunnerVIA(FrmMainVIA frmMain){
			this.frmMain = frmMain;
		}
		public void run() {
			try{
				//			Starten des Hauptframes in dem Event Dispathcer Thread	
				System.out.println("TEST, now launching the frame");
				frmMain.launch(this.frmMain.getKernelObject().getApplicationKey() + " - Main Frame");
				System.out.println("TEST, finished launching.");
			} catch (ExceptionZZZ ez) {
				System.out.println(ez.getDetailAllLast()+"\n");
				ez.printStackTrace();
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());				
			}	
		}		
	}//END private class FrameMAinRunnerVIA
	*/
	
}//End class
