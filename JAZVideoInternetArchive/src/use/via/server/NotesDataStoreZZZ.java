package use.via.server;

import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zNotes.kernel.IKernelNotesZZZ;

/**TODO: Diese Klasse sollte in das Notes-Package verschoben werden, da sie allgemein die Erstellung von Notesdocumenten behandelt
 * @author 0823
 *
 */
public class NotesDataStoreZZZ extends DataStoreZZZ implements IKernelNotesZZZ{
	private KernelNotesZZZ objKernelNotes = null;
	private String sForm = null;

	public NotesDataStoreZZZ(KernelNotesZZZ objKernelNotes, String sFormName) throws ExceptionZZZ{
		super(sFormName);
		this.objKernelNotes = objKernelNotes;
		this.sForm = sFormName;
		
		//Nun ein Feld mit dem Maskennamen anlegen
		DataFieldZZZ objField = new DataFieldZZZ("Form");
		objField.Datatype=DataFieldZZZ.sSTRING;
		objField.Fieldname="Form";
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName() + "# Wert für TargetValueHandling vor dem Zuweisen: '" + objField.Targetvaluehandling + "'");
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName() + "# Weise Wert für TargetValueHandling zu: '" + DataFieldZZZ.sTARGET_VALUE_REPLACE + "'");
		objField.Targetvaluehandling=DataFieldZZZ.sTARGET_VALUE_REPLACE;
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName() + "# Wert für TargetValueHandling nach dem Zuweisen: '" + objField.Targetvaluehandling + "'");
		
		this.getKernelNotesLogObject().writeLog(ReflectCodeZZZ.getMethodCurrentName() + "# Setze DataFieldZZZ für den Alias 'Form'");
		this.setField(objField);
		this.appendValue("Form", sFormName);
	}
	
	
	public String getForm(){
		return sForm;
	}


	public KernelNotesLogZZZ getKernelNotesLogObject() throws ExceptionZZZ {
		return this.getKernelNotesObject().getKernelNotesLogObject();
	}
	public KernelNotesZZZ getKernelNotesObject() {
		return this.objKernelNotes;
	}
	public void setKernelNotesLogObject(KernelNotesLogZZZ objKernelNotesLogIn) {
		this.objKernelNotes.setKernelNotesLogObject(objKernelNotesLogIn);
	}
	public void setKernelNotesObject(KernelNotesZZZ objKernelNotes) {
		this.objKernelNotes = objKernelNotes;
	}
	
	
}
