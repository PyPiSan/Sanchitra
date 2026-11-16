package com.pypisan.sanchitra.presentation.screens.common


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import java.io.File
import android.os.storage.StorageManager

@Composable
fun FileBrowserRow(
    badge: String,
    name: String,
    subtitle: String?,
    isDirectory: Boolean,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected && isFocused -> accentColor.copy(alpha = 0.30f)
            isSelected -> accentColor.copy(alpha = 0.20f)
            isFocused -> Color(0xFF30303E).copy(alpha = 0.95f)
            else -> Color(0xFF1E1E28).copy(alpha = 0.80f)
        },
        label = "fileRowBg"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = if (isFocused || isSelected) 1.5.dp else 0.8.dp,
            color = when {
                isFocused -> accentColor
                isSelected -> Color(0xFF81C784)
                else -> Color.White.copy(alpha = 0.10f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge (DIR / M3U / JSON / UP)
            Box(
                modifier = Modifier
                    .width(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDirectory) accentColor.copy(alpha = 0.18f)
                        else Color.White.copy(alpha = 0.10f)
                    )
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge,
                    color = if (isDirectory) accentColor else Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected || isFocused) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            subtitle?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = it,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun GlossyChip(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = if (isFocused) 1.8.dp else 1.dp,
            color = when {
                isFocused -> accentColor
                selected -> accentColor.copy(alpha = 0.70f)
                else -> Color.White.copy(alpha = 0.20f)
            }
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    when {
                        selected -> accentColor.copy(alpha = 0.24f)
                        isFocused -> Color(0xFF343442).copy(alpha = 0.92f)
                        else -> Color(0xFF20202A).copy(alpha = 0.85f)
                    }
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                color = if (selected || isFocused) Color.White else Color.White.copy(alpha = 0.80f),
                fontSize = 12.sp,
                fontWeight = if (selected || isFocused) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun GlossyDiskButton(
    text: String,
    accentColor: Color,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        label = "diskBtnScale"
    )
    val baseBg by animateColorAsState(
        targetValue = when {
            isPrimary && isFocused -> accentColor.copy(alpha = 0.95f)
            isPrimary -> accentColor.copy(alpha = 0.50f)
            isFocused -> Color(0xFF3E3E4E).copy(alpha = 0.95f)
            else -> Color(0xFF252530).copy(alpha = 0.85f)
        },
        label = "diskBtnBg"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .zIndex(if (isFocused) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = if (isFocused) 2.dp else 1.dp,
            color = if (isFocused) Color.White else Color.White.copy(alpha = 0.25f)
        )
    ) {
        Box(
            modifier = Modifier
                .background(baseBg)
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.White.copy(alpha = if (isFocused) 0.34f else 0.16f),
                        0.48f to Color.White.copy(alpha = if (isFocused) 0.12f else 0.05f),
                        0.50f to Color.Transparent,
                        1.00f to Color.White.copy(alpha = if (isFocused) 0.10f else 0.03f)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isPrimary && isFocused) Color.Black else Color.White,
                fontSize = 14.sp,
                fontWeight = if (isFocused || isPrimary) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}


data class TvStorageRoot(
    val label: String,
    val directory: File
)


fun hasTvStorageAccess(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}


fun getTvStorageRoots(context: Context): List<TvStorageRoot> {
    val roots = mutableListOf<TvStorageRoot>()
    val internal = Environment.getExternalStorageDirectory()
    if (internal.exists() && internal.canRead()) {
        val downloads = File(internal, Environment.DIRECTORY_DOWNLOADS)
        if (downloads.exists() && downloads.canRead()) {
            roots.add(TvStorageRoot("Downloads", downloads))
        }
        roots.add(TvStorageRoot("Internal", internal))
    }

    // Detect USB / External SD drives
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
        storageManager?.storageVolumes?.forEach { volume ->
            val dir = volume.directory
            if (dir != null && dir.exists() && dir.canRead() && !volume.isPrimary) {
                val label = volume.getDescription(context).ifBlank { "USB Drive" }
                roots.add(TvStorageRoot(label, dir))
            }
        }
    } else {
        File("/storage").listFiles()?.forEach { dir ->
            if (dir.isDirectory &&
                dir.canRead() &&
                dir.name != "emulated" &&
                dir.name != "self"
            ) {
                roots.add(TvStorageRoot("USB (${dir.name})", dir))
            }
        }
    }

    return roots.distinctBy { it.directory.absolutePath }
}

fun listDirectoryItems(dir: File, allowedExtensions: Set<String>): List<File> {
    val children = dir.listFiles() ?: return emptyList()
    return children
        .filter { file ->
            !file.name.startsWith(".") && (
                    file.isDirectory ||
                            file.extension.lowercase() in allowedExtensions
                    )
        }
        .sortedWith(
            compareBy<File> { !it.isDirectory }
                .thenBy { it.name.lowercase() }
        )
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.1f MB", bytes / (1024f * 1024f))
    }
}