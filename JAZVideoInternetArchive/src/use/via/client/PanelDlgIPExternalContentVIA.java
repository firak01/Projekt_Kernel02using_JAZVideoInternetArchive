/**
 * 
 */
package use.via.client;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import use.via.client.module.ip.ProgramIPContentVIA;



import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.IFunctionZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.thread.SwingWorker;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernelUI.component.KernelActionCascadedZZZ;
import basic.zKernelUI.component.KernelJDialogExtendedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernel.KernelZZZ;
import custom.zKernel.LogZZZ;

/**Das Panel, was im "BorderLayout.CENTER" des entprechenden Dialogs angezeigt werden soll.
 * Merke: Die Buttons OK / Cancel werden durch die DialogBox-Extended-Klasse in den BorderLayout.SOUTH der Dialogbox gesetzt.
 * 
 * @author 0823
 *
 */
public class PanelDlgIPExternalContentVIA  extends KernelJPanelCascadedZZZ{

	public PanelDlgIPExternalContentVIA(KernelZZZ objKernel, KernelJDialogExtendedZZZ dialogExtended) {
		super(objKernel, dialogExtended);
		
		//Diese einfache Maske besteht aus 3 Zeilen und 4 Spalten. 
		//Es gibt au�en einen Rand von jeweils einer Spalte/Zeile
		//Merke: gibt man pref an, so bewirkt dies, das die Spalte beim ver�ndern der Fenstergr��e nicht angepasst wird, auch wenn grow dahinter steht.
		
		FormLayout layout = new FormLayout(
				"5dlu, right:pref:grow(0.5), 5dlu:grow(0.5), left:50dlu:grow(0.5), 5dlu, center:pref:grow(0.5),5dlu",         //erster Parameter sind die Spalten/Columns (hier: vier), als Komma getrennte Eint�ge.
				"5dlu, center:10dlu, 5dlu"); 				 //zweiter Parameter sind die Zeilen/Rows (hier:  drei), Merke: Wenn eine feste L�nge k�rzer ist als der Inhalt, dann wird der Inaht als "..." dargestellt
		this.setLayout(layout);              //!!! wichtig: Das layout muss dem Panel zugwiesen werden BEVOR mit constraints die Componenten positioniert werden.
		CellConstraints cc = new CellConstraints();
		
		JLabel label = new JLabel("Server IP:");
		this.add(label, cc.xy(2,2));
			
		JTextField textfieldIPExternal = new JTextField("Enter or refresh", 20);
		textfieldIPExternal.setHorizontalAlignment(JTextField.LEFT);
		textfieldIPExternal.setCaretPosition(0);
		//Dimension dim = new Dimension(10, 15);
		//textfield.setPreferredSize(dim);
		this.add(textfieldIPExternal, cc.xy(4,2));
		
		// Dieses Feld soll einer Aktion in der Buttonleiste zur Verf�gung stehen.
		//Als CascadedPanelZZZ, wird diese Componente mit einem Alias versehen und in eine HashMap gepackt.
		//Der Inhalt des Textfelds soll dann beim O.K. Button in die ini-Datei gepackt werden.
		this.setComponent("text1", textfieldIPExternal);      //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		
		JButton buttonReadIPExternal = new JButton("Refresh server ip from the web.");
		ActionIPRefreshVIA actionIPRefresh = new ActionIPRefreshVIA(objKernel, this);
		buttonReadIPExternal.addActionListener(actionIPRefresh);
		
		this.add(buttonReadIPExternal, cc.xy(6,2));
		
		
		/* Das funktioniert nicht. Funktionalit�t des JGoodies-Framework. Warum ???
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.addLabel("Externe IP Adresse des Servers");
		JTextField textfield = new JTextField("noch automatisch zu f�llen");
		builder.add(textfield, cc.xy(3,2));
		*/	
	}//END Konstruktor
		
//		#######################################
		//Innere Klassen, welche eine Action behandelt	
		class ActionIPRefreshVIA extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{						
			public ActionIPRefreshVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
				super(objKernel, panelParent);			
			}
			
			public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
//				try {
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'IP-Refresh'");
													
				String[] saFlag = {"useProxy"};					
				KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
																		
