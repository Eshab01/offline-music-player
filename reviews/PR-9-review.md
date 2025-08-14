<!-- trunk-ignore-all(markdownlint/MD041) -->

Thanks! Proceeding as requested.

Status

- PR is ready from my side. I can’t flip it from Draft to Ready-for-review via this interface—please click “Ready for review” in the PR UI.

What’s included

- Foreground service hardening (Android 10–15 compliance, early startForeground, service type, START_STICKY).
- Media notification actions (prev/play-pause/next), channel creation at app start, and continuous updates on state changes.
- Receivers locked down (exported=false) and controller leak fixed.
- Scanning performance: removed heavy per-track genre retrieval for v1; fast MediaStore read.
- Dependency upgrades: Media3 1.4.1 + AndroidX/coroutines bumps.
- MainActivity wired to MediaSession via MediaController; tapping a track plays and builds a queue from the visible list.
- Small utilities/strings for stability and UX.

Quick validation checklist (please run on Android 14/13 and one older device/emulator):

- Permissions: grant READ_MEDIA_AUDIO (or READ_EXTERNAL_STORAGE on <=12); deny POST_NOTIFICATIONS on 13+ does not crash foreground start.
- Scan: scans a few hundred files smoothly; add/remove a file and see library refresh shortly.
- Playback: play/pause/next/prev from app, notification, and BT headset; unplug pauses playback; call/Assistant interruptions recover.
- Background: lock screen >5 minutes; playback continues; notification stable (no flapping).
- Search and scrolling remain responsive on large lists.

Next steps

- If the above passes, click Ready for review and merge. I can then prepare a short 1.0.0 release notes draft.

If you want me to fold in any optional polish (About screen, auto-backup on import, album art in notifications), reply here and I’ll add them fast.
