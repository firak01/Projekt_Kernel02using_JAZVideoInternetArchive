package use.via.client.module.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.IFunctionZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.thread.SwingWorker;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernel.KernelZZZ;
import custom.zKernel.LogZZZ;

/**Diese Klasse wird genutzt beim Aktualisieren deer JList, die die Dateien enthält.
 *  Sowohl beim Start des Frames als auch nach der Auswahl eines neuen Laufwerks in der Combo-Box.
 * @author lindhaueradmin
 *
 */
public class SwingWorker4ProgramListContentVIA extends SwingWorker implements IConstantZZZ, IObjectZZZ, IFunctionZZZ, IKernelUserZZZ{
	private KernelZZZ objKernel;
	private LogZZZ objLog;
	private KernelJPanelCascadedZZZ panelCascaded;
	
	private String sDirectoryPathSelected;    //Der Wert, der in der ComboBox als neues Laufwerk ausgewählt worden ist.
	private String sText2Update; //Der Wert, der mit "update label" in ein Feld geschrieben werden kann, dass über den Status "der work" berichten soll.
	
	//Flags
	private boolean bFlagInit = false;
	private boolean bFlagTerminate = false;
	private boolean bFlagDebug = false;
										
	
	public SwingWorker4ProgramListContentVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelCascaded, String sDirectoryPathSelected, String[] saFlagControlIn) throws ExceptionZZZ{
		super();
		main:{
			this.objKernel = objKernel;
			this.objLog = objKernel.getLogObject();
			
			 //setzen der übergebenen Flags	
			  if(saFlagControlIn != null){
				  for(int iCount = 0;iCount<=saFlagControlIn.length-1;iCount++){
					  String stemp = saFlagControlIn[iCount];
					  boolean btemp = setFlag(stemp, true);
					  if(btemp==false){						
						  ExceptionZZZ ez = new ExceptionZZZ("the flag '" + stemp + "' is not available.", iERROR_PARAMETER_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName()); 
						  throw ez;		 
					  }
				  }
				  if(this.getFlag("INIT")==true)	break main; 			
			  }
			
			
			this.panelCascaded = panelCascaded;
			this.sDirectoryPathSelected = sDirectoryPathSelected;
		}//End main:
	}
	
	//#### abstracte - Method aus SwingWorker
	public Object construct() {
		DefaultListModel modelList;
		main:{
		try{
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Worker Thread für den Content der JList Komponente gestartet.");
			
			JList list = (JList) this.panelCascaded.getComponent("list"); 
			if(list == null){
				ExceptionZZZ ez  = new ExceptionZZZ("List not available by alias.", iERROR_ZFRAME_METHOD, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			modelList = (DefaultListModel)  list.getModel();  //Das muss der JList im Konstruktor mitgegeben worden sein.
			modelList.clear();					
			modelList.addElement("Loading files for " + sDirectoryPathSelected + " ... ");
			list.setModel(modelList);			
			list.repaint();
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Default list Model geleert und neuen 'Loading' Eintrag in der Statuszeile hinzugefügt für: '" + sDirectoryPathSelected + "'");
				
			File fileDir = new File(sDirectoryPathSelected);
			if(fileDir.exists()==false){
				modelList.clear();	
				modelList.addElement("No file (" + sDirectoryPathSelected + ")");   //Das tritt z.B. bei leeren CDRom-Laufwerk ein
				list.setModel(modelList);
			    list.repaint();
			}else if(fileDir.isDirectory()==false){
				ExceptionZZZ ez = new ExceptionZZZ("This is not a directory '" + sDirectoryPathSelected + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			
			//+++ Nun die Dateien des Verzeichnisses auflisten und in ein neue Model packen.
			//IDEE: Erst in einem Array die Dateien (und nur die Dateien) sammeln und diese ArrayElemente dann in einer Schleife dem DefaultListModel hinzufügen.
			ArrayList listaFile = new ArrayList();
			File[] fileaAll = fileDir.listFiles();
			
//			Falls der Worker von extern per .interrupt() abgebrochen wird, wird ein entsprechendes flag im .interrupt() gesetzt.
			if(this.getFlag("Terminate")==true){			
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "TERMINATE FLAG WAR GESETZT.  Ermittle keine Dateien.");
				break main;
			}else{
				if(fileaAll==null){
					synchronized (list){
					modelList.clear();
					modelList.addElement("No (readable ?) file ("+sDirectoryPathSelected+")");
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Es gibt keine 'lesbaren' Dateien in: '" + sDirectoryPathSelected + "'");
					list.setModel(modelList);		
					list.repaint();	
					}
					break main;
				}
				if(fileaAll.length <= 0){
					synchronized (list){
					modelList.clear();
					modelList.addElement("No file/No subdir... ("+sDirectoryPathSelected+")");
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Es gibt keine Dateien und kein Verzeichnis in: '" + sDirectoryPathSelected + "'");
					list.setModel(modelList);		
					list.repaint();		
					}
					break main;
				}
			}
			
			//Nun die Verzeichnisse rausfiltern
			for(int icount = 0; icount <= fileaAll.length-1; icount++){
				if(fileaAll[icount].isFile()==true) listaFile.add(fileaAll[icount]);							
			}
			
			//Vorher konnten noch directory - Einträge in der FileListe vorhanden sein
			if(listaFile.size() <= 0){
				synchronized (list){
				modelList.clear();
				modelList.addElement("No file ("+sDirectoryPathSelected+")");
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Es gibt keine Dateien in: '" + sDirectoryPathSelected + "'");
				list.setModel(modelList);		
				list.repaint();		
				}
				break main;
				
			}
			
			
			/*In der Entwicklungsphase geht das zu schnell, darum:
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {				
				ExceptionZZZ ez = new ExceptionZZZ("InterruptedException:" + e.getMessage(), iERROR_RUNTIME, this, ReflectionZZZ.getMethodCurrentName());
				throw ez;
			}//*/
			
			
			//Falls der Worker von extern per .interrupt() abgebrochen wird, wird ein entsprechendes flag im .interrupt() gesetzt.
			if(this.getFlag("Terminate")==true){
				//this.updateLabel("Interrupted loading: " + sDirectoryPathSelected); //Dies scheint wichtig zu sein, wird dadurch scheinbar erst der tree.repaint() wahrgenommen.
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "TERMINATE FLAG WAR GESETZT.  Schreibe keine Einträge.");
				break main;
			}else{				
				synchronized (list){
				//hier die ermittelten files dem Model hinzufügen
				modelList.clear();														
					Iterator it = listaFile.iterator();
					while(it.hasNext()){
						File file = (File) it.next();
						list.setIgnoreRepaint(true);    //vielleicht ist da performanceoptimierend, wenn man versucht nun nicht jedesmal die Komponente neu zu zeichnen.
						modelList.addElement(file.getName());						
					}
					list.setIgnoreRepaint(false);
					list.setModel(modelList);		
					list.repaint();			
				}
			}//END getFlag("Terminate")
		}catch(ExceptionZZZ ez){						
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			System.out.println(ez.getDetailAllLast());
		}
		}//End main:
		return "all done";
	}
	
	/** Dieser Methode sorgt dafür, dass die angegebene Komponente (hier ein JLabel unterhalb des JTrees) aktualisiert wird.
	 * 
	 *  Aus dem Worker-Thread heraus wird ein Thread gestartet (der sich in die EventQueue von Swing einreiht.)
	 *  Entspricht auch ProgramIPContext.updateTextField(..)
	 *  
	* @param stext, der Text, der in das JLabel eingetragen wird
	* 
	* lindhaueradmin; 17.01.2007 12:09:17
	 */
	public void updateLabel(String stext){
		this.sText2Update = stext;
		
//		Das Schreiben des Ergebnisses wieder an den EventDispatcher thread übergeben
		Runnable runnerUpdateLabel= new Runnable(){

			public void run(){
//				In das Textfeld den gefundenen Wert eintragen, der Wert ist ganz oben als private Variable deklariert			
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Writing '" + sText2Update + "' to the JLabel-Field 'label1");				
				JLabel label = (JLabel) panelCascaded.getComponent("label1");					
				label.setText(sText2Update);
				
				//Für ein TextField interressant
				//label.setCaretPosition(0);   //Das soll bewirken, dass der Anfang jedes neu eingegebenen Textes sichtbar ist.  
			}
		};
		
		SwingUtilities.invokeLater(runnerUpdateLabel);	

	}
	
	
	///#### Interfaces
	public boolean proofFlagExists(String sFlagName) {
		// TODO Auto-generated method stub
		return false;
	}

	public KernelZZZ getKernelObject() {
		return this.objKernel;
	}

	public void setKernelObject(KernelZZZ objKernel) {
		this.objKernel = objKernel;
	}

	public LogZZZ getLogObject() {
		return this.objLog;
	}

	public void setLogObject(LogZZZ objLog) {
		this.objLog = objLog;
	}

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
	
	public void interrupt(){		
		ReportLogZZZ.write(ReportLogZZZ.DEBUG, "This worker thread is interrupted.");
		//this.get().wait(5000); //hält das ganze System (nur GUI)  an, vermutlich wird der Thread, auf dem der Event Queue läuft angehalten.
		this.setFlag("Terminate", true);   //Damit kann der aufrufende Thread prüfen, ob dieser Thread abgebrochen wurde.
		super.interrupt();		
	}


	public boolean getFlag(String sFlagName) {
		boolean bFunction = false;
		main:{
			if(sFlagName == null) break main;
			if(sFlagName.equals("")) break main;
			
			// hier keine Superclass aufrufen, ist ja schon ObjectZZZ
			// bFunction = super.getFlag(sFlagName);
			// if(bFunction == true) break main;
			
			// Die Flags dieser Klasse setzen
			String stemp = sFlagName.toLowerCase();
			if(stemp.equals("debug")){
				bFunction = this.bFlagDebug;
				break main;
			}else if(stemp.equals("init")){
				bFunction = this.bFlagInit;
				break main;
			}else if(stemp.equals("terminate")){
				bFunction = this.bFlagTerminate;
			}else{
				bFunction = false;
			}		
		}	// end main:
		
		return bFunction;	
		}

	public boolean setFlag(String sFlagName, boolean bFlagValue) {
		boolean bFunction = true;
		main:{
			if(sFlagName == null) break main;
			if(sFlagName.equals("")) break main;
			
			// hier keine Superclass aufrufen, ist ja schon ObjectZZZ
			// bFunction = super.setFlag(sFlagName, bFlagValue);
			// if(bFunction == true) break main;
			
			// Die Flags dieser Klasse setzen
			String stemp = sFlagName.toLowerCase();
			if(stemp.equals("debug")){
				this.bFlagDebug = bFlagValue;
				bFunction = true;                            //durch diesen return wert kann man "reflexiv" ermitteln, ob es in dem ganzen hierarchie-strang das flag überhaupt gibt !!!
				break main;
			}else if(stemp.equals("init")){
				this.bFlagInit = bFlagValue;
				bFunction = true;
				break main;
			}else if(stemp.equals("terminate")){
				this.bFlagTerminate = bFlagValue;
				bFunction = true;
				break main;
			}else{
				bFunction = false;
			}	
			
		}	// end main:
		
		return bFunction;	
	}
}
