package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AdminSection
import com.example.viewmodel.QuorikUiState

private val LoginBgBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF102157), QuorikBluePrimary, QuorikBlueBright)
)

@Composable
fun AdminLoginScreen(
    hasError: Boolean,
    onUnlock: (String) -> Unit,
    onClearError: () -> Unit,
    onOpenStudentApp: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginBgBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(QuorikHeaderBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Admin Shield",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Quorik by BOBI",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Slate900,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Master Admin Control Panel • Real-Time Sync",
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate500,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "ADMIN PASSWORD",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                        color = Slate500
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (hasError) onClearError()
                        },
                        placeholder = { Text("Enter Admin Password", fontSize = 14.sp) },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onUnlock(password) }),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide Password" else "Show Password",
                                    tint = Slate500
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QuorikBluePrimary,
                            unfocusedBorderColor = QuorikBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = QuorikBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("main_admin_password_input")
                    )
                    if (hasError) {
                        Text(
                            text = "Incorrect password! Try again.",
                            style = MaterialTheme.typography.labelMedium,
                            color = Rose600
                        )
                    }
                }

                Button(
                    onClick = { onUnlock(password) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("main_admin_unlock_btn")
                ) {
                    Text(
                        text = "Unlock Admin Panel",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                HorizontalDivider(color = QuorikSurfaceVariant)

                TextButton(
                    onClick = onOpenStudentApp,
                    modifier = Modifier.testTag("login_open_student_app_btn")
                ) {
                    Text(
                        text = "Open Student App",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = QuorikBluePrimary
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open Student App",
                        tint = QuorikBluePrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminDashboardHeader(
    onOpenStudentApp: () -> Unit,
    onExportJson: () -> Unit,
    onImportJson: () -> Unit,
    onLock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(QuorikHeaderBrush)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Admin Control",
                        tint = Amber300,
                        modifier = Modifier.size(21.dp)
                    )
                }
                Column {
                    Text(
                        text = "Quorik by BOBI",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "● Real-Time Sync Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6EE7B7)
                    )
                }
            }

            Surface(
                onClick = onLock,
                shape = RoundedCornerShape(12.dp),
                color = Rose600,
                modifier = Modifier.testTag("admin_lock_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Lock", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = onOpenStudentApp,
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("admin_open_student_app_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Student App",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = QuorikBluePrimary
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Student App",
                        tint = QuorikBluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Surface(
                onClick = onExportJson,
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                modifier = Modifier.testTag("admin_export_json_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = "Export JSON", tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Export JSON", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
            Surface(
                onClick = onImportJson,
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                modifier = Modifier.testTag("admin_import_json_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Import JSON", tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Import JSON", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AdminSectionTopBar(
    title: String,
    onBackHome: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, QuorikBorder),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    onClick = onBackHome,
                    shape = RoundedCornerShape(12.dp),
                    color = QuorikBluePrimary,
                    modifier = Modifier.testTag("admin_back_home_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Admin Home",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text("Admin Home", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Slate900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "● Auto-Sync",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Emerald600
            )
        }
    }
}

@Composable
fun AdminHomeWorkspace(
    state: QuorikUiState,
    onSelectSection: (AdminSection) -> Unit,
    onResetDefaults: () -> Unit
) {
    val db = state.db
    val totalBanners = db.banners.size + db.staticBanners.size

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 6 KPI Overview Cards (3x2 grid on mobile)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiMiniCard("Banners", totalBanners.toString(), QuorikBluePrimary, Modifier.weight(1f))
                KpiMiniCard("Notes", db.notes.size.toString(), Emerald600, Modifier.weight(1f))
                KpiMiniCard("Q&A Bank", db.qna.size.toString(), Teal600, Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiMiniCard("MCQs", db.mcqs.size.toString(), Indigo600, Modifier.weight(1f))
                KpiMiniCard("Pages", db.pages.size.toString(), Color(0xFF7C3AED), Modifier.weight(1f))
                KpiMiniCard("Levels", db.levels.size.toString(), Amber600, Modifier.weight(1f))
            }
        }

        // 01–07 Numbered Management Cards Grid (2 Columns)
        val cards = listOf(
            DashboardCardSpec("01", "Banners", "Main auto-slide & page top banners", "${db.banners.size} Slides", AdminSection.BANNERS, true),
            DashboardCardSpec("02", "Notes & PDFs", "Upload, edit, delete notes, levels & subjects", "${db.notes.size} Notes", AdminSection.NOTES, false),
            DashboardCardSpec("03", "Question & Answer", "Short/Long Q&As, images & PDF bank", "${db.qna.size} Q&As", AdminSection.QNA, false),
            DashboardCardSpec("04", "Syllabus & Notices", "Semesters, subjects, syllabus PDFs & notices", "${db.syllabus.size + db.notices.size} Items", AdminSection.NOTICES_SYLLABUS, true),
            DashboardCardSpec("05", "Festivals & Holidays", "Upload images, titles, dates & descriptions", "${db.festivals.size + db.holidays.size} Cards", AdminSection.EVENTS, false),
            DashboardCardSpec("06", "Sidebar Pages & URLs", "Edit page titles, link pages & custom URLs", "${db.pages.size} Pages", AdminSection.PAGES, false),
            DashboardCardSpec("07", "MCQ Quiz Bank", "Add, edit & delete interactive MCQ tests", "${db.mcqs.size} MCQs", AdminSection.MCQ, false)
        )

        cards.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { card ->
                    AdminDashboardCard(
                        spec = card,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectSection(card.section) }
                    )
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // Subtle Reset to Default Data utility button
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, QuorikBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Database Tools", style = MaterialTheme.typography.titleSmall, color = Slate900)
                    Text("Restore original BA Arts starter data", style = MaterialTheme.typography.bodySmall, color = Slate500)
                }
                OutlinedButton(
                    onClick = onResetDefaults,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Rose600.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Reset Defaults", style = MaterialTheme.typography.labelMedium, color = Rose600)
                }
            }
        }
    }
}

private data class DashboardCardSpec(
    val num: String,
    val title: String,
    val desc: String,
    val countLabel: String,
    val section: AdminSection,
    val featured: Boolean
)

@Composable
private fun KpiMiniCard(label: String, value: String, valueColor: Color, modifier: Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, QuorikBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Slate500, maxLines = 1)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = valueColor
            )
        }
    }
}

