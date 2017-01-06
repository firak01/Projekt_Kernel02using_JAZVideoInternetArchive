package use.via.client.module.export;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.DriveEasyZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.txt.TxtReaderZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJTextFieldListening4ComponentSelectionResetZZZ;

public class CommonUtilVIA  implements IConstantZZZ {
	private CommonUtilVIA() {
		//private construktor zum verbergen.
	}
	
	
	public static String computeDateLastModifiedByFile(File file){
		String sReturn="";
		main:{
			if(file==null) break main;			
			//Das hier nicht abfragen. Eine Abfrage auf ein CD-Rom Laufwerk erzeugt die Aufforderung eine CD-einzulegen. Das Problem ist, das diese Abfrage so h�ufig kommt, wie diese Methode aufgerufen wird. .... if(file.exists()==false)	break main;
			
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "DETAILS �ber das Erstelldatum des Laufwerks/der CD/DVD: " + file.lastModified());
			Date objDate = new Date(file.lastModified());
	
			Calendar cal = Calendar.getInstance();
			cal.setTime(objDate);
			int iYear = cal.get(Calendar.YEAR);
			if(iYear == 1900){ 
				//!!! aus irgendeinem Grund muss wohl ein Leerwert als 1.1.1900 zur�ckgegeben werden. Darauf pr�fen
				sReturn = "";
			}else{
				//Merke: Das Servlet setzt das �bergebene "Brenndatum/Erstelldatum" in das format yymmdd um.    String sFormat = "yyMMdd"; //das ist das normale Datumsformat "dd.MM.yyyy";
				String sFormat = "dd.MM.yyyy"; 
				SimpleDateFormat objFormat = new SimpleDateFormat(sFormat);
				sReturn = objFormat.format(objDate);
			}							
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "DETAILS �ber das Erstelldatum des Laufwerks/der CD/DVD: " + sReturn);
		}//end main:
		return sReturn;
	}
		
	/** Falls im Dateinamen Unterstriche vorhanden sind, werden diese ersetzt durch Leerzeichen oder Kommata.
	 *   Zudem wird ein ggf. vorhandener Serienname gesucht. Dieser sollte dann vor dem ersten Bindestrich stehen.
	 *   Der dort gefundene Wert wird aber auch noch mit dem "Katalog der Serien" geprüft. Ist er dort vorhanden, 
	 *   so wird er als Serienname akzeptiert.
	 *   
	 *   Merke: an Position 0 des Vektors steht der Serienname
	 *   an Position 1 des Vektors steht der ggf. übriggebliebenen Movietitle
	 *   
	 *    Diese Methode wird z.B. in den ListenerComponenten "Serie Combo Box" und "JTextField MovieTitle" eingesetzt.
	* @param file
	* @return
	* 
	* lindhaueradmin; 21.03.2007 14:48:30
	 * @throws ExceptionZZZ 
	 */
	public static Vector computeMovieDetailByFile(File fileToCheck, File fileCatalogSerie) throws ExceptionZZZ{
		Vector objReturn = new Vector();
		main:{
			if(fileToCheck==null) break main; 	//Existenzpr�fung der zu checkenden Datei nicht erforderlich
			
//			Der Default-Titel ist der Dateiname OHNE Endung. Er soll aber noch hinsichtlich der Unterstriche verarbeitet werden.
			String sAllRaw = FileEasyZZZ.getNameOnly(fileToCheck.getName());
			if(StringZZZ.isEmpty(sAllRaw)) break main;
						
			//NEU 2008-08-16
			//deutsche Umlaute sollen ersetzt werden, aber nur, wenn es noch nicht einen Umlaut im Filenamen gibt, Groß- und Kleinbuchstaben der Umlaute (sofern Großbuchstebe vorhanden).
			org.apache.regexp.RE objReUmlaut = new org.apache.regexp.RE("[öÖüÜäÄß]");
			boolean bHasUmlaut = objReUmlaut.match(sAllRaw);

			//###########################################
			//0. Vorbereitung : Es kann in seltenen Fällen vorkommen, dass kein Bindestrich - sondern - verwendet worden ist (ANSI Problem).
			sAllRaw = StringZZZ.replace(sAllRaw, "�", "-");
						
			//20170105: Fehler: Vor der "Bereichsaufteilung" erst noch den Bemerkungsteil von den anderen Teilen abtrennen.
			//NEIN, bei Verwendung des Tokenizers wird immer nur ein Delimiter mit der Länge 1 genommen. Zwar ist ein Deliminter String möglich. Das sind aber nur verschiedene erlaubte Trennzeichen.
			//StringTokenizer tokenizerAll = new StringTokenizer(sAllRaw, "---", false);
			String[] saAll = StringZZZ.explode(sAllRaw, "---");
					
			String sSerie = "";
			String sMovie = "";
			String sRemark = "";
			for(int iPosition=0; iPosition <= saAll.length-1;iPosition++){				
				String sSectionCurrent = saAll[iPosition];
				StringTokenizer tokenizerSection = new StringTokenizer(sSectionCurrent, "-", false);
				
				//1. Ersetzen von Unterstrichen. Wichtig: Das muss für jeden Bereich separat geschehen, z.B. weil an den Originären Dateinamen Kommentare angehängt worden sind
				int iSectionPosition=-1;
				while(tokenizerSection.hasMoreTokens()){
					iSectionPosition++;
					String sSectionSub = (String) tokenizerSection.nextToken();
					sSectionSub = sSectionSub.trim();
					
					//1. Schritt Ersetzen von Unterstrichen durch ", "
					String stemp4proof = "";
					if(sSectionSub.length()>=2){
						stemp4proof = sSectionSub.substring(2); //Erst nach dem 3. Zeichen anfangen hinsichtlich der für ", " verwendeten Prüfung
					}else{
						stemp4proof = sSectionSub;
					}
					if(StringZZZ.contains(stemp4proof, "_")){					
						if(StringZZZ.contains(sSectionSub, " ")){  //Merke: Ein Komma im Dateinamen ist nicht möglich. Darum wurde dies ggf. durch Unterstrich ersetzt.
							sSectionSub = StringZZZ.replace(sSectionSub, "_", ", ");
						}					
					}	
					
//					2. Schritt nun noch vorhandene Unterstriche durch Leerzeichen ersetzen
					sSectionSub = StringZZZ.replace(sSectionSub, "_", " "); //Merke: Das "Aufnahmetool" setzt(e) ggf. f�r Leerzeichen automatisch den Unterstrich. Dies soll hier wieder korregiert werden.
					
					//####### Herausziehen der Details
					//BEREICHE UNTERSCHEIDEN NACH Serie und Titelteil
					if(iPosition==0 && iSectionPosition==0){
						//Hauptteil
						//2. Herausziehen einen möglichen Seriennamens (und vergleichen mit einem ggf. vorhandenen Katalogeintrag)			
						if(fileCatalogSerie!=null){																
//							2b. Prüfen auf Existenz in der Katalogdatei
							if(fileCatalogSerie.exists()==false){
								ReportLogZZZ.warn("provided catalog file '" + fileCatalogSerie.getPath() + "' for CatalogSerie, does not exist. No 'serie check' possible.");
							}else{
								//Falls der Katalog übergeben wurde wird geprüft auf Seriennamen			
								int iPos = sSectionCurrent.indexOf("-"); //Merke: Die gesammte Section zur Prüfung hier verwenden, also das was ggfs. links von --- steht.
								if(iPos >= 1){ //Position 0 würde bedeuten: Da ist kein Serienname voranstehend																		
									String sSerieTemp = sSectionSub; 
										
									//+++ JETZT muss schon die Ersetzung der dt. Umlaute erfolgen
									if(bHasUmlaut==false){  //Also nur ersetzen, wenn es nicht schon einen Umlaut gibt.
										sSerieTemp = StringZZZ.replaceCharacterGermanFromSentence(sSerieTemp);
									}//end bHasUmlaut
									
									//+++ Die Anfänge der Werte sollen mit einem Großbuchstaben belegt werden. Also: Ersten Buchstaben "Kapitalizen"
									sSerieTemp = sSerieTemp.trim();
									sSerieTemp = StringZZZ.capitalize(sSerieTemp);
									
									
									//Nun die Katalogdatei auf Vorhandensein der Zeile prüfen. Dabei wird NICHT AUF CASESENSITIVIT�T WERT GELEGT
									//FileTextParserZZZ parser = new FileTextParserZZZ(fileCatalogSerie);
									TxtReaderZZZ parser = new TxtReaderZZZ(fileCatalogSerie, "IgnoreCase");
									long lByte = parser.readPositionLineFirst(sSerieTemp, 0);
									if(lByte>=0){
										//Katalogeintrag gefunden.
	//									Ich gehe davon aus, dass die Katalogdatei besser gepflegt ist als die Daten aus dem zu verarbeitenden Dateinamen. Darum wird der Katalogeintrag verwendet.
										sSerie = parser.readLineByByte(lByte);
									}else{
										sSerie = "";
									}
								}//end if iPos >= 1																	
							}
						}//end if fileCatalogSerie == null
									
						//3. Herausziehen des möglichen Movietitels (merke, die ggf. vorhandener Bemerkung kommt in einer anderen Section)
						if(sSerie.equals("") && iSectionPosition==0){
							String sMovieTemp = sSectionSub;
							
							//+++ JETZT muss schon die Ersetzung der dt. Umlaute erfolgen
							if(bHasUmlaut==false){  //Also nur ersetzen, wenn es nicht schon einen Umlaut gibt.
								sMovieTemp = StringZZZ.replaceCharacterGermanFromSentence(sMovieTemp);
							}//end bHasUmlaut
							
							//+++ Die Anfänge der Werte sollen mit einem Großbuchstaben belegt werden. Also: Ersten Buchstaben "Kapitalizen"
							sMovieTemp = sMovieTemp.trim();
							sMovieTemp = StringZZZ.capitalize(sMovieTemp);							
							if(sMovie.equals("")){
								sMovie = sMovieTemp;
							}else{
								sMovie = sMovie + " - " + sMovieTemp;
							}
						}
					}else if(iPosition==0 && iSectionPosition>=1){
						String sMovieTemp = sSectionSub;
						
						//+++ JETZT muss schon die Ersetzung der dt. Umlaute erfolgen
						if(bHasUmlaut==false){  //Also nur ersetzen, wenn es nicht schon einen Umlaut gibt.
							sMovieTemp = StringZZZ.replaceCharacterGermanFromSentence(sMovieTemp);
						}//end bHasUmlaut
						
						//+++ Die Anfänge der Werte sollen mit einem Großbuchstaben belegt werden. Also: Ersten Buchstaben "Kapitalizen"
						sMovieTemp = sMovieTemp.trim();
						sMovieTemp = StringZZZ.capitalize(sMovieTemp);						
						if(sMovie.equals("")){
							sMovie = sMovieTemp;
						}else{
							sMovie = sMovie + " - " + sMovieTemp;
						}
					}else if(iPosition>=1){
						String sRemarkTemp = sSectionSub;
						
						//Bemerkungsteile
						//### Ersetzung der dt. Umlaute in den Bemerkungen
						if(bHasUmlaut==false){  //Also nur ersetzen, wenn es nicht schon einen Umlaut gibt.							
							sRemarkTemp = StringZZZ.replaceCharacterGermanFromSentence(sRemarkTemp);							
						}//end bHasUmlaut
						
						//+++ Die Anfänge der Werte sollen mit einem Großbuchstaben belegt werden. Also: Ersten Buchstaben "Kapitalizen"
						sRemarkTemp = sRemarkTemp.trim();
						sRemarkTemp = StringZZZ.capitalize(sRemarkTemp);																	
						if(sRemark.equals("")){
							sRemark = sRemarkTemp;
						}else{
							sRemark = sRemark + "---" + sRemarkTemp;
						}
					} //End if iPosition										
				}//End while tokenizerSection				
			}//End for saAll.length
			
	
			//20090322 um die Bindestriche der Serie / des Filmtitels / in der Bemerkung soll jeweils ein Leerszeichen stehen.
			//!!! Vermeiden, dass auf diese Art doppelte Leerzeichen enstehen
			sMovie = StringZZZ.replaceFarFrom(sMovie, "-", " - ");
			sSerie = StringZZZ.replaceFarFrom(sSerie, "-", " - ");
			sRemark = StringZZZ.replaceFarFrom(sRemark, "-", " - ");
			sRemark = StringZZZ.replaceFarFrom(sRemark, "---", " --- ");
			
			//#################################################
			//4. Vektor füllen
			objReturn.add(0, sSerie);
			objReturn.add(1, sMovie);
			objReturn.add(2, sRemark);
		}
		return objReturn;
	}

	public static String computeDriveTitleByRootFile(File fileRoot) throws ExceptionZZZ{
		String sReturn = "";
		main:{		
				if(fileRoot==null){
					sReturn = JLabel_Listening_CarrierTitleVIA.sTEXT_INITIAL;
					break main;
				}
				
				//Keine Pr�fung auf file.exists .. bei cd-rom laufwerken kommt so eine Aufforderung die CD einzulegen. Das Problem ist, dass diese Aufforderung mehrmals kommt wenn hier die Pr�fung stattfindet....
				//if(fileRoot.exists()==false){
				//	sReturn = JLabel_Listening_CarrierTitleVIA.sTEXT_NO_DRIVE;
				//	break main;
				//}
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Hole den Titel von der Datei mit folgendem Pfad: FileRoot='" + fileRoot.getPath() + "'");
				sReturn = DriveEasyZZZ.getTitle(fileRoot);
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Gefundener Titel: sReturn ='" + sReturn + "'");
				if(StringZZZ.isEmpty(sReturn)) sReturn = JLabel_Listening_CarrierTitleVIA.sTEXT_NO_DRIVE; //besser als file.exists abzufragen !!!
		}//end main
		return sReturn;
	}
}
