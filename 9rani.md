# SmartRoad Maroc — Analyse Intelligente de la Sécurité Routière (NARSA 2021–2024)

Plateforme web et API REST d'analyse statistique et de modélisation Machine Learning des accidents corporels de la circulation au Maroc à partir des données ouvertes officielles de la **NARSA** (2021–2024).

## Architecture du Projet

- `data/` : Fichiers Excel officiels NARSA (2021–2024) et dataset nettoyé `accidents_narsa_2021_2024.csv`.
- `explore.py` & `prepare_data.py` : Pipeline d'ingestion, contrôle qualité et calcul des indicateurs métier (taux de mortalité, blessés graves, indice de gravité, victimes/accident).
- `train_models.py` : Entraînement et comparaison (Validation Leave-One-Out) de 5 algorithmes Scikit-learn + AutoML **FLAML**, suivis avec **MLflow**.
- `models/` : Modèles sérialisés (`.joblib`) et tableaux de benchmarks.
- `main.py` : Backend **FastAPI** découplé (API REST + documentation interactive Swagger).
- `frontend/` : Application web **React + Vite + Recharts**.

## Installation et Exécution Locale

### 1. Préparation des données et entraînement des modèles
```powershell
pip install -r requirements.txt
python prepare_data.py
python train_models.py