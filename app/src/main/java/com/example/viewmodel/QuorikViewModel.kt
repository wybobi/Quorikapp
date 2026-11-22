package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTab {
    HOME, NOTES, QNA, SYLLABUS, NOTICES, FESTIVALS, HOLIDAYS, PAGE
}

enum class QnaSubMode {
    DESCRIPTIVE, MCQ
}

enum class AdminSection(val title: String) {
    HOME("Admin Dashboard"),
    BANNERS("01 • Banners"),
    NOTES("02 • Notes & PDFs"),
    QNA("03 • Question & Answer"),
    NOTICES_SYLLABUS("04 • Syllabus & Notices"),
    EVENTS("05 • Festivals & Holidays"),
    PAGES("06 • Sidebar Pages & URLs"),
    MCQ("07 • MCQ Quiz Bank")
}

enum class HighlightMode {
    NONE, YELLOW, GREEN
}

data class EventDetailData(
    val title: String,
    val dateLabel: String,
    val description: String,
    val imagePath: String = "",
    val extraImages: List<String> = emptyList()
)

data class PdfModalData(
    val title: String,
    val url: String
)

data class QuorikUiState(
    // Mode switcher: Launches into Quorik by BOBI Master Admin Panel, 1-tap switch to Student App
    val isAdminAppMode: Boolean = true,
    val adminUnlocked: Boolean = false,
    val adminLoginError: Boolean = false,
    val adminActiveSection: AdminSection = AdminSection.HOME,
    // Shared synced database & student preferences
    val db: QuorikDatabase = DefaultQuorikData.createDefaultDatabase(),
    val userPrefs: UserPrefs = UserPrefs(),
    // Student App navigation state
    val activeTab: AppTab = AppTab.HOME,
    val searchQuery: String = "",
    val bannerIndex: Int = 0,
    // Notes state
    val selectedNotesSemester: String? = null,
    val selectedNotesSubject: String? = null,
    val notesSearchQuery: String = "",
    val activeReaderNoteId: String? = null,
    val activeReaderTopicIdx: Int = 0,
    val readerHighlightMode: HighlightMode = HighlightMode.NONE,
    val showReaderMarginPanel: Boolean = false,
    // QnA & MCQ state
    val qnaSubMode: QnaSubMode = QnaSubMode.DESCRIPTIVE,
    val selectedQnaSemester: String? = null,
    val selectedQnaSubject: String? = null,
    val selectedMarksFilter: String = "ALL",
    val activeQnaDetailId: String? = null,
    val mcqSubjectFilter: String = "ALL",
    val mcqCurrentIdx: Int = 0,
    val mcqSelectedOptions: Map<String, Int> = emptyMap(),
    val mcqFlaggedIds: Set<String> = emptySet(),
    val mcqShowGridPalette: Boolean = false,
    val mcqSecondsLeft: Int = 900,
    // Syllabus state
    val selectedSyllabusSemester: String? = null,
    val selectedSyllabusSubject: String? = null,
    // Notices state
    val selectedNoticeCategory: String = "ALL",
    // Custom Page state
    val activePageSlug: String = "about-us",
    // Modals
    val showBookmarksModal: Boolean = false,
    val showAdminPinModal: Boolean = false,
    val adminPinError: Boolean = false,
    val showAddSidebarPageModal: Boolean = false,
    val activeEventDetail: EventDetailData? = null,
    val activePdfModal: PdfModalData? = null,
    val lastCopiedLink: String = "#/page/about-us",
    // Toast / Snackbar
    val toastMessage: String? = null
)

class QuorikViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuorikRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(QuorikUiState())
    val uiState: StateFlow<QuorikUiState> = _uiState.asStateFlow()

    private var logoTapCount = 0
    private var lastLogoTapTime = 0L

    init {
        viewModelScope.launch {
            val loadedDb = repository.loadDatabase()
            val loadedPrefs = repository.loadUserPrefs()
            _uiState.update { it.copy(db = loadedDb, userPrefs = loadedPrefs) }
        }
        // 3-second auto-sliding home banner in Student App
        viewModelScope.launch {
            while (true) {
                delay(3000)
                val s = _uiState.value
                if (!s.isAdminAppMode && s.activeTab == AppTab.HOME && s.db.banners.isNotEmpty()) {
                    _uiState.update {
                        it.copy(bannerIndex = (it.bannerIndex + 1) % it.db.banners.size)
                    }
                }
            }
        }
        // 1-second MCQ countdown timer
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val s = _uiState.value
                if (!s.isAdminAppMode && s.activeTab == AppTab.QNA && s.qnaSubMode == QnaSubMode.MCQ) {
                    _uiState.update {
                        val nextSec = if (it.mcqSecondsLeft > 0) it.mcqSecondsLeft - 1 else 900
                        it.copy(mcqSecondsLeft = nextSec)
                    }
                }
            }
        }
    }

    private fun todayDateLabel(): String =
        SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date())

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    private fun persistDb(newDb: QuorikDatabase, message: String? = null) {
        _uiState.update { it.copy(db = newDb) }
        viewModelScope.launch { repository.saveDatabase(newDb) }
        if (message != null) showToast(message)
    }

    private fun persistPrefs(newPrefs: UserPrefs) {
        _uiState.update { it.copy(userPrefs = newPrefs) }
        viewModelScope.launch { repository.saveUserPrefs(newPrefs) }
    }

    // ==================== MAIN ADMIN PANEL AUTH & NAVIGATION ====================

    fun unlockMainAdmin(password: String) {
        if (password.trim() == "9435149840") {
            _uiState.update {
                it.copy(
                    isAdminAppMode = true,
                    adminUnlocked = true,
                    adminLoginError = false,
                    adminActiveSection = AdminSection.HOME
                )
            }
            showToast("Admin Panel Unlocked • Real-Time Sync Active")
        } else {
            _uiState.update { it.copy(adminLoginError = true) }
        }
    }

    fun clearAdminLoginError() {
        _uiState.update { it.copy(adminLoginError = false) }
    }

    fun lockMainAdmin() {
        _uiState.update {
            it.copy(
                isAdminAppMode = true,
                adminUnlocked = false,
                adminLoginError = false,
                adminActiveSection = AdminSection.HOME
            )
        }
        showToast("Admin Panel Locked")
    }

    fun enterStudentApp() {
        _uiState.update {
            it.copy(
                isAdminAppMode = false,
                activeTab = AppTab.HOME,
                activeReaderNoteId = null,
                activeQnaDetailId = null
            )
        }
    }

    fun enterAdminApp() {
        _uiState.update {
            it.copy(
                isAdminAppMode = true,
                showAdminPinModal = false,
                adminPinError = false
            )
        }
    }

    fun selectAdminSection(section: AdminSection) {
        _uiState.update { it.copy(adminActiveSection = section) }
    }

    // ==================== 01. BANNERS CRUD ====================

    fun addMainBanner() {
        val current = _uiState.value.db.banners
        val nextNum = current.size + 1
        val newSlide = BannerSlide(
            id = "ban-${System.currentTimeMillis()}",
            badge = "Highlight",
            title = "New Study Banner $nextNum",
            subtitle = "BA Arts Free Notes & PDFs",
            gradientKey = "blue",
            icon = "book-open-check",
            targetType = "tab",
            targetId = "notes",
            imagePath = ""
        )
        persistDb(_uiState.value.db.copy(banners = current + newSlide), "New banner slot added!")
    }

    fun updateMainBannerImage(index: Int, imagePath: String) {
        val list = _uiState.value.db.banners.toMutableList()
        if (index !in list.indices) return
        list[index] = list[index].copy(imagePath = imagePath)
        persistDb(_uiState.value.db.copy(banners = list), "Banner ${index + 1} image uploaded & synced!")
    }

    fun saveMainBannerMeta(index: Int, badge: String, title: String, subtitle: String, targetId: String) {
        val list = _uiState.value.db.banners.toMutableList()
        if (index !in list.indices) return
        val cleanTarget = targetId.trim().ifBlank { list[index].targetId }
        val targetType = when {
            cleanTarget.startsWith("#/page/") -> "page"
            cleanTarget in listOf("notes", "qna", "syllabus", "notices", "festivals", "holidays") -> "tab"
            else -> "note"
        }
        list[index] = list[index].copy(
            badge = badge.trim().ifBlank { list[index].badge },
            title = title.trim().ifBlank { list[index].title },
            subtitle = subtitle.trim(),
            targetType = targetType,
            targetId = cleanTarget
        )
        persistDb(_uiState.value.db.copy(banners = list), "Banner ${index + 1} saved & synced!")
    }

    fun deleteMainBanner(index: Int) {
        val list = _uiState.value.db.banners.toMutableList()
        if (index !in list.indices) return
        list.removeAt(index)
        val newIdx = if (list.isEmpty()) 0 else _uiState.value.bannerIndex.coerceAtMost(list.lastIndex)
        _uiState.update { it.copy(bannerIndex = newIdx) }
        persistDb(_uiState.value.db.copy(banners = list), "Banner deleted & synced!")
    }

    fun updatePageBannerImage(pageKey: String, imagePath: String) {
        val map = _uiState.value.db.pageBanners.toMutableMap()
        val existing = map[pageKey] ?: return
        map[pageKey] = existing.copy(imagePath = imagePath)
        val msg = if (imagePath.isBlank()) "Page banner image removed!" else "Page banner uploaded & synced!"
        persistDb(_uiState.value.db.copy(pageBanners = map), msg)
    }

    // ==================== 02. NOTES, LEVELS & SUBJECTS CRUD ====================

    fun upsertNote(
        editingId: String?,
        title: String,
        paperCode: String,
        level: String,
        subject: String,
        semester: String,
        medium: String,
        pdfUrl: String,
        summary: String,
        unitHeading: String,
        body: String
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val cleanSummary = summary.trim().ifBlank { cleanTitle }
        val cleanUnit = unitHeading.trim().ifBlank { "Unit 1" }
        val cleanBody = body.trim().ifBlank { cleanSummary }

        val notes = _uiState.value.db.notes.toMutableList()
        if (editingId != null) {
            val idx = notes.indexOfFirst { it.id == editingId }
            if (idx >= 0) {
                val old = notes[idx]
                val updatedTopics = if (old.topics.size > 1) {
                    listOf(StudyTopic(cleanUnit, cleanSummary, cleanBody)) + old.topics.drop(1)
                } else {
                    listOf(StudyTopic(cleanUnit, cleanSummary, cleanBody))
                }
                notes[idx] = old.copy(
                    title = cleanTitle,
                    paperCode = paperCode.trim().ifBlank { "BA-NOTE" },
                    level = level,
                    subject = subject,
                    semester = semester,
                    medium = medium,
                    pdfUrl = pdfUrl.trim(),
                    summary = cleanSummary,
                    topics = updatedTopics
                )
                persistDb(_uiState.value.db.copy(notes = notes), "Note updated & synced!")
                return
            }
        }
        val newNote = StudyNote(
            id = "note-${System.currentTimeMillis()}",
            title = cleanTitle,
            paperCode = paperCode.trim().ifBlank { "BA-NOTE" },
            subject = subject,
            semester = semester,
            medium = medium,
            level = level,
            rating = 5.0,
            pages = 28,
            date = todayDateLabel(),
            pdfUrl = pdfUrl.trim(),
            summary = cleanSummary,
            topics = listOf(StudyTopic(cleanUnit, cleanSummary, cleanBody))
        )
        persistDb(_uiState.value.db.copy(notes = listOf(newNote) + notes), "New Study Note published & synced!")
    }

    fun deleteNote(noteId: String) {
        persistDb(_uiState.value.db.copy(notes = _uiState.value.db.notes.filterNot { it.id == noteId }), "Note deleted & synced!")
    }

    fun addLevel(name: String) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        val current = _uiState.value.db.levels
        if (current.any { it.equals(clean, ignoreCase = true) }) {
            showToast("Level already exists")
            return
        }
        persistDb(_uiState.value.db.copy(levels = current + clean), "Added level: $clean")
    }

    fun removeLevel(index: Int) {
        val list = _uiState.value.db.levels.toMutableList()
        if (index !in list.indices) return
        val removed = list.removeAt(index)
        persistDb(_uiState.value.db.copy(levels = list), "Removed level: $removed")
    }

    fun addSubject(name: String) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        val current = _uiState.value.db.subjects
        if (current.any { it.equals(clean, ignoreCase = true) }) {
            showToast("Subject already exists")
            return
        }
        persistDb(_uiState.value.db.copy(subjects = current + clean), "Subject \"$clean\" added & synced!")
    }

    fun renameSubject(index: Int, newName: String) {
        val clean = newName.trim()
        val current = _uiState.value.db.subjects.toMutableList()
        if (clean.isEmpty() || index !in current.indices) return
        val oldName = current[index]
        if (oldName == clean) return
        current[index] = clean
        val db = _uiState.value.db
        val updatedDb = db.copy(
            subjects = current,
            notes = db.notes.map { if (it.subject == oldName) it.copy(subject = clean) else it },
            qna = db.qna.map { if (it.subject == oldName) it.copy(subject = clean) else it },
            syllabus = db.syllabus.map { if (it.subject == oldName) it.copy(subject = clean) else it },
            mcqs = db.mcqs.map { if (it.subject == oldName) it.copy(subject = clean) else it }
        )
        persistDb(updatedDb, "Subject renamed to \"$clean\" & synced!")
    }

    fun removeSubject(index: Int) {
        val list = _uiState.value.db.subjects.toMutableList()
        if (index !in list.indices) return
        val removed = list.removeAt(index)
        persistDb(_uiState.value.db.copy(subjects = list), "Subject \"$removed\" removed & synced!")
    }

    // ==================== 03. Q&A CRUD ====================

    fun upsertQna(
        editingId: String?,
        subject: String,
        semester: String,
        marks: String,
        level: String,
        medium: String,
        question: String,
        description: String,
        answer: String,
        imagePath: String
    ) {
        val cleanQ = question.trim()
        if (cleanQ.isEmpty()) return
        val list = _uiState.value.db.qna.toMutableList()
        if (editingId != null) {
            val idx = list.indexOfFirst { it.id == editingId }
            if (idx >= 0) {
                list[idx] = list[idx].copy(
                    subject = subject,
                    semester = semester,
                    marks = marks,
                    level = level,
                    medium = medium,
                    question = cleanQ,
                    description = description.trim(),
                    answer = answer.trim(),
                    imagePath = imagePath
                )
                persistDb(_uiState.value.db.copy(qna = list), "Q&A updated & synced!")
                return
            }
        }
        val item = QnaItem(
            id = "qna-${System.currentTimeMillis()}",
            subject = subject,
            semester = semester,
            marks = marks,
            medium = medium,
            level = level,
            description = description.trim(),
            imagePath = imagePath,
            rating = 5.0,
            date = todayDateLabel(),
            question = cleanQ,
            answer = answer.trim()
        )
        persistDb(_uiState.value.db.copy(qna = listOf(item) + list), "New Q&A published & synced!")
    }

    fun deleteQna(qnaId: String) {
        persistDb(_uiState.value.db.copy(qna = _uiState.value.db.qna.filterNot { it.id == qnaId }), "Q&A deleted & synced!")
    }

    // ==================== 04. SYLLABUS, SEMESTERS & NOTICES CRUD ====================

    fun addSemester(name: String, description: String) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        val sems = _uiState.value.db.syllabusSemesters
        val meta = SemesterMeta(
            id = "sem-${System.currentTimeMillis()}",
            name = clean,
            description = description.trim().ifBlank { "FYUGP BA Arts $clean Syllabus" }
        )
        persistDb(_uiState.value.db.copy(syllabusSemesters = sems + meta), "Semester \"$clean\" added & synced!")
    }

    fun removeSemester(index: Int) {
        val list = _uiState.value.db.syllabusSemesters.toMutableList()
        if (index !in list.indices) return
        val removed = list.removeAt(index)
        persistDb(_uiState.value.db.copy(syllabusSemesters = list), "Semester \"${removed.name}\" removed!")
    }

    fun upsertSyllabus(
        editingId: String?,
        title: String,
        paperCode: String,
        credits: String,
        subject: String,
        semester: String,
        pdfUrl: String,
        unitsRaw: String
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val parsedUnits = unitsRaw.lines().map { it.trim() }.filter { it.isNotEmpty() }
            .ifEmpty { listOf("Official Syllabus PDF attached") }
        val list = _uiState.value.db.syllabus.toMutableList()
        if (editingId != null) {
            val idx = list.indexOfFirst { it.id == editingId }
            if (idx >= 0) {
                list[idx] = list[idx].copy(
                    title = cleanTitle,
                    paperCode = paperCode.trim().ifBlank { "SYL-PAPER" },
                    credits = credits.trim().ifBlank { "4 Credits (100 Marks)" },
                    subject = subject,
                    semester = semester,
                    pdfUrl = pdfUrl.trim(),
                    units = parsedUnits
                )
                persistDb(_uiState.value.db.copy(syllabus = list), "Syllabus updated & synced!")
                return
            }
        }
        val item = SyllabusItem(
            id = "syl-${System.currentTimeMillis()}",
            paperCode = paperCode.trim().ifBlank { "SYL-PAPER" },
            title = cleanTitle,
            subject = subject,
            semester = semester,
            credits = credits.trim().ifBlank { "4 Credits (100 Marks)" },
            date = todayDateLabel(),
            pdfUrl = pdfUrl.trim(),
            units = parsedUnits
        )
        persistDb(_uiState.value.db.copy(syllabus = listOf(item) + list), "Syllabus published & synced!")
    }

    fun deleteSyllabus(syllabusId: String) {
        persistDb(_uiState.value.db.copy(syllabus = _uiState.value.db.syllabus.filterNot { it.id == syllabusId }), "Syllabus deleted!")
    }

    fun addNotice(title: String, category: String, date: String, authority: String, description: String, imagePath: String = "") {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val item = ExamNotice(
            id = "not-${System.currentTimeMillis()}",
            title = cleanTitle,
            category = category,
            date = date.trim().ifBlank { todayDateLabel() },
            authority = authority.trim().ifBlank { "Controller of Examinations, GU" },
            description = description.trim(),
            urgent = true,
            imagePath = imagePath
        )
        persistDb(_uiState.value.db.copy(notices = listOf(item) + _uiState.value.db.notices), "Exam Notice published & synced!")
    }

    fun deleteNotice(noticeId: String) {
        persistDb(_uiState.value.db.copy(notices = _uiState.value.db.notices.filterNot { it.id == noticeId }), "Notice deleted!")
    }

    // ==================== 05. FESTIVALS & HOLIDAYS CRUD ====================

    fun addCalendarEvent(
        isFestival: Boolean,
        title: String,
        date: String,
        description: String,
        imagePath: String,
        extraImages: List<String>
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val event = CalendarEvent(
            id = "${if (isFestival) "fest" else "hol"}-${System.currentTimeMillis()}",
            title = cleanTitle,
            date = date.trim().ifBlank { todayDateLabel() },
            description = description.trim(),
            imagePath = imagePath,
            extraImages = extraImages
        )
        val db = _uiState.value.db
        val updated = if (isFestival) {
            db.copy(festivals = listOf(event) + db.festivals)
        } else {
            db.copy(holidays = listOf(event) + db.holidays)
        }
        persistDb(updated, "${if (isFestival) "Festival" else "Holiday"} published & synced!")
    }

    fun deleteCalendarEvent(isFestival: Boolean, eventId: String) {
        val db = _uiState.value.db
        val updated = if (isFestival) {
            db.copy(festivals = db.festivals.filterNot { it.id == eventId })
        } else {
            db.copy(holidays = db.holidays.filterNot { it.id == eventId })
        }
        persistDb(updated, "${if (isFestival) "Festival" else "Holiday"} removed!")
    }

    // ==================== 06. SIDEBAR PAGES & URLS CRUD ====================

    fun upsertCustomPage(editingId: String?, title: String, slugInput: String, externalUrl: String, content: String) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val slug = slugInput.trim().ifBlank {
            cleanTitle.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
                .ifBlank { "page-${System.currentTimeMillis()}" }
        }
        val currentPages = _uiState.value.db.pages.toMutableList()
        val existingIdx = if (editingId != null) {
            currentPages.indexOfFirst { it.id == editingId }
        } else {
            currentPages.indexOfFirst { it.slug == slug }
        }
        if (existingIdx >= 0) {
            currentPages[existingIdx] = currentPages[existingIdx].copy(
                title = cleanTitle,
                slug = slug,
                externalUrl = externalUrl.trim(),
                content = content.trim()
            )
            persistDb(_uiState.value.db.copy(pages = currentPages), "Page \"$cleanTitle\" updated & synced!")
        } else {
            currentPages.add(
                CustomPage(
                    id = "page-${System.currentTimeMillis()}",
                    title = cleanTitle,
                    slug = slug,
                    icon = "file-text",
                    externalUrl = externalUrl.trim(),
                    content = content.trim()
                )
            )
            persistDb(_uiState.value.db.copy(pages = currentPages), "Page \"$cleanTitle\" added to Sidebar & synced!")
        }
    }

    fun deleteCustomPage(pageId: String) {
        persistDb(_uiState.value.db.copy(pages = _uiState.value.db.pages.filterNot { it.id == pageId }), "Page deleted!")
    }

    // ==================== 07. MCQ QUIZ BANK CRUD ====================

    fun upsertMcq(
        editingId: String?,
        subject: String,
        question: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String
    ) {
        val cleanQ = question.trim()
        if (cleanQ.isEmpty()) return
        val cleanOpts = options.map { it.trim() }
        val list = _uiState.value.db.mcqs.toMutableList()
        if (editingId != null) {
            val idx = list.indexOfFirst { it.id == editingId }
            if (idx >= 0) {
                list[idx] = list[idx].copy(
                    subject = subject,
                    question = cleanQ,
                    options = cleanOpts,
                    correctIndex = correctIndex.coerceIn(0, 3),
                    explanation = explanation.trim()
                )
                persistDb(_uiState.value.db.copy(mcqs = list), "MCQ updated & synced!")
                return
            }
        }
        val item = McqItem(
            id = "mcq-${System.currentTimeMillis()}",
            subject = subject,
            question = cleanQ,
            options = cleanOpts,
            correctIndex = correctIndex.coerceIn(0, 3),
            explanation = explanation.trim()
        )
        persistDb(_uiState.value.db.copy(mcqs = list + item), "MCQ added & synced!")
    }

    fun deleteMcq(mcqId: String) {
        persistDb(_uiState.value.db.copy(mcqs = _uiState.value.db.mcqs.filterNot { it.id == mcqId }), "MCQ deleted!")
    }

    // ==================== BACKUP EXPORT / IMPORT / RESET ====================

    fun exportDatabaseJson(): String = repository.exportDatabaseJson(_uiState.value.db)

    fun importDatabaseJson(json: String) {
        viewModelScope.launch {
            val imported = repository.importDatabaseJson(json)
            if (imported != null) {
                _uiState.update { it.copy(db = imported, bannerIndex = 0) }
                showToast("Backup imported & synced in real time!")
            } else {
                showToast("Invalid JSON backup file!")
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            val defaults = repository.resetDatabase()
            _uiState.update { it.copy(db = defaults, bannerIndex = 0) }
            showToast("Reset to default data!")
        }
    }

    // ==================== STUDENT PORTAL ACTIONS ====================

    fun switchTab(tab: AppTab, resetSubViews: Boolean = true) {
        _uiState.update { s ->
            s.copy(
                isAdminAppMode = false,
                activeTab = tab,
                activeReaderNoteId = if (tab != AppTab.NOTES || resetSubViews) null else s.activeReaderNoteId,
                selectedNotesSemester = if (tab == AppTab.NOTES && resetSubViews) null else s.selectedNotesSemester,
                selectedNotesSubject = if (tab == AppTab.NOTES && resetSubViews) null else s.selectedNotesSubject,
                selectedQnaSemester = if (tab == AppTab.QNA && resetSubViews) null else s.selectedQnaSemester,
                selectedQnaSubject = if (tab == AppTab.QNA && resetSubViews) null else s.selectedQnaSubject,
                activeQnaDetailId = if (tab != AppTab.QNA || resetSubViews) null else s.activeQnaDetailId,
                selectedSyllabusSemester = if (tab == AppTab.SYLLABUS && resetSubViews) null else s.selectedSyllabusSemester,
                selectedSyllabusSubject = if (tab == AppTab.SYLLABUS && resetSubViews) null else s.selectedSyllabusSubject
            )
        }
    }

    fun onBrandTitleTap() {
        val now = System.currentTimeMillis()
        if (now - lastLogoTapTime > 600L) {
            logoTapCount = 1
        } else {
            logoTapCount++
        }
        lastLogoTapTime = now
        if (logoTapCount >= 3) {
            logoTapCount = 0
            enterAdminApp()
        } else if (logoTapCount == 1 && _uiState.value.activeTab != AppTab.HOME) {
            switchTab(AppTab.HOME)
        }
    }

    fun updateSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.equals("#admin", ignoreCase = true) ||
            trimmed.equals("/admin", ignoreCase = true) ||
            trimmed == "9435149840"
        ) {
            _uiState.update { it.copy(searchQuery = "") }
            enterAdminApp()
            return
        }
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setBannerIndex(index: Int) {
        _uiState.update { it.copy(bannerIndex = index) }
    }

    fun handleBannerClick(banner: BannerSlide) {
        when (banner.targetType) {
            "note" -> openStudyReader(banner.targetId, 0)
            "page" -> openCustomPage(banner.targetId.removePrefix("#/page/"))
            else -> {
                val targetTab = when (banner.targetId) {
                    "notes" -> AppTab.NOTES
                    "qna" -> AppTab.QNA
                    "syllabus" -> AppTab.SYLLABUS
                    "notices" -> AppTab.NOTICES
                    "festivals" -> AppTab.FESTIVALS
                    "holidays" -> AppTab.HOLIDAYS
                    else -> AppTab.NOTES
                }
                switchTab(targetTab, true)
            }
        }
    }

    fun selectNotesSemester(semester: String?) {
        _uiState.update { it.copy(selectedNotesSemester = semester, selectedNotesSubject = null) }
    }

    fun selectNotesSubject(subject: String?) {
        _uiState.update { it.copy(selectedNotesSubject = subject, notesSearchQuery = "") }
    }

    fun updateNotesSearchQuery(query: String) {
        _uiState.update { it.copy(notesSearchQuery = query) }
    }

    fun openStudyReader(noteId: String, topicIdx: Int = 0) {
        val note = _uiState.value.db.notes.find { it.id == noteId } ?: _uiState.value.db.notes.firstOrNull() ?: return
        _uiState.update {
            it.copy(
                isAdminAppMode = false,
                activeTab = AppTab.NOTES,
                activeReaderNoteId = note.id,
                activeReaderTopicIdx = topicIdx,
                readerHighlightMode = HighlightMode.NONE,
                showReaderMarginPanel = false
            )
        }
    }

    fun closeStudyReader() {
        _uiState.update { it.copy(activeReaderNoteId = null, showReaderMarginPanel = false) }
    }

    fun selectReaderTopic(index: Int) {
        _uiState.update { it.copy(activeReaderTopicIdx = index) }
    }

    fun toggleHighlightMode(mode: HighlightMode) {
        _uiState.update {
            it.copy(readerHighlightMode = if (it.readerHighlightMode == mode) HighlightMode.NONE else mode)
        }
    }

    fun toggleReaderMarginPanel() {
        _uiState.update { it.copy(showReaderMarginPanel = !it.showReaderMarginPanel) }
    }

    fun addMarginNote(noteId: String, topicIdx: Int, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val key = "${noteId}_${topicIdx}"
        val currentMap = _uiState.value.userPrefs.marginNotes.toMutableMap()
        currentMap[key] = (currentMap[key] ?: emptyList()) + trimmed
        persistPrefs(_uiState.value.userPrefs.copy(marginNotes = currentMap))
        showToast("Margin note saved")
    }

    fun deleteMarginNote(noteId: String, topicIdx: Int, index: Int) {
        val key = "${noteId}_${topicIdx}"
        val currentMap = _uiState.value.userPrefs.marginNotes.toMutableMap()
        val list = (currentMap[key] ?: emptyList()).toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            currentMap[key] = list
            persistPrefs(_uiState.value.userPrefs.copy(marginNotes = currentMap))
        }
    }

    fun toggleBookmark(noteId: String) {
        val current = _uiState.value.userPrefs.bookmarks.toMutableList()
        val msg = if (current.contains(noteId)) {
            current.remove(noteId); "Removed from Saved"
        } else {
            current.add(noteId); "Saved to Bookmarks"
        }
        persistPrefs(_uiState.value.userPrefs.copy(bookmarks = current))
        showToast(msg)
    }

    fun rateItem(itemId: String, stars: Int) {
        val updated = _uiState.value.userPrefs.ratings.toMutableMap()
        updated[itemId] = stars
        persistPrefs(_uiState.value.userPrefs.copy(ratings = updated))
        showToast("You rated this $stars ★!")
    }

    fun setShowBookmarksModal(show: Boolean) {
        _uiState.update { it.copy(showBookmarksModal = show) }
    }

    fun setQnaSubMode(mode: QnaSubMode) {
        _uiState.update { it.copy(qnaSubMode = mode, activeQnaDetailId = null) }
    }

    fun selectQnaSemester(semester: String?) {
        _uiState.update { it.copy(selectedQnaSemester = semester, selectedQnaSubject = null, activeQnaDetailId = null) }
    }

    fun selectQnaSubject(subject: String?) {
        _uiState.update { it.copy(selectedQnaSubject = subject, activeQnaDetailId = null) }
    }

    fun selectMarksFilter(mark: String) {
        _uiState.update { it.copy(selectedMarksFilter = mark) }
    }

    fun openQnaDetail(qnaId: String) {
        _uiState.update {
            it.copy(
                isAdminAppMode = false,
                activeTab = AppTab.QNA,
                qnaSubMode = QnaSubMode.DESCRIPTIVE,
                activeQnaDetailId = qnaId
            )
        }
    }

    fun closeQnaDetail() {
        _uiState.update { it.copy(activeQnaDetailId = null) }
    }

    fun setMcqSubjectFilter(subject: String) {
        _uiState.update { it.copy(mcqSubjectFilter = subject, mcqCurrentIdx = 0) }
    }

    fun getFilteredMcqs(): List<McqItem> {
        val s = _uiState.value
        val filtered = s.db.mcqs.filter { s.mcqSubjectFilter == "ALL" || it.subject == s.mcqSubjectFilter }
        return if (filtered.isNotEmpty()) filtered else s.db.mcqs
    }

    fun selectMcqOption(mcqId: String, optionIdx: Int) {
        val updated = _uiState.value.mcqSelectedOptions.toMutableMap()
        updated[mcqId] = optionIdx
        _uiState.update { it.copy(mcqSelectedOptions = updated) }
    }

    fun toggleMcqFlag(mcqId: String) {
        val updated = _uiState.value.mcqFlaggedIds.toMutableSet()
        if (updated.contains(mcqId)) updated.remove(mcqId) else updated.add(mcqId)
        _uiState.update { it.copy(mcqFlaggedIds = updated) }
    }

    fun toggleMcqGridPalette() {
        _uiState.update { it.copy(mcqShowGridPalette = !it.mcqShowGridPalette) }
    }

    fun jumpToMcq(index: Int) {
        val list = getFilteredMcqs()
        if (list.isEmpty()) return
        val safeIdx = ((index % list.size) + list.size) % list.size
        _uiState.update { it.copy(mcqCurrentIdx = safeIdx) }
    }

    fun selectSyllabusSemester(semester: String?) {
        _uiState.update { it.copy(selectedSyllabusSemester = semester, selectedSyllabusSubject = null) }
    }

    fun selectSyllabusSubject(subject: String?) {
        _uiState.update { it.copy(selectedSyllabusSubject = subject) }
    }

    fun openSyllabusPaper(semester: String, subject: String) {
        _uiState.update {
            it.copy(
                isAdminAppMode = false,
                activeTab = AppTab.SYLLABUS,
                selectedSyllabusSemester = semester,
                selectedSyllabusSubject = subject
            )
        }
    }

    fun selectNoticeCategory(category: String) {
        _uiState.update { it.copy(selectedNoticeCategory = category) }
    }

    fun openEventDetail(
        title: String,
        dateLabel: String,
        description: String,
        imagePath: String = "",
        extraImages: List<String> = emptyList()
    ) {
        _uiState.update {
            it.copy(activeEventDetail = EventDetailData(title, dateLabel, description, imagePath, extraImages))
        }
    }

    fun closeEventDetail() {
        _uiState.update { it.copy(activeEventDetail = null) }
    }

    fun openPdfModal(title: String, url: String) {
        _uiState.update { it.copy(activePdfModal = PdfModalData(title, url)) }
    }

    fun closePdfModal() {
        _uiState.update { it.copy(activePdfModal = null) }
    }

    fun openCustomPage(slug: String) {
        val found = _uiState.value.db.pages.find { it.slug == slug } ?: _uiState.value.db.pages.firstOrNull() ?: return
        _uiState.update { it.copy(isAdminAppMode = false, activeTab = AppTab.PAGE, activePageSlug = found.slug) }
    }

    fun setShowAddSidebarPageModal(show: Boolean) {
        _uiState.update { it.copy(showAddSidebarPageModal = show) }
    }

    fun rememberCopiedLink(link: String) {
        _uiState.update { it.copy(lastCopiedLink = link) }
    }

    fun handleBackNavigation(): Boolean {
        val s = _uiState.value
        if (s.isAdminAppMode) {
            if (s.adminUnlocked && s.adminActiveSection != AdminSection.HOME) {
                selectAdminSection(AdminSection.HOME)
                return true
            }
            return false
        }

        if (s.activePdfModal != null) { closePdfModal(); return true }
        if (s.activeEventDetail != null) { closeEventDetail(); return true }
        if (s.showAddSidebarPageModal) { setShowAddSidebarPageModal(false); return true }
        if (s.showBookmarksModal) { setShowBookmarksModal(false); return true }

        return when (s.activeTab) {
            AppTab.NOTES -> when {
                s.activeReaderNoteId != null -> { closeStudyReader(); true }
                s.selectedNotesSubject != null -> { selectNotesSubject(null); true }
                s.selectedNotesSemester != null -> { selectNotesSemester(null); true }
                else -> { switchTab(AppTab.HOME); true }
            }
            AppTab.QNA -> when {
                s.activeQnaDetailId != null -> { closeQnaDetail(); true }
                s.qnaSubMode == QnaSubMode.MCQ -> { setQnaSubMode(QnaSubMode.DESCRIPTIVE); true }
                s.selectedQnaSubject != null -> { selectQnaSubject(null); true }
                s.selectedQnaSemester != null -> { selectQnaSemester(null); true }
                else -> { switchTab(AppTab.HOME); true }
            }
            AppTab.SYLLABUS -> when {
                s.selectedSyllabusSubject != null -> { selectSyllabusSubject(null); true }
                s.selectedSyllabusSemester != null -> { selectSyllabusSemester(null); true }
                else -> { switchTab(AppTab.HOME); true }
            }
            AppTab.NOTICES, AppTab.FESTIVALS, AppTab.HOLIDAYS, AppTab.PAGE -> {
                switchTab(AppTab.HOME); true
            }
            AppTab.HOME -> {
                // Return to Admin Panel from Student Home on Back
                enterAdminApp()
                true
            }
        }
    }
}
