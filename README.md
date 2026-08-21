# Abstergo OS

> **A sandboxed virtual OS environment for Android — clone apps, sign in, use them, and flush all data whenever you want.**

Abstergo OS is an Android app that simulates a desktop operating system inside your phone. It lets you **clone social apps** (Instagram, WhatsApp, Telegram, X, Facebook, YouTube, Reddit, TikTok, Discord, Spotify, and 30+ more) into isolated sandboxed WebViews. Sign into any app, use it freely, then **wipe everything clean** — cookies, sessions, cache, localStorage — with a single tap. Your real device stays untouched.

---

## Features

### Virtual Desktop Environment
- **Boot animation** with Abstergo branding
- **Lock screen** with PIN authentication (default: `1234`)
- **Desktop** with wallpaper images, status bar, dock, and app grid
- **Floating window manager** — drag, resize, minimize, close windows (macOS-style controls)
- **App drawer** with categorized app sections
- **Multiple wallpaper options** using HD Abstergo-themed images

### App Cloning
- **5 built-in social apps**: Instagram, WhatsApp, Telegram, X/Twitter, Facebook
- **Clone 40+ installed apps** from your phone via the "+" button
  - YouTube, Reddit, TikTok, Discord, Spotify, Netflix, LinkedIn, Pinterest, Slack, Twitch, and more
- Each cloned app runs in a **sandboxed WebView** with isolated data
- **Real app icons** pulled from installed packages
- Cloned apps **persist across restarts** (Room database)

### Data Flush System
- **Per-app flush** — red trash icon inside any app wipes all data instantly
- **Flush All** — nuclear option wipes every cloned app at once
- Clears: cookies, localStorage, sessionStorage, cache, form data, browsing history
- Flush triggers automatic WebView reload — you're back to a clean slate

### Settings
- Change PIN with verification
- Switch between 4 HD wallpapers
- Dark/Light theme toggle
- Lock screen shortcut
- Flush all data from settings
- About screen

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | **Kotlin** |
| UI | **Jetpack Compose** + Material 3 |
| Architecture | **MVVM** — Single Activity, Compose Navigation |
| Persistence | **Room** (cloned apps) + **DataStore** (settings/PIN) |
| WebView | Sandboxed with isolated cookie/storage profiles |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 |
| Build System | **Gradle** (Kotlin DSL) |

---

## Project Structure

```
app/src/main/java/com/abstergo/
├── AbstergoOSApplication.kt          # Application class
├── MainActivity.kt                    # Single activity host
├── navigation/
│   └── OSNavigation.kt               # NavHost: Boot → Lock → Desktop
├── model/
│   ├── AppInfo.kt                     # AppType enum (built-in apps)
│   ├── AppSource.kt                   # Sealed class: BuiltIn + Cloned apps
│   ├── WallpaperOption.kt            # Wallpaper image options
│   └── WindowState.kt                # Floating window state
├── data/
│   ├── AppDatabase.kt                 # Room database
│   ├── NoteDao.kt / NoteEntity.kt     # Notes persistence
│   ├── ClonedAppDao.kt               # Cloned apps persistence
│   ├── ClonedAppEntity.kt            # Cloned app entity
│   └── SettingsDataStore.kt          # PIN, wallpaper, theme prefs
├── ui/
│   ├── theme/                         # Colors, Typography, Theme
│   ├── boot/BootScreen.kt            # Animated boot sequence
│   ├── lock/LockScreen.kt            # PIN lock screen
│   ├── desktop/
│   │   ├── DesktopScreen.kt          # Main desktop + "+" button
│   │   ├── DesktopViewModel.kt       # Wallpaper + cloned apps state
│   │   ├── StatusBar.kt              # Clock, battery, WiFi
│   │   ├── Dock.kt                   # Bottom dock with running indicators
│   │   ├── AppDrawer.kt             # Swipe-up app drawer
│   │   ├── AppPickerDialog.kt       # "+" clone apps picker
│   │   └── AppPickerViewModel.kt    # Scans installed apps
│   ├── window/
│   │   ├── FloatingWindow.kt        # Draggable/resizable window
│   │   └── WindowViewModel.kt       # Multi-window management
│   └── apps/
│       ├── sandbox/SandboxedWebApp.kt  # Generic sandboxed WebView + DataFlushManager
│       └── settings/                   # Settings app
```

---

## Setup & Build

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** (bundled with Android Studio)
- **Android SDK 34**

### Build & Run

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/AbstergoOS.git
cd AbstergoOS

# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch the app
adb shell am start -n com.abstergo/.MainActivity
```

### Default PIN
```
1234
```

---

## How It Works

1. **Boot** → Animated splash with Abstergo logo
2. **Lock Screen** → Enter PIN to unlock
3. **Desktop** → See built-in + cloned apps on your wallpaper
4. **Tap "+"** → Browse installed apps with web versions → Tap "Clone"
5. **Open any app** → Runs in a floating sandboxed WebView
6. **Sign in** → Use the app normally
7. **Flush data** → Red trash icon wipes everything clean
8. **Repeat** → Clone more apps, flush anytime

Each app's WebView data is isolated — flushing one doesn't affect others. The "Flush All" option wipes every app at once.

---

## Supported Apps for Cloning

| Category | Apps |
|----------|------|
| **Social** | Instagram, WhatsApp, Telegram, X/Twitter, Facebook, Messenger, Snapchat, Tumblr, BeReal |
| **Video** | YouTube, Netflix, Twitch, Prime Video |
| **Communication** | Discord, Slack, Skype, Zoom, Teams |
| **Productivity** | Gmail, Outlook, Google Meet, Dropbox, GitHub |
| **Lifestyle** | Spotify, SoundCloud, Strava, Duolingo, Uber |
| **Other** | Reddit, TikTok, LinkedIn, Pinterest, Quora, Medium, Amazon, eBay, Shazam |

---

## Screenshots

| Boot | Lock Screen | Desktop | App Picker |
|------|------------|---------|------------|
| Boot animation with Abstergo logo | PIN lock screen | Desktop with cloned apps | Browse & clone installed apps |

---

## License

This project is for educational and personal use.

---

## Disclaimer

Abstergo OS is not affiliated with Abstergo Industries (a fictional company from the Assassin's Creed franchise). The name and branding are used for creative/educational purposes only. All cloned app trademarks belong to their respective owners.
