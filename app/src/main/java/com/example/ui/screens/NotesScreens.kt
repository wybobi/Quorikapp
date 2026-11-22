package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultQuorikData
import com.example.data.StudyNote
import com.example.data.StudyTopic
import com.example.ui.components.PageBannerCard
import com.example.ui.components.StarRatingBar
import com.example.ui.components.SubScreenTopBar
import com.example.ui.theme.*
import com.example.viewmodel.HighlightMode
import com.example.viewmodel.QuorikUiState

@Composable
fun NotesScreen(
    state: QuorikUiState,
    isReadingAloud: Boolean,
    onBack: () -> Unit,
    onSelectSemester: (String) -> Unit,
    onSelectSubject: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onOpenReader: (String) -> Unit,
    onCloseReader: () -> Unit,
    onSelectTopic: (Int) -> Unit,
    onToggleHighlight: (HighlightMode) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleAudio: (String) -> Unit,
    onToggleMarginPanel: () -> Unit,
    onAddMarginNote: (String, Int, String) -> Unit,
    onDeleteMarginNote: (String, Int, Int) -> Unit,
    onRateNote: (String, Int) -> Unit,
    onDownloadNotePdf: (StudyNote) -> Unit
) {
    val activeNote = state.activeReaderNoteId?.let { id ->
        state.db.notes.find { it.id == id }
    }

    if (activeNote != null) {
        StudyReaderScreen(
            note = activeNote,
            topicIdx = state.activeReaderTopicIdx,
            highlightMode = state.readerHighlightMode,
            isBookmarked = state.userPrefs.bookmarks.contains(activeNote.id),
            userRating = state.userPrefs.ratings[activeNote.id],
            isReadingAloud = isReadingAloud,
            showMarginPanel = state.showReaderMarginPanel,
            marginNotes = state.userPrefs.marginNotes["${activeNote.id}_${state.activeReaderTopicIdx}"] ?: emptyList(),
            onClose = onCloseReader,
            onSelectTopic = onSelectTopic,
            onToggleHighlight = onToggleHighlight,
            onToggleBookmark = { onToggleBookmark(activeNote.id) },
            onToggleAudio = onToggleAudio,
            onToggleMarginPanel = onToggleMarginPanel,
            onAddMarginNote = { txt -> onAddMarginNote(activeNote.id, state.activeReaderTopicIdx, txt) },
            onDeleteMarginNote = { idx -> onDeleteMarginNote(activeNote.id, state.activeReaderTopicIdx, idx) },
            onRate = { stars -> onRateNote(activeNote.id, stars) },
            onDownloadPdf = { onDownloadNotePdf(activeNote) }
        )
        return
    }

    val heading = when {
        state.selectedNotesSubject != null -> state.selectedNotesSubject
        state.selectedNotesSemester != null -> state.selectedNotesSemester
        else -> "Notes"
    }
    val stepBadge = when {
        state.selectedNotesSubject != null -> state.selectedNotesSemester ?: "Notes"
        state.selectedNotesSemester != null -> "Select Subject"
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.db.pageBanners["notes"]?.let { banner ->
                item { PageBannerCard(banner) }
            }

            // Step 1: Semester List
            if (state.selectedNotesSemester == null) {
                val semesters = state.db.syllabusSemesters.ifEmpty { DefaultQuorikData.allSemesters }
                items(semesters, key = { it.id }) { sem ->
                    val count = state.db.notes.count { it.semester == sem.name }
                    Surface(
                        onClick = { onSelectSemester(sem.name) },
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, QuorikBorder),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 78.dp)
                            .testTag("notes_sem_${sem.id}")
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
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = sem.name,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
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
                                        text = "$count Notes",
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
            // Step 2: Subject List
            else if (state.selectedNotesSubject == null) {
                val subjects = state.db.subjects.ifEmpty { DefaultQuorikData.allSubjects }
                items(subjects, key = { it }) { subj ->
                    val count = state.db.notes.count {
                        it.semester == state.selectedNotesSemester && it.subject == subj
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
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = subj,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(subj, style = MaterialTheme.typography.titleMedium, color = Slate900)
                                    Text(
                                        text = "${state.selectedNotesSemester} • $subj Study Notes",
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
                                        text = "$count Notes",
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
            // Step 3: Notes List
            else {
                item {
                    OutlinedTextField(
                        value = state.notesSearchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search notes in this subject...", fontSize = 13.sp) },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                val q = state.notesSearchQuery.trim().lowercase()
                val filtered = state.db.notes.filter {
                    it.semester == state.selectedNotesSemester &&
                        it.subject == state.selectedNotesSubject &&
                        (q.isEmpty() || "${it.title} ${it.paperCode} ${it.summary}".lowercase().contains(q))
                }

                if (filtered.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, QuorikBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No notes uploaded yet for ${state.selectedNotesSemester} • ${state.selectedNotesSubject}.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate500,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { note ->
                        Surface(
                            onClick = { onOpenReader(note.id) },
                            shape = RoundedCornerShape(20.dp),
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
                                        text = note.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Slate900,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = note.summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val levelPart = if (note.level.isNotBlank()) " • ${note.level}" else ""
                                    Text(
                                        text = "${note.paperCode} • ${note.medium}$levelPart",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = QuorikBluePrimary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(68.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(QuorikTileBrush)
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = note.subject.uppercase(),
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

@Composable
private fun StudyReaderScreen(
    note: StudyNote,
    topicIdx: Int,
    highlightMode: HighlightMode,
    isBookmarked: Boolean,
    userRating: Int?,
    isReadingAloud: Boolean,
    showMarginPanel: Boolean,
    marginNotes: List<String>,
    onClose: () -> Unit,
    onSelectTopic: (Int) -> Unit,
    onToggleHighlight: (HighlightMode) -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleAudio: (String) -> Unit,
    onToggleMarginPanel: () -> Unit,
    onAddMarginNote: (String) -> Unit,
    onDeleteMarginNote: (Int) -> Unit,
    onRate: (Int) -> Unit,
    onDownloadPdf: () -> Unit
) {
    val topics = note.topics.ifEmpty {
        listOf(StudyTopic(note.title, note.summary, note.summary))
    }
    val safeIdx = topicIdx.coerceIn(0, topics.lastIndex)
    val currentTopic = topics[safeIdx]
    var marginInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuorikBackground)
    ) {
        // Reader Top Bar
        Surface(
            color = QuorikBluePrimary,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onClose,
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.14f),
                    modifier = Modifier.testTag("reader_back_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Back", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${note.paperCode} • ${note.semester}".uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF5EEAD4),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onDownloadPdf,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("reader_pdf_btn")
                ) {
                    Icon(Icons.Default.Download, contentDescription = "PDF", modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("PDF", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Scrollable Reader Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Syllabus Units & Key Point Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, QuorikBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Syllabus Units", style = MaterialTheme.typography.titleSmall, color = Slate900)
                        Text("Unit ${safeIdx + 1} of ${topics.size}", style = MaterialTheme.typography.labelMedium, color = Slate400)
                    }
                    topics.forEachIndexed { idx, topic ->
                        val active = idx == safeIdx
                        Surface(
                            onClick = { onSelectTopic(idx) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) QuorikBlueSoftBg else QuorikBackground,
                            border = if (active) BorderStroke(1.dp, QuorikBluePrimary.copy(alpha = 0.35f)) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (active) QuorikBluePrimary else Slate600,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                            )
                        }
                    }

                    // Key Point Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Indigo50,
                        border = BorderStroke(1.dp, Indigo600.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Indigo700, modifier = Modifier.size(15.dp))
                                Text("Key Point", style = MaterialTheme.typography.labelLarge, color = Indigo700)
                            }
                            Text(
                                text = currentTopic.aiTakeaway.ifBlank { note.summary },
                                style = MaterialTheme.typography.bodySmall,
                                color = Indigo700.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Main Reading Sheet Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, QuorikBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${note.medium} Medium",
                            style = MaterialTheme.typography.labelMedium,
                            color = Slate500
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            topics.forEachIndexed { idx, _ ->
                                Box(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .width(if (idx == safeIdx) 18.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(if (idx == safeIdx) QuorikBluePrimary else QuorikBorder)
                                )
                            }
                        }
                        StarRatingBar(
                            itemId = note.id,
                            defaultAvg = note.rating,
                            userRating = userRating,
                            onRate = onRate
                        )
                    }

                    HorizontalDivider(color = QuorikSurfaceVariant)

                    Text(
                        text = currentTopic.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Slate900
                    )

                    val styledContent = remember(currentTopic.content, highlightMode) {
                        buildHighlightedStudyText(currentTopic.content, highlightMode)
                    }
                    Text(
                        text = styledContent,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 25.sp),
                        color = Slate900
                    )

                    // Collapsible Personal Margin Notes Panel
                    if (showMarginPanel) {
                        HorizontalDivider(color = QuorikBorder)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Personal Margin Notes", style = MaterialTheme.typography.labelLarge, color = Slate700)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = marginInput,
                                    onValueChange = { marginInput = it },
                                    placeholder = { Text("Write personal margin note...", fontSize = 12.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (marginInput.isNotBlank()) {
                                            onAddMarginNote(marginInput)
                                            marginInput = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Slate900)
                                ) {
                                    Text("Save", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                            marginNotes.forEachIndexed { idx, text ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Amber50,
                                    border = BorderStroke(1.dp, Amber200),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📌 $text",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Amber800,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete note",
                                            tint = Rose600,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clickable { onDeleteMarginNote(idx) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Interactive Study Tools Bar
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(0.5.dp, QuorikBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReaderToolChip(
                    label = "Highlight",
                    dotColor = Amber400,
                    active = highlightMode == HighlightMode.YELLOW,
                    bgColor = Amber100,
                    contentColor = Amber800,
                    onClick = { onToggleHighlight(HighlightMode.YELLOW) }
                )
                ReaderToolChip(
                    label = "Green",
                    dotColor = Emerald500,
                    active = highlightMode == HighlightMode.GREEN,
                    bgColor = Emerald50,
                    contentColor = Teal700,
                    onClick = { onToggleHighlight(HighlightMode.GREEN) }
                )
                ReaderIconToolChip(
                    label = if (isBookmarked) "Saved ✓" else "Save",
                    icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    active = isBookmarked,
                    onClick = onToggleBookmark
                )
                ReaderIconToolChip(
                    label = if (isReadingAloud) "Stop ■" else "Audio",
                    icon = if (isReadingAloud) Icons.Default.StopCircle else Icons.Default.Headphones,
                    active = isReadingAloud,
                    onClick = { onToggleAudio(currentTopic.content) }
                )
                ReaderIconToolChip(
                    label = "Note",
                    icon = Icons.Default.EditNote,
                    active = showMarginPanel,
                    onClick = onToggleMarginPanel
                )
            }
        }
    }
}

@Composable
private fun ReaderToolChip(
    label: String,
    dotColor: Color,
    active: Boolean,
    bgColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = if (active) BorderStroke(1.5.dp, contentColor) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(label, style = MaterialTheme.typography.labelMedium, color = contentColor)
        }
    }
}

@Composable
private fun ReaderIconToolChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (active) QuorikBlueSoftBg else Color.White,
        border = BorderStroke(1.dp, if (active) QuorikBluePrimary else QuorikBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(icon, contentDescription = label, tint = if (active) QuorikBluePrimary else Slate700, modifier = Modifier.size(15.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = if (active) QuorikBluePrimary else Slate700)
        }
    }
}

private fun buildHighlightedStudyText(content: String, mode: HighlightMode) = buildAnnotatedString {
    val hlColor = when (mode) {
        HighlightMode.YELLOW -> HighlightYellow
        HighlightMode.GREEN -> HighlightGreen
        HighlightMode.NONE -> Color.Transparent
    }
    val lines = content.split("\n")
    lines.forEachIndexed { idx, line ->
        val colonIdx = line.indexOf(':')
        if (colonIdx in 1..60) {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = if (mode != HighlightMode.NONE) hlColor else Color.Transparent
                )
            ) {
                append(line.substring(0, colonIdx + 1))
            }
            append(line.substring(colonIdx + 1))
        } else {
            append(line)
        }
        if (idx < lines.lastIndex) append("\n")
    }
}
