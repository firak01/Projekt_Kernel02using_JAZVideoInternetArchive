package use.via;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import basic.zKernel.KernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;


/**Aufgabe dieser Klasse ist die Erstellung von Notesdokumenten in der "Application"-Datenbank.
 * Die notwendigen Informationen werden über das DocumentData-Objekt zur Verfügung gestellt.
 * 
 * Merke:
 * Es wird nicht nur ein Dokument hiermit erstellbar, sondern alle Dokumente, für die es auch ein entsprechendes DataStore-Objekt gibt.
 * 
 * @author lindhaueradmin
 *
 */
public abstract class MapperStoreHttpZZZ extends KernelUseObjectZZZ{
	public static final int iPARAMETER_ALIAS=0;
	public static final int iPARAMETER_NAME_HTTP=1;  //Das ist die index position in der Mehrdimensionalen Arraylist, siehe loadFieldMapAllDefault()
	public static final int iPARAMETER_FIELDNAME=2;
	
	//Hinweis: Diese DataStore-Objekte enthalten nur die Daten, die von A nach B übertragen werden. Dazu kommen noch Daten, die an alle Dokumente übertragen werden sollen und aus "Umgebungsvariablen" oder "statischen Werten" bestehen.
	protected HashMap hmDataStore = new HashMap();
	protected DataStoreZZZ objDataMeta = new DataStoreZZZ("$ALL"); //DataStore für die Dokument Metadaten, die in alle Dokumente kommen, z.B. Ersteller
																		     //Merke: Das ist kein Notes(!)DataStoreZZZ, weil es keinen Maskennamen darin gibt.
	protected HashMap hmFieldMapping = new HashMap();
	
	public MapperStoreHttpZZZ(KernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
		hmDataStore.put("$ALL", objDataMeta);		
	}
	
	/** !!! Diese MEthode führt alle zu überschreibenden MEthoden aus. Sie muss nach der erzeugung des Objekts ausgefürht werden.
	 *        Dieses Vorgehen erlaubt es den Klassen, die von dieser Klasse erben, ihre speziellen Fähigkeiten (z.B.Protokollierung mit  NotesLogZZZ) zu nutzen, die es für diese Klasse noch nicht gibt.
	* @throws ExceptionZZZ
	* 
	* lindhauer; 30.01.2008 13:13:04
	 */
	public void loadAll() throws ExceptionZZZ{

		
		//Das Mapping AliasName --> HTTP_Parameter, bzw. --> NotesFeldname oder SwingKomponentenAlias festlegen
		this.setFieldMapAllDefault(loadFieldMapAll());
				
		//Die DataStore Objekte festlegen
		this.loadDataStoreAll();
		
		//Die DataFieldStruktur für alle DataStore Objekte laden
		this.loadFieldDataStoreFieldStructureAll();
	}
	
	
	//###Acessor - Methoden
	public HashMap getFieldMapAll() throws ExceptionZZZ{
		if(this.hmFieldMapping.isEmpty()){
			ExceptionZZZ ez = new ExceptionZZZ("FieldMapping is empty", iERROR_PROPERTY_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		return this.hmFieldMapping;
	}
	public void setFieldMapAllDefault(HashMap hmFieldMapping){
		this.hmFieldMapping = hmFieldMapping;
	}
	
	
	/** Gibt alle Aliasnamen für das HTTP-Mapping zurück, bezogen auf einen bestimmten Aliasnamen eines DataStores.
	 *  Merke: Das Mapping wird in .loadFieldMapAll() durchgeführt.
	* @param sAliasDataStore
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 28.02.2007 10:52:55
	 */
	public ArrayList getAliasMappedAll(String sAliasDataStore) throws ExceptionZZZ{
		ArrayList listaReturn = null;
			//TODO; Falls sAliasDataStore leer ist, sollen alle Werte zurückgegeben werden.
			HashMap hmAll = this.getFieldMapAll();
			ArrayList[] listaaForDataStoreAlias = (ArrayList[]) hmAll.get(sAliasDataStore);
			
			if(listaaForDataStoreAlias != null){
				listaReturn = listaaForDataStoreAlias[MapperStoreHttpZZZ.iPARAMETER_ALIAS];
			}
		return listaReturn;
	}
	
	
	/** Der Data-Store, der in jedem Dokument enthalten ist
	 *   z.B. das Feld "Ersteller"
	* @return DataStoreZZZ, der mit dem Alias $ALL gespeichert wird;
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 27.11.2006 08:26:16
	 */
	public DataStoreZZZ getDataStore() throws ExceptionZZZ{
		return this.getDataStoreByAlias("$ALL");
	}
	public void setDataStore(DataStoreZZZ objData) throws ExceptionZZZ{
		this.setDataStoreByAlias("$ALL", objData);
	}
	public DataStoreZZZ getDataStoreByAlias(String sAlias) throws ExceptionZZZ{
		DataStoreZZZ objReturn = null;
		main:{
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}			
			if(! this.hmDataStore.containsKey(sAlias)) break main;
			
			objReturn = (DataStoreZZZ) this.hmDataStore.get(sAlias);			
		}//End main
		return objReturn;
	}
	public void setDataStoreByAlias(String sAlias, DataStoreZZZ objStore) throws ExceptionZZZ{
		main:{
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}			
			if(objStore == null){
				if(!this.hmDataStore.containsKey(sAlias)) break main;
				this.hmDataStore.remove(sAlias);
			}else{			
				this.hmDataStore.put(sAlias, objStore);
			}
		}//END main
	}
	
	
	
