package com.pypisan.sanchitra.utils

import android.app.ActivityManager
import android.content.Context
import android.media.MediaCodecList
import android.media.MediaDrm
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.media3.common.C

object DeviceProfileHelper {
    const val KEY_MANUFACTURER = "manufacturer"
    const val KEY_BRAND = "brand"
    const val KEY_MODEL = "model"
    const val KEY_DEVICE = "device"
    const val KEY_BOARD = "board"
    const val KEY_HARDWARE = "hardware"
    const val KEY_FINGERPRINT = "fingerprint"

    const val KEY_OS_VERSION = "os_android_version"
    const val KEY_OS_SDK_INT = "os_sdk_int"
    const val KEY_OS_SECURITY_PATCH = "os_security_patch"
    const val KEY_BUILD_ID = "build_id"

    const val KEY_RAM_TOTAL_MB = "ram_total_mb"
    const val KEY_RAM_AVAIL_MB = "ram_available_mb"
    const val KEY_RAM_IS_LOW = "ram_is_low_memory"

    const val KEY_DISPLAY_RESOLUTION = "display_resolution"
    const val KEY_DISPLAY_REFRESH_RATE = "display_refresh_rate"
    const val KEY_DISPLAY_DENSITY_DPI = "display_density_dpi"
    const val KEY_DISPLAY_HDR_TYPES = "display_hdr_types"

    const val KEY_DRM_SECURITY_LEVEL = "drm_security_level"
    const val KEY_DRM_VENDOR = "drm_vendor"
    const val KEY_DRM_VERSION = "drm_version"
    const val KEY_DRM_SYSTEM_ID = "drm_system_id"

    const val KEY_SECURE_DECODERS = "secure_decoders"

    fun getDeviceProfileMap(context: Context): Map<String, Any> {
        val profile = mutableMapOf<String, Any>()

        // 1. Device Hardware
        profile[KEY_MANUFACTURER] = Build.MANUFACTURER ?: "Unknown"
        profile[KEY_BRAND] = Build.BRAND ?: "Unknown"
        profile[KEY_MODEL] = Build.MODEL ?: "Unknown"
        profile[KEY_DEVICE] = Build.DEVICE ?: "Unknown"
        profile[KEY_BOARD] = Build.BOARD ?: "Unknown"
        profile[KEY_HARDWARE] = Build.HARDWARE ?: "Unknown"
        profile[KEY_FINGERPRINT] = Build.FINGERPRINT ?: "Unknown"

        // 2. OS Details
        profile[KEY_OS_VERSION] = Build.VERSION.RELEASE ?: "Unknown"
        profile[KEY_OS_SDK_INT] = Build.VERSION.SDK_INT
        profile[KEY_BUILD_ID] = Build.DISPLAY ?: "Unknown"
        profile[KEY_OS_SECURITY_PATCH] = Build.VERSION.SECURITY_PATCH ?: "Unknown"

        // 3. RAM Info
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        profile[KEY_RAM_TOTAL_MB] = memInfo.totalMem / (1024 * 1024)
        profile[KEY_RAM_AVAIL_MB] = memInfo.availMem / (1024 * 1024)
        profile[KEY_RAM_IS_LOW] = memInfo.lowMemory

        // 4. Display
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val display = wm?.defaultDisplay
        val metrics = DisplayMetrics()
        display?.getRealMetrics(metrics)
        profile[KEY_DISPLAY_RESOLUTION] = "${metrics.widthPixels}x${metrics.heightPixels}"
        profile[KEY_DISPLAY_REFRESH_RATE] = display?.refreshRate ?: 0f
        profile[KEY_DISPLAY_DENSITY_DPI] = metrics.densityDpi
        val hdrTypes = display?.hdrCapabilities?.supportedHdrTypes ?: intArrayOf()
        profile[KEY_DISPLAY_HDR_TYPES] = hdrTypes.joinToString()

        // 5. DRM Info (Widevine L1/L3)
        var mediaDrm: MediaDrm? = null
        try {
            mediaDrm = MediaDrm(C.WIDEVINE_UUID)
            profile[KEY_DRM_SECURITY_LEVEL] = getDrmPropertySafe(mediaDrm, "securityLevel")
            profile[KEY_DRM_VENDOR] = getDrmPropertySafe(mediaDrm, MediaDrm.PROPERTY_VENDOR)
            profile[KEY_DRM_VERSION] = getDrmPropertySafe(mediaDrm, MediaDrm.PROPERTY_VERSION)
            profile[KEY_DRM_SYSTEM_ID] = getDrmPropertySafe(mediaDrm, "systemId")

        } catch (e: Exception) {
            profile[KEY_DRM_SECURITY_LEVEL] = "Error: ${e.message}"
        } finally {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mediaDrm?.close()
                } else {
                    @Suppress("DEPRECATION")
                    mediaDrm?.release()
                }
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }

        // 6. Secure Video Decoders
        val secureDecoders = mutableListOf<String>()
        try {
            val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            for (info in codecList.codecInfos) {
                if (!info.isEncoder && info.name.contains("secure", ignoreCase = true)) {
                    secureDecoders.add(info.name)
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        profile[KEY_SECURE_DECODERS] = secureDecoders

        return profile
    }

    private fun getDrmPropertySafe(drm: MediaDrm, key: String): String {
        return try {
            drm.getPropertyString(key)
        } catch (e: Exception) {
            "Unavailable"
        }
    }
}