LIVRABLE
==============
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
chmod +x octopus/octopus.sh;
zip -r octopus_Linux_$JOB_NAME_$BUILD_ID.zip octopus/* -x .svn;
cd ../../target/livrableWin32;
zip -r octopus_win32_$JOB_NAME_$BUILD_ID.zip octopus/* -x .svn;
cd ../../target/livrableWin64;
zip -r octopus_win64_$JOB_NAME_$BUILD_ID.zip octopus/* -x .svn

archive the artefacts=target/livrable*/octopus*.zip