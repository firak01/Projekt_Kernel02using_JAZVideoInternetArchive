package use.via;

import java.util.ArrayList;

import basic.zKernel.IKernelZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.data.DataFieldZZZ;
import basic.zBasic.util.data.DataStoreZZZ;
import basic.zBasic.util.datatype.string.StringArrayZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelUseObjectZZZ;

/**Diese Klasse soll den hinterlegten Ausdruck, der auf allgemeine Aliasnamen beruht, in einen Ausdruck ab�ndern, der z.B. auf den UI-Aliasnamen beruht oder auf den HTTP-Parameter-Namen.
 * 
 * Merke: Entwickelt wird dies erst f�r die Umwandlung in UI-Aliasnamen.
 * @author lindhaueradmin
 *
 */
public class ExpressionTranslatorZZZ extends KernelUseObjectZZZ{
	MapperStoreHttpZZZ mapperStore = null;
	String sDataStoreAlias = null;
	
	DataStoreZZZ dataStore = null;
	
	public ExpressionTranslatorZZZ(IKernelZZZ objKernel, MapperStoreHttpZZZ mapperStore, String sDataStoreAlias) throws ExceptionZZZ{
		super(objKernel);
		if(mapperStore==null){
			ExceptionZZZ ez = new ExceptionZZZ("MapperStore-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		if(StringZZZ.isEmpty(sDataStoreAlias)){
			ExceptionZZZ ez = new ExceptionZZZ("DataStoreAlias", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		this.mapperStore = mapperStore;
		this.sDataStoreAlias = sDataStoreAlias;
	}
	

	public ExpressionTranslatorZZZ(IKernelZZZ objKernel, MapperStoreHttpZZZ mapperStore, DataStoreZZZ dataStore) throws ExceptionZZZ{
		super(objKernel);
		if(dataStore==null){
			ExceptionZZZ ez = new ExceptionZZZ("DataStore-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}	
		if(mapperStore==null){
			ExceptionZZZ ez = new ExceptionZZZ("MapperStore-Object", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		this.dataStore = dataStore;	
		this.mapperStore = mapperStore;
	}
	
	public String translate(String sExpressionAlias, int iTypeTarget) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sExpressionAlias)) break main;
			
			//1. den Ausdruck in alle vorhandenen Teilstrings zerlegen. Merke: Einen StringTokenizer darf man nicht verwenden, weil dieser die Zeichne nur einzel n betrahtet.
			//TODO: Das Array aller zur Verf�gung stehenden Delimiter per static - Methode holen.
			String[] saDelimiter = {"<?/>", "<+/>", "<:/>"};
			String[] saToken = StringZZZ.explode(sExpressionAlias, saDelimiter);
			
			//2. ein Array aller Delimiter aufbauen (und zwar in der Reihenfolge des Auftretens)
			ArrayList listaTokenDelim = StringZZZ.findSorted(sExpressionAlias, saDelimiter);
			
			//3. Die werte f�r den Alias ersetzen durch eine Kombination aus dem Alias des iTypeTarget und der dazugeh�renden Methode
			DataStoreZZZ dataStore = this.getDataStoreUsed();
			if(dataStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("No datastore-object found.", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sDataStoreAlias = dataStore.getStoreAlias();
			if(StringZZZ.isEmpty(sDataStoreAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("No dataStoreAlias name found in datastore-object.", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			MapperStoreHttpZZZ mapperStore = this.getMapperStore();
			if(mapperStore==null){
				ExceptionZZZ ez = new ExceptionZZZ("No mapperstore-object found.", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String[] saTokenAlias = new String[saToken.length];
			for(int icount = 0; icount <= saTokenAlias.length - 1; icount++){
				String sAliasTemp = saToken[icount];
				if(!(sAliasTemp.endsWith("'") || sAliasTemp.startsWith("'"))){
				//TODO: Den Ausdruck "ExportPanel" konfigurierbar machen
					//Merke: das w�rde auch folgender Ausdruck tun. Man m��te nur jedes Mal das DataStore-Objekt holen
					//String sClassComponent = mapperStore.getParameterFieldClassByAlias("ExportPanel", sAliasTemp);
					//String sClassMethod = mapperStore.getParameterFieldMethodByAlias("ExportPanel", sAliasTemp);
					
					//Zuerst den UI-Alias herausfinden. Er kann mit dem allgemeinen Alias �bereinstimmen, muss es aber nicht.
					String sAliasComponent = mapperStore.getParameterNameByAlias(sDataStoreAlias, sAliasTemp, MapperStoreHttpZZZ.iPARAMETER_FIELDNAME);
										
					//Mit dem Korrektem Alias kann man nun aus dem DataStore Objekt die MEtadata-Informationen auslesen
					String sAliasUIComponent = dataStore.getMetadata(sAliasComponent, DataFieldZZZ.FIELDNAME);
					if(StringZZZ.isEmpty(sAliasUIComponent)){
						ExceptionZZZ ez = new ExceptionZZZ("No component alias found for the alias '" + sAliasComponent + "' mapped in the DataStore '" + sDataStoreAlias + "'");
						throw ez;
					}
					String sClassMethod = dataStore.getMetadata(sAliasComponent, DataFieldZZZ.FIELDMETHOD);
					if(StringZZZ.isEmpty(sClassMethod)){
						ExceptionZZZ ez = new ExceptionZZZ("No method mapped for the component '" + sAliasUIComponent +"' (which has the alias '" + sAliasComponent + "') in the DataStore '" + sDataStoreAlias + "'");
						throw ez;
					}
					saTokenAlias[icount] = sAliasUIComponent + "." + sClassMethod;	
				}else{
					//Konstanter String
					saTokenAlias[icount] = sAliasTemp;
				}
			}
						
			//4. nun die Arrays "fusionieren"
			String[] saTokenDelim = new String[listaTokenDelim.size()];
			saTokenDelim = (String[])listaTokenDelim.toArray(saTokenDelim);
			String[] saTemp = StringArrayZZZ.plusStringArray(saTokenAlias, saTokenDelim, "BEHIND");
			
			
			//5. der zur�ckzugebende Wert ist ein einzelner String
			sReturn = StringArrayZZZ.implode(saTemp);
			
		}//END main:
		return sReturn;
	}
	
	public DataStoreZZZ getDataStoreUsed() throws ExceptionZZZ{
		DataStoreZZZ objReturn = null;
			if(this.dataStore==null){
				MapperStoreHttpZZZ objMapper = this.mapperStore;
				objReturn = objMapper.getDataStoreByAlias(this.sDataStoreAlias);
				this.dataStore = objReturn;
			}else{
				objReturn = this.dataStore;
			}
		return objReturn;
	}
	
	public MapperStoreHttpZZZ getMapperStore(){
		return this.mapperStore;
	}
}
