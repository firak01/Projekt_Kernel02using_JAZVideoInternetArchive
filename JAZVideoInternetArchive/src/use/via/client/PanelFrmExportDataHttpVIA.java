package use.via.client;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
//wg. gleichnaiger XMLBean Klasse File nicht m�glich....    import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLStreamHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import lotus.domino.Document;


//import org.apache.commons.httpclient.Cookie;
//import org.apache.commons.httpclient.Credentials;
//import org.apache.commons.httpclient.Header;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpState;
//import org.apache.commons.httpclient.UsernamePasswordCredentials;
//import org.apache.commons.httpclient.auth.AuthScope;
//import org.apache.commons.httpclient.auth.AuthState;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.OptionsMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.params.HttpMethodParams;
//import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.xmlbeans.XmlException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import use.via.ExpressionTranslatorZZZ;
import use.via.MapperStoreHttpZZZ;
import use.via.client.PanelMain_EASTVIA.ActionFormExportDataLaunchVIA;
import use.via.client.module.export.CommonUtilVIA;
import use.via.client.module.export.JComboBoxListening_SerieTitleVIA;
import use.via.client.module.export.JComboBox_Listening_CarrierTypeVIA;
import use.via.client.module.export.JEditorPane_Listening_ExportStatusVIA;
import use.via.client.module.export.JLabel_Listening_CarrierTitleVIA;
import use.via.client.module.export.JLabel_Listening_FileDateVIA;
import use.via.client.module.export.JLabel_Listening_FileSizeVIA;
import use.via.client.module.export.JTextAreaListening4ComponentSelectionResetVIA;
import use.via.client.module.export.JTextArea_Listening_FileRemarkVIA;
import use.via.client.module.export.JTextField_Listening_MovieTitleVIA;
import use.via.client.module.export.JText_Listening_FileNameVIA;
import use.via.client.module.export.JListFileListening4ComponentResetVIA;
import use.via.client.module.export.JTextFieldListening4ComponentSelectionResetVIA;
import use.via.client.module.export.JTextField_Listening_CarrierDateVIA;
import use.via.client.module.export.ListenerCombo4TreeRefreshVIA;
import use.via.client.module.export.ListenerComboDriveRefreshVIA;
import use.via.client.module.export.ListenerComboDriveSelectionVIA;
import use.via.client.module.export.ListenerListFileSelectionVIA;
import use.via.client.module.export.ListenerTreeDirectorySelectionVIA;
import use.via.client.module.export.SwingWorker4ProgramListContentVIA;
import use.via.client.module.export.SwingWorker4ProgramTreeContentVIA;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import basic.javagently.Stream;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.txt.TxtReaderZZZ;
import basic.zBasic.util.file.txt.TxtWriterZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import basic.zKernelUI.KernelUIZZZ;
import basic.zKernelUI.component.KernelActionCascadedZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.expression.KernelUIExpressionZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.IEventBrokerUserZZZ;
import basic.zKernelUI.component.model.IListenerSelectionResetZZZ;
import basic.zKernelUI.component.model.ISenderSelectionResetZZZ;
import basic.zKernelUI.component.model.KernelSenderComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.JTree.ModelJTreeNodeRootDummyZZZ;
import basic.zKernelUI.util.KernelJComboBoxHelperZZZ;
import basic.zKernel.IKernelConfigSectionEntryZZZ;
import basic.zKernel.IKernelZZZ;
import custom.zUtil.io.FileZZZ;
import de.tOnline.homepage.fgl.jazVideoInternetArchiveClient.ArchiveEntry;
import de.tOnline.homepage.fgl.jazVideoInternetArchiveClient.ArchiveEntryList;
import de.tOnline.homepage.fgl.jazVideoInternetArchiveClient.Carrier;
import de.tOnline.homepage.fgl.jazVideoInternetArchiveClient.File;
import de.tOnline.homepage.fgl.jazVideoInternetArchiveClient.JAZVideoInternetArchiveClientDocument;
import de.tOnline.homepage.fgl.jazVideoInternetArchiveClient.Movie;
import de.tOnline.homepage.fgl.jazVideoInternetArchiveClient.Serie;
import de.wenzlaff.cdrom.CdRom;

public class PanelFrmExportDataHttpVIA extends KernelJPanelCascadedZZZ{
	
	public PanelFrmExportDataHttpVIA(IKernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent) throws ExceptionZZZ{
		super(objKernel, frameParent);
		  
		//Zur Normierung der Komponenten werden diese Dimsion-Objekte in .setPrefferedSize herangezgen.
		Dimension dimTree = new Dimension(200, 300);
		Dimension dimButtonTiny = new Dimension(25, 25); //z.B. der Refresh Button neben der Laufwerksauswahl Combobox
		Dimension dimHalft = new Dimension(75, 15);
		Dimension dimSingle = new Dimension(150, 15);
		Dimension dimDouble = new Dimension(300, 15);
		
		//Wichtige Informationen, zum Auslesen von Parametern aus der KernelConfiguration
		String sProgram = frameParent.getClass().getName(); //der Frame, in den dieses Panel eingebettet ist
		String sModule = frameParent.getFrameParent().getClass().getName();
				
		
		//Diese einfache Maske besteht aus 3 Zeilen und 4 Spalten. 
		//Es gibt au�en einen Rand von jeweils einer Spalte/Zeile
		//Merke: gibt man pref an, so bewirkt dies, das die Spalte beim ver�ndern der Fenstergr��e nicht angepasst wird, auch wenn grow dahinter steht.
		final String sCSep = "center:max(10pt;min)"; //"1dlu:grow(0.5)"; //z.B. eine Trennspalte zwiwschen F�hrungstext und dahinterliegendem Textfeld
		final String sCButLeft = ",right:max(15pt;min), "; 
		final String sCButRight = ",left:max(10pt;min), ";//dient als spalte f�r zus�tzliche Buttons (z.B. den Refresh Button f�r die Tree-Auswahl)
		final String sSection = ",right:100dlu,";       //darin ist die Dateiliste
		final String sSection75 = ",right:75dlu,";       //darin ist der Tree (er geht dann auch noch �ber mehrer Button Sections)
		final String sColumnLeft = ",left:60dlu:grow(0.5),";
		final String sColumnRight = ",right:60dlu:grow(0.5),";
	
		final String sColumnAll = sCSep + sSection75 +sCButRight + sCButRight + sCSep + sSection + sCSep +
		sColumnRight + sCSep + sColumnLeft + sCSep + sColumnRight + sCSep + sColumnLeft + sCSep;

		final String sRSep = "4dlu";
		final String sRow = ",center:20dlu,";
		final int iNumberOfRow = 18;
		
		//Nach jeder Zeile wird so automatisch eine Trennzeile eingef�hrt
		String sRowAll = new String(sRSep);
		for(int icount=1; icount <= iNumberOfRow; icount ++){
			//Ausnahme: Zeile �ber eine Block (hier die 19.+20. Zeile)
			//					 Zeile 1 + 2 
			if(icount==9 || icount == 1){
				sRowAll = sRowAll + sRow + sRow;
			}else{
				sRowAll = sRowAll +  sRow + sRSep ;
			}
		};
	
		/*	FormLayout layout = new FormLayout(						
				"5dlu, right:pref:grow(0.5), 5dlu:grow(0.5), left:50dlu:grow(0.5), 5dlu",         //erster Parameter sind die Spalten/Columns, als Komma getrennte Eint�ge.
				"5dlu, center:10dlu, 5dlu"); 				 //zweiter Parameter sind die Zeilen/Rows (hier:  drei), Merke: Wenn eine feste L�nge k�rzer ist als der Inhalt, dann wird der Inaht als "..." dargestellt
		*/
		
		FormLayout layout = new FormLayout(sColumnAll, sRowAll);		
		this.setLayout(layout);              //!!! wichtig: Das layout muss dem Panel zugwiesen werden BEVOR mit constraints die Componenten positioniert werden.
		CellConstraints cc = new CellConstraints();
		
		//#######################################################
		//# MERKE:
		//# Die Combo-Box und der JTree fungieren nicht direkt als Sender von den "k�nstlichen, eigenen Events",
		//# sondern es sind die Listener, welche auf die ComboBox, bzw. der Auswahl im Tree h�ren, die auch als Sender f�r die "k�nstlichen, eigenen Events" fungieren.
		//# Zu diesem Zweck haben die Listener meine Interfaces implementiert, die sie zu "Sendern" machen.
		//#
		//# MERKE 2:
		//# Die JList, sowie (im Debug-Teil) die JLabels, bzw. (im Produktiv-Teil) die JTextFields sind so erweitert, dass sie Methoden der IListener-Interfaces implementieren.
		//# Daher k�nnen sie durch die entsprechenden .fireEvent(event) - Methoden der Sender herangezogen werden. Sprich: In den .fireEvent(event) - Methoden der Sender wird eine Methode
		//# des IListener-Interfaces aufgerufen. Da sich die Komponenten-Objekte an den entsprechenden Sender registriert haben (d.h. in der ArrayList der Listener der Sender aufgenommen sind), werden ihre Methoden aufgerufen. 
		//#######################################################
		
		//++++ ALLE verwendeten Listener
		//TODO: Den Eventbroker erzeugen, der den Event abfeuert "neuer Datentr�ger ausgew�hlt".
		//TODO: Alle Komponenten m�ssen sich an diesen EventBroker registrieren.
		
		
		
		//TODO: Den EventBroker hinzufügen, damit dar�ber der Event abgefeuert werden kann
		KernelSenderComponentSelectionResetZZZ objEventDriveBroker = new KernelSenderComponentSelectionResetZZZ(objKernel);
		
		//FGL20081001 Den EventBroker mehrfach nutzen ListenerComboDriveSelectionVIA listenerComboSelection = new ListenerComboDriveSelectionVIA(objKernel, this);      //Erzeugt den Event, wenn in der Combobox ein neues Laufwerk ausgew�hlt wird. Merke: Wird 2mal das gleiche ausgew�hlt, so wird kein Event gestartet.
		ListenerComboDriveSelectionVIA listenerComboSelection = new ListenerComboDriveSelectionVIA(objKernel, this, objEventDriveBroker);      //Erzeugt den Event, wenn in der Combobox ein neues Laufwerk ausgew�hlt wird. Merke: Wird 2mal das gleiche ausgew�hlt, so wird kein Event gestartet.
		
		//FGL 20081001 Den EventBroker mehrfach nutzen  ListenerComboDriveRefreshVIA listenerComboRefresh = new ListenerComboDriveRefreshVIA(objKernel, this);				//Erzeugt den Event, wenn der RefreshButton neben der ComboBox ausgew�hlt wird. Entspricht im Grunde dem Listerne Combo Drive Selection.
		ListenerComboDriveRefreshVIA listenerComboRefresh = new ListenerComboDriveRefreshVIA(objKernel, this, objEventDriveBroker); //Erzeugt den Event, wenn der RefreshButton neben der ComboBox ausgew�hlt wird. Entspricht im Grunde dem Listerne Combo Drive Selection, aber: Wird auch bei gleicher vorherigen Auswahl ausgef�rht
	
		ListenerListFileSelectionVIA listenerListSelection = new ListenerListFileSelectionVIA(objKernel, this);                              //Erzeugt den Event, wenn in der Dateiliste ein Eintrag ausgw�hlt wird
		ListenerTreeDirectorySelectionVIA listenerTreeSelection = new ListenerTreeDirectorySelectionVIA(objKernel, this);      //Erzeugt den Event, wenn im Verzeichnisbaum ein Eintrag ausgew�hlt wird
		
		//+++++++++++++++++++++++++++++++++++++++++
				
		//    +++  COMBO-Box, für die Auswahl des Laufwerks
		JComboBox comboDrive = new JComboBox();
		this.setComponent("combo", comboDrive); //Die combo Box �ber den PanelCascadedZZZ-Mechanismus f�r andere Komponenten greifbar machen.
		comboDrive.setEditable(true);
		
		java.io.File[] filea = java.io.File.listRoots();
		for(int icount = 0 ; icount <= filea.length-1; icount++){
			java.io.File file = filea[icount];
			comboDrive.addItem(file.getPath());
		}
				
		String sDriveDefault;
		try{
			IKernelConfigSectionEntryZZZ objEntry = objKernel.getParameterByProgramAlias(sModule, sProgram, "DriveDefault");
			sDriveDefault = objEntry.getValue();
		}catch(ExceptionZZZ ez){
			ReportLogZZZ.write(ReportLogZZZ.ERROR, "DriveDefault not configured. Will use c:\\");
			sDriveDefault = "C:\\";
		}
		//prüfen, ob dieses Laufwerk überhaupt in der liste ist
		boolean bDefaultSelectable = false;
		for (int icount = 0; icount <= comboDrive.getItemCount()-1; icount++){
			String sItem = (String) comboDrive.getItemAt(icount);
			if(sItem.equals(sDriveDefault)){
				bDefaultSelectable = true;
				break;
			}
		}
		if(bDefaultSelectable==false){
			comboDrive.setSelectedIndex(0);  //Das erste Element ausw�hlen
		}else{
			comboDrive.setSelectedItem(sDriveDefault);  //Mit einer Vorauswahl belegen. z.b. comboDrive.setSelectedItem("C:\\");
		}		
		
		//Diese Actionen werden von der Combo-Box abgefeuert, wenn sich die aktuelle Auswahl �ndert.
		//1. Starte einen Event-der die (an diesen Sender) angemeldeten Komponenten �ber den "Reset" informiert.		
		comboDrive.addActionListener(listenerComboSelection);
		
		//2. Bei Änderung der ComboBox: Starte jeweils einen SwingWorker-Thread, der den JTree und die File-Liste  mit einem neuen Model der Verzeichnisse versorgt.
		ListenerCombo4TreeRefreshVIA listenerComboCascaded = new ListenerCombo4TreeRefreshVIA(objKernel, this);                    //
		comboDrive.addActionListener(listenerComboCascaded);
		
		//Füge die ComboBox der Maske hinzu
		this.add(comboDrive, cc.xy(2,2));   
		
		
		//Dieser String bzw. das File ist die Grundlage f�r die Initalbef�llung des Verzeichnisbaums und der JList Komponente
		String sRoot = (String) comboDrive.getSelectedItem();
		java.io.File fileRoot = new java.io.File(sRoot);
		boolean bRootExists = fileRoot.exists(); //An dieser zentralen Stelle abpr�fen, damit die Aufforderung "Bitte Datentr�ger einlegen" bei leerer CD nur einmal erscheint
		
		
		//+++ BUTTON - Verzeichnisbaum Refreshen.
		//Refresh - Button f�r die Aktualisierung des Verzeichnisbaums, basierend auf dem ausgew�hlten Laufwerk
		JButton buttonTreeRefresh = new JButton(); //TODO: Kein Text, sondern nur ein kleines Image
		buttonTreeRefresh.setPreferredSize(dimButtonTiny);
		buttonTreeRefresh.addActionListener(listenerComboCascaded); //aktualisiert den Tree, startet zus�tzliche Threads
		//nein: dieser Listener feuert keinen Event ab, wenn dier gleich Eintrag gew�hlt wurde buttonTreeRefresh.addActionListener(listenerComboSelection); //aktualisiert die Liste, auch wenn das Laufwerk nicht bereit ist wird die Liste dadurch zur�ckgesetzt 
		buttonTreeRefresh.addActionListener(listenerComboRefresh); //aktualisiert die Liste, auch wenn das Laufwerk nicht bereit ist wird die Liste dadurch zur�ckgesetzt
		this.add(buttonTreeRefresh,  cc.xy(3,2));
		
		//+++ BUTTON - CD-ROM Laufwerk �ffnen
		//--- TODO: Soll sich an einen Event anh�ngen, der dann den Button verbirgt, wenn kein CD/DVD-Laufwerk ausgw�hlt wird
		//--- TODO: Soll das CD-ROM Laufwerk �ffnen, soll einen Thread starten, der auf eine neue CD wartet. Wenn eine neue CD eingelegt wurde, dann den internen Status af "geschlossen" setzen.
		//--- TODO: Soll das CD-ROM Laufwerk ggf. schliessen, wenn der interne Status auf "offen" gesetzt wurde.
		JButton buttonCd = new JButton();
		buttonCd.setPreferredSize(dimButtonTiny);
		
		//Bild hinzufügen
		//20190211: Lies den Namen der Datei aus der Konfiguration aus und hole dies als ImageIcon-Parameter.		
		ImageIcon bild = objKernel.getParameterImageIconByProgramAlias(sModule, sProgram, "Image_CD_closed");
		if(bild==null){
			String sFilePath = "image/quit.png";
			java.io.File objFile = FileEasyZZZ.searchFile(sFilePath);
			String sImageAlternative = objFile.getAbsolutePath();
			bild = new ImageIcon(sImageAlternative);
		}
		buttonCd.setIcon(bild);
			
		bild = objKernel.getParameterImageIconByProgramAlias(sModule, sProgram, "Image_CD_opened");	
		if(bild==null){
			String sFilePath = "image/quit.png";
			java.io.File objFile = FileEasyZZZ.searchFile(sFilePath);
			String sImageAlternative = objFile.getAbsolutePath();
			bild = new ImageIcon(sImageAlternative);
		}
		buttonCd.setPressedIcon(bild);
		
		buttonCd.setHorizontalAlignment(SwingConstants.CENTER);
		buttonCd.setVerticalAlignment(SwingConstants.CENTER);
		
		ActionCdDriveOpenCloseVIA actionCdDriveOpenClose = new ActionCdDriveOpenCloseVIA(objKernel, this, objEventDriveBroker);
		buttonCd.addActionListener(actionCdDriveOpenClose);
		this.add(buttonCd,  cc.xy(4,2));
		

		
		//### VERZEICHNISBAUM ########################################
		//Ein Label, dass �ber den Verzeichnisbaum berichten soll. s. updateLabel in der SwingWorker Klasse f�r den Aufbau des TreeModels.
		JLabel labelTreeStatus = new JLabel("");
		this.add(labelTreeStatus, cc.xywh(2,2*iNumberOfRow, 2, 1));  //2* , wg. der "Trennzeilen", umfasst also die Buttonspalte des Refresh-Buttons plus weitere
		this.setComponent("label1", labelTreeStatus);
		
		//### Der Verzeichnisbaum selbst
		//1. JTree aus anderen Componenten zugreifbar machen.
		//!!! DAS EIGENTLICHE MODEL ERST IM Swing Worker berechnen   ModelJTreeNodeDirectoyZZZ treeModel = new ModelJTreeNodeDirectoyZZZ(fileStart);		
		//JTree tree = new JTree(treeModel);
		JTree tree = new JTree();
		this.setComponent("tree", tree);
		
		//2. Listener und weitere Eigenschaften f�r den Tree und die Auswahl von Knoten hinzuf�gen
		//    Merke: Darin wird ein event angestossen, der alle angemeldeten Komponenten �ber die neue Auswahl informiert		
		tree.addTreeSelectionListener(listenerTreeSelection);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		//3, Model hinzuf�gen UND den Versuch die Performance zu verbessern, indem man einen externen Thread startet.
		try{
//			FGL 20080314 Damit nur einmal die Abfrage "Bitte legen Sie eine CD ein" kommt, vor dem Start des Worker Thread auf die Existenz des Pfades abfragen
			if(bRootExists){
				ModelJTreeNodeRootDummyZZZ dummyModel = new ModelJTreeNodeRootDummyZZZ(fileRoot);
				/*Funktioniert nicht
				for(int icount = 0; icount <= 100; icount++){
					dummyModel.add(new DefaultMutableTreeNode(" "));  //Das soll ein Workarround sein, damit die ScrollBars angezeigt werden.
				}		
				*/
				DefaultTreeModel treeModelDummy = new DefaultTreeModel(dummyModel);
				tree.setModel(treeModelDummy);  //Sonst steht dort nur Mist drin.  "Sports, Color, Food", wohl default von Sun
				
//				!!! Da zun�chst ein Dummy-Tree, der nur aus dem Root besteht eingelesen werden soll, Merke: Am Ende des WorkerThreads wird. tree.setRootVisible(false);  aufgerufen.
				tree.setRootVisible(true);    //Dadurch sind auch Dateien auf Root-Ebene in der List-Box anzeigbar.
				tree.setShowsRootHandles(true);
				
				String[] saFlag = null;
				SwingWorker4ProgramTreeContentVIA workerTree = null; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ERST MAL RAUS         listenerComboCascaded.getWorkerThreadTree();
				if(workerTree==null){
					workerTree = new SwingWorker4ProgramTreeContentVIA(objKernel, this, sRoot, saFlag);
				//!!!!!!!!!!!!!!!!!!!	listenerComboCascaded.setWorkerThreadTree(workerTree); //Damit immer nur ein worker thread arbeitet. Theoretisch kann man ja nach dem Starten des Frames die Combo - Box �ndern. Hier wird dann ein bereits laufender Thread abgebrochen.
					workerTree.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.	
				}
			}else{
				ModelJTreeNodeRootDummyZZZ dummyModel = new ModelJTreeNodeRootDummyZZZ();   //" No disk in drive ... "
				DefaultTreeModel treeModelDummy = new DefaultTreeModel(dummyModel);
				tree.setModel(treeModelDummy);  //Sonst steht dort nur Mist drin.  "Sports, Color, Food", wohl default von Sun
				
				tree.setRootVisible(false);    //Dadurch sind auch Dateien auf Root-Ebene in der List-Box anzeigbar.
				tree.setShowsRootHandles(false);
			}
		}catch(ExceptionZZZ ez){
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getMessage());			
		}
		
	
		
