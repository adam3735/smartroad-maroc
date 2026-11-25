import { useEffect, useState } from 'react';
import {
  BarChart, Bar, LineChart, Line, AreaChart, Area, XAxis, YAxis,
  CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import {
  Menu, X, LayoutDashboard, BarChart3, Scale, BrainCircuit,
  BookOpen, Home, AlertTriangle, Activity, Users, CarFront,
  ArrowRight, Download, ShieldCheck
} from 'lucide-react';
import './App.css';
const API_URL = 'http://127.0.0.1:8000';


function DrapeauMaroc({ width = 34, height = 22 }) {
  return (
    <svg width={width} height={height} viewBox="0 0 900 600" style={{ borderRadius: '4px', boxShadow: '0 1px 3px rgba(0,0,0,0.2)', flexShrink: 0 }}>
      <rect width="900" height="600" fill="#c1272d" />
      <path
        d="M450 185 L479 275 L574 275 L497 330 L526 420 L450 365 L374 420 L403 330 L326 275 L421 275 Z"
        fill="none"
        stroke="#006233"
        strokeWidth="22"
        strokeLinejoin="miter"
      />
    </svg>
  );
}

function SceneAutorouteMaroc() {
  return (
    <svg viewBox="0 0 460 270" width="100%" height="100%" preserveAspectRatio="xMidYMid slice">
      <defs>
        <linearGradient id="sky" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#dbeafe" />
          <stop offset="100%" stopColor="#f8fafc" />
        </linearGradient>
      </defs>
      <rect width="460" height="270" fill="url(#sky)" />
      <path d="M0 155 Q95 105 190 148 T380 135 L460 150 L460 180 L0 180 Z" fill="#cbd5e1" opacity="0.55" />
      <rect y="165" width="460" height="105" fill="#e2e8f0" />
      <polygon points="70,270 185,165 275,165 390,270" fill="#1e293b" />
      <polygon points="62,270 70,270 185,165 181,165" fill="#c1272d" />
      <polygon points="390,270 398,270 279,165 275,165" fill="#006233" />
      <line x1="230" y1="172" x2="230" y2="188" stroke="#ffffff" strokeWidth="3.5" />
      <line x1="230" y1="200" x2="230" y2="224" stroke="#ffffff" strokeWidth="4.5" />
      <line x1="230" y1="238" x2="230" y2="266" stroke="#ffffff" strokeWidth="5.5" />
      <rect x="115" y="92" width="6" height="75" fill="#475569" />
      <rect x="339" y="92" width="6" height="75" fill="#475569" />
      <rect x="110" y="86" width="240" height="34" rx="5" fill="#006233" />
      <text x="230" y="107" textAnchor="middle" fill="#ffffff" fontSize="12" fontWeight="700" fontFamily="sans-serif">
        SÉCURITÉ ROUTIÈRE • NARSA
      </text>
      <rect x="46" y="145" width="5" height="58" fill="#475569" />
      <polygon points="48.5,102 76,148 21,148" fill="#ffffff" stroke="#c1272d" strokeWidth="5" strokeLinejoin="round" />
      <text x="48.5" y="141" textAnchor="middle" fill="#c1272d" fontSize="22" fontWeight="900">!</text>
      <g transform="translate(185, 208)">
        <rect x="4" y="12" width="44" height="19" rx="5" fill="#c1272d" />
        <path d="M11 12 L16 3 L36 3 L41 12 Z" fill="#991b1b" />
        <rect x="17" y="5" width="18" height="6" rx="1.5" fill="#f1f5f9" />
        <circle cx="10" cy="21" r="3" fill="#fef08a" />
        <circle cx="42" cy="21" r="3" fill="#fef08a" />
        <rect x="8" y="31" width="7" height="5" rx="1.5" fill="#0f172a" />
        <rect x="37" y="31" width="7" height="5" rx="1.5" fill="#0f172a" />
      </g>
    </svg>
  );
}

function VignetteZone({ type }) {
  if (type === 'Agglomeration') {
    return (
      <svg viewBox="0 0 320 90" width="100%" height="85" style={{ borderRadius: '0.6rem', marginBottom: '0.9rem', background: '#ecfdf5' }}>
        <rect x="20" y="25" width="32" height="55" fill="#006233" opacity="0.85" rx="2" />
        <rect x="60" y="14" width="38" height="66" fill="#047857" opacity="0.75" rx="2" />
        <rect x="106" y="32" width="30" height="48" fill="#059669" opacity="0.65" rx="2" />
        <rect x="0" y="76" width="320" height="14" fill="#334155" />
        <line x1="15" y1="83" x2="305" y2="83" stroke="#ffffff" strokeWidth="2" strokeDasharray="10,10" />
        <text x="155" y="46" fill="#006233" fontWeight="800" fontSize="13.5" fontFamily="sans-serif">MILIEU URBAIN (79,8% DES ACCIDENTS)</text>
      </svg>
    );
  }
  return (
    <svg viewBox="0 0 320 90" width="100%" height="85" style={{ borderRadius: '0.6rem', marginBottom: '0.9rem', background: '#fef2f2' }}>
      <path d="M0 65 Q70 35 140 65 L140 78 L0 78 Z" fill="#fca5a5" opacity="0.55" />
      <rect x="0" y="74" width="320" height="16" fill="#1e293b" />
      <line x1="15" y1="82" x2="305" y2="82" stroke="#ffffff" strokeWidth="2.5" strokeDasharray="14,10" />
      <polygon points="34,26 52,56 16,56" fill="#ffffff" stroke="#c1272d" strokeWidth="4" />
      <text x="68" y="46" fill="#c1272d" fontWeight="800" fontSize="13.5" fontFamily="sans-serif">HORS AGGLOMÉRATION (54,5% DES TUÉS)</text>
    </svg>
  );
}

function SimulationAccident() {
  const [zoneSim, setZoneSim] = useState('Hors_agglomeration');
  const [enCours, setEnCours] = useState(true);

  const vitesse = zoneSim === 'Agglomeration' ? 50 : 110;
  const distanceFreinage = zoneSim === 'Agglomeration' ? 28 : 115;
  const energieRel = zoneSim === 'Agglomeration' ? '1.0x (Modérée)' : '4.8x (Critique)';
  const tauxMortZone = zoneSim === 'Agglomeration' ? '1,74 %' : '8,19 %';

  return (
    <div className="panel" style={{ marginBottom: '2rem', borderTop: '4px solid #c1272d' }}>
      <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', gap: '1rem', marginBottom: '1rem' }}>
        <div>
          <h2 style={{ marginBottom: '0.2rem' }}>Simulation Visuelle : Impact de la Vitesse &amp; du Milieu</h2>
          <p style={{ color: '#475569', fontSize: '0.88rem' }}>
            Illustration physique expliquant l'écart de mortalité NARSA entre Agglomération et Hors agglomération.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            className={zoneSim === 'Agglomeration' ? 'btn-green' : 'btn-outline'}
            onClick={() => { setZoneSim('Agglomeration'); setEnCours(true); }}
          >
            Agglomération (50 km/h)
          </button>
          <button
            className={zoneSim === 'Hors_agglomeration' ? 'btn-red' : 'btn-outline'}
            onClick={() => { setZoneSim('Hors_agglomeration'); setEnCours(true); }}
          >
            Hors Agglo (110 km/h)
          </button>
        </div>
      </div>

      {/* Animation SVG interactive */}
      <div style={{ background: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: '0.85rem', padding: '1rem', overflow: 'hidden' }}>
        <svg viewBox="0 0 700 170" width="100%" height="165">
          {/* Chaussée */}
          <rect x="0" y="95" width="700" height="55" fill="#1e293b" rx="6" />
          <line x1="15" y1="122" x2="685" y2="122" stroke="#ffffff" strokeWidth="3" strokeDasharray="18,14" />

          {/* Zone de freinage */}
          <rect
            x={zoneSim === 'Agglomeration' ? 360 : 210}
            y="95"
            width={zoneSim === 'Agglomeration' ? 110 : 260}
            height="55"
            fill={zoneSim === 'Agglomeration' ? '#006233' : '#c1272d'}
            opacity="0.35"
          />
          <text
            x={zoneSim === 'Agglomeration' ? 415 : 340}
            y="84"
            textAnchor="middle"
            fill={zoneSim === 'Agglomeration' ? '#006233' : '#c1272d'}
            fontSize="12"
            fontWeight="800"
          >
            Zone de freinage ({distanceFreinage} m)
          </text>

          {/* Obstacle / Véhicule arrêté */}
          <g transform="translate(485, 106)">
            <rect width="58" height="28" rx="6" fill="#64748b" />
            <rect x="10" y="5" width="32" height="18" rx="3" fill="#cbd5e1" />
            <circle cx="54" cy="7" r="3.5" fill="#ef4444" />
            <circle cx="54" cy="21" r="3.5" fill="#ef4444" />
          </g>

          {/* Véhicule en mouvement (animation CSS) */}
          <g style={{
            animation: enCours ? `${zoneSim === 'Agglomeration' ? 'simAgglo' : 'simRoute'} 3.2s infinite ease-in-out` : 'none'
          }}>
            <rect x="40" y="106" width="62" height="28" rx="6" fill={zoneSim === 'Agglomeration' ? '#006233' : '#c1272d'} />
            <rect x="56" y="110" width="28" height="20" rx="3" fill="#ffffff" opacity="0.9" />
            <circle cx="100" cy="112" r="3" fill="#fef08a" />
            <circle cx="100" cy="128" r="3" fill="#fef08a" />
          </g>

          {/* Point d'impact si Hors Agglo */}
          {zoneSim === 'Hors_agglomeration' && (
            <g transform="translate(476, 120)">
              <circle r="14" fill="#ef4444" opacity="0.35" />
              <circle r="7" fill="#c1272d" />
            </g>
          )}

          {/* Compteur HUD */}
          <rect x="16" y="14" width="235" height="44" rx="8" fill="#ffffff" stroke="#e2e8f0" />
          <text x="28" y="33" fill="#0f172a" fontSize="12" fontWeight="800">
            Vitesse : {vitesse} km/h
          </text>
          <text x="28" y="49" fill={zoneSim === 'Agglomeration' ? '#006233' : '#c1272d'} fontSize="11" fontWeight="700">
            {zoneSim === 'Agglomeration' ? 'Arrêt avant impact (Choc évité/faible)' : 'Impact résiduel à haute énergie'}
          </text>
        </svg>
      </div>

      {/* Indicateurs physiques & NARSA liés */}
      <div className="grid-3" style={{ marginTop: '1rem', marginBottom: 0 }}>
        <div style={{ background: '#f8fafc', padding: '0.85rem', borderRadius: '0.65rem', border: '1px solid #e2e8f0' }}>
          <div style={{ fontSize: '0.78rem', color: '#64748b', fontWeight: 700 }}>DISTANCE D'ARRÊT</div>
          <div style={{ fontSize: '1.25rem', fontWeight: 800 }}>{distanceFreinage} mètres</div>
        </div>
        <div style={{ background: '#f8fafc', padding: '0.85rem', borderRadius: '0.65rem', border: '1px solid #e2e8f0' }}>
          <div style={{ fontSize: '0.78rem', color: '#64748b', fontWeight: 700 }}>ÉNERGIE CINÉTIQUE (Ec = ½ m·v²)</div>
          <div style={{ fontSize: '1.25rem', fontWeight: 800 }}>{energieRel}</div>
        </div>
        <div style={{ background: '#f8fafc', padding: '0.85rem', borderRadius: '0.65rem', border: '1px solid #e2e8f0' }}>
          <div style={{ fontSize: '0.78rem', color: '#64748b', fontWeight: 700 }}>MORTALITÉ MOYENNE NARSA</div>
          <div style={{ fontSize: '1.25rem', fontWeight: 800, color: zoneSim === 'Agglomeration' ? '#006233' : '#c1272d' }}>{tauxMortZone}</div>
        </div>
      </div>
    </div>
  );
}

export default function App() {
  const [onglet, setOnglet] = useState('accueil');
  const [menuOuvert, setMenuOuvert] = useState(false);

  const [kpis, setKpis] = useState(null);
  const [stats, setStats] = useState([]);
  const [comparaison, setComparaison] = useState([]);
  const [benchmarks, setBenchmarks] = useState(null);
  const [erreurApi, setErreurApi] = useState('');

  const [filtreZone, setFiltreZone] = useState('Total');
  const [filtreAnnee, setFiltreAnnee] = useState('Toutes');

  const [anneePred, setAnneePred] = useState(2025);
  const [zonePred, setZonePred] = useState('Agglomeration');
  const [resReg, setResReg] = useState(null);

  const [tauxMort, setTauxMort] = useState(8.1);
  const [tauxGraves, setTauxGraves] = useState(13.2);
  const [victimesAcc, setVictimesAcc] = useState(1.75);
  const [resClf, setResClf] = useState(null);

  useEffect(() => {
    Promise.all([
      fetch(`${API_URL}/api/kpis`).then((r) => r.json()),
      fetch(`${API_URL}/api/statistiques`).then((r) => r.json()),
      fetch(`${API_URL}/api/comparaison-zones`).then((r) => r.json()),
      fetch(`${API_URL}/api/ml/benchmarks`).then((r) => r.json()),
    ])
      .then(([dataKpis, dataStats, dataComp, dataBench]) => {
        setKpis(dataKpis);
        setStats(dataStats);
        setComparaison(dataComp);
        setBenchmarks(dataBench);
      })
      .catch(() => {
        setErreurApi("Impossible de joindre le backend FastAPI sur http://127.0.0.1:8000. Vérifiez qu'Uvicorn est bien démarré.");
      });
  }, []);

  const naviguerVers = (id) => {
    setOnglet(id);
    setMenuOuvert(false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const exporterCSV = () => {
    if (!statsFiltrees.length) return;
    const entetes = Object.keys(statsFiltrees[0]).join(';');
    const lignes = statsFiltrees.map((row) => Object.values(row).join(';')).join('\n');
    const blob = new Blob(['\uFEFF' + entetes + '\n' + lignes], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `smartroad_narsa_${filtreZone}_${filtreAnnee}.csv`;
    a.click();
  };

  const lancerPredictionTendance = async (e) => {
    e.preventDefault();
    const rep = await fetch(`${API_URL}/api/ml/predire-tendance`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ annee: Number(anneePred), zone: zonePred }),
    });
    setResReg(await rep.json());
  };

  const lancerClassificationRisque = async (e) => {
    e.preventDefault();
    const rep = await fetch(`${API_URL}/api/ml/classifier-risque`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        taux_mortalite_pct: Number(tauxMort),
        taux_blesses_graves_pct: Number(tauxGraves),
        victimes_par_accident: Number(victimesAcc),
      }),
    });
    setResClf(await rep.json());
  };

  const statsFiltrees = stats.filter((row) => {
    const okZone = filtreZone === 'Toutes' || row.Zone === filtreZone;
    const okAnnee = filtreAnnee === 'Toutes' || row.Annee === Number(filtreAnnee);
    return okZone && okAnnee;
  });

  const resumeFiltre = statsFiltrees.reduce(
    (acc, r) => ({
      accidents: acc.accidents + r.Total_Accidents,
      tues: acc.tues + r.Tues,
      graves: acc.graves + r.Blesses_Graves,
      victimes: acc.victimes + r.Total_Victimes,
    }),
    { accidents: 0, tues: 0, graves: 0, victimes: 0 }
  );

  const dataEvolutionZones = [2021, 2022, 2023, 2024].map((an) => {
    const agg = stats.find((s) => s.Annee === an && s.Zone === 'Agglomeration');
    const hors = stats.find((s) => s.Annee === an && s.Zone === 'Hors_agglomeration');
    const tot = stats.find((s) => s.Annee === an && s.Zone === 'Total');
    return {
      Annee: an,
      Agglomeration: agg ? agg.Total_Accidents : 0,
      Hors_agglomeration: hors ? hors.Total_Accidents : 0,
      Total_Accidents: tot ? tot.Total_Accidents : 0,
      Tues_Agglo: agg ? agg.Tues : 0,
      Tues_Hors_Agglo: hors ? hors.Tues : 0,
      Total_Tues: tot ? tot.Tues : 0,
    };
  });

  const menus = [
    { id: 'accueil', label: 'Accueil', desc: 'Vue nationale & synthèses', icon: <Home size={18} /> },
    { id: 'dashboard', label: 'Tableau de bord', desc: 'Filtres interactifs & export', icon: <LayoutDashboard size={18} /> },
    { id: 'analyse', label: 'Analyse détaillée', desc: 'Ratios & indices de gravité', icon: <BarChart3 size={18} /> },
    { id: 'comparaison', label: 'Agglo vs Hors Agglo', desc: 'Comparaison & simulation', icon: <Scale size={18} /> },
    { id: 'ia', label: 'Espace IA & AutoML', desc: 'Modèles ML, FLAML & simulateurs', icon: <BrainCircuit size={18} /> },
    { id: 'docs', label: 'Projet & Documentation', desc: 'Architecture & API mobile', icon: <BookOpen size={18} /> },
  ];

  const ongletActifObj = menus.find((m) => m.id === onglet);

  return (
    <div className="layout">
      <div className="flag-strip" />

      {/* MENU TIROIR EN HAUT À GAUCHE */}
      {menuOuvert && (
        <>
          <div className="drawer-backdrop" onClick={() => setMenuOuvert(false)} />
          <aside className="drawer-panel">
            <div>
              <div className="drawer-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
                  <DrapeauMaroc width={34} height={22} />
                  <div>
                    <div style={{ fontWeight: 800, fontSize: '1.05rem' }}>
                      SmartRoad <span style={{ color: '#006233' }}>Maroc</span>
                    </div>
                    <div style={{ fontSize: '0.73rem', color: '#64748b', fontWeight: 600 }}>
                      Navigation de la plateforme
                    </div>
                  </div>
                </div>
                <button className="close-btn" onClick={() => setMenuOuvert(false)} aria-label="Fermer le menu">
                  <X size={18} />
                </button>
              </div>

              <nav className="drawer-nav">
                {menus.map((m) => (
                  <button
                    key={m.id}
                    className={`drawer-item ${onglet === m.id ? 'active' : ''}`}
                    onClick={() => naviguerVers(m.id)}
                  >
                    <div className="drawer-item-left">
                      {m.icon}
                      <div>
                        <div>{m.label}</div>
                        <div style={{ fontSize: '0.74rem', opacity: onglet === m.id ? 0.9 : 0.65, fontWeight: 500 }}>
                          {m.desc}
                        </div>
                      </div>
                    </div>
                    <ArrowRight size={15} />
                  </button>
                ))}
              </nav>
            </div>

            <div className="drawer-footer">
              <div style={{ fontWeight: 700, color: '#0f172a', marginBottom: '0.2rem' }}>Royaume du Maroc • NARSA</div>
              <div>Données ouvertes officielles 2021–2024 • PFE Data Science, IA &amp; MLOps</div>
            </div>
          </aside>
        </>
      )}

      {/* TOPBAR BLANCHE ÉPURÉE */}
      <header className="topbar">
        <div className="topbar-left">
          <button className="menu-trigger" onClick={() => setMenuOuvert(true)}>
            <Menu size={19} />
            <span>Menu</span>
          </button>

          <div className="brand-inline" onClick={() => naviguerVers('accueil')}>
            <DrapeauMaroc width={30} height={20} />
            <div className="brand-inline-title">
              SmartRoad <span>Maroc</span>
            </div>
          </div>

          {ongletActifObj && (
            <span className="current-section-badge">
              {ongletActifObj.label}
            </span>
          )}
        </div>
      </header>

      <main className="container">
        {erreurApi && <div className="notice-box">{erreurApi}</div>}

        {/* 1. ACCUEIL */}
        {onglet === 'accueil' && (
          <div>
            <div className="hero-card">
              <div className="hero-text">
                <div className="hero-eyebrow">
                  <DrapeauMaroc width={20} height={13} /> Observatoire National de la Sécurité Routière (NARSA)
                </div>
                <h1>Analyse Intelligente &amp; Prédiction du Risque Routier au Maroc</h1>
                <p>
                  Plateforme analytique complète exploitant les bilans officiels de la sécurité routière au Maroc (2021–2024).
                  Explorez l'évolution des accidents corporels, comparez la sinistralité urbaine et interurbaine,
                  et testez nos modèles de Machine Learning et AutoML.
                </p>
                <div className="btn-group">
                  <button className="btn-green" onClick={() => naviguerVers('dashboard')}>
                    <LayoutDashboard size={17} /> Ouvrir le Tableau de Bord
                  </button>
                  <button className="btn-red" onClick={() => naviguerVers('ia')}>
                    <BrainCircuit size={17} /> Espace IA &amp; Simulateurs
                  </button>
                  <button className="btn-outline" onClick={() => setMenuOuvert(true)}>
                    <Menu size={16} /> Tous les modules
                  </button>
                </div>
              </div>
              <div className="hero-visual">
                <SceneAutorouteMaroc />
              </div>
            </div>

            {kpis && (
              <div className="kpi-grid">
                <div className="kpi-box">
                  <div className="kpi-top">
                    <span className="kpi-label">Cumul Accidents (2021–2024)</span>
                    <div className="kpi-icon-red"><CarFront size={19} /></div>
                  </div>
                  <div className="kpi-number">{kpis.cumul_accidents_4ans.toLocaleString('fr-FR')}</div>
                  <span className="pill-red">+{kpis.evolution_accidents_2021_2024_pct}% entre 2021 et 2024</span>
                  <div className="kpi-subtext">Dont {kpis.accidents_2024.toLocaleString('fr-FR')} accidents enregistrés en 2024.</div>
                </div>

                <div className="kpi-box">
                  <div className="kpi-top">
                    <span className="kpi-label">Cumul Tués (2021–2024)</span>
                    <div className="kpi-icon-red"><AlertTriangle size={19} /></div>
                  </div>
                  <div className="kpi-number">{kpis.cumul_tues_4ans.toLocaleString('fr-FR')}</div>
                  <span className="pill-red">+{kpis.evolution_tues_2021_2024_pct}% entre 2021 et 2024</span>
                  <div className="kpi-subtext">Soit une moyenne de 3 757 décès par an sur la période.</div>
                </div>

                <div className="kpi-box">
                  <div className="kpi-top">
                    <span className="kpi-label">Blessés Graves (2021–2024)</span>
                    <div className="kpi-icon-green"><Users size={19} /></div>
                  </div>
                  <div className="kpi-number">{kpis.cumul_blesses_graves_4ans.toLocaleString('fr-FR')}</div>
                  <span className="pill-green">Bilan national consolidé</span>
                  <div className="kpi-subtext">Sur un total de {kpis.cumul_victimes_4ans.toLocaleString('fr-FR')} victimes corporelles.</div>
                </div>

                <div className="kpi-box">
                  <div className="kpi-top">
                    <span className="kpi-label">Taux de Mortalité 2024</span>
                    <div className="kpi-icon-green"><Activity size={19} /></div>
                  </div>
                  <div className="kpi-number">{kpis.taux_mortalite_2024_pct} %</div>
                  <span className="pill-green">En amélioration vs 3.215% en 2021</span>
                  <div className="kpi-subtext">Ratio tués / total accidents corporels en 2024.</div>
                </div>
              </div>
            )}

            <div className="grid-2">
              <div className="panel">
                <h2>Évolution des accidents : Agglomération vs Hors agglomération</h2>
                <p style={{ color: '#64748b', fontSize: '0.86rem', marginBottom: '0.9rem' }}>
                  Comparaison annuelle des volumes d'accidents corporels selon le milieu géographique (2021–2024).
                </p>
                <ResponsiveContainer width="100%" height={270}>
                  <BarChart data={dataEvolutionZones}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="Annee" stroke="#64748b" />
                    <YAxis stroke="#64748b" />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="Agglomeration" name="Agglomération (Urbain)" fill="#006233" radius={[5, 5, 0, 0]} />
                    <Bar dataKey="Hors_agglomeration" name="Hors agglomération (Route)" fill="#c1272d" radius={[5, 5, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="panel">
                <h2>Trajectoire des décès (Tués) sur le réseau routier marocain</h2>
                <p style={{ color: '#64748b', fontSize: '0.86rem', marginBottom: '0.9rem' }}>
                  Malgré 4 fois moins d'accidents, le réseau hors agglomération génère davantage de décès chaque année.
                </p>
                <ResponsiveContainer width="100%" height={270}>
                  <LineChart data={dataEvolutionZones}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="Annee" stroke="#64748b" />
                    <YAxis stroke="#64748b" />
                    <Tooltip />
                    <Legend />
                    <Line type="monotone" dataKey="Tues_Hors_Agglo" name="Tués Hors Agglomération" stroke="#c1272d" strokeWidth={3} />
                    <Line type="monotone" dataKey="Tues_Agglo" name="Tués en Agglomération" stroke="#006233" strokeWidth={3} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>

            <div className="grid-3">
              <div className="panel">
                <h3>1. Dynamique temporelle (2021–2024)</h3>
                <p style={{ color: '#475569', fontSize: '0.9rem' }}>
                  Après une légère stabilisation en 2022 (113 625 accidents), le volume national a fortement progressé en 2023 (+8,5 %) puis en 2024 (+16,2 %) pour atteindre 143 272 accidents.
                </p>
              </div>
              <div className="panel">
                <h3>2. Le paradoxe Fréquence vs Létalité</h3>
                <p style={{ color: '#475569', fontSize: '0.9rem' }}>
                  L'agglomération concentre <strong>79,8 %</strong> des accidents (394 684 sur 4 ans) mais seulement <strong>45,5 %</strong> des tués. Hors agglomération, la vitesse élevée multiplie le taux de mortalité par <strong>4,7</strong>.
                </p>
              </div>
              <div className="panel">
                <h3>3. Apport de l'IA &amp; Architecture REST</h3>
                <p style={{ color: '#475569', fontSize: '0.9rem' }}>
                  Nos modèles Scikit-learn et AutoML (FLAML), suivis via MLflow, permettent d'estimer les tendances annuelles et d'identifier automatiquement le profil de risque d'un bilan routier.
                </p>
              </div>
            </div>

            <div className="panel">
              <h2>Accès rapide aux modules de la plateforme</h2>
              <div className="grid-3" style={{ marginBottom: 0 }}>
                {menus.filter((m) => m.id !== 'accueil').map((m) => (
                  <div key={m.id} className="module-card">
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#006233', fontWeight: 700, marginBottom: '0.4rem' }}>
                        {m.icon} <span>{m.label}</span>
                      </div>
                      <p style={{ color: '#475569', fontSize: '0.88rem' }}>{m.desc}</p>
                    </div>
                    <button className="btn-outline" onClick={() => naviguerVers(m.id)}>
                      Accéder au module <ArrowRight size={15} />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* 2. TABLEAU DE BORD INTERACTIF */}
        {onglet === 'dashboard' && (
          <div>
            <div className="section-title">
              <h1>Tableau de Bord Interactif NARSA</h1>
              <p>Filtrage dynamique par milieu et par année, indicateurs recalculés en temps réel et export CSV.</p>
            </div>

            <div className="filter-bar">
              <div className="filter-controls">
                <div>
                  <label style={{ marginRight: '0.5rem', fontWeight: 700 }}>Zone :</label>
                  <select value={filtreZone} onChange={(e) => setFiltreZone(e.target.value)}>
                    <option value="Total">Total National</option>
                    <option value="Agglomeration">Agglomération (Urbain)</option>
                    <option value="Hors_agglomeration">Hors agglomération (Route)</option>
                    <option value="Toutes">Toutes les lignes (Vue complète)</option>
                  </select>
                </div>
                <div>
                  <label style={{ marginRight: '0.5rem', fontWeight: 700 }}>Année :</label>
                  <select value={filtreAnnee} onChange={(e) => setFiltreAnnee(e.target.value)}>
                    <option value="Toutes">2021 – 2024 (Toutes)</option>
                    <option value="2021">2021</option>
                    <option value="2022">2022</option>
                    <option value="2023">2023</option>
                    <option value="2024">2024</option>
                  </select>
                </div>
              </div>

              <button className="btn-green" onClick={exporterCSV}>
                <Download size={16} /> Exporter la sélection (CSV)
              </button>
            </div>

            <div className="kpi-grid">
              <div className="kpi-box">
                <div className="kpi-top"><span className="kpi-label">Accidents (Sélection)</span></div>
                <div className="kpi-number">{resumeFiltre.accidents.toLocaleString('fr-FR')}</div>
                <span className="pill-green">Périmètre : {filtreZone} • {filtreAnnee}</span>
              </div>
              <div className="kpi-box">
                <div className="kpi-top"><span className="kpi-label">Tués (Sélection)</span></div>
                <div className="kpi-number" style={{ color: '#c1272d' }}>{resumeFiltre.tues.toLocaleString('fr-FR')}</div>
                <span className="pill-red">Décès enregistrés</span>
              </div>
              <div className="kpi-box">
                <div className="kpi-top"><span className="kpi-label">Blessés Graves</span></div>
                <div className="kpi-number">{resumeFiltre.graves.toLocaleString('fr-FR')}</div>
                <span className="pill-green">Hospitalisations lourdes</span>
              </div>
              <div className="kpi-box">
                <div className="kpi-top"><span className="kpi-label">Total Victimes</span></div>
                <div className="kpi-number">{resumeFiltre.victimes.toLocaleString('fr-FR')}</div>
                <span className="pill-green">Tués + Blessés graves &amp; légers</span>
              </div>
            </div>

            <div className="grid-2">
              <div className="panel">
                <h2>Volume des Accidents et Victimes ({filtreZone})</h2>
                <ResponsiveContainer width="100%" height={280}>
                  <BarChart data={statsFiltrees}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="Annee" stroke="#64748b" />
                    <YAxis stroke="#64748b" />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="Total_Accidents" name="Total Accidents" fill="#006233" radius={[5, 5, 0, 0]} />
                    <Bar dataKey="Total_Victimes" name="Total Victimes" fill="#c1272d" radius={[5, 5, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="panel">
                <h2>Répartition Blessés Graves vs Tués ({filtreZone})</h2>
                <ResponsiveContainer width="100%" height={280}>
                  <AreaChart data={statsFiltrees}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="Annee" stroke="#64748b" />
                    <YAxis stroke="#64748b" />
                    <Tooltip />
                    <Legend />
                    <Area type="monotone" dataKey="Blesses_Graves" name="Blessés Graves" stroke="#006233" fill="#bbf7d0" />
                    <Area type="monotone" dataKey="Tues" name="Tués" stroke="#c1272d" fill="#fecaca" />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>

            <div className="panel">
              <h2>Table détaillée des statistiques officielles NARSA</h2>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Année</th>
                      <th>Zone</th>
                      <th>Accidents</th>
                      <th>Tués</th>
                      <th>Blessés Graves</th>
                      <th>Blessés Légers</th>
                      <th>Total Victimes</th>
                      <th>Taux Mortalité</th>
                      <th>Indice Gravité</th>
                    </tr>
                  </thead>
                  <tbody>
                    {statsFiltrees.map((r, i) => (
                      <tr key={i}>
                        <td><strong>{r.Annee}</strong></td>
                        <td>{r.Zone}</td>
                        <td>{r.Total_Accidents.toLocaleString('fr-FR')}</td>
                        <td style={{ color: '#c1272d', fontWeight: 700 }}>{r.Tues.toLocaleString('fr-FR')}</td>
                        <td>{r.Blesses_Graves.toLocaleString('fr-FR')}</td>
                        <td>{r.Blesses_Legers.toLocaleString('fr-FR')}</td>
                        <td>{r.Total_Victimes.toLocaleString('fr-FR')}</td>
                        <td><span className="pill-red">{r.Taux_Mortalite_Pct} %</span></td>
                        <td><span className="pill-green">{r.Indice_Gravite_Pct} %</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* 3. ANALYSE DÉTAILLÉE & RATIOS */}
        {onglet === 'analyse' && (
          <div>
            <div className="section-title">
              <h1>Analyse Exploratoire &amp; Indicateurs de Gravité</h1>
              <p>Étude approfondie des ratios normalisés : létalité, traumatismes graves et nombre moyen de victimes par accident.</p>
            </div>

            <div className="grid-3">
              <div className="panel">
                <h3>Taux de Mortalité (%)</h3>
                <p style={{ color: '#475569', fontSize: '0.88rem' }}>
                  <strong>Formule :</strong> (Tués / Total Accidents) × 100.<br />
                  Mesure la létalité directe d'un accident. Il baisse régulièrement au niveau national, passant de <strong>3,215 %</strong> (2021) à <strong>2,809 %</strong> (2024).
                </p>
              </div>
              <div className="panel">
                <h3>Indice de Gravité (%)</h3>
                <p style={{ color: '#475569', fontSize: '0.88rem' }}>
                  <strong>Formule :</strong> ((Tués + Blessés Graves) / Total Victimes) × 100.<br />
                  Évalue la proportion de victimes ayant subi un dommage vital ou irréversible parmi l'ensemble des blessés.
                </p>
              </div>
              <div className="panel">
                <h3>Victimes par Accident</h3>
                <p style={{ color: '#475569', fontSize: '0.88rem' }}>
                  <strong>Formule :</strong> Total Victimes / Total Accidents.<br />
                  Très stable autour de <strong>1,40</strong> en ville (chocs à faible vitesse) contre <strong>1,75</strong> hors agglomération (collisions frontales ou multi-occupants).
                </p>
              </div>
            </div>

            <div className="grid-2">
              <div className="panel">
                <h2>Évolution du Taux de Mortalité (%) par Zone</h2>
                <ResponsiveContainer width="100%" height={290}>
                  <LineChart data={stats.filter((s) => s.Zone !== 'Total')}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="Annee" allowDuplicatedCategory={false} stroke="#64748b" />
                    <YAxis stroke="#64748b" />
                    <Tooltip />
                    <Legend />
                    <Line
                      data={stats.filter((s) => s.Zone === 'Agglomeration')}
                      type="monotone"
                      dataKey="Taux_Mortalite_Pct"
                      name="Agglomération (%)"
                      stroke="#006233"
                      strokeWidth={3}
                    />
                    <Line
                      data={stats.filter((s) => s.Zone === 'Hors_agglomeration')}
                      type="monotone"
                      dataKey="Taux_Mortalite_Pct"
                      name="Hors Agglomération (%)"
                      stroke="#c1272d"
                      strokeWidth={3}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              <div className="panel">
                <h2>Évolution du Taux de Blessés Graves (%) par Zone</h2>
                <ResponsiveContainer width="100%" height={290}>
                  <LineChart data={stats.filter((s) => s.Zone !== 'Total')}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="Annee" allowDuplicatedCategory={false} stroke="#64748b" />
                    <YAxis stroke="#64748b" />
                    <Tooltip />
                    <Legend />
                    <Line
                      data={stats.filter((s) => s.Zone === 'Agglomeration')}
                      type="monotone"
                      dataKey="Taux_Blesses_Graves_Pct"
                      name="Blessés Graves Agglo (%)"
                      stroke="#006233"
                      strokeWidth={3}
                    />
                    <Line
                      data={stats.filter((s) => s.Zone === 'Hors_agglomeration')}
                      type="monotone"
                      dataKey="Taux_Blesses_Graves_Pct"
                      name="Blessés Graves Hors Agglo (%)"
                      stroke="#c1272d"
                      strokeWidth={3}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>

            <div className="panel">
              <h2>Matrice complète des indicateurs calculés (2021–2024)</h2>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Année</th>
                      <th>Zone</th>
                      <th>Taux Mortalité (%)</th>
                      <th>Taux Blessés Graves (%)</th>
                      <th>Indice de Gravité (%)</th>
                      <th>Victimes / Accident</th>
                    </tr>
                  </thead>
                  <tbody>
                    {stats.map((r, i) => (
                      <tr key={i}>
                        <td><strong>{r.Annee}</strong></td>
                        <td>{r.Zone}</td>
                        <td>{r.Taux_Mortalite_Pct} %</td>
                        <td>{r.Taux_Blesses_Graves_Pct} %</td>
                        <td>{r.Indice_Gravite_Pct} %</td>
                        <td>{r.Victimes_Par_Accident}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* 4. COMPARAISON AGGLO VS HORS AGGLO + SIMULATION */}
        {onglet === 'comparaison' && (
          <div>
            <div className="section-title">
              <h1>Comparaison Structurelle : Agglomération vs Hors Agglomération</h1>
              <p>Analyse cumulée sur 4 ans (2021–2024) et simulation physique de l'impact de la vitesse.</p>
            </div>

            <SimulationAccident />

            <div className="notice-box">
              <strong>Rigueur scientifique &amp; périmètre géographique :</strong> Les jeux de données ouverts officiels de la NARSA (2021–2024) sont publiés au niveau national ventilés par milieu (Agglomération / Hors agglomération), sans découpage par région administrative. Conformément à la méthodologie scientifique, aucune donnée régionale artificielle n'a été générée.
            </div>

            <div className="grid-2">
              {comparaison.map((z, idx) => (
                <div className="panel" key={idx}>
                  <VignetteZone type={z.Zone} />
                  <h2>{z.Zone === 'Agglomeration' ? 'Milieu Urbain (Agglomération)' : 'Milieu Interurbain (Hors agglomération)'}</h2>
                  <table>
                    <tbody>
                      <tr><td>Cumul Accidents (2021–2024)</td><td><strong>{z.Total_Accidents.toLocaleString('fr-FR')}</strong></td></tr>
                      <tr><td>Cumul Tués (2021–2024)</td><td><strong>{z.Total_Tues.toLocaleString('fr-FR')}</strong></td></tr>
                      <tr><td>Cumul Blessés Graves</td><td><strong>{z.Total_Blesses_Graves.toLocaleString('fr-FR')}</strong></td></tr>
                      <tr><td>Cumul Victimes</td><td><strong>{z.Total_Victimes.toLocaleString('fr-FR')}</strong></td></tr>
                      <tr><td>Taux de mortalité moyen</td><td><span className={z.Zone === 'Agglomeration' ? 'pill-green' : 'pill-red'}>{z.Taux_Mortalite_Moyen_Pct} %</span></td></tr>
                      <tr><td>Taux moyen de blessés graves</td><td><strong>{z.Taux_Blesses_Graves_Moyen_Pct} %</strong></td></tr>
                      <tr><td>Victimes moyennes / accident</td><td><strong>{z.Victimes_Par_Accident_Moyen}</strong></td></tr>
                    </tbody>
                  </table>
                </div>
              ))}
            </div>

            <div className="panel">
              <h2>Interprétation métier pour la prévention routière au Maroc</h2>
              <div className="grid-2" style={{ marginBottom: 0 }}>
                <div>
                  <h3 style={{ color: '#006233' }}>En Agglomération (Milieu Urbain)</h3>
                  <p style={{ color: '#475569', fontSize: '0.92rem' }}>
                    La forte densité du trafic, les intersections et la cohabitation avec les deux-roues et piétons expliquent le volume élevé (394 684 accidents sur 4 ans). Toutefois, les vitesses modérées limitent la mortalité à 1,74 % en moyenne, bien que les blessés graves aient augmenté en 2024 (11 547 blessés graves).
                  </p>
                </div>
                <div>
                  <h3 style={{ color: '#c1272d' }}>Hors Agglomération (Routes Nationales &amp; Autoroutes)</h3>
                  <p style={{ color: '#475569', fontSize: '0.92rem' }}>
                    Avec 100 113 accidents sur 4 ans (20,2 % du total), le réseau interurbain enregistre 8 196 décès (54,5 % des tués du pays). L'énergie cinétique lors des chocs à haute vitesse rend chaque accident en moyenne <strong>4,7 fois plus mortel</strong> qu'en ville.
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* 5. ESPACE IA & AUTOML */}
        {onglet === 'ia' && (
          <div>
            <div className="section-title">
              <h1>Espace Intelligence Artificielle, Machine Learning &amp; AutoML (FLAML)</h1>
              <p>Comparaison rigoureuse des algorithmes Scikit-learn et AutoML (suivis via MLflow) et simulateurs de prédiction en direct.</p>
            </div>

            <div className="info-box">
              <ShieldCheck size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />
              <strong>Protocole expérimental :</strong> Compte tenu de la taille du dataset agrégé (8 observations : 4 années × 2 zones), tous les modèles sont évalués par validation croisée <strong>Leave-One-Out (LOOCV)</strong> avec standardisation préalable (<code>StandardScaler</code>). En régression, les arbres de décision ne savent pas extrapoler au-delà de 2024 : la <strong>Régression Linéaire</strong> est donc retenue pour la projection de tendance.
            </div>

            {benchmarks && (
              <div className="grid-2">
                <div className="panel">
                  <h2>Benchmark Régression (Volume annuel d'accidents)</h2>
                  <p style={{ color: '#64748b', fontSize: '0.86rem', marginBottom: '0.8rem' }}>
                    Meilleur estimateur AutoML FLAML : <strong style={{ color: '#006233' }}>{benchmarks.automl.regression_meilleur}</strong>
                  </p>
                  <table>
                    <thead>
                      <tr>
                        <th>Algorithme</th>
                        <th>MAE (Erreur Moy. Abs.)</th>
                        <th>RMSE</th>
                      </tr>
                    </thead>
                    <tbody>
                      {benchmarks.regression.map((b, i) => (
                        <tr key={i}>
                          <td><strong>{b.Modele}</strong></td>
                          <td>{b.MAE}</td>
                          <td>{b.RMSE}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <div className="panel">
                  <h2>Benchmark Classification (Profil de Risque)</h2>
                  <p style={{ color: '#64748b', fontSize: '0.86rem', marginBottom: '0.8rem' }}>
                    Meilleur estimateur AutoML FLAML : <strong style={{ color: '#c1272d' }}>{benchmarks.automl.classification_meilleur}</strong>
                  </p>
                  <table>
                    <thead>
                      <tr>
                        <th>Algorithme</th>
                        <th>Accuracy (LOOCV)</th>
                        <th>F1-Score</th>
                      </tr>
                    </thead>
                    <tbody>
                      {benchmarks.classification.map((b, i) => (
                        <tr key={i}>
                          <td><strong>{b.Modele}</strong></td>
                          <td>{(b.Accuracy * 100).toFixed(1)} %</td>
                          <td>{b.F1_Score}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            <div className="grid-2">
              <div className="panel">
                <h2>Simulateur 1 : Projection de Tendance (Régression Linéaire)</h2>
                <p style={{ color: '#64748b', fontSize: '0.86rem', marginBottom: '1rem' }}>
                  Estime le volume d'accidents pour une année future en prolongeant la tendance linéaire observée sur 2021–2024.
                </p>
                <form onSubmit={lancerPredictionTendance}>
                  <div className="field">
                    <label>Année cible (ex: 2025, 2026)</label>
                    <input type="number" min="2021" max="2035" value={anneePred} onChange={(e) => setAnneePred(e.target.value)} required />
                  </div>
                  <div className="field">
                    <label>Zone géographique</label>
                    <select value={zonePred} onChange={(e) => setZonePred(e.target.value)}>
                      <option value="Agglomeration">Agglomération</option>
                      <option value="Hors_agglomeration">Hors agglomération</option>
                    </select>
                  </div>
                  <button type="submit" className="btn-green">Calculer la projection</button>
                </form>
                {resReg && (
                  <div className="result-card">
                    <div><strong>Estimation ({resReg.annee} — {resReg.zone}) :</strong> {resReg.accidents_estimes.toLocaleString('fr-FR')} accidents</div>
                    <div style={{ fontSize: '0.82rem', color: '#475569', marginTop: '0.3rem' }}>{resReg.avertissement}</div>
                  </div>
                )}
              </div>

              <div className="panel">
                <h2>Simulateur 2 : Classification du Risque (Random Forest)</h2>
                <p style={{ color: '#64748b', fontSize: '0.86rem', marginBottom: '1rem' }}>
                  Identifie si un bilan statistique correspond à un profil urbain (modéré) ou routier interurbain (létalité élevée).
                </p>
                <form onSubmit={lancerClassificationRisque}>
                  <div className="field">
                    <label>Taux de mortalité (%) — ~1.7 (Urbain) ou ~8.2 (Route)</label>
                    <input type="number" step="0.01" value={tauxMort} onChange={(e) => setTauxMort(e.target.value)} required />
                  </div>
                  <div className="field">
                    <label>Taux de blessés graves (%) — ~8.5 (Urbain) ou ~13.0 (Route)</label>
                    <input type="number" step="0.01" value={tauxGraves} onChange={(e) => setTauxGraves(e.target.value)} required />
                  </div>
                  <div className="field">
                    <label>Victimes par accident — ~1.40 (Urbain) ou ~1.75 (Route)</label>
                    <input type="number" step="0.01" value={victimesAcc} onChange={(e) => setVictimesAcc(e.target.value)} required />
                  </div>
                  <button type="submit" className="btn-red">Classifier le profil de risque</button>
                </form>
                {resClf && (
                  <div className="result-card">
                    <div><strong>Profil détecté :</strong> {resClf.profil_predit}</div>
                    <div><strong>Niveau de risque :</strong> {resClf.niveau_risque}</div>
                    <div style={{ fontSize: '0.84rem', marginTop: '0.3rem' }}>
                      Probabilités : Agglomération ({(resClf.probabilite_agglomeration * 100).toFixed(1)}%) | Hors agglomération ({(resClf.probabilite_hors_agglomeration * 100).toFixed(1)}%)
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* 6. PROJET & DOCUMENTATION TECHNIQUE */}
        {onglet === 'docs' && (
          <div>
            <div className="section-title">
              <h1>Présentation du Projet, Architecture &amp; Documentation API</h1>
              <p>Architecture logicielle découplée permettant la réutilisation immédiate du backend par l'application mobile.</p>
            </div>

            <div className="grid-3">
              <div className="panel">
                <h3>1. Ingestion &amp; Préparation</h3>
                <p style={{ color: '#475569', fontSize: '0.88rem' }}>
                  Fusion automatisée des 4 fichiers Excel officiels de la NARSA (2021–2024), contrôle de cohérence comptable (0 valeur manquante) et calcul des ratios métier avec <strong>Pandas</strong> et <strong>NumPy</strong>.
                </p>
              </div>
              <div className="panel">
                <h3>2. Machine Learning &amp; MLOps</h3>
                <p style={{ color: '#475569', fontSize: '0.88rem' }}>
                  Benchmark de 5 algorithmes <strong>Scikit-learn</strong> et recherche automatisée via <strong>FLAML</strong>. Traçabilité complète des paramètres, métriques et artefacts dans <strong>MLflow</strong>.
                </p>
              </div>
              <div className="panel">
                <h3>3. Architecture Découplée</h3>
                <p style={{ color: '#475569', fontSize: '0.88rem' }}>
                  Séparation stricte entre le frontend <strong>React</strong> et le backend <strong>FastAPI</strong> afin que l'application mobile consomme exactement les mêmes endpoints JSON.
                </p>
              </div>
            </div>

            <div className="grid-2">
              <div className="panel">
                <h2>Contrat d'API REST (Pour l'Application Mobile)</h2>
                <p style={{ color: '#64748b', fontSize: '0.86rem', marginBottom: '0.8rem' }}>
                  URL locale : <code>http://127.0.0.1:8000</code> • Documentation interactive Swagger :{' '}
                  <a href="http://127.0.0.1:8000/docs" target="_blank" rel="noreferrer" style={{ color: '#006233', fontWeight: 700 }}>
                    http://127.0.0.1:8000/docs
                  </a>
                </p>
                <table>
                  <thead>
                    <tr>
                      <th>Méthode</th>
                      <th>Endpoint</th>
                      <th>Description</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr><td><span className="pill-green">GET</span></td><td><code>/api/kpis</code></td><td>KPIs nationaux consolidés 2021–2024</td></tr>
                    <tr><td><span className="pill-green">GET</span></td><td><code>/api/statistiques</code></td><td>Données filtrables (?annee=&amp;zone=)</td></tr>
                    <tr><td><span className="pill-green">GET</span></td><td><code>/api/comparaison-zones</code></td><td>Synthèse Agglomération vs Hors agglo</td></tr>
                    <tr><td><span className="pill-green">GET</span></td><td><code>/api/ml/benchmarks</code></td><td>Scores des modèles ML &amp; FLAML</td></tr>
                    <tr><td><span className="pill-red">POST</span></td><td><code>/api/ml/predire-tendance</code></td><td>Estimation annuelle par régression</td></tr>
                    <tr><td><span className="pill-red">POST</span></td><td><code>/api/ml/classifier-risque</code></td><td>Classification du profil de risque</td></tr>
                  </tbody>
                </table>
              </div>

              <div className="panel">
                <h2>Limites scientifiques &amp; Perspectives</h2>
                <ul style={{ paddingLeft: '1.2rem', color: '#334155', display: 'flex', flexDirection: 'column', gap: '0.7rem', fontSize: '0.92rem' }}>
                  <li><strong>Granularité des données :</strong> Les fichiers publics utilisés fournissent des bilans nationaux annuels agrégés (sans coordonnées GPS, météo ni profil conducteur).</li>
                  <li><strong>Interprétation des prédictions :</strong> Les estimations 2025+ constituent des projections de tendance macroscopique et non des prédictions d'accidents individuels.</li>
                  <li><strong>Évolution future :</strong> L'architecture modulaire permet d'intégrer immédiatement des données régionales ou mensuelles dès leur publication par la NARSA.</li>
                </ul>
              </div>
            </div>
          </div>
        )}
      </main>

      <footer className="footer">
        SmartRoad Maroc • Projet Universitaire de Fin d'Études (Data Science, IA &amp; MLOps) • Sources officielles : NARSA (2021–2024)
      </footer>
    </div>
  );
}