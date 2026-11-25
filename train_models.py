import os
import joblib
import mlflow
import mlflow.sklearn
import pandas as pd
from flaml import AutoML
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
from sklearn.linear_model import LinearRegression, LogisticRegression
from sklearn.metrics import accuracy_score, f1_score, mean_absolute_error, root_mean_squared_error
from sklearn.model_selection import LeaveOneOut, cross_val_predict
from sklearn.neighbors import KNeighborsClassifier, KNeighborsRegressor
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVC, SVR
from sklearn.tree import DecisionTreeClassifier, DecisionTreeRegressor

os.makedirs("models", exist_ok=True)

# 1. Chargement des données préparées (on exclut les lignes "Total" pour éviter le double comptage)
df = pd.read_csv("data/accidents_narsa_2021_2024.csv")
df_zones = df[df["Zone"] != "Total"].copy()
df_zones["Zone_Code"] = (df_zones["Zone"] == "Hors_agglomeration").astype(int)

mlflow.set_experiment("SmartRoad_Maroc_NARSA")
loo = LeaveOneOut()

# ==============================================================================
# PARTIE A : REGRESSION (Prédire Total_Accidents selon Annee et Zone_Code)
# ==============================================================================
X_reg = df_zones[["Annee", "Zone_Code"]]
y_reg = df_zones["Total_Accidents"]

modeles_reg = {
    "Regression_Lineaire": make_pipeline(StandardScaler(), LinearRegression()),
    "KNN_Regressor": make_pipeline(StandardScaler(), KNeighborsRegressor(n_neighbors=2)),
    "Arbre_Decision_Reg": DecisionTreeRegressor(max_depth=3, random_state=42),
    "Random_Forest_Reg": RandomForestRegressor(n_estimators=100, random_state=42),
    "SVR_RBF": make_pipeline(StandardScaler(), SVR(kernel="rbf", C=10000)),
}

resultats_reg = []
print("=== COMPARAISON DES MODÈLES DE RÉGRESSION (Validation Leave-One-Out) ===")
for nom, modele in modeles_reg.items():
    with mlflow.start_run(run_name=f"REG_{nom}"):
        preds = cross_val_predict(modele, X_reg, y_reg, cv=loo)
        mae = mean_absolute_error(y_reg, preds)
        rmse = root_mean_squared_error(y_reg, preds)

        mlflow.log_param("tache", "regression")
        mlflow.log_param("algorithme", nom)
        mlflow.log_metric("MAE", round(mae, 2))
        mlflow.log_metric("RMSE", round(rmse, 2))

        modele.fit(X_reg, y_reg)
        mlflow.sklearn.log_model(modele, name="model", serialization_format="cloudpickle")

        resultats_reg.append({"Modele": nom, "MAE": round(mae, 2), "RMSE": round(rmse, 2)})

# AutoML FLAML pour la régression (mlflow_logging=False pour éviter le conflit skops)
automl_reg = AutoML()
automl_reg.fit(
    X_train=X_reg,
    y_train=y_reg,
    task="regression",
    time_budget=10,
    metric="mae",
    eval_method="cv",
    n_splits=4,
    mlflow_logging=False,
    verbose=0,
)
preds_automl = automl_reg.predict(X_reg)
mae_auto = mean_absolute_error(y_reg, preds_automl)
rmse_auto = root_mean_squared_error(y_reg, preds_automl)

with mlflow.start_run(run_name="REG_FLAML_AutoML"):
    mlflow.log_param("tache", "regression")
    mlflow.log_param("algorithme", f"FLAML_{automl_reg.best_estimator}")
    mlflow.log_metric("MAE_train", round(mae_auto, 2))
    mlflow.log_metric("RMSE_train", round(rmse_auto, 2))

print(f"Meilleur estimateur FLAML (Régression) : {automl_reg.best_estimator} (MAE train: {round(mae_auto, 2)})")

df_res_reg = pd.DataFrame(resultats_reg).sort_values("MAE")
print(df_res_reg.to_string(index=False))

