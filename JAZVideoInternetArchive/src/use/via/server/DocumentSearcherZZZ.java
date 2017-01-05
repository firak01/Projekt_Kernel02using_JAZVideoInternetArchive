package use.via.server;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import custom.zNotes.kernel.KernelNotesZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.kernel.KernelNotesUseObjectZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;

public class DocumentSearcherZZZ extends KernelNotesUseObjectZZZ implements ICategoryConstantZZZ{
	private Database dbSearch = null;
	
	public DocumentSearcherZZZ(){
		//Wird benötigt, um einfach so per ReflectionAPI mal ein Objekt zu erzeugen.
	}
	public DocumentSearcherZZZ(KernelNotesZZZ objKernelNotes){
		super(objKernelNotes);
	}
	public DocumentSearcherZZZ(KernelNotesZZZ objKernelNotes, Database db2beSearchedIn){
		super(objKernelNotes);
		this.setDatabase(db2beSearchedIn);
	}
	
	/** Führt eine suche in der ansicht durch, in der alle Werte stehen, die für eine Kategorisierung verwendet werden
	 *   viwLookupCatValVIA mit der Formel für die erste Spalte: objAliasVIA  + "#" + @GetField(objCatValVIA)
	 *   Merke: In objCatValVIA stehen alle Feldnamen drin, die von einem Dokument für die Kategorisierung verwendet werden.
	 *   
	* @param sValue, der gesuchte Wert
	* @param sAlias,  der gesuchte Documenttype
	* @return
	* 
	* lindhauer; 07.05.2008 11:55:38
	 * @throws ExceptionZZZ 
	 */
	public DocumentCollection searchCategorySource(String sValue, String sAlias) throws ExceptionZZZ{
		DocumentCollection colReturn=null;
		main:{
			try{
				if(StringZZZ.isEmpty(sValue)){
					ExceptionZZZ ez = new ExceptionZZZ("No value provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(StringZZZ.isEmpty(sAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("No alias provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				//################################################
				Database db = this.getDatabase();
				if(db==null){
					ExceptionZZZ ez = new ExceptionZZZ("Database not provided", iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();				
				View viw = db.getView(ICategoryConstantZZZ.sVIEW_LOOKUP_CATEGORY_VALUE + objKernelNotes.getApplicationKeyCurrent()) ;
				if(viw==null){
					ExceptionZZZ ez = new ExceptionZZZ("View not found '" + ICategoryConstantZZZ.sVIEW_LOOKUP_CATEGORY_VALUE + objKernelNotes.getApplicationKeyCurrent()+ "'" , iERROR_ZFRAME_DESIGN, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sSearch =StringZZZ.capitalize(sAlias) + "#" + sValue;
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searchstring: '" + sSearch +"', using view: " + viw.getName() + ", using db: " + db.getFilePath(), true);
				colReturn = viw.getAllDocumentsByKey(sSearch, true);
				
			}catch(NotesException ne){
				NotesReportLogZZZ.write(NotesReportLogZZZ.ERROR, "NotesException: '" + ne.text +"'", true);
				ExceptionZZZ ez = new ExceptionZZZ("NotesException: " + ne.text, iERROR_RUNTIME, DocumentCategorizerZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}	
		}//end main
		return colReturn;
	}
	
	
	
	
	//#### GETTER / SETTER
	public void setDatabase(Database db2beSearchedIn){
		this.dbSearch = db2beSearchedIn;
	}
	public Database getDatabase(){
		return this.dbSearch;
	}
}
