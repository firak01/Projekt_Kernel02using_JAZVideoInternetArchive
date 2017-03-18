package use.via.client.module.export;

import static java.lang.System.out;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IFlagZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.ReflectClassZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.thread.SwingWorker;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import custom.zKernel.LogZZZ;

/**Diese Klasse wird genutzt beim Aktualisieren deer JList, die die Dateien enth�lt.
 *  Sowohl beim Start des Frames als auch nach der Auswahl eines neuen Laufwerks in der Combo-Box.
 * @author lindhaueradmin
 *
 */
public class SwingWorker4ProgramListContentVIA extends SwingWorker implements IObjectZZZ, IFlagZZZ, IKernelUserZZZ{
	private KernelZZZ objKernel;
	private LogZZZ objLog;
	private KernelJPanelCascadedZZZ panelCascaded;
	
	private String sDirectoryPathSelected;    //Der Wert, der in der ComboBox als neues Laufwerk ausgew�hlt worden ist.
	private String sText2Update; //Der Wert, der mit "update label" in ein Feld geschrieben werden kann, dass �ber den Status "der work" berichten soll.
	
	//Flags
	private boolean bFlagInit = false;
	private boolean bFlagTerminate = false;
	private boolean bFlagDebug = false;
	

private HashMap<String, Boolean>hmFlag = new HashMap<String, Boolean>(); //Neu 20130721
	