		/*DAS BEREITET PROBLEME, SEITDEM DAS TREEMODEL IN EINEM ANDEREN THREAD ERSTELLT WIRD
		//int icount = treeModel.getChildCount();  //Zu diesem ZEitpunkt ist das Model noch nicht fertig !!!!
		//if(icount>=1) tree.expandRow(0);
		
		
		//3. Setze einen JFileChooser-artigen Renderer
		//tree.setCellRenderer(new DefaultTreeCellRenderer());
		 */
		
		
		//4. Scrollbar hinzuf�gen und in das Formular einbauen
		//!!! Bei l�ngeren Eintr�gen sind einige Eintr�ge nicht erreichbar per Scrollen.
		JScrollPane scrollTree = new JScrollPane(tree);
		scrollTree.setPreferredSize(dimTree);
		this.add(scrollTree, cc.xywh(2,3,3, 2*iNumberOfRow-3));  //beinhaltet also die Button-Refresh Spalte, H�he 2* , wg. der "Trennzeilen"  -3 weil es erst in der 3. Zeile anf�ngt 
		
		//################## LISTE DER DATEIEN eines Verzeichnissed ############################
		//Die Liste h�rt auf den Tree und auf die ComboBox der Laufwerksauswahl
		//Die Liste feuert einen eigenen "EventListFileSelectedVIA"
		DefaultListModel listModel = new DefaultListModel();
		JListFileListening4ComponentResetVIA listListening = new JListFileListening4ComponentResetVIA(objKernel, listModel, listenerComboCascaded, this);
		listListening.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listListening.addListSelectionListener(listenerListSelection);   //Der listener, f�r die Auswahl aus der ListBox
		this.setComponent("list", listListening);   		//Die Dateiliste, auch �ber die Kernel-Funktionalit�t per Alias "bekannt" machen.
		
		//Scrollbar hinzuf�gen und in das Formular einbauen
		//listListening.setPreferredSize(dimTree); //damit soll dies Liste das gleiche Format haben wie der Baum. ABER: Wenn das gesetzt ist, hat JScrollPane Probleme neue Dateien per Scrollen zu erreichen
		JScrollPane scrollList = new JScrollPane(listListening);
		scrollList.setPreferredSize(dimTree); //damit soll dies Liste das gleiche Format haben wie der Baum. 
		this.add(scrollList, cc.xywh(6,3,1, 2*iNumberOfRow-3));
		
		
//		Diese Listbox an den Listener des Trees registrieren. Merke: Der Listener des Trees fungiert gleichzeitig als Sender des "Reset-Events"
		listenerTreeSelection.addListenerSelectionReset(listListening);
		
		//Diese Listbox an den Listener der Combobox f�r die Auswahl des Laufwerks registrieren. Damit bei einer �nderung des Laufwerks die Liste mit den neuen root-Datein gef�llt wird.
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(listListening);
		//FGL 20081001 nun direkt an den EventBroker registrieren  listenerComboRefresh.addListenerSelectionReset(listListening); //Beim Refresh Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(listListening); //FGL 20081001 nun direkt an den EventBroker registrieren //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		
		//Die Dateien des ausgew�hlten Roots (in der Combo-Box) anzeigen
//		Versuch die Performance zu verbessern, indem man einen externen Thread startet
		try{
			//FGL 20080314 Damit nur einmal die Abfrage "Bitte legen Sie eine CD ein" kommt, vor dem Start des Worker Thread auf die Existenz des Pfades abfragen
			if(bRootExists){
				String[] saFlag = null;				
				SwingWorker4ProgramListContentVIA workerList = null; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   listenerComboCascaded.getWorkerThreadList();
				if(workerList == null){
					workerList = new SwingWorker4ProgramListContentVIA(objKernel, this, sRoot, saFlag);
				//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!	listenerComboCascaded.setWorkerThreadList(workerList); //Damit immer nur ein worker thread PRO ALIAS arbeitet. Theoretisch kann man ja nach dem Starten des Frames die Combo - Box �ndern. Hier wird dann ein bereits laufender Thread abgebrochen.
					workerList.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
				}							
			}else{
//				Hinweis auf leeres Verzeichnis setzen
				DefaultListModel modelList = (DefaultListModel)  listListening.getModel();  //Das muss der JList im Konstruktor mitgegeben worden sein.
				modelList.clear();	
				modelList.addElement("No file (" + sRoot + ")");   //Das tritt z.B. bei leeren CDRom-Laufwerk ein
				listListening.setModel(modelList);
			    this.repaint();	
			}
		}catch(ExceptionZZZ ez){
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getMessage());			
		}
		
	
		
		//####################### PRODUKTIVE EINGABEFELDER: AUCH WENN AUSKOMMENTIERT, NICHT L�SCHEN #######
		
		//+++ LABEL als �berschrift 		
		JLabel labelHeader = new JLabel("Data for transmission to server:");
		this.add(labelHeader, cc.xywh(10,2,3,1)); //Soll m�glichst zentriert stehen
		
		JLabel labelTXTFile = new JLabel("Carrier / File Data ...");
		this.add(labelTXTFile, cc.xy(10,3));
		
		//+++ Label: Eigenschaften des Datentr�gers anzeigen
		JLabel labelTXTCarrier = new JLabel("Carrier Title:");
		this.add(labelTXTCarrier, cc.xy(8,4));

		String sRootUI;
		if(bRootExists){
		try{
			sRootUI=CommonUtilVIA.computeDriveTitleByRootFile(fileRoot);
		}catch(ExceptionZZZ ez){
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			sRootUI = JLabel_Listening_CarrierTitleVIA.sTEXT_ERROR;
		}	
		}else{
			sRootUI = JLabel_Listening_CarrierTitleVIA.sTEXT_NO_DRIVE;
		}
		JLabel_Listening_CarrierTitleVIA labelCarrier=  new JLabel_Listening_CarrierTitleVIA(objKernel, sRootUI);
		
//		!!! Wenn sich die Auswahl in der Dateilste �ndert, dann soll auch der Datentr�gertiltel aktualisiert werden. Das liegt an dem Problem, dass Windows bei neu eingelgten CDs den Titel z.B. als "CD-DVD-ROM Laufwerk" identifiziert, was nicht aktuell ist.
		listenerListSelection.addListenerFileSelection(labelCarrier);
		
		//!!! Wenn sich die Auswahl im Tree oder in der Combo-Box �ndert, dann werden hier ResetEvents "k�nstlich" erzeugt und an dieses Label geschickt.		
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(labelCarrier);  //Das �ndert sich nur, wenn sich die Auswal in der Combo-Box �ndert.
 //FGL 20081001 nun direkt an den EventBroker registrieren listenerComboRefresh.addListenerSelectionReset(labelCarrier);
		objEventDriveBroker.addListenerSelectionReset(labelCarrier);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		
		//labelCarrier.setPreferredSize(dim); //Keine Prefered Size f�r Labels, sondern nur f�r TextFields		
		this.setComponent("labelCarrierTitle", labelCarrier);
		this.add(labelCarrier, cc.xywh(10,4, 1, 1)); //Wenn das nur 1 Feld breit ist, hat man daneben noch Platz
		
		//+++ Label und Combo-Box: Datentr�gertyp
		JLabel labelTXTCarrierType = new JLabel("Carrier type:");
		this.add(labelTXTCarrierType, cc.xy(12, 4));
		
		String sDriveAlias = "";
		if(bRootExists){
			try{
				sDriveAlias = JComboBox_Listening_CarrierTypeVIA.computeDriveAliasByRootFile(fileRoot);
			} catch (ExceptionZZZ ez) {
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Keine DETAILS �ber das ausgew�hlte Laufwerk ermittelbar. " + ez.getDetailAllLast());
			}
		}
		
		//Combo - Box hat als Vorbelegung den CarrierType des Laufwerks, das in der Auswahl f�r den Laufwerksbuchstaben enthalten ist.
		JComboBox_Listening_CarrierTypeVIA comboCarrierType = new JComboBox_Listening_CarrierTypeVIA(objKernel, sDriveAlias);
		comboCarrierType.addItem("CD");
		comboCarrierType.addItem("DVD");
		comboCarrierType.addItem("HD");
		
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(comboCarrierType);  //Das �ndert sich nur, wenn sich die Auswahl des Laufwerks/des Roots in der Combo-Box �ndert.
//		FGL 20081001 nun direkt an den EventBroker registrieren listenerComboRefresh.addListenerSelectionReset(comboCarrierType);   //Beim Refresh-Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(comboCarrierType);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		comboCarrierType.setPreferredSize(dimHalft);
		this.setComponent("comboCarrierType", comboCarrierType);
		this.add(comboCarrierType, cc.xywh(14,4,1,2)); //weil es eine Combo Box ist, noch die untere (kleine) Zeile einbinden
		
		
		//IDEE: Wenn die CarrierID eingegeben wird, dann sucht das Servlet selbst nach der Nummer
		//TODO: Hier ein Feld f�r die CarrierID incl. F�hrungtext erstellen
		JLabel labelTXTCarrierId = new JLabel("Carrier Id:");
		this.add(labelTXTCarrierId, cc.xy(8,6));
		
		JTextFieldListening4ComponentSelectionResetVIA textCarrierId = new JTextFieldListening4ComponentSelectionResetVIA(objKernel,this,  "");
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(textCarrierId); //Das �ndert sich nun bei einer �nderung des Laufwerks
//		FGL 20081001 nun direkt an den EventBroker registrieren listenerComboRefresh.addListenerSelectionReset(textCarrierId);   //Beim Refresh-Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(textCarrierId);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		textCarrierId.setPreferredSize(dimSingle);
		this.setComponent("textCarrierId", textCarrierId);
		this.add(textCarrierId, cc.xy(10, 6));
		
		
		//+++ TEXTFIELDer f�r die Berechnung der CarrierId: BRENNDATUM +  fortlaufende Nummer (hat die Form JJMMDDX, also Jahr, Monat Tag, fortlaufende Nummer)	
		//IDEE: Wenn in der Carrier Sequenz Number nix eingegeben wird,  dann holt sich das Servlet die 2. Nummer durch Suche nach Datentr�gernamen und Brenndatum
		JLabel labelTXTCarrierCreated = new JLabel("Carrier created:");
		this.add(labelTXTCarrierCreated, cc.xy(8, 8));
		
		String sDateCarrierLastModified = "";
		if(bRootExists) sDateCarrierLastModified = CommonUtilVIA.computeDateLastModifiedByFile(fileRoot);
		JTextField_Listening_CarrierDateVIA textCarrierCreated = new JTextField_Listening_CarrierDateVIA(objKernel,  sDateCarrierLastModified);   
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(textCarrierCreated); //Das �ndert sich nun bei einer �nderung des Laufwerks
 //FGL 20081001 nun direkt an den EventBroker registrieren listenerComboRefresh.addListenerSelectionReset(textCarrierCreated);   //Beim Refresh-Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(textCarrierCreated);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		textCarrierCreated.setPreferredSize(dimSingle);
		this.setComponent("textCarrierCreated", textCarrierCreated);
		this.add(textCarrierCreated, cc.xy(10,8));
		
		JLabel labelTXTCarrierSequenze = new JLabel("Carrier sequence #:");
		this.add(labelTXTCarrierSequenze, cc.xy(12, 8));
				
		JTextFieldListening4ComponentSelectionResetVIA textCarrierSequenze = new JTextFieldListening4ComponentSelectionResetVIA(objKernel, this, "");		
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(textCarrierSequenze);
//		FGL 20081001 nun direkt an den EventBroker registrieren listenerComboRefresh.addListenerSelectionReset(textCarrierSequenze); //Beim Refresh Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(textCarrierSequenze);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		textCarrierSequenze.setPreferredSize(dimHalft);
		this.setComponent("textCarrierSequenze", textCarrierSequenze);
		this.add(textCarrierSequenze, cc.xy(14, 8));
		
		
		//+++ TEXTFIELD FILENAME, 
		JLabel labelTXTFilename = new JLabel("Filename:");
		this.add(labelTXTFilename, cc.xy(8,10));
		
		//Merke: Das ist ein TextField, damit der Inhalt gescrollt und "ausschneidbar" ist.
		JText_Listening_FileNameVIA textFilename = new JText_Listening_FileNameVIA(objKernel, JText_Listening_FileNameVIA.sTEXT_INITIAL);
		textFilename.setEditable(false); //Es soll nur Scrollbar und "ausschneidbar" sein
		textFilename.setPreferredSize(dimDouble);
		
		//Bei Auswahl eines neuen Laufwerks oder eines neuen Verzeichnisses zur�cksetzen. Bei Auswahl einer Datei aus der Liste: Setzen.
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(textFilename);
//		FGL 20081001 nun direkt an den EventBroker registrierenlistenerComboRefresh.addListenerSelectionReset(textFilename); //Beim Refresh Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(textFilename);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		listenerTreeSelection.addListenerSelectionReset(textFilename);
		listenerListSelection.addListenerFileSelection(textFilename);
		this.setComponent("textFileName", textFilename);
		JScrollPane scrollFileName = new JScrollPane(textFilename);
		this.add(scrollFileName, cc.xywh(10,10, 5, 2));  //wg. des JScrollPane wird die (kleine) Zeile darunter auch noch genommen.
				
