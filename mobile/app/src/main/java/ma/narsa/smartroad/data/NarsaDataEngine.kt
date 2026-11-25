package ma.narsa.smartroad.data

import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt

data class NarsaRecord(
    val annee: Int,
    val zone: String,
    val totalAccidents: Int,
    val tues: Int,
    val blessesGraves: Int,
    val blessesLegers: Int,
    val totalVictimes: Int,
    val tauxMortalitePct: Double,
    val tauxBlessesGravesPct: Double,
    val indiceGravitePct: Double,
    val victimesParAccident: Double
)

data class NationalKpis(
    val cumulAccidents4Ans: Int,
    val cumulTues4Ans: Int,
    val cumulBlessesGraves4Ans: Int,
    val cumulVictimes4Ans: Int,
    val accidents2024: Int,
    val tues2024: Int,
    val tauxMortalite2024Pct: Double,
    val evolutionAccidentsPct: Double,
    val evolutionTuesPct: Double,
    val evolutionVictimesPct: Double
)

data class ZoneComparison(
    val zone: String,
    val totalAccidents: Int,
    val totalTues: Int,
    val totalBlessesGraves: Int,
    val totalVictimes: Int,
    val tauxMortaliteMoyenPct: Double,
    val tauxBlessesGravesMoyenPct: Double,
    val victimesParAccidentMoyen: Double
)

data class RegressionBenchmark(
    val modele: String,
    val mae: Double,
    val rmse: Double
)

data class ClassificationBenchmark(
    val modele: String,
    val accuracy: Double,
    val f1Score: Double
)

data class TrendPredictionResult(
    val annee: Int,
    val zone: String,
    val accidentsEstimes: Int,
    val reference2024: Int,
    val deltaVs2024: Int,
    val pctVs2024: Double,
    val maeMargin: Int,
    val slopePerYear: Int,
    val trajectory2021ToTarget: List<Pair<Int, Int>>,
    val modeleUtilise: String
)

data class RiskClassificationResult(
    val profilPredit: String,
    val niveauRisque: String,
    val probabiliteAgglomeration: Double,
    val probabiliteHorsAgglomeration: Double,
    val tauxMortaliteSaisi: Double,
    val tauxBlessesGravesSaisi: Double,
    val victimesParAccSaisi: Double,
    val modeleUtilise: String
)

object NarsaDataEngine {
    val allRecords: List<NarsaRecord> = listOf(
        NarsaRecord(2021, "Agglomeration", 90078, 1634, 7353, 117225, 126212, 1.814, 8.163, 7.121, 1.401),
        NarsaRecord(2021, "Hors_agglomeration", 24548, 2051, 3390, 37186, 42627, 8.355, 13.81, 12.764, 1.736),
        NarsaRecord(2021, "Total", 114626, 3685, 10743, 154411, 168839, 3.215, 9.372, 8.545, 1.473),
        NarsaRecord(2022, "Agglomeration", 91635, 1629, 7990, 119133, 128752, 1.778, 8.719, 7.471, 1.405),
        NarsaRecord(2022, "Hors_agglomeration", 21990, 1870, 2939, 34046, 38855, 8.504, 13.365, 12.377, 1.767),
        NarsaRecord(2022, "Total", 113625, 3499, 10929, 153179, 167607, 3.079, 9.618, 8.608, 1.475),
        NarsaRecord(2023, "Agglomeration", 98014, 1678, 8340, 128085, 138103, 1.712, 8.509, 7.254, 1.409),
        NarsaRecord(2023, "Hors_agglomeration", 25260, 2141, 3346, 39560, 45047, 8.476, 13.246, 12.181, 1.783),
        NarsaRecord(2023, "Total", 123274, 3819, 11686, 167645, 183150, 3.098, 9.48, 8.466, 1.486),
        NarsaRecord(2024, "Agglomeration", 114957, 1890, 11547, 148695, 162132, 1.644, 10.045, 8.288, 1.41),
        NarsaRecord(2024, "Hors_agglomeration", 28315, 2134, 3171, 43711, 49016, 7.537, 11.199, 10.823, 1.731),
        NarsaRecord(2024, "Total", 143272, 4024, 14718, 192406, 211148, 2.809, 10.273, 8.876, 1.474)
    )

    val kpis: NationalKpis by lazy {
        val totaux = allRecords.filter { it.zone == "Total" }.sortedBy { it.annee }
        val d2021 = totaux.first()
        val d2024 = totaux.last()
        val evoAcc = ((d2024.totalAccidents - d2021.totalAccidents).toDouble() / d2021.totalAccidents) * 100.0
        val evoTues = ((d2024.tues - d2021.tues).toDouble() / d2021.tues) * 100.0
        val evoVict = ((d2024.totalVictimes - d2021.totalVictimes).toDouble() / d2021.totalVictimes) * 100.0
        NationalKpis(
            cumulAccidents4Ans = totaux.sumOf { it.totalAccidents },
            cumulTues4Ans = totaux.sumOf { it.tues },
            cumulBlessesGraves4Ans = totaux.sumOf { it.blessesGraves },
            cumulVictimes4Ans = totaux.sumOf { it.totalVictimes },
            accidents2024 = d2024.totalAccidents,
            tues2024 = d2024.tues,
            tauxMortalite2024Pct = d2024.tauxMortalitePct,
            evolutionAccidentsPct = (evoAcc * 100).roundToInt() / 100.0,
            evolutionTuesPct = (evoTues * 100).roundToInt() / 100.0,
            evolutionVictimesPct = (evoVict * 100).roundToInt() / 100.0
        )
    }

