# Octopus - Livrables

## Principes de base

Les répertoires de livraison sont préparés par Maven.

Les fichiers executables (`.sh` pour Linux et `.exe` pour Windows) sont générés indépendamment
- manuellement pour les `.sh`
- avec Launch4j pour les `.exe`

Les nouveaux livrables sont conçus pour embarquer leur propre JRE. On génère donc un livrable par OS et par architecture (32 / 64 bits).

Répertoires générés :
- livrableWin32
- livrableWin64
- livrableLinux32
- livrableLinux64

Les confs et artefacts de Launch4j sont situés dans le répertoire `/build/windows/` du projet.

Contenu des livrables :
- `octopus.jar` : le JAR provenant du build.
- `octopus.exe` ou `octopus.sh` : l'exécutable de l'application, selon l'OS
- `resources/` : le répertoire contenant les ressources (manuel, ...)
- `jre/` : le répertoire contenant la JRE correspondant à l'OS/architecture.

Le contenu est fourni par Maven, à part la JRE qui doit pour l'instant être fournie manuellement.

## Launch4j

- télécharger http://launch4j.sourceforge.net/
- installer
- ouvrir le fichier de config voulu (voir les fichiers de config dans le projet)
- modifier si besoin
- générer le `.exe` en cliquant sur la roue dentée
- tester le bon fonctionnement en cliquant sur le triangle

## Legacy

Les répertoires de livraison livrableWin et livrableLinux sont préparés par maven

Les fichiers executables (.sh pour linux et .exe pour windows) sont générés indépendamment
- manuellement pour les .sh
- avec Launch4j pour les .exe

Launch4j
- télécharger http://launch4j.sourceforge.net/
- installer
- ouvrir le fichier de config cfg_java9.xml
- modifier les si besoin
- générer le .exe pour java >= 9 en cliquant sur la roue dentée
- vérifier en cliquant sur le triangle
- pour générer le ..exe pour java <=8, retirer les arguments de la jvm (onglet jre)

À lancer après le build:

```shell
$ cd target/livrableLinux
$ cp -R octopus octopus_${POM_VERSION}
$ chmod +x octopus_${POM_VERSION}/octopus.sh
$ chmod +x octopus_${POM_VERSION}/octopus_java9.sh
$ zip -r octopus_Linux_${POM_VERSION}_$BUILD_ID.zip octopus_${POM_VERSION}/* -x .svn
$ cd ../../target/livrableWin
$ cp -R octopus octopus_${POM_VERSION}
$ zip -r octopus_Win_${POM_VERSION}_$BUILD_ID.zip octopus_${POM_VERSION}/* -x .svn
```