from pathlib import Path
import joblib
import pandas as pd
from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

app = FastAPI(
    title="SmartRoad Maroc API",
    description="API REST d'analyse de la sécurité routière au Maroc (Données officielles NARSA 2021-2024) pour Web & Mobile.",
    version="1.0.0",
)

# Autoriser les requêtes depuis le frontend React (Vite) et l'application mobile
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Chargement des données et des modèles entraînés
DATA_PATH = Path("data/accidents_narsa_2021_2024.csv")
REG_MODEL_PATH = Path("models/modele_regression_accidents.joblib")
CLF_MODEL_PATH = Path("models/modele_classification_risque.joblib")
BENCH_REG_PATH = Path("models/benchmark_regression.csv")
BENCH_CLF_PATH = Path("models/benchmark_classification.csv")

df_data = pd.read_csv(DATA_PATH)
modele_reg = joblib.load(REG_MODEL_PATH)
modele_clf = joblib.load(CLF_MODEL_PATH)
bench_reg = pd.read_csv(BENCH_REG_PATH)
bench_clf = pd.read_csv(BENCH_CLF_PATH)


# --- Schémas Pydantic pour la validation des requêtes ---
class PredictionTendanceRequest(BaseModel):
    annee: int = Field(..., ge=2021, le=2035, description="Année cible (ex: 2025)")
    zone: str = Field(..., description="Agglomeration ou Hors_agglomeration")


class ClassificationRisqueRequest(BaseModel):
    taux_mortalite_pct: float = Field(..., ge=0, le=100, description="Taux de mortalité en %")
    taux_blesses_graves_pct: float = Field(..., ge=0, le=100, description="Taux de blessés graves en %")
    victimes_par_accident: float = Field(..., ge=0, le=50, description="Nombre moyen de victimes par accident")


# --- Routes REST ---
@app.get("/", tags=["Général"])
def accueil():
    return {
        "projet": "SmartRoad Maroc",
        "source": "NARSA (Open Data Maroc 2021-2024)",
        "documentation_swagger": "/docs",
        "statut": "opérationnel",
    }


@app.get("/api/statistiques", tags=["Statistiques"])
def get_statistiques(
    annee: int | None = Query(None, description="Filtrer par année (2021-2024)"),
    zone: str | None = Query(None, description="Filtrer par zone (Agglomeration, Hors_agglomeration, Total)"),
):
    res = df_data.copy()
    if annee is not None:
        res = res[res["Annee"] == annee]
    if zone is not None:
        res = res[res["Zone"].str.lower() == zone.lower()]
    if res.empty:
        raise HTTPException(status_code=404, detail="Aucune donnée trouvée pour ces filtres.")
    return res.to_dict(orient="records")


@app.get("/api/kpis", tags=["Statistiques"])
def get_kpis():
    totaux = df_data[df_data["Zone"] == "Total"].sort_values("Annee")
    d2021 = totaux[totaux["Annee"] == 2021].iloc[0]
    d2024 = totaux[totaux["Annee"] == 2024].iloc[0]

    evo_accidents = round(((d2024["Total_Accidents"] - d2021["Total_Accidents"]) / d2021["Total_Accidents"]) * 100, 2)
    evo_tues = round(((d2024["Tues"] - d2021["Tues"]) / d2021["Tues"]) * 100, 2)
    evo_victimes = round(((d2024["Total_Victimes"] - d2021["Total_Victimes"]) / d2021["Total_Victimes"]) * 100, 2)

    return {
        "periode": "2021-2024",
        "cumul_accidents_4ans": int(totaux["Total_Accidents"].sum()),
        "cumul_tues_4ans": int(totaux["Tues"].sum()),
        "cumul_blesses_graves_4ans": int(totaux["Blesses_Graves"].sum()),
        "cumul_victimes_4ans": int(totaux["Total_Victimes"].sum()),
        "accidents_2024": int(d2024["Total_Accidents"]),
        "tues_2024": int(d2024["Tues"]),
        "taux_mortalite_2024_pct": float(d2024["Taux_Mortalite_Pct"]),
        "evolution_accidents_2021_2024_pct": evo_accidents,
        "evolution_tues_2021_2024_pct": evo_tues,
        "evolution_victimes_2021_2024_pct": evo_victimes,
    }


@app.get("/api/comparaison-zones", tags=["Analyse"])
def get_comparaison_zones():
    zones = df_data[df_data["Zone"] != "Total"]
    synthese = (
        zones.groupby("Zone", as_index=False)
        .agg(
            Total_Accidents=("Total_Accidents", "sum"),
            Total_Tues=("Tues", "sum"),
            Total_Blesses_Graves=("Blesses_Graves", "sum"),
            Total_Victimes=("Total_Victimes", "sum"),
            Taux_Mortalite_Moyen_Pct=("Taux_Mortalite_Pct", "mean"),
            Taux_Blesses_Graves_Moyen_Pct=("Taux_Blesses_Graves_Pct", "mean"),
            Victimes_Par_Accident_Moyen=("Victimes_Par_Accident", "mean"),
        )
        .round(3)
    )
    return synthese.to_dict(orient="records")


@app.get("/api/ml/benchmarks", tags=["Intelligence Artificielle"])
def get_benchmarks():
    return {
        "regression": bench_reg.to_dict(orient="records"),
        "classification": bench_clf.to_dict(orient="records"),
        "automl": {
            "regression_meilleur": "extra_tree",
            "classification_meilleur": "sgd",
            "note_methodologique": "Validation Leave-One-Out (8 observations : 4 années x 2 zones). Les projections sont indicatives.",
        },
    }


@app.post("/api/ml/predire-tendance", tags=["Intelligence Artificielle"])
def predire_tendance(req: PredictionTendanceRequest):
    zone_norm = req.zone.strip()
    if zone_norm not in ["Agglomeration", "Hors_agglomeration"]:
        raise HTTPException(
            status_code=400,
            detail="Le champ 'zone' doit être 'Agglomeration' ou 'Hors_agglomeration'.",
        )
    zone_code = 1 if zone_norm == "Hors_agglomeration" else 0
    X_in = pd.DataFrame({"Annee": [req.annee], "Zone_Code": [zone_code]})
    pred = float(modele_reg.predict(X_in)[0])
    return {
        "annee": req.annee,
        "zone": zone_norm,
        "accidents_estimes": max(0, int(round(pred))),
        "modele_utilise": "Regression_Lineaire",
        "avertissement": "Estimation statistique indicative basée sur la tendance nationale NARSA 2021-2024.",
    }


@app.post("/api/ml/classifier-risque", tags=["Intelligence Artificielle"])
def classifier_risque(req: ClassificationRisqueRequest):
    X_in = pd.DataFrame(
        [
            {
                "Taux_Mortalite_Pct": req.taux_mortalite_pct,
                "Taux_Blesses_Graves_Pct": req.taux_blesses_graves_pct,
                "Victimes_Par_Accident": req.victimes_par_accident,
            }
        ]
    )
    pred_code = int(modele_clf.predict(X_in)[0])
    probas = modele_clf.predict_proba(X_in)[0]
    profil = "Hors_agglomeration" if pred_code == 1 else "Agglomeration"
    niveau_gravite = "Élevé (Profil Route / Hors agglomération)" if pred_code == 1 else "Modéré (Profil Urbain / Agglomération)"

    return {
        "profil_predit": profil,
        "niveau_risque": niveau_gravite,
        "probabilite_agglomeration": round(float(probas[0]), 4),
        "probabilite_hors_agglomeration": round(float(probas[1]), 4),
        "modele_utilise": "Random_Forest_Classifier",
    }