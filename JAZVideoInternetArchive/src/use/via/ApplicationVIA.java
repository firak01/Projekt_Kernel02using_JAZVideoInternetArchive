package use.via;

import java.util.HashMap;

import use.via.client.FrmMainSingletonVIA;
import custom.zKernel.file.ini.FileIniZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IRessourceHandlingObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.JarEasyZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

public class ApplicationVIA extends KernelUseObjectZZZ implements IRessourceHandlingObjectZZZ{
	//Hier wäre der Platz für globale Variablen, Beispiele stammen aus dem TileHexMap-Projekt	
//	private String sBaseDirectoryImages = null;
//	private String sApplicationDirectoryDownload = null;
//	private static final String sINI_APPLICATION_DOWNLOADPATH = "ApplicationDownloadPath";
//	
//	private HashMap<String,String> hmGuiZoomFactorAlias = null;	
//	private String sHexZoomFactorAliasCurrent = null;
//	private Font objFontGuiCurrent = null;
	
	public ApplicationVIA(IKernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	public ApplicationVIA(){
		super(); 
	}
	
	public boolean launchIt() throws ExceptionZZZ {
		ReportLogZZZ.write(ReportLogZZZ.DEBUG, "launch - thread: " + ". ApplicationSingleton....");
		main:{
			IKernelZZZ objKernel = this.getKernelObject();
			boolean bSuccessFillVariableIniInitial = fillVariableIniInitial();//Setze bestimmte, für die Applikation (in <z:math>) verwendeten Variablen der Ini Datei, initial an dieser Stelle, damit sie immer zur Verfügung stehen.			
			FrmMainSingletonVIA frameInfo = FrmMainSingletonVIA.getInstance(objKernel, null);
			
			//Das ist dann die Stelle für globale Aktionen, z.B. in TileHexMap-Projekt
			//---- Bereite Hibernate und die SQLite Datenbank vor.
			
			//---- Bereite das Reporten über Log4J vor...			
			KernelReportContextProviderZZZ objContext = new KernelReportContextProviderZZZ(objKernel, frameInfo.getClass().getName());  //Damit ist das ein Context Provider, der die Informationen auf "Modulebene" sucht.
			ReportLogZZZ.loadKernelContext(objContext, true);  //Mit dem true bewirkt man, dass das file immer neu aus dem ConfigurationsPattern erzeugt wird.		
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Launching Application. Start of main-frame");
					
			//---- Starte den Frame
			boolean bLaunched = frameInfo.launch(objKernel.getApplicationKey() + " - Client (Für Domino 8.5.2 Servlet)");
			if(bLaunched == true){
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'ApplicationVIA', was successfull");
				
				boolean bCentered = frameInfo.centerOnParent();
				if(bCentered==true){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: CenterOnParent 'ApplicationVIA', was successfull");					
				}else{
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "Performing action: CenterOnParent 'ApplicationVIA', was NOT successfull");	
				}
				
			}
			
			/*Merke: Dieser Code wird vor dem Fensterstart ausgeführt. Nur möglich, weil der EventDispatcher-Code nebenläufig ausgeführt.... wird.
			//            Und das ist nur möglich, wenn das der "Erste Frame/ der Hauptframe" der Applikation ist.
			 */
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "##############################################");
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "#### TESTE MULTITHREADING #####################");
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "##############################################");				
			try{			
				for(int icount = 0; icount <= 10; icount++){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "main - thread (actionPerformed): " + icount + ". doing something....");
					Thread.sleep(10);
				}
			}catch(InterruptedException ie){		
				throw new ExceptionZZZ(ie.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getPositionCurrent());
			} 
										
		}//end main;
		return true;
	}
	
	
	//==== METHODEN DER ANWENDUNGSINITIALISIERUNG 
	//=== INI-VARIABLEN
	public boolean fillVariableIniInitial() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			
			//Kernel Objekt
			IKernelZZZ objKernel = this.getKernelObject();
			FileIniZZZ objIni = objKernel.getFileConfigIni();
			
			//Z:B:: 
