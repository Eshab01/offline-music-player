package com.eshab.offlineplayer.util

import android.Manifest
import android.os.Build

fun audioPermission(): String =
    if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE