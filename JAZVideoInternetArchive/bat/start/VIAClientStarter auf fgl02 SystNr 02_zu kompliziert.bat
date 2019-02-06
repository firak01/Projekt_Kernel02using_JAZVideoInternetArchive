REM START OHNE ECLIPSE: java -jar c:\java\VIAClient\VIAClientServer_fat.jar -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini
REM START DES CODES IM ECLIPSE WORKSPACE: 
REM java C:\1fgl\repo_Eclipse4_v02\Projekt_Kernel02using_JAZVideoInternetArchive\JAZVideoInternetArchive\src\use\via\ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini

REM 1. Wechsle in das Klassenverzeichnis
REM /d ist notwendig, um per Variable das Verzeichnis/Laufwerk zu wechseln. Beachte VErzeichnisphad in Hochkommata.
set myWorkspace="C:\1fgl\workspace\Eclipse4_V02"
set myRepo="C:\1fgl\repo_Eclipse4_v02"
cd \
cd /d %myRepo%
cd /d "Projekt_Kernel02using_JAZVideoInternetArchive\JAZVideoInternetArchive\bin"

REM java use.via.ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini

REM Das startet zwar die Java-Application, findet aber keine anderen eingebundenen Projekte
REM cd bin
REM java use.via.ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini


set DUMMY=%myRepo%\Projekt_Kernel02_JAZKernel\JAZDummy
set Kernel=%myRepo%\Projekt_Kernel02_JAZKernel\JAZKernel
set KernelLogReport=%myRepo%\Projekt_Kernel02_JAZKernelLogReport\JAZKernelLogReport
set KernelNotes=%myRepo%\Projekt_Kernel02_JAZKernelNotes\JAZKernelNotes
set KernelNotesLogReport=%myRepo%\Projekt_Kernel02_JAZKernelNotesLogReport\JAZKernelNotesLogReport
set KernelUI=%myRepo%\Projekt_Kernel02_JAZKernelUI\JAZKernelUI
set KernelUIExpression=%myRepo%\Projekt_Kernel02_JAZKernelUIExpression\JAZKernelUIExpression
set KernelLanguageMarkup=%myRepo%\Projekt_Kernel02_JAZLanguageMarkup\JAZLanguageMarkup
set KernelUIExpression=%myRepo%\Projekt_Kernel02using_JAZVideoInternetArchive\JAZVideoInternetArchive

REM Einbinden externer Bibliotheken
set KernelFileCollection=%myWorkspace%\ZKernelFileCollection
set A=%KernelFileCollection%\commons.lang.2/commons-lang2-2.6/commons-lang-2.6.jar
set KernelFileZJar=%myWorkspace%\ZKernelFileZJar\ZKernelFileCollection.jar

set Classpath=%Classpath%;%JAVA_HOME%\lib;%DUMMY%\bin;%Kernel%\bin;%KernelLogReport%\bin;%KernelNotes%\bin;%KernelNotesLogReport%\bin;%KernelUI%\bin;%KernelUIExpression%\bin;%KernelLanguageMarkup%\bin;%KernelUIExpression%\bin;%KernelFileCollection%\bin;
set Classpath=%Classpath%;%A%;%KernelFileZJar%
REM TROTZDEM WIRD LOG4J NICHT GEFUNDEN... DEN CLASSPATH PER HAND ZU SETZEN IST ZU KOMPLZIERT. VERWENDE STARTBARE .JAR DATEI STATTDESSEN.
echo ################################################################################
java -cp %Classpath% use.via.ApplicationSingletonVIA -k VIA -s 02 -d c:\java\VIAClient -f ZKernelConfigVideoArchiveClientFGL02.ini
pause
