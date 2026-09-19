import sys, json, re
from collections import Counter
from pathlib import Path
from datetime import datetime, timezone
import graphify.export as exp
from graphify.build import build_from_json
from graphify.cluster import score_all
from graphify.analyze import god_nodes, surprising_connections, suggest_questions
from graphify.report import generate
from graphify.export import to_json
from graphify.detect import save_manifest
from graphify.cli import _stamped_manifest_files

extraction = json.loads(Path('graphify-out/.graphify_extract.json').read_text(encoding="utf-8"))
detection  = json.loads(Path('graphify-out/.graphify_detect.json').read_text(encoding="utf-8"))
analysis   = json.loads(Path('graphify-out/.graphify_analysis.json').read_text(encoding="utf-8"))

node_map = {n['id']: n for n in extraction['nodes']}
communities = {int(k): v for k, v in analysis['communities'].items()}

# Heuristic namer for communities
labels = {}
for cid, nodes in communities.items():
    node_names = []
    for nid in nodes:
        if nid in node_map:
            n = node_map[nid]
            label = n.get('label') or n.get('name') or nid
            node_names.append(label)
        else:
            parts = nid.split('_')
            node_names.append(parts[-1])

    combined = ' '.join(node_names).lower()

    if any('recipe' in n.lower() for n in node_names) and len(nodes) > 40:
        label = "Machine Recipe Systems"
    elif 'disassembler' in combined:
        label = "Disassembler Multiblock System"
    elif 'incompactcyclotron' in combined or 'cyclotron' in combined:
        label = "Incompact Cyclotron Multiblock"
    elif 'bigbroarray' in combined or 'big_bro' in combined:
        label = "Big Bro Array Multiblock"
    elif 'starcoreminer' in combined or 'starcore' in combined:
        label = "Starcore Miner Multiblock"
    elif 'massfabricator' in combined:
        label = "Mass Fabricator Multiblock"
    elif 'meganaquadahreactor' in combined or 'naquadah' in combined:
        label = "Naquadah Reactor System"
    elif 'draconiccrucible' in combined or 'draconic' in combined:
        label = "Draconic Crucible Multiblock"
    elif 'galacticarmillary' in combined or 'armillary' in combined:
        label = "Galactic Armillary Multiblock"
    elif 'astralcomputing' in combined:
        label = "Astral Computing Array"
    elif 'oreprocessing' in combined:
        label = "Ore Processing Factory"
    elif 'largeneutron' in combined or 'oscillator' in combined:
        label = "Large Neutron Oscillator"
    elif 'megatreefarm' in combined or 'treefarm' in combined:
        label = "Mega Tree Farm"
    elif 'megastonebreaker' in combined or 'stonebreaker' in combined:
        label = "Mega Stone Breaker"
    elif 'netherinterface' in combined:
        label = "Nether Interface Multiblock"
    elif 'vacuumdrying' in combined or 'dryingfurnace' in combined:
        label = "Vacuum Drying Furnace"
    elif 'hyperthermal' in combined or 'convector' in combined:
        label = "Hyper Thermal Convector"
    elif 'tstblocks' in combined or 'block' in combined:
        label = "Block Registry & Casings"
    elif 'tstitems' in combined or 'item' in combined:
        label = "Item Registry & Components"
    elif 'tstmaterials' in combined or 'material' in combined:
        label = "Material Registry & Elements"
    elif 'tstrecipetypes' in combined:
        label = "Recipe Type Registry"
    elif 'tstcreativemodetabs' in combined:
        label = "Creative Tab Registration"
    elif 'client' in combined or 'renderer' in combined:
        label = "Client Rendering & Models"
    elif 'jei' in combined:
        label = "JEI Recipe Integration"
    elif 'config' in combined:
        label = "Mod Configuration"
    elif 'data' in combined or 'datagen' in combined:
        label = "Data Generation Providers"
    else:
        words = [w for w in re.findall(r'[a-zA-Z]{3,}', combined) if w not in ('src', 'main', 'java', 'com', 'tstmodern', 'net', 'minecraft', 'gregtechceu')]
        top_words = [w for w, _ in Counter(words).most_common(2)]
        if top_words:
            label = " ".join(top_words).title() + " Component"
        else:
            label = f"Subsystem {cid}"

    labels[cid] = label

counts = Counter(labels.values())
for cid, name in list(labels.items()):
    if counts[name] > 1:
        labels[cid] = f"{name} ({cid})"

G = build_from_json(extraction, root='.', directed=False)
cohesion = {int(k): v for k, v in analysis['cohesion'].items()}
tokens = {'input': extraction.get('input_tokens', 0), 'output': extraction.get('output_tokens', 0)}

questions = suggest_questions(G, communities, labels)

report = generate(G, communities, cohesion, labels, analysis['gods'], analysis['surprises'], detection, tokens, '.', suggested_questions=questions)
Path('graphify-out/GRAPH_REPORT.md').write_text(report, encoding="utf-8")
Path('graphify-out/.graphify_labels.json').write_text(json.dumps({str(k): v for k, v in labels.items()}, ensure_ascii=False), encoding="utf-8")

wrote = to_json(G, communities, 'graphify-out/graph.json', community_labels=labels)
print(f"Report updated with {len(labels)} community labels. to_json wrote={wrote}")

# Step 6 - HTML export
try:
    if hasattr(exp, 'to_html'):
        exp.to_html(G, communities, 'graphify-out/graph.html', community_labels=labels)
        print("HTML exported via exp.to_html")
    elif hasattr(exp, 'export_html'):
        exp.export_html(G, communities, 'graphify-out/graph.html', community_labels=labels)
        print("HTML exported via exp.export_html")
    else:
        # Check CLI export
        print("Export symbols in exp:", [s for s in dir(exp) if not s.startswith('_')])
except Exception as e:
    print(f"HTML export error: {e}")

# Step 9 - Manifest and Cost
_corpus = detection.get('all_files') or detection['files']
_manifest_files = _stamped_manifest_files(_corpus, extraction, Path('.'))
_sem_types = ('document', 'paper', 'image')
_dispatched = {f for t, fl in detection['files'].items() if t in _sem_types for f in fl}
_stamped = {f for fl in _manifest_files.values() for f in fl}
_cleared = _dispatched - _stamped
_scan = {f for fl in _corpus.values() for f in fl}
save_manifest(_manifest_files, root='.', scan_corpus=_scan, clear_semantic=_cleared or None)

cost_path = Path('graphify-out/cost.json')
if cost_path.exists():
    cost = json.loads(cost_path.read_text(encoding="utf-8"))
else:
    cost = {'runs': [], 'total_input_tokens': 0, 'total_output_tokens': 0}

cost['runs'].append({
    'date': datetime.now(timezone.utc).isoformat(),
    'input_tokens': 0,
    'output_tokens': 0,
    'files': detection.get('total_files', 0),
})
cost_path.write_text(json.dumps(cost, indent=2, ensure_ascii=False), encoding="utf-8")
print("Manifest and cost saved successfully.")
