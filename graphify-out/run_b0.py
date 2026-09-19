import os, json
from pathlib import Path
from graphify.cache import check_semantic_cache

gemini_set = bool(os.environ.get('GEMINI_API_KEY') or os.environ.get('GOOGLE_API_KEY'))
print("GEMINI_KEY_SET:", gemini_set)

detect = json.loads(Path('graphify-out/.graphify_detect.json').read_text(encoding="utf-8"))
all_files = [f for cat in ('document', 'paper', 'image') for f in detect['files'].get(cat, [])]

spec_path = Path.home() / '.claude' / 'skills' / 'graphify' / 'references' / 'extraction-spec.md'

cached_nodes, cached_edges, cached_hyperedges, uncached = check_semantic_cache(all_files, root='.', prompt_file=str(spec_path.resolve()))

if cached_nodes or cached_edges or cached_hyperedges:
    Path('graphify-out/.graphify_cached.json').write_text(json.dumps({'nodes': cached_nodes, 'edges': cached_edges, 'hyperedges': cached_hyperedges}, ensure_ascii=False), encoding="utf-8")
else:
    Path('graphify-out/.graphify_cached.json').unlink(missing_ok=True)
Path('graphify-out/.graphify_uncached.txt').write_text('\n'.join(uncached), encoding="utf-8")
print(f'Semantic cache: {len(all_files)-len(uncached)} hit, {len(uncached)} uncached')
print(f'SPEC_PATH: {spec_path.resolve()}')
print('Document files:', detect['files'].get('document', []))
print('Image files count:', len(detect['files'].get('image', [])))
