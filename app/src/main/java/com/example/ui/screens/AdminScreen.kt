package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BannerSlide
import com.example.data.DefaultQuorikData
import com.example.ui.theme.*
import com.example.viewmodel.AdminTab
import com.example.viewmodel.QuorikUiState

@Composable
fun AdminScreen(
    state: QuorikUiState,
    onSelectAdminTab: (AdminTab) -> Unit,
    onExitToStudent: () -> Unit,
    onResetDefaults: () -> Unit,
    onExportJson: () -> Unit,
    onCopyLink: (String) -> Unit,
    onReadClipboardOrLast: () -> String,
    onSaveBanners: (List<BannerSlide>) -> Unit,
    onSavePage: (title: String, slug: String, extUrl: String, content: String) -> Unit,
    onUpdatePageUrl: (String, String) -> Unit,
    onDeletePage: (String) -> Unit,
    onViewPage: (String) -> Unit,
    onAddNote: (String, String, String, String, String, String, String, String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onAddQna: (String, String, String, String, String, String) -> Unit,
    onDeleteQna: (String) -> Unit,
    onAddMcq: (String, String, List<String>, Int, String) -> Unit,
    onDeleteMcq: (String) -> Unit,
    onAddSyllabus: (String, String, String, String, String, String) -> Unit,
    onDeleteSyllabus: (String) -> Unit,
    onAddNotice: (String, String, String, String, String) -> Unit,
    onDeleteNotice: (String) -> Unit
) {
    val db = state.db
    val subjects = db.subjects.ifEmpty { DefaultQuorikData.allSubjects }
    val semesters = DefaultQuorikData.allSemesters.map { it.name }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuorikBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Dark Admin Header
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Slate900,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Rose600),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = "Admin", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text("HIDDEN ADMIN CMS", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFDA4AF))
                        Text("Quorik Master Control Panel", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onExportJson,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Slate700),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Backup JSON", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                    OutlinedButton(
                        onClick = onResetDefaults,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Slate700),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset Data", style = MaterialTheme.typography.labelMedium, color = Color(0xFFFDA4AF))
                    }
                    Button(
                        onClick = onExitToStudent,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Student →", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
            }
        }

        // 4 KPI Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiStatBox("Slides", db.banners.size.toString(), QuorikBluePrimary, Modifier.weight(1f))
            KpiStatBox("Pages", db.pages.size.toString(), Indigo600, Modifier.weight(1f))
            KpiStatBox("Notes", db.notes.size.toString(), Emerald600, Modifier.weight(1f))
            KpiStatBox("Q&A/Not.", (db.qna.size + db.notices.size).toString(), Amber500, Modifier.weight(1f))
        }

        // Workspace Card
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.dp, QuorikBorder),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 7 Admin Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AdminTab.entries.forEach { tab ->
                        val active = state.adminActiveTab == tab
                        Surface(
                            onClick = { onSelectAdminTab(tab) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) Slate900 else QuorikSurfaceVariant
                        ) {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (active) Color.White else Slate700,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = QuorikBorder)

                when (state.adminActiveTab) {
                    AdminTab.BANNERS -> AdminBannersTab(
                        banners = db.banners,
                        onReadClipboard = onReadClipboardOrLast,
                        onSave = onSaveBanners
                    )
                    AdminTab.PAGES -> AdminPagesTab(
                        pages = db.pages,
                        onCopyLink = onCopyLink,
                        onReadClipboard = onReadClipboardOrLast,
                        onSavePage = onSavePage,
                        onUpdateUrl = onUpdatePageUrl,
                        onDeletePage = onDeletePage,
                        onViewPage = onViewPage
                    )
                    AdminTab.NOTES -> AdminNotesTab(
                        notes = db.notes,
                        subjects = subjects,
                        semesters = semesters,
                        onAddNote = onAddNote,
                        onCopyId = onCopyLink,
                        onDeleteNote = onDeleteNote
                    )
                    AdminTab.QNA -> AdminQnaTab(
                        qnaList = db.qna,
                        subjects = subjects,
                        semesters = semesters,
                        onAddQna = onAddQna,
                        onDeleteQna = onDeleteQna
                    )
                    AdminTab.MCQ -> AdminMcqTab(
                        mcqs = db.mcqs,
                        subjects = subjects,
                        onAddMcq = onAddMcq,
                        onDeleteMcq = onDeleteMcq
                    )
                    AdminTab.SYLLABUS -> AdminSyllabusTab(
                        syllabusList = db.syllabus,
                        subjects = subjects,
                        semesters = semesters,
                        onAddSyllabus = onAddSyllabus,
                        onDeleteSyllabus = onDeleteSyllabus
                    )
                    AdminTab.NOTICES -> AdminNoticesTab(
                        notices = db.notices,
                        onAddNotice = onAddNotice,
                        onDeleteNotice = onDeleteNotice
                    )
                }
            }
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun KpiStatBox(label: String, value: String, valueColor: Color, modifier: Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, QuorikBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Slate500)
            Text(value, style = MaterialTheme.typography.headlineSmall, color = valueColor)
        }
    }
}

