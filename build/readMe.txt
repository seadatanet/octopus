Les répertoire de livraison livrableWin et livrableLinux sont préparés par maven

Les fichiers executables (.sh pour linux et .exe pour windows) sont générés indépendement
- manuellement pour les .sh
- avec Launch4j pour les .exe


Launch4j: 
télécharger http://launch4j.sourceforge.net/
installer
ouvrir le fichier de config cfg_java10.xml
modifier les si besoin
générer le .exe pour java >= 9 en cliquant sur la roue dentée
vérifier en cliquant sur le triangle
pour générer le ..exe pour java <=8, retirer les arguments de la jvm (onglet jre)



A lancer apres le build:

cd target/livrableLinux; 
cp -R octopus octopus_${POM_VERSION};
chmod +x octopus_${POM_VERSION}/octopus.sh;
zip -r octopus_Linux_${POM_VERSION}_$BUILD_ID.zip octopus_${POM_VERSION}/* -x .svn;
cd ../../target/livrableWin;
cp -R octopus octopus_${POM_VERSION};
zip -r octopus_Win_${POM_VERSION}_$BUILD_ID.zip octopus_${POM_VERSION}/* -x .svn;