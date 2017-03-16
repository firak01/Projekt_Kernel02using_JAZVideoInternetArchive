/**
 * 
 */
package use.via.client.module.export;

import static java.lang.System.out;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import basic.zKernel.KernelZZZ;
import custom.zKernel.LogZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.IFlagZZZ;
import basic.zBasic.IFunctionZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectClassZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.IFlagZZZ.FLAGZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.thread.SwingWorker;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.JTree.ModelJTreeNodeDirectoyZZZ;
import basic.zKernelUI.component.model.JTree.ModelJTreeNodeRootDummyZZZ;

/**Diese Klasse wird genutzt beim Aktualisieren des Baums,der die Verzeichnisse enth�lt.
 *  Sowohl beim Start des Frames als auch nach der Auswahl eines neuen Laufwerks in der Combo-Box.
 * @author 0823
 *
 */
public class SwingWorker4ProgramTreeContentVIA extends SwingWorker implements IObjectZZZ, IFlagZZZ, IKernelUserZZZ{
	private KernelZZZ objKernel;
	private LogZZZ objLog;
	private KernelJPanelCascadedZZZ panelCascaded;
	
	private String sRootNew;    //Der Wert, der in der ComboBox als neues Laufwerk ausgew�hlt worden ist.
	private String sText2Update; //Der Wert, der mit "update label" in ein Feld geschrieben werden kann, dass �ber den Status "der work" berichten soll.
	
	//Flags
	//TODO: diese einzelvariablen können deaktiviert werden, statt dessen die FLGZ Lösung mit der Hashmap verwednen
	private boolean bFlagInit = false;
	private boolean bFlagTerminate = false;
	private boolean bFlagDebug = false;
	
