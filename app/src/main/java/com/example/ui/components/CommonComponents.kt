package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.EventDetailData
import com.example.viewmodel.PdfModalData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

@Composable
fun LocalFileImage(
    path: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = path) {
        value = if (path.isNotBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val file = File(path)
                    if (file.exists()) BitmapFactory.decodeFile(path)?.asImageBitmap() else null
                } catch (_: Exception) {
                    null
                }
            }
        } else null
    }
    bitmap?.let { img ->
        Image(
            bitmap = img,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

fun mapIconName(name: String): ImageVector = when (name) {
    "book-open-check", "book-open" -> Icons.Default.MenuBook
    "compass" -> Icons.Default.Explore
    "award" -> Icons.Default.EmojiEvents
    "bell-ring", "bell" -> Icons.Default.NotificationsActive
    "graduation-cap" -> Icons.Default.School
    "sparkles" -> Icons.Default.AutoAwesome
    "calendar-days" -> Icons.Default.CalendarMonth
    "info" -> Icons.Default.Info
    "shield" -> Icons.Default.Shield
    "file-check" -> Icons.Default.Verified
    "mail" -> Icons.Default.MailOutline
    else -> Icons.Default.Description
}

@Composable
fun SubScreenTopBar(
    title: String,
    badgeText: String,
    badgeBg: Color = QuorikBlueSoftBg,
    badgeTextColor: Color = QuorikBluePrimary,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, QuorikBorder),
                modifier = Modifier
                    .size(44.dp)
                    .testTag("subscreen_back_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Slate700,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Slate900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = badgeBg
        ) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelMedium,
                color = badgeTextColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun PageBannerCard(banner: PageBanner) {
    if (banner.imagePath.isNotBlank() && File(banner.imagePath).exists()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .clip(RoundedCornerShape(24.dp))
                .background(Slate900)
        ) {
            LocalFileImage(
                path = banner.imagePath,
                contentDescription = banner.title,
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(gradientBrushForKey(banner.gradientKey))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = banner.badge.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Amber200,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = banner.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = banner.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = mapIconName(banner.icon),
                    contentDescription = banner.title,
                    tint = Amber300,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun StarRatingBar(
    itemId: String,
    defaultAvg: Double,
    userRating: Int?,
    onRate: (Int) -> Unit
) {
    val displayScore = if (userRating != null) "${userRating}.0" else String.format("%.1f", defaultAvg)
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Amber50,
        border = BorderStroke(1.dp, Amber200)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (s in 1..5) {
                val filled = if (userRating != null) s <= userRating else s <= defaultAvg.roundToInt()
                Icon(
                    imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Rate $s stars",
                    tint = if (filled) Amber400 else Slate400,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onRate(s) }
                )
            }
            Spacer(Modifier.width(3.dp))
            Text(
                text = displayScore,
                style = MaterialTheme.typography.labelMedium,
                color = Amber800
            )
        }
    }
}

@Composable
fun FeedPostRow(
    categoryLabel: String,
    dateStr: String,
    title: String,
    desc: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.titleSmall,
                color = Slate700
            )
            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelMedium,
                color = Slate400
            )
        }
        Surface(
            onClick = onClick,
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
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate900,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(74.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(QuorikTileBrush)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = categoryLabel,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = categoryLabel.uppercase(),
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

@Composable
fun QuorikBottomNavBar(
    activeTab: AppTab,
    onSelectTab: (AppTab) -> Unit
) {
    val items = listOf(
        Triple(AppTab.HOME, "Home", Icons.Default.Home),
        Triple(AppTab.NOTES, "Notes", Icons.Default.MenuBook),
        Triple(AppTab.QNA, "Q&A", Icons.AutoMirrored.Filled.HelpOutline),
        Triple(AppTab.SYLLABUS, "Syllabus", Icons.Default.School),
        Triple(AppTab.NOTICES, "Notices", Icons.Default.Notifications)
    )
    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 10.dp,
        border = BorderStroke(0.5.dp, QuorikBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (tab, label, icon) ->
                val isSelected = activeTab == tab
                val bg = if (isSelected) QuorikBlueSoftBg else Color.Transparent
                val tint = if (isSelected) QuorikBluePrimary else QuorikBluePrimary.copy(alpha = 0.65f)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bg)
                        .clickable { onSelectTab(tab) }
                        .padding(vertical = 8.dp)
                        .testTag("bottom_nav_${tab.name.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = tint,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                        ),
                        color = tint
                    )
                }
            }
        }
    }
}

