import json
from pathlib import Path

analysis = json.loads(Path('graphify-out/.graphify_analysis.json').read_text(encoding="utf-8"))
extract = json.loads(Path('graphify-out/.graphify_extract.json').read_text(encoding="utf-8"))
node_map = {n['id']: n for n in extract['nodes']}

print(f"Total communities: {len(analysis['communities'])}")
for cid, nodes in sorted(analysis['communities'].items(), key=lambda x: int(x[0])):
    node_objs = [node_map.get(nid) for nid in nodes if nid in node_map]
    labels = [n.get('label', n.get('name', n['id'])) for n in node_objs[:8]]
    files = set(n.get('source_file', '') for n in node_objs if n.get('source_file'))
    print(f"Community {cid} ({len(nodes)} nodes): {', '.join(labels[:5])} | Files: {list(files)[:3]}")
