#!/usr/bin/env python3
"""Self-contained source rebuild: JDK17+, stdlib Python only.
   All upstream API stubs are compile-time-only. Verify runtime in Minecraft."""
from pathlib import Path
import shutil,subprocess,zipfile,sys
root=Path(__file__).resolve().parent
proj=root
prod=proj/'src/main/java'
stubs=proj/'test-stubs'
resources=proj/'src/main/resources'
classes=root/'rebuild-classes'
out=root/'darkrpg-tweaks-fabric-1.20.1-1.0.0-beta.2.jar'
if classes.exists():shutil.rmtree(classes)
classes.mkdir()
files=[str(x) for x in stubs.rglob('*.java')]+[str(x) for x in prod.rglob('*.java')]
subprocess.run(['javac','--release','17','-encoding','UTF-8','-g:none','-d',str(classes),*files],check=True)
with zipfile.ZipFile(out,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=8) as z:
 for src in sorted(prod.rglob('*.java')):
  name=src.relative_to(prod).as_posix()[:-5]
  for cl in sorted(classes.glob(name+'*.class')):
   z.write(cl,cl.relative_to(classes).as_posix())
 for res in sorted(resources.rglob('*')):
  if res.is_file():z.write(res,res.relative_to(resources).as_posix())
with zipfile.ZipFile(out) as z:
 assert z.testzip() is None
 assert len([x for x in z.namelist() if x.startswith('resourcepacks/balanced_overrides/data/') and x.endswith('.json')])==168
print('Built',out)
