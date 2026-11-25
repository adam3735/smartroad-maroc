from pathlib import Path
import pandas as pd

# Recherche automatique de tous les fichiers .xlsx dans le projet
fichiers = sorted(Path(".").rglob("*.xlsx"))
print("Fichiers trouvés :", fichiers, "\n")

dfs = []
for chemin in fichiers:
    df = pd.read_excel(chemin)
    # On récupère l'année depuis la 1ère colonne (ex: "Année 2021" -> 2021)
    annee = int(str(df.columns[0]).split()[-1])
    df.columns = ["Indicateur", "Agglomeration", "Hors_agglomeration", "Total"]
    df["Annee"] = annee
    dfs.append(df)

data = pd.concat(dfs, ignore_index=True)
print(data)
print("\nTypes des colonnes :\n", data.dtypes)
print("\nValeurs manquantes :\n", data.isnull().sum())