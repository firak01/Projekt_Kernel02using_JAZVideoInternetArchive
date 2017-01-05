/**
 * 
 */
package use.via.client.module.export;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import basic.zKernel.KernelZZZ;
import custom.zKernel.LogZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.IFunctionZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.thread.SwingWorker;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.model.JTree.ModelJTreeNodeDirectoyZZZ;
import basic.zKernelUI.component.model.JTree.ModelJTreeNodeRootDummyZZZ;

/**Diese Klasse wird genutzt beim Aktualisieren des Baums,der die Verzeichnisse enthält.
 *  Sowohl beim Start des Frames als auch nach der Auswahl eines neuen Laufwerks in der Combo-Box.
 * @author 0823
 *
 */
public class SwingWorker4ProgramTreeContentVIA extends SwingWorker implements IConstantZZZ, IObjectZZZ, IFunctionZZZ, IKernelUserZZZ{
	private KernelZZZ objKernel;
	private LogZZZ objLog;
	private KernelJPanelCascadedZZZ panelCascaded;
	
	private String sRootNew;    //Der Wert, der in der ComboBox als neues Laufwerk ausgewählt worden ist.
	private String sText2Update; //Der Wert, der mit "update label" in ein Feld geschrieben werden kann, dass über den Status "der work" berichten soll.
	
	//Flags
	private boolean bFlagInit = false;
	private boolean bFlagTerminate = false;
	private boolean bFlagDebug = false;
										
	
	public SwingWorker4ProgramTreeContentVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelCascaded, String sNewSelection, String[] saFlagControlIn) throws ExceptionZZZ{
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
			tree.setModel(treeModelLoading); //Das bewirkt die Änderung des Modell, welches der JTree verwendet. 
			tree.setRootVisible(false); //nun wird der ggf. eingestellte DummyRoot entfernt, daher hier nur die Child-Knoten anzeigen.
			tree.repaint(); //doch notwendig, wenn es in einem extra thread ist  ???             //ist gar nicht notwendig, da .setModel schon alle Ereigniskomponenten Listener informiert. Ergo wird der Baum auch neu gezeichnet.
		
			
			ModelJTreeNodeDirectoyZZZ treeRootModel = new ModelJTreeNodeDirectoyZZZ(fileStart);	//damit wird der neue Tree eingelesen, was eine Aufgabe für die nebenläufige Programmierung ist.
																		   //																Das bewirkt aber keine Änderung der Anzeige
		
			//Falls der Worker von extern per .interrupt() abgebrochen wird, wird ein entsprechendes flag im .interrupt() gesetzt.
			if(this.getFlag("Terminate")==true){
				this.updateLabel("Interrupted loading: " + sRootNew); //Dies scheint wichtig zu sein, wird dadurch scheinbar erst der tree.repaint() wahrgenommen.
			}else{
			
				//bis dato wird noch nix im JTree aktualisiert
				//Dazu muss man mit dem wirklichen tree - Model arbeiten, das man z.B. bekommt mit:  DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				//Wir übergeben aber nun ein neues Model an den Baum, das dann auch auf unserem mühevoll (mit Rekursion) aufgebautem ModleJTreeNodeDirectoryZZZ baisert.
				DefaultTreeModel treeModel = new DefaultTreeModel(treeRootModel);
				tree.setModel(treeModel); //Das bewirkt die Änderung des Modell, welches der JTree verwendet. 
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
