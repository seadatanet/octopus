# Projet Octopus

![Schema dependances](/docs/dependances.png "Schéma des dépendances Octopus")

## Build du projet

### Build en local

```sh
mvn clean install
```

Si certaines dépendances ne peuvent pas être téléchargées, vérifier les configurations de tokens et les adresses des repos maven,
et réessayer avec l'option `-U`.

```sh
mvn -U clean install -DskipTests
```

## Cycle de vie du projet

### Branches

Suivre au maximum la [convention de nommages de branche préconisée à Ifremer](https://dev-ops.gitlab-pages.ifremer.fr/documentation/gitlab_quickstart/git/git/#convention-de-nommage-de-branche)

### Processus d'intégration continue

Le processus d'intégration continue mis en place est normalisé car il s'appuie sur le [socle CI/CD Ifremer](https://dev-ops.gitlab-pages.ifremer.fr/templates/automatisation/ci-cd/), \
qui propose un [catalogue de pipelines d'automatisation Java](https://dev-ops.gitlab-pages.ifremer.fr/templates/automatisation/ci-cd/pipelines/java/) \
utilisé dans le pipeline du projet `.gitlab-ci.yml`.

### Version

Pour générer une version de l'application [créer un tag Git](https://dev-ops.gitlab-pages.ifremer.fr/documentation/gitlab_quickstart/gitlab/repository/#creer-une-version). Ceci exécutera automatiquement les tâches automatisées permettant de générer les livrables.

### Release

Une releases Gitlab est créée automatiquement à la création d'une version, elle regroupe les sources de l'application et ses artefacts pour la version en question. La liste des release est consultable depuis la page principale du projet.

### Déploiement

#### Validation

Suivre la [procédure dédiée](https://dev-ops.gitlab-pages.ifremer.fr/hebergement_web/documentation/declaration/deployer-et-tester-une-application-sur-isival/) pour une première intégration de l'application à la plateforme Dockerisée de validation Ifremer.

#### Production

Procédure de mise en production : <https://dev-ops.gitlab-pages.ifremer.fr/hebergement_web/documentation/overview/mex-et-mep/>
NT :** dans le pipeline gitlab-ci, le suffixe "-SNAPSHOT" est ignoré, le pipeline configure lui-même l'ajout du suffixe.