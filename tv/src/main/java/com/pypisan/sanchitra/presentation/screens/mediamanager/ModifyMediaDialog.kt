package com.pypisan.sanchitra.presentation.screens.mediamanager

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.pypisan.sanchitra.data.entities.ChannelEntity
import com.pypisan.sanchitra.data.repositories.ChannelRepository
import com.pypisan.sanchitra.presentation.screens.common.TvTextField
import kotlinx.coroutines.launch

private enum class ChannelSortTab(val label: String) {
    NAME_ASC("A–Z"),
    NAME_DESC("Z–A"),
    RECENT("Recent"),
    CATEGORY("Category")
}
private data class BulkDeleteRequest(
    val channels: List<ChannelEntity>,
    val scopeDescription: String,
    val clearCategory: Boolean,
    val clearSource: Boolean
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyMediaDialog(
    repository: ChannelRepository,
    showDialog: Boolean = true,
    accentColor: Color = Color(0xFFFF5722),
    focusBorderColor: Color = Color(0xFFFFE082),
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    val scope = rememberCoroutineScope()
    val allChannels by repository.observeAllChannels().collectAsState(initial = emptyList())

    var selectedSortTab by remember { mutableStateOf(ChannelSortTab.NAME_ASC) }
    var selectedSource by remember { mutableStateOf<String?>(null) } // null = All Sources
    var selectedCategory by remember { mutableStateOf<String?>(null) } // null = All Categories

    var editingChannel by remember { mutableStateOf<ChannelEntity?>(null) }
    var deletingChannel by remember { mutableStateOf<ChannelEntity?>(null) }
    var bulkDeleteRequest by remember { mutableStateOf<BulkDeleteRequest?>(null) }
    var isBusy by remember { mutableStateOf(false) }

    // Build Source -> Categories hierarchy from existing ChannelEntity fields
    val sourceHierarchy by remember(allChannels) {
        derivedStateOf {
            allChannels
                .groupBy { it.resolvedSource() }
                .toSortedMap(String.CASE_INSENSITIVE_ORDER)
                .mapValues { (_, sourceChannels) ->
                    sourceChannels
                        .groupBy { it.category.ifBlank { "Uncategorized" } }
                        .toSortedMap(String.CASE_INSENSITIVE_ORDER)
                }
        }
    }

    // Filter + Sort channels based on Hierarchy selection & Sort Tab
    val visibleChannels by remember(allChannels, selectedSource, selectedCategory, selectedSortTab) {
        derivedStateOf {
            val filtered = allChannels.filter { ch ->
                val matchesSource = selectedSource == null || ch.resolvedSource() == selectedSource
                val matchesCategory = selectedCategory == null ||
                        ch.category.ifBlank { "Uncategorized" } == selectedCategory
                matchesSource && matchesCategory
            }

            when (selectedSortTab) {
                ChannelSortTab.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
                ChannelSortTab.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
                ChannelSortTab.RECENT -> filtered.sortedByDescending { it.updatedOn }
                ChannelSortTab.CATEGORY -> filtered.sortedWith(
                    compareBy<ChannelEntity> { it.category.lowercase() }
                        .thenBy { it.name.lowercase() }
                )
            }
        }
    }

    val dialogShape = RoundedCornerShape(20.dp)

    Dialog(
        onDismissRequest = {
            if (!isBusy) {
                when {
                    bulkDeleteRequest != null -> bulkDeleteRequest = null
                    deletingChannel != null -> deletingChannel = null
                    editingChannel != null -> editingChannel = null
                    else -> onDismiss()
                }
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val dialogView = LocalView.current
        LaunchedEffect(dialogView) {
            dialogView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 860.dp)
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
                .padding(22.dp)
        ) {
            when {
                // 1. Edit Channel View (Name, Category, URL, Logo)
                editingChannel != null -> {
                    EditChannelPanel(
                        channel = editingChannel!!,
                        isSaving = isBusy,
                        accentColor = accentColor,
                        focusBorderColor = focusBorderColor,
                        onCancel = { if (!isBusy) editingChannel = null },
                        onSave = { updated ->
                            isBusy = true
                            scope.launch {
                                try {
                                    repository.updateChannel(updated)
                                    editingChannel = null
                                } finally {
                                    isBusy = false
                                }
                            }
                        }
                    )
                }

                // 2. Delete Confirmation View
                deletingChannel != null -> {
                    DeleteChannelConfirmPanel(
                        channel = deletingChannel!!,
                        isDeleting = isBusy,
                        focusBorderColor = focusBorderColor,
                        onCancel = { if (!isBusy) deletingChannel = null },
                        onConfirmDelete = {
                            val target = deletingChannel ?: return@DeleteChannelConfirmPanel
                            isBusy = true
                            scope.launch {
                                try {
                                    repository.deleteChannel(target.id)
                                    deletingChannel = null
                                } finally {
                                    isBusy = false
                                }
                            }
                        }
                    )
                }

                // 3. Delete All Confirmation View
                bulkDeleteRequest != null -> {
                    val request = bulkDeleteRequest!!
                    DeleteAllChannelsConfirmPanel(
                        scopeDescription = request.scopeDescription,
                        isDeleting = isBusy,
                        focusBorderColor = focusBorderColor,
                        onCancel = { if (!isBusy) bulkDeleteRequest = null },
                        onConfirmDelete = {
                            isBusy = true
                            scope.launch {
                                try {
                                    // If your ChannelRepository has a batch delete method (e.g. deleteChannels(ids)
                                    // or deleteAllChannels()), you can call it here instead of looping.
                                    request.channels.forEach { ch ->
                                        repository.deleteChannel(ch.id)
                                    }
                                    if (request.clearCategory) selectedCategory = null
                                    if (request.clearSource) selectedSource = null
                                    bulkDeleteRequest = null
                                } finally {
                                    isBusy = false
                                }
                            }
                        }
                    )
                }

                // 3. Main Sorting Tabs + Source -> Category -> Channel Browser
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top Header + Sorting Tabs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Modify Media & Channels",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${visibleChannels.size} of ${allChannels.size} channels",
                                    color = Color.White.copy(alpha = 0.60f),
                                    fontSize = 12.sp
                                )
                            }

                            // Sorting Tab Bar
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChannelSortTab.entries.forEach { tab ->
                                    GlassTabChip(
                                        label = tab.label,
                                        selected = selectedSortTab == tab,
                                        accentColor = accentColor,
                                        focusBorderColor = focusBorderColor,
                                        onClick = { selectedSortTab = tab }
                                    )
                                }
                            }
                        }

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
                        Spacer(modifier = Modifier.height(12.dp))

                        if (allChannels.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No channels imported yet.",
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontSize = 15.sp
                                )
                            }
                        } else {
                            // Two-Pane Hierarchy: Left = Source -> Category, Right = Channel List
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Left Pane: Source -> Category Tree
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(0.34f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF16161E).copy(alpha = 0.90f))
                                        .border(
                                            1.dp,
                                            Color.White.copy(alpha = 0.12f),
                                            RoundedCornerShape(14.dp)
                                        ),
                                    contentPadding = PaddingValues(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    item(key = "all_sources") {
                                        HierarchyNodeItem(
                                            label = "All Sources",
                                            count = allChannels.size,
                                            isHeader = false,
                                            isIndented = false,
                                            selected = selectedSource == null && selectedCategory == null,
                                            accentColor = accentColor,
                                            focusBorderColor = focusBorderColor,
                                            onClick = {
                                                selectedSource = null
                                                selectedCategory = null
                                            }
                                        )
                                    }

                                    sourceHierarchy.forEach { (sourceName, categoriesMap) ->
                                        val sourceTotal = categoriesMap.values.sumOf { it.size }
                                        item(key = "src_$sourceName") {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            HierarchyNodeItem(
                                                label = sourceName,
                                                count = sourceTotal,
                                                isHeader = true,
                                                isIndented = false,
                                                selected = selectedSource == sourceName && selectedCategory == null,
                                                accentColor = accentColor,
                                                focusBorderColor = focusBorderColor,
                                                onClick = {
                                                    selectedSource = sourceName
                                                    selectedCategory = null
                                                }
                                            )
                                        }

                                        categoriesMap.forEach { (catName, catChannels) ->
                                            item(key = "cat_${sourceName}_$catName") {
                                                HierarchyNodeItem(
                                                    label = catName,
                                                    count = catChannels.size,
                                                    isHeader = false,
                                                    isIndented = true,
                                                    selected = selectedSource == sourceName && selectedCategory == catName,
                                                    accentColor = accentColor,
                                                    focusBorderColor = focusBorderColor,
                                                    onClick = {
                                                        selectedSource = sourceName
                                                        selectedCategory = catName
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Right Pane: Channel List with Edit & Delete
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(0.66f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF16161E).copy(alpha = 0.90f))
                                        .border(
                                            1.dp,
                                            Color.White.copy(alpha = 0.12f),
                                            RoundedCornerShape(14.dp)
                                        ),
                                    contentPadding = PaddingValues(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (visibleChannels.isEmpty()) {
                                        item(key = "empty_filtered") {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "No channels in this selection.",
                                                    color = Color.White.copy(alpha = 0.55f),
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    } else {
                                        items(
                                            items = visibleChannels,
                                            key = { it.id }
                                        ) { channel ->
                                            ChannelManageRow(
                                                channel = channel,
                                                accentColor = accentColor,
                                                focusBorderColor = focusBorderColor,
                                                onEdit = { editingChannel = channel },
                                                onDelete = { deletingChannel = channel }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (visibleChannels.isNotEmpty()) {
                                val deleteAllLabel = when {
                                    selectedCategory != null -> "Delete Category (${visibleChannels.size})"
                                    selectedSource != null -> "Delete Source (${visibleChannels.size})"
                                    else -> "Delete All (${allChannels.size})"
                                }
                                ModifyGlassButton(
                                    text = deleteAllLabel,
                                    accentColor = Color(0xFFEF5350),
                                    focusBorderColor = focusBorderColor,
                                    isPrimary = true,
                                    enabled = !isBusy,
                                    onClick = {
                                        val snapshot = visibleChannels.toList()
                                        val deletedIds = snapshot.mapTo(HashSet()) { it.id }
                                        val remainingInSource = selectedSource != null && allChannels.any {
                                            it.resolvedSource() == selectedSource && it.id !in deletedIds
                                        }
                                        val desc = when {
                                            selectedSource != null && selectedCategory != null ->
                                                "all ${snapshot.size} channels in \"$selectedSource • $selectedCategory\""
                                            selectedSource != null ->
                                                "all ${snapshot.size} channels in \"$selectedSource\""
                                            else ->
                                                "all ${snapshot.size} channels"
                                        }
                                        bulkDeleteRequest = BulkDeleteRequest(
                                            channels = snapshot,
                                            scopeDescription = desc,
                                            clearCategory = selectedCategory != null,
                                            clearSource = selectedSource != null && (selectedCategory == null || !remainingInSource)
                                        )
                                    }
                                )
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            ModifyGlassButton(
                                text = "Close",
                                accentColor = Color.White,
                                focusBorderColor = focusBorderColor,
                                isPrimary = false,
                                enabled = !isBusy,
                                onClick = onDismiss
                            )
                        }
                    }
                }
            }
        }
    }
}



private fun ChannelEntity.resolvedSource(): String {
    if (!network.isNullOrBlank()) return network
    val url = streamUrl.lowercase()
    return when {
        url.startsWith("file://") || url.startsWith("content://") || url.startsWith("/") -> "Local Disk"
        category.contains("local", ignoreCase = true) -> "Local Disk"
        isDrm -> "DRM Stream"
        else -> "Web / URL"
    }
}

@Composable
private fun ChannelManageRow(
    channel: ChannelEntity,
    accentColor: Color,
    focusBorderColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val logoUrlDisplay = channel.logoUrl ?: channel.logo

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF21212B).copy(alpha = 0.90f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo / Monogram Badge (swap with Coil AsyncImage if you use Coil)
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.18f))
                .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = channel.name.trim().take(2).uppercase().ifEmpty { "TV" },
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Channel Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = channel.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = channel.category,
                    color = accentColor.copy(alpha = 0.95f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = if (!logoUrlDisplay.isNullOrBlank()) "• Logo ✓" else "• No Logo",
                    color = Color.White.copy(alpha = 0.50f),
                    fontSize = 11.sp
                )
            }
            Text(
                text = channel.streamUrl,
                color = Color.White.copy(alpha = 0.50f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Edit & Delete Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModifyGlassButton(
                text = "Edit",
                accentColor = accentColor,
                focusBorderColor = focusBorderColor,
                isPrimary = true,
                compact = true,
                onClick = onEdit
            )
            ModifyGlassButton(
                text = "Delete",
                accentColor = Color(0xFFEF5350),
                focusBorderColor = focusBorderColor,
                isPrimary = true,
                compact = true,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun EditChannelPanel(
    channel: ChannelEntity,
    isSaving: Boolean,
    accentColor: Color,
    focusBorderColor: Color,
    onCancel: () -> Unit,
    onSave: (ChannelEntity) -> Unit
) {
    var name by remember(channel.id) { mutableStateOf(channel.name) }
    var category by remember(channel.id) { mutableStateOf(channel.category) }
    var streamUrl by remember(channel.id) { mutableStateOf(channel.streamUrl) }
    var logoUrl by remember(channel.id) { mutableStateOf(channel.logoUrl ?: channel.logo ?: "") }
    var errorText by remember(channel.id) { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Edit Channel",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Source: ${channel.resolvedSource()}",
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.70f), Color.Transparent)
                    )
                )
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Scrollable 4 Editable Fields (Name, Category, URL, Logo)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TvTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorText = null
                },
                label = "Channel Name",
                placeholder = "Channel name",
                enabled = !isSaving,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            TvTextField(
                value = category,
                onValueChange = {
                    category = it
                    errorText = null
                },
                label = "Category",
                placeholder = "Sports / News / Entertainment",
                enabled = !isSaving,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            TvTextField(
                value = streamUrl,
                onValueChange = {
                    streamUrl = it
                    errorText = null
                },
                label = "Stream URL",
                placeholder = "https://example.com/stream.m3u8",
                enabled = !isSaving,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            TvTextField(
                value = logoUrl,
                onValueChange = { logoUrl = it },
                label = "Logo URL (Optional)",
                placeholder = "https://example.com/logo.png",
                enabled = !isSaving,
                imeAction = ImeAction.Done,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            ModifyGlassButton(
                text = "Back",
                accentColor = Color.White,
                focusBorderColor = focusBorderColor,
                isPrimary = false,
                enabled = !isSaving,
                onClick = onCancel
            )
            ModifyGlassButton(
                text = if (isSaving) "Saving..." else "Save Changes",
                accentColor = accentColor,
                focusBorderColor = focusBorderColor,
                isPrimary = true,
                enabled = !isSaving,
                onClick = {
                    when {
                        name.isBlank() -> errorText = "Channel name cannot be empty."
                        category.isBlank() -> errorText = "Category cannot be empty."
                        streamUrl.isBlank() -> errorText = "Stream URL cannot be empty."
                        else -> {
                            val trimmedLogo = logoUrl.trim().ifEmpty { null }
                            onSave(
                                channel.copy(
                                    name = name.trim(),
                                    category = category.trim(),
                                    streamUrl = streamUrl.trim(),
                                    logoUrl = trimmedLogo,
                                    logo = trimmedLogo
                                )
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun DeleteChannelConfirmPanel(
    channel: ChannelEntity,
    isDeleting: Boolean,
    focusBorderColor: Color,
    onCancel: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val cancelFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { cancelFocus.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Delete Channel?",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Are you sure you want to delete \"${channel.name}\" from ${channel.category}?",
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            ModifyGlassButton(
                text = "Cancel",
                accentColor = Color.White,
                focusBorderColor = focusBorderColor,
                isPrimary = false,
                enabled = !isDeleting,
                modifier = Modifier.focusRequester(cancelFocus),
                onClick = onCancel
            )
            ModifyGlassButton(
                text = if (isDeleting) "Deleting..." else "Delete",
                accentColor = Color(0xFFEF5350),
                focusBorderColor = focusBorderColor,
                isPrimary = true,
                enabled = !isDeleting,
                onClick = onConfirmDelete
            )
        }
    }
}

@Composable
private fun HierarchyNodeItem(
    label: String,
    count: Int,
    isHeader: Boolean,
    isIndented: Boolean,
    selected: Boolean,
    accentColor: Color,
    focusBorderColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier.padding(start = if (isIndented) 12.dp else 0.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = if (isFocused) 1.8.dp else if (selected) 1.2.dp else 0.dp,
            color = when {
                isFocused -> focusBorderColor
                selected -> accentColor.copy(alpha = 0.80f)
                else -> Color.Transparent
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    when {
                        selected -> accentColor.copy(alpha = 0.24f)
                        isFocused -> Color(0xFF323240).copy(alpha = 0.92f)
                        isHeader -> Color(0xFF252531).copy(alpha = 0.85f)
                        else -> Color(0xFF1D1D26).copy(alpha = 0.80f)
                    }
                )
                .padding(horizontal = 10.dp, vertical = if (isHeader) 9.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isIndented) "↳ $label" else label,
                color = if (selected || isFocused || isHeader) Color.White else Color.White.copy(alpha = 0.80f),
                fontSize = if (isHeader) 13.sp else 12.sp,
                fontWeight = if (isHeader || selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = count.toString(),
                color = if (selected) accentColor else Color.White.copy(alpha = 0.55f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun GlassTabChip(
    label: String,
    selected: Boolean,
    accentColor: Color,
    focusBorderColor: Color,
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
                isFocused -> focusBorderColor
                selected -> accentColor.copy(alpha = 0.85f)
                else -> Color.White.copy(alpha = 0.18f)
            }
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    when {
                        selected -> accentColor.copy(alpha = 0.30f)
                        isFocused -> Color(0xFF343442).copy(alpha = 0.92f)
                        else -> Color(0xFF20202A).copy(alpha = 0.85f)
                    }
                )
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Text(
                text = label,
                color = if (selected || isFocused) Color.White else Color.White.copy(alpha = 0.78f),
                fontSize = 12.sp,
                fontWeight = if (selected || isFocused) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ModifyGlassButton(
    modifier: Modifier = Modifier,
    text: String,
    accentColor: Color,
    focusBorderColor: Color,
    isPrimary: Boolean,
    compact: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        label = "modifyBtnScale"
    )
    val baseBg by animateColorAsState(
        targetValue = when {
            isPrimary && isFocused -> accentColor.copy(alpha = 0.95f)
            isPrimary -> accentColor.copy(alpha = 0.45f)
            isFocused -> Color(0xFF3E3E4E).copy(alpha = 0.95f)
            else -> Color(0xFF252530).copy(alpha = 0.85f)
        },
        label = "modifyBtnBg"
    )

    Surface(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .zIndex(if (isFocused) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = if (isFocused) 2.dp else 1.dp,
            color = if (isFocused) focusBorderColor else Color.White.copy(alpha = 0.22f)
        )
    ) {
        Box(
            modifier = Modifier
                .background(baseBg)
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.White.copy(alpha = if (isFocused) 0.34f else 0.14f),
                        0.48f to Color.White.copy(alpha = if (isFocused) 0.10f else 0.04f),
                        0.50f to Color.Transparent,
                        1.00f to Color.White.copy(alpha = 0.04f)
                    )
                )
                .padding(
                    horizontal = if (compact) 12.dp else 20.dp,
                    vertical = if (compact) 7.dp else 11.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = if (compact) 12.sp else 14.sp,
                fontWeight = if (isFocused || isPrimary) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}


@Composable
private fun DeleteAllChannelsConfirmPanel(
    scopeDescription: String,
    isDeleting: Boolean,
    focusBorderColor: Color,
    onCancel: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val cancelFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { cancelFocus.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Delete All Channels?",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Are you sure you want to delete $scopeDescription? This action cannot be undone.",
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            ModifyGlassButton(
                text = "Cancel",
                accentColor = Color.White,
                focusBorderColor = focusBorderColor,
                isPrimary = false,
                enabled = !isDeleting,
                modifier = Modifier.focusRequester(cancelFocus),
                onClick = onCancel
            )
            ModifyGlassButton(
                text = if (isDeleting) "Deleting..." else "Delete All",
                accentColor = Color(0xFFEF5350),
                focusBorderColor = focusBorderColor,
                isPrimary = true,
                enabled = !isDeleting,
                onClick = onConfirmDelete
            )
        }
    }
}