				SwingWorker4ProgramIPContentVIA worker = new SwingWorker4ProgramIPContentVIA(objKernel, panelParent, saFlag);
				worker.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
				

			/*} catch (ExceptionZZZ ez) {				
				this.getLogObject().WriteLineDate(ez.getDetailAllLast());
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			}	*/
				
				return true;
			}

			public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
				return true;
			}

			public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			}			 							
			
			class SwingWorker4ProgramIPContentVIA extends SwingWorker implements IConstantZZZ, IObjectZZZ, IFunctionZZZ, IKernelUserZZZ{
				private KernelZZZ objKernel;
				private LogZZZ objLog;
				private KernelJPanelCascadedZZZ panel;
				private String[] saFlag4Program;
				
				private String sText2Update;    //Der Wert, der ins Label geschreiben werden soll. Jier als Variable, damit die intene Runner-Klasse darauf zugreifen kann.
															// Auch: Dieser Wert wird aus dem Web ausgelesen und danach in das Label des Panels geschrieben.
				
				public SwingWorker4ProgramIPContentVIA(KernelZZZ objKernel, KernelJPanelCascadedZZZ panel, String[] saFlag4Program){
					super();
					this.objKernel = objKernel;
					this.objLog = objKernel.getLogObject();
					this.panel = panel;
					this.saFlag4Program = saFlag4Program;					
				}
				
				//#### abstracte - Method aus SwingWorker
				public Object construct() {
					try{
						//1. Ins Label schreiben, dass hier ein Update stattfindet
						updateTextField("Reading ...");
						
						//2. IP Auslesen von der Webseite
						ProgramIPContentVIA objProg =new ProgramIPContentVIA(objKernel, this.panel, this.saFlag4Program);					
						String sIp = objProg.getIpExternal();
						
						//3. Diesen Wert wieder ins Label schreiben.
						updateTextField(sIp);
					}catch(ExceptionZZZ ez){
						System.out.println(ez.getDetailAllLast());
						ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());					
					}
					return "all done";
				}
				
				/**Aus dem Worker-Thread heraus wird ein Thread gestartet (der sich in die EventQueue von Swing einreiht.)
				 *  Entspricht auch ProgramIPContext.updateLabel(..)
				* @param stext
				* 
				* lindhaueradmin; 17.01.2007 12:09:17
				 */
				public void updateTextField(String stext){
					this.sText2Update = stext;
					
//					Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
					Runnable runnerUpdateLabel= new Runnable(){

						public void run(){
//							In das Textfeld den gefundenen Wert eintragen, der Wert ist ganz oben als private Variable deklariert			
							ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Writing '" + sText2Update + "' to the JTextField 'text1");				
							JTextField textField = (JTextField) panel.getComponent("text1");					
							textField.setText(sText2Update);
							textField.setCaretPosition(0);   //Das soll bewirken, dass der Anfang jedes neu eingegebenen Textes sichtbar ist.  
						}
					};
					
					SwingUtilities.invokeLater(runnerUpdateLabel);	
					
//					In das Textfeld eintragen, das etwas passiert.								
					//JTextField textField = (JTextField) panelParent.getComponent("text1");					
					//textField.setText("Lese aktuellen Wert .....");
					
				}
				
				
				///#### Interfaces
				public ExceptionZZZ getExceptionObject() {
					// TODO Auto-generated method stub
					return null;
				}
				public void setExceptionObject(ExceptionZZZ objException) {
					// TODO Auto-generated method stub
					
				}
				public boolean getFlag(String sFlagName) {
					// TODO Auto-generated method stub
					return false;
				}
				public boolean setFlag(String sFlagName, boolean bValue) {
					// TODO Auto-generated method stub
					return false;
				}
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

				@Override
				public HashMap<String, Boolean> getHashMapFlagZ() {
					// TODO Auto-generated method stub
					return null;
				}

				//201702: Enum FLAGZ nutzen 
				@Override
				public boolean proofFlagZExists(String sFlagName) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean setFlagZ(String sFlagName, boolean bFlagValue)
						throws ExceptionZZZ {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean getFlagZ(String sFlagName) {
					// TODO Auto-generated method stub
					return false;
				}					
			} //End Class MySwingWorker

			public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
				// TODO Auto-generated method stub
				
			}
			
	}//End class ...KErnelActionCascaded....
}