//			guizoomfactor:{
//				// "GuiZoomFactorUsed"
//				//... Zuerst den eingestellten ZoomFaktor holen UND als Variable hier speichern. Ansonsten wird ggfs. der zuletzt bei der Erstellung der Bilder (z.B bei der Variante) verwendete ZoomFaktor verwendet. //TODO GOON 20180727: Der wird noch aus der Ini.Datei ausgelesen. Demnächst aus Applikation-Einstellung.....
//				String sModuleAlias = this.getKernelObject().getApplicationKey(); //
//				String sProgramAlias = this.getKernelObject().getSystemNumber(); //Ist hier auf Applikationsebene (also "Modulübergreifend") eingerichtet.				
//				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'GuiZoomFactorAliasStart'");
//
//				String sHexZoomFactorAlias = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "GuiZoomFactorAliasStart" );
//				HashMap<String,String>hmZoomFactor=this.getHashMapGuiZoomFactorAlias();
//				String sHexZoomFactorUsed = hmZoomFactor.get(sHexZoomFactorAlias);				
//				objIni.setVariable("GuiZoomFactorUsed", sHexZoomFactorUsed);
//			}
				
			bReturn = true;
		}//end main:		
		return bReturn;
	}
	
	//#### GETTER SETTER
	
	//### FlagMethods ##########################
	public enum FLAGZ{
		DATABASE_NEW,TERMINATE; //Merke: DEBUG und INIT aus ObjectZZZ sollen über IObjectZZZ eingebunden werden, weil von ObjectkZZZ kann man ja nicht erben. Es wird schon von File geerbt.
	}
	private HashMap<String, Boolean>hmFlag = new HashMap<String, Boolean>(); //Neu 20130721 ersetzt die einzelnen Flags, irgendwann...
		
	
		@Override
		public Class getClassFlagZ() {		
			return FLAGZ.class;
		}

		public HashMap<String, Boolean>getHashMapFlagZ(){
			return this.hmFlag;
		} 
		
		/* @see basic.zBasic.IFlagZZZ#getFlagZ(java.lang.String)
		 * 	 Weteire Voraussetzungen:
		 * - Public Default Konstruktor der Klasse, damit die Klasse instanziiert werden kann.
		 * - Innere Klassen müssen auch public deklariert werden.(non-Javadoc)
		 */
		@Override
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
		
		
		/**
		 * @param sFlagName
		 * @return
		 * lindhaueradmin, 06.07.2013
		 */
		public boolean getFlag(String sFlagName) {
//			boolean bFunction = false;
//			main:{
//				if(sFlagName == null) break main;
//				if(sFlagName.equals("")) break main;
//				
//				// hier keine Superclass aufrufen, ist ja schon ObjectZZZ
//				// bFunction = super.getFlag(sFlagName);
//				// if(bFunction == true) break main;
//				
//				// Die Flags dieser Klasse setzen
//				String stemp = sFlagName.toLowerCase();
//				if(stemp.equals("debug")){
//					bFunction = this.bFlagDebug;
//					break main;
//				}else if(stemp.equals("init")){
//					bFunction = this.bFlagInit;
//					break main;
//				}else if(stemp.equals("terminate")){
//					bFunction = this.bFlagTerminate;
//				}else if(stemp.equals("isdraggable")){
//					bFunction = this.flagComponentDraggable;
//				}else if(stemp.equals("iskernelprogram")){
//					bFunction = this.flagComponentKernelProgram;
//				}else{
//					bFunction = false;
//				}		
//			}	// end main:
//			
//			return bFunction;
			return this.getFlagZ(sFlagName);
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
		@Override
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
		
		/**
		 * @param sFlagName
		 * @param bFlagValue
		 * @return
		 * lindhaueradmin, 06.07.2013
		 * @throws ExceptionZZZ 
		 */
		public boolean setFlag(String sFlagName, boolean bFlagValue)  {
//			boolean bFunction = true;
//			main:{
//				if(sFlagName == null) break main;
//				if(sFlagName.equals("")) break main;
//				
//				// hier keine Superclass aufrufen, ist ja schon ObjectZZZ
//				// bFunction = super.setFlag(sFlagName, bFlagValue);
//				// if(bFunction == true) break main;
//				
//				// Die Flags dieser Klasse setzen
//				String stemp = sFlagName.toLowerCase();
//				if(stemp.equals("debug")){
//					this.bFlagDebug = bFlagValue;
//					bFunction = true;                            //durch diesen return wert kann man "reflexiv" ermitteln, ob es in dem ganzen hierarchie-strang das flag �berhaupt gibt !!!
//					break main;
//				}else if(stemp.equals("init")){
//					this.bFlagInit = bFlagValue;
//					bFunction = true;
//					break main;
//				}else if(stemp.equals("terminate")){
//					this.bFlagTerminate = bFlagValue;
//					bFunction = true;
//					break main;
//				}else if(stemp.equals("isdraggabel")){
//					this.flagComponentDraggable = bFlagValue;
//					bFunction = true;
//					break main;
//				}else if(stemp.equals("iskernelprogram")){
//					this.flagComponentKernelProgram = bFlagValue;
//					bFunction = true;
//					break main;
//				}else{
//					bFunction = false;
//				}	
//				
//			}	// end main:
//			
//			return bFunction;
			try{
				return this.setFlagZ(sFlagName, bFlagValue);
			} catch (ExceptionZZZ e) {
				System.out.println("ExceptionZZZ (aus compatibilitaetgruenden mit Version vor Java 6 nicht weitergereicht) : " + e.getDetailAllLast());
				return false;
			}
		}
		
		//aus IResscourceHandlingObjectZZZ
		//### Ressourcen werden anders geholt, wenn die Klasse in einer JAR-Datei gepackt ist. Also:					
			/** Das Problem ist, das ein Zugriff auf Ressourcen anders gestaltet werden muss, wenn die Applikation in einer JAR-Datei läuft.
			 *   Merke: Static Klassen müssen diese Methode selbst implementieren. Das ist dann das Beispiel.
			 * @return
			 * @author lindhaueradmin, 21.02.2019
			 * @throws ExceptionZZZ 
			 */
			public static boolean isInJarStatic() throws ExceptionZZZ{
				boolean bReturn = false;
				main:{
					bReturn = JarEasyZZZ.isInJar(KernelZZZ.class);
				}
				return bReturn;
			}
			@Override
			public boolean isInJar() throws ExceptionZZZ {
				boolean bReturn = false;
				main:{
					bReturn = JarEasyZZZ.isInJar(this.getClass());
				}
				return bReturn;
			}
		
}