//		+++ LABEL FILEDATE
		JLabel labelTXTFileDate = new JLabel("Date last modified:");
		this.add(labelTXTFileDate, cc.xy(8,12));
		
		JLabel_Listening_FileDateVIA labelFileDate = new JLabel_Listening_FileDateVIA(objKernel, JLabel_Listening_FileSizeVIA.sTEXT_INITIAL);
//		Bei Auswahl eines neuen Laufwerks oder eines neuen Verzeichnisses zur�cksetzen. Bei Auswahl einer Datei aus der Liste: Setzen.
//		FGL 20081001 nun direkt an den EventBroker registrierenlistenerComboSelection.addListenerSelectionReset(labelFileDate);
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboRefresh.addListenerSelectionReset(labelFileDate); //Beim Refresh Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(labelFileDate);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		listenerTreeSelection.addListenerSelectionReset(labelFileDate);
		listenerListSelection.addListenerFileSelection(labelFileDate);
		this.setComponent("labelFileDate", labelFileDate);
		this.add(labelFileDate, cc.xy(10,12));
		
		//+++ LABEL FILESIZE
		JLabel labelTXTFileSize = new JLabel("File size (in MB):");
		this.add(labelTXTFileSize, cc.xy(12,12));
		
		JLabel_Listening_FileSizeVIA labelFileSize = new JLabel_Listening_FileSizeVIA(objKernel, JLabel_Listening_FileSizeVIA.sTEXT_INITIAL);
//		Bei Auswahl eines neuen Laufwerks oder eines neuen Verzeichnisses zur�cksetzen. Bei Auswahl einer Datei aus der Liste: Setzen.
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(labelFileSize);
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboRefresh.addListenerSelectionReset(labelFileSize); //Beim Refresh Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(labelFileSize);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		listenerTreeSelection.addListenerSelectionReset(labelFileSize);
		listenerListSelection.addListenerFileSelection(labelFileSize);
		this.setComponent("labelFileSize", labelFileSize);
		this.add(labelFileSize, cc.xy(14,12));
		
		//+++ COMBO CompressionType
		JLabel labelTXTFileCompression = new JLabel("File compression:");
		this.add(labelTXTFileCompression, cc.xy(12,14));
		
		//HINTERGRUND: Items dieser ComboBox werden aus einer Datei geholt.
		//TODO // IDEE: In einer extra Maske diese per spezieller Servlet Abfrage aktualisieren.
		//!!! Diese Einstellung �ndert sich nicht, darum ist das kein Listener
		JComboBox comboFileCompression = new JComboBox();
		
		//1. die Datei auslesen:
		java.io.File fileCatalogFileCompression =null;
		try{
			fileCatalogFileCompression = objKernel.getParameterFileByProgramAlias(sModule, sProgram, "CatalogFileCompressionFilename");
			
			//Falls die Datei nicht existiert, dort erzeugen und mit Vorbelegung versehen.
			if(fileCatalogFileCompression.exists()==false){
				IKernelConfigSectionEntryZZZ objEntry =  objKernel.getParameterByProgramAlias(sModule, sProgram, "CatalogFileCompressionFilename");
				String sCatalogFileCompression = objEntry.getValue();
				if(!StringZZZ.isEmpty(sCatalogFileCompression)){					
					try {
						Stream stream = new Stream(fileCatalogFileCompression.getPath(), 1);
						stream.println("DivX 5.xx");
						stream.println("DivX 4.xx");
						stream.println("DivX 3.xx");
						stream.println("Other");
						stream.println("None");
						stream.flush();
						stream.close();
					} catch (FileNotFoundException e) {					
						ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
						ReportLogZZZ.write(ReportLogZZZ.ERROR, "Configured 'CatalogFileCompressionFilename'="+ fileCatalogFileCompression.getPath() + " for local catalog does not exist");
					} catch (IOException e) {
						ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
						ReportLogZZZ.write(ReportLogZZZ.ERROR, "Unable to write default entries in configured 'CatalogFileCompressionFilename'="+ fileCatalogFileCompression.getPath() + " for local catalog.");
					}					
				}
			}			
		} catch (ExceptionZZZ e) {		
			ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
		}
		
		//2. die Datei auswerten und in die combo-Box f�llen		
		if(fileCatalogFileCompression!=null){			
				try {
					FileReader fread = new FileReader(fileCatalogFileCompression);
					BufferedReader bfread = new BufferedReader(fread);
					String sLine = bfread.readLine();
					while(sLine!=null){
						comboFileCompression.addItem(sLine);
						sLine = bfread.readLine();
					}
					bfread.close();
					fread.close();					
				} catch (FileNotFoundException e) {					
					ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "Configured 'CatalogFileCompressionFilename'="+ fileCatalogFileCompression.getPath() + " for local catalog does not exist");
				} catch (IOException e) {
					ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "Unable to read line in configured 'CatalogFileCompressionFilename'="+ fileCatalogFileCompression.getPath() + " for local catalog.");
				}
		}
		/* Diese Combo Box soll nicht auf �nderungen der Auswahl reagieren
		listenerComboSelection.addListenerSelectionReset(comboSerieTitle);
		listenerTreeSelection.addListenerSelectionReset(comboSerieTitle);
		listenerListSelection.addListenerFileSelection(comboSerieTitle);
		*/
		
		comboFileCompression.setEditable(false);  //Wenn man diese ComboBox Editierbar macht, dann m�sste beim Verschicken der Daten auf "Vorhandensein" in der  TXT-Datei gepr�ft werden, etc.
		this.setComponent("comboFileCompression", comboFileCompression);
		this.add(comboFileCompression, cc.xywh(14,14, 1, 1));
		
//		+++ TEXTAREA FILEREMARK
		JLabel labelTXTFileRemark = new JLabel("File Remark:");
		this.add(labelTXTFileRemark, cc.xy(8,16));
		
		//FGL 20080111: Es findet nun ggf. eine Vorbelegung statt   JTextAreaListening4ComponentSelectionResetVIA textaFileRemark = new JTextAreaListening4ComponentSelectionResetVIA(objKernel, "", 5, 20);
		 JTextArea_Listening_FileRemarkVIA textaFileRemark = new JTextArea_Listening_FileRemarkVIA(objKernel, "", 5, 20);
		
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(textaFileRemark);
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboRefresh.addListenerSelectionReset(textaFileRemark);   //Beim Refresh-Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(textaFileRemark); //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		listenerTreeSelection.addListenerSelectionReset(textaFileRemark);
		listenerListSelection.addListenerFileSelection(textaFileRemark);
		this.setComponent("textaFileRemark", textaFileRemark);
		JScrollPane scrollFileRemark = new JScrollPane(textaFileRemark);
		this.add(scrollFileRemark, cc.xywh(10,16, 5, 2));
	
		
		//zwischen Datei  und Movie bleibt eine richtige Zeile frei
		JLabel labelTXTMovieData = new JLabel("Episode / Movie data.....");
		this.add(labelTXTMovieData, cc.xywh(10,19,3,1));
		
		//+++ COMBOBOX - SERIE AUSW�HLEN
		JLabel labelTXTSerieTitle = new JLabel("Choose serie:");
		this.add(labelTXTSerieTitle, cc.xy(8,20));
		
		//HINTERGRUND: Items dieser ComboBox werden aus einer Datei geholt.
		//TODO / IDEE: In einer extra Maske diese per spezieller Servlet Abfrage aktualisieren.
		JComboBoxListening_SerieTitleVIA comboSerieTitle = new JComboBoxListening_SerieTitleVIA(objKernel, " ");
		comboSerieTitle.addItem(" ");
		
		//1. die Datei auslesen:
		java.io.File fileCatalogSerieTitle=null;
		try{
			fileCatalogSerieTitle = objKernel.getParameterFileByProgramAlias(sModule, sProgram, "CatalogSerieTitleFilename");
		} catch (ExceptionZZZ e) {		
			ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
		}
		
		//2. die Datei auswerten und in die combo-Box f�llen		
		if(fileCatalogSerieTitle!=null){			
				try {
					FileReader fread = new FileReader(fileCatalogSerieTitle);
					BufferedReader bfread = new BufferedReader(fread);
					String sLine = bfread.readLine();
					while(StringZZZ.isEmpty(sLine)==false){
						comboSerieTitle.addItem(sLine);
						sLine = bfread.readLine();
					}
					bfread.close();
					fread.close();					
				} catch (FileNotFoundException e) {					
					ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "Configured 'CatalogSerieFilename'="+ fileCatalogSerieTitle.getPath() + " for local catalog does not exist");
				} catch (IOException e) {
					ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "Unable to read line in configured 'CatalogSerieFilename'="+ fileCatalogSerieTitle.getPath() + " for local catalog.");
				}
		}
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(comboSerieTitle);
		 //FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboRefresh.addListenerSelectionReset(comboSerieTitle);   //Beim Refresh-Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(comboSerieTitle);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		listenerTreeSelection.addListenerSelectionReset(comboSerieTitle);
		listenerListSelection.addListenerFileSelection(comboSerieTitle);
		comboSerieTitle.setEditable(false);  //Wenn man diese ComboBox Editierbar macht, dann m�sste beim Verschicken der Daten auf "Vorhandensein" in der  TXT-Datei gepr�ft werden, etc.
		this.setComponent("comboSerieTitle", comboSerieTitle);
		this.add(comboSerieTitle, cc.xywh(10,20, 3, 1));
		
		//+++ BUTTON - Aus Katalog entfernen.
		JButton buttonSerieTitleRemove = new JButton("- catalog");
		buttonSerieTitleRemove.setPreferredSize(dimSingle);
		ActionCatalogSerieTitleRemoveVIA actionSerieTitleRemove = new ActionCatalogSerieTitleRemoveVIA(objKernel, this);
		buttonSerieTitleRemove.addActionListener(actionSerieTitleRemove);
		this.add(buttonSerieTitleRemove, cc.xywh(14, 20, 1, 1));
		
		
		//+++ TEXTFIELD SERIETITLE
		//Dieses Textfield dient dazu neue Elemente sowohl der ComboBox als auch der Datei hinzuzuf�gen.
		//Er muss immer wieder geleert werden, wenn eine neue Datei ausgew�hlt wurde, oder wenn die Serie der ComboBox hinzugef�gt worden ist.
		JLabel labelTXTSerieTitleNew = new JLabel("New serie: ");
		this.add(labelTXTSerieTitleNew, cc.xywh(8, 22,1,1));
		
		//!!!Merke, dadurch, dass dieses TextField auf die Auswahl�nderung des Verzeichnisse, des Laufwerks, der Datei achtet, wird der Inahlt zur�ckgesetzt.
		JTextFieldListening4ComponentSelectionResetVIA textSerieTitleNew = new JTextFieldListening4ComponentSelectionResetVIA(objKernel, this, "");
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(textSerieTitleNew);
		listenerTreeSelection.addListenerSelectionReset(textSerieTitleNew);
		listenerListSelection.addListenerFileSelection(textSerieTitleNew);
		JScrollPane scrollSerieTiltleNew = new JScrollPane(textSerieTitleNew); 
		this.setComponent("textSerieTitleNew", textSerieTitleNew); //das Textfield bekannt machen, zkernel-funktionalit�t		
		this.add(scrollSerieTiltleNew, cc.xywh(10, 22,3,2));//wg. des JScrollPane werden die (kleine) Zeilen darunter plus die gro�e Ziele darunter auch noch genommen.
		
		//+++ BUTTON - SERIE HINZUF�GEN (zur ComboBox und zur TXT-Datei.
		JButton buttonSerieTitleUpdate = new JButton("+ catalog");
		buttonSerieTitleUpdate.setPreferredSize(dimSingle);
		ActionCatalogSerieTitleUpdateVIA actionSerieTitleUpdate = new ActionCatalogSerieTitleUpdateVIA(objKernel, this);
		buttonSerieTitleUpdate.addActionListener(actionSerieTitleUpdate);
		this.add(buttonSerieTitleUpdate, cc.xywh(14, 22, 1, 2));
		
		//+++ TEXTFIELD MOVIETITLE
		JLabel labelTXTMovieTitle = new JLabel("Title:");
		this.add(labelTXTMovieTitle, cc.xy(8,26));
		
		JTextField_Listening_MovieTitleVIA textMovieTitle = new JTextField_Listening_MovieTitleVIA(objKernel, JTextField_Listening_MovieTitleVIA.sTEXT_INITIAL);
		//Bei Auswahl eines neuen Laufwerks oder eines neuen Verzeichnisses zur�cksetzen. Bei Auswahl einer Datei aus der Liste: Setzen.
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(textMovieTitle);
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboRefresh.addListenerSelectionReset(textMovieTitle); //Beim Refresh Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(textMovieTitle);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button		
		listenerTreeSelection.addListenerSelectionReset(textMovieTitle);
		listenerListSelection.addListenerFileSelection(textMovieTitle);
		this.setComponent("textMovieTitle", textMovieTitle);
		JScrollPane scrollMovieTitle = new JScrollPane(textMovieTitle);
		this.add(scrollMovieTitle, cc.xywh(10,26, 5, 2));  //wg. des JScrollPane werden die (kleine) Zeilen darunter plus die gro�e Ziele darunter auch noch genommen.
		
