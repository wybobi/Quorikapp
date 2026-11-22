package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.MediaStorageUtil
import com.example.data.PdfExportUtil
import com.example.ui.theme.QuorikBackground
import com.example.ui.theme.Slate900
import com.example.viewmodel.AdminSection
import com.example.viewmodel.QuorikUiState
import com.example.viewmodel.QuorikViewModel
import kotlinx.coroutines.launch

private sealed class ImagePickTarget {
    data class MainBanner(val index: Int) : ImagePickTarget()
    data class PageBanner(val pageKey: String) : ImagePickTarget()
    data object QnaCard : ImagePickTarget()
    data object NoticeCard : ImagePickTarget()
    data object EventThumb : ImagePickTarget()
}

private enum class PdfPickTarget {
    NOTE, SYLLABUS
}

@Composable
fun AdminPanelRoot(
    state: QuorikUiState,
    viewModel: QuorikViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendingImageTarget by remember { mutableStateOf<ImagePickTarget?>(null) }
    var pendingPdfTarget by remember { mutableStateOf<PdfPickTarget?>(null) }

    var pickedQnaImagePath by remember { mutableStateOf("") }
    var pickedNoticeImagePath by remember { mutableStateOf("") }
    var pickedEventThumbPath by remember { mutableStateOf("") }
    var pickedEventGalleryPaths by remember { mutableStateOf<List<String>>(emptyList()) }
    var pickedNotePdfPath by remember { mutableStateOf("") }
    var pickedSyllabusPdfPath by remember { mutableStateOf("") }

    // Zero-permission Android Photo Picker (single image)
    val singleImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val target = pendingImageTarget
        pendingImageTarget = null
        if (uri != null && target != null) {
            scope.launch {
                val savedPath = MediaStorageUtil.saveCompressedImageFromUri(context, uri)
                if (savedPath == null) {
                    viewModel.showToast("Could not process selected image")
                    return@launch
                }
                when (target) {
                    is ImagePickTarget.MainBanner -> viewModel.updateMainBannerImage(target.index, savedPath)
                    is ImagePickTarget.PageBanner -> viewModel.updatePageBannerImage(target.pageKey, savedPath)
                    ImagePickTarget.QnaCard -> {
                        pickedQnaImagePath = savedPath
                        viewModel.showToast("Q&A image selected!")
                    }
                    ImagePickTarget.NoticeCard -> {
                        pickedNoticeImagePath = savedPath
                        viewModel.showToast("Notice photo selected!")
                    }
                    ImagePickTarget.EventThumb -> {
                        pickedEventThumbPath = savedPath
                        viewModel.showToast("Event thumbnail selected!")
                    }
                }
            }
        }
    }

    // Zero-permission Android Photo Picker (multiple gallery photos)
    val multiImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8)
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                val paths = uris.mapNotNull { MediaStorageUtil.saveCompressedImageFromUri(context, it, 900) }
                if (paths.isNotEmpty()) {
                    pickedEventGalleryPaths = paths
                    viewModel.showToast("Added ${paths.size} gallery photos!")
                }
            }
        }
    }

    // SAF OpenDocument for PDF upload
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        val target = pendingPdfTarget
        pendingPdfTarget = null
        if (uri != null && target != null) {
            scope.launch {
                val savedPath = MediaStorageUtil.savePdfFromUri(context, uri)
                if (savedPath == null) {
                    viewModel.showToast("Could not import PDF file")
                    return@launch
                }
                when (target) {
                    PdfPickTarget.NOTE -> {
                        pickedNotePdfPath = savedPath
                        viewModel.showToast("Note PDF attached!")
                    }
                    PdfPickTarget.SYLLABUS -> {
                        pickedSyllabusPdfPath = savedPath
                        viewModel.showToast("Syllabus PDF attached!")
                    }
                }
            }
        }
    }

    // SAF OpenDocument for JSON backup import
    val jsonImportPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val text = MediaStorageUtil.readTextFromUri(context, uri)
                if (text != null) {
                    viewModel.importDatabaseJson(text)
                } else {
                    viewModel.showToast("Could not read JSON file")
                }
            }
        }
    }

    fun launchPhotoPicker(target: ImagePickTarget) {
        pendingImageTarget = target
        singleImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun launchPdfPicker(target: PdfPickTarget) {
        pendingPdfTarget = target
        pdfPicker.launch(arrayOf("application/pdf"))
    }

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
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!state.adminUnlocked) {
                AdminLoginScreen(
                    hasError = state.adminLoginError,
                    onUnlock = { viewModel.unlockMainAdmin(it) },
                    onClearError = { viewModel.clearAdminLoginError() },
                    onOpenStudentApp = { viewModel.enterStudentApp() }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(QuorikBackground)
                ) {
                    AdminDashboardHeader(
                        onOpenStudentApp = { viewModel.enterStudentApp() },
                        onExportJson = {
                            val json = viewModel.exportDatabaseJson()
                            val ok = PdfExportUtil.exportAndShareJsonBackup(context, json)
                            viewModel.showToast(if (ok) "Exported quorik_by_bobi_backup.json" else "Export failed")
                        },
                        onImportJson = {
                            jsonImportPicker.launch(arrayOf("application/json", "text/plain", "*/*"))
                        },
                        onLock = { viewModel.lockMainAdmin() }
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .navigationBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (state.adminActiveSection != AdminSection.HOME) {
                            AdminSectionTopBar(
                                title = state.adminActiveSection.title,
                                onBackHome = { viewModel.selectAdminSection(AdminSection.HOME) }
                            )
                        }

                        when (state.adminActiveSection) {
                            AdminSection.HOME -> AdminHomeWorkspace(
                                state = state,
                                onSelectSection = { viewModel.selectAdminSection(it) },
                                onResetDefaults = { viewModel.resetToDefaults() }
                            )

                            AdminSection.BANNERS -> AdminBannersSection(
                                state = state,
                                onAddMainBanner = { viewModel.addMainBanner() },
                                onPickBannerImage = { idx -> launchPhotoPicker(ImagePickTarget.MainBanner(idx)) },
                                onClearBannerImage = { idx -> viewModel.updateMainBannerImage(idx, "") },
                                onSaveBannerMeta = { idx, b, t, sub, tgt ->
                                    viewModel.saveMainBannerMeta(idx, b, t, sub, tgt)
                                },
                                onDeleteMainBanner = { idx -> viewModel.deleteMainBanner(idx) },
                                onPickPageBannerImage = { key -> launchPhotoPicker(ImagePickTarget.PageBanner(key)) },
                                onClearPageBannerImage = { key -> viewModel.updatePageBannerImage(key, "") }
                            )

                            AdminSection.NOTES -> AdminNotesSection(
                                state = state,
                                pickedPdfPath = pickedNotePdfPath,
                                onClearPickedPdf = { pickedNotePdfPath = "" },
                                onPickNotePdf = { launchPdfPicker(PdfPickTarget.NOTE) },
                                onUpsertNote = { id, t, c, lvl, subj, sem, med, pdf, sum, u, body ->
                                    viewModel.upsertNote(id, t, c, lvl, subj, sem, med, pdf, sum, u, body)
                                },
                                onDeleteNote = { viewModel.deleteNote(it) },
                                onAddSubject = { viewModel.addSubject(it) },
                                onRemoveSubject = { viewModel.removeSubject(it) },
                                onAddLevel = { viewModel.addLevel(it) },
                                onRemoveLevel = { viewModel.removeLevel(it) }
                            )

                            AdminSection.QNA -> AdminQnaSection(
                                state = state,
                                pickedImagePath = pickedQnaImagePath,
                                onClearPickedImage = { pickedQnaImagePath = "" },
                                onPickQnaImage = { launchPhotoPicker(ImagePickTarget.QnaCard) },
                                onUpsertQna = { id, subj, sem, m, lvl, med, q, desc, a, img ->
                                    viewModel.upsertQna(id, subj, sem, m, lvl, med, q, desc, a, img)
                                },
                                onDeleteQna = { viewModel.deleteQna(it) }
                            )

                            AdminSection.NOTICES_SYLLABUS -> AdminNoticesSyllabusSection(
                                state = state,
                                pickedNoticeImagePath = pickedNoticeImagePath,
                                pickedSyllabusPdfPath = pickedSyllabusPdfPath,
                                onClearPickedNoticeImage = { pickedNoticeImagePath = "" },
                                onClearPickedSyllabusPdf = { pickedSyllabusPdfPath = "" },
                                onPickNoticeImage = { launchPhotoPicker(ImagePickTarget.NoticeCard) },
                                onPickSyllabusPdf = { launchPdfPicker(PdfPickTarget.SYLLABUS) },
                                onAddNotice = { t, cat, d, auth, desc, img ->
                                    viewModel.addNotice(t, cat, d, auth, desc, img)
                                },
                                onDeleteNotice = { viewModel.deleteNotice(it) },
                                onAddSemester = { name, desc -> viewModel.addSemester(name, desc) },
                                onRemoveSemester = { viewModel.removeSemester(it) },
                                onAddSubject = { viewModel.addSubject(it) },
                                onRenameSubject = { idx, newName -> viewModel.renameSubject(idx, newName) },
                                onRemoveSubject = { viewModel.removeSubject(it) },
                                onUpsertSyllabus = { id, t, c, cr, subj, sem, pdf, u ->
                                    viewModel.upsertSyllabus(id, t, c, cr, subj, sem, pdf, u)
                                },
                                onDeleteSyllabus = { viewModel.deleteSyllabus(it) }
                            )

                            AdminSection.EVENTS -> AdminEventsSection(
                                state = state,
                                pickedThumbPath = pickedEventThumbPath,
                                pickedGalleryPaths = pickedEventGalleryPaths,
                                onClearPickedThumb = { pickedEventThumbPath = "" },
                                onClearPickedGallery = { pickedEventGalleryPaths = emptyList() },
                                onPickEventThumb = { launchPhotoPicker(ImagePickTarget.EventThumb) },
                                onPickEventGallery = {
                                    multiImagePicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                onAddEvent = { isFest, t, d, desc, thumb, extra ->
                                    viewModel.addCalendarEvent(isFest, t, d, desc, thumb, extra)
                                },
                                onDeleteEvent = { isFest, id -> viewModel.deleteCalendarEvent(isFest, id) }
                            )

                            AdminSection.PAGES -> AdminPagesSection(
                                state = state,
                                onUpsertPage = { id, t, sl, url, c ->
                                    viewModel.upsertCustomPage(id, t, sl, url, c)
                                },
                                onDeletePage = { viewModel.deleteCustomPage(it) }
                            )

                            AdminSection.MCQ -> AdminMcqSection(
                                state = state,
                                onUpsertMcq = { id, subj, q, opts, ci, exp ->
                                    viewModel.upsertMcq(id, subj, q, opts, ci, exp)
                                },
                                onDeleteMcq = { viewModel.deleteMcq(it) }
                            )
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
