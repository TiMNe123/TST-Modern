import json, io, sys
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

with open('.codex-spreadsheet-casing-audit/resolved_rows_gtceu_reuse.json', 'r', encoding='utf-8') as f:
    rows = json.load(f)

for r in rows:
    if 'MegaTreeFarm' in r.get('machine', ''):
        print(f"Symbol: {r.get('symbol')} | Expression: {r.get('expression')} | Status: {r.get('status')} | Note: {r.get('note')}")
