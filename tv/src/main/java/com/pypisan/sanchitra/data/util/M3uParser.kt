package com.pypisan.sanchitra.data.util

import com.pypisan.sanchitra.data.entities.ChannelEntity

object M3uParser {

    private val attrRegex = Regex("""([\w-]+)\s*=\s*"([^"]*)"|([\w-]+)\s*=\s*'([^']*)'""")

    fun parse(
        content: String,
        defaultCategory: String = "General"
    ): List<ChannelEntity> {
        val fallbackCategory = defaultCategory.trim().ifEmpty { "General" }
        val channels = mutableListOf<ChannelEntity>()
        val lines = content.lineSequence().map { it.trim() }.toList()

        var globalEpgUrl: String? = null
        var pendingAttrs: Map<String, String> = emptyMap()
        var pendingTitle = ""
        var pendingLicenseKey: String? = null
        var pendingLicenseUrl: String? = null
        var pendingIsDrm = false
        var hasExtInf = false

        for (line in lines) {
            if (line.isEmpty()) continue

            when {
                line.startsWith("#EXTM3U", ignoreCase = true) -> {
                    val attrs = parseAttributes(line)
                    globalEpgUrl = attrs["url-tvg"]?.ifBlank { null }
                        ?: attrs["x-tvg-url"]?.ifBlank { null }
                }

                line.startsWith("#EXTINF", ignoreCase = true) -> {
                    val (metaPart, titlePart) = splitOutsideQuotes(line)
                    pendingAttrs = parseAttributes(metaPart)
                    pendingTitle = titlePart.trim()
                    hasExtInf = true
                }

                line.startsWith("#KODIPROP:", ignoreCase = true) -> {
                    val prop = line.substringAfter("#KODIPROP:", "")
                    val key = prop.substringBefore("=").trim()
                    val value = prop.substringAfter("=", "").trim()

                    if (key.equals("inputstream.adaptive.license_type", ignoreCase = true) && value.isNotEmpty()) {
                        pendingIsDrm = true
                    } else if (key.equals("inputstream.adaptive.license_key", ignoreCase = true) && value.isNotEmpty()) {
                        pendingIsDrm = true
                        if (value.startsWith("http://", ignoreCase = true) ||
                            value.startsWith("https://", ignoreCase = true)
                        ) {
                            pendingLicenseUrl = value
                        } else {
                            pendingLicenseKey = value
                        }
                    }
                }

                line.startsWith("#") -> {
                    // Ignore other comments / #EXTVLCOPT lines
                }

                else -> {
                    // Stream URL line
                    val streamUrl = line.trim()
                    if (streamUrl.isNotEmpty()) {
                        val name = pendingTitle.ifBlank {
                            pendingAttrs["tvg-name"]?.trim().orEmpty()
                        }.ifBlank {
                            "Channel ${channels.size + 1}"
                        }

                        val category = pendingAttrs["group-title"]?.trim()
                            ?.takeIf { it.isNotEmpty() }
                            ?: fallbackCategory

                        val now = ChannelEntity.nowUtcIso()

                        channels += ChannelEntity(
                            id = 0L,
                            name = name,
                            category = category,
                            streamUrl = streamUrl,
                            iptvChannelId = pendingAttrs["tvg-id"]?.trim()?.ifEmpty { null },
                            country = pendingAttrs["tvg-country"]?.trim()?.ifEmpty { null } ?: "IN",
                            language = pendingAttrs["tvg-language"]?.trim()?.ifEmpty { null } ?: "English",
                            logoUrl = pendingAttrs["tvg-logo"]?.trim()?.ifEmpty { null },
                            epgUrl = globalEpgUrl,
                            licenseKey = pendingLicenseKey,
                            licenseUrl = pendingLicenseUrl,
                            isDrm = pendingIsDrm,
                            insertedOn = now,
                            updatedOn = now
                        )
                    }

                    // Reset state for next channel
                    pendingAttrs = emptyMap()
                    pendingTitle = ""
                    pendingLicenseKey = null
                    pendingLicenseUrl = null
                    pendingIsDrm = false
                    hasExtInf = false
                }
            }
        }

        // Ignore dangling #EXTINF with no URL
        if (hasExtInf) {
            pendingAttrs = emptyMap()
        }

        return channels
    }

    private fun parseAttributes(input: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        attrRegex.findAll(input).forEach { match ->
            val key = (match.groupValues[1].ifEmpty { match.groupValues[3] }).lowercase()
            val value = match.groupValues[2].ifEmpty { match.groupValues[4] }
            map[key] = value
        }
        return map
    }

    private fun splitOutsideQuotes(line: String): Pair<String, String> {
        var inDouble = false
        var inSingle = false
        for (i in line.indices) {
            when (line[i]) {
                '"' -> if (!inSingle) inDouble = !inDouble
                '\'' -> if (!inDouble) inSingle = !inSingle
                ',' -> if (!inDouble && !inSingle) {
                    return line.substring(0, i) to line.substring(i + 1)
                }
            }
        }
        return line to ""
    }
}