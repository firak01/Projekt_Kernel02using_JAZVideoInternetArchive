package use.via.client.module.export;

import java.io.File;
import java.util.Vector;

import basic.zKernel.KernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJComboBoxListening4ComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

public class JComboBoxListening_SerieTitleVIA  extends KernelJComboBoxListening4ComponentSelectionResetZZZ   implements IListenerFileSelectedVIA{
	public static String sTEXT_ERROR = " ";
	public JComboBoxListening_SerieTitleVIA(KernelZZZ objKernel, Object objItemInitial) {
		super(objKernel, objItemInitial);		
	}

	public void doResetCustom(EventComponentSelectionResetZZZ eventSelectionResetNew) {
		this.setSelectedItem(" ");
	}

	public void fileChanged(EventListFileSelectedVIA eventFileSelected) {
		String sReturnUI=" ";
		
		main:{
			try{
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Serien ComboBox receives Event 'fileChanged(...)");
				File file = eventFileSelected.getFile();
				if(file==null) break main;
								
//				Der Default-Titel ist der Dateiname OHNE Endung			
				//Er soll aber noch hinsichtlich der Unterstriche verarbeitet werden.
				File fileCatalogSerie = this.getKernelObject().getParameterFileByProgramAlias("use.via.client.FrmMainVIA","Export_Context", "CatalogSerieTitleFilename");	
				Vector vecTemp = CommonUtilVIA.computeMovieDetailByFile(file, fileCatalogSerie);
				sReturnUI=(String)vecTemp.get(0);  //Dieser Serieneintrag sollte in der Datei und damit in der Combo Box vorhanden sein
				if(StringZZZ.isEmpty(sReturnUI)) sReturnUI = " ";

			}catch(ExceptionZZZ ez){
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());				
				sReturnUI = JComboBoxListening_SerieTitleVIA.sTEXT_ERROR;
			}				
		}//End main:
		this.setSelectedItem(sReturnUI);
	}
}
