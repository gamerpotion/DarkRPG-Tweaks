# Upload DarkRPG Tweaks to GitHub from Ubuntu

GitHub limits browser uploads to 100 files at once. Use `git push` for the complete source.

## Prepare

Install tools: `sudo apt update && sudo apt install git gh unzip`

Extract `DarkRPG-Tweaks-1.0.0-beta.2-GitHub-Ready.zip` into a new folder, for example `~/DarkRPG-Tweaks`. Keep files such as `README.md`, `src/`, and `LICENSE` immediately inside that folder, not in an extra nested directory.

## Create a NEW repository

1. Open a terminal inside the extracted folder.
2. Set your Git identity if you have never done it before:
   - `git config --global user.name "YOUR NAME"`
   - `git config --global user.email "YOUR GITHUB EMAIL"`
3. Run:

```bash
git init -b main
git add .
git commit -m "Initial DarkRPG Tweaks source release"
gh auth login
gh repo create DarkRPG-Tweaks --public --source=. --remote=origin --push
```

Choose GitHub.com and HTTPS when prompted. The GitHub CLI will request authorisation. You can replace `--public` with `--private` if desired.

**If the repository already exists on GitHub**, don't run `gh repo create` with the same name. Instead, add the repository as a remote: `git remote add origin https://github.com/YOUR-USERNAME/DarkRPG-Tweaks.git` and run `git push -u origin main` if the repository is empty. If it already has commits, clone it first and add the source there to avoid a history conflict.

## What the repository should show

GitHub should display `README.md`, `LICENSE`, `src/`, `test-stubs/`, `docs/`, `.github/`, `build-from-source.py`, and `verify_release.py` at the top level. Source Java files are under `src/main/java` and 168 datapack JSON definitions are under `src/main/resources`.

**Build note:** the included standalone Python/JDK build uses compile-only stubs, not Fabric Loom/Gradle. This is clearly stated in the README.
