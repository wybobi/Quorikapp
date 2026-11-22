package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.PdfExportUtil
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.QuorikViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private val isSpeakingState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        tts = TextToSpeech(this, this)

        setContent {
            QuorikTheme {
                QuorikAppRoot(
                    isReadingAloud = isSpeakingState.value,
                    onToggleAudio = { text -> toggleReadAloud(text) },
                    onStopAudio = { stopReadAloud() }
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isSpeakingState.value = true
                }

                override fun onDone(utteranceId: String?) {
                    isSpeakingState.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    isSpeakingState.value = false
                }
            })
            ttsReady = true
        }
    }

    private fun toggleReadAloud(text: String) {
        if (!ttsReady) return
        if (isSpeakingState.value) {
            stopReadAloud()
        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "quorik_reader_tts")
            isSpeakingState.value = true
        }
    }

    private fun stopReadAloud() {
        tts?.stop()
        isSpeakingState.value = false
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
private fun QuorikAppRoot(
    isReadingAloud: Boolean,
    onToggleAudio: (String) -> Unit,
    onStopAudio: () -> Unit,
    viewModel: QuorikViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Stop TTS when leaving Study Reader
    LaunchedEffect(state.activeReaderNoteId, state.activeReaderTopicIdx, state.activeTab) {
        if (state.activeReaderNoteId == null || state.activeTab != AppTab.NOTES) {
            onStopAudio()
        }
    }

    // Show Toast messages via Snackbar
    LaunchedEffect(state.toastMessage) {
        val msg = state.toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearToast()
        }
    }

    // Handle Android Back button across Drawer, Modals, Full-screen Reader, and drill-down views
    val shouldInterceptBack = drawerState.isOpen ||
        state.activePdfModal != null ||
        state.activeEventDetail != null ||
        state.showAddSidebarPageModal ||
        state.showAdminPinModal ||
        state.showBookmarksModal ||
        state.activeTab != AppTab.HOME

    BackHandler(enabled = shouldInterceptBack) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            onStopAudio()
            viewModel.handleBackNavigation()
        }
    }

    val isFullScreenReader = state.activeTab == AppTab.NOTES && state.activeReaderNoteId != null
    val showBottomBar = !isFullScreenReader && state.activeTab != AppTab.ADMIN

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !isFullScreenReader,
            drawerContent = {
                QuorikDrawerContent(
                    customPages = state.db.pages,
                    onSelectTab = { viewModel.switchTab(it, true) },
                    onOpenPage = { viewModel.openCustomPage(it) },
                    onOpenAddPage = { viewModel.setShowAddSidebarPageModal(true) },
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        ) {
            Scaffold(
                containerColor = QuorikBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = Slate900,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                },
                bottomBar = {
                    if (showBottomBar) {
                        QuorikBottomNavBar(
                            activeTab = state.activeTab,
                            onSelectTab = { viewModel.switchTab(it, true) }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (state.activeTab) {
                        AppTab.HOME -> HomeScreen(
                            state = state,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onBrandTap = { viewModel.onBrandTitleTap() },
                            onOpenBookmarks = { viewModel.setShowBookmarksModal(true) },
                            onSearchChange = { viewModel.updateSearchQuery(it) },
                            onQuickFilterClick = { viewModel.switchTab(AppTab.NOTES, true) },
                            onBannerSelect = { viewModel.setBannerIndex(it) },
                            onBannerAction = { viewModel.handleBannerClick(it) },
                            onSelectTab = { viewModel.switchTab(it, true) },
                            onOpenNote = { viewModel.openStudyReader(it, 0) },
                            onOpenQna = { viewModel.openQnaDetail(it) },
                            onOpenSyllabus = { sem, subj -> viewModel.openSyllabusPaper(sem, subj) },
                            onOpenEventDetail = { title, date, desc -> viewModel.openEventDetail(title, date, desc) }
                        )

                        AppTab.NOTES -> NotesScreen(
                            state = state,
                            isReadingAloud = isReadingAloud,
                            onBack = { viewModel.handleBackNavigation() },
                            onSelectSemester = { viewModel.selectNotesSemester(it) },
                            onSelectSubject = { viewModel.selectNotesSubject(it) },
                            onSearchChange = { viewModel.updateNotesSearchQuery(it) },
                            onOpenReader = { viewModel.openStudyReader(it, 0) },
                            onCloseReader = {
                                onStopAudio()
                                viewModel.closeStudyReader()
                            },
                            onSelectTopic = {
                                onStopAudio()
                                viewModel.selectReaderTopic(it)
                            },
                            onToggleHighlight = { viewModel.toggleHighlightMode(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onToggleAudio = onToggleAudio,
                            onToggleMarginPanel = { viewModel.toggleReaderMarginPanel() },
                            onAddMarginNote = { id, idx, txt -> viewModel.addMarginNote(id, idx, txt) },
                            onDeleteMarginNote = { id, idx, mIdx -> viewModel.deleteMarginNote(id, idx, mIdx) },
                            onRateNote = { id, stars -> viewModel.rateItem(id, stars) },
                            onDownloadNotePdf = { note ->
                                if (note.pdfUrl.isNotBlank()) {
                                    viewModel.openPdfModal(note.title, note.pdfUrl)
                                } else {
                                    val sections = note.topics.map {
                                        PdfExportUtil.PdfSection(it.title, it.content)
                                    }.ifEmpty {
                                        listOf(PdfExportUtil.PdfSection(note.title, note.summary))
                                    }
                                    val ok = PdfExportUtil.exportAndSharePdf(
                                        context = context,
                                        title = note.title,
                                        subtitle = "${note.subject} • ${note.semester} • ${note.paperCode}",
                                        code = note.paperCode,
                                        sections = sections
                                    )
                                    viewModel.showToast(if (ok) "PDF ready to save or share!" else "Could not generate PDF")
                                }
                            }
                        )

                        AppTab.QNA -> QnaScreen(
                            state = state,
                            filteredMcqs = viewModel.getFilteredMcqs(),
                            onBack = { viewModel.handleBackNavigation() },
                            onSetSubMode = { viewModel.setQnaSubMode(it) },
                            onSelectSemester = { viewModel.selectQnaSemester(it) },
                            onSelectSubject = { viewModel.selectQnaSubject(it) },
                            onSelectMarksFilter = { viewModel.selectMarksFilter(it) },
                            onOpenQnaDetail = { viewModel.openQnaDetail(it) },
                            onCloseQnaDetail = { viewModel.closeQnaDetail() },
                            onRateQna = { id, stars -> viewModel.rateItem(id, stars) },
                            onDownloadAllQnaPdf = {
                                val sections = state.db.qna.mapIndexed { idx, q ->
                                    PdfExportUtil.PdfSection("Q${idx + 1}. [${q.marks}] ${q.question}", q.answer)
                                }
                                val ok = PdfExportUtil.exportAndSharePdf(
                                    context = context,
                                    title = "BA Arts Solved Question & Bank",
                                    subtitle = "1, 2, 5 & 10 Marks Important Answers",
                                    code = "BA-QNA",
                                    sections = sections
                                )
                                viewModel.showToast(if (ok) "Q&A PDF ready!" else "Could not generate PDF")
                            },
                            onDownloadSingleQnaPdf = { item ->
                                val ok = PdfExportUtil.exportAndSharePdf(
                                    context = context,
                                    title = item.question,
                                    subtitle = "${item.marks} • ${item.subject} • ${item.semester}",
                                    code = "QNA-${item.marks}",
                                    sections = listOf(PdfExportUtil.PdfSection("Answer", item.answer))
                                )
                                viewModel.showToast(if (ok) "Answer PDF ready!" else "Could not generate PDF")
                            },
                            onSetMcqSubjectFilter = { viewModel.setMcqSubjectFilter(it) },
                            onSelectMcqOption = { id, idx -> viewModel.selectMcqOption(id, idx) },
                            onToggleMcqFlag = { viewModel.toggleMcqFlag(it) },
                            onToggleMcqGrid = { viewModel.toggleMcqGridPalette() },
                            onJumpMcq = { viewModel.jumpToMcq(it) }
                        )

                        AppTab.SYLLABUS -> SyllabusScreen(
                            state = state,
                            onBack = { viewModel.handleBackNavigation() },
                            onSelectSemester = { viewModel.selectSyllabusSemester(it) },
                            onSelectSubject = { viewModel.selectSyllabusSubject(it) },
                            onRateSyllabus = { id, stars -> viewModel.rateItem(id, stars) },
                            onOpenSyllabusPdf = { syl ->
                                if (syl.pdfUrl.isNotBlank()) {
                                    viewModel.openPdfModal("${syl.paperCode}: ${syl.title}", syl.pdfUrl)
                                } else {
                                    val ok = PdfExportUtil.exportAndSharePdf(
                                        context = context,
                                        title = "${syl.paperCode}: ${syl.title}",
                                        subtitle = "${syl.subject} • ${syl.semester} • ${syl.credits}",
                                        code = syl.paperCode,
                                        sections = listOf(
                                            PdfExportUtil.PdfSection(
                                                "Official Course Units",
                                                syl.units.joinToString("\n") { "• $it" }
                                            )
                                        )
                                    )
                                    viewModel.showToast(if (ok) "Syllabus PDF ready!" else "Could not generate PDF")
                                }
                            }
                        )

                        AppTab.NOTICES -> NoticesScreen(
                            state = state,
                            onBack = { viewModel.switchTab(AppTab.HOME) },
                            onSelectCategory = { viewModel.selectNoticeCategory(it) },
                            onOpenDetail = { n ->
                                viewModel.openEventDetail(n.title, "${n.category} • ${n.date}", n.description)
                            },
                            onDownloadNoticePdf = { n ->
                                val ok = PdfExportUtil.exportAndSharePdf(
                                    context = context,
                                    title = n.title,
                                    subtitle = "${n.authority} • ${n.date}",
                                    code = "NOTICE",
                                    sections = listOf(PdfExportUtil.PdfSection(n.category, n.description))
                                )
                                viewModel.showToast(if (ok) "Notice PDF ready!" else "Could not generate PDF")
                            }
                        )

                        AppTab.FESTIVALS -> EventsCalendarScreen(
                            title = "Festivals",
                            badgeText = "Assam & National",
                            badgeBg = Rose50,
                            badgeTextColor = Rose600,
                            banner = state.db.pageBanners["festivals"],
                            events = state.db.festivals,
                            isFestival = true,
                            onBack = { viewModel.switchTab(AppTab.HOME) },
                            onOpenEvent = { ev -> viewModel.openEventDetail(ev.title, "Date: ${ev.date}", ev.description) }
                        )

                        AppTab.HOLIDAYS -> EventsCalendarScreen(
                            title = "Holidays",
                            badgeText = "2026–2027",
                            badgeBg = Emerald50,
                            badgeTextColor = Teal700,
                            banner = state.db.pageBanners["holidays"],
                            events = state.db.holidays,
                            isFestival = false,
                            onBack = { viewModel.switchTab(AppTab.HOME) },
                            onOpenEvent = { ev -> viewModel.openEventDetail(ev.title, "Date: ${ev.date}", ev.description) }
                        )

                        AppTab.PAGE -> {
                            val page = state.db.pages.find { it.slug == state.activePageSlug }
                                ?: state.db.pages.firstOrNull()
                            if (page != null) {
                                CustomPageScreen(
                                    page = page,
                                    onBack = { viewModel.switchTab(AppTab.HOME) },
                                    onCopyLink = { link ->
                                        PdfExportUtil.copyToClipboard(context, "Quorik Page Link", link)
                                        viewModel.rememberCopiedLink(link)
                                        viewModel.showToast("Copied link: $link")
                                    },
                                    onOpenLinkedUrl = { url ->
                                        if (url.startsWith("#/page/")) {
                                            viewModel.openCustomPage(url.removePrefix("#/page/"))
                                        } else {
                                            try {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                            } catch (_: Exception) {
                                                viewModel.showToast("Invalid link URL")
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        AppTab.ADMIN -> AdminScreen(
                            state = state,
                            onSelectAdminTab = { viewModel.selectAdminTab(it) },
                            onExitToStudent = { viewModel.switchTab(AppTab.HOME) },
                            onResetDefaults = { viewModel.resetToDefaults() },
                            onExportJson = {
                                val json = viewModel.exportDatabaseJson()
                                val ok = PdfExportUtil.exportAndShareJsonBackup(context, json)
                                viewModel.showToast(if (ok) "Exported quorik_cms_backup.json" else "Export failed")
                            },
                            onCopyLink = { link ->
                                PdfExportUtil.copyToClipboard(context, "Quorik Link", link)
                                viewModel.rememberCopiedLink(link)
                                viewModel.showToast("Copied: $link")
                            },
                            onReadClipboardOrLast = {
                                PdfExportUtil.readFromClipboard(context)?.takeIf { it.isNotBlank() }
                                    ?: state.lastCopiedLink
                            },
                            onSaveBanners = { viewModel.saveBanners(it) },
                            onSavePage = { t, sl, url, c -> viewModel.addOrUpdateCustomPage(t, sl, url, c) },
                            onUpdatePageUrl = { id, url -> viewModel.updatePageExternalUrl(id, url) },
                            onDeletePage = { viewModel.deleteCustomPage(it) },
                            onViewPage = { viewModel.openCustomPage(it) },
                            onAddNote = { t, c, s, sem, med, unit, sum, body ->
                                viewModel.addNote(t, c, s, sem, med, unit, sum, body)
                            },
                            onDeleteNote = { viewModel.deleteNote(it) },
                            onAddQna = { s, sem, m, med, q, a -> viewModel.addQna(s, sem, m, med, q, a) },
                            onDeleteQna = { viewModel.deleteQna(it) },
                            onAddMcq = { s, q, opts, ci, exp -> viewModel.addMcq(s, q, opts, ci, exp) },
                            onDeleteMcq = { viewModel.deleteMcq(it) },
                            onAddSyllabus = { t, c, cr, s, sem, u -> viewModel.addSyllabus(t, c, cr, s, sem, u) },
                            onDeleteSyllabus = { viewModel.deleteSyllabus(it) },
                            onAddNotice = { t, cat, d, auth, desc -> viewModel.addNotice(t, cat, d, auth, desc) },
                            onDeleteNotice = { viewModel.deleteNotice(it) }
                        )
                    }
                }
            }
        }

        // Modals
        if (state.showBookmarksModal) {
            val savedNotes = state.db.notes.filter { state.userPrefs.bookmarks.contains(it.id) }
            BookmarksDialog(
                savedNotes = savedNotes,
                onOpenNote = { noteId ->
                    viewModel.setShowBookmarksModal(false)
                    viewModel.openStudyReader(noteId, 0)
                },
                onDismiss = { viewModel.setShowBookmarksModal(false) }
            )
        }

        if (state.showAdminPinModal) {
            AdminPinDialog(
                hasError = state.adminPinError,
                onVerify = { viewModel.verifyAdminPin(it) },
                onDismiss = { viewModel.closeAdminPinModal() }
            )
        }

        if (state.showAddSidebarPageModal) {
            AddSidebarPageDialog(
                onSave = { title, url, content ->
                    viewModel.addOrUpdateCustomPage(title, "", url, content)
                },
                onDismiss = { viewModel.setShowAddSidebarPageModal(false) }
            )
        }

        state.activeEventDetail?.let { ev ->
            EventDetailDialog(
                event = ev,
                onDismiss = { viewModel.closeEventDetail() }
            )
        }

        state.activePdfModal?.let { pdf ->
            InAppPdfWebViewDialog(
                pdfModal = pdf,
                onDismiss = { viewModel.closePdfModal() }
            )
        }

        // 2-Second Branded Text-Only Splash Screen
        AnimatedVisibility(
            visible = state.showSplash,
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(QuorikBluePrimary)
                    .clickable { viewModel.dismissSplash() }
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Quorik",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "BA Arts Free Notes & Question Answer",
                        style = MaterialTheme.typography.titleSmall,
                        color = QuorikBlueTint,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