//		Weil der Title noch ScrollBars bekommen kann, bleibt eine Zeile frei
		
		//+++ TEXTAREA MOVIEREMARK
		JLabel labelTXTMovieRemark = new JLabel("Remark:");
		this.add(labelTXTMovieRemark, cc.xy(8,28));
		
		JTextAreaListening4ComponentSelectionResetVIA textaMovieRemark = new JTextAreaListening4ComponentSelectionResetVIA(objKernel, "", 5, 20);
		//das Vorbelegen der Werte komm raus.  FGL 20080111:    JTextArea_Listening_MovieRemarkVIA textaMovieRemark = new JTextArea_Listening_MovieRemarkVIA(objKernel, "", 5, 20);
		 
//		FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(textaMovieRemark);
		 //FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboRefresh.addListenerSelectionReset(textaMovieRemark); //Beim Refresh Button soll der Eintrag leer gesetzt werden
		objEventDriveBroker.addListenerSelectionReset(textaMovieRemark);  //FGL 20081001 nun direkt an den EventBroker registrieren  //Beim Refresh Button soll der Eintrag leer gesetzt werden, Das passiert auch beim CD-Open-Button
		listenerTreeSelection.addListenerSelectionReset(textaMovieRemark);
		listenerListSelection.addListenerFileSelection(textaMovieRemark);
		this.setComponent("textaMovieRemark", textaMovieRemark);
		JScrollPane scrollMovieRemark = new JScrollPane(textaMovieRemark);
		this.add(scrollMovieRemark, cc.xywh(10,28, 5, 2));
		
		//+++ BUTTON - DATEN AN SERVER SENDEN
		JButton buttonDataExportServer = new JButton("Send data to server");
		buttonDataExportServer.setPreferredSize(dimDouble);
		ActionDataSendVIA actionDataSend = new ActionDataSendVIA(objKernel, this);
		buttonDataExportServer.addActionListener(actionDataSend);
		this.add(buttonDataExportServer, cc.xywh(10, 30, 2,1)); //von 5 auf weite 2 verkleinert, damit der neue buttonDataExportLocal auf die gleiche zeile passt
		
		JButton buttonDataExportLocal = new JButton("Send data to file");
		buttonDataExportLocal.setPreferredSize(dimDouble);
		ActionDataSaveVIA actionDataSave = new ActionDataSaveVIA(objKernel, this);
		buttonDataExportLocal.addActionListener(actionDataSave);
		this.add(buttonDataExportLocal, cc.xywh(13, 30, 2,1)); //von 5 auf weite 2 verkleinert, damit der neue buttonDataExportLocal auf die gleiche zeile passt
	
		
		
		/*DAS WIRD NUN EINE TEXTAREA
//		+++ TEXTFIELD STATUS DER �BERTRAGUNG, 
		//Merke: Das ist ein TextField, damit der Inhalt gescrollt und "ausschneidbar" ist.
		JTextFieldListening4ComponentSelectionResetVIA textExportStatus = new JTextFieldListening4ComponentSelectionResetVIA(objKernel, "Status: GUI Initialized.");
		textExportStatus.setEditable(false); //Es soll nur Scrollbar und "ausschneidbar" sein
		textExportStatus.setPreferredSize(dimDouble);
		
		//Bei Auswahl eines neuen Laufwerks oder eines neuen Verzeichnisses zur�cksetzen. Bei Auswahl einer Datei aus der Liste: Setzen.
		listenerComboSelection.addListenerSelectionReset(textExportStatus);
		listenerTreeSelection.addListenerSelectionReset(textExportStatus);
		listenerListSelection.addListenerFileSelection(textExportStatus);
		this.setComponent("textExportStatus", textExportStatus);
		JScrollPane scrollExportStatus = new JScrollPane(textExportStatus);
		this.add(scrollExportStatus, cc.xywh(6,32, 7, 2));  //wg. des JScrollPane wird die (kleine) Zeile darunter auch noch genommen.
		 */
		
//		+++ JEDITORPANE:  STATUS DER �BERTRAGUNG.		
			JEditorPane_Listening_ExportStatusVIA editor = new JEditorPane_Listening_ExportStatusVIA(objKernel, "GUI initialized");		
			//Dimension dim = new Dimension(iEDITOR_WIDTH, iEDITOR_HEIGHT);
			editor.setPreferredSize(dimDouble);//Das funktioniert nicht, wahrscheinlich, weil frame.pack gemacht wird:    .setSize(100,100);
			editor.setContentType("text/html");
			editor.setEditable(false);
			
			JScrollPane paneScroll = new JScrollPane(editor);	
			editor.setCaretPosition(0);
			this.setComponent("editorExportStatus", editor);
		
		//Bei Auswahl eines neuen Laufwerks oder eines neuen Verzeichnisses zur�cksetzen. Bei Auswahl einer Datei aus der Liste: Setzen.			
//			FGL 20081001 nun direkt an den EventBroker registrieren		listenerComboSelection.addListenerSelectionReset(editor);
			//nein, default ist  der ecomponent.text.()     editor.setFlag("useeventresetdefault", true);  //Damit wird der Pane leergesetzt
			objEventDriveBroker.addListenerSelectionReset(editor);
			
		listenerTreeSelection.addListenerSelectionReset(editor);
		listenerListSelection.addListenerFileSelection(editor);
		this.add(paneScroll, cc.xywh(8,32, 7, 2));  //wg. des JScrollPane wird die (kleine) Zeile darunter auch noch genommen.
		
		
		
		/* NICHT L�SCHEN !!!
		
		//###################### BEISPIEL UND TEST/DEBUG M�glichkeit ###########################
		//Dimension dim = new Dimension(10, 15);
		//label.setPreferredSize(dim);
		
		
//		### Der TreeSelectionListener ist die Quelle f�r den ResetEvent. Hier muss sich das Label registrieren...
		//Label als Feldf�hrungstext f�r dieses DebugFeld:
		JLabel labelTree = new JLabel("Selection im Tree: ");
		this.add(labelTree, cc.xy(6,2));
		
//		Label, das auf den Event h�rt und sich demnach ver�ndern kann an den Listener f�r die Tree-Auswahl registrieren und zur Maske hinzuf�gen
		KernelJLabelListening4ComponentSelectionResetZZZ labelTreeListening = new KernelJLabelListening4ComponentSelectionResetZZZ(objKernel, "Nothing selected");
		listenerTreeSelection.addListenerSelectionReset(labelTreeListening);
		this.add(labelTreeListening, cc.xywh(8,2, 3, 1));
		
		
		//### Der ComboSelectionListener als Quelle f�r den ResetEvent. Hier muss sich das Label registrieren....
//		Label als Feldf�hrungstext f�r dieses DebugFeld:
		JLabel labelCombo = new JLabel("Selection in ComboBox: ");
		this.add(labelCombo, cc.xy(6,4));
		
//		Label, das uf den Event h�rt und sich demnach ver�ndern kann an den Listener f�r die ComboBox-Auswahl registrieren und der Maske hinzuf�gen
		KernelJLabelListening4ComponentSelectionResetZZZ labelComboListening = new KernelJLabelListening4ComponentSelectionResetZZZ(objKernel, "Nothing selected");
		listenerComboSelection.addListenerSelectionReset(labelComboListening);		
		this.add(labelComboListening, cc.xywh(8,4, 3, 1));
		
		
		
		//### Der List FileSelectedListener als Quelle f�r den FileSelectedEvent. Hier muss sich das Label registrieren....
		//Label als F�hrungstext f�r dieses Debugfeld:
		JLabel labelList = new JLabel("File selected in ListBox: ");
		this.add(labelList, cc.xy(6,6));
		
		//Label, das auf den Event h�rt und sich demnach ver�ndern kann an den Listener f�r die ListBox-Auswahl registrieren und zur Maske hinzuf�gen
		JLabelListening4Reset_SelectionVIA labelListListening = new JLabelListening4Reset_SelectionVIA(objKernel, "Nothing selected");
		listenerListSelection.addListenerFileSelection(labelListListening);
		this.add(labelListListening, cc.xywh(8,6, 3, 1));
		
		//!!! Wenn sich die Auswahl im Tree oder in der Combo-Box �ndert, dann werden hier ResetEvents "k�nstlich" erzeugt und an dieses Label geschickt.
		listenerTreeSelection.addListenerSelectionReset(labelListListening);	
		listenerComboSelection.addListenerSelectionReset(labelListListening);
		
		//### Der List FileSelectListener als Quelle f�r den FileSelectedEvent. Hier muss sich das TextField regitstrieren....
		JLabel labelListSize = new JLabel("Size of file (in MB): ");
		this.add(labelListSize, cc.xy(6, 8));
		
		//TextField, das auf den Event h�rt und sich demnach ver�ndern kann, an den Listener f�r die ListBox-Auswahl registrieren und zur Maske hinzuf�gen
		JLabel_Listening_SizeVIA textListSize = new JLabel_Listening_SizeVIA(objKernel, "0");
		listenerListSelection.addListenerFileSelection(textListSize);
		
		Dimension dimText = new Dimension(200, 15);
		textListSize.setPreferredSize(dimText);
		this.add(textListSize, cc.xy(8,8));
		
		//!!! Wenn sich die Auswahl im Tree oder in der Combo-Box �ndert, dann werden hier ResetEvents "k�nstlich" erzeugt und an dieses Label geschickt.
		listenerTreeSelection.addListenerSelectionReset(textListSize);	
		listenerComboSelection.addListenerSelectionReset(textListSize);
		
		
		//DEBUG NEU 20070206
		//### Diese Kernel-Komponente ist eine Combo-Box, die auf den Reset-Event h�rt, der von der Combo-Box zur Laufwerksauswahl abgefeuert wird. 
		KernelJComboBoxListening4ComponentSelectionResetZZZ comboCarrierType = new KernelJComboBoxListening4ComponentSelectionResetZZZ(objKernel);		
		comboCarrierType.addItem("C:\\");   //Standardm��ig wird im doReset(...) auf ein Item des gleichen Namens gesetzt. Dadurch das man die ComboBox so f�llt, kann man das nachvollziehen
		comboCarrierType.addItem("D:\\");
		comboCarrierType.addItem("E:\\");
				
		listenerComboSelection.addListenerSelectionReset(comboCarrierType);  //Das �ndert sich nur, wenn sich die Auswal in der Combo-Box �ndert.		
		this.add(comboCarrierType, cc.xy(12,4));
		
		
		*/ //ENDE DER DEBUGM�GLICHKEIT
		
		/* Das funktioniert nicht. Warum ???
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.addLabel("Externe IP Adresse des Servers");
		JTextField textfield = new JTextField("noch automatisch zu f�llen");
		builder.add(textfield, cc.xy(3,2));
		*/
		
	}
	
	/** Klasse wird dem Button CD OPEN/CLOSE als ActionListener hinzugef�gt.
	 *    �ber das �ffnen und Schliesen hinaus, muss noch gemacht werden
	 *    -- Start des Swing Worker Threads zum Einlesen des JTrees und des Root-Inhalts
	 *    		(TODO: Das Interface IJTreeRefresher erstellen und hier implementieren.)
	 *    
	 *    -- Feuer den Event ab, der ander Komponenten dar�ber benachrichtigt, dass ein neuer Datentr�ger eingelegt worden ist.
	 *        (Daher implementiert diese Klasse das IEventBrokerUserZZZ Interface)
	 *        Das Abfeuern �bernimmt ein EventBroker, der auch im "RefreshButton" verwendet wird. Daher brauchen sich die Komponenten nur einmal am EventBroker zu registrieren. 
	 *        
	 * @author lindhaueradmin
	 *
	 */
	class ActionCdDriveOpenCloseVIA extends KernelActionCascadedZZZ implements IEventBrokerUserZZZ{
		private boolean bDriveOpened=false;
		private SwingWorker4ProgramTreeContentVIA workerTree =null;
		private KernelSenderComponentSelectionResetZZZ objEventDriveBroker = null;
		
		public ActionCdDriveOpenCloseVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, KernelSenderComponentSelectionResetZZZ objEventDriveBroker){
			super(objKernel, panelParent);
			this.setSenderUsed(objEventDriveBroker);
		}

		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			boolean bReturn = true;
			main:{
				//Den Laufwerksbuchstaben des gerade gew�hlten Laufwerks auslesen
				KernelJPanelCascadedZZZ panelParent = this.getPanelParent();
				JComboBox comboDrive = (JComboBox) panelParent.getComponent("combo");
				
				String sDrive = (String) comboDrive.getSelectedItem();
				//TODO: Ist das Laufwerk auch ein CD-Laufwerk ?
				/*
				 * 	if(FileEasyZZZ.isRoot(fileRoot)==false) break main;
			FileSystemView fileSystemView = FileSystemView.getFileSystemView();
			
			sReturn = fileSystemView.getSystemTypeDescription(fileRoot);
				 */
				
				if(this.bDriveOpened==true){			
				  char ctemp = sDrive.toCharArray()[0];
				  CdRom.setCD(CdRom.CLOSE_CDROM, ctemp ); // nur Laufwerk V close (Merke: Ohne Doppelpunkt etc.)
				  
				  
				}else{
					char ctemp = sDrive.toCharArray()[0];
					CdRom.setCD(CdRom.OPEN_CDROM, ctemp); // nur Laufwerk V open
					  
					 this.bDriveOpened=true;
					 
					 //TODO: Externen Thread starten, der auch beim Schliessen per Button abgebrochen wird.
					 //TODO: Im Extenenen Thread eine Verz�gerung von 30 Sekunden einbauen.
					 //TODO: Im Externen Thread: Wird die Root der CD-gefunen... den Event losschicken, das der JTree und die JList zu refreshen sind (s. Refresh Button)
					 String sDriveRoot = sDrive;
					 java.io.File fileRoot = new java.io.File(sDriveRoot);
					 boolean bFound = false;
						do{
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Warte auf neuen Datentr�ger " + sDriveRoot + ".");
							//if(FileEasyZZZ.isRoot(fileRoot)==false) bFound = true;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
								ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
							}
							bFound = fileRoot.exists();
						}while(bFound == false);
						System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Neuen Datentr�ger gefunden " + sDriveRoot + ".");
					 
						 this.bDriveOpened=false;
						 try{
							 System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Warte auf das Laufwerk " + sDriveRoot + ".");
							 Thread.sleep(5000); //Warte bis die CD vom Betriebssystem wirklich gelesen wurde.
						} catch (InterruptedException e) {
							e.printStackTrace();
							ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getMessage());
						}
				
						 //Starte erst jetzt den Worker Tree, der die Baumeintr�ge ermittelt
//						### TREE
							//+++ Versuch die Performance zu verbessern, indem man einen externen Thread startet			
							//Falls es schon einen anderen worker gibt, der l�uft, diesen beenden.
							if(this.workerTree!=null){
								ReportLogZZZ.write(ReportLogZZZ.DEBUG , "Es gibt schon einen anderen WorkerThread f�r Tree.(1)");
								System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Es gibt schon einen anderen WorkerThread f�r Tree.(2)");
								this.workerTree.interrupt();
								ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Versuche den alten WorkerThread f�r Tree zu interrupten.(1)");
								System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#Versuche den alten WorkerThread f�r Tree zu interrupten.(2)");
							}
							
							//Neuer Worker
							String[] saFlagTree = null;			
							this.workerTree = new SwingWorker4ProgramTreeContentVIA(objKernel, panelParent, sDriveRoot, saFlagTree);
							this.workerTree .start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.

						//### Den Event starten, das ein neuer Datentr�ger/Laufwerk gew�hlt wurde
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "#EVENTEVENT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							EventComponentSelectionResetZZZ eventNew= new EventComponentSelectionResetZZZ(comboDrive, 10002, sDriveRoot);
							this.getSenderUsed().fireEvent(eventNew);
						 
				}
				
			}//end main:
			return bReturn;
		}

		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
			// TODO Auto-generated method stub
			
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			// TODO Auto-generated method stub
			
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}

		//### Interface IEventBrokerUser
		public KernelSenderComponentSelectionResetZZZ getSenderUsed() {
			return this.objEventDriveBroker;
		}

		public void setSenderUsed(KernelSenderComponentSelectionResetZZZ objEventSender) {
			this.objEventDriveBroker = objEventSender;			
		}
		
	}
	

