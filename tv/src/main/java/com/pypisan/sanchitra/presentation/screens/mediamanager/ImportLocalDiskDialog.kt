package com.pypisan.sanchitra.presentation.screens.mediamanager

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File
import androidx.core.net.toUri
import com.pypisan.sanchitra.presentation.screens.common.FileBrowserRow
import com.pypisan.sanchitra.presentation.screens.common.GlossyChip
import com.pypisan.sanchitra.presentation.screens.common.GlossyDiskButton
import com.pypisan.sanchitra.presentation.screens.common.formatFileSize
import com.pypisan.sanchitra.presentation.screens.common.getTvStorageRoots
import com.pypisan.sanchitra.presentation.screens.common.hasTvStorageAccess
import com.pypisan.sanchitra.presentation.screens.common.listDirectoryItems


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ImportLocalDiskDialog(
    showDialog: Boolean = true,
    accentColor: Color = Color(0xFFFFE082),
    allowedExtensions: Set<String> = setOf("mp4", "mkv"),
    onDismiss: () -> Unit,
    onConfirm: (Uri) -> Unit
) {
    if (!showDialog) return

    val context = LocalContext.current

    var hasStoragePermission by remember(showDialog) {
        mutableStateOf(hasTvStorageAccess(context))
    }

    val legacyPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasStoragePermission = granted || hasTvStorageAccess(context)
    }

    val allFilesSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        hasStoragePermission = hasTvStorageAccess(context)
    }

    val storageRoots = remember(showDialog, hasStoragePermission) {
        getTvStorageRoots(context)
    }

    var currentDir by remember(showDialog, storageRoots) {
        mutableStateOf(
            storageRoots.firstOrNull { it.label == "Downloads" }?.directory
                ?: storageRoots.firstOrNull()?.directory
                ?: Environment.getExternalStorageDirectory()
        )
    }

    var selectedFile by remember(showDialog) { mutableStateOf<File?>(null) }
    var manualPath by remember(showDialog) { mutableStateOf("") }
    var errorText by remember(showDialog) { mutableStateOf<String?>(null) }

    val directoryEntries by remember(currentDir, hasStoragePermission, allowedExtensions) {
        derivedStateOf {
            if (!hasStoragePermission) {
                emptyList()
            } else {
                listDirectoryItems(currentDir, allowedExtensions)
            }
        }
    }

    val dialogShape = RoundedCornerShape(20.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val dialogView = LocalView.current
        LaunchedEffect(dialogView) {
            dialogView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 640.dp)
                .heightIn(max = 500.dp)
                .clip(dialogShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF23232E).copy(alpha = 0.96f),
                            Color(0xFF13131A).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f),
                            accentColor.copy(alpha = 0.32f)
                        )
                    ),
                    shape = dialogShape
                )
                .drawWithCache {
                    val sheen = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.09f), Color.Transparent),
                        start = Offset.Zero,
                        end = Offset(size.width * 0.65f, size.height * 0.40f)
                    )
                    onDrawBehind { drawRect(brush = sheen) }
                }
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Pinned Header
                Text(
                    text = "Import from Local Disk / USB",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select an MPEG4, MKV, or media file stored on your TV or USB drive.",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.70f),
                                    Color.White.copy(alpha = 0.14f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!hasStoragePermission) {
                        // Storage Permission Prompt (when needed)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1A1A24).copy(alpha = 0.92f))
                                .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Storage permission is needed to browse folders & USB drives on this TV.",
                                color = Color.White.copy(alpha = 0.88f),
                                fontSize = 13.sp
                            )
                            GlossyDiskButton(
                                text = "Grant Storage Access",
                                accentColor = accentColor,
                                isPrimary = true,
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                        val intent = Intent(
                                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                            "package:${context.packageName}".toUri()
                                        )
                                        try {
                                            allFilesSettingsLauncher.launch(intent)
                                        } catch (_: ActivityNotFoundException) {
                                            // Fallback for TVs without All-Files screen
                                            val fallback = Intent(
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                "package:${context.packageName}".toUri()
                                            )
                                            try {
                                                allFilesSettingsLauncher.launch(fallback)
                                            } catch (_: Exception) {
                                                errorText = "Unable to open permission settings on this TV. Enter the path manually below."
                                            }
                                        }
                                    } else {
                                        legacyPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                }
                            )
                        }
                    } else {
                        // Storage Root Selector Chips (Internal / Downloads / USB)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            storageRoots.forEach { root ->
                                val isCurrentRoot = currentDir.absolutePath.startsWith(root.directory.absolutePath) &&
                                        (root.label != "Internal" || !currentDir.absolutePath.startsWith(
                                            File(root.directory, "Download").absolutePath
                                        ))
                                GlossyChip(
                                    label = root.label,
                                    selected = isCurrentRoot,
                                    accentColor = accentColor,
                                    onClick = {
                                        currentDir = root.directory
                                        errorText = null
                                    }
                                )
                            }
                        }

                        // Current Path Indicator
                        Text(
                            text = "Path: ${currentDir.absolutePath}",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // In-App Glass File List
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF16161E).copy(alpha = 0.92f))
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.14f),
                                    RoundedCornerShape(14.dp)
                                )
                        ) {
                            val parentDir = currentDir.parentFile
                            val canGoUp = parentDir != null &&
                                    storageRoots.any { currentDir.absolutePath != it.directory.absolutePath } &&
                                    parentDir.canRead()

                            if (!canGoUp && directoryEntries.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No supported files (.mp4, .mkv, media) found here.",
                                        color = Color.White.copy(alpha = 0.50f),
                                        fontSize = 13.sp
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (canGoUp) {
                                        item(key = "parent_dir") {
                                            FileBrowserRow(
                                                badge = "UP",
                                                name = ".. (Parent Folder)",
                                                subtitle = null,
                                                isDirectory = true,
                                                isSelected = false,
                                                accentColor = accentColor,
                                                onClick = { currentDir = parentDir }
                                            )
                                        }
                                    }

                                    items(
                                        items = directoryEntries,
                                        key = { it.absolutePath }
                                    ) { file ->
                                        val isDir = file.isDirectory
                                        val isSelected = selectedFile?.absolutePath == file.absolutePath
                                        FileBrowserRow(
                                            badge = if (isDir) "DIR" else file.extension.uppercase().ifBlank { "FILE" },
                                            name = file.name,
                                            subtitle = if (isDir) "Folder" else formatFileSize(file.length()),
                                            isDirectory = isDir,
                                            isSelected = isSelected,
                                            accentColor = accentColor,
                                            onClick = {
                                                if (isDir) {
                                                    currentDir = file
                                                } else {
                                                    selectedFile = file
                                                    manualPath = ""
                                                    errorText = null
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Selected File Banner
                    selectedFile?.let { file ->
                        Text(
                            text = "Selected: ${file.name} (${file.absolutePath})",
                            color = Color(0xFF81C784),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    errorText?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pinned Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlossyDiskButton(
                        text = "Cancel",
                        accentColor = Color.White,
                        isPrimary = false,
                        onClick = onDismiss
                    )
                    GlossyDiskButton(
                        text = "Import File",
                        accentColor = accentColor,
                        isPrimary = true,
                        onClick = {
                            val manualFile = manualPath.trim()
                                .takeIf { it.isNotEmpty() }
                                ?.let { File(it) }

                            val targetFile = selectedFile ?: manualFile

                            when {
                                targetFile == null -> {
                                    errorText = "Please select a file or enter a valid file path."
                                }
                                !targetFile.exists() || !targetFile.isFile -> {
                                    errorText = "File not found at: ${targetFile.absolutePath}"
                                }
                                else -> {
                                    errorText = null
                                    onConfirm(Uri.fromFile(targetFile))
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}