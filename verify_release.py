#!/usr/bin/env python3
"""Portable static verification of the locally built DarkRPG Tweaks JAR."""
from pathlib import Path
from zipfile import ZipFile
import hashlib
import json
import subprocess

ROOT = Path(__file__).resolve().parent
JAR = ROOT / "darkrpg-tweaks-fabric-1.20.1-1.0.0-beta.2.jar"
RESOURCES = ROOT / "src/main/resources"
assert JAR.is_file(), f"Missing {JAR.name}; run python3 build-from-source.py first"

with ZipFile(JAR) as archive:
    assert archive.testzip() is None, "ZIP integrity failure"
    names = set(archive.namelist())
    assert not any("darkunbug" in name.lower() or "allow-editing-player-data" in name for name in names)
    assert not any(name.startswith("net/minecraft/") or name.startswith("stubs/") for name in names)

    metadata = json.loads(archive.read("fabric.mod.json"))
    assert (metadata["id"], metadata["name"], metadata["version"]) == (
        "darkrpg_tweaks", "DarkRPG Tweaks", "1.0.0-beta.2"
    )
    assert len(metadata["mixins"]) == 4
    assert "main" in metadata["entrypoints"]
    assert "client" in metadata["entrypoints"]

    prefix = "resourcepacks/balanced_overrides/data/"
    souls = sorted(n for n in names if n.startswith(prefix + "soulsweapons/spell_assignments/") and n.endswith(".json"))
    tier = sorted(n for n in names if n.startswith(prefix + "tiered/item_attributes/") and n.endswith(".json"))
    assert len(souls) == 24, f"Expected 24 Soulslike assignments, got {len(souls)}"
    assert len(tier) == 144, f"Expected 144 TieredZ modifiers, got {len(tier)}"

    for name in souls:
        assert json.loads(archive.read(name)) == {"spell_container": {}}, name

    def modifier_values(obj):
        if isinstance(obj, dict):
            for k, v in obj.items():
                if k == "value" and isinstance(v, (float, int)):
                    yield v
                yield from modifier_values(v)
        elif isinstance(obj, list):
            for v in obj:
                yield from modifier_values(v)

    assert all(value >= 0 for name in tier for value in modifier_values(json.loads(archive.read(name))))


    from collections import defaultdict, Counter
    weights_by_category = defaultdict(lambda: defaultdict(list))
    expected_weights = {"common":30,"uncommon":30,"rare":25,"epic":10,"legendary":4,"unique":1}
    for name in tier:
        entry=json.loads(archive.read(name))
        rarity=entry["id"].split(":")[-1].split("_")[0]
        assert rarity in expected_weights, (name, rarity)
        assert entry["weight"]==expected_weights[rarity], (name,entry["weight"])
        category=name.split("/")[-2]
        weights_by_category[category][rarity].append(entry["weight"])
        for attr in entry.get("attributes", []):
            amount=attr.get("modifier",{}).get("amount")
            assert amount is None or (isinstance(amount,(int,float)) and amount>=0), (name,amount)
    assert len(weights_by_category)==7
    for cat, dist in weights_by_category.items():
        assert set(dist)==set(expected_weights), (cat,dist)
        assert len({len(weights) for weights in dist.values()})==1, (cat,dist)

    for config_file in metadata["mixins"]:
        config = json.loads(archive.read(config_file))
        package = config["package"].replace(".", "/")
        assert package.startswith("darkrpg/tweaks/")
        assert config["plugin"].replace(".", "/") + ".class" in names
        for mixin in config.get("mixins", []) + config.get("client", []):
            assert f"{package}/{mixin}.class" in names, (config_file, mixin)

    for file in RESOURCES.rglob("*"):
        if file.is_file():
            rel = file.relative_to(RESOURCES).as_posix()
            assert rel in names, f"Resource not packaged: {rel}"
            assert archive.read(rel) == file.read_bytes(), f"Resource mismatch: {rel}"

    for name in names:
        if name.endswith(".class"):
            bytecode = archive.read(name)
            assert b"me/proferk/alloweditingplayerdata" not in bytecode
            assert b"allow-editing-player-data" not in bytecode
            assert b"darkunbug" not in bytecode.lower()

    mixin = "darkrpg/tweaks/compat/pvp/mixin/PvPReflectionLoopGuardMixin.class"
    assert b"more_rpg_classes$damageReflect$damage" in archive.read(mixin)

for class_name in (
    "darkrpg.tweaks.DarkRpgTweaks",
    "darkrpg.tweaks.compat.pvp.MoreRpgReflectionMixinPlugin",
):
    proc = subprocess.run(
        ["javap", "-v", "-classpath", str(JAR), class_name],
        capture_output=True, text=True, check=True
    )
    lines = [line for line in proc.stdout.splitlines() if "net/fabricmc/loader/api/FabricLoader.getInstance" in line]
    assert lines, class_name
    assert all("InterfaceMethodref" in line or "InterfaceMethod" in line or "invokestatic" in line for line in lines), lines
    assert any("InterfaceMethodref" in line for line in lines), lines

print("PASS: archive, metadata, resource completeness, all 168 data definitions and exact 30/30/25/10/4/1 TieredZ weights")
print("PASS: mixin configs, no original HP repair, no test stubs in JAR")
print("PASS: FabricLoader.getInstance has interface-method references")
print("SHA-256:", hashlib.sha256(JAR.read_bytes()).hexdigest())