//	#######################################
	//Innere Klassen, welche eine Action behandelt		
	class ActionCatalogSerieTitleUpdateVIA extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{
		//private JPanel panelParent;
				
		public ActionCatalogSerieTitleUpdateVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			boolean bReturn = true;
			main:{
			//try {
				 
				//Einige Test- /Protokollausgaben					
				//System.out.println("Anzahl der Componenten im Parent-Panel: " + panelSubSouth.getComponentCount());
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Update 'Catalog serie title.'");
										
				//00.Handle auf das zu verarbeitende TextFeld.									
				KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
				JTextField textSerieTitleNew = (JTextField) panelParent.getComponent("textSerieTitleNew");
				if(textSerieTitleNew==null){
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "component with alias 'textSerieTitleNew' not found.");
					bReturn = false;
					break main;
				}
				
				//0. Validieren des Feldes: Steht da was drin
				String sValue = textSerieTitleNew.getText();
				if(StringZZZ.isBlank(sValue)){
					ReportLogZZZ.write(ReportLogZZZ.INFO, "no valid text-value found in component with alias 'textSerieTitleNew'.");
					bReturn = false;
					break main;
				}
				
				//00. Handle auf die zu aktualisierende ComboBox
				JComboBox comboSerieTitle = (JComboBox) panelParent.getComponent("comboSerieTitle");
				if(comboSerieTitle==null){
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "component with alias 'comboSerieTitle' not found.");
					bReturn = false;
					break main;
				}
				
				
				//################################################################