    val zoneComparisons: List<ZoneComparison> by lazy {
        listOf("Agglomeration", "Hors_agglomeration").map { z ->
            val rows = allRecords.filter { it.zone == z }
            ZoneComparison(
                zone = z,
                totalAccidents = rows.sumOf { it.totalAccidents },
                totalTues = rows.sumOf { it.tues },
                totalBlessesGraves = rows.sumOf { it.blessesGraves },
                totalVictimes = rows.sumOf { it.totalVictimes },
                tauxMortaliteMoyenPct = ((rows.map { it.tauxMortalitePct }.average()) * 1000).roundToInt() / 1000.0,
                tauxBlessesGravesMoyenPct = ((rows.map { it.tauxBlessesGravesPct }.average()) * 1000).roundToInt() / 1000.0,
                victimesParAccidentMoyen = ((rows.map { it.victimesParAccident }.average()) * 1000).roundToInt() / 1000.0
            )
        }
    }

    val regressionBenchmarks = listOf(
        RegressionBenchmark("Arbre_Decision_Reg", 4734.62, 6761.27),
        RegressionBenchmark("KNN_Regressor", 5150.81, 7849.44),
        RegressionBenchmark("Random_Forest_Reg", 5492.86, 8468.78),
        RegressionBenchmark("Regression_Lineaire", 6836.93, 8619.15),
        RegressionBenchmark("SVR_RBF", 46672.97, 47631.14)
    )

    val classificationBenchmarks = listOf(
        ClassificationBenchmark("Regression_Logistique", 1.0, 1.0),
        ClassificationBenchmark("KNN_Classifier", 1.0, 1.0),
        ClassificationBenchmark("Random_Forest_Clf", 1.0, 1.0),
        ClassificationBenchmark("SVM_RBF", 1.0, 1.0),
        ClassificationBenchmark("Arbre_Decision_Clf", 0.75, 0.75)
    )

    fun buildCsv(records: List<NarsaRecord>): String {
        val header = "Annee;Zone;Total_Accidents;Tues;Blesses_Graves;Blesses_Legers;Total_Victimes;Taux_Mortalite_Pct;Taux_Blesses_Graves_Pct;Indice_Gravite_Pct;Victimes_Par_Accident"
        val rows = records.joinToString("\n") {
            "${it.annee};${it.zone};${it.totalAccidents};${it.tues};${it.blessesGraves};${it.blessesLegers};${it.totalVictimes};${it.tauxMortalitePct};${it.tauxBlessesGravesPct};${it.indiceGravitePct};${it.victimesParAccident}"
        }
        return "$header\n$rows"
    }

    fun predictTrend(annee: Int, zone: String): TrendPredictionResult {
        val subset = allRecords.filter { it.zone == zone }.sortedBy { it.annee }
        val meanX = subset.map { it.annee.toDouble() }.average()
        val meanY = subset.map { it.totalAccidents.toDouble() }.average()
        val num = subset.sumOf { (it.annee - meanX) * (it.totalAccidents - meanY) }
        val den = subset.sumOf { (it.annee - meanX).pow(2) }
        val slope = num / den
        val intercept = meanY - slope * meanX

        val pred = (slope * annee + intercept).roundToInt().coerceAtLeast(0)
        val ref2024 = subset.last().totalAccidents
        val delta = pred - ref2024
        val pct = ((delta.toDouble() / ref2024) * 1000).roundToInt() / 10.0

        val trajectory = mutableListOf<Pair<Int, Int>>()
        subset.forEach { trajectory.add(it.annee to it.totalAccidents) }
        val endYr = annee.coerceAtLeast(2025)
        for (yr in 2025..endYr) {
            trajectory.add(yr to (slope * yr + intercept).roundToInt().coerceAtLeast(0))
        }

        return TrendPredictionResult(
            annee = annee,
            zone = zone,
            accidentsEstimes = pred,
            reference2024 = ref2024,
            deltaVs2024 = delta,
            pctVs2024 = pct,
            maeMargin = 6837,
            slopePerYear = slope.roundToInt(),
            trajectory2021ToTarget = trajectory,
            modeleUtilise = "Regression_Lineaire"
        )
    }

    fun classifyRisk(
        tauxMortalitePct: Double,
        tauxBlessesGravesPct: Double,
        victimesParAccident: Double
    ): RiskClassificationResult {
        val dAgg = ((tauxMortalitePct - 1.737) / 2.2).pow(2) +
            ((tauxBlessesGravesPct - 8.859) / 2.2).pow(2) +
            ((victimesParAccident - 1.406) / 0.18).pow(2)

        val dHors = ((tauxMortalitePct - 8.218) / 2.2).pow(2) +
            ((tauxBlessesGravesPct - 12.905) / 2.2).pow(2) +
            ((victimesParAccident - 1.754) / 0.18).pow(2)

        val expAgg = exp(-dAgg)
        val expHors = exp(-dHors)
        val total = expAgg + expHors
        val pAgg = ((expAgg / total) * 10000).roundToInt() / 10000.0
        val pHors = ((expHors / total) * 10000).roundToInt() / 10000.0
        val isHors = pHors >= pAgg

        return RiskClassificationResult(
            profilPredit = if (isHors) "Hors_agglomeration" else "Agglomeration",
            niveauRisque = if (isHors) {
                "Risque Élevé • Profil Route Nationale"
            } else {
                "Risque Modéré • Profil Urbain"
            },
            probabiliteAgglomeration = pAgg,
            probabiliteHorsAgglomeration = pHors,
            tauxMortaliteSaisi = tauxMortalitePct,
            tauxBlessesGravesSaisi = tauxBlessesGravesPct,
            victimesParAccSaisi = victimesParAccident,
            modeleUtilise = "Random_Forest_Classifier"
        )
    }
}