@Composable
private fun AdminDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, QuorikBorder),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$label: $selected",
                style = MaterialTheme.typography.labelMedium,
                color = Slate900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate600, modifier = Modifier.size(18.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt, fontSize = 13.sp) },
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun AdminBannersTab(
    banners: List<BannerSlide>,
    onReadClipboard: () -> String,
    onSave: (List<BannerSlide>) -> Unit
) {
    val editable = remember(banners) { mutableStateListOf(*banners.toTypedArray()) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(12.dp), color = QuorikBlueSoftBg) {
            Text(
                text = "Manage Home Auto-Slide Banners (3-second rotation). Paste any Page link (#/page/about-us) or Note ID.",
                style = MaterialTheme.typography.bodySmall,
                color = QuorikBlueDark,
                modifier = Modifier.padding(12.dp)
            )
        }

        editable.forEachIndexed { idx, slide ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = QuorikBackground,
                border = BorderStroke(1.dp, QuorikBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Slide ${idx + 1} • 3s Auto-Slide", style = MaterialTheme.typography.labelLarge, color = QuorikBluePrimary)
                    OutlinedTextField(
                        value = slide.badge,
                        onValueChange = { editable[idx] = slide.copy(badge = it) },
                        label = { Text("Badge") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = slide.title,
                        onValueChange = { editable[idx] = slide.copy(title = it) },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = slide.subtitle,
                        onValueChange = { editable[idx] = slide.copy(subtitle = it) },
                        label = { Text("Subtitle") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = slide.targetId,
                            onValueChange = { editable[idx] = slide.copy(targetId = it) },
                            label = { Text("Target ID / Link") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { editable[idx] = slide.copy(targetId = onReadClipboard()) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                        ) {
                            Text("Paste", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onSave(editable.toList()) },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Banner Slides", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun AdminPagesTab(
    pages: List<com.example.data.CustomPage>,
    onCopyLink: (String) -> Unit,
    onReadClipboard: () -> String,
    onSavePage: (String, String, String, String) -> Unit,
    onUpdateUrl: (String, String) -> Unit,
    onDeletePage: (String) -> Unit,
    onViewPage: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }
    var extUrl by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = QuorikBackground,
            border = BorderStroke(1.dp, QuorikBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("+ Create / Link Sidebar Page", style = MaterialTheme.typography.titleSmall, color = Slate900)
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        slug = it.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
                    },
                    label = { Text("Page Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = slug,
                    onValueChange = { slug = it },
                    label = { Text("Page Slug") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = extUrl,
                        onValueChange = { extUrl = it },
                        label = { Text("External/Linked URL (optional)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { extUrl = onReadClipboard() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate900)
                    ) {
                        Text("Paste", style = MaterialTheme.typography.labelMedium)
                    }
                }
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Page Content") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            onSavePage(title, slug, extUrl, content)
                            title = ""; slug = ""; extUrl = ""; content = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Page to Sidebar")
                }
            }
        }

        Text("Active Sidebar Pages", style = MaterialTheme.typography.titleSmall, color = Slate900)
        pages.forEach { page ->
            var linkField by remember(page.id, page.externalUrl) { mutableStateOf(page.externalUrl) }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, QuorikBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(page.title, style = MaterialTheme.typography.titleSmall, color = Slate900)
                            Text("#/page/${page.slug}", style = MaterialTheme.typography.labelSmall, color = Indigo600)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = { onCopyLink("#/page/${page.slug}") }) { Text("Copy", fontSize = 11.sp) }
                            TextButton(onClick = { onViewPage(page.slug) }) { Text("View", fontSize = 11.sp) }
                            TextButton(onClick = { onDeletePage(page.id) }) { Text("Delete", fontSize = 11.sp, color = Rose600) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = linkField,
                            onValueChange = { linkField = it },
                            placeholder = { Text("Linked URL", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { onUpdateUrl(page.id, linkField) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text("Save", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminNotesTab(
    notes: List<com.example.data.StudyNote>,
    subjects: List<String>,
    semesters: List<String>,
    onAddNote: (String, String, String, String, String, String, String, String) -> Unit,
    onCopyId: (String) -> Unit,
    onDeleteNote: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf(subjects.first()) }
    var semester by remember { mutableStateOf(semesters.first()) }
    var medium by remember { mutableStateOf("Assamese") }
    var unitTitle by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(16.dp), color = QuorikBackground, border = BorderStroke(1.dp, QuorikBorder)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("+ Add Study Note & PDF", style = MaterialTheme.typography.titleSmall, color = Slate900)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Note Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Paper Code") }, singleLine = true, modifier = Modifier.weight(1f))
                    AdminDropdown("Med", listOf("Assamese", "English"), medium, { medium = it }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminDropdown("Subj", subjects, subject, { subject = it }, Modifier.weight(1f))
                    AdminDropdown("Sem", semesters, semester, { semester = it }, Modifier.weight(1f))
                }
                OutlinedTextField(value = unitTitle, onValueChange = { unitTitle = it }, label = { Text("Unit Heading") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Short Summary") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Full Note Content") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        if (title.isNotBlank() && code.isNotBlank() && body.isNotBlank()) {
                            onAddNote(title, code, subject, semester, medium, unitTitle.ifBlank { "Unit 1" }, summary.ifBlank { title }, body)
                            title = ""; code = ""; unitTitle = ""; summary = ""; body = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publish Note")
                }
            }
        }

        notes.forEach { n ->
            AdminListRow(
                title = n.title,
                subtitle = "${n.paperCode} • ${n.subject} • ${n.semester}",
                extraActionLabel = "Copy ID",
                onExtraAction = { onCopyId(n.id) },
                onDelete = { onDeleteNote(n.id) }
            )
        }
    }
}

@Composable
private fun AdminQnaTab(
    qnaList: List<com.example.data.QnaItem>,
    subjects: List<String>,
    semesters: List<String>,
    onAddQna: (String, String, String, String, String, String) -> Unit,
    onDeleteQna: (String) -> Unit
) {
    var subject by remember { mutableStateOf(subjects.first()) }
    var semester by remember { mutableStateOf(semesters.first()) }
    var marks by remember { mutableStateOf("5 Marks") }
    var medium by remember { mutableStateOf("Assamese") }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(16.dp), color = QuorikBackground, border = BorderStroke(1.dp, QuorikBorder)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("+ Add Question & Bank Item", style = MaterialTheme.typography.titleSmall, color = Slate900)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminDropdown("Subj", subjects, subject, { subject = it }, Modifier.weight(1f))
                    AdminDropdown("Sem", semesters, semester, { semester = it }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminDropdown("Marks", listOf("1 Mark", "2 Marks", "5 Marks", "10 Marks"), marks, { marks = it }, Modifier.weight(1f))
                    AdminDropdown("Med", listOf("Assamese", "English"), medium, { medium = it }, Modifier.weight(1f))
                }
                OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Question") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Answer") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        if (question.isNotBlank() && answer.isNotBlank()) {
                            onAddQna(subject, semester, marks, medium, question, answer)
                            question = ""; answer = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal600),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Question")
                }
            }
        }

        qnaList.forEach { q ->
            AdminListRow(
                title = q.question,
                subtitle = "${q.marks} • ${q.subject} • ${q.semester}",
                onDelete = { onDeleteQna(q.id) }
            )
        }
    }
}

@Composable
private fun AdminMcqTab(
    mcqs: List<com.example.data.McqItem>,
    subjects: List<String>,
    onAddMcq: (String, String, List<String>, Int, String) -> Unit,
    onDeleteMcq: (String) -> Unit
) {
    var subject by remember { mutableStateOf(subjects.first()) }
    var question by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctLabel by remember { mutableStateOf("Option A") }
    var explanation by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(16.dp), color = QuorikBackground, border = BorderStroke(1.dp, QuorikBorder)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("+ Add MCQ Question", style = MaterialTheme.typography.titleSmall, color = Slate900)
                AdminDropdown("Subject", subjects, subject, { subject = it }, Modifier.fillMaxWidth())
                OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Question") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                AdminDropdown("Correct", listOf("Option A", "Option B", "Option C", "Option D"), correctLabel, { correctLabel = it }, Modifier.fillMaxWidth())
                OutlinedTextField(value = explanation, onValueChange = { explanation = it }, label = { Text("Explanation") }, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        if (question.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                            val correctIdx = listOf("Option A", "Option B", "Option C", "Option D").indexOf(correctLabel).coerceAtLeast(0)
                            onAddMcq(subject, question, listOf(optA, optB, optC.ifBlank { "Option C" }, optD.ifBlank { "Option D" }), correctIdx, explanation.ifBlank { "Correct answer verified." })
                            question = ""; optA = ""; optB = ""; optC = ""; optD = ""; explanation = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save MCQ")
                }
            }
        }

        mcqs.forEach { m ->
            AdminListRow(title = m.question, subtitle = m.subject, onDelete = { onDeleteMcq(m.id) })
        }
    }
}

@Composable
private fun AdminSyllabusTab(
    syllabusList: List<com.example.data.SyllabusItem>,
    subjects: List<String>,
    semesters: List<String>,
    onAddSyllabus: (String, String, String, String, String, String) -> Unit,
    onDeleteSyllabus: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("4 Credits (100 Marks)") }
    var subject by remember { mutableStateOf(subjects.first()) }
    var semester by remember { mutableStateOf(semesters.first()) }
    var units by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(16.dp), color = QuorikBackground, border = BorderStroke(1.dp, QuorikBorder)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("+ Add Syllabus Paper", style = MaterialTheme.typography.titleSmall, color = Slate900)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Paper Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Paper Code") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = credits, onValueChange = { credits = it }, label = { Text("Credits") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminDropdown("Subj", subjects, subject, { subject = it }, Modifier.weight(1f))
                    AdminDropdown("Sem", semesters, semester, { semester = it }, Modifier.weight(1f))
                }
                OutlinedTextField(value = units, onValueChange = { units = it }, label = { Text("Units (one per line)") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        if (title.isNotBlank() && code.isNotBlank()) {
                            onAddSyllabus(title, code, credits, subject, semester, units)
                            title = ""; code = ""; units = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Syllabus")
                }
            }
        }

        syllabusList.forEach { s ->
            AdminListRow(title = "${s.paperCode}: ${s.title}", subtitle = "${s.semester} • ${s.subject}", onDelete = { onDeleteSyllabus(s.id) })
        }
    }
}