//				Wichtige Informationen, zum Auslesen von Parametern aus der KernelConfiguration
				KernelJFrameCascadedZZZ frameParent = (KernelJFrameCascadedZZZ) this.getFrameParent();
				String sProgram = frameParent.getClass().getName(); //der Frame, in den dieses Panel eingebettet ist
				
				String sModule = KernelUIZZZ.searchModuleFirstConfiguredClassname(frameParent); 
				if(StringZZZ.isEmpty(sModule)){
					ExceptionZZZ ez = new ExceptionZZZ("No module configured for the parent frame/program: '" +  sProgram + "'", iERROR_CONFIGURATION_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//1. Daten zur TXT-Datei hinzuf�gen
				//Datei holen:
				IKernelZZZ objKernel = this.getKernelObject();
				java.io.File fileCatalog = objKernel.getParameterFileByProgramAlias(sModule, sProgram, "CatalogSerieTitleFilename");
				if(fileCatalog==null){
					ReportLogZZZ.write(ReportLogZZZ.INFO, "No catalog file configured for the property 'CatalogSerieTitleFilename' in the program '" + sProgram + "', and module '" + sModule +"'");
					bReturn = false;
					break main;
				}else if(fileCatalog.exists()==false){
					ReportLogZZZ.write(ReportLogZZZ.INFO, "File not found, generating it !!! : '" + fileCatalog.getPath() + "', configured for the property 'CatalogSerieTitleFilename' in the program '" + sProgram + "', and module '" + sModule +"'");
					IKernelConfigSectionEntryZZZ objEntry =  objKernel.getParameterByProgramAlias(sModule, sProgram, "CatalogSerieTitleFilename");
					String sCatalog = objEntry.getValue();
					try {
						Stream streamTemp = new Stream(sCatalog, 1);
						streamTemp.flush();
						streamTemp.close();
					} catch (FileNotFoundException e) {
						ReportLogZZZ.write(ReportLogZZZ.ERROR, "FileNotFoundException; " + e.getMessage());
						bReturn = false;
						break main;
					} catch (IOException e) {						
						ReportLogZZZ.write(ReportLogZZZ.ERROR, "IOException: " + e.getMessage());
						bReturn = false;
						break main;
					}
					
					fileCatalog = new java.io.File(sCatalog);
					if(fileCatalog.exists()==false){
						ReportLogZZZ.write(ReportLogZZZ.ERROR, "No catalog file configured for the property 'CatalogSerieTitleFilename' in the program '" + sProgram + "', and module '" + sModule +"'");
						bReturn = false;
						break main;
					}
					
				} 
				
				//+++ Catalogdatei bearbeiten				
				//Pr�fen, ob in Datei der Eintrag vorhanden ist.		
				String[] saFlagReader = {"IsFileSorted"};
				TxtReaderZZZ objReader = new TxtReaderZZZ(fileCatalog, saFlagReader);
				long lBytePosition = objReader.readPositionLineFirst(sValue, 0);
				if(lBytePosition <= -1){
				
	//				Den Eintrag in alphabetisch sortierter Stelle einf�gen.
					String[] saFlagWriter = {"IsFileSorted","IgnoreCase"};
					TxtWriterZZZ objWriter = new TxtWriterZZZ(objReader, saFlagWriter);
					objWriter.insertLine(sValue);
					
					//2. ComboBox aktualisieren 
					//Pr�fen, ob der Eintrag in der ComboBox vorhanden ist.
					//Den Eintrag an alphabetisch sortierter Stelle einf�gen.
					KernelJComboBoxHelperZZZ objComboHelper = new KernelJComboBoxHelperZZZ(objKernel, comboSerieTitle);
					objComboHelper.insertSorted(sValue);
					
					comboSerieTitle.setSelectedItem(sValue);
				}else{
					comboSerieTitle.setSelectedItem(sValue);
				}
				
/*
			} catch (ExceptionZZZ ez) {				
				this.getLogObject().WriteLineDate(ez.getDetailAllLast());
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			}
			*/
			}//end main:
			return bReturn;
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
	
	class ActionCatalogSerieTitleRemoveVIA extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{
		//private JPanel panelParent;
				
		public ActionCatalogSerieTitleRemoveVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			boolean bReturn = true;
			main:{
			//try {
				 
				//Einige Test- /Protokollausgaben					
				//System.out.println("Anzahl der Componenten im Parent-Panel: " + panelSubSouth.getComponentCount());
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Remove 'Catalog serie title.'");
																			
				//00. Handle auf die zu aktualisierende ComboBox
				KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
				JComboBox comboSerieTitle = (JComboBox) panelParent.getComponent("comboSerieTitle");
				if(comboSerieTitle==null){
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "component with alias 'comboSerieTitle' not found.");
					bReturn = false;
					break main;
				}
				
//				0. Validieren des Feldes: Steht da was drin
				String sValue = (String) comboSerieTitle.getSelectedItem();
				if(StringZZZ.isBlank(sValue)){
					ReportLogZZZ.write(ReportLogZZZ.INFO, "No valid text-value found in component with alias 'comboSerieTitle'.");
					bReturn = false;
					break main;
				}
				
				
				//################################################################
//				Wichtige Informationen, zum Auslesen von Parametern aus der KernelConfiguration
				KernelJFrameCascadedZZZ frameParent = (KernelJFrameCascadedZZZ) this.getFrameParent();
				String sProgram = frameParent.getClass().getName(); //der Frame, in den dieses Panel eingebettet ist
				
				String sModule = KernelUIZZZ.searchModuleFirstConfiguredClassname(frameParent); 
				if(StringZZZ.isEmpty(sModule)){
					ExceptionZZZ ez = new ExceptionZZZ("No module configured for the parent frame/program: '" +  sProgram + "'", iERROR_CONFIGURATION_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//1. Daten zur TXT-Datei hinzuf�gen
				//Datei holen:
				IKernelZZZ objKernel = this.getKernelObject();
				java.io.File fileCatalog = objKernel.getParameterFileByProgramAlias(sModule, sProgram, "CatalogSerieTitleFilename");
				if(fileCatalog==null){
					ReportLogZZZ.write(ReportLogZZZ.INFO, "No catalog file configured for the property 'CatalogSerieTitleFilename' in the program '" + sProgram + "', and module '" + sModule +"'");
					bReturn = false;
					break main;
				}else if(fileCatalog.exists()==false){
					ReportLogZZZ.write(ReportLogZZZ.INFO, "File not found: '" + fileCatalog.getPath() + "', configured for the property 'CatalogSerieTitleFilename' in the program '" + sProgram + "', and module '" + sModule +"'");
					bReturn = false;
					break main;
				}
				
				//Pr�fen, ob in Datei der Eintrag vorhanden ist.
				//Den Eintrag aus der Datei entfernen.
				String[] saFlagWriter = {"IsFileSorted"};
				TxtWriterZZZ objWriter = new TxtWriterZZZ(fileCatalog, saFlagWriter);
				objWriter.removeLineFirst(sValue, 0);
				
				//2. ComboBox aktualisieren
				//Pr�fen, ob der Eintrag in der ComboBox vorhanden ist.
				//Den Eintrag aus der Liste der Items entfernen				
				comboSerieTitle.removeItem(sValue);
				
				//Auf den Leerwert springen
				comboSerieTitle.setSelectedItem(" ");
			
				
				/* Merke: Diese Fehler werden nicht "global" abgefangen, damit der Aufbau der Maske auch trotz Fehler weitergehen kann.
			} catch (ExceptionZZZ ez) {				
				this.getLogObject().WriteLineDate(ez.getDetailAllLast());
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			}
			*/
			}//end main:
			return bReturn;
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
	
	class ActionDataSendVIA extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{	
		public ActionDataSendVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		/* (non-Javadoc)
		 * @see basic.zKernelUI.component.KernelActionCascadedZZZ#actionPerformed(java.awt.event.ActionEvent)
		 * 
		 * !!! �berschreibt die Methode der Kernel-Klasse, weil hier die Exception in einem Feld des UI-Clients sichtbar gemacht werden soll
		 */
		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez){
			try{
				main:{
				if(ez==null) break main;
				
	//			+++ EditorPane f�r R�ckgabe von Meldungen					
				KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
				if(panelParent==null) break main;
				
					//1. Handle auf die Result - Textarea
				JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
				if(editorExportStatus==null){
					this.getLogObject().WriteLineDate(ReflectCodeZZZ.getMethodCurrentName() + "#Die editorPane 'editorExportStatus' ist nicht im ParentPanel vorhanden.");
					break main;
				}
	
					//2. Fehlermeldung holen und in die Result - Textarea schreiben
				String sStatus = ez.getDetailAllLast();
				editorExportStatus.setText(sStatus);
				editorExportStatus.setCaretPosition(0);
				
				}//End main
			}catch(Throwable t){
				//Falls in dieser Fehlerbehandlung selbst ein Fehler auftritt.
				this.getLogObject().WriteLineDate(ReflectCodeZZZ.getMethodCurrentName() + "#Ein Fehler ist aufgetreten: " + t.getMessage());
			}
		}
			
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			boolean bReturn = true;
			int iTry = 0; //Anzahl Versuche zur Authentifizierung
			main:{	
				try {
					//Einige Test- /Protokollausgaben
					String sStatus = "Performing action: 'Send data to server'";
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, sStatus);
					
					//+++ EditorPane f�r R�ckgabe von Meldungen					
					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					editorExportStatus.setText(sStatus);
					editorExportStatus.setCaretPosition(0);
					
					//+++ Textfield f�r die ggf. zur�ckgelieferte CarrierId, SequenceNumber
					//!!! Falls in einem vorherigen Lauf ein Fehler passiert ist, dann ist ggf. noch der vorherige Standardtext "wird gesetzt ..." darin. Dieser muss eintfernt werden, da das wieder eine Falscheingabe f�r das Servlet w�re.
					JTextField textIdCarrier = (JTextField) panelParent.getComponent("textCarrierId");
					if(textIdCarrier!=null){
						if(textIdCarrier.getText().equals("wird neu gesetzt...")){
							textIdCarrier.setText("");
						}
					}
					JTextField textSequenzeNrCarrier = (JTextField) panelParent.getComponent("textCarrierSequenze");
					if(textSequenzeNrCarrier!=null){
						if(textSequenzeNrCarrier.getText().equals("wird neu gesetzt...")){
							textSequenzeNrCarrier.setText("");
						}
					}
					
				//#############################################################	
				//TODO DIES ALLES IN EINEN WORKER-THREAD PACKEN	
				IKernelZZZ objKernel = this.getKernelObject();
				
//				Wichtige Informationen, zum Auslesen von Parametern aus der KernelConfiguration
				KernelJFrameCascadedZZZ frameParent = (KernelJFrameCascadedZZZ) this.getFrameParent();
				String sProgram = frameParent.getClass().getName(); //der Frame, in den dieses Panel eingebettet ist
				
				String sModule = KernelUIZZZ.searchModuleFirstConfiguredClassname(frameParent); 
				if(StringZZZ.isEmpty(sModule)){
					ExceptionZZZ ez = new ExceptionZZZ("No module configured for the parent frame/program: '" +  sProgram + "'", iERROR_CONFIGURATION_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					editorExportStatus.setText("Error: " + ez.getMessageLast());
					editorExportStatus.setCaretPosition(0);
					throw ez;
				}
									
//				Create als get method f�r die Anmeldung
				//+++ pr�fe auf Vorhandensein des Proxys
				//TODO						
				
				//+++ lies den Benutzernamen und das Kennwort aus der ini-Konfiguration aus.	
				IKernelConfigSectionEntryZZZ objEntry =  objKernel.getParameterByProgramAlias(sModule, "Login_Context", "Username");
				String sUsername = objEntry.getValue();
				objEntry =  objKernel.getParameterByProgramAlias(sModule, "Login_Context", "Password");
				String sPassword = objEntry.getValue();
								
				//+++ lies die URL des Servlets UND der Authentifizierung aus
				//a) Das gehört in den Export_Context
				objEntry = objKernel.getParameterByProgramAlias(sModule, "Export_Context", "URLServlet"); //"http://" + sIPExternal + "/servlet/VIADocumentCreate";
				String sUrlServlet = objEntry.getValue();
				sUrlServlet = StringZZZ.trimQuotationMarked(sUrlServlet);
				
				//b) Das gehört in den "Login_Context"					
				objEntry = objKernel.getParameterByProgramAlias(sModule, "Login_Context", "URLLogin");	//"http://" + sIPExternal + "/names.nsf?Login";
				String sUrlAuthentification = objEntry.getValue();
				sUrlAuthentification = StringZZZ.trimQuotationMarked(sUrlAuthentification);
				
				//######################################
				//	Den httpClient erstellen, erste Anfrage
				//Merke: Der Authentifizierungsstring kann so nicht verarbeitet werden: http://192.168.3.103/names.nsf?login , darum mit meiner eigenen Klasse Zerlegen.
				String sScheme = UrlLogicZZZ.getProtocol(sUrlAuthentification);
				String sHost = UrlLogicZZZ.getHost(sUrlAuthentification);
				String sPath = UrlLogicZZZ.getPath(sUrlAuthentification); 				//String stemp = "http://192.168.3.103/homepage_fgl03.nsf?Open";
				
				URIBuilder urib1 = new URIBuilder()
				.setScheme(sScheme)
		        .setHost(sHost)
		        .setPath(sPath);
				
				URI uri1 = urib1.build();
				System.out.println("URI: " + uri1.toURL().toString());
				HttpGet httpRequest1 = new HttpGet(uri1);
								
				BasicCookieStore cookieStore = new BasicCookieStore(); //Ohne Cookies kann man auch mit HttpClients.createMinimal() auskommen.
				CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCookieStore(cookieStore)
				.disableContentCompression()
				.disableAuthCaching()
				.disableAutomaticRetries()
				.build();
				
				CloseableHttpResponse response1 = null;
				StatusLine statusline1 = null;
				List<Cookie> cookies1 = null;
				HttpEntity entity1 = null;
				response1 = httpclient.execute(httpRequest1);
				statusline1 = response1.getStatusLine();					
				entity1 = response1.getEntity();
				System.out.println("Login form get: " + statusline1.toString());
				EntityUtils.consume(entity1);
				response1.close();
				
				
				//#####################################
				//Den httpClient erstellen, zum Authentifizieren

				//+++ Auch in der Eingabemaske m�sste man sich per Post authentitfizieren. Per Post die Daten f�r die Anmeldung an den Server �bertragen			
				//MERKE: Per POST ist die Anzahl der Authentifizierungsversuche wohl nicht begrenzt, was da ein Hacker wohl denkt .....

				//Merke: Das Protokoll kann nur erkannt werden, wenn es einen entprechenden StreamHandler gibt.
				//TEST URLStreamHandler objStream = UrlLogicZZZ.getURLStreamHandler("http");
								
				URIBuilder urib = new URIBuilder()
				.setScheme(sScheme)
		        .setHost(sHost)
		        .setPath(sPath);
		
				URI uri = urib.build();
				System.out.println("URI: " + uri.toURL().toString());
				
				 List <NameValuePair> nvpsClient = new ArrayList <NameValuePair>();
	             nvpsClient.add(new BasicNameValuePair("username", sUsername));
	             nvpsClient.add(new BasicNameValuePair("password", sPassword));
				
                HttpPost httpRequest = new HttpPost(uri); 
				                
                 //+++ Ausf�hrung               
				String sResponseAuthentification=null;	
				boolean bAuthentificated = false;
				CloseableHttpResponse response2 = null;
				StatusLine statusline = null;
				List<Cookie> cookies = null;
				HttpEntity entity = null;
				do{					
					iTry++;
				
					httpRequest.setEntity(new UrlEncodedFormEntity(nvpsClient));
					response2 = httpclient.execute(httpRequest);
					statusline = response2.getStatusLine();
					System.out.println("Login form get: " + statusline.toString());
					
					entity = response2.getEntity();				
					EntityUtils.consume(entity);
					
					//PROBLEM: Man darf mehrmals das Kennwort eingeben. Ergo wird auch nach einem falschen Passwort zuerst der Returncode 200 gegeben.					
					if(statusline.getStatusCode()<400){	
												
						//HEURISTISCHE L�SUNG: Bei der wirklich erfolgreichen Anmeldung wird ein Cookie mit einer SessionID gesetzt.
						cookies = cookieStore.getCookies();
		                if (cookies.isEmpty()) {
		                    System.out.println("No cookie available");
		                } else {
		                    for (int i = 0; i < cookies.size(); i++) {
		                        System.out.println("- " + cookies.get(i).toString());
		                        
		                        Cookie cookie = cookies.get(i);
								String sDomain = cookie.getDomain();
								String sCookiePath = cookie.getPath();
								String sName = cookie.getName();
								String sValue = cookie.getValue();
								int iVersion = cookie.getVersion();
								System.out.println("Cookie: " + sDomain + ", " + sCookiePath + ", " + sName + ", " + sValue + ", Version: " + iVersion);
		                    }
		                }

						if(cookies.size()>=1){
							bAuthentificated = true;							
							sStatus = "Successfully authentificated (cookie available) as '" + sUsername + "' on url '" + sUrlAuthentification + "' with status code: " +statusline.getStatusCode();
							ReportLogZZZ.write(ReportLogZZZ.INFO, sStatus);								
							editorExportStatus.setText(sStatus);
							editorExportStatus.setCaretPosition(0);
							editorExportStatus.updateUI();
							break;
						}else{							
//							Header[] header = response2.getAllHeaders();
//							for(Header headerTemp : header){
//								System.out.println("Header vom Response: " + headerTemp.getName() + " | " + headerTemp.getValue().toString());
//							}							
						}
					}else{
						bAuthentificated = false;		
						sStatus = "Error '" + statusline.getStatusCode() + "' tried to authentificate as '" + sUsername + "' on url '" + sUrlAuthentification + "'";
						ReportLogZZZ.write(ReportLogZZZ.ERROR, sStatus);
						editorExportStatus.setText(sStatus);
						editorExportStatus.setCaretPosition(0);
						editorExportStatus.updateUI();
						break main;
					}
				}while(iTry < 1);

				if (statusline.getStatusCode()==200 && bAuthentificated == false){
					bAuthentificated = true;
				}
				
				if(bAuthentificated == false){
					sStatus = "NOT SUCCESSFUL !!!  Servlet response: " + statusline.getStatusCode() + "   " + sResponseAuthentification + ". OR Unable to authentificate as '" + sUsername + "' on url '" + sUrlAuthentification + "'. Number of Tries: " + iTry;
					ReportLogZZZ.write(ReportLogZZZ.ERROR, sStatus);
					editorExportStatus.setText("Error: " + sStatus);
					editorExportStatus.setCaretPosition(0);
					editorExportStatus.updateUI();
					break main;
				}
				response2.close();
				
		
				//###################################################################
				//+++ Die im Mapperstore definierten Felder holen und per POST an das Servlet �bertragen
				//###################################################################
				
				String sSchemeServlet = UrlLogicZZZ.getProtocol(sUrlServlet);
				String sHostServlet = UrlLogicZZZ.getHost(sUrlServlet);
				String sPathServlet = UrlLogicZZZ.getPath(sUrlServlet);
				
				URIBuilder uribServlet = new URIBuilder()
				.setScheme(sSchemeServlet)
		        .setHost(sHostServlet)
		        .setPath(sPathServlet);
				
				URI uriServlet = uribServlet.build();
				System.out.println("URI: " + uriServlet.toURL().toString());
			
				CloseableHttpResponse responseServlet = null;	
                HttpPost httpServlet = new HttpPost(uriServlet);
                List <NameValuePair> nvpsServlet = new ArrayList <NameValuePair>();
                           							
				//+++ Mapper Store holen und daraus den DataStore f�r dieses Panel
				MapperStoreClientVIA mapperStore = new MapperStoreClientVIA(objKernel);
				
				//+++ Aus dem mapperStore alle Aliasse holen				
				ArrayList listaAlias = (ArrayList) mapperStore.getAliasMappedAll("ExportPanel");
				Iterator it = listaAlias.iterator();

				while(it.hasNext()){
											
					//+++ In einer Schleife alle Aliasse f�r den Mapper store verarbeiten, sprich die Werte aus den Komponenten auslesen
					String sAliasTemp = (String) it.next();
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# alias current: " + sAliasTemp);	
					
					//+++ Nun f�r den Alias der �bertragung den richtigen HTTP-PArameter holen
					String sHttpParamName = mapperStore.getParameterNameHttpByAlias("ExportPanel", sAliasTemp);
					if(StringZZZ.isEmpty(sHttpParamName)){
						ReportLogZZZ.write(ReportLogZZZ.DEBUG,  "No http parameter name mapped for the alias '" + sAliasTemp +"' and the mapperstore 'ExportPanel', skipping this parameter.");						
					}else{
						//+++ Nun den 'Klassennamen' der Komponente auslesen. Dies ist ggf. auch eine Formel
						String sClassComponent = mapperStore.getParameterFieldClassByAlias("ExportPanel", sAliasTemp);
						if(StringZZZ.isEmpty(sClassComponent)){
							ExceptionZZZ ez = new ExceptionZZZ("No componentclassname mapped for the alias '" + sAliasTemp +"' and the mapperstore 'ExportPanel'");
							editorExportStatus.setText("Error: " + ez.getMessageLast());
							editorExportStatus.setCaretPosition(0);
							
							throw ez;
						}
						String sZComponent = mapperStore.getParameterZClassByAlias("ExportPanel", sAliasTemp);
						
									
						if(sClassComponent.equalsIgnoreCase("@Z")){
							//#######################################################################
							//WERTE als Berechnung der @Z-Formula setzen. Dabei auf die Fieldmethod beziehen
							
							//1. Formel aus dem Mapping holen (diese Formel besteht aus allgemeinen Aliasnamen und hat noch keine konkrete Klassen / Methodenauspr�gung
							String sFormulaRaw = mapperStore.getParameterFieldMethodByAlias("ExportPanel", sAliasTemp); //Merke: ExportPanel ist der Alias f�r den DataStore
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# processing raw-formula: " + sFormulaRaw);
							
							//2. Formel in eine Formel umwandeln, welche die UI aliaswerte UND deren Methoden verwendet
							ExpressionTranslatorZZZ exprTranslator = new ExpressionTranslatorZZZ(objKernel, mapperStore, "ExportPanel"); //Merke: ExportPanel ist der Alias f�r den DataStore
							String sFormulaUI = exprTranslator.translate(sFormulaRaw, MapperStoreHttpZZZ.iPARAMETER_FIELDNAME);
							
							//3. Formel ausf�hren (intern wir JEXL verwendet)							
							KernelUIExpressionZZZ exprUI = new KernelUIExpressionZZZ(objKernel, (KernelJFrameCascadedZZZ) this.getFrameParent());
							String sValue = exprUI.computeString("ContentPane", sFormulaUI);   //Merke: ContentPane ist der UIAlias f�r das Panel, in dem sich die Felder befinden.
							
							//4. Wert setzen							
				             if(sValue!=null) nvpsServlet.add(new BasicNameValuePair(sHttpParamName, sValue.trim())); 
						}else if(!StringZZZ.isEmpty(sZComponent)){
							if(sZComponent.equalsIgnoreCase("@Z")){
						
//							#######################################################################
							//WERTE als Berechnung der @Z-Formula setzen. Dabei auf die ZMethod beziehen
							//JTextField textTemp = (JTextField) panelParent.getComponent("textCarrierId"); 
							//System.out.println("textCarrierId.getText()=" + textTemp.getText());
												
							
							//1. Formel aus dem Mapping holen (diese Formel besteht aus allgemeinen Aliasnamen und hat noch keine konkrete Klassen / Methodenauspr�gung
							String sFormulaRaw = mapperStore.getParameterZMethodByAlias("ExportPanel", sAliasTemp); //Merke: ExportPanel ist der Alias f�r den DataStore
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# processing raw-formula: " + sFormulaRaw);
							
							//2. Formel in eine Formel umwandeln, welche die UI aliaswerte UND deren Methoden verwendet
							ExpressionTranslatorZZZ exprTranslator = new ExpressionTranslatorZZZ(objKernel, mapperStore, "ExportPanel"); //Merke: ExportPanel ist der Alias f�r den DataStore
							String sFormulaUI = exprTranslator.translate(sFormulaRaw, MapperStoreHttpZZZ.iPARAMETER_FIELDNAME);
							
							//3. Formel ausf�hren (intern wir JEXL verwendet)							
							//FALSCH HIER WIRD DER WERT NICTH GEFUNDEN !!!!KernelUIExpressionZZZ exprUI = new KernelUIExpressionZZZ(objKernel, (KernelJFrameCascadedZZZ) this.getFrameParent());
							KernelUIExpressionZZZ exprUI = new KernelUIExpressionZZZ(objKernel, (KernelJFrameCascadedZZZ) panelParent.getFrameParent(), panelParent);
					
							//String sValue = exprUI.computeString("ContentPane", sFormulaUI);   //Merke: ContentPane ist der UIAlias f�r das Panel, in dem sich die Felder befinden.
							String sValue = exprUI.computeString(sFormulaUI);
							
							//4. Wert setzen
							if(sValue!=null) nvpsServlet.add(new BasicNameValuePair(sHttpParamName, sValue.trim())); 
							}else{
								ExceptionZZZ ez = new ExceptionZZZ("Wrong Component ZName'" + sZComponent +"'' bisher nur f�r @Z etwas entwickelt.");
								editorExportStatus.setText("Error: " + ez.getMessageLast());
								editorExportStatus.setCaretPosition(0);
								editorExportStatus.updateUI();
								
								throw ez;
							}
						}else{
								//#######################################################################
								//WERTE Ohne Berechnung setzen
								
								//+++ Nun f�r den Alias der �bertragung den entsprechenden Aliasse der Swing-Komponente holen
								String sAliasComponent = mapperStore.getParameterFieldNameByAlias("ExportPanel", sAliasTemp);
								if(StringZZZ.isEmpty(sAliasComponent)){
									ExceptionZZZ ez = new ExceptionZZZ("No componentalias mapped for the alias '" + sAliasTemp +"' and the mapperstore 'ExportPanel'");
									editorExportStatus.setText("Error: " + ez.getMessageLast());
									editorExportStatus.setCaretPosition(0);
									editorExportStatus.updateUI();
									
									throw ez;
								}
												
								//System.out.println("Alias der Komponente: " + sAliasComponent + " | Name HTTP-Parameter: " + sHttpParamName);
														
								//+++ Das Mapping per ReflectionAPI
								try{
									Class cl = Class.forName(sClassComponent);
									Object obj = cl.newInstance();
									obj = panelParent.getComponent(sAliasComponent);
									
				//					+++ Nun f�r diese Komponente die Datails aus dem DataStore-Objekt holen									
									//1. Methode holen und aufrufen
									String sMethodComponent = mapperStore.getParameterFieldMethodByAlias("ExportPanel", sAliasTemp);
									Method method = cl.getMethod(sMethodComponent, null);//Das holt scheinbar nur Werte aus der Elternklasse   cl.getDeclaredMethod(sMethodComponent, null);													
									String sValue = (String) method.invoke(obj, null);  //
									//System.out.println("Wert in der Komponente: " + sAliasComponent + " ausgelesen mit ." + sMethodComponent + " ist = " + sValue);
																
									//2. Wert setzen
									if(sValue!=null) nvpsServlet.add(new BasicNameValuePair(sHttpParamName, sValue.trim())); 
									
								}catch(ClassNotFoundException cnfe){
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "ClassNotFoundException for " + sClassComponent + ". Message reported: " + cnfe.getMessage());					
								} catch (InstantiationException ie) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "InstantiationException for " + sClassComponent + ". Message reported: " + ie.getMessage());
								} catch (IllegalAccessException iae) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "IllegalAccessException for " + sClassComponent + ". Message reported: " + iae.getMessage());
								} catch (SecurityException se) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "SecurityException for " + sClassComponent + ". Message reported: " + se.getMessage());
								} catch (NoSuchMethodException nsme) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "NoSuchMethodException for " + sClassComponent + ". Message reported: " + nsme.getMessage());
								} catch (IllegalArgumentException iarge) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "IllegalArgumentException for " + sClassComponent + ". Message reported: " + iarge.getMessage());
								} catch (InvocationTargetException ite) {					
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "InvokationTargetException for " + sClassComponent + ". Message reported: " + ite.getMessage());
								}			
						}//end if(sClassComponent.equalsIgnoreCase("@Z")){
					
					}//End StringZZZ.isEmpty(sHttpParamName)
				}//End while it.hasnext
				
				
				//+++  Im Client soll konfiguriert werden k�nnen, welcher SystemKey und welche ini-Datei auf dem Server verwendet werden soll.
				//          Ziel ist es, dem Server zu sagen "Mach mal den Debug-Modus"
				String stemp = this.getKernelObject().getSystemNumber();
				nvpsServlet.add(new BasicNameValuePair("Systemnumber", stemp));
			 
