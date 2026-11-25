from pathlib import Path
import pandas as pd

# 1. Chargement des 4 fichiers Excel
fichiers = sorted(Path("data").glob("*.xlsx"))
dfs = []

for chemin in fichiers:
    df = pd.read_excel(chemin)
    annee = int(str(df.columns[0]).split()[-1])
    df.columns = ["Indicateur", "Agglomeration", "Hors_agglomeration", "Total"]
    df["Annee"] = annee
    dfs.append(df)

brut = pd.concat(dfs, ignore_index=True)

# 2. Restructuration par (Annee, Zone) pour l'analyse et le ML
lignes = []
for annee, groupe in brut.groupby("Annee"):
    g = groupe.set_index("Indicateur")
    for zone in ["Agglomeration", "Hors_agglomeration", "Total"]:
        accidents = int(g.loc["Total Accidents", zone])
        tues = int(g.loc["Tués", zone])
        blesses_graves = int(g.loc["Blessés Graves", zone])
        blesses_legers = int(g.loc["Blessés légers", zone])
        victimes = int(g.loc["Total Victimes", zone])

        lignes.append({
            "Annee": int(annee),
            "Zone": zone,
            "Total_Accidents": accidents,
            "Tues": tues,
            "Blesses_Graves": blesses_graves,
            "Blesses_Legers": blesses_legers,
            "Total_Victimes": victimes,
            "Taux_Mortalite_Pct": round((tues / accidents) * 100, 3),
            "Taux_Blesses_Graves_Pct": round((blesses_graves / accidents) * 100, 3),
            "Indice_Gravite_Pct": round(((tues + blesses_graves) / victimes) * 100, 3),
            "Victimes_Par_Accident": round(victimes / accidents, 3),
        })

dataset = pd.DataFrame(lignes).sort_values(["Annee", "Zone"]).reset_index(drop=True)

# 3. Sauvegarde du dataset propre
dataset.to_csv("data/accidents_narsa_2021_2024.csv", index=False, encoding="utf-8-sig")
print("Dataset sauvegardé dans data/accidents_narsa_2021_2024.csv :\n")
print(dataset.to_string())