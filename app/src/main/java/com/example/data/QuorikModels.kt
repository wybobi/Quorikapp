package com.example.data

data class BannerSlide(
    val id: String,
    val badge: String,
    val title: String,
    val subtitle: String,
    val gradientKey: String = "blue",
    val icon: String = "book-open-check",
    val targetType: String = "note", // "note", "tab", "page"
    val targetId: String = "note-his-301",
    val imagePath: String = ""
)

data class StaticBanner(
    val id: String,
    val badge: String,
    val title: String,
    val subtitle: String,
    val buttonText: String = "Open →",
    val targetTab: String = "notes",
    val gradientKey: String = "blue",
    val imagePath: String = ""
)

data class PageBanner(
    val badge: String,
    val title: String,
    val subtitle: String,
    val gradientKey: String = "blue",
    val icon: String = "book-open",
    val imagePath: String = ""
)

data class CustomPage(
    val id: String,
    val title: String,
    val slug: String,
    val icon: String = "file-text",
    val externalUrl: String = "",
    val content: String
)

data class StudyTopic(
    val title: String,
    val aiTakeaway: String,
    val content: String
)

data class StudyNote(
    val id: String,
    val title: String,
    val paperCode: String,
    val subject: String,
    val semester: String,
    val medium: String = "Assamese",
    val level: String = "Honours / Major",
    val rating: Double = 4.9,
    val pages: Int = 32,
    val date: String = "20 Nov 2026",
    val pdfUrl: String = "",
    val summary: String,
    val topics: List<StudyTopic> = emptyList()
)

data class QnaItem(
    val id: String,
    val subject: String,
    val semester: String,
    val marks: String,
    val medium: String = "Assamese",
    val level: String = "",
    val description: String = "",
    val imagePath: String = "",
    val rating: Double = 4.9,
    val date: String = "20 Nov 2026",
    val question: String,
    val answer: String
)

data class McqItem(
    val id: String,
    val subject: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class SyllabusItem(
    val id: String,
    val paperCode: String,
    val title: String,
    val subject: String,
    val semester: String,
    val credits: String = "4 Credits (100 Marks)",
    val date: String = "20 Nov 2026",
    val pdfUrl: String = "",
    val units: List<String> = emptyList()
)

data class SemesterMeta(
    val id: String,
    val name: String,
    val description: String
)

data class ExamNotice(
    val id: String,
    val title: String,
    val date: String,
    val category: String,
    val urgent: Boolean = false,
    val authority: String = "Controller of Examinations, GU",
    val description: String,
    val imagePath: String = ""
)

data class CalendarEvent(
    val id: String,
    val title: String,
    val date: String,
    val description: String,
    val imagePath: String = "",
    val extraImages: List<String> = emptyList()
)

data class QuorikDatabase(
    val banners: List<BannerSlide>,
    val staticBanners: List<StaticBanner> = DefaultQuorikData.defaultStaticBanners,
    val pageBanners: Map<String, PageBanner>,
    val pages: List<CustomPage>,
    val notes: List<StudyNote>,
    val qna: List<QnaItem>,
    val mcqs: List<McqItem>,
    val syllabus: List<SyllabusItem>,
    val notices: List<ExamNotice>,
    val syllabusSemesters: List<SemesterMeta>,
    val subjects: List<String>,
    val levels: List<String> = DefaultQuorikData.defaultLevels,
    val festivals: List<CalendarEvent>,
    val holidays: List<CalendarEvent>
)

data class UserPrefs(
    val bookmarks: List<String> = listOf("note-his-301"),
    val ratings: Map<String, Int> = emptyMap(),
    val marginNotes: Map<String, List<String>> = emptyMap()
)