	private HashMap<String, Boolean>hmFlag = new HashMap<String, Boolean>(); //Neu 20130721	
	protected ExceptionZZZ objException = null;    // diese Exception hat jedes Objekt
										
	
	public SwingWorker4ProgramTreeContentVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelCascaded, String sNewSelection, String[] saFlagControlIn) throws ExceptionZZZ{
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
			this.sRootNew = sNewSelection;
		}//End main:
	}
	
	//#### abstracte - Method aus SwingWorker
	public Object construct() {
		try{

			JTree tree = (JTree) this.panelCascaded.getComponent("tree"); 
			if(tree == null){
				ExceptionZZZ ez  = new ExceptionZZZ("Tree not available by alias.", iERROR_ZFRAME_METHOD, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
						
			//Neues Mode / sprich neuen Knoten ausrechnen.
			File fileStart = new File(this.sRootNew);			
			if(fileStart.exists()==false){
//				FGL: 2008-08-15: Wenn es das Laufwerk nicht gibt, oder z.B. keine CD eingelegt ist, nicht starten !!!	
//				this.updateLabel("Root: "+ sRootNew + " is not available.");
				
				//Alternativ zum Verbleiben auf dem aktuellen Laufwerk das anzeigen, was auch beim Initialisieren ohne eingelegte CD passiert
				ModelJTreeNodeRootDummyZZZ dummyModel = new ModelJTreeNodeRootDummyZZZ();   //" No disk in drive ... "
				DefaultTreeModel treeModelDummy = new DefaultTreeModel(dummyModel);
				tree.setModel(treeModelDummy);  //Sonst steht dort nur Mist drin.  "Sports, Color, Food", wohl default von Sun
				
				tree.setRootVisible(false);    //Dadurch sind auch Dateien auf Root-Ebene in der List-Box anzeigbar.
				tree.setShowsRootHandles(false);
				
			}else{
			this.updateLabel("Loading tree. New root: "+ sRootNew);
			
			//Das alte Model durch neues ersetzen
			ModelJTreeNodeRootDummyZZZ treeModelMutableLoading = new ModelJTreeNodeRootDummyZZZ(fileStart);
			DefaultTreeModel treeModelLoading = new DefaultTreeModel(treeModelMutableLoading);
			tree.setModel(treeModelLoading); //Das bewirkt die �nderung des Modell, welches der JTree verwendet. 
			tree.setRootVisible(false); //nun wird der ggf. eingestellte DummyRoot entfernt, daher hier nur die Child-Knoten anzeigen.
			tree.repaint(); //doch notwendig, wenn es in einem extra thread ist  ???             //ist gar nicht notwendig, da .setModel schon alle Ereigniskomponenten Listener informiert. Ergo wird der Baum auch neu gezeichnet.
		
			
			ModelJTreeNodeDirectoyZZZ treeRootModel = new ModelJTreeNodeDirectoyZZZ(fileStart);	//damit wird der neue Tree eingelesen, was eine Aufgabe f�r die nebenl�ufige Programmierung ist.
																		   //																Das bewirkt aber keine �nderung der Anzeige
		
			//Falls der Worker von extern per .interrupt() abgebrochen wird, wird ein entsprechendes flag im .interrupt() gesetzt.
			if(this.getFlag("Terminate")==true){
				this.updateLabel("Interrupted loading: " + sRootNew); //Dies scheint wichtig zu sein, wird dadurch scheinbar erst der tree.repaint() wahrgenommen.
			}else{
			
				//bis dato wird noch nix im JTree aktualisiert
				//Dazu muss man mit dem wirklichen tree - Model arbeiten, das man z.B. bekommt mit:  DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				//Wir �bergeben aber nun ein neues Model an den Baum, das dann auch auf unserem m�hevoll (mit Rekursion) aufgebautem ModleJTreeNodeDirectoryZZZ baisert.
				DefaultTreeModel treeModel = new DefaultTreeModel(treeRootModel);
				tree.setModel(treeModel); //Das bewirkt die �nderung des Modell, welches der JTree verwendet. 
				tree.setRootVisible(true); //das ist der einfachste Weg, auch Dateien im Root anzeigen zu lassen.
				
				//So wird ein Knoten markiert
				TreeNode treeNode = (TreeNode) treeModel.getRoot();
				TreePath treepathRoot = new TreePath(treeNode);
				tree.setSelectionPath(treepathRoot);
				tree.repaint(); //doch notwendig, wenn es in einem extra thread ist  ???             //ist gar nicht notwendig, da .setModel schon alle Ereigniskomponenten Listener informiert. Ergo wird der Baum auch neu gezeichnet.
				
				this.updateLabel("Finished loading: " + sRootNew); //Dies scheint wichtig zu sein, wird dadurch scheinbar erst der tree.repaint() wahrgenommen.
			}//END getFlag("Terminate")
			}//end if filestart.exists
		}catch(ExceptionZZZ ez){
			System.out.println(ez.getDetailAllLast());
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());					
		}
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

	public static boolean proofFlagZExists(IFlagZZZ objcp, String sFlagName) {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sFlagName))break main;
			if(objcp==null)break main;
						
			Class objClass4Enum = objcp.getClassFlagZ();	//Aufgrund des Interfaces IFlagZZZ wird vorausgesetzt, dass diese Methode vorhanden ist.
			String sFilterName = objClass4Enum.getSimpleName();
			
			//Merke: Nachfolgender Code ist Redundant, bleibt aber drin, solange "FLAGZ" nicht irgendwo als Konstante definiert ist.
			ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(objcp.getClass(), sFilterName);
			if(listEmbedded == null) break main;
			
			for(Class objClass : listEmbedded){
				
				Field[] fields = objClass.getDeclaredFields();
				for(Field field : fields){
					if(!field.isSynthetic()){ //Sonst wird ENUM$VALUES auch zur�ckgegeben.
						//out.format("%s# Field...%s%n", ReflectCodeZZZ.getPositionCurrent(), field.getName());
						if(sFlagName.equalsIgnoreCase(field.getName())){
							bReturn = true;
							break main;
						}
					}				
				}		
			}
		}//End main:
		if(bReturn){
			//System.out.println(ReflectCodeZZZ.getPositionCurrent() + "# VORHANDEN Flag='" + sFlagName + "'");
		}else{
			//System.out.println(ReflectCodeZZZ.getPositionCurrent() + "# NICHT DA Flag='" + sFlagName + "'");
		}
		return bReturn;
	}

	public static boolean proofFlagZExists(Class objcp, String sFlagName) {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sFlagName))break main;
			if(objcp==null)break main;
								
			String sFilterName = "FLAGZ"; //TODO: Diesen Namen als Konstante definieren, dann kann auch in diesen static methoden redundanter Code entfernt werden.
			
			ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(objcp.getClass(), sFilterName);
			if(listEmbedded == null) break main;
			
			for(Class objClass : listEmbedded){
				
				Field[] fields = objClass.getDeclaredFields();
				for(Field field : fields){
					if(!field.isSynthetic()){ //Sonst wird ENUM$VALUES auch zurückgegeben.
						out.format("%s# Field...%s%n", ReflectCodeZZZ.getMethodCurrentName(), field.getName());
						if(sFlagName.equalsIgnoreCase(field.getName())){
							bReturn = true;
							break main;
						}
					}				
				}		
			}
			
			//20170307: Durch das Verschieben von FLAGZ mit den Werten DEBUG und INIT in das IObjectZZZ Interface, muss man explizit auch dort nachsehen.
			   //                Merke: Das Verschieben ist deshlab notwenig, weil nicht alle Klassen direkt von ObjectZZZ erben können, sondern das Interface implementieren müsssen.				
			Class<FLAGZ> enumClass = FLAGZ.class;				
			for(Object obj : FLAGZ.class.getEnumConstants()){
				//System.out.println(obj + "; "+obj.getClass().getName());
				if(sFlagName.equalsIgnoreCase(obj.toString())) {
					bReturn = true;
					break main;
				}
			}			
					
			//20170308: Merke: Wenn immer noch kein FLAGZ gefunden wurde, dieses Flag aus der aufrufenden Klasse selbst holen.
			
		}//End main:
		if(bReturn){
			//System.out.println(ReflectCodeZZZ.getPositionCurrent() + "# VORHANDEN Flag='" + sFlagName + "'");
		}else{
			//System.out.println(ReflectCodeZZZ.getPositionCurrent() + "# NICHT VORHANDEN Flag='" + sFlagName + "'");
		}
		return bReturn;
	}

	/* Voraussetzungen:
	 * - Public Default Konstruktor, damit die Klasse instanziiert werden kann.
	 * - Innere Klassen müssen auch public deklariert werden.
	 */
	public static boolean proofFlagZExists(String sClassName, String sFlagName){
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sFlagName))break main;
			if(StringZZZ.isEmpty(sClassName))break main;
			try {
				
				//Existiert in der Elternklasse oder in der aktuellen Klasse das Flag?
				//System.out.println(ReflectCodeZZZ.getPositionCurrent() + "# ObjektInstanz erzeugen für '" + sClassName + "'");
				Class<?> objClass = Class.forName(sClassName);		
				
				//!!! für abstrakte Klassen gilt: Es kann per Reflection keine neue Objektinstanz geholt werden.
				if(!ReflectClassZZZ.isAbstract(objClass)){
					if(ReflectClassZZZ.isInner(objClass)){
						//Bei inneren Klassen anders eine neue Instanz erzeugen.
					    //http://stackoverflow.com/questions/17485297/how-to-instantiate-inner-class-with-reflection-in-java
						Class<?> objClassEnclosing = ReflectClassZZZ.getEnclosingClass(objClass);
						Object objClassEnclosingInstance = objClassEnclosing.newInstance();
						
						try {
							Constructor<?> ctor= objClass.getDeclaredConstructor(objClassEnclosing);
							Object objInnerInstance = ctor.newInstance(objClassEnclosingInstance);
							IFlagZZZ objcp = (IFlagZZZ) objInnerInstance;
							if(objcp==null){
							}else{
								//System.out.println(ReflectCodeZZZ.getPositionCurrent() + "# INNERE ObjektInstanz für '" + objcp.getClass().getName() + "' erfolgreich erzeugt. Nun daraus Enum Klasse holen... .");
								bReturn = ObjectZZZ.proofFlagZExists(objcp, sFlagName);
							}
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						IFlagZZZ objcp = (IFlagZZZ)objClass.newInstance();  //Aus der Objektinstanz kann dann gut die Enumeration FLAGZ ausgelesen werden.				
						if(objcp==null){
						}else{
							//System.out.println(ReflectCodeZZZ.getPositionCurrent() + "# ObjektInstanz für '" + objcp.getClass().getName() + "' erfolgreich erzeugt. Nun daraus Enum Klasse holen... .");
							bReturn = ObjectZZZ.proofFlagZExists(objcp, sFlagName);
						}
					}//isInner(...)
				}else{
					//System.out.println("Abstrakte Klasse, weiter zur Elternklasse.");
					Class objcp2 = objClass.getSuperclass();
					if(objcp2!=null){
						bReturn = ObjectZZZ.proofFlagZExists(objcp2.getName(), sFlagName);
					}
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//20170307: Durch das Verschieben von FLAGZ mit den Werten DEBUG und INIT in das IObjectZZZ Interface, muss man explizit auch dort nachsehen.
			   //                Merke: Das Verschieben ist deshlab notwenig, weil nicht alle Klassen direkt von ObjectZZZ erben können, sondern das Interface implementieren müsssen.						
			Class<FLAGZ> enumClass = FLAGZ.class;				
			for(Object obj : FLAGZ.class.getEnumConstants()){
				//System.out.println(obj + "; "+obj.getClass().getName());
				if(sFlagName.equalsIgnoreCase(obj.toString())) {
					bReturn = true;
					break main;
				}
			}			
			
			
			//20170308: Merke: Wenn immer noch kein FLAGZ gefunden wurde, dieses Flag aus der aufrufenden Klasse selbst holen.
			
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
