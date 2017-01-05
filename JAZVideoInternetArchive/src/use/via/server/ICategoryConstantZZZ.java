package use.via.server;

/** Merke: Falls eine Konstante ein Präfix ist, dann kommt dahinter zumindest immer der Application Key
 * @author lindhauer
 *
 */
public interface ICategoryConstantZZZ {
	/**Mehrfachwertefeld in dem dokument, dass die Kategorisierung übernimmt 
	     <BR>Besteht aus: 
	     <BR>Aliasname (s. Feld objAlias) des Dokuments aus dem ein Kategorierfeld kommt.
	     #
	 	UniversalId
	  	# 
	 	Feldname (s. Feld objCatRef... ) in dem Dokument für die Kategoriesierung
 		<BR> z.B.: "Carrier# 097849794... #CarrierTitle" : "Carrier#08080... # CarrierId"
	 */
	public static String sFIELD_PREFIX_CATEGORYSOURCE_META="objCatRefSource"; 
	  
	
/**Wert eines anderen Dokuments, unter dem dieses Dokument auch kategoriesierbar sein soll
 *  <BR>Z.B. als Feldname: 'objCatValCarrierTitle' mit dem Inhalt des Feldes 'CarrierTitle'
 *  <BR>                              
 *  <BR>Merke: Feldname 'objCatValVIA' (also mit dem ApplicationKey as Suffix). Mit dem Inhalt dieses Felds werden  alle Feldnamen eines Dokuments, die zur Kategorisierung herangezogen werden, beschrieben.
 */
public static String sFIELD_PREFIX_CATEGORY_META="objCatVal";

/**Das ist also in dem dokument der Wert, der für eine Kategorisierung eines Dokuments benutzt werden wird.
 *  <BR>z.B. "CatRefCarrierTitle" als Feldname, um das Dokument nach CarrierTitle zu kategorisieren (Merke: Das Dokumetn ist nicht vom Typ Carrier)
 */
public static String sFIELD_PREFIX_CATEGORY="CatRef";      


/**Wert des Dokuments, der herangezogen werden soll, zum Kategoriesieren anderer Dokumente
 * <BR>Z.B. als Feldname: CatValCarrierTitle = "Film 1" (Merke: Das gilt für ein Dokument des Typs 'Carrier')
 */
public static String sFIELD_PREFIX_CATEGORY_VALUE = "CatVal";         

/**Name des Dokumenttyps
 *<BR>Z.B. als Feldname: AliasVIA = "carrier" 
 */
public static String sFIELD_PREFIX_ALIAS="objAlias";	                         


/**Feldnamen Präfix für die ReferenzId. Normalerweise die universalid des Documents.
 * <BR> z.B. objRefVIA
 * 
 */
public static String sFIELD_PREFIX_REFERENCE="objRef";							

public static String sVIEW_LOOKUP_CATEGORY_VALUE="viwLookupCatVal";
public static String sVIEW_LOOKUP_CATEGORY_REFERENCE = "viwLookupCatRefSource";
public static String sVIEW_LOOKUP_REFERENCE = "viwLookupRef";

}
