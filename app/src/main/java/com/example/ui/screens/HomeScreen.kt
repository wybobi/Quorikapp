package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BannerSlide
import com.example.ui.components.FeedPostRow
import com.example.ui.components.LocalFileImage
import com.example.ui.components.mapIconName
import com.example.ui.theme.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.QuorikUiState
import java.io.File

@Composable
fun HomeScreen(
    state: QuorikUiState,
    onOpenDrawer: () -> Unit,
    onBrandTap: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onSearchChange: (String) -> Unit,
    onQuickFilterClick: () -> Unit,
    onBannerSelect: (Int) -> Unit,
    onBannerAction: (BannerSlide) -> Unit,
    onSelectTab: (AppTab) -> Unit,
    onOpenNote: (String) -> Unit,
    onOpenQna: (String) -> Unit,
    onOpenSyllabus: (String, String) -> Unit,
    onOpenEventDetail: (String, String, String, String, List<String>) -> Unit
) {
    val db = state.db
    val query = state.searchQuery.trim().lowercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuorikBackground)
    ) {
        // Unified Theme-Colored Top Header
        HomeTopHeader(
            bookmarkCount = state.userPrefs.bookmarks.size,
            searchQuery = state.searchQuery,
            onOpenDrawer = onOpenDrawer,
            onBrandTap = onBrandTap,
            onOpenBookmarks = onOpenBookmarks,
            onSearchChange = onSearchChange,
            onQuickFilterClick = onQuickFilterClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // If searching, show instant live search results at the top!
            if (query.isNotEmpty()) {
                val matchedNotes = db.notes.filter {
                    "${it.title} ${it.paperCode} ${it.subject} ${it.semester} ${it.summary}".lowercase().contains(query)
                }
                val matchedQna = db.qna.filter {
                    "${it.question} ${it.answer} ${it.subject} ${it.marks}".lowercase().contains(query)
                }
                val matchedSyllabus = db.syllabus.filter {
                    "${it.title} ${it.paperCode} ${it.subject} ${it.semester}".lowercase().contains(query)
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Search Results (${matchedNotes.size + matchedQna.size + matchedSyllabus.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate900
                        )
                        if (matchedNotes.isEmpty() && matchedQna.isEmpty() && matchedSyllabus.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, QuorikBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No matching notes, questions, or syllabus found for \"${state.searchQuery}\".",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate500,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        } else {
                            matchedNotes.forEach { n ->
                                FeedPostRow(
                                    categoryLabel = "Notes • ${n.subject}",
                                    dateStr = n.date,
                                    title = n.title,
                                    desc = n.summary,
                                    icon = Icons.Default.MenuBook,
                                    onClick = { onOpenNote(n.id) }
                                )
                            }
                            matchedQna.forEach { q ->
                                FeedPostRow(
                                    categoryLabel = "${q.marks} • ${q.subject}",
                                    dateStr = q.date,
                                    title = q.question,
                                    desc = q.answer,
                                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                                    onClick = { onOpenQna(q.id) }
                                )
                            }
                            matchedSyllabus.forEach { s ->
                                FeedPostRow(
                                    categoryLabel = "Syllabus • ${s.semester}",
                                    dateStr = s.date,
                                    title = "${s.paperCode}: ${s.title}",
                                    desc = "${s.semester} • ${s.subject}",
                                    icon = Icons.Default.School,
                                    onClick = { onOpenSyllabus(s.semester, s.subject) }
                                )
                            }
                        }
                    }
                }
            } else {
                // 1. Auto-Sliding Hero Banner + Dots Below
                item {
                    val banners = db.banners
                    if (banners.isNotEmpty()) {
                        val safeIdx = state.bannerIndex.coerceIn(0, banners.lastIndex)
                        val activeBanner = banners[safeIdx]
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnimatedContent(
                                targetState = activeBanner,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "banner_slide"
                            ) { banner ->
                                if (banner.imagePath.isNotBlank() && File(banner.imagePath).exists()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(2.4f)
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(Slate900)
                                            .clickable { onBannerAction(banner) }
                                            .testTag("home_banner_card")
                                    ) {
                                        LocalFileImage(
                                            path = banner.imagePath,
                                            contentDescription = banner.title,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 142.dp)
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(gradientBrushForKey(banner.gradientKey))
                                            .clickable { onBannerAction(banner) }
                                            .padding(16.dp)
                                            .testTag("home_banner_card")
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color.White.copy(alpha = 0.15f))
                                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = mapIconName(banner.icon),
                                                contentDescription = banner.title,
                                                tint = Amber300,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color.White.copy(alpha = 0.16f)
                                            ) {
                                                Text(
                                                    text = banner.badge.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Amber200,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = banner.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                color = Color.White,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = banner.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = QuorikBlueTint,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Surface(
                                            onClick = { onBannerAction(banner) },
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color.White,
                                            shadowElevation = 2.dp
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Text(
                                                    text = "Open",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = Slate900
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.NorthEast,
                                                    contentDescription = "Open",
                                                    tint = QuorikBluePrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        }
                                    }
                                }
                            }

                            // Indicator Dots
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                banners.forEachIndexed { idx, _ ->
                                    Box(
                                        modifier = Modifier
                                            .height(6.dp)
                                            .width(if (idx == safeIdx) 22.dp else 7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (idx == safeIdx) QuorikBluePrimary else Slate400.copy(alpha = 0.45f)
                                            )
                                            .clickable { onBannerSelect(idx) }
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. 6 Main Services in 2-Column Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ServiceGridCard(
                                title = "Notes & PDF",
                                subtitle = "${db.notes.size} Study PDFs",
                                icon = Icons.Default.Description,
                                modifier = Modifier.weight(1f).testTag("service_notes"),
                                onClick = { onSelectTab(AppTab.NOTES) }
                            )
                            ServiceGridCard(
                                title = "Question & Bank",
                                subtitle = "${db.qna.size} Solved Q&As",
                                icon = Icons.AutoMirrored.Filled.HelpOutline,
                                modifier = Modifier.weight(1f).testTag("service_qna"),
                                onClick = { onSelectTab(AppTab.QNA) }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ServiceGridCard(
                                title = "Syllabus",
                                subtitle = "${db.syllabus.size} Official Papers",
                                icon = Icons.Default.School,
                                modifier = Modifier.weight(1f).testTag("service_syllabus"),
                                onClick = { onSelectTab(AppTab.SYLLABUS) }
                            )
                            ServiceGridCard(
                                title = "Exam Notice",
                                subtitle = "${db.notices.size} Live Updates",
                                icon = Icons.Default.Notifications,
                                modifier = Modifier.weight(1f).testTag("service_notices"),
                                onClick = { onSelectTab(AppTab.NOTICES) }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ServiceGridCard(
                                title = "Festivals",
                                subtitle = "Assam & Cultural",
                                icon = Icons.Default.AutoAwesome,
                                modifier = Modifier.weight(1f).testTag("service_festivals"),
                                onClick = { onSelectTab(AppTab.FESTIVALS) }
                            )
                            ServiceGridCard(
                                title = "Holidays",
                                subtitle = "Academic Calendar",
                                icon = Icons.Default.CalendarMonth,
                                modifier = Modifier.weight(1f).testTag("service_holidays"),
                                onClick = { onSelectTab(AppTab.HOLIDAYS) }
                            )
                        }
                    }
                }

                // 3. Recently Uploaded Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recently Uploaded",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Slate900
                        )
                        Text(
                            text = "Latest Updates",
                            style = MaterialTheme.typography.labelMedium,
                            color = QuorikBluePrimary
                        )
                    }
                }

                // Notes (2 posts)
                if (db.notes.isNotEmpty()) {
                    item {
                        FeedSectionBlock(
                            sectionTitle = "Notes",
                            onViewAll = { onSelectTab(AppTab.NOTES) }
                        ) {
                            db.notes.take(2).forEach { n ->
                                FeedPostRow(
                                    categoryLabel = "Notes",
                                    dateStr = n.date,
                                    title = n.title,
                                    desc = n.summary,
                                    icon = Icons.Default.MenuBook,
                                    onClick = { onOpenNote(n.id) }
                                )
                            }
                        }
                    }
                }

                // Question & Answer (2 posts)
                if (db.qna.isNotEmpty()) {
                    item {
                        FeedSectionBlock(
                            sectionTitle = "Question & Answer",
                            onViewAll = { onSelectTab(AppTab.QNA) }
                        ) {
                            db.qna.take(2).forEach { q ->
                                FeedPostRow(
                                    categoryLabel = q.marks,
                                    dateStr = q.date,
                                    title = q.question,
                                    desc = q.answer,
                                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                                    onClick = { onOpenQna(q.id) }
                                )
                            }
                        }
                    }
                }

                // Syllabus (2 posts)
                if (db.syllabus.isNotEmpty()) {
                    item {
                        FeedSectionBlock(
                            sectionTitle = "Syllabus",
                            onViewAll = { onSelectTab(AppTab.SYLLABUS) }
                        ) {
                            db.syllabus.take(2).forEach { s ->
                                FeedPostRow(
                                    categoryLabel = "Syllabus",
                                    dateStr = s.date,
                                    title = "${s.paperCode}: ${s.title}",
                                    desc = "${s.semester} • ${s.subject}",
                                    icon = Icons.Default.School,
                                    onClick = { onOpenSyllabus(s.semester, s.subject) }
                                )
                            }
                        }
                    }
                }

                // Exam Notices (2 posts)
                if (db.notices.isNotEmpty()) {
                    item {
                        FeedSectionBlock(
                            sectionTitle = "Exam Notices",
                            onViewAll = { onSelectTab(AppTab.NOTICES) }
                        ) {
                            db.notices.take(2).forEach { n ->
                                FeedPostRow(
                                    categoryLabel = "Exam Notice",
                                    dateStr = n.date,
                                    title = n.title,
                                    desc = n.description,
                                    icon = Icons.Default.NotificationsActive,
                                    onClick = { onSelectTab(AppTab.NOTICES) }
                                )
                            }
                        }
                    }
                }

                // Festivals (2 posts)
                if (db.festivals.isNotEmpty()) {
                    item {
                        FeedSectionBlock(
                            sectionTitle = "Festivals",
                            onViewAll = { onSelectTab(AppTab.FESTIVALS) }
                        ) {
                            db.festivals.take(2).forEach { f ->
                                FeedPostRow(
                                    categoryLabel = "Festivals",
                                    dateStr = f.date,
                                    title = f.title,
                                    desc = f.description,
                                    icon = Icons.Default.AutoAwesome,
                                    onClick = { onOpenEventDetail(f.title, "Date: ${f.date}", f.description, f.imagePath, f.extraImages) }
                                )
                            }
                        }
                    }
                }

                // Holidays (2 posts)
                if (db.holidays.isNotEmpty()) {
                    item {
                        FeedSectionBlock(
                            sectionTitle = "Holidays",
                            onViewAll = { onSelectTab(AppTab.HOLIDAYS) }
                        ) {
                            db.holidays.take(2).forEach { h ->
                                FeedPostRow(
                                    categoryLabel = "Holidays",
                                    dateStr = h.date,
                                    title = h.title,
                                    desc = h.description,
                                    icon = Icons.Default.CalendarMonth,
                                    onClick = { onOpenEventDetail(h.title, "Date: ${h.date}", h.description, h.imagePath, h.extraImages) }
                                )
                            }
                        }
                    }
                }

                // 4. Most Popular Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Most Popular",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Slate900
                            )
                            Text(
                                text = "Explore All →",
                                style = MaterialTheme.typography.labelLarge,
                                color = QuorikBluePrimary,
                                modifier = Modifier.clickable { onSelectTab(AppTab.QNA) }
                            )
                        }
                        db.notes.firstOrNull()?.let { n ->
                            FeedPostRow("Popular Notes", n.date, n.title, n.summary, Icons.Default.MenuBook) {
                                onOpenNote(n.id)
                            }
                        }
                        db.qna.firstOrNull()?.let { q ->
                            FeedPostRow("Popular Q&A", q.date, q.question, q.answer, Icons.AutoMirrored.Filled.HelpOutline) {
                                onOpenQna(q.id)
                            }
                        }
                        db.syllabus.firstOrNull()?.let { s ->
                            FeedPostRow("Popular Syllabus", s.date, "${s.paperCode}: ${s.title}", "${s.semester} • ${s.subject}", Icons.Default.School) {
                                onOpenSyllabus(s.semester, s.subject)
                            }
                        }
                        db.notices.firstOrNull()?.let { nt ->
                            FeedPostRow("Popular Exam Notice", nt.date, nt.title, nt.description, Icons.Default.NotificationsActive) {
                                onSelectTab(AppTab.NOTICES)
                            }
                        }
                        db.festivals.firstOrNull()?.let { f ->
                            FeedPostRow("Popular Festival", f.date, f.title, f.description, Icons.Default.AutoAwesome) {
                                onOpenEventDetail(f.title, "Date: ${f.date}", f.description, f.imagePath, f.extraImages)
                            }
                        }
                        db.holidays.firstOrNull()?.let { h ->
                            FeedPostRow("Popular Holiday", h.date, h.title, h.description, Icons.Default.CalendarMonth) {
                                onOpenEventDetail(h.title, "Date: ${h.date}", h.description, h.imagePath, h.extraImages)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopHeader(
    bookmarkCount: Int,
    searchQuery: String,
    onOpenDrawer: () -> Unit,
    onBrandTap: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onSearchChange: (String) -> Unit,
    onQuickFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(QuorikHeaderBrush)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("open_drawer_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Sidebar Menu",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = "Quorik",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                    color = Color.White,
                    modifier = Modifier
                        .clickable { onBrandTap() }
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .testTag("brand_logo_trigger")
                )
            }

            // Saved Bookmarks Icon + Count Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onOpenBookmarks() }
                    .testTag("saved_bookmarks_btn"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.14f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Saved Items",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Amber400),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bookmarkCount.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Slate950
                    )
                }
            }
        }

        // Full-Width Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.14f))
                .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
                .padding(start = 12.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = QuorikBlueTint,
                modifier = Modifier.size(19.dp)
            )
            Box(modifier = Modifier.weight(1f)) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search notes, PDF, Question & Bank, syllabus...",
                        style = MaterialTheme.typography.bodySmall,
                        color = QuorikBlueTint.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    ),
                    cursorBrush = SolidColor(Amber300),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input")
                )
            }
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "Clear",
                    style = MaterialTheme.typography.labelMedium,
                    color = Amber200,
                    modifier = Modifier
                        .clickable { onSearchChange("") }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(QuorikBluePrimary)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .clickable { onQuickFilterClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Explore Notes",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ServiceGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, QuorikBluePrimary.copy(alpha = 0.28f)),
        shadowElevation = 3.dp,
        modifier = modifier.heightIn(min = 78.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(QuorikTileBrush),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Slate900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FeedSectionBlock(
    sectionTitle: String,
    onViewAll: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = Slate900
            )
            Text(
                text = "View All →",
                style = MaterialTheme.typography.labelLarge,
                color = QuorikBluePrimary,
                modifier = Modifier.clickable { onViewAll() }
            )
        }
        content()
    }
}