	public static HashMap loadHTTPRequestValue(HttpServletRequest req){
		HashMap objReturn = new HashMap();
			Enumeration enumeration = req.getParameterNames();
			String sParam=null; String sValue = null;
			while(enumeration.hasMoreElements()){
				sParam = (String) enumeration.nextElement();
				if(! StringZZZ.isEmpty(sParam)){
					sValue = req.getParameter(sParam); //TODO: Hier die Verarbeitung von mehrerern gleichnamigen Parametern mit .getParameters(...) einfügen.
					                                                   //           Dazu muss aber auch die Speicherung gelöst werden. Vielleicht JSON als Lösung wie in der Hashmap noch weitere Mehrfachwerte gespeichert werden sollen...
					                                                   //           Hier eine weitere HashMap für Mehrfachwerte einzuführen, wird zu extrem komplexen Datenstrukturen führen, FGL 20070609
					objReturn.put(sParam, sValue);
				}
			}
		return objReturn;
	}
	
	
	
	
	public String getParameterNameByAlias(String sStoreNameIn, String sAlias, int iParameterType) throws ExceptionZZZ{
		String sReturn = null;
			if(iParameterType != iPARAMETER_NAME_HTTP && iParameterType != iPARAMETER_FIELDNAME){
				ExceptionZZZ ez = new ExceptionZZZ("This Parameter Type does not exist: " + iParameterType, iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
			}
			
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			String sStoreName;
			if(StringZZZ.isEmpty(sStoreNameIn)){
				sStoreName = "$All";
			}else{
				sStoreName = sStoreNameIn;
			}
			
			HashMap hmFieldMapping = this.getFieldMapAll();
			if(hmFieldMapping.containsKey(sStoreName)==false){
				ExceptionZZZ ez = new ExceptionZZZ("Store Name '" + sStoreName + "' not in field mapping.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			ArrayList[] listaForAlias = (ArrayList[])hmFieldMapping.get(sStoreName);
			
			//Nun hat man eine "MEHRDIMENSIONALE" ArrayList, in der ggf. der sAlias vorhanden ist.
			Iterator it = listaForAlias[iPARAMETER_ALIAS].iterator();
			int ipos = -1;
			while(it.hasNext()){
				String sAliasTemp = (String) it.next();
				ipos++;
				if(sAliasTemp.equals(sAlias)){
					sReturn = (String) listaForAlias[iParameterType].get(ipos);
					break;  //while Schleife verlassen
				}
				
			}		
		return sReturn;	
	}
	
	
	public String getParameterNameHttpByAlias(String sStoreNameIn, String sAlias) throws ExceptionZZZ{
			return this.getParameterNameByAlias(sStoreNameIn, sAlias, iPARAMETER_NAME_HTTP);
	}
	public String getParameterNameFieldByAlias(String sStoreNameIn, String sAlias) throws ExceptionZZZ{
		return this.getParameterNameByAlias(sStoreNameIn, sAlias, iPARAMETER_FIELDNAME);
	}
	
	public String getParameterFieldByAlias(String sStoreNameIn, String sAlias, String sMetadataType) throws ExceptionZZZ{
		String sReturn = null;
			String sStoreName;
			if(StringZZZ.isEmpty(sStoreNameIn)){
				sStoreName = "$All";
			}else{
				sStoreName = sStoreNameIn;
			}
	
	//		Hier muss das entsprechende DataStoreObjekt erst aus dem Mapper geholt werden
			DataStoreZZZ objStore = this.getDataStoreByAlias(sStoreName);
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("No datastore-object found with the storename:'"+sStoreName+"'");
				throw ez;
			}
			sReturn = this.getParameterFieldByAlias(objStore, sAlias, sMetadataType);
		return sReturn;		
	}
	
	public String getParameterFieldByAlias(DataStoreZZZ objStore, String sAlias, String sMetadataType) throws ExceptionZZZ{
		String sReturn = null;
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("Datastore-object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(StringZZZ.isEmpty(sAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				 
			} 
			if(DataFieldZZZ.isValidMetadataType(sMetadataType)==false){
				ExceptionZZZ ez = new ExceptionZZZ("This metadatatype is not valid in '" + DataFieldZZZ.class.getName() + "': " + sMetadataType);
				throw ez;
			}
		
			//!!! Der Alias ist nun der Alias für den HTTP-Parameter, bzw. die SwingKomponente (als Quelle) oder das Notesdokument (als Ziel)
			//Daher erst den "KomponentenAlias" ermitteln
			String sStoreName = objStore.getStoreAlias();
			String sAliasComponent = this.getParameterNameByAlias(sStoreName, sAlias, iPARAMETER_FIELDNAME);
			if(StringZZZ.isEmpty(sAliasComponent)){
				ExceptionZZZ ez = new ExceptionZZZ("Threre is no component configured with the StoreName'" + sStoreName + "' and the alias '" + sAlias + "'");
				throw ez;
			}
		
			//Nun den Klassennamen als Metadata auslesen
			sReturn = objStore.getMetadata(sAliasComponent, sMetadataType);
		return sReturn;		
	}
	
	
	public String getParameterFieldClassByAlias(String sStoreNameIn, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(sStoreNameIn, sAlias, DataFieldZZZ.FIELDCLASS);
	}
	public String getParameterFieldClassByAlias(DataStoreZZZ objStore, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(objStore, sAlias, DataFieldZZZ.FIELDCLASS);
	}
	
	public String getParameterFieldDatatypeByAlias(String sStoreNameIn, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(sStoreNameIn, sAlias, DataFieldZZZ.DATATYPE);
	}
	
	public String getParameterFieldMethodByAlias(String sStoreNameIn, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(sStoreNameIn, sAlias, DataFieldZZZ.FIELDMETHOD);
	}
	public String getParameterFieldMethodByAlias(DataStoreZZZ objStore, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(objStore, sAlias, DataFieldZZZ.FIELDMETHOD);
	}
	
	public String getParameterFieldNameByAlias(String sStoreNameIn, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(sStoreNameIn, sAlias, DataFieldZZZ.FIELDNAME);
	}
	public String getParameterFieldNameByAlias(DataStoreZZZ objStore, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(objStore, sAlias, DataFieldZZZ.FIELDNAME);
	}
	
	public String getParameterZClassByAlias(DataStoreZZZ objStore, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(objStore, sAlias, DataFieldZZZ.ZCLASS);
	}
	public String getParameterZClassByAlias(String sStoreNameIn, String sAlias) throws ExceptionZZZ{
		String sReturn = null;
			String sStoreName;
			if(StringZZZ.isEmpty(sStoreNameIn)){
				sStoreName = "$All";
			}else{
				sStoreName = sStoreNameIn;
			}
	
	//		Hier muss das entsprechende DataStoreObjekt erst aus dem Mapper geholt werden
			DataStoreZZZ objStore = this.getDataStoreByAlias(sStoreName);
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("No datastore-object found with the storename:'"+sStoreName+"'");
				throw ez;
			}
			sReturn = this.getParameterZClassByAlias(objStore, sAlias);
		return sReturn;		
	} 
	public String getParameterZMethodByAlias(DataStoreZZZ objStore, String sAlias) throws ExceptionZZZ{
		return this.getParameterFieldByAlias(objStore, sAlias, DataFieldZZZ.ZMETHOD);
	}
	public String getParameterZMethodByAlias(String sStoreNameIn, String sAlias) throws ExceptionZZZ{
		String sReturn = null;
			String sStoreName;
			if(StringZZZ.isEmpty(sStoreNameIn)){
				sStoreName = "$All";
			}else{
				sStoreName = sStoreNameIn;
			}
	
	//		Hier muss das entsprechende DataStoreObjekt erst aus dem Mapper geholt werden
			DataStoreZZZ objStore = this.getDataStoreByAlias(sStoreName);
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("No datastore-object found with the storename:'"+sStoreName+"'");
				throw ez;
			}
			sReturn = this.getParameterZMethodByAlias(objStore, sAlias);
		return sReturn;		
	}
	
	public boolean isAliasMappedAvailable(String sStoreNameIn, String sAliasSearchedFor) throws ExceptionZZZ{
		boolean bReturn = false;
			if(StringZZZ.isEmpty(sAliasSearchedFor)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				 
			} 
			
			String sStoreName;
			if(StringZZZ.isEmpty(sStoreNameIn)){
				sStoreName = "$All";
			}else{
				sStoreName = sStoreNameIn;
			}
						
			HashMap hmFieldMapping = this.getFieldMapAll();
			if(hmFieldMapping.containsKey(sStoreName)==false){
				ExceptionZZZ ez = new ExceptionZZZ("Store Name '" + sStoreName + "' not in field mapping.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			ArrayList[] listaForAlias = (ArrayList[])hmFieldMapping.get(sStoreName);
			
			//Nun hat man eine "MEHRDIMENSIONALE" ArrayList, in der ggf. der sAlias vorhanden ist.
			Iterator it = listaForAlias[iPARAMETER_ALIAS].iterator();
			int ipos = -1;
			while(it.hasNext()){
				String sAliasTemp = (String) it.next();
				ipos++;
				if(sAliasTemp.equals(sAliasSearchedFor)){
					bReturn = true;
					break;  //while Schleife verlassen
				}
			}//
		return bReturn;
	}
	
	
	public boolean isAliasFieldStructureAvailable(String sStoreNameIn, String sAliasSearchedFor) throws ExceptionZZZ{
		boolean bReturn = false;
			if(StringZZZ.isEmpty(sAliasSearchedFor)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				 
			} 
			
			String sStoreName;
			if(StringZZZ.isEmpty(sStoreNameIn)){
				sStoreName = "$All";
			}else{
				sStoreName = sStoreNameIn;
			}
			
			DataStoreZZZ objStore = this.getDataStoreByAlias(sStoreName);
			if (objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("No datastore-object availabel for the alias: '" + sStoreNameIn + "'");
				throw ez;
			}
			
			//Das ist aber nicht der Aliasname, der in der Field-Struktur gespeichert ist, sondern ein übergeordneter
			String sFieldStructureAlias = this.getParameterNameFieldByAlias(sStoreName, sAliasSearchedFor);
			bReturn = objStore.isFieldAliasMapped(sFieldStructureAlias);
			
			/*
			HashMap hmFieldStructureMetadata = objStore.get//this.getFieldMapAll();
			if(hmFieldMapping.containsKey(sStoreName)==false){
				ExceptionZZZ ez = new ExceptionZZZ("Store Name '" + sStoreName + "' not in field mapping.", iERROR_PARAMETER_VALUE, this, ReflectionZZZ.getMethodCurrentName());
				throw ez;
			}
			ArrayList[] listaForAlias = (ArrayList[])hmFieldMapping.get(sStoreName);
			
			//Nun hat man eine "MEHRDIMENSIONALE" ArrayList, in der ggf. der sAlias vorhanden ist.
			Iterator it = listaForAlias[iPARAMETER_ALIAS].iterator();
			int ipos = -1;
			while(it.hasNext()){
				String sAliasTemp = (String) it.next();
				ipos++;
				if(sAliasTemp.equals(sAliasSearchedFor)){
					bReturn = true;
					break;  //while Schleife verlassen
				}
			}*/
		return bReturn;
	} 
	
	
	public abstract void loadDataStoreAll() throws ExceptionZZZ;
	public abstract void loadFieldDataStoreFieldStructureAll() throws ExceptionZZZ;
	public abstract HashMap loadFieldMapAll() throws ExceptionZZZ;
		
	/** TODO What the method does.
	* @param hmMapping              , Mapping der HttpParameternamen auf die Feldnamen
	* @param hmParamValue          , Mapping der HttpParameternamen auf die Werte die in den HttpParametern enthalten sind 
	* @return
	* @throws ExceptionZZZ
	* 
	* lindhaueradmin; 22.02.2007 11:29:21
	 */
	public boolean storeHttpParam(HashMap hmMapping, HashMap hmParamValue) throws ExceptionZZZ{
			boolean bReturn = false;
				if(hmMapping==null){
					ExceptionZZZ ez = new ExceptionZZZ("Mapping Hashmap", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(hmMapping.isEmpty()){
					ExceptionZZZ ez = new ExceptionZZZ("Mapping Hashmap is empty", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(hmParamValue==null){
					ExceptionZZZ ez = new ExceptionZZZ("Value Hashmap", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(hmParamValue.isEmpty()){
					ExceptionZZZ ez = new ExceptionZZZ("Value Hashmap is empty", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//############################################				
				Set objSetKey = hmMapping.keySet();
				Iterator objItKey = objSetKey.iterator();
		
				String sStoreAlias = null; String sParam = null; String sColumnAlias = null; String sValue = null;	 										
				int icountline = -1;
				while(objItKey.hasNext()){	
					icountline = -1;
					sStoreAlias = (String)objItKey.next();
				
					
					//Nun die Werte holen
					ArrayList[] alsaTable = (ArrayList[]) hmMapping.get(sStoreAlias);
					ArrayList alsColumnAlias = alsaTable[iPARAMETER_ALIAS];
					ArrayList alsColumnHTTP = alsaTable[iPARAMETER_NAME_HTTP];

					Iterator objItAlias = alsColumnAlias.iterator();
					while(objItAlias.hasNext()){
						icountline++;
						sColumnAlias =  (String) objItAlias.next();  //Name des HTTP Parameters
						sParam = (String) alsColumnHTTP.get(icountline);
						if(!StringZZZ.isEmpty(sColumnAlias)){													
							//weil beim Testen kein HTTPRequest-Objekt (Auch kein Mock-Objekt) zur Verfügung steht, wird hier der Umweg über einen weitere HashMap gemacht. Die steht dem Test nämlich zur Verfügung: sValue = req.getParameter(sParam);   //Wert des HTTP Parameters, kann nur String sein, wird aber durch den objStore in den konfigurierten Datentyp umgewandelt.
							sValue = (String) hmParamValue.get(sParam);
							if(sValue!=null){
								NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Parameter '" + sParam + "', trying to set: '" + sValue + "'");
															
								//!!! Wert in den passenden Datastore und dem Aliasnamen setzen (Dabei wird der Datentyp ggf. angepasst) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
								this.setValue(sStoreAlias, sColumnAlias, sValue);
								NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Parameter '" + sParam + "', successfully set: '" + sValue + "'");		
							}
						}//END if ! isEmpty(sParam)
					}
					bReturn = true;
				}							
			return bReturn;
		}
	
	public boolean storeHttpParam(HttpServletRequest req)throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(req==null){
				ExceptionZZZ ez = new ExceptionZZZ("HttpServletRequest", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//Damit das zuweisen der HTTP-Parameter zu den DataStore-Objkten testbar wird, die HTTP-Parameter-Namen und die Werte in eine HashMap packen.
			HashMap objHmHttp = MapperStoreHttpZZZ.loadHTTPRequestValue(req);
			if(objHmHttp.isEmpty()) break main; 
			
			//Die Hashmap, in der die HTTP-Parameter auf die Felder gemappt werden
			HashMap objHmMapping = this.getFieldMapAll();//objMapper.loadFieldMapAllDefault();
			if(objHmMapping.isEmpty()) break main; 
			
			bReturn = this.storeHttpParam(objHmMapping, objHmHttp); 					
		}
		return bReturn;
	}
		
	public void setValue(String sStoreAlias, String sFieldAlias, String sValue) throws ExceptionZZZ{
			if(StringZZZ.isEmpty(sStoreAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Store Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			DataStoreZZZ objStore = this.getDataStoreByAlias(sStoreAlias);
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("A store with the alias '" + sStoreAlias + "' is not available in this DocumentCreator-Class", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			objStore.appendValue(sFieldAlias, sValue);
	}
	
	public void setValue(String sStoreAlias, String sFieldAlias, int iValue) throws ExceptionZZZ{
			if(StringZZZ.isEmpty(sStoreAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Store Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			DataStoreZZZ objStore = this.getDataStoreByAlias(sStoreAlias);
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("A store with the alias '" + sStoreAlias + "' is not available in this DocumentCreator-Class", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			objStore.appendValue(sFieldAlias, iValue);
	}
	
	public void setValue(String sStoreAlias, String sFieldAlias, long lValue) throws ExceptionZZZ{
			if(StringZZZ.isEmpty(sStoreAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Store Alias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			DataStoreZZZ objStore = this.getDataStoreByAlias(sStoreAlias);
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("A store with the alias '" + sStoreAlias + "' is not available in this DocumentCreator-Class", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			objStore.appendValue(sFieldAlias, lValue);
	}
	
	//### für $ALL
	public void setValue(String sFieldAlias, String sValue) throws ExceptionZZZ{
			DataStoreZZZ objStore = this.getDataStoreByAlias("$ALL");
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("A store with the alias $ALL is not available in this DocumentCreator-Class", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			objStore.appendValue(sFieldAlias, sValue);
	}
	
	public void setValue(String sFieldAlias, int iValue) throws ExceptionZZZ{
			DataStoreZZZ objStore = this.getDataStoreByAlias("$ALL");
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("A store with the alias $ALL is not available in this DocumentCreator-Class", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			objStore.appendValue(sFieldAlias, iValue);
	}
	
	public void setValue(String sFieldAlias, long lValue) throws ExceptionZZZ{
			DataStoreZZZ objStore = this.getDataStoreByAlias("$ALL");
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("A store with the alias $ALL is not available in this DocumentCreator-Class", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			objStore.appendValue(sFieldAlias, lValue);
	}
	
	public void setValue(String sFieldAlias, Object objValue) throws ExceptionZZZ{
			DataStoreZZZ objStore = this.getDataStoreByAlias("$ALL");
			if(objStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("A store with the alias $ALL is not available in this DocumentCreator-Class", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			objStore.appendValue(sFieldAlias, objValue);
	}
	
}//End class