# Sauvegarde du meilleur modèle de régression (Régression Linéaire entraînée sur toutes les données)
meilleur_reg = make_pipeline(StandardScaler(), LinearRegression())
meilleur_reg.fit(X_reg, y_reg)
joblib.dump(meilleur_reg, "models/modele_regression_accidents.joblib")

# Projection indicative 2025
X_2025 = pd.DataFrame({"Annee": [2025, 2025], "Zone_Code": [0, 1]})
preds_2025 = meilleur_reg.predict(X_2025)
print("\nProjection indicative 2025 (Régression Linéaire) :")
print(f" - Agglomération      : {int(round(preds_2025[0]))} accidents")
print(f" - Hors agglomération : {int(round(preds_2025[1]))} accidents")

# ==============================================================================
# PARTIE B : CLASSIFICATION (Prédire la Zone à risque selon les taux de gravité)
# ==============================================================================
features_clf = ["Taux_Mortalite_Pct", "Taux_Blesses_Graves_Pct", "Victimes_Par_Accident"]
X_clf = df_zones[features_clf]
y_clf = df_zones["Zone_Code"]

modeles_clf = {
    "Regression_Logistique": make_pipeline(StandardScaler(), LogisticRegression(random_state=42)),
    "KNN_Classifier": make_pipeline(StandardScaler(), KNeighborsClassifier(n_neighbors=3)),
    "Arbre_Decision_Clf": DecisionTreeClassifier(max_depth=2, random_state=42),
    "Random_Forest_Clf": RandomForestClassifier(n_estimators=100, random_state=42),
    "SVM_RBF": make_pipeline(StandardScaler(), SVC(kernel="rbf", probability=True, random_state=42)),
}

resultats_clf = []
print("\n=== COMPARAISON DES MODÈLES DE CLASSIFICATION (Profil de Risque) ===")
for nom, modele in modeles_clf.items():
    with mlflow.start_run(run_name=f"CLF_{nom}"):
        preds = cross_val_predict(modele, X_clf, y_clf, cv=loo)
        acc = accuracy_score(y_clf, preds)
        f1 = f1_score(y_clf, preds)

        mlflow.log_param("tache", "classification")
        mlflow.log_param("algorithme", nom)
        mlflow.log_metric("Accuracy", round(acc, 4))
        mlflow.log_metric("F1_Score", round(f1, 4))

        modele.fit(X_clf, y_clf)
        mlflow.sklearn.log_model(modele, name="model", serialization_format="cloudpickle")

        resultats_clf.append({"Modele": nom, "Accuracy": round(acc, 4), "F1_Score": round(f1, 4)})

# AutoML FLAML pour la classification
automl_clf = AutoML()
automl_clf.fit(
    X_train=X_clf,
    y_train=y_clf,
    task="classification",
    time_budget=10,
    metric="accuracy",
    eval_method="cv",
    n_splits=4,
    mlflow_logging=False,
    verbose=0,
)
preds_auto_clf = automl_clf.predict(X_clf)
acc_auto = accuracy_score(y_clf, preds_auto_clf)
with mlflow.start_run(run_name="CLF_FLAML_AutoML"):
    mlflow.log_param("tache", "classification")
    mlflow.log_param("algorithme", f"FLAML_{automl_clf.best_estimator}")
    mlflow.log_metric("Accuracy_train", round(acc_auto, 4))

print(f"Meilleur estimateur FLAML (Classification) : {automl_clf.best_estimator} (Accuracy train: {round(acc_auto, 4)})")

df_res_clf = pd.DataFrame(resultats_clf).sort_values("Accuracy", ascending=False)
print(df_res_clf.to_string(index=False))

# Sauvegarde du meilleur classifieur (Random Forest) et des tableaux de benchmark
meilleur_clf = RandomForestClassifier(n_estimators=100, random_state=42)
meilleur_clf.fit(X_clf, y_clf)
joblib.dump(meilleur_clf, "models/modele_classification_risque.joblib")

df_res_reg.to_csv("models/benchmark_regression.csv", index=False)
df_res_clf.to_csv("models/benchmark_classification.csv", index=False)
print("\nModèles et benchmarks enregistrés dans models/ et suivis dans MLflow !")