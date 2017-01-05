package use.via.server.module.create;

import java.util.Vector;

import use.via.MapperStoreHttpZZZ;
import use.via.server.DocumentCreatorZZZ;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.VectorZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zNotes.document.DocumentZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import basic.zNotes.use.util.KernelNumberGeneratorZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class MovieVIA extends DocumentCreatorZZZ{
		
	public MovieVIA(){
		//Wird benötigt, um einfach so per ReflectionAPI ein Dokument zu erzeugen.
	}
	public MovieVIA(KernelNotesZZZ objKernelNotes){
		super(objKernelNotes);
	}
	public MovieVIA(KernelNotesZZZ objKernelNotes, MapperStoreServerVIA objStoreHTTPMapper){
		super(objKernelNotes);
		this.setMapperStore(objStoreHTTPMapper);
	}
	
	public boolean validateMapperStore() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			//try{
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ) break main;
				
				MapperStoreServerVIA objMapper = (MapperStoreServerVIA) this.getMapperStore();
				DataStoreZZZ objData = objMapper.getDataStoreMovie();	
				
				
				//################################################
				//Die Validierungen
								
				//### Der Title ist ein ganz wichtiger Parameter, der übergeben worden sein MUSS
				String sTitle = objData.getValueString("Title", 0);
				if(StringZZZ.isEmpty(sTitle)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'Title' of the movie was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
								
			/*
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectionZZZ.getMethodCurrentName());
				throw ez;
			}
			*/
			bReturn = true; // wenn bis hierhin alles erfolgreich validiert wurde, o.k.
		}
		return bReturn;
	}
	
	public Document createDocument(Document docCarrier) throws ExceptionZZZ{
		Document objReturn = null;
		main:{	
			try{
				if(docCarrier==null){
					ExceptionZZZ ez = new ExceptionZZZ("No carrierdocument provided", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Erst einmal überhaupt ein Dokument erzeugen
				objReturn = this.createDocument();
				
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Session session = objKernelNotes.getSession();
				
//				### Die CarrierID aus dem CarrierDokument auslesen
				DataStoreZZZ objDataCarrier = this.getMapperStore().getDataStoreByAlias("Carrier");			
				String sIDCarrierFieldname = objDataCarrier.getMetadata("Number", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sIDCarrierFieldname)){
					ExceptionZZZ ez = new ExceptionZZZ("Fieldname for the carrierID available", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sIDCarrier = CarrierVIA.readCarrierId(session, docCarrier, objDataCarrier);
				if(StringZZZ.isEmpty(sIDCarrier)){
					ExceptionZZZ ez = new ExceptionZZZ("No carrierID available", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
//		### CarrierID als Schlüssel des Parentdokuments. Merke 1 Movie kann sich auf n Carriern befinden
		Item item = objReturn.appendItemValue(sIDCarrierFieldname, sIDCarrier);
		item.setSummary(true);
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//End main:
		
		return objReturn;
	}
	
	public boolean updateDocument(Document doc2update, Document docCarrier) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{
				if(doc2update==null){
					ExceptionZZZ ez = new ExceptionZZZ("No document to update provided", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(docCarrier==null){
					ExceptionZZZ ez = new ExceptionZZZ("No carrier document provided", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();			
				Session session = objKernelNotes.getSession();
				
				
				//##########################################
				//### Die CarrierID wird unabhängig von den Kategoriesierungsfeldern in jedem Dokument gespeichert.
				//### Eine Datei kann auf mehreren Carrier sein. Darum wird beim Update das entsprechende Feld ggf. um den Wert erweitert.
				
				//1 Die CarrierID aus dem CarrierDokument auslesen
				DataStoreZZZ objDataCarrier = this.getMapperStore().getDataStoreByAlias("Carrier");			
				String sIdCarrierFieldname = objDataCarrier.getMetadata("Number", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sIdCarrierFieldname)){
					ExceptionZZZ ez = new ExceptionZZZ("Fieldname for the carrierID available", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sIdCarrier = CarrierVIA.readCarrierId(session, docCarrier, objDataCarrier);
				if(StringZZZ.isEmpty(sIdCarrier)){
					ExceptionZZZ ez = new ExceptionZZZ("No carrierID available", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				//2. den alten CarrierID Wert des upzudatenden Dokuments lesen
				Vector vecIdOld = doc2update.getItemValue(sIdCarrierFieldname);
				vecIdOld.add(sIdCarrier);
				Vector vecIdNew = VectorZZZ.unique(vecIdOld);  //Falls doch wieder der gleich Carrier gemeint ist.
				
				//3. Item ersetzen
				DocumentZZZ.itemInstanceAllRemove(session, doc2update, sIdCarrierFieldname);
				Item item = doc2update.replaceItemValue(sIdCarrierFieldname, vecIdNew);
				item.setSummary(true);
				
				super.updateDocument(session, doc2update);
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		return bReturn;
	}
	
	
	public String getDataStoreAliasUsed(){
		return "Movie";
	}
	public static String getFormUsed(){
		return "frmMovieVIA";
	}
	
	/** Suche, ob es den Filmtitel schon gibt. Dies Methode sucht aber auch nach der Serie.
	 * 
	 * 20090321
	 * Falls dieser Film Bestandteil einer Serie ist, muss die Serie unbedingt auch Suchkriterium sein. 
	 * Grund: Der Filmname "Teil 2" muss auch als eigenständiges Dokument existieren können und zwar für mehrere Serien.
	 * Lösung: Diese Methode die ein Objekt von SerieVIA als Parameter hat.
	 * 
	* @param objCreatorSerie
	* @return
	* 
	* lindhaueradmin; 21.03.2009 14:12:42
	 * @throws ExceptionZZZ 
	 */
	public Document searchDocumentExisting(SerieVIA objCreatorSerie) throws ExceptionZZZ{
		Document docReturn = null;
		main:{
			try{
				
				//### Den Serientitel ermitteln
				MapperStoreServerVIA objMapperSerie = (MapperStoreServerVIA) objCreatorSerie.getMapperStore();
				DataStoreZZZ objDataSerie = objMapperSerie.getDataStoreSerie();	
			
				String sTitleSerie = objDataSerie.getValueString("Title", 0);
				if(StringZZZ.isEmpty(sTitleSerie)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'title' of the serie expected for the movie was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sTitleSerieField = objDataSerie.getMetadata("Title", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sTitleSerieField)){
					ExceptionZZZ ez = new ExceptionZZZ("No fieldname for the 'title' of the serie found.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//##################################################################
				//### Angaben für den Film
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();							
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ) break main;
				
				MapperStoreServerVIA objMapper = (MapperStoreServerVIA) this.getMapperStore();
				DataStoreZZZ objData = objMapper.getDataStoreMovie();
				
				//################################################
				//Das Ermitteln der Felder/Felwerte, die als Key herangezogen werden
				String sForm = MovieVIA.getFormUsed();

				//###############################################################################
				//### Nun nach einem anderen, zusammengesetzten Schlüssel suchen
				String sTitle = objData.getValueString("Title", 0);
				if(StringZZZ.isEmpty(sTitle)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'title' of the movie was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
								
				String sTitleField = objData.getMetadata("Title", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sTitleField)){
					ExceptionZZZ ez = new ExceptionZZZ("No fieldname for the 'title' of the movie found.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sSearch = "@Lowercase(Form)=\""+ sForm.toLowerCase() + "\" & @Trim(@Lowercase(" + sTitleField + "))=\"" + sTitle.toLowerCase().toLowerCase() + "\"";
				sSearch = sSearch + " & @Trim(@Lowercase(CatRef" + sTitleSerieField + "))=\"" + sTitleSerie.toLowerCase().trim() + "\""; 
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searching for document in database '"+ db.getTitle() + "' with '" + sSearch + "'");
				
				DocumentCollection col = db.search(sSearch);
				if(col.getCount() >= 1){
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "A movie document was found");		
					docReturn = col.getFirstDocument();						
					break main;
				}else{
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "NO movie document was found");								
				}				
				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}		
		}//end main
		return docReturn;
	}
	
	public Document searchDocumentExisting() throws ExceptionZZZ {
		Document docReturn = null;
		main:{
			try{
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();							
				Database db = objKernelNotes.getDBApplicationCurrent();
				if (db ==null ) break main;
				
				MapperStoreServerVIA objMapper = (MapperStoreServerVIA) this.getMapperStore();
				DataStoreZZZ objData = objMapper.getDataStoreMovie();
				
				
				//################################################
				//Das Ermitteln der Felder/Felwerte, die als Key herangezogen werden
				String sForm = MovieVIA.getFormUsed();

				//###############################################################################
				//### Nun nach einem anderen, zusammengesetzten Schlüssel suchen
				String sTitle = objData.getValueString("Title", 0);
				if(StringZZZ.isEmpty(sTitle)){
					ExceptionZZZ ez = new ExceptionZZZ("No 'title' of the movie was passed by http-request-parameter.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
								
				//++++++++ die Suche		
				/* 20090321 Falls dieser Film Bestandteil einer Serie ist, muss die Serie unbedingt auch Suchkriterium sein. 
				 * Grund: Der Filmname "Teil 2" muss auch als eigenständiges Dokument existieren können und zwar für mehrere Serien.
				 * Lösung: Dafür gibt es eine Methode die SerieVIA als Parameter hat.
				 * 
				 * ABER: Serienfolgen müssen von der Suche explizit ausgeschlossen werden.
				 * Damit der Filmname zusätzlich auch als Film ohne Serie erstellt werden könnte.
				 */
				DataStoreZZZ objDataSerie = objMapper.getDataStoreSerie();
				String sTitleSerieField = objDataSerie.getMetadata("Title", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sTitleSerieField)){
					ExceptionZZZ ez = new ExceptionZZZ("No fieldname for the 'title' of a serie found (\"exclude serie from search case\").", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sTitleField = objData.getMetadata("Title", DataFieldZZZ.FIELDNAME);
				if(StringZZZ.isEmpty(sTitleField)){
					ExceptionZZZ ez = new ExceptionZZZ("No fieldname for the 'title' of the movie found.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sSearch = "@Lowercase(Form)=\""+ sForm.toLowerCase() + "\" & @Lowercase(" + sTitleField + ")=@Lowercase(\"" + sTitle + "\")";
				sSearch = sSearch + " & catRef" + sTitleSerieField + "=\"\"";
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Searching for document in database '"+ db.getTitle() + "' with '" + sSearch + "'");
				
				DocumentCollection col = db.search(sSearch);
				if(col.getCount() >= 1){
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "A movie document was found");		
					docReturn = col.getFirstDocument();						
					break main;
				}else{
					NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "NO movie document was found");								
				}				
			}catch(NotesException ne){
				ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}		
		}
		return docReturn;
	}
	
	/* (non-Javadoc)
	 * @see use.via.server.DocumentCreatorZZZ#getCategoryAllUsed()
	 */
	public Vector getCategoryAllUsed() {
		Vector vecReturn = new Vector();
		vecReturn.add("MovieTitle");
		return vecReturn;
	}
	
	/* (non-Javadoc)
	 * @see use.via.server.DocumentCreatorZZZ#getViewnameEmbeddedAllUsed()
	 */
	public static Vector getViewnameEmbeddedAllUsed() {
			Vector vecReturn = new Vector();
			vecReturn.add("viwFileByMovieSingleCategoryVIA");
			return vecReturn;
	}
}//END class