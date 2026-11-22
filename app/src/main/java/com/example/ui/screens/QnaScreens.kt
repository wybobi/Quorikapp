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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultQuorikData
import com.example.data.McqItem
import com.example.data.QnaItem
import com.example.ui.components.LocalFileImage
import com.example.ui.components.StarRatingBar
import com.example.ui.components.SubScreenTopBar
import com.example.ui.theme.*
import com.example.viewmodel.QnaSubMode
import com.example.viewmodel.QuorikUiState
import java.io.File

@Composable
fun QnaScreen(
    state: QuorikUiState,
    filteredMcqs: List<McqItem>,
    onBack: () -> Unit,
    onSetSubMode: (QnaSubMode) -> Unit,
    onSelectSemester: (String) -> Unit,
    onSelectSubject: (String) -> Unit,
    onSelectMarksFilter: (String) -> Unit,
    onOpenQnaDetail: (String) -> Unit,
    onCloseQnaDetail: () -> Unit,
    onRateQna: (String, Int) -> Unit,
    onDownloadAllQnaPdf: () -> Unit,
    onDownloadSingleQnaPdf: (QnaItem) -> Unit,
    onSetMcqSubjectFilter: (String) -> Unit,
    onSelectMcqOption: (String, Int) -> Unit,
    onToggleMcqFlag: (String) -> Unit,
    onToggleMcqGrid: () -> Unit,
    onJumpMcq: (Int) -> Unit
) {
    val activeDetail = state.activeQnaDetailId?.let { id -> state.db.qna.find { it.id == id } }

    val heading = when {
        state.qnaSubMode == QnaSubMode.MCQ -> "MCQ Quiz"
        activeDetail != null -> "Question & Answer"
        state.selectedQnaSubject != null -> state.selectedQnaSubject
        state.selectedQnaSemester != null -> state.selectedQnaSemester
        else -> "Question & Answer"
    }
    val stepBadge = when {
        state.qnaSubMode == QnaSubMode.MCQ -> "Practice"
        activeDetail != null -> activeDetail.marks
        state.selectedQnaSubject != null -> state.selectedQnaSemester ?: "Q&A"
        state.selectedQnaSemester != null -> "Select Subject"
        else -> "Select Semester"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuorikBackground)
    ) {
        SubScreenTopBar(
            title = heading,
            badgeText = stepBadge,
            onBack = onBack
        )

        // Mode Switcher Bar
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, QuorikBorder),
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = QuorikSurfaceVariant,
                    border = BorderStroke(1.dp, QuorikBorder)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        ModeTogglePill(
                            label = "Question & Bank",
                            selected = state.qnaSubMode == QnaSubMode.DESCRIPTIVE,
                            onClick = { onSetSubMode(QnaSubMode.DESCRIPTIVE) },
                            modifier = Modifier.testTag("qna_mode_descriptive")
                        )
                        ModeTogglePill(
                            label = "MCQ Quiz",
                            selected = state.qnaSubMode == QnaSubMode.MCQ,
                            onClick = { onSetSubMode(QnaSubMode.MCQ) },
                            modifier = Modifier.testTag("qna_mode_mcq")
                        )
                    }
                }
                if (state.qnaSubMode == QnaSubMode.DESCRIPTIVE) {
                    Button(
                        onClick = onDownloadAllQnaPdf,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "PDF", modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("PDF", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (state.qnaSubMode == QnaSubMode.MCQ) {
            McqQuizContent(
                state = state,
                mcqs = filteredMcqs,
                onSetSubjectFilter = onSetMcqSubjectFilter,
                onSelectOption = onSelectMcqOption,
                onToggleFlag = onToggleMcqFlag,
                onToggleGrid = onToggleMcqGrid,
                onJump = onJumpMcq
            )
            return
        }

        if (activeDetail != null) {
            QnaDetailContent(
                item = activeDetail,
                userRating = state.userPrefs.ratings[activeDetail.id],
                onBack = onCloseQnaDetail,
                onRate = { stars -> onRateQna(activeDetail.id, stars) },
                onDownloadPdf = { onDownloadSingleQnaPdf(activeDetail) }
            )
            return
        }

        // Step 1: 2-Column Numbered Semester Grid
        if (state.selectedQnaSemester == null) {
            val semesters = state.db.syllabusSemesters.ifEmpty { DefaultQuorikData.allSemesters }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val rows = semesters.chunked(2)
                items(rows) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEachIndexed { offset, sem ->
                            val globalIdx = semesters.indexOf(sem) + 1
                            val numStr = globalIdx.toString().padStart(2, '0')
                            val count = state.db.qna.count { it.semester == sem.name }
                            val isFeatured = count > 0

                            Surface(
                                onClick = { onSelectSemester(sem.name) },
                                shape = RoundedCornerShape(24.dp),
                                color = if (isFeatured) Color.Transparent else Color.White,
                                border = if (isFeatured) null else BorderStroke(1.dp, QuorikBorder),
                                shadowElevation = if (isFeatured) 6.dp else 2.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 148.dp)
                                    .testTag("qna_sem_${sem.id}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .then(
                                            if (isFeatured) Modifier.background(QuorikTileBrush) else Modifier
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = numStr,
                                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 36.sp),
                                                color = if (isFeatured) Color.White.copy(alpha = 0.28f) else QuorikBluePrimary.copy(alpha = 0.2f)
                                            )
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isFeatured) Color.White else QuorikBlueSoftBg
                                            ) {
                                                Text(
                                                    text = "$count Q&A",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = QuorikBluePrimary,
                                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(14.dp))
                                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Text(
                                                text = sem.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                color = if (isFeatured) Color.White else Slate900
                                            )
                                            Text(
                                                text = "${sem.name} Question & Answer Bank",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isFeatured) QuorikBlueTint else Slate500,
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        // Step 2: Subject List with All-Around #1E3B8B Blue Border
        else if (state.selectedQnaSubject == null) {
            val subjects = state.db.subjects.ifEmpty { DefaultQuorikData.allSubjects }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects, key = { it }) { subj ->
                    val count = state.db.qna.count {
                        it.semester == state.selectedQnaSemester && it.subject == subj
                    }
                    Surface(
                        onClick = { onSelectSubject(subj) },
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, QuorikBluePrimary),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 76.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(subj, style = MaterialTheme.typography.titleMedium, color = Slate900)
                                Text(
                                    text = "${state.selectedQnaSemester} • $subj Short & Long Q&A",
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
                                        text = "$count Q&A",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = QuorikBluePrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = QuorikBluePrimary)
                            }
                        }
                    }
                }
            }
        }
        // Step 3: Filtered Marks & Question Cards
        else {
            val marksPills = listOf("ALL", "Short Question", "Long Question", "1 Mark", "2 Marks", "5 Marks", "10 Marks")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                marksPills.forEach { mark ->
                    val active = state.selectedMarksFilter == mark
                    Surface(
                        onClick = { onSelectMarksFilter(mark) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (active) QuorikBluePrimary else Color.White,
                        border = if (active) null else BorderStroke(1.dp, QuorikBorder)
                    ) {
                        Text(
                            text = if (mark == "ALL") "All" else mark,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (active) Color.White else Slate700,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            val list = state.db.qna.filter { item ->
                item.semester == state.selectedQnaSemester &&
                    item.subject == state.selectedQnaSubject &&
                    when (state.selectedMarksFilter) {
                        "ALL" -> true
                        "Short Question" -> item.marks in listOf("1 Mark", "2 Marks", "Short Question")
                        "Long Question" -> item.marks in listOf("5 Marks", "10 Marks", "Long Question")
                        else -> item.marks == state.selectedMarksFilter
                    }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (list.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, QuorikBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No questions match this filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate500,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }
                } else {
                    items(list, key = { it.id }) { item ->
                        Surface(
                            onClick = { onOpenQnaDetail(item.id) },
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, QuorikBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = item.question,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Slate900,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.description.ifBlank { item.answer },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (item.imagePath.isNotBlank() && File(item.imagePath).exists()) {
                                    LocalFileImage(
                                        path = item.imagePath,
                                        contentDescription = item.question,
                                        modifier = Modifier
                                            .width(86.dp)
                                            .height(66.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .width(86.dp)
                                            .height(66.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(QuorikTileBrush)
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(item.marks, style = MaterialTheme.typography.labelMedium, color = Color.White, maxLines = 1)
                                            Text(
                                                text = item.subject,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = QuorikBlueTint,
                                                maxLines = 1,
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
        }
    }
}

@Composable
private fun ModeTogglePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Color.White else Color.Transparent,
        shadowElevation = if (selected) 1.dp else 0.dp,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = if (selected) QuorikBluePrimary else Slate600,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun QnaDetailContent(
    item: QnaItem,
    userRating: Int?,
    onBack: () -> Unit,
    onRate: (Int) -> Unit,
    onDownloadPdf: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = QuorikBluePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    onClick = onBack,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(15.dp))
                        Text("Back", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
                Text(
                    text = "${item.subject} • ${item.semester}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Button(
                    onClick = onDownloadPdf,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "PDF", modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("PDF", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(24.dp),
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
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = QuorikBlueSoftBg) {
                            Text(
                                text = item.marks,
                                style = MaterialTheme.typography.labelMedium,
                                color = QuorikBluePrimary,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = QuorikSurfaceVariant) {
                            Text(
                                text = item.subject,
                                style = MaterialTheme.typography.labelMedium,
                                color = Slate700,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                    StarRatingBar(item.id, item.rating, userRating, onRate)
                }

                Text(
                    text = item.question,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Slate900
                )

                if (item.imagePath.isNotBlank() && File(item.imagePath).exists()) {
                    LocalFileImage(
                        path = item.imagePath,
                        contentDescription = item.question,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "ANSWER",
                            style = MaterialTheme.typography.labelMedium,
                            color = Teal700
                        )
                        Text(
                            text = item.answer,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Slate900
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun McqQuizContent(
    state: QuorikUiState,
    mcqs: List<McqItem>,
    onSetSubjectFilter: (String) -> Unit,
    onSelectOption: (String, Int) -> Unit,
    onToggleFlag: (String) -> Unit,
    onToggleGrid: () -> Unit,
    onJump: (Int) -> Unit
) {
    if (mcqs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No MCQ questions available.", color = Slate500)
        }
        return
    }

    val safeIdx = state.mcqCurrentIdx.coerceIn(0, mcqs.lastIndex)
    val item = mcqs[safeIdx]
    val selectedIdx = state.mcqSelectedOptions[item.id]
    val isFlagged = state.mcqFlaggedIds.contains(item.id)
    val mins = (state.mcqSecondsLeft / 60).toString().padStart(2, '0')
    val secs = (state.mcqSecondsLeft % 60).toString().padStart(2, '0')
    val letters = listOf("A", "B", "C", "D")
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, QuorikBorder),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Timer + Subject Filter + Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = "Timer", tint = Slate500, modifier = Modifier.size(18.dp))
                        Text("$mins:$secs", style = MaterialTheme.typography.titleMedium, color = Slate900)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box {
                            Surface(
                                onClick = { dropdownExpanded = true },
                                shape = RoundedCornerShape(10.dp),
                                color = QuorikSurfaceVariant,
                                border = BorderStroke(1.dp, QuorikBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (state.mcqSubjectFilter == "ALL") "All Subjects" else state.mcqSubjectFilter,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Slate700
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                                }
                            }
                            DropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                                DropdownMenuItem(
                                    text = { Text("All Subjects") },
                                    onClick = { onSetSubjectFilter("ALL"); dropdownExpanded = false }
                                )
                                state.db.subjects.forEach { subj ->
                                    DropdownMenuItem(
                                        text = { Text(subj) },
                                        onClick = { onSetSubjectFilter(subj); dropdownExpanded = false }
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${safeIdx + 1} of ${mcqs.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Slate500
                        )
                    }
                }

                // Progress bar
                LinearProgressIndicator(
                    progress = { (safeIdx + 1).toFloat() / mcqs.size.toFloat() },
                    color = QuorikBluePrimary,
                    trackColor = QuorikSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                )

                // Question Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "QUESTION ${safeIdx + 1} • ${item.subject.uppercase()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = QuorikBluePrimary
                            )
                            if (isFlagged) {
                                Surface(shape = CircleShape, color = Amber100) {
                                    Text(
                                        text = "🚩 Flagged",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Amber800,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = item.question,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Slate900
                        )

                        item.options.forEachIndexed { idx, optText ->
                            val isSelected = selectedIdx == idx
                            val isCorrect = idx == item.correctIndex

                            val bg = when {
                                selectedIdx != null && isCorrect -> Teal50
                                selectedIdx != null && isSelected && !isCorrect -> Rose50
                                else -> QuorikSurfaceVariant
                            }
                            val border = when {
                                selectedIdx != null && isCorrect -> BorderStroke(2.dp, Teal600)
                                selectedIdx != null && isSelected && !isCorrect -> BorderStroke(2.dp, Rose600)
                                else -> BorderStroke(1.dp, QuorikBorder)
                            }
                            val badgeBg = when {
                                selectedIdx != null && isCorrect -> Teal700
                                selectedIdx != null && isSelected && !isCorrect -> Rose600
                                else -> Color.White
                            }
                            val badgeText = when {
                                selectedIdx != null && isCorrect -> "✓"
                                selectedIdx != null && isSelected && !isCorrect -> "✕"
                                else -> letters.getOrElse(idx) { "${idx + 1}" }
                            }
                            val badgeFg = if (selectedIdx != null && (isCorrect || isSelected)) Color.White else Slate700

                            Surface(
                                onClick = { onSelectOption(item.id, idx) },
                                shape = RoundedCornerShape(14.dp),
                                color = bg,
                                border = border,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mcq_option_$idx")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(badgeBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(badgeText, style = MaterialTheme.typography.labelMedium, color = badgeFg)
                                    }
                                    Text(
                                        text = optText,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = Slate900
                                    )
                                }
                            }
                        }

                        if (selectedIdx != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Emerald50,
                                border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = item.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Teal700,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // Grid Jump Palette
                if (state.mcqShowGridPalette) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = QuorikSurfaceVariant,
                        border = BorderStroke(1.dp, QuorikBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            mcqs.forEachIndexed { idx, _ ->
                                val active = idx == safeIdx
                                Surface(
                                    onClick = { onJump(idx) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (active) QuorikBluePrimary else Color.White,
                                    border = BorderStroke(1.dp, if (active) QuorikBluePrimary else QuorikBorder),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${idx + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (active) Color.White else Slate700
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onToggleFlag(item.id) },
                        shape = CircleShape,
                        border = BorderStroke(1.dp, QuorikBorder),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = "Flag", tint = if (isFlagged) Amber500 else Slate600, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(if (isFlagged) "Flagged" else "Flag", style = MaterialTheme.typography.labelMedium, color = Slate700)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            onClick = onToggleGrid,
                            border = BorderStroke(1.dp, QuorikBorder)
                        ) {
                            Icon(Icons.Default.GridView, contentDescription = "Question Grid", tint = Slate700, modifier = Modifier.size(18.dp))
                        }
                        OutlinedButton(
                            onClick = { onJump(safeIdx - 1) },
                            shape = CircleShape,
                            border = BorderStroke(1.dp, QuorikBorder),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("←", style = MaterialTheme.typography.labelLarge, color = Slate700)
                        }
                        Button(
                            onClick = { onJump(safeIdx + 1) },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Next", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
