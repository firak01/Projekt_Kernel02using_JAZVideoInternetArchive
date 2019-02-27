REM START OHNE ECLIPSE: java -jar c:\java\VIAClient\VIAClientServer_fat.jar -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini
REM START DES CODES IM ECLIPSE WORKSPACE: 
REM java C:\1fgl\repo_Eclipse4_v02\Projekt_Kernel02using_JAZVideoInternetArchive\JAZVideoInternetArchive\src\use\via\ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini

REM 1. Wechsle in das Klassenverzeichnis
REM /d ist notwendig, um per Variable das Verzeichnis/Laufwerk zu wechseln. Beachte Verzeichnisphad in Hochkommata.
set myDirectory="C:\1fgl\client\VIAClient"
cd /d %myDirectory%

REM java -jar VIAClientServer_fat.jar -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini

REM Ohne Angabe des absoluten Verzeichnisses wird das Verzeichnis genommen, in dem die .jar Datei liegt.
java -jar VIAClientServer_fat.jar -k VIA -s 02 -f ZKernelConfigVideoArchiveClientFGL02.ini

pause
exit
