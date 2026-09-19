import json, io, sys
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

with open('.codex-spreadsheet-casing-audit/resolved_rows_gtceu_reuse.json', 'r', encoding='utf-8') as f:
    rows = json.load(f)

mtf_rows = [r for r in rows if r.get('machine') == 'MegaTreeFarm']

for i, r in enumerate(mtf_rows):
    print(f"=== Row {i+1}: Symbol {r.get('symbol')} ===")
    for k, v in r.items():
        if k != 'textureFiles':
            print(f"  {k}: {v}")
    if 'textureFiles' in r:
        print(f"  textureFiles count: {len(r['textureFiles'])}")