@Composable
private fun AdminDashboardCard(
    spec: DashboardCardSpec,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (spec.featured) Color.Transparent else Color.White,
        border = if (spec.featured) null else BorderStroke(1.dp, QuorikBorder),
        shadowElevation = if (spec.featured) 6.dp else 2.dp,
        modifier = modifier
            .heightIn(min = 152.dp)
            .testTag("admin_card_${spec.num}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (spec.featured) Modifier.background(QuorikTileBrush) else Modifier)
                .padding(14.dp)
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
                        text = spec.num,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 34.sp),
                        color = if (spec.featured) Color.White.copy(alpha = 0.28f) else QuorikBluePrimary.copy(alpha = 0.18f)
                    )
                    Surface(
                        shape = CircleShape,
                        color = if (spec.featured) Color.White else QuorikBlueSoftBg
                    ) {
                        Text(
                            text = spec.countLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = QuorikBluePrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = spec.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (spec.featured) Color.White else Slate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = spec.desc,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
                        color = if (spec.featured) QuorikBlueTint else Slate500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ==================== SHARED ADMIN FORM COMPONENTS ====================

@Composable
fun AdminSectionCard(
    title: String,
    subtitle: String? = null,
    headerAction: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Slate900
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Slate500)
                    }
                }
                headerAction?.invoke()
            }
            content()
        }
    }
}

@Composable
fun AdminDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 11.sp) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}
