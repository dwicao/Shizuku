# Shizuku (dwicao fork)

**Shizuku's fork, with [Stealth Mode](#-stealth-mode) — hide Shizuku from other apps.** This is [dwicao/Shizuku](https://github.com/dwicao/Shizuku), a fork of [thedjchi/Shizuku](https://github.com/thedjchi/Shizuku) (which is itself a fork of [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku)). It carries over everything from the upstream forks and adds further development on top of the stealth feature.

An Android app that allows other apps to use system-level APIs that require ADB/root privileges.

[![Stars](https://img.shields.io/github/stars/dwicao/Shizuku?style=for-the-badge&color=bfb330&labelColor=807820&label=Stars&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHRpdGxlPnN0YXI8L3RpdGxlPjxwYXRoIGQ9Ik0xMiwxNy4yN0wxOC4xOCwyMUwxNi41NCwxMy45N0wyMiw5LjI0TDE0LjgxLDguNjJMMTIsMkw5LjE5LDguNjJMMiw5LjI0TDcuNDUsMTMuOTdMNS44MiwyMUwxMiwxNy4yN1oiIGZpbGw9IndoaXRlIiAvPjwvc3ZnPg==)](https://github.com/dwicao/Shizuku/stargazers)
[![Downloads](https://img.shields.io/github/downloads/dwicao/Shizuku/total?style=for-the-badge&color=bf7830&labelColor=805020&label=Downloads&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHRpdGxlPmRvd25sb2FkPC90aXRsZT48cGF0aCBkPSJNNSwyMEgxOVYxOEg1TTE5LDlIMTVWM0g5VjlINUwxMiwxNkwxOSw5WiIgZmlsbD0id2hpdGUiIC8+PC9zdmc+)](https://github.com/dwicao/Shizuku/releases)
[![Latest Release](https://img.shields.io/github/v/release/dwicao/Shizuku?style=for-the-badge&color=3060bf&labelColor=204080&label=Latest%20Release&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMjRweCIgdmlld0JveD0iMCAtOTYwIDk2MCA5NjAiIHdpZHRoPSIyNHB4IiBmaWxsPSIjZTNlM2UzIj48cGF0aCBkPSJNNDQwLTgycS03Ni04LTE0MS41LTQxLjV0LTExNC04N1ExMzYtMjY0IDEwOC0zMzNUODAtNDgwcTAtOTEgMzYuNS0xNjhUMjE2LTc4MGgtOTZ2LTgwaDI0MHYyNDBoLTgwdi0xMDlxLTU1IDQ0LTg3LjUgMTA4LjVUMTYwLTQ4MHEwIDEyMyA4MC41IDIxMi41VDQ0MC0xNjN2ODFabS0xNy0yMTRMMjU0LTQ2Nmw1Ni01NiAxMTMgMTEzIDIyNy0yMjcgNTYgNTctMjgzIDI4M1ptMTc3IDE5NnYtMjQwaDgwdjEwOXE1NS00NSA4Ny41LTEwOVQ4MDAtNDgwcTAtMTIzLTgwLjUtMjEyLjVUNTIwLTc5N3YtODFxMTUyIDE1IDI1NiAxMjh0MTA0IDI3MHEwIDkxLTM2LjUgMTY4VDc0NC0xODBoOTZ2ODBINjAwWiIvPjwvc3ZnPg==)](https://github.com/dwicao/Shizuku/releases/latest)
[![Issues](https://img.shields.io/github/issues-search/dwicao/Shizuku?query=state%3Aopen&style=for-the-badge&color=bf3030&labelColor=802020&label=Open%20Issues)](https://github.com/dwicao/Shizuku/issues)
[![Buy Me A Coffee](https://img.shields.io/badge/Buy%20Me%20A%20Coffee-bfb330?style=for-the-badge&logo=buymeacoffee&logoColor=ffffff)](https://www.buymeacoffee.com/thedjchi)

## ⚠️ Fork Notice & Attribution

This repository is a **fork of a fork**:

| | Repository | Maintainer | Notes |
|---|---|---|---|
| Original | [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku) | RikkaW (RikkaApps) | The original Shizuku project |
| Upstream fork | [thedjchi/Shizuku](https://github.com/thedjchi/Shizuku) | thedjchi | Added the features below; upstream maintains a pause notice on this project |
| **This fork** | **[dwicao/Shizuku](https://github.com/dwicao/Shizuku)** | **dwicao** | Stealth by default (`moe.dwicao.shizuku`); in-app updater driven by this repo's `version.json` |

All credit for the original app and the upstream added features goes to RikkaApps and thedjchi. If you were looking for the upstream version, use [thedjchi/Shizuku](https://github.com/thedjchi/Shizuku) instead.

## ⬇️ Download

Get the latest release from [GitHub Releases](https://github.com/dwicao/Shizuku/releases/latest).

> **Note — one-time identity change:** starting with v13.9.0, this fork uses its own package name (`moe.dwicao.shizuku`) instead of `moe.shizuku.privileged.api`. The v13.8.0 and earlier builds **cannot be updated in place** — uninstall them first, then install the new build and pair it again. Updating *within* the app works normally from then on (see [🥷 Stealth Mode](#-stealth-mode)).

## ✨ What's in this fork

The headline feature of this fork is **Stealth mode** — see [🥷 Stealth Mode](#-stealth-mode) for the full guide. On top of that, it includes everything the upstream fork added over the original:

* **🥷 Stealth by default:** this fork uses its own package name (`moe.dwicao.shizuku`) instead of the original one, so apps that detect Shizuku at the original package name simply can't see it — see [🥷 Stealth Mode](#-stealth-mode) for the full guide
* **More robust "start on boot":** waits for a Wi-Fi connection before starting the Shizuku service
* **TCP mode:** (i.e., the `adb tcpip` command) once Shizuku successfully starts with Wi-Fi after a reboot, you can stop/restart Shizuku without a Wi-Fi connection!
* **Watchdog service:** automatically restarts Shizuku if it stops unexpectedly, and can alert you of crashes/potential fixes
* **Start/stop intents:** toggle Shizuku on-demand using automation apps (e.g., Tasker, MacroDroid, Automate)
* **[BETA] In-app updates:** option to automatically check for new updates, and can automatically download/install the latest version from this fork (update detection is driven by [`version.json`](version.json) in this repository)
* **Android/Google TV and VR headset support:** UI is now compatible with D-Pad remotes, all TVs are supported (including Android 14+ TVs that require pairing), and the multi-window pairing dialog is toggleable in settings for VR headsets
* **MediaTek support:** fixes a critical bug in the original v13.6.0 which prevented Shizuku from working on MediaTek devices

## 🥷 Stealth Mode

Some apps refuse to work — or even launch — when they detect that Shizuku is installed. This fork is **hidden by default**: it uses its own package name (`moe.dwicao.shizuku`) instead of the original `moe.shizuku.privileged.api`, so the usual "is Shizuku installed?" checks at the original package name come up empty. No setup required — Shizuku itself works exactly as before.

### Compatibility: installing the stub

Apps that rely on Shizuku may stop working, since they look for it at the original package name. To make Shizuku visible to those apps, install the **compatibility stub**:

1. Open the **"Compatibility mode"** card on the home screen.
2. Tap **Install stub** — Shizuku installs a lightweight stub with the original package name that forwards everything to this app: launcher entry, permission requests, binder requests, and the `shizuku` provider.
3. Use the apps that require Shizuku as usual.
4. When you're done, tap **Uninstall stub** to hide Shizuku again.

You can also tap the compatibility card on that screen to copy a code snippet you can send to the developer of an affected app, so their "Shizuku installed" check works with this hidden build.

### Things to know

* Apps that were allowed to use Shizuku keep their permission — it is restored automatically when the service starts.
* **rish** users: re-export the files.
* **Automation intent** users: intents use this build's package name (`moe.dwicao.shizuku`).
* **Updates:** updates are installed from within the app (or by installing the release APK manually — same package, same signature). Update detection uses [`version.json`](version.json) in this repository instead of the GitHub Releases API.
* Some apps may still be able to detect this build of Shizuku.
* Your Shizuku authorizations are kept — permissions you granted to apps are restored automatically when the service starts.

## 📝 User Guide

This fork does not maintain its own wiki. Please read the [wiki on the upstream repository (thedjchi/Shizuku)](https://github.com/thedjchi/Shizuku/wiki) for setup, info, and troubleshooting steps — it applies to this fork as well, with the exception of the stealth mode notes above.

## ☑️ Requirements

**Minimum Version: Android 7+**
- **Root mode:** Requires a rooted device
- **Wireless Debugging mode:** Works on Android 11+ and all Android TVs
- **PC mode:** Works on all devices
- **Start on boot:** Available only when using Wireless Debugging or Root mode

## 🔒 Privacy

Shizuku takes user privacy very seriously.

* No tracking or analytics
* No telemetry
* No proprietary libraries
* No Google Play Services
* Open-source codebase
* Reproducible builds
* Internet access is only used for wireless debugging connections and to fetch updates from GitHub
* Only required permissions are declared

### Permissions

* **INTERNET:** required for the wireless debugging start mode to work. Also used to fetch updates from this fork's GitHub releases
* **ACCESS_NETWORK_STATE:** used to determine when Wi-Fi is available for background start via wireless debugging
* **POST_NOTIFICATIONS:** required for pairing notification and other alerts
* **RECEIVE_BOOT_COMPLETED:** required for start on boot
* **FOREGROUND_SERVICE:** prevents watchdog from being killed
* **REQUEST_IGNORE_BATTERY_OPTIMIZATIONS:** prevents start on boot and watchdog services from being killed
* **WRITE_SECURE_SETTINGS:** used to toggle USB and wireless debugging in the background when starting/stopping Shizuku
* **REQUEST_DELETE_PACKAGES:** used to request uninstall for Shizuku/stub when using stealth mode
* **REQUEST_INSTALL_PACKAGES:** used to request install for app updates, as well as Shizuku stub when using stealth mode

## 🌎 Translations

Translations are managed on the [Crowdin project of the upstream Shizuku project](https://crowdin.com/project/shizuku) (not a project of this fork). Note that the strings this fork adds on top of upstream (e.g., stealth mode) are not yet available on Crowdin.

## 🎁 Donations

This Shizuku fork and all of its features will always be free, and there will never be ads. If you've found any of the added features to be useful, consider [donating to thedjchi](https://www.buymeacoffee.com/thedjchi) — the maintainer of the upstream fork this project is based on — to help them keep maintaining the project!

## 📱 Developer Guide

### API & Demo Project

The API guide and a demo project are maintained in the [Shizuku-API](https://github.com/thedjchi/Shizuku-API) repository (upstream; not a project of this fork).

### Notes

1. Shizuku has different permissions in root and ADB mode. You can see permissions granted to ADB [here](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/packages/Shell/AndroidManifest.xml).
   If your app requires root permission, use `ShizukuService#getUid` to check if Shizuku is running as root or ADB, or use `ShizukuService#checkPermission` to check if the server has sufficient permissions.
2. On devices running Android 8 or lower, if you need to use Shizuku in a Service or Broadcast Receiver that might not be started by an Activity, please trigger the send binder by starting a transparent activity.
3. Please prefer using `ShizukuBinderWrapper` instead of directly using `transactRemote` when possible, as API calls can change across Android versions.

## 🤝 Contributing

### Building the App

- Clone with `git clone --recurse-submodules`
- Run gradle task `:manager:assembleDebug` or `:manager:assembleRelease`

The `:manager:assembleDebug` task generates a debuggable server. You can attach a debugger to `shizuku_server` to debug the server. In Android Studio, ensure `Run/Debug configurations > Always install with package manager` is checked, so that the server will use the latest code.

### Submitting Changes

1. Fork [dwicao/Shizuku](https://github.com/dwicao/Shizuku)
2. Create a feature branch (`git checkout -b branch-name`)
3. Make your changes
4. Commit your changes (`git commit -m 'Commit message'`)
5. Push to the branch (`git push origin branch-name`)
6. Open a Pull Request

## 📃 License

Code in this project is licensed under [Apache 2.0](LICENSE), following the original Shizuku project. The Shizuku-API (a separate repository) is licensed under MIT © RikkaW.

Original Shizuku © RikkaW / RikkaApps · Upstream fork © thedjchi · This fork © dwicao and contributors.