//				//+++ Per POST die Formulardaten an den Server �bertragen	
//				/*darauf verlassen, das ein Cookie vom Client-Objekt verwaltet wird
//				String sIPExternal2 = "172.16.0.102";
//				
//				HttpState state2 = client.getState();
//				Credentials credentials2 = new UsernamePasswordCredentials(sUsername, sPassword);
//				AuthScope scope2 = new AuthScope(sIPExternal2, 80);
//				state2.setCredentials(scope2, credentials2);
//				*/
//				
				//+++ Im Client soll konfiguriert werden k�nnen, was als Antwort zur�ckkommen soll, XML oder html. Das wird vom Servlet ausgewertet.
				objEntry = objKernel.getParameterByProgramAlias(sModule, "Export_Context", "ResultContentType");  //Merke: text/html ist default
				String sResultContentType = objEntry.getValue();
				if(sResultContentType.trim().toLowerCase().equals("text/xml")){
					nvpsServlet.add(new BasicNameValuePair("ResultContentType", sResultContentType.trim().toLowerCase()));				 
				}
				
				//+++ Vor dem Ausf�hren der Methode, alles leer setzen. Dadurch merkt man, das etwas passiert und was nicht richtig zur�ckgegeben wurde
				if(editorExportStatus!=null){
				editorExportStatus.setText("wird neu gesetzt ..."); 
				editorExportStatus.setCaretPosition(0);
				editorExportStatus.updateUI();
				}
				if(textIdCarrier!=null){
					if(textIdCarrier.getText().equals("")){  //Falls etwas eingegeben wurde dann nicht �berschreiben
						textIdCarrier.setText("wird neu gesetzt...");
						textIdCarrier.setCaretPosition(0);
						textIdCarrier.updateUI();
					}
				}
				if(textSequenzeNrCarrier!=null){
					if(textSequenzeNrCarrier.getText().equals("")){
						textSequenzeNrCarrier.setText("wird neu gesetzt...");
						textSequenzeNrCarrier.setCaretPosition(0);
						textSequenzeNrCarrier.updateUI();
					}
				}
							
				//+++ Die Methode ausf�hren
				httpServlet.setEntity(new UrlEncodedFormEntity(nvpsServlet));
				 responseServlet = httpclient.execute(httpServlet);
				 
				 //###########################################################
				 //### Die Antwort vom Server entgegennehmen
				if(editorExportStatus!=null){
						String sResponseRequest = responseServlet.toString();
						
						HttpEntity entityServletResponse = responseServlet.getEntity();	
						InputStream streamServletResponse = entityServletResponse.getContent();
						
						StringWriter writer = new StringWriter();
						IOUtils.copy(streamServletResponse, writer, "UTF-8");
						sResponseRequest = writer.toString();
						
						EntityUtils.consume(entityServletResponse); //sicherstellen, dass der Stream leer ist.

						this.getLogObject().WriteLineDate(sResponseRequest);
						
						//+++ Je nach angefordertem ContentType fortfahren
						if(sResultContentType.trim().toLowerCase().equals("text/xml")){
							//A) XML verarbeiten
							StringReader objReaderStringStream = new StringReader(sResponseRequest);
							SAXBuilder saxbuilder = new SAXBuilder();
							org.jdom.Document docjdom = saxbuilder.build(objReaderStringStream);
													
							//Elemente holen
							Element elemRoot = docjdom.getRootElement();
							if(elemRoot==null){
								ExceptionZZZ ez = new ExceptionZZZ("Result XML enth�lt kein Root Element.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
								throw ez;
							}
							
							
							//+++ R�ckgabe an den Client,  das zuvorher alles leergesetze wieder bef�llen. Wird dies nicht ausgeführt, dann ist ein Fehler im Client passiert oder der Wert wurde im Servlet nicht gef�llt.
							String sTagMessage= objKernel.getParameterByProgramAlias(sModule, "Export_Context", "ResultTagMessage").getValue(); 
							String sTagIdCarrier= objKernel.getParameterByProgramAlias(sModule, "Export_Context", "ResultTagIdCarrier").getValue(); 
							String sTagSequenceNrCarrier = objKernel.getParameterByProgramAlias(sModule, "Export_Context", "ResultTagSequenzeNrCarrier").getValue();
							
							List listElementMain=elemRoot.getChildren();
							for(int icount = 0; icount < listElementMain.size()-1; icount++){
								Element elem = (Element) listElementMain.get(icount);
								if(elem==null) continue;
								
								if(elem.getName().toLowerCase().equals(sTagMessage)){
//							     	-Message
									editorExportStatus.setText(elem.getTextTrim());
									editorExportStatus.setCaretPosition(0);
									editorExportStatus.updateUI();
								}else if(elem.getName().toLowerCase().equals(sTagIdCarrier)){
//							    	-CarrierID
									textIdCarrier.setText(elem.getTextTrim());
									textIdCarrier.setCaretPosition(0);
									textIdCarrier.updateUI();
								}else if(elem.getName().toLowerCase().equals(sTagSequenceNrCarrier)){
									//-CarrierSequenzeNumber
									textSequenzeNrCarrier.setText(elem.getTextTrim());
									textSequenzeNrCarrier.setCaretPosition(0);
									textSequenzeNrCarrier.updateUI();
								}
							}
							
						}else{
							//B) HTML zur�ckgeben
							//    Merke: Hier m�sste ggf. auch der HTML-String geparsed werden, um die Carrier ID zu erhalten
							editorExportStatus.setText(sResponseRequest);
							editorExportStatus.setCaretPosition(0);
							editorExportStatus.updateUI();
						}
					}
					response2.close();					
				} catch (IOException e) {			
					ExceptionZZZ ez = new ExceptionZZZ("IOException: " + e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					
					//Neu 20070613: Fehler bei der HTTPConnection nun im Frontend ausgeben
					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					editorExportStatus.setText("Error: " + ez.getDetailAllLast());
					editorExportStatus.setText("Error: "  + ez.getDetailAllLast());
					editorExportStatus.setCaretPosition(0);
					editorExportStatus.updateUI();
					
					throw ez;		
				} catch (JDOMException jdome) {
					ExceptionZZZ ez = new ExceptionZZZ("JDOMException: " + jdome.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());

					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					editorExportStatus.setText("Error: " + ez.getDetailAllLast());
					editorExportStatus.setText("Error: "  + ez.getDetailAllLast());
					editorExportStatus.setCaretPosition(0);
					editorExportStatus.updateUI();
					
					throw ez;		
				} catch (URISyntaxException e) {
					ExceptionZZZ ez = new ExceptionZZZ("URISyntaxException: " + e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					
					ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
					
					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					editorExportStatus.setText("Error: " + ez.getDetailAllLast());
					editorExportStatus.setText("Error: "  + ez.getDetailAllLast());
					editorExportStatus.setCaretPosition(0);
					editorExportStatus.updateUI();
					
					throw ez;		
				}
			}//end main:	
			this.getPanelParent().invalidate();
			this.getPanelParent().validate();
			this.getPanelParent().repaint();
			this.getPanelParent().updateUI();
			this.getPanelParent().searchPanelRoot().repaint();
			return bReturn;
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {			
		}
	}//END Class "ActionExportDataLaunchVIA	

	
	/** Speichere die eingegebenen Daten in eine lokale Datei
	 * @author lindhauer
	 *
	 */
	class ActionDataSaveVIA extends KernelActionCascadedZZZ{
		public ActionDataSaveVIA(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			boolean bReturn = true;
			//nicht notwendig  int iTry = 0; //Anzahl Versuche zur Authentifizierung
			main:{	
				try {
					//Einige Test- /Protokollausgaben
					String sStatus = "Performing action: 'Save data to file'";
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, sStatus);
					
					//+++ EditorPane f�r R�ckgabe von Meldungen					
					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					editorExportStatus.setText(sStatus);
					editorExportStatus.setCaretPosition(0);
					
					//+++ Textfield f�r die ggf. zur�ckgelieferte CarrierId, SequenceNumber
					//!!! Falls in einem vorherigen Lauf ein Fehler passiert ist, dann ist ggf. noch der vorherige Standardtext "wird gesetzt ..." darin. Dieser muss eintfernt werden, da das wieder eine Falscheingabe f�r das Servlet w�re.
					//Merke: Es kann beim Speichern in eine lokale Datei noch keine CarrierId, SequenceNumber etc. zur�ckgegeben werden. Aber es soll m�glich sein sie sofort einzugeben.
					JTextField textIdCarrier = (JTextField) panelParent.getComponent("textCarrierId");
					if(textIdCarrier!=null){
						if(textIdCarrier.getText().equals("") | textIdCarrier.getText().equals("wird neu gesetzt...")) {
							textIdCarrier.setText("wird erst beim Senden an den Server gesetzt...");
						}
					}
					JTextField textSequenzeNrCarrier = (JTextField) panelParent.getComponent("textCarrierSequenze");
					if(textSequenzeNrCarrier!=null){
						if(textSequenzeNrCarrier.getText().equals("") | textSequenzeNrCarrier.getText().equals("wird neu gesetzt...")){
							textSequenzeNrCarrier.setText("wird erst beim Senden an den Server gesetzt...");
						}
					}
					
				//#############################################################	
				//TODO DIES ALLES IN EINEN WORKER-THREAD PACKEN	
				IKernelZZZ objKernel = this.getKernelObject();
				
//				Wichtige Informationen, zum Auslesen von Parametern aus der KernelConfiguration
				KernelJFrameCascadedZZZ frameParent = (KernelJFrameCascadedZZZ) this.getFrameParent();
				String sProgram = frameParent.getClass().getName(); //der Frame, in den dieses Panel eingebettet ist
				
				String sModule = KernelUIZZZ.searchModuleFirstConfiguredClassname(frameParent); 
				if(StringZZZ.isEmpty(sModule)){
					ExceptionZZZ ez = new ExceptionZZZ("No module configured for the parent frame/program: '" +  sProgram + "'", iERROR_CONFIGURATION_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
											
				
				//+++ lies den Pfad und den Dateinamen aus der ini-Konfiguration aus.	
				String sFilePath = objKernel.getParameterByProgramAlias(sModule, "Local_Store_Context", "XMLStorePath").getValue();
				String sFileName = objKernel.getParameterByProgramAlias(sModule, "Local_Store_Context", "XMLStoreFile").getValue();
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "FilePath: " + sFilePath + " | FileName: " + sFileName);
				if(StringZZZ.isEmpty(sFileName)){
					ExceptionZZZ ez = new ExceptionZZZ("No filename configured. Modul: " + sModule + " | Program: Local_Store_Context | Parameter: XMLStoreFile.", iERROR_CONFIGURATION_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				String sFilePathTotal = sFilePath + java.io.File.separator + sFileName;
				
				
				//+++ Auf das Vorhandensein der Datei pr�fen.
				boolean bDirectoryExists = FileEasyZZZ.exists(sFilePath);
				boolean bFileExists = false;
				if(!bDirectoryExists){
					FileEasyZZZ.makeDirectory(sFilePath);
				}else{
					bFileExists = FileEasyZZZ.exists(sFilePathTotal);
				}
				java.io.File xmlFile = new java.io.File(sFilePathTotal);
				
				//+++ Je nachdem neues XML Dokument erstellen oder an bestehendes anh�ngen.
				JAZVideoInternetArchiveClientDocument docBeanXML = null;
				ArchiveEntryList al = null;
				ArchiveEntry a = null;
				Carrier c = null;
				File f = null;
				Movie m = null;
				Serie s = null;
				
				if(!bFileExists){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Datei existiert nicht: " + sFilePathTotal);
					docBeanXML = JAZVideoInternetArchiveClientDocument.Factory.newInstance();
					al = docBeanXML.addNewJAZVideoInternetArchiveClient();	
				}else{
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Datei ist vorhanden: " + sFilePathTotal);
					try{
					docBeanXML = JAZVideoInternetArchiveClientDocument.Factory.parse(xmlFile);
					}catch(XmlException e){
						ExceptionZZZ ez = new ExceptionZZZ("XmlException: " + e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						
						//Fehler im Frontend ausgeben
						panelParent= this.getPanelParent();
						editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
						editorExportStatus.setText("Error: " + ez.getDetailAllLast());
						editorExportStatus.setText("Error: "  + ez.getDetailAllLast());
						editorExportStatus.setCaretPosition(0);
						
						throw ez;		
					} catch (IOException e) {
						ExceptionZZZ ez = new ExceptionZZZ("IOException: " + e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						
						//Fehler im Frontend ausgeben
						panelParent= this.getPanelParent();
						editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
						editorExportStatus.setText("Error: " + ez.getDetailAllLast());
						editorExportStatus.setText("Error: "  + ez.getDetailAllLast());
						editorExportStatus.setCaretPosition(0);
						
						throw ez;		
					}		
					al = docBeanXML.getJAZVideoInternetArchiveClient();
					if(al==null) al = docBeanXML.addNewJAZVideoInternetArchiveClient();
				}
				docBeanXML.documentProperties().setVersion("1.0");
				docBeanXML.documentProperties().setEncoding("UTF-8");
				
//				+++ Vor dem Ausf�hren der Methode, alles auf gespeichert setzen. Dadurch merkt man, das etwas passiert und was nicht richtig zur�ckgegeben wurde
				if(editorExportStatus!=null){
				editorExportStatus.setText("wird gespeichert ..."); 
				editorExportStatus.setCaretPosition(0);
				}
				if(textIdCarrier!=null){
					if(textIdCarrier.getText().equals("wird neu gesetzt...") | textIdCarrier.getText().equals("wird erst beim Senden an den Server gesetzt...")){
						textIdCarrier.setText("");
						textIdCarrier.setCaretPosition(0);
					}
				}
				if(textSequenzeNrCarrier!=null){
					if(textSequenzeNrCarrier.getText().equals("wird neu gesetzt...")  | textSequenzeNrCarrier.getText().equals("wird erst beim Senden an den Server gesetzt...")){
						textSequenzeNrCarrier.setText("");
						textSequenzeNrCarrier.setCaretPosition(0);
					}
				}
				
				/* hier zum Testen hart verdrahtet
				//+++ XML m��iges anh�ngen: Neuer Archiv Eintrag
				ArchiveEntry a = al.addNewArchiveEntry();
				
				//TODO Goon: Hier die realen Werte aus dem GUI auslesen
				//Aktuelles Datum als Dummy Wert f�r alle Datumswerte
				GregorianCalendar objGregCal = new GregorianCalendar();
				Date objDate = objGregCal.getTime();
				Calendar cal = Calendar.getInstance();
				cal.setTime(objDate);
				
				Carrier c = a.addNewCarrier();
				c.setNumber("123");
				c.setRemark("Dat issen test");
				c.setSequence(1);
				c.setTitle("Testcarrier");
				c.setType("DVD");
				c.setCreated(cal);

				Movie m = a.addNewMovie();
				m.setTitle("Vom XML Path verweht");

				File f = a.addNewFile();
				f.setCompressionType("Div X");
				f.setDate(cal);
				f.setName("a.avi");
				f.setSize(1234556.12);
				//+++++++++++++++++++++++++++++++++++++++++++++
			*/
				
				//#### F�R DIE XML-BEANS ist nur das Ergebnis wichtig. 
				//TODO: Anhand des Alias muss festgelegt werden welche Methode verwendet wird.....
				
				//+++ Mapper Store holen und daraus den DataStore f�r dieses Panel
				MapperStoreClientVIA mapperStore = new MapperStoreClientVIA(objKernel);
				//DataStoreZZZ storePanel = mapperStore.getDataStoreExportPanel(); //TODO Dies dynamischer machen, z.B. �ber den Klassennamen
				
				
				//+++ Aus dem mapperStore alle Aliasse holen				
				ArrayList listaAlias = (ArrayList) mapperStore.getAliasMappedAll("ExportPanel");
				Iterator it = listaAlias.iterator();

				//+++ Den jeweiligen Wert berechnen
				while(it.hasNext()){
					String sValue = null;
										
					//+++ In einer Schleife alle Aliasse f�r den Mapper store verarbeiten, sprich die Werte aus den Komponenten auslesen
					String sAliasTemp = (String) it.next();
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# alias current: " + sAliasTemp);	
					

					//TODO Goon: Man muss aus dem Aliasnamen heraus das XMLBeans-Objekt holen......
					//             UND die dazugeh�rende Methode den Wert zu setzen
					//             UND schon f�r das Auslesen... die dazugeh�rende Methode.
					//MERKE: Lediglich die Berechnung des Wertes aus dem GUI - Element kann �bernommen werden. 
					//TODO: Im 1. Schritt: Die Ermittlung des Wertes kapseln, so das der Wert geholt wird.
					//TODO: Im 2. Schritt: Im ersten Entwurf die richtigen XML-Bean-Objekte lediglich in einer If-Abfrage ansteuern, die auf den Alias basiert.

					//+++ Nun den 'Klassennamen' der Komponente auslesen. Dies ist ggf. auch eine Formel
						String sClassComponent = mapperStore.getParameterFieldClassByAlias("ExportPanel", sAliasTemp);
						if(StringZZZ.isEmpty(sClassComponent)){
							ExceptionZZZ ez = new ExceptionZZZ("No componentclassname mapped for the alias '" + sAliasTemp +"' and the mapperstore 'ExportPanel'");		
							throw ez;
						}
						String sZComponent = mapperStore.getParameterZClassByAlias("ExportPanel", sAliasTemp);									
						if(sClassComponent.equalsIgnoreCase("@Z")){
							//#######################################################################
							//WERTE als Berechnung der @Z-Formula setzen. Dabei auf die Fieldmethod beziehen
							
							//1. Formel aus dem Mapping holen (diese Formel besteht aus allgemeinen Aliasnamen und hat noch keine konkrete Klassen / Methodenauspr�gung
							String sFormulaRaw = mapperStore.getParameterFieldMethodByAlias("ExportPanel", sAliasTemp); //Merke: ExportPanel ist der Alias f�r den DataStore
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# processing raw-formula: " + sFormulaRaw);
							
							//2. Formel in eine Formel umwandeln, welche die UI aliaswerte UND deren Methoden verwendet
							ExpressionTranslatorZZZ exprTranslator = new ExpressionTranslatorZZZ(objKernel, mapperStore, "ExportPanel"); //Merke: ExportPanel ist der Alias f�r den DataStore
							String sFormulaUI = exprTranslator.translate(sFormulaRaw, MapperStoreHttpZZZ.iPARAMETER_FIELDNAME);
							
							//3. Formel ausf�hren (intern wir JEXL verwendet)							
							KernelUIExpressionZZZ exprUI = new KernelUIExpressionZZZ(objKernel, (KernelJFrameCascadedZZZ) this.getFrameParent());
							sValue = exprUI.computeString("ContentPane", sFormulaUI);   //Merke: ContentPane ist der UIAlias f�r das Panel, in dem sich die Felder befinden.
							
							//!!! DAS NUN PER XML BEAN.... 4. Wert setzen
							//if(sValue!=null) methodRequest.setParameter(sHttpParamName, sValue.trim());
						}else if(!StringZZZ.isEmpty(sZComponent)){
							if(sZComponent.equalsIgnoreCase("@Z")){
						
//							#######################################################################
							//WERTE als Berechnung der @Z-Formula setzen. Dabei auf die ZMethod beziehen
							//JTextField textTemp = (JTextField) panelParent.getComponent("textCarrierId"); 
							//System.out.println("textCarrierId.getText()=" + textTemp.getText());
												
							
							//1. Formel aus dem Mapping holen (diese Formel besteht aus allgemeinen Aliasnamen und hat noch keine konkrete Klassen / Methodenauspr�gung
							String sFormulaRaw = mapperStore.getParameterZMethodByAlias("ExportPanel", sAliasTemp); //Merke: ExportPanel ist der Alias f�r den DataStore
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# processing raw-formula: " + sFormulaRaw);
							
							//2. Formel in eine Formel umwandeln, welche die UI aliaswerte UND deren Methoden verwendet
							ExpressionTranslatorZZZ exprTranslator = new ExpressionTranslatorZZZ(objKernel, mapperStore, "ExportPanel"); //Merke: ExportPanel ist der Alias f�r den DataStore
							String sFormulaUI = exprTranslator.translate(sFormulaRaw, MapperStoreHttpZZZ.iPARAMETER_FIELDNAME);
							
							//3. Formel ausf�hren (intern wir JEXL verwendet)							
							//FALSCH HIER WIRD DER WERT NCITH GEFUNDEN !!!!KernelUIExpressionZZZ exprUI = new KernelUIExpressionZZZ(objKernel, (KernelJFrameCascadedZZZ) this.getFrameParent());
							KernelUIExpressionZZZ exprUI = new KernelUIExpressionZZZ(objKernel, (KernelJFrameCascadedZZZ) panelParent.getFrameParent(), panelParent);
					
							//String sValue = exprUI.computeString("ContentPane", sFormulaUI);   //Merke: ContentPane ist der UIAlias f�r das Panel, in dem sich die Felder befinden.
							sValue = exprUI.computeString(sFormulaUI);
							
							//!!! DAS NUN PER XML BEAN .... 4. Wert setzen
							//if(sValue!=null) methodRequest.setParameter(sHttpParamName, sValue.trim());
							}else{
								ExceptionZZZ ez = new ExceptionZZZ("Wrong Component ZName'" + sZComponent +"'' bisher nur f�r @Z etwas entwickelt.");	
								throw ez;
							}
						}else{
								//#######################################################################
								//WERTE Ohne Berechnung setzen
								
								//+++ Nun f�r den Alias der �bertragung den entsprechenden Aliasse der Swing-Komponente holen
								String sAliasComponent = mapperStore.getParameterFieldNameByAlias("ExportPanel", sAliasTemp);
								if(StringZZZ.isEmpty(sAliasComponent)){
									ExceptionZZZ ez = new ExceptionZZZ("No componentalias mapped for the alias '" + sAliasTemp +"' and the mapperstore 'ExportPanel'");	
									throw ez;
								}
												
								//System.out.println("Alias der Komponente: " + sAliasComponent + " | Name HTTP-Parameter: " + sHttpParamName);
														
								//+++ Das Mapping per ReflectionAPI
								try{
									Class cl = Class.forName(sClassComponent);
									Object obj = cl.newInstance();
									obj = panelParent.getComponent(sAliasComponent);
									
				//					+++ Nun f�r diese Komponente die Datails aus dem DataStore-Objekt holen									
									//1. Methode holen und aufrufen
									String sMethodComponent = mapperStore.getParameterFieldMethodByAlias("ExportPanel", sAliasTemp);
									Method method = cl.getMethod(sMethodComponent, null);//Das holt scheinbar nur Werte aus der Elternklasse   cl.getDeclaredMethod(sMethodComponent, null);													
									sValue = (String) method.invoke(obj, null);  //
									//System.out.println("Wert in der Komponente: " + sAliasComponent + " ausgelesen mit ." + sMethodComponent + " ist = " + sValue);
																
									//!!! DAS NUN PER XML BEAN: .... 2. Wert setzen
									//methodRequest.setParameter(sHttpParamName, sValue.trim());
									
								}catch(ClassNotFoundException cnfe){
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "ClassNotFoundException for " + sClassComponent + ". Message reported: " + cnfe.getMessage());					
								} catch (InstantiationException ie) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "InstantiationException for " + sClassComponent + ". Message reported: " + ie.getMessage());
								} catch (IllegalAccessException iae) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "IllegalAccessException for " + sClassComponent + ". Message reported: " + iae.getMessage());
								} catch (SecurityException se) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "SecurityException for " + sClassComponent + ". Message reported: " + se.getMessage());
								} catch (NoSuchMethodException nsme) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "NoSuchMethodException for " + sClassComponent + ". Message reported: " + nsme.getMessage());
								} catch (IllegalArgumentException iarge) {
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "IllegalArgumentException for " + sClassComponent + ". Message reported: " + iarge.getMessage());
								} catch (InvocationTargetException ite) {					
									ReportLogZZZ.write(ReportLogZZZ.ERROR, "InvokationTargetException for " + sClassComponent + ". Message reported: " + ite.getMessage());
								}			
						}//end if(sClassComponent.equalsIgnoreCase("@Z")){
					
						
						//### Anhand des Aliasnamens das XMLBean-Objekt holen
						System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# value current: " + sValue);
						if(!StringZZZ.isEmpty(sValue)){ 
						String sDatatype = mapperStore.getParameterFieldDatatypeByAlias("ExportPanel", sAliasTemp);
						System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# datatype current: " + sDatatype);
						
						if(a==null) a=al.addNewArchiveEntry();
						if(c==null) c=a.addNewCarrier();
						if(f==null) f= a.addNewFile();
						if(m==null) m = a.addNewMovie(); 
						//Merke: Serien werden nur hinzugef�gt, wenn auch daf�r ein Wert vorhanden ist.
						if(sAliasTemp.equalsIgnoreCase("CarrierTitle")){
							c.setTitle(sValue);
						}else if(sAliasTemp.equalsIgnoreCase("CarrierType")){
							c.setType(sValue);
						}else if(sAliasTemp.equalsIgnoreCase("CarrierSequenze")){
							Integer inttemp = new Integer(sValue);
							int itemp = inttemp.intValue();
							c.setSequence(itemp);
						}else if(sAliasTemp.equalsIgnoreCase("CarrierCreated")){
							
							//++++ Datum verarbeiten
//							Aktuelles Datum als Dummy Wert f�r alle Datumswerte
							//GregorianCalendar objGregCal = new GregorianCalendar();
							//Date objDate = objGregCal.getTime();
							
							//Den Datumswert aus einem String parsen... Dies als Kernel-Klasse in einem entsprechenden Paket bereitstellen.
							//Idee: Simple DateFormat nutzen.....
							Date objDate = null;
							SimpleDateFormat objDateSimple = new SimpleDateFormat("dd.MM.yyyy");
							try {
								objDate = objDateSimple.parse(sValue);
							} catch (ParseException e) {
								ExceptionZZZ ez = new ExceptionZZZ("ParseException: " + e.getMessage() + "", iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName());
								throw ez;
							}
							
							Calendar cal = Calendar.getInstance();
							cal.setTime(objDate);
							
							c.setCreated(cal);
						}else if(sAliasTemp.equalsIgnoreCase("FileName")){
							f.setName(sValue);
						}else if(sAliasTemp.equalsIgnoreCase("FileSize")){
							Double dbltemp = new Double(sValue);
							double dtemp = dbltemp.doubleValue();
							f.setSize(dtemp);
						}else if(sAliasTemp.equalsIgnoreCase("FileDate")){
							Date objDate = null;
							SimpleDateFormat objDateSimple = new SimpleDateFormat("dd.MM.yyyy");
							try {
								objDate = objDateSimple.parse(sValue);
							} catch (ParseException e) {
								ExceptionZZZ ez = new ExceptionZZZ("ParseException: " + e.getMessage() + "", iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName());
								throw ez;
							}
							
							Calendar cal = Calendar.getInstance();
							cal.setTime(objDate);
							f.setDate(cal);
						}else if(sAliasTemp.equalsIgnoreCase("FileCompressionType")){
							f.setCompressionType(sValue);
						}else if(sAliasTemp.equalsIgnoreCase("FileRemark")){
							f.setRemark(sValue);
						}else if(sAliasTemp.equalsIgnoreCase("MovieTitle")){
							m.setTitle(sValue);
						}else if(sAliasTemp.equalsIgnoreCase("Remark")){
							m.setRemark(sValue);
						}else if(sAliasTemp.equalsIgnoreCase("SerieTitle")){
							if(!StringZZZ.isEmpty(sValue)){
								if(s==null) s= a.addNewSerie();
								s.setTitle(sValue);
							}
						}else{
							ExceptionZZZ ez = new ExceptionZZZ("Unexpected Alias: " + sAliasTemp + ". This alias is not mapped to a xml bean.", iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
						}
						}//end if !StringZZZ.isEmpty(sValue)
						
				}//End while it.hasnext
				
		
				
				
				
				//+++ Validieren
				boolean bisvalid = docBeanXML.validate();
				if(!bisvalid){
					System.out.print("Document not valid");
					
					ExceptionZZZ ez = new ExceptionZZZ("XML Document not valid: " + sFilePath + java.io.File.pathSeparator + sFileName, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}else{
					System.out.print("Document is valid");
				}
				
				try {
				   FileOutputStream fout;
					fout = new FileOutputStream(sFilePathTotal);
						docBeanXML.save(fout);
					}catch (FileNotFoundException e) {
						ExceptionZZZ ez = new ExceptionZZZ("FileNotFoundException: " + e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					} catch (IOException e) {
						ExceptionZZZ ez = new ExceptionZZZ("IoException: " + e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}

					
					
					
//	++++++				NAch dem Ausf�hren der MEthode alles wieder leer setzen
					if(editorExportStatus!=null){
						editorExportStatus.setText("erfolgreich gespeichert"); 
						editorExportStatus.setCaretPosition(0);
						}
						if(textIdCarrier!=null){
							if(textIdCarrier.getText().equals("") | textIdCarrier.getText().equals("wird neu gesetzt...") | textIdCarrier.getText().equals("wird erst beim Senden an den Server gesetzt...")){
								textIdCarrier.setText("wird erst beim Senden an den Server gesetzt...");
								textIdCarrier.setCaretPosition(0);
							}
						}
						if(textSequenzeNrCarrier!=null){
							if(textSequenzeNrCarrier.getText().equals("") | textSequenzeNrCarrier.getText().equals("wird neu gesetzt...")  | textSequenzeNrCarrier.getText().equals("wird erst beim Senden an den Server gesetzt...")){
								textSequenzeNrCarrier.setText("wird erst beim Senden an den Server gesetzt...");
								textSequenzeNrCarrier.setCaretPosition(0);
							}
						}
					
				} catch(ExceptionZZZ ez){
					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					this.getLogObject().WriteLineDate("Error: " + ez.getDetailAllLast());
					editorExportStatus.setText("Error: " + ez.getDetailAllLast());
					editorExportStatus.setCaretPosition(0);
					
					throw ez;
				}
				
				
				
				
				/*							
				} catch (HttpException e) {		
					ExceptionZZZ ez = new ExceptionZZZ("HttpException: " + e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					
					//Neu 20070613: Fehler bei der HTTPConnection nun im Frontend ausgeben
					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					this.getLogObject().WriteLineDate("Error: " + ez.getDetailAllLast());
					editorExportStatus.setText("Error: " + ez.getDetailAllLast());
					editorExportStatus.setCaretPosition(0);
					
					throw ez;
				} catch (IOException e) {			
					ExceptionZZZ ez = new ExceptionZZZ("IOException: " + e.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					
					//Neu 20070613: Fehler bei der HTTPConnection nun im Frontend ausgeben
					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					editorExportStatus.setText("Error: " + ez.getDetailAllLast());
					editorExportStatus.setText("Error: "  + ez.getDetailAllLast());
					editorExportStatus.setCaretPosition(0);
					
					throw ez;		
				} catch (JDOMException jdome) {
					ExceptionZZZ ez = new ExceptionZZZ("JDOMException: " + jdome.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());

					KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
					JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
					editorExportStatus.setText("Error: " + ez.getDetailAllLast());
					editorExportStatus.setText("Error: "  + ez.getDetailAllLast());
					editorExportStatus.setCaretPosition(0);
					
					throw ez;		
				}
				*/ 
			}//end main:		
			return bReturn;
		}

		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
			try{
				main:{
				if(ez==null) break main;
				
	//			+++ EditorPane f�r R�ckgabe von Meldungen					
				KernelJPanelCascadedZZZ panelParent= this.getPanelParent();
				if(panelParent==null) break main;
				
					//1. Handle auf die Result - Textarea
				JEditorPane editorExportStatus = (JEditorPane) panelParent.getComponent("editorExportStatus");
				if(editorExportStatus==null){
					this.getLogObject().WriteLineDate(ReflectCodeZZZ.getMethodCurrentName() + "#Die editorPane 'editorExportStatus' ist nicht im ParentPanel vorhanden.");
					break main;
				}
	
					//2. Fehlermeldung holen und in die Result - Textarea schreiben
				String sStatus = ez.getDetailAllLast();
				editorExportStatus.setText(sStatus);
				editorExportStatus.setCaretPosition(0);
				
				}//End main
			}catch(Throwable t){
				//Falls in dieser Fehlerbehandlung selbst ein Fehler auftritt.
				this.getLogObject().WriteLineDate(ReflectCodeZZZ.getMethodCurrentName() + "#Ein Fehler ist aufgetreten: " + t.getMessage());
			}
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}
		
	}
}//END class