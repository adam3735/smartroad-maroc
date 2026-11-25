# SmartRoad Maroc - Application Android Native (Kotlin + Jetpack Compose)

Application mobile native Android pour l'analyse des bilans officiels de la sécurité routière au Maroc (NARSA 2021-2024), inspirée de l'écosystème visuel **ONCF Voyages** (Bleu Royal `#14213D`, Orange Solaire `#FF6B00`, Cyan `#0284C7`).

## Nouveautés et Fonctionnalités

- **Ouverture Zellige Marocain** : Animation vectorielle géométrique inspirée du Zellige de Fès (rosace `Shamsa` à 16 pétales, entrelacs `Tafsil` animés et étoiles `Khatam` à 8 branches).
- **Bilingue FR / عربي (RTL)** : Bascule instantanée entre le français et l'arabe depuis l'en-tête sur l'ensemble des écrans, graphiques et simulateurs.
- **Voyage / Synthèse** : En-tête courbé Navy, sélecteur de trajet flottant, carrousel de billets perforés, jauges radiales, simulation autoroutière 3 voies à dépassement autonome et courbes spline interactives.
- **Explorateur NARSA** : Filtres dynamiques par milieu et année, export **E-Billet CSV** via le partage Android et registre tabulaire complet.
- **Cockpit Freinage & Collision** : Compteur 240 degrés temps réel, mode automatique ou manuel (pédale Maintenir le frein), transfert de masse, fumée/eau au freinage et modélisation physique des collisions.
- **Analytique Ratios & Simulateur IA** : Jauges radiales, projection de tendance (2025-2035) calculée à la demande, diagnostic Random Forest et benchmarks LOOCV.

## Compilation APK

```powershell
.\gradlew.bat assembleDebug
```

L'APK compilé est généré dans `SmartRoad-Maroc.apk` (et `app/build/outputs/apk/debug/app-debug.apk`).
