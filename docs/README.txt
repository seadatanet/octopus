========================================================
ENVIRONNEMENT DE DEV
========================================================

Eclipse IDE for Java Developers avec plugins
- subclipse / subersive
- e(fx)clipse
- maven

Repo svn projets :
sismertoolslib : https://scmforge.ifremer.fr/authscm/****/svn/sismermco/sismerTools/sismerToolsLib
mgd : https://scmforge.ifremer.fr/authscm/****/svn/sismermco/sismerTools/mgd
cfpointLib : https://scmforge.ifremer.fr/authscm/****/svn/sismermco/sismerTools/cfpointLib
OdvSDN2CFPointLib : https://scmforge.ifremer.fr/authscm/****/svn/sismermco/sismerTools/OdvSDN2CFPointLib
medatlasreader : https://scmforge.ifremer.fr/authscm/****/svn/sismermco/sismerTools/medatlasreader
octopus : https://scmforge.ifremer.fr/authscm/****/svn/sismermco/sismerTools/octopus/trunk

Repo svn data test: 
MedSDN2CFPOINT_datatest : https://scmforge.ifremer.fr/authscm/****/svn/sismermco/MedSDN2CFPOINT/MedSDN2CFPOINT_datatest
OdvSDN2CFPoint_datatest : https://scmforge.ifremer.fr/authscm/****/svn/sismermco/OdvSDN2CFPoint/OdvSDN2CFPoint_datatest


Build maven avec "clean install" sur les projets dans cet ordre :
1 sismertoolslib
2 mgd
3 cfpointLib
4 OdvSDN2CFPointLib
5 medatlasreader
6 octopus

========================================================
FICHIER .EXE POUR  WINDOWS
========================================================

Le fichier octopus.exe utilisé pour le lancement sous windows est généré à l'aide de launch4j  (http://launch4j.sourceforge.net/)
Utiliser au moins la version 3.11 pour compatibilité avec windows 10.

Modification de l'icône du .exe: utiliser javaexe (http://devwizard.free.fr/html/fr/JavaExe.html)

========================================================
LIVRABLE
========================================================
Tâches à effectuer avant génération:
- mettre à jour le journal des modifications
   * anglais: /src/main/resources/fr/ifremer/octopus/view/changelog/changelog_en_GB.html
   * francais:/src/main/resources/fr/ifremer/octopus/view/changelog/changelog_fr_FR.html
- vérifier le no de version maven: x.y.z avec x=majeur, y=mineur, z=build  (le no de version dans le about est lu directement dans le pom.properties)
- vérifier que les tests de non regression n'ont pas d'erreur
- mettre à jour le manuel

Méthode manuelle
-----------------
FERMER les projets dependances de Octopus dans le workspace eclipse (sinon ils ne sont pas inclus dans le path du build!)
mvn clean install
build ant

zipper un repertoire contenant
octopus/build/dist/octopus.jar
octopus/build/dist/libs/* 
octopus/resources


Jenkins
-----------------
JDK:
JDK8u60

svn:
https://forge.ifremer.fr/svn/sismermco/sismerTools/octopus/trunk@HEAD -> .
https://svn-brest.altran.com/svnprojets/commun/maven/mavenSettings@HEAD -> mavenSettings

build:
root pom = pom.xml
goal and options = clean install -Paltran -U deploy

post steps:

execute shell=
cd target/livrableLinux; 
cp -R octopus octopus_${POM_VERSION};
chmod +x octopus_${POM_VERSION}/octopus.sh;
zip -r octopus_Linux_${POM_VERSION}_$BUILD_ID.zip octopus_${POM_VERSION}/* -x .svn;
cd ../../target/livrableWin;
cp -R octopus octopus_${POM_VERSION};
zip -r octopus_Win_${POM_VERSION}_$BUILD_ID.zip octopus_${POM_VERSION}/* -x .svn;

archive the artefacts=target/livrable*/octopus*.zip