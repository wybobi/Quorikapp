package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultQuorikData
import com.example.ui.components.LocalFileImage
import com.example.ui.theme.*
import com.example.viewmodel.QuorikUiState
import java.io.File

// ==================== 04. SYLLABUS, SEMESTERS, SUBJECTS & NOTICES ====================
@Composable
fun AdminNoticesSyllabusSection(
    state: QuorikUiState,
    pickedNoticeImagePath: String,
    pickedSyllabusPdfPath: String,
    onClearPickedNoticeImage: () -> Unit,
    onClearPickedSyllabusPdf: () -> Unit,
    onPickNoticeImage: () -> Unit,
    onPickSyllabusPdf: () -> Unit,
    onAddNotice: (String, String, String, String, String, String) -> Unit,
    onDeleteNotice: (String) -> Unit,
    onAddSemester: (String, String) -> Unit,
    onRemoveSemester: (Int) -> Unit,
    onAddSubject: (String) -> Unit,
    onRenameSubject: (Int, String) -> Unit,
    onRemoveSubject: (Int) -> Unit,
    onUpsertSyllabus: (String?, String, String, String, String, String, String, String) -> Unit,
    onDeleteSyllabus: (String) -> Unit
) {
    val subjects = state.db.subjects.ifEmpty { DefaultQuorikData.allSubjects }
    val semesters = state.db.syllabusSemesters.map { it.name }.ifEmpty { DefaultQuorikData.allSemesters.map { it.name } }

    // Notice form state
    var noticeTitle by remember { mutableStateOf("") }
    var noticeCategory by remember { mutableStateOf("Exam Routine") }
    var noticeDate by remember { mutableStateOf("21 Nov 2026") }
    var noticeAuthority by remember { mutableStateOf("Controller of Examinations, GU") }
    var noticeDesc by remember { mutableStateOf("") }
    var noticeImage by remember { mutableStateOf("") }

    // Semester form state
    var newSemName by remember { mutableStateOf("") }
    var newSemDesc by remember { mutableStateOf("") }

    // Subject manager + rename dialog state
    var newSubjName by remember { mutableStateOf("") }
    var renamingSubjPair by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var renameDraft by remember { mutableStateOf("") }

    // Syllabus form state
    var editingSylId by remember { mutableStateOf<String?>(null) }
    var sylTitle by remember { mutableStateOf("") }
    var sylCode by remember { mutableStateOf("") }
    var sylCredits by remember { mutableStateOf("4 Credits (100 Marks)") }
    var sylSubject by remember { mutableStateOf(subjects.first()) }
    var sylSemester by remember { mutableStateOf(semesters.getOrElse(2) { semesters.first() }) }
    var sylPdfUrl by remember { mutableStateOf("") }
    var sylUnits by remember { mutableStateOf("") }

    LaunchedEffect(pickedNoticeImagePath) {
        if (pickedNoticeImagePath.isNotBlank()) {
            noticeImage = pickedNoticeImagePath
            onClearPickedNoticeImage()
        }
    }
    LaunchedEffect(pickedSyllabusPdfPath) {
        if (pickedSyllabusPdfPath.isNotBlank()) {
            sylPdfUrl = pickedSyllabusPdfPath
            onClearPickedSyllabusPdf()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. Exam Notices
        AdminSectionCard(title = "Post Exam Notice / Routine") {
            OutlinedTextField(
                value = noticeTitle,
                onValueChange = { noticeTitle = it },
                label = { Text("Notice Headline") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminDropdownField(
                    label = "Category",
                    selected = noticeCategory,
                    options = listOf("Exam Routine", "Form Fill-up", "Result / Circular"),
                    onSelect = { noticeCategory = it },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = noticeDate,
                    onValueChange = { noticeDate = it },
                    label = { Text("Date") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = noticeAuthority,
                onValueChange = { noticeAuthority = it },
                label = { Text("Issuing Authority") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = noticeDesc,
                onValueChange = { noticeDesc = it },
                label = { Text("Notice Details") },
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onPickNoticeImage, shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(if (noticeImage.isNotBlank()) "Change Notice Photo" else "Upload Notice Photo")
                }
                if (noticeImage.isNotBlank()) {
                    TextButton(onClick = { noticeImage = "" }) { Text("Remove", color = Rose600) }
                }
            }
            if (noticeImage.isNotBlank() && File(noticeImage).exists()) {
                LocalFileImage(
                    path = noticeImage,
                    contentDescription = "Notice Photo",
                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp))
                )
            }
            Button(
                onClick = {
                    if (noticeTitle.isNotBlank() && noticeDesc.isNotBlank()) {
                        onAddNotice(noticeTitle, noticeCategory, noticeDate, noticeAuthority, noticeDesc, noticeImage)
                        noticeTitle = ""; noticeDesc = ""; noticeImage = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber600),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publish Notice & Sync")
            }

            state.db.notices.forEach { n ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(n.title, style = MaterialTheme.typography.titleSmall, color = Slate900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${n.category} • ${n.date}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        IconButton(onClick = { onDeleteNotice(n.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Notice", tint = Rose600, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // 2. Semester Manager
        AdminSectionCard(title = "Semester Manager (${state.db.syllabusSemesters.size})") {
            OutlinedTextField(
                value = newSemName,
                onValueChange = { newSemName = it },
                label = { Text("Semester Name (e.g. 7th Semester)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newSemDesc,
                onValueChange = { newSemDesc = it },
                label = { Text("Short Description (optional)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (newSemName.isNotBlank()) {
                        onAddSemester(newSemName, newSemDesc)
                        newSemName = ""; newSemDesc = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary)
            ) {
                Text("+ Add Semester")
            }
            FlowChipsRow(
                items = state.db.syllabusSemesters.map { it.name },
                onDelete = onRemoveSemester
            )
        }

        // 3. Subject Manager with Rename Dialog
        AdminSectionCard(
            title = "Subject Manager (${subjects.size})",
            subtitle = "Tap pencil icon to rename subject across Notes, Q&A, Syllabus & MCQs"
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newSubjName,
                    onValueChange = { newSubjName = it },
                    placeholder = { Text("New subject name", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newSubjName.isNotBlank()) {
                            onAddSubject(newSubjName)
                            newSubjName = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary)
                ) {
                    Text("+ Add")
                }
            }
            FlowChipsRow(
                items = subjects,
                onRename = { idx, current ->
                    renamingSubjPair = idx to current
                    renameDraft = current
                },
                onDelete = onRemoveSubject
            )
        }

        // 4. Syllabus & PDF Manager
        AdminSectionCard(title = if (editingSylId != null) "Edit Syllabus Paper" else "Add Syllabus & PDF") {
            OutlinedTextField(
                value = sylTitle,
                onValueChange = { sylTitle = it },
                label = { Text("Paper Title") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sylCode,
                    onValueChange = { sylCode = it },
                    label = { Text("Paper Code") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = sylCredits,
                    onValueChange = { sylCredits = it },
                    label = { Text("Credits / Marks") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminDropdownField("Subject", sylSubject, subjects, { sylSubject = it }, Modifier.weight(1f))
                AdminDropdownField("Semester", sylSemester, semesters, { sylSemester = it }, Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sylPdfUrl,
                    onValueChange = { sylPdfUrl = it },
                    label = { Text("Google Drive PDF Link or Local PDF") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = onPickSyllabusPdf,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Pick PDF", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("PDF", style = MaterialTheme.typography.labelMedium)
                }
            }
            OutlinedTextField(
                value = sylUnits,
                onValueChange = { sylUnits = it },
                label = { Text("Course Units (one per line)") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (sylTitle.isNotBlank()) {
                            onUpsertSyllabus(editingSylId, sylTitle, sylCode, sylCredits, sylSubject, sylSemester, sylPdfUrl, sylUnits)
                            editingSylId = null; sylTitle = ""; sylCode = ""; sylPdfUrl = ""; sylUnits = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (editingSylId != null) "Update Syllabus" else "Publish Syllabus & Sync")
                }
                if (editingSylId != null) {
                    OutlinedButton(
                        onClick = { editingSylId = null; sylTitle = ""; sylCode = ""; sylPdfUrl = ""; sylUnits = "" },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Cancel") }
                }
            }

            state.db.syllabus.forEach { s ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${s.paperCode}: ${s.title}", style = MaterialTheme.typography.titleSmall, color = Slate900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${s.semester} • ${s.subject}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        Row {
                            IconButton(onClick = {
                                editingSylId = s.id
                                sylTitle = s.title
                                sylCode = s.paperCode
                                sylCredits = s.credits
                                sylSubject = s.subject
                                sylSemester = s.semester
                                sylPdfUrl = s.pdfUrl
                                sylUnits = s.units.joinToString("\n")
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Syllabus", tint = QuorikBluePrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDeleteSyllabus(s.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Syllabus", tint = Rose600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Subject Rename Dialog
    renamingSubjPair?.let { (idx, oldName) ->
        AlertDialog(
            onDismissRequest = { renamingSubjPair = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(22.dp),
            title = { Text("Rename Subject", style = MaterialTheme.typography.titleLarge, color = Slate900) },
            text = {
                OutlinedTextField(
                    value = renameDraft,
                    onValueChange = { renameDraft = it },
                    label = { Text("New Subject Name (was $oldName)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameDraft.isNotBlank()) onRenameSubject(idx, renameDraft)
                        renamingSubjPair = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary)
                ) { Text("Save Name") }
            },
            dismissButton = {
                TextButton(onClick = { renamingSubjPair = null }) { Text("Cancel") }
            }
        )
    }
}

// ==================== 05. FESTIVALS & HOLIDAYS SECTION ====================
@Composable
fun AdminEventsSection(
    state: QuorikUiState,
    pickedThumbPath: String,
    pickedGalleryPaths: List<String>,
    onClearPickedThumb: () -> Unit,
    onClearPickedGallery: () -> Unit,
    onPickEventThumb: () -> Unit,
    onPickEventGallery: () -> Unit,
    onAddEvent: (Boolean, String, String, String, String, List<String>) -> Unit,
    onDeleteEvent: (Boolean, String) -> Unit
) {
    var eventType by remember { mutableStateOf("Festival") }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var thumbPath by remember { mutableStateOf("") }
    var extraPaths by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(pickedThumbPath) {
        if (pickedThumbPath.isNotBlank()) {
            thumbPath = pickedThumbPath
            onClearPickedThumb()
        }
    }
    LaunchedEffect(pickedGalleryPaths) {
        if (pickedGalleryPaths.isNotEmpty()) {
            extraPaths = extraPaths + pickedGalleryPaths
            onClearPickedGallery()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AdminSectionCard(
            title = "Add Festival or Holiday Card",
            subtitle = "Upload 16:9 thumbnail & extra gallery photos"
        ) {
            AdminDropdownField("Card Type", eventType, listOf("Festival", "Holiday"), { eventType = it })
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title (e.g. Magh Bihu)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (e.g. 14–15 January)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description") },
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onPickEventThumb, shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (thumbPath.isNotBlank()) "Change Thumb" else "Thumbnail Photo", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(onClick = onPickEventGallery, shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+ Gallery (${extraPaths.size})", style = MaterialTheme.typography.labelMedium)
                }
            }
            if (thumbPath.isNotBlank() && File(thumbPath).exists()) {
                LocalFileImage(
                    path = thumbPath,
                    contentDescription = "Event Thumbnail",
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp))
                )
            }
            Button(
                onClick = {
                    if (title.isNotBlank() && desc.isNotBlank()) {
                        onAddEvent(eventType == "Festival", title, date, desc, thumbPath, extraPaths)
                        title = ""; date = ""; desc = ""; thumbPath = ""; extraPaths = emptyList()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publish $eventType Card & Sync")
            }
        }

        AdminSectionCard(title = "Festivals (${state.db.festivals.size})") {
            state.db.festivals.forEach { f ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(f.title, style = MaterialTheme.typography.titleSmall, color = Slate900)
                            Text("${f.date} • ${f.extraImages.size} extra photos", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        IconButton(onClick = { onDeleteEvent(true, f.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Festival", tint = Rose600, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        AdminSectionCard(title = "Holidays (${state.db.holidays.size})") {
            state.db.holidays.forEach { h ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(h.title, style = MaterialTheme.typography.titleSmall, color = Slate900)
                            Text("${h.date} • ${h.extraImages.size} extra photos", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        IconButton(onClick = { onDeleteEvent(false, h.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Holiday", tint = Rose600, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==================== 06. SIDEBAR PAGES & URLS SECTION ====================
@Composable
fun AdminPagesSection(
    state: QuorikUiState,
    onUpsertPage: (String?, String, String, String, String) -> Unit,
    onDeletePage: (String) -> Unit
) {
    var editingId by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }
    var externalUrl by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val linkablePages = listOf("None (Use Custom URL)") + state.db.pages.map { "${it.title} (#/page/${it.slug})" }
    var selectedLinkOption by remember { mutableStateOf(linkablePages.first()) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AdminSectionCard(
            title = if (editingId != null) "Edit Sidebar Page" else "Add Sidebar Page & URL",
            subtitle = "Synced live to the Student App Navigation Drawer"
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Page Name (e.g. Scholarships)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = slug,
                onValueChange = { slug = it },
                label = { Text("Slug (auto-generated if blank)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            AdminDropdownField(
                label = "Link to a Specific App Page (optional)",
                selected = selectedLinkOption,
                options = linkablePages,
                onSelect = { opt ->
                    selectedLinkOption = opt
                    val match = Regex("\\((#/page/[^)]+)\\)").find(opt)?.groupValues?.getOrNull(1)
                    if (match != null) externalUrl = match
                }
            )
            OutlinedTextField(
                value = externalUrl,
                onValueChange = { externalUrl = it },
                label = { Text("Custom URL or #/page/slug") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Page Content") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onUpsertPage(editingId, title, slug, externalUrl, content)
                            editingId = null; title = ""; slug = ""; externalUrl = ""; content = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (editingId != null) "Update Page & Sync" else "Save Page & Sync")
                }
                if (editingId != null) {
                    OutlinedButton(
                        onClick = { editingId = null; title = ""; slug = ""; externalUrl = ""; content = "" },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Cancel") }
                }
            }
        }

        AdminSectionCard(title = "Active Sidebar Pages (${state.db.pages.size})") {
            state.db.pages.forEach { p ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.title, style = MaterialTheme.typography.titleSmall, color = Slate900)
                            Text("#/page/${p.slug}${if (p.externalUrl.isNotBlank()) " → ${p.externalUrl}" else ""}", style = MaterialTheme.typography.labelSmall, color = Slate500, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Row {
                            IconButton(onClick = {
                                editingId = p.id
                                title = p.title
                                slug = p.slug
                                externalUrl = p.externalUrl
                                content = p.content
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Page", tint = QuorikBluePrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDeletePage(p.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Page", tint = Rose600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== 07. MCQ QUIZ BANK SECTION ====================
@Composable
fun AdminMcqSection(
    state: QuorikUiState,
    onUpsertMcq: (String?, String, String, List<String>, Int, String) -> Unit,
    onDeleteMcq: (String) -> Unit
) {
    val subjects = state.db.subjects.ifEmpty { DefaultQuorikData.allSubjects }
    val optionLetters = listOf("A", "B", "C", "D")

    var editingId by remember { mutableStateOf<String?>(null) }
    var subject by remember { mutableStateOf(subjects.first()) }
    var question by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctLetter by remember { mutableStateOf("A") }
    var explanation by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AdminSectionCard(title = if (editingId != null) "Edit MCQ Question" else "Add New MCQ Question") {
            AdminDropdownField("Subject", subject, subjects, { subject = it })
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("MCQ Question") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f))
                OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f))
                OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f))
            }
            AdminDropdownField("Correct Option", correctLetter, optionLetters, { correctLetter = it })
            OutlinedTextField(
                value = explanation,
                onValueChange = { explanation = it },
                label = { Text("Answer Explanation") },
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (question.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                            onUpsertMcq(
                                editingId,
                                subject,
                                question,
                                listOf(optA, optB, optC.ifBlank { "None" }, optD.ifBlank { "All of the above" }),
                                optionLetters.indexOf(correctLetter).coerceIn(0, 3),
                                explanation
                            )
                            editingId = null; question = ""; optA = ""; optB = ""; optC = ""; optD = ""; explanation = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (editingId != null) "Update MCQ & Sync" else "Add MCQ & Sync")
                }
                if (editingId != null) {
                    OutlinedButton(
                        onClick = { editingId = null; question = ""; optA = ""; optB = ""; optC = ""; optD = ""; explanation = "" },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Cancel") }
                }
            }
        }

        AdminSectionCard(title = "MCQ Question Bank (${state.db.mcqs.size})") {
            state.db.mcqs.forEachIndexed { idx, m ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Q${idx + 1}. ${m.question}", style = MaterialTheme.typography.titleSmall, color = Slate900, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("${m.subject} • Ans: ${optionLetters.getOrElse(m.correctIndex) { "A" }}", style = MaterialTheme.typography.labelSmall, color = Emerald600)
                        }
                        Row {
                            IconButton(onClick = {
                                editingId = m.id
                                subject = m.subject
                                question = m.question
                                optA = m.options.getOrElse(0) { "" }
                                optB = m.options.getOrElse(1) { "" }
                                optC = m.options.getOrElse(2) { "" }
                                optD = m.options.getOrElse(3) { "" }
                                correctLetter = optionLetters.getOrElse(m.correctIndex) { "A" }
                                explanation = m.explanation
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit MCQ", tint = QuorikBluePrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDeleteMcq(m.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete MCQ", tint = Rose600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
