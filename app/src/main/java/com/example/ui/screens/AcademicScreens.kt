package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.LocalFileImage
import com.example.ui.components.PageBannerCard
import com.example.ui.components.StarRatingBar
import com.example.ui.components.SubScreenTopBar
import com.example.ui.theme.*
import com.example.viewmodel.QuorikUiState
import java.io.File

// ==================== SYLLABUS SCREEN ====================
@Composable
fun SyllabusScreen(
    state: QuorikUiState,
    onBack: () -> Unit,
    onSelectSemester: (String) -> Unit,
    onSelectSubject: (String) -> Unit,
    onRateSyllabus: (String, Int) -> Unit,
    onOpenSyllabusPdf: (SyllabusItem) -> Unit
) {
    val heading = when {
        state.selectedSyllabusSubject != null -> state.selectedSyllabusSubject
        state.selectedSyllabusSemester != null -> state.selectedSyllabusSemester
        else -> "Syllabus"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuorikBackground)
    ) {
        SubScreenTopBar(
            title = heading,
            badgeText = "Semesters 1–6",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.db.pageBanners["syllabus"]?.let { banner ->
                item { PageBannerCard(banner) }
            }

            // Step 1: Semester List
            if (state.selectedSyllabusSemester == null) {
                val sems = state.db.syllabusSemesters.ifEmpty { DefaultQuorikData.allSemesters }
                items(sems, key = { it.id }) { sem ->
                    val count = state.db.syllabus.count { it.semester == sem.name }
                    Surface(
                        onClick = { onSelectSemester(sem.name) },
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, QuorikBorder),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 78.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(QuorikTileBrush),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.School, contentDescription = sem.name, tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(sem.name, style = MaterialTheme.typography.titleMedium, color = Slate900)
                                    Text(
                                        text = sem.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(shape = RoundedCornerShape(10.dp), color = QuorikBlueSoftBg) {
                                    Text(
                                        text = "$count Papers",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = QuorikBluePrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
                            }
                        }
                    }
                }
            }
            // Step 2: Subject List with Signature Left & Right #1E3B8B Accent Borders
            else if (state.selectedSyllabusSubject == null) {
                val subjects = state.db.subjects.ifEmpty { DefaultQuorikData.allSubjects }
                items(subjects, key = { it }) { subj ->
                    val count = state.db.syllabus.count {
                        it.semester == state.selectedSyllabusSemester && it.subject == subj
                    }
                    Surface(
                        onClick = { onSelectSubject(subj) },
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, QuorikBorder),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 76.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .fillMaxHeight()
                                    .background(QuorikBluePrimary)
                            )
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(subj, style = MaterialTheme.typography.titleMedium, color = Slate900)
                                    Text(
                                        text = "${state.selectedSyllabusSemester} • $subj Syllabus",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(shape = RoundedCornerShape(10.dp), color = QuorikBlueSoftBg) {
                                        Text(
                                            text = "$count Papers",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = QuorikBluePrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = QuorikBluePrimary)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .fillMaxHeight()
                                    .background(QuorikBluePrimary)
                            )
                        }
                    }
                }
            }
            // Step 3: Syllabus Paper Cards
            else {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, QuorikBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.selectedSyllabusSemester,
                                style = MaterialTheme.typography.titleSmall,
                                color = QuorikBluePrimary
                            )
                            Surface(shape = RoundedCornerShape(10.dp), color = QuorikBlueSoftBg) {
                                Text(
                                    text = state.selectedSyllabusSubject,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = QuorikBluePrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                val papers = state.db.syllabus.filter {
                    it.semester == state.selectedSyllabusSemester && it.subject == state.selectedSyllabusSubject
                }

                if (papers.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, QuorikBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No syllabus uploaded yet for ${state.selectedSyllabusSemester} • ${state.selectedSyllabusSubject}.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate500,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }
                } else {
                    items(papers, key = { it.id }) { syl ->
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, QuorikBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(shape = CircleShape, color = QuorikBlueSoftBg) {
                                        Text(
                                            text = "${syl.paperCode} • ${syl.subject}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = QuorikBluePrimary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    StarRatingBar(
                                        itemId = syl.id,
                                        defaultAvg = 4.9,
                                        userRating = state.userPrefs.ratings[syl.id],
                                        onRate = { onRateSyllabus(syl.id, it) }
                                    )
                                }
                                Text(
                                    text = syl.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Slate900
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    syl.units.forEach { unit ->
                                        Text(
                                            text = "• $unit",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate600
                                        )
                                    }
                                }
                                HorizontalDivider(color = QuorikSurfaceVariant)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${syl.semester} • ${syl.credits}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Slate400
                                    )
                                    Button(
                                        onClick = { onOpenSyllabusPdf(syl) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = "Open PDF", modifier = Modifier.size(15.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Open PDF", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== EXAM NOTICES SCREEN ====================
@Composable
fun NoticesScreen(
    state: QuorikUiState,
    onBack: () -> Unit,
    onSelectCategory: (String) -> Unit,
    onOpenDetail: (ExamNotice) -> Unit,
    onDownloadNoticePdf: (ExamNotice) -> Unit
) {
    val categories = listOf("ALL", "Exam Routine", "Form Fill-up", "Result / Circular")
    val notices = state.db.notices.filter {
        state.selectedNoticeCategory == "ALL" || it.category == state.selectedNoticeCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuorikBackground)
    ) {
        SubScreenTopBar(
            title = "Exam Notices",
            badgeText = "Updates & Routines",
            badgeBg = Amber50,
            badgeTextColor = Amber800,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.db.pageBanners["notices"]?.let { banner ->
                item { PageBannerCard(banner) }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val active = state.selectedNoticeCategory == cat
                            Surface(
                                onClick = { onSelectCategory(cat) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (active) Slate900 else QuorikSurfaceVariant
                            ) {
                                Text(
                                    text = if (cat == "ALL") "All" else cat,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (active) Color.White else Slate700,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

            items(notices, key = { it.id }) { notice ->
                Surface(
                    onClick = { onOpenDetail(notice) },
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, QuorikBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Facebook-style Post Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(QuorikBluePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Q", style = MaterialTheme.typography.labelLarge, color = Color.White)
                                }
                                Column {
                                    Text(
                                        text = notice.authority,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Slate900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${notice.date} • ${notice.category}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400
                                    )
                                }
                            }
                            Button(
                                onClick = { onDownloadNoticePdf(notice) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("Open PDF", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        // Hero Banner or Uploaded Notice Photo
                        if (notice.imagePath.isNotBlank() && File(notice.imagePath).exists()) {
                            LocalFileImage(
                                path = notice.imagePath,
                                contentDescription = notice.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(145.dp)
                                    .background(QuorikTileBrush)
                                    .padding(18.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.2f)) {
                                        Text(
                                            text = notice.category.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Amber200,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notice.authority,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = Amber300,
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Post Body
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = notice.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Slate900
                            )
                            Text(
                                text = notice.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate600
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== FESTIVALS & HOLIDAYS SCREENS ====================
@Composable
fun EventsCalendarScreen(
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
    banner: PageBanner?,
    events: List<CalendarEvent>,
    isFestival: Boolean,
    onBack: () -> Unit,
    onOpenEvent: (CalendarEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuorikBackground)
    ) {
        SubScreenTopBar(
            title = title,
            badgeText = badgeText,
            badgeBg = badgeBg,
            badgeTextColor = badgeTextColor,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (banner != null) {
                item { PageBannerCard(banner) }
            }
            items(events, key = { it.id }) { ev ->
                Surface(
                    onClick = { onOpenEvent(ev) },
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, QuorikBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .background(gradientBrushForKey(if (isFestival) "blue" else "teal"))
                        ) {
                            if (ev.imagePath.isNotBlank() && File(ev.imagePath).exists()) {
                                LocalFileImage(
                                    path = ev.imagePath,
                                    contentDescription = ev.title,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFestival) Icons.Default.AutoAwesome else Icons.Default.CalendarMonth,
                                        contentDescription = ev.title,
                                        tint = Amber300,
                                        modifier = Modifier.size(38.dp)
                                    )
                                    Text(
                                        text = ev.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Slate950.copy(alpha = 0.78f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = ev.date,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(QuorikBluePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isFestival) Icons.Default.AutoAwesome else Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = ev.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Slate900
                                )
                                Text(
                                    text = ev.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate500,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== CUSTOM SIDEBAR PAGE VIEWER ====================
@Composable
fun CustomPageScreen(
    page: CustomPage,
    onBack: () -> Unit,
    onCopyLink: (String) -> Unit,
    onOpenLinkedUrl: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuorikBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.dp, QuorikBorder),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            onClick = onBack,
                            shape = RoundedCornerShape(12.dp),
                            color = QuorikSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate700, modifier = Modifier.size(18.dp))
                            }
                        }
                        Column {
                            Text(page.title, style = MaterialTheme.typography.titleLarge, color = Slate900)
                            Text("#/page/${page.slug}", style = MaterialTheme.typography.labelSmall, color = Slate400)
                        }
                    }

                    if (page.externalUrl.isNotBlank()) {
                        Button(
                            onClick = { onOpenLinkedUrl(page.externalUrl) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Open Link", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    } else {
                        Surface(
                            onClick = { onCopyLink("#/page/${page.slug}") },
                            shape = RoundedCornerShape(10.dp),
                            color = QuorikSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate700, modifier = Modifier.size(14.dp))
                                Text("Copy Link", style = MaterialTheme.typography.labelMedium, color = Slate700)
                            }
                        }
                    }
                }

                HorizontalDivider(color = QuorikSurfaceVariant)

                Text(
                    text = page.content,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                    color = Slate700
                )
            }
        }
    }
}