	protected ExceptionZZZ objException = null;    // diese Exception hat jedes Objekt
										
	
	public SwingWorker4ProgramListContentVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelCascaded, String sDirectoryPathSelected, String[] saFlagControlIn) throws ExceptionZZZ{
		super();
		main:{
			this.objKernel = objKernel;
			this.objLog = objKernel.getLogObject();
			
			 //setzen der �bergebenen Flags	
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
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Worker Thread f�r den Content der JList Komponente gestartet.");
			
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
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Default list Model geleert und neuen 'Loading' Eintrag in der Statuszeile hinzugef�gt f�r: '" + sDirectoryPathSelected + "'");
				
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
			//IDEE: Erst in einem Array die Dateien (und nur die Dateien) sammeln und diese ArrayElemente dann in einer Schleife dem DefaultListModel hinzuf�gen.
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
			
			//Vorher konnten noch directory - Eintr�ge in der FileListe vorhanden sein
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
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "TERMINATE FLAG WAR GESETZT.  Schreibe keine Eintr�ge.");
				break main;
			}else{				
				synchronized (list){
				//hier die ermittelten files dem Model hinzuf�gen
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
	
	/** Dieser Methode sorgt daf�r, dass die angegebene Komponente (hier ein JLabel unterhalb des JTrees) aktualisiert wird.
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
		
//		Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
		Runnable runnerUpdateLabel= new Runnable(){

			public void run(){
//				In das Textfeld den gefundenen Wert eintragen, der Wert ist ganz oben als private Variable deklariert			
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Writing '" + sText2Update + "' to the JLabel-Field 'label1");				
				JLabel label = (JLabel) panelCascaded.getComponent("label1");					
				label.setText(sText2Update);
				
				//F�r ein TextField interressant
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

	
	public void interrupt(){		
		ReportLogZZZ.write(ReportLogZZZ.DEBUG, "This worker thread is interrupted.");
		//this.get().wait(5000); //h�lt das ganze System (nur GUI)  an, vermutlich wird der Thread, auf dem der Event Queue l�uft angehalten.
		this.setFlag("Terminate", true);   //Damit kann der aufrufende Thread pr�fen, ob dieser Thread abgebrochen wurde.
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
				bFunction = true;                            //durch diesen return wert kann man "reflexiv" ermitteln, ob es in dem ganzen hierarchie-strang das flag �berhaupt gibt !!!
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
	
	//20170308: Enum FLAGZ nutzen
	//### FlagMethods ##########################		
		public Class getClassFlagZ(){
			return FLAGZ.class;
		}

			public HashMap<String, Boolean>getHashMapFlagZ(){
				return this.hmFlag;
			} 
		
		/** DIESE METHODE MUSS IN ALLEN KLASSEN VORHANDEN SEIN - über Vererbung -, DIE IHRE FLAGS SETZEN WOLLEN
		 * Weteire Voraussetzungen:
		 * - Public Default Konstruktor der Klasse, damit die Klasse instanziiert werden kann.
		 * - Innere Klassen müssen auch public deklariert werden.
		 * @param objClassParent
		 * @param sFlagName
		 * @param bFlagValue
		 * @return
		 * lindhaueradmin, 23.07.2013
		 */
		public boolean setFlagZ(String sFlagName, boolean bFlagValue) throws ExceptionZZZ {
			boolean bFunction = false;
			main:{
				if(StringZZZ.isEmpty(sFlagName)) break main;
				

				bFunction = this.proofFlagZExists(sFlagName);												
				if(bFunction == true){
					
					//Setze das Flag nun in die HashMap
					HashMap<String, Boolean> hmFlag = this.getHashMapFlagZ();
					hmFlag.put(sFlagName.toUpperCase(), bFlagValue);
					bFunction = true;								
				}										
			}	// end main:
			
			return bFunction;	
		}

		/** DIESE METHODE MUSS IN ALLEN KLASSEN VORHANDEN SEIN - über Vererbung ODER Interface Implementierung -, DIE IHRE FLAGS SETZEN WOLLEN
		 *  SIE WIRD PER METHOD.INVOKE(....) AUFGERUFEN.
		 * @param name 
		 * @param sFlagName
		 * @return
		 * lindhaueradmin, 23.07.2013
		 */
		public boolean proofFlagZExists(String sFlagName){
			boolean bReturn = false;
			main:{
				if(StringZZZ.isEmpty(sFlagName))break main;
					System.out.println("sFlagName = " + sFlagName);
					
					Class objClass4Enum = this.getClassFlagZ();	//Aufgrund des Interfaces IFlagZZZ wird vorausgesetzt, dass diese Methode vorhanden ist.
					String sFilterName = objClass4Enum.getSimpleName();
					
					ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
					if(listEmbedded == null) break main;
					out.format("%s# ListEmbeddedClasses.size()...%s%n", ReflectCodeZZZ.getPositionCurrent(), listEmbedded.size());
					
					for(Class objClass : listEmbedded){
						out.format("%s# Class...%s%n", ReflectCodeZZZ.getPositionCurrent(), objClass.getName());
						Field[] fields = objClass.getDeclaredFields();
						for(Field field : fields){
							if(!field.isSynthetic()){ //Sonst wird ENUM$VALUES auch zurückgegeben.
								//out.format("%s# Field...%s%n", ReflectCodeZZZ.getPositionCurrent(), field.getName());
								if(sFlagName.equalsIgnoreCase(field.getName())){
									bReturn = true;
									break main;
								}
							}				
					}//end for
				}//end for
					
				//20170307: Durch das Verschieben von FLAGZ mit den Werten DEBUG und INIT in das IObjectZZZ Interface, muss man explizit auch dort nachsehen.
			   //                Merke: Das Verschieben ist deshlab notwenig, weil nicht alle Klassen direkt von ObjectZZZ erben können, sondern das Interface implementieren müsssen.
			
								
					//TODO GOON: 
					//Merke: bReturn = set.contains(sFlagName.toUpperCase());
					//          Weil das nicht funktioniert meine Util-Klasse erstellen, die dann den String tatsächlich prüfen kann
					
					Class<FLAGZ> enumClass = FLAGZ.class;
					//EnumSet<FLAGZ> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
					
					for(Object obj : FLAGZ.class.getEnumConstants()){
						System.out.println(obj + "; "+obj.getClass().getName());
						if(sFlagName.equalsIgnoreCase(obj.toString())) {
							bReturn = true;
							break main;
						}
						//set.add((FLAGZ) obj);
					}				
			}//end main:
			return bReturn;
		}

	
		
		/* (non-Javadoc)
		 * @see zzzKernel.basic.KernelAssetObjectZZZ#getExceptionObject()
		 */
		public ExceptionZZZ getExceptionObject() {
			return this.objException;
		}
		/* (non-Javadoc)
		 * @see zzzKernel.basic.KernelAssetObjectZZZ#setExceptionObject(zzzKernel.custom.ExceptionZZZ)
		 */
		public void setExceptionObject(ExceptionZZZ objException) {
			this.objException = objException;
		}
		
		
		/**Overwritten and using an object of jakarta.commons.lang
		 * to create this string using reflection. 
		 * Remark: this is not yet formated. A style class is available in jakarta.commons.lang. 
		 */
		public String toString(){
			String sReturn = "";
			sReturn = ReflectionToStringBuilder.toString(this);
			return sReturn;
		}
		
		/* @see basic.zBasic.IFlagZZZ#getFlagZ(java.lang.String)
		 * 	 Weteire Voraussetzungen:
		 * - Public Default Konstruktor der Klasse, damit die Klasse instanziiert werden kann.
		 * - Innere Klassen m�ssen auch public deklariert werden.(non-Javadoc)
		 */
		public boolean getFlagZ(String sFlagName) {
			boolean bFunction = false;
			main:{
				if(StringZZZ.isEmpty(sFlagName)) break main;
											
				HashMap<String, Boolean> hmFlag = this.getHashMapFlagZ();
				Boolean objBoolean = hmFlag.get(sFlagName.toUpperCase());
				if(objBoolean==null){
					bFunction = false;
				}else{
					bFunction = objBoolean.booleanValue();
				}
								
			}	// end main:
			
			return bFunction;	
		}
	
}
