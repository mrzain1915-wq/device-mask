# Device Mask

**Device Mask** is an LSPosed / Vector module that lets selected apps see
a different device identity — model, manufacturer, country, operator,
timezone, EID and SIM details — while the real device stays untouched.
It is display-only spoofing: the checker or tester app shows the chosen
values, the real eSIM, hardware, SIM and system values are never changed.

- Package: `com.device.mask`
- Current version: **1.0.29** (versionCode 68)
- Requires: rooted Android (KernelSU or Magisk) + LSPosed / Vector, Android 10+

## What you can do with it

- **Spoof your device** — make any selected app see a different phone:
  model, manufacturer, brand, fingerprint and codename, from 60+ real
  models.
- **Spoof your location and country** — GPS coordinates, country and
  timezone follow the country you pick (118 countries).
- **Spoof your network** — operator, operator name, MCC/MNC, SIM serial
  (ICCID), IMSI and phone number, per SIM.
- **Spoof your EID** — the eSIM identifier shown to apps can be changed
  to the one that belongs to the chosen device.
- **Enable eSIM support** — apps that check whether the device supports
  eSIM see it as supported, so eSIM features and testers work on any
  app you choose.
- **Spoof the rest too** — IMEI, MAC address, WebView user-agent and
  more, each with its own on/off switch, so you only change what you
  want to change.

Everything above is display-only: the chosen app sees the spoofed
values, while your real device, SIM and eSIM stay exactly as they are.

## Features

- **60+ device models** across all major brands (Samsung, Pixel,
  OnePlus, Xiaomi, Vivo, OPPO, Huawei, iPhone, ROG, and more), each with
  its real fingerprint and codename.
- **118 countries** with operators, dial codes, timezones and GPS
  coordinates — pick a country and the operator, timezone and GPS follow.
- **Per-function toggles** — every spoofed field (model, country,
  operator, timezone, EID, IMEI/IMSI/ICCID, phone number, MAC, WebView
  user-agent, …) can be switched on or off individually.
- **Random all** — one tap randomises model, country and operator, and
  regenerates the IDs.
- **Hook Target picker** — the module only runs in the apps you tick in
  LSPosed scope (dynamic scope, no static scope list).
- **Activation card** — the header shows whether the module actually ran
  in this boot (green *Activated* / red *Module is inactive*), proven by
  a timestamp the hook writes every time it runs.
- **Diagnose** — a built-in diagnostic screen that shows the toggles and
  the values apps will see, so a mismatch can be traced instead of
  guessed.

## Free tier and Premium

Only the first 10 models and the first 10 countries are selectable
on the free tier. Everything after that is shown as locked.

Premium unlocks all 62 models and all 118 countries. Log in on the
Account screen (side menu), then enter a redeem code on the Premium
screen. Redeem codes are given out by the owner; only their SHA-256
hashes are stored in the app, so a code can only be activated by
entering the real code. Premium stays on the account that redeemed it
and is cleared on logout. There is no server and no payment in the
app.

## Installation

1. Install the APK (see
   [Releases](https://github.com/SnzLabs/device-mask/releases)).
2. Enable **Device Mask** in LSPosed and tick the apps it should run in
   (e.g. your checker app).
3. Reboot.
4. Open Device Mask, pick a model and a country, save, then force-stop
   the target app and re-check it.

## Signature check

The app verifies its own signing certificate at startup against the
pinned debug keystore (`app/debug.keystore`). If the APK is re-signed
with a different key, the app refuses to open. Build it with the
pinned key (see Building) so updates install over each other.

## Building

Use the GitHub Actions workflow in `.github/workflows/build-apk.yml`:
push this project to a GitHub repo, open the **Actions** tab, run the
workflow, then download the `android-release` artifact and install
`Device-Mask-v1.0.29-Android.apk`. The build signs with the pinned
debug key in `app/debug.keystore`, so future updates install over it.

The project uses AGP 9.1.0, Gradle 9.4.1, Kotlin 2.2.20, Compose BOM
2025.10.01 and libxposed API 101. Settings reach the hook through the
framework's `getRemotePreferences` (RemotePreferences) — the libxposed
service artifact is an implementation dependency so its XposedProvider
is packaged in the APK.

## License

MIT — see `LICENSE`.

## Disclaimer

Device Mask is a privacy and testing tool. It only changes what a
chosen app *displays*; it does not change the real device, SIM or
eSIM. Do not use it to misrepresent a device to a service that
requires a genuine attestation.