@Composable
private fun AdminNoticesTab(
    notices: List<com.example.data.ExamNotice>,
    onAddNotice: (String, String, String, String, String) -> Unit,
    onDeleteNotice: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Exam Routine") }
    var date by remember { mutableStateOf("21 Nov 2026") }
    var authority by remember { mutableStateOf("Controller of Examinations, GU") }
    var desc by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(16.dp), color = QuorikBackground, border = BorderStroke(1.dp, QuorikBorder)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("+ Post Exam Notice", style = MaterialTheme.typography.titleSmall, color = Slate900)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Notice Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminDropdown("Cat", listOf("Exam Routine", "Form Fill-up", "Result / Circular"), category, { category = it }, Modifier.weight(1f))
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = authority, onValueChange = { authority = it }, label = { Text("Authority") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Details") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        if (title.isNotBlank() && desc.isNotBlank()) {
                            onAddNotice(title, category, date, authority, desc)
                            title = ""; desc = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publish Notice", color = Color.White)
                }
            }
        }

        notices.forEach { n ->
            AdminListRow(title = n.title, subtitle = "${n.category} • ${n.date}", onDelete = { onDeleteNotice(n.id) })
        }
    }
}

@Composable
private fun AdminListRow(
    title: String,
    subtitle: String,
    extraActionLabel: String? = null,
    onExtraAction: (() -> Unit)? = null,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, QuorikBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = Slate900, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Slate500)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (extraActionLabel != null && onExtraAction != null) {
                    TextButton(onClick = onExtraAction) { Text(extraActionLabel, fontSize = 11.sp) }
                }
                TextButton(onClick = onDelete) { Text("Delete", fontSize = 11.sp, color = Rose600) }
            }
        }
    }
}
