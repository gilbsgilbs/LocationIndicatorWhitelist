## Location Indicator Whitelist

This LSPosed module is supposed to prevent selected packages from spamming the annoying location
notification dot.

### How

- Install Magisk/Riru/LSPosed
- Install and enable this module
- Open the main activity and select the packages you want to whitelist
- Reboot

### Why

- Some apps do access the location in background for legitimate reasons, and the user may not want
  to be interrupted because of that.
- Some location providers packages aren't whitelisted by ROMs.
- The "Status bar location indicator" switch in developer options has a lot of drawbacks:
  - It disables **all** notifications when the user might actually find the notification dot useful
  - It doesn't survive a reboot, meaning that you consistently have to switch it off
  - It's buggy: if you disable it at the wrong moment, you may get the notification dot stuck on
    your screen until you reboot. On LOS20, it seems that it even doesn't prevent the location
    notification dot from showing at all.

### Caveats

- Currently only tested on OnePlus 8 (instantnoodle) running LineageOS 20. Please note that I do not
  plan on adding support for all ROMs and devices, and I might not even read or respond to issues
  requesting new supports. However, I'll gladly review PRs adding support for new ROMs or
  devices.
- The current hook is very weak and dirty. I would have preferred hooking into the core framework
  directly, but none of my attempts were successful. I had to hook into SystemUI instead. If you
  find a way to hook into android framework directly, please let me know. Here's a sneak peek of my
  unsuccessful hook attempts:
  - `android.permission.PermissionUsageHelper` => `shouldShowLocationIndicator`
  - `android.provider.DeviceConfig` => `get/setBoolean("location_indicators_enabled")`
  - `android.permission.PermissionManager` => `getIndicatorExemptedPackages`
  - Patching resource `config_locationExtraPackageNames`
  - Systemizing whitelisted apps
  - `com.android.systemui.statusbar.phone.PhoneStatusBarPolicy` => `onLocationActiveChanged`
  - a combination of all those
- UI is clunky but simple and straight to the point. It lacks a search feature though.

### Contributing

Please open issues and PRs on [the upstream
repo](https://github.com/gilbsgilbs/LocationIndicatorWhitelist), not on the
[XPosed-Module-Repo](https://github.com/Xposed-Modules-Repo/fr.netstat.locationindicatorwhitelist).
