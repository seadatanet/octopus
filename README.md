# Projet Octopus
## Build du projet

### Build en local

1. Dans le fichier local `~/.m2/settings.xml` configurer les tokens `gitlab-maven-internal` et `gitlab-maven-octopus` de la même manière
que dans le fichier ci_settings.xml en remplaçant les variables `DEPLOY_TOKEN_*` par leurs vraies valeurs (Voir fichier liste_tokens hors repo).
2. Ensuite effectuer la compilation normalement : (toutes les dépendances doivent avoir été buildées)

```sh
mvn clean install
```

Si certaines dépendances ne peuvent pas être téléchargées, vérifier les configurations de tokens et les adresses des repos maven,
et réessayer avec l'option `-U`.

```sh
mvn -U clean install -DskipTests
```

### Build en distant (gitlab-ci)

#### Configuration de gitlab-ci 

Le fichier `.gitlab-ci.yml` fait foi.  

3 jobs peuvent s'exécuter avec leur propre commande :

- sur les branches feature/hotfix/master/* à chaque commit : build_verify
- sur la branche develop à chaque commit : deploy_snapshot
- sur la branche develop sur action manuelle : deploy_snapshot (disponible après la génération du snapshot)

#### Tester le gitlab-ci en local

*Prérequis :* docker et gitlab-runner installé.

Procédure pour tester:

1. se placer dans le dossier parent de `.gitlab-ci.yml`
2. **Commiter les changements à tester (ils ne seront pas pris en compte dans le CI sinon)**
3. Exécuter la commande : (en ajoutant les tokens nécessaires)

```sh
gitlab-runner exec docker --env DEPLOY_TOKEN_INTERNAL=... --env DEPLOY_TOKEN_OCTOPUS=... build_verify
```

## Gestion des versions

### Incrémentation de version

La gestion des versions est configurée par la balise `project.version` dans le module `fr.ifremer.nemo.parent`.  
Le format de `project.version` doit suivre `<MAJOR>.<MINOR>.<FIX>(-SNAPSHOT)`.  
Tous les niveaux de versions doivent être incrémentés manuellement.

### Note le déploiement dans maven

Choix du repository de déploiement (voir section `<distributionManagement>` dans `pom.xml` ) :

- si `project.version` contient "-SNAPSHOT" (ex : `1.2.1-SNAPSHOT`), les artifacts sont déployés dans le repository désigné par `snapshotRepository` et sont horodatés
- si `project.version` ne contient pas "-SNAPSHOT", les artifacts sont déployés dans le repository `repository`.

**IMPORTANT :** dans le pipeline gitlab-ci, le suffixe "-SNAPSHOT" est ignoré, le pipeline configure lui-même l'ajout du suffixe.