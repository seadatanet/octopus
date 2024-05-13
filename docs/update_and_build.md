# Octopus update and build

## Update version

- Mettre à jour les versions dans les POM :
  - version du pom
  - version des dépendances
- Mettre à jour les changelog `/octopus/src/main/resources/fr/ifremer/octopus/view/changelog`

## Build

- changer version partout
- changer le changelog `/octopus/src/main/resources/fr/ifremer/octopus/view/changelog`
- Update de la doc utilisateur (`octopus/docs`), générer et placer le PDF dans `octopus/resoures`, au moins pour mettre à jour la version
- Commiter les external resources à jour `octopus/resoures/externalResources`
- lancer les build Jenkins
  - sismertoolslib
  - mgd
  - cfpointlib
  - odv2SDNCfpointLib
  - medatlasreader
  - octopus
- récupérer les livrables
  - archives des exécutables (linux, linux standalone, windows, windows standalone) + enlever le No de build (pour la RELEASE)
  - lib octopus-x.y.z.jar
- Uploader les livrables dans le cloud prévu (ex : `https://cloud.ifremer.fr/index.php/apps/files/?dir=/Octopus/Livrables/1.8.0/PROD`)