package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultQuorikData
import com.example.ui.components.LocalFileImage
import com.example.ui.theme.*
import com.example.viewmodel.QuorikUiState
import java.io.File

// ==================== 01. BANNERS SECTION ====================
@Composable
fun AdminBannersSection(
    state: QuorikUiState,
    onAddMainBanner: () -> Unit,
    onPickBannerImage: (Int) -> Unit,
    onClearBannerImage: (Int) -> Unit,
    onSaveBannerMeta: (Int, String, String, String, String) -> Unit,
    onDeleteMainBanner: (Int) -> Unit,
    onPickPageBannerImage: (String) -> Unit,
    onClearPageBannerImage: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AdminSectionCard(
            title = "Home Top Auto-Slide Banners",
            subtitle = "Recommended: 1200 × 450 px (2.5:1) • Tap Upload to pick image from gallery",
            headerAction = {
                Button(
                    onClick = onAddMainBanner,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("+ Add Banner", style = MaterialTheme.typography.labelMedium)
                }
            }
        ) {
            state.db.banners.forEachIndexed { idx, banner ->
                var badge by remember(banner.id, banner.badge) { mutableStateOf(banner.badge) }
                var title by remember(banner.id, banner.title) { mutableStateOf(banner.title) }
                var subtitle by remember(banner.id, banner.subtitle) { mutableStateOf(banner.subtitle) }
                var targetId by remember(banner.id, banner.targetId) { mutableStateOf(banner.targetId) }
                val hasImage = banner.imagePath.isNotBlank() && File(banner.imagePath).exists()

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Home Top Slide ${idx + 1}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = QuorikBluePrimary
                                )
                                Text(
                                    text = "Size: 1200 × 450 px (2.5:1)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate500
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (hasImage) Emerald50 else QuorikSurfaceVariant
                            ) {
                                Text(
                                    text = if (hasImage) "● Uploaded" else "○ Gradient Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (hasImage) Emerald600 else Slate500,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (hasImage) {
                            LocalFileImage(
                                path = banner.imagePath,
                                contentDescription = banner.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(2.4f)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onPickBannerImage(idx) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(if (hasImage) "Change Image" else "Upload Image", style = MaterialTheme.typography.labelMedium)
                            }
                            if (hasImage) {
                                OutlinedButton(
                                    onClick = { onClearBannerImage(idx) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)
                                ) {
                                    Text("Clear Photo", style = MaterialTheme.typography.labelMedium, color = Slate700)
                                }
                            }
                            OutlinedButton(
                                onClick = { onDeleteMainBanner(idx) },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Rose600.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Text("Delete", style = MaterialTheme.typography.labelMedium, color = Rose600)
                            }
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Banner Title", fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = badge,
                                onValueChange = { badge = it },
                                label = { Text("Badge", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = targetId,
                                onValueChange = { targetId = it },
                                label = { Text("Target (notes/qna/id)", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            label = { Text("Subtitle", fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = { onSaveBannerMeta(idx, badge, title, subtitle, targetId) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                        ) {
                            Text("Save Slide ${idx + 1}", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        AdminSectionCard(
            title = "Inner Page Top Banners",
            subtitle = "Notes, Notices, Syllabus, Festivals & Holidays • Required: 1200 × 600 px (2:1)"
        ) {
            val pageKeys = listOf(
                "notes" to "Notes Page Top Banner",
                "notices" to "Notices Page Top Banner",
                "syllabus" to "Syllabus Page Top Banner",
                "festivals" to "Festivals Page Top Banner",
                "holidays" to "Holidays Page Top Banner"
            )
            pageKeys.forEach { (key, label) ->
                val pb = state.db.pageBanners[key]
                val hasImage = !pb?.imagePath.isNullOrBlank() && File(pb!!.imagePath).exists()
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(label, style = MaterialTheme.typography.titleSmall, color = Slate900)
                                Text("1200 × 600 px (2:1)", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (hasImage) Emerald50 else QuorikSurfaceVariant
                            ) {
                                Text(
                                    text = if (hasImage) "● Uploaded" else "○ Default",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (hasImage) Emerald600 else Slate500,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        if (hasImage && pb != null) {
                            LocalFileImage(
                                path = pb.imagePath,
                                contentDescription = label,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(2f)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onPickPageBannerImage(key) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(if (hasImage) "Change / Edit" else "Upload Image", style = MaterialTheme.typography.labelMedium)
                            }
                            if (hasImage) {
                                OutlinedButton(
                                    onClick = { onClearPageBannerImage(key) },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Rose600.copy(alpha = 0.4f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Delete Image", style = MaterialTheme.typography.labelMedium, color = Rose600)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== 02. NOTES, LEVELS & SUBJECTS SECTION ====================
@Composable
fun AdminNotesSection(
    state: QuorikUiState,
    pickedPdfPath: String,
    onClearPickedPdf: () -> Unit,
    onPickNotePdf: () -> Unit,
    onUpsertNote: (String?, String, String, String, String, String, String, String, String, String, String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onAddSubject: (String) -> Unit,
    onRemoveSubject: (Int) -> Unit,
    onAddLevel: (String) -> Unit,
    onRemoveLevel: (Int) -> Unit
) {
    val subjects = state.db.subjects.ifEmpty { DefaultQuorikData.allSubjects }
    val semesters = state.db.syllabusSemesters.map { it.name }.ifEmpty { DefaultQuorikData.allSemesters.map { it.name } }
    val levels = state.db.levels.ifEmpty { DefaultQuorikData.defaultLevels }

    var editingId by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var paperCode by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(levels.first()) }
    var subject by remember { mutableStateOf(subjects.first()) }
    var semester by remember { mutableStateOf(semesters.getOrElse(2) { semesters.first() }) }
    var medium by remember { mutableStateOf("Assamese") }
    var pdfUrl by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var unitHeading by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    var newSubjectInput by remember { mutableStateOf("") }
    var newLevelInput by remember { mutableStateOf("") }

    LaunchedEffect(pickedPdfPath) {
        if (pickedPdfPath.isNotBlank()) {
            pdfUrl = pickedPdfPath
            onClearPickedPdf()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AdminSectionCard(
            title = if (editingId != null) "Edit Study Note" else "Publish New Study Note",
            subtitle = "Real-time sync with Student App Notes & PDFs"
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Note Title") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = paperCode,
                    onValueChange = { paperCode = it },
                    label = { Text("Paper Code") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                AdminDropdownField("Level", level, levels, { level = it }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminDropdownField("Subject", subject, subjects, { subject = it }, Modifier.weight(1f))
                AdminDropdownField("Semester", semester, semesters, { semester = it }, Modifier.weight(1f))
            }
            AdminDropdownField("Medium", medium, listOf("Assamese", "English", "Assamese & English"), { medium = it })

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pdfUrl,
                    onValueChange = { pdfUrl = it },
                    label = { Text("PDF / Google Drive Link (optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = onPickNotePdf,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Pick PDF", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("PDF", style = MaterialTheme.typography.labelMedium)
                }
            }

            OutlinedTextField(
                value = summary,
                onValueChange = { summary = it },
                label = { Text("Short Description / Key Takeaway") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = unitHeading,
                onValueChange = { unitHeading = it },
                label = { Text("Unit 1 Heading") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Full Note Content") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onUpsertNote(editingId, title, paperCode, level, subject, semester, medium, pdfUrl, summary, unitHeading, body)
                            editingId = null; title = ""; paperCode = ""; pdfUrl = ""; summary = ""; unitHeading = ""; body = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (editingId != null) "Update Note & Sync" else "Publish Note & Sync")
                }
                if (editingId != null) {
                    OutlinedButton(
                        onClick = {
                            editingId = null; title = ""; paperCode = ""; pdfUrl = ""; summary = ""; unitHeading = ""; body = ""
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }

        // Manage Notes Subjects
        AdminSectionCard(title = "Notes Subjects (${subjects.size})") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newSubjectInput,
                    onValueChange = { newSubjectInput = it },
                    placeholder = { Text("Add new subject (e.g. Geography)", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newSubjectInput.isNotBlank()) {
                            onAddSubject(newSubjectInput)
                            newSubjectInput = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary)
                ) {
                    Text("+ Add")
                }
            }
            FlowChipsRow(items = subjects, onDelete = onRemoveSubject)
        }

        // Manage Custom Levels
        AdminSectionCard(title = "Custom Study Levels (${levels.size})") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newLevelInput,
                    onValueChange = { newLevelInput = it },
                    placeholder = { Text("Add level (e.g. Crash Course)", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newLevelInput.isNotBlank()) {
                            onAddLevel(newLevelInput)
                            newLevelInput = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber600)
                ) {
                    Text("+ Add")
                }
            }
            FlowChipsRow(items = levels, onDelete = onRemoveLevel)
        }

        // Published Notes List
        AdminSectionCard(title = "Published Notes (${state.db.notes.size})") {
            state.db.notes.forEach { n ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(n.title, style = MaterialTheme.typography.titleSmall, color = Slate900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                text = "${n.subject} • ${n.semester}${if (n.level.isNotBlank()) " • ${n.level}" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate500
                            )
                        }
                        Row {
                            IconButton(onClick = {
                                editingId = n.id
                                title = n.title
                                paperCode = n.paperCode
                                level = n.level.ifBlank { levels.first() }
                                subject = n.subject
                                semester = n.semester
                                medium = n.medium
                                pdfUrl = n.pdfUrl
                                summary = n.summary
                                unitHeading = n.topics.firstOrNull()?.title ?: ""
                                body = n.topics.firstOrNull()?.content ?: ""
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Note", tint = QuorikBluePrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDeleteNote(n.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = Rose600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== 03. QUESTION & ANSWER SECTION ====================
@Composable
fun AdminQnaSection(
    state: QuorikUiState,
    pickedImagePath: String,
    onClearPickedImage: () -> Unit,
    onPickQnaImage: () -> Unit,
    onUpsertQna: (String?, String, String, String, String, String, String, String, String, String) -> Unit,
    onDeleteQna: (String) -> Unit
) {
    val subjects = state.db.subjects.ifEmpty { DefaultQuorikData.allSubjects }
    val semesters = state.db.syllabusSemesters.map { it.name }.ifEmpty { DefaultQuorikData.allSemesters.map { it.name } }
    val levels = state.db.levels.ifEmpty { DefaultQuorikData.defaultLevels }
    val markTypes = listOf("Short Question", "Long Question", "1 Mark", "2 Marks", "5 Marks", "10 Marks")

    var editingId by remember { mutableStateOf<String?>(null) }
    var subject by remember { mutableStateOf(subjects.first()) }
    var semester by remember { mutableStateOf(semesters.getOrElse(2) { semesters.first() }) }
    var marks by remember { mutableStateOf("5 Marks") }
    var level by remember { mutableStateOf(levels.first()) }
    var medium by remember { mutableStateOf("Assamese") }
    var question by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    LaunchedEffect(pickedImagePath) {
        if (pickedImagePath.isNotBlank()) {
            imagePath = pickedImagePath
            onClearPickedImage()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AdminSectionCard(
            title = if (editingId != null) "Edit Question & Answer" else "Add Solved Question & Answer",
            subtitle = "Supports Short/Long Q&As, optional photo & instant sync"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminDropdownField("Subject", subject, subjects, { subject = it }, Modifier.weight(1f))
                AdminDropdownField("Semester", semester, semesters, { semester = it }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminDropdownField("Marks", marks, markTypes, { marks = it }, Modifier.weight(1f))
                AdminDropdownField("Level", level, levels, { level = it }, Modifier.weight(1f))
            }
            AdminDropdownField("Medium", medium, listOf("Assamese", "English", "Assamese & English"), { medium = it })
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Question / Title") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Short Description (optional)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it },
                label = { Text("Full Solved Answer") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Optional Card Image Picker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onPickQnaImage,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(if (imagePath.isNotBlank()) "Change Photo" else "Attach Q&A Image")
                }
                if (imagePath.isNotBlank()) {
                    TextButton(onClick = { imagePath = "" }) {
                        Text("Remove", color = Rose600)
                    }
                }
            }
            if (imagePath.isNotBlank() && File(imagePath).exists()) {
                LocalFileImage(
                    path = imagePath,
                    contentDescription = "Q&A Image Preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (question.isNotBlank() && answer.isNotBlank()) {
                            onUpsertQna(editingId, subject, semester, marks, level, medium, question, description, answer, imagePath)
                            editingId = null; question = ""; description = ""; answer = ""; imagePath = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal700),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (editingId != null) "Update Q&A & Sync" else "Publish Q&A & Sync")
                }
                if (editingId != null) {
                    OutlinedButton(
                        onClick = { editingId = null; question = ""; description = ""; answer = ""; imagePath = "" },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }

        AdminSectionCard(title = "All Q&As (${state.db.qna.size})") {
            state.db.qna.forEach { q ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = QuorikBackground,
                    border = BorderStroke(1.dp, QuorikBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(q.question, style = MaterialTheme.typography.titleSmall, color = Slate900, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("${q.marks} • ${q.subject} • ${q.semester}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        Row {
                            IconButton(onClick = {
                                editingId = q.id
                                subject = q.subject
                                semester = q.semester
                                marks = q.marks
                                level = q.level.ifBlank { levels.first() }
                                medium = q.medium
                                question = q.question
                                description = q.description
                                answer = q.answer
                                imagePath = q.imagePath
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Q&A", tint = QuorikBluePrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDeleteQna(q.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Q&A", tint = Rose600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowChipsRow(
    items: List<String>,
    onRename: ((Int, String) -> Unit)? = null,
    onDelete: (Int) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEachIndexed { idx, label ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = QuorikBlueSoftBg,
                border = BorderStroke(1.dp, QuorikBluePrimary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelMedium, color = QuorikBluePrimary)
                    if (onRename != null) {
                        IconButton(
                            onClick = { onRename(idx, label) },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename $label", tint = QuorikBluePrimary, modifier = Modifier.size(13.dp))
                        }
                    }
                    IconButton(
                        onClick = { onDelete(idx) },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove $label", tint = Rose600, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