@Composable
fun QuorikDrawerContent(
    customPages: List<CustomPage>,
    onSelectTab: (AppTab) -> Unit,
    onOpenPage: (String) -> Unit,
    onOpenAddPage: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Brand Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(QuorikBluePrimary)
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Quorik",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Text(
                                text = "BA Arts Free Study Portal",
                                style = MaterialTheme.typography.labelMedium,
                                color = QuorikBlueTint
                            )
                        }
                        IconButton(
                            onClick = onCloseDrawer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Menu", tint = Color.White)
                        }
                    }
                }

                // Main Sections
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "MAIN SECTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    DrawerNavRow("Home", Icons.Default.Home, QuorikBluePrimary) {
                        onCloseDrawer(); onSelectTab(AppTab.HOME)
                    }
                    DrawerNavRow("Notes & Free PDFs", Icons.Default.MenuBook, QuorikBluePrimary) {
                        onCloseDrawer(); onSelectTab(AppTab.NOTES)
                    }
                    DrawerNavRow("Question & Bank", Icons.AutoMirrored.Filled.HelpOutline, Teal600) {
                        onCloseDrawer(); onSelectTab(AppTab.QNA)
                    }
                    DrawerNavRow("Syllabus", Icons.Default.School, Indigo600) {
                        onCloseDrawer(); onSelectTab(AppTab.SYLLABUS)
                    }
                    DrawerNavRow("Exam Notices", Icons.Default.Notifications, Amber500) {
                        onCloseDrawer(); onSelectTab(AppTab.NOTICES)
                    }
                    DrawerNavRow("Festivals", Icons.Default.AutoAwesome, Rose600) {
                        onCloseDrawer(); onSelectTab(AppTab.FESTIVALS)
                    }
                    DrawerNavRow("Holidays", Icons.Default.CalendarMonth, Emerald600) {
                        onCloseDrawer(); onSelectTab(AppTab.HOLIDAYS)
                    }
                }

                HorizontalDivider(color = QuorikSurfaceVariant)

                // Official & Admin-Managed Pages
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OFFICIAL PAGES",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400
                        )
                        Text(
                            text = "+ Add Page",
                            style = MaterialTheme.typography.labelMedium,
                            color = QuorikBluePrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    onCloseDrawer()
                                    onOpenAddPage()
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("drawer_add_page_btn")
                        )
                    }
                    customPages.forEach { page ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onCloseDrawer()
                                    onOpenPage(page.slug)
                                }
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = mapIconName(page.icon),
                                    contentDescription = page.title,
                                    tint = Slate400,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = page.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Slate700
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QuorikBackground)
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quorik Education", style = MaterialTheme.typography.labelMedium, color = Slate500)
                Text("● 100% Free Mode", style = MaterialTheme.typography.labelMedium, color = Emerald600)
            }
        }
    }
}

@Composable
private fun DrawerNavRow(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(19.dp))
        Text(text = label, style = MaterialTheme.typography.titleSmall, color = Slate700)
    }
}

@Composable
fun BookmarksDialog(
    savedNotes: List<StudyNote>,
    onOpenNote: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Saved Notes", style = MaterialTheme.typography.titleLarge, color = Slate900)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                }
            }
        },
        text = {
            if (savedNotes.isEmpty()) {
                Text(
                    "No saved notes yet. Tap Save while reading any study note.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 320.dp)
                ) {
                    items(savedNotes, key = { it.id }) { note ->
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
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Slate900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${note.paperCode} • ${note.semester}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate500
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = { onOpenNote(note.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary)
                                ) {
                                    Text("Read", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun AdminPinDialog(
    hasError: Boolean,
    onVerify: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text("Admin Access", style = MaterialTheme.typography.titleLarge, color = Slate900)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    placeholder = { Text("PIN (1234 or 9435149840)", fontSize = 13.sp) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )
                if (hasError) {
                    Text(
                        text = "Invalid PIN (Use 1234)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Rose600
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onVerify(pin) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                modifier = Modifier.testTag("unlock_admin_btn")
            ) {
                Text("Unlock Admin Panel", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Slate600)
            }
        }
    )
}

@Composable
fun AddSidebarPageDialog(
    onSave: (title: String, url: String, content: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text("+ Add New Sidebar Page", style = MaterialTheme.typography.titleLarge, color = Slate900)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Page Title (e.g. Scholarships)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Custom URL (optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Page Content") },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, url, content)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary)
            ) {
                Text("Save Page & Sync")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate600) }
        }
    )
}

@Composable
fun EventDetailDialog(
    event: EventDetailData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = QuorikBlueSoftBg
                ) {
                    Text(
                        text = event.dateLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = QuorikBluePrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Slate900
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (event.imagePath.isNotBlank() && File(event.imagePath).exists()) {
                    LocalFileImage(
                        path = event.imagePath,
                        contentDescription = event.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate600
                )
                val validGallery = event.extraImages.filter { it.isNotBlank() && File(it).exists() }
                if (validGallery.isNotEmpty()) {
                    Text(
                        text = "MORE PHOTOS (${validGallery.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400
                    )
                    validGallery.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { imgPath ->
                                LocalFileImage(
                                    path = imgPath,
                                    contentDescription = event.title,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(92.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QuorikBluePrimary)
            ) {
                Text("Close")
            }
        }
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InAppPdfWebViewDialog(
    pdfModal: PdfModalData,
    onDismiss: () -> Unit
) {
    val resolvedUrl = remember(pdfModal.url) {
        val driveId = Regex("/d/([a-zA-Z0-9_-]+)").find(pdfModal.url)?.groupValues?.getOrNull(1)
            ?: Regex("[?&]id=([a-zA-Z0-9_-]+)").find(pdfModal.url)?.groupValues?.getOrNull(1)
        if (driveId != null) "https://drive.google.com/file/d/$driveId/preview" else pdfModal.url
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(QuorikHeaderBrush)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "QUORIK IN-APP VIEWER",
                            style = MaterialTheme.typography.labelSmall,
                            color = QuorikBlueTint
                        )
                        Text(
                            text = pdfModal.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Viewer", tint = Color.White)
                    }
                }
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            webViewClient = WebViewClient()
                            loadUrl(resolvedUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
