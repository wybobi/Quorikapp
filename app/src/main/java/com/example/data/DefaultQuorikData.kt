package com.example.data

object DefaultQuorikData {

    val allSubjects = listOf(
        "History",
        "Political Science",
        "Assamese",
        "Education",
        "Sociology",
        "Philosophy",
        "Economics",
        "English"
    )

    val defaultLevels = listOf(
        "Beginner",
        "Intermediate",
        "Honours / Major",
        "Exam Special",
        "Important",
        "PYQ Solved"
    )

    val defaultStaticBanners = listOf(
        StaticBanner(
            id = "sban-1",
            badge = "New Content",
            title = "BA Arts Free Notes & Study PDFs",
            subtitle = "All unit-wise guides & exam summaries ready to read",
            buttonText = "Open Notes →",
            targetTab = "notes",
            gradientKey = "teal"
        ),
        StaticBanner(
            id = "sban-2",
            badge = "Fresh Program",
            title = "FYUGP New Syllabus & Exam Routine",
            subtitle = "Updated Major, Minor, MDC papers & official schedules",
            buttonText = "Explore",
            targetTab = "syllabus",
            gradientKey = "blue"
        ),
        StaticBanner(
            id = "sban-3",
            badge = "Exam Booster",
            title = "Practice 5 Marks & Live MCQ Test",
            subtitle = "Solved exam-ready answers & instant quiz practice",
            buttonText = "Practice →",
            targetTab = "qna",
            gradientKey = "indigo"
        )
    )

    val allSemesters = listOf(
        SemesterMeta("sem-1", "1st Semester", "FYUGP BA Arts Semester 1 Major, Minor & MDC Syllabus"),
        SemesterMeta("sem-2", "2nd Semester", "FYUGP BA Arts Semester 2 Major, Minor & MDC Syllabus"),
        SemesterMeta("sem-3", "3rd Semester", "FYUGP BA Arts Semester 3 Major, Minor & MDC Syllabus"),
        SemesterMeta("sem-4", "4th Semester", "FYUGP BA Arts Semester 4 Major, Minor & MDC Syllabus"),
        SemesterMeta("sem-5", "5th Semester", "FYUGP BA Arts Semester 5 Major, Minor & Honours Syllabus"),
        SemesterMeta("sem-6", "6th Semester", "FYUGP BA Arts Semester 6 Final Honours & Core Syllabus")
    )

    fun createDefaultDatabase(): QuorikDatabase = QuorikDatabase(
        banners = listOf(
            BannerSlide(
                id = "ban-1",
                badge = "3rd Semester • History",
                title = "History of East Asia: China & Japan (1839–1949)",
                subtitle = "HIS4300204MJ • Unit 1–4 Free Notes & PDF",
                gradientKey = "blue",
                icon = "book-open-check",
                targetType = "note",
                targetId = "note-his-301"
            ),
            BannerSlide(
                id = "ban-2",
                badge = "3rd Semester • MDC",
                title = "Land and People of Assam (অসমৰ ভূমি আৰু জনগণ)",
                subtitle = "MDC0302603N • Complete Assamese & English Notes",
                gradientKey = "teal",
                icon = "compass",
                targetType = "note",
                targetId = "note-mdc-302"
            ),
            BannerSlide(
                id = "ban-3",
                badge = "Exam Question & Bank",
                title = "1, 2, 5 & 10 Marks Solved Questions + MCQ Test",
                subtitle = "Practice high-yield BA Arts exam answers free",
                gradientKey = "purple",
                icon = "award",
                targetType = "tab",
                targetId = "qna"
            ),
            BannerSlide(
                id = "ban-4",
                badge = "Official Exam Routine",
                title = "BA 1st, 3rd & 5th Semester Exam Schedule PDF",
                subtitle = "Download official University timetable & circulars",
                gradientKey = "amber",
                icon = "bell-ring",
                targetType = "tab",
                targetId = "notices"
            )
        ),
        pageBanners = mapOf(
            "notes" to PageBanner(
                badge = "Study Library",
                title = "BA Arts Notes & Free PDFs",
                subtitle = "Unit-wise guides for Semesters 1 to 6",
                gradientKey = "blue",
                icon = "book-open"
            ),
            "notices" to PageBanner(
                badge = "Official Updates",
                title = "Exam Notices & Routines",
                subtitle = "University circulars, form fill-up & schedules",
                gradientKey = "amber",
                icon = "bell-ring"
            ),
            "syllabus" to PageBanner(
                badge = "FYUGP Official",
                title = "BA Arts Syllabus & Papers",
                subtitle = "Major, Minor & MDC course structures",
                gradientKey = "indigo",
                icon = "graduation-cap"
            ),
            "festivals" to PageBanner(
                badge = "Festivals Calendar",
                title = "Assam & Cultural Festivals",
                subtitle = "Tap any festival below to view full details & dates",
                gradientKey = "blue",
                icon = "sparkles"
            ),
            "holidays" to PageBanner(
                badge = "Holiday List",
                title = "Academic & University Holidays",
                subtitle = "Tap any holiday card below for full details",
                gradientKey = "teal",
                icon = "calendar-days"
            )
        ),
        pages = listOf(
            CustomPage(
                id = "page-about",
                title = "About Us",
                slug = "about-us",
                icon = "info",
                externalUrl = "",
                content = "Welcome to Quorik — an open-access academic portal created for Bachelor of Arts (BA Honours & FYUGP) college students.\n\nAll unit-wise study notes, downloadable PDFs, 1/2/5/10-mark solved Question & Bank sets, official university syllabi, and exam notices are 100% free with zero login or registration required."
            ),
            CustomPage(
                id = "page-privacy",
                title = "Privacy Policy",
                slug = "privacy-policy",
                icon = "shield",
                externalUrl = "",
                content = "Zero Personal Data Collection: Quorik operates in 100% Free Mode without user registration. You never have to enter your phone number, email address, or personal credentials.\n\nLocal Device Storage: Bookmarks, star ratings, and study margin notes are saved locally on your device."
            ),
            CustomPage(
                id = "page-terms",
                title = "Terms & Conditions",
                slug = "terms-conditions",
                icon = "file-check",
                externalUrl = "",
                content = "All study materials, solved questions, and PDF guides on Quorik are provided free for student exam preparation and academic reference."
            ),
            CustomPage(
                id = "page-contact",
                title = "Contact & Support",
                slug = "contact-us",
                icon = "mail",
                externalUrl = "",
                content = "For study material requests or syllabus updates, contact the Quorik academic desk at Quorik.co@gmail.com."
            )
        ),
        notes = listOf(
            StudyNote(
                id = "note-his-301",
                title = "History of East Asia: China & Japan (1839–1949)",
                paperCode = "HIS4300204MJ",
                subject = "History",
                semester = "3rd Semester",
                medium = "Assamese",
                rating = 4.9,
                pages = 48,
                date = "20 Nov 2026",
                summary = "Opium Wars, Treaty of Nanking, Taiping Rebellion & Meiji Restoration in Japan.",
                topics = listOf(
                    StudyTopic(
                        title = "Unit 1: Imperial China & The First Opium War (1839–1842)",
                        aiTakeaway = "Focus on the Canton Trade system, Lin Zexu’s confiscation of opium, and the 5 Treaty Ports opened under the Treaty of Nanking (1842).",
                        content = "১. পটভূমি আৰু কেণ্টন বাণিজ্য ব্যৱস্থা (Canton System): উনবিংশ শতিকাৰ আৰম্ভণিতে চীনৰ মাঞ্চু (Qing) শাসকসকলে বিদেশী বণিকসকলক কেৱল কেণ্টন বন্দৰতহে বাণিজ্য কৰিবলৈ অনুমতি দিছিল। বৃটিছ ইষ্ট ইণ্ডিয়া কোম্পানীয়ে ভাৰতত উৎপাদিত আফিং (Opium) চীনলৈ ৰপ্তানি কৰিবলৈ ধৰে।\n\n২. প্ৰথম আফিং যুদ্ধৰ মূল কাৰণসমূহ:\n• অবৈধ আফিং ব্যৱসায়: আফিঙৰ বাবে চীনৰ ৰূপৰ মুদ্ৰা বাহিৰলৈ ওলাই গৈছিল।\n• লিন জে-চু (Lin Zexu)ৰ কঠোৰ পদক্ষেপ (১৮৩৯): আয়ুক্ত লিন জে-চুৱে কেণ্টনত বৃটিছ বণিকৰ ২০,০০০ পেটি আফিং জব্দ কৰি ধ্বংস কৰে।\n\n৩. নানকিঙৰ সন্ধি (Treaty of Nanking, 29 August 1842): হংকং দ্বীপ বৃটিছক হস্তান্তৰ আৰু কেণ্টন, আময়, ফুচৌ, নিংপো, চাংহাই—এই ৫টা বন্দৰ বিদেশী বাণিজ্যৰ বাবে মুকলি।"
                    ),
                    StudyTopic(
                        title = "Unit 2: Taiping Rebellion (1850–1864)",
                        aiTakeaway = "Hung Hsiu-chuan led the Taiping Tianguo (Heavenly Kingdom of Great Peace) with capital at Nanking.",
                        content = "টাইপিং বিদ্ৰোহ (১৮৫০–১৮৬৪): নেতা হাং চিউ-চুয়ান (Hung Hsiu-chuan)ৰ নেতৃত্বত দক্ষিণ চীনত মাঞ্চু ৰাজবংশৰ বিৰুদ্ধে এক বিশাল কৃষক বিদ্ৰোহ গঢ় লৈ উঠিছিল।"
                    ),
                    StudyTopic(
                        title = "Unit 3: Meiji Restoration in Japan (1868)",
                        aiTakeaway = "Fall of Tokugawa Shogunate in 1867–68, Charter Oath of 1868, Abolition of Feudalism (1871), and Meiji Constitution of 1889.",
                        content = "মেইজি পুনৰুদ্ধাৰ (১৮৬৮): টকুগাৱা শ্বোগুন শাসনৰ পতন ঘটে আৰু সম্ৰাট মুৎসুহিতো (মেইজি)ৰ হাতলৈ কেন্দ্ৰীয় ক্ষমতা ঘূৰি আহে।"
                    )
                )
            ),
            StudyNote(
                id = "note-mdc-302",
                title = "Land and People of Assam (অসমৰ ভূমি আৰু জনগণ)",
                paperCode = "MDC0302603N",
                subject = "History",
                semester = "3rd Semester",
                medium = "Assamese",
                rating = 4.9,
                pages = 36,
                date = "19 Nov 2026",
                summary = "Physiography of Brahmaputra & Barak Valleys, ethnic groups & Satra culture.",
                topics = listOf(
                    StudyTopic(
                        title = "Unit 1: Physiographic Divisions of Assam",
                        aiTakeaway = "Three divisions: Brahmaputra Valley, Barak Valley, and Karbi Anglong & North Cachar Hills.",
                        content = "অসমৰ ভূ-প্ৰাকৃতিক বিভাগ: (১) ব্ৰহ্মপুত্ৰ উপত্যকা, (২) বৰাক উপত্যকা, আৰু (৩) কাৰ্বি মালভূমি আৰু ডিমা হাছাও পার্বত্য অঞ্চল।"
                    )
                )
            ),
            StudyNote(
                id = "note-pol-101",
                title = "Constitutional Government & Democracy in India",
                paperCode = "POL-MJ-101",
                subject = "Political Science",
                semester = "1st Semester",
                medium = "English",
                rating = 4.8,
                pages = 42,
                date = "18 Nov 2026",
                summary = "Constituent Assembly, Preamble, Fundamental Rights (Art. 12–35) & DPSP.",
                topics = listOf(
                    StudyTopic(
                        title = "Unit 1: Philosophy of the Preamble & Fundamental Rights",
                        aiTakeaway = "42nd Amendment (1976) added Socialist, Secular, Integrity. Art 32 is the Heart and Soul of the Constitution.",
                        content = "Preamble & Fundamental Rights: Part III (Articles 12–35) guarantees six Fundamental Rights enforceable by the Supreme Court under Article 32."
                    )
                )
            ),
            StudyNote(
                id = "note-edu-201",
                title = "Foundations of Educational Psychology (শিক্ষা মনোবিজ্ঞান)",
                paperCode = "EDU-MJ-201",
                subject = "Education",
                semester = "2nd Semester",
                medium = "Assamese",
                rating = 4.8,
                pages = 34,
                date = "17 Nov 2026",
                summary = "Scope of Educational Psychology, Theories of Learning (Thorndike, Pavlov, Skinner).",
                topics = listOf(
                    StudyTopic(
                        title = "Unit 1: Thorndike’s Laws of Learning",
                        aiTakeaway = "Law of Readiness, Law of Exercise, and Law of Effect.",
                        content = "থৰ্নডাইকৰ শিক্ষণৰ মূলসূত্ৰ: (ক) প্ৰস্তুতিৰ সূত্ৰ, (খ) অনুশীলনৰ সূত্ৰ, আৰু (গ) ফলাফলৰ সূত্ৰ।"
                    )
                )
            )
        ),
        qna = listOf(
            QnaItem(
                id = "qna-1",
                subject = "History",
                semester = "3rd Semester",
                marks = "1 Mark",
                medium = "Assamese",
                rating = 4.9,
                date = "20 Nov 2026",
                question = "নানকিঙৰ সন্ধি কোন চনত স্বাক্ষৰিত হৈছিল?",
                answer = "১৮৪২ চনৰ ২৯ আগষ্টত চীন আৰু গ্ৰেট বৃটেইনৰ মাজত নানকিঙৰ সন্ধি স্বাক্ষৰিত হৈছিল।"
            ),
            QnaItem(
                id = "qna-2",
                subject = "History",
                semester = "3rd Semester",
                marks = "2 Marks",
                medium = "Assamese",
                rating = 4.9,
                date = "19 Nov 2026",
                question = "নানকিঙৰ সন্ধিৰ জৰিয়তে মুকলি কৰা চীনৰ ৫টা চুক্তিবদ্ধ বন্দৰৰ নাম লিখা।",
                answer = "(১) কেণ্টন, (২) আময়, (৩) ফুচৌ, (৪) নিংপো আৰু (৫) চাংহাই।"
            ),
            QnaItem(
                id = "qna-3",
                subject = "History",
                semester = "3rd Semester",
                marks = "5 Marks",
                medium = "Assamese",
                rating = 4.8,
                date = "18 Nov 2026",
                question = "টাইপিং বিদ্ৰোহৰ ব্যৰ্থতাৰ পাঁচটা মূল কাৰণ উল্লেখ কৰা।",
                answer = "(১) নেতৃত্বৰ আভ্যন্তৰীণ কন্দল, (২) শিক্ষিত জমিদাৰ শ্ৰেণীৰ বিৰোধিতা, (৩) পাশ্চাত্য শক্তিসমূহৰ মাঞ্চু চৰকাৰক সামৰিক সহায়, (৪) কৌশলগত ভুল, আৰু (৫) বিদ্ৰোহী নেতাসকলৰ বিলাসী জীৱন।"
            ),
            QnaItem(
                id = "qna-4",
                subject = "History",
                semester = "3rd Semester",
                marks = "10 Marks",
                medium = "Assamese",
                rating = 5.0,
                date = "17 Nov 2026",
                question = "প্ৰথম আফিং যুদ্ধৰ (১৮৩৯–১৮৪২) কাৰণ আৰু ফলাফলসমূহ আলোচনা কৰা।",
                answer = "কেণ্টন বাণিজ্য ব্যৱস্থা, বৃটিছ বণিকৰ অবৈধ আফিং আমদানি, লিন জে-চুৰ দ্বাৰা আফিং ধ্বংস, আৰু ১৮৪২ চনৰ নানকিঙৰ সন্ধিৰ জৰিয়তে হংকং হস্তান্তৰ আৰু ৫টা বন্দৰ মুকলি।"
            )
        ),
        mcqs = listOf(
            McqItem(
                id = "mcq-1",
                subject = "History",
                question = "In which year was the Treaty of Nanking signed after the First Opium War?",
                options = listOf("1839 AD", "1840 AD", "1842 AD (29 August)", "1858 AD"),
                correctIndex = 2,
                explanation = "Signed on 29 August 1842 ending the First Opium War."
            ),
            McqItem(
                id = "mcq-2",
                subject = "History",
                question = "Who was appointed in 1839 to suppress the opium trade at Canton?",
                options = listOf("Hung Hsiu-chuan", "Lin Zexu", "Li Hung-chang", "Sun Yat-sen"),
                correctIndex = 1,
                explanation = "Imperial Commissioner Lin Zexu destroyed over 20,000 chests of opium at Canton in June 1839."
            ),
            McqItem(
                id = "mcq-3",
                subject = "History",
                question = "The Meiji Restoration in Japan took place in which year?",
                options = listOf("1853 AD", "1868 AD", "1889 AD", "1894 AD"),
                correctIndex = 1,
                explanation = "In 1868, practical imperial rule was restored under Emperor Meiji."
            ),
            McqItem(
                id = "mcq-4",
                subject = "Political Science",
                question = "Which amendment added \"Socialist\" and \"Secular\" to the Preamble?",
                options = listOf("24th Amendment", "42nd Amendment, 1976", "44th Amendment", "86th Amendment"),
                correctIndex = 1,
                explanation = "The 42nd Constitutional Amendment Act (1976) inserted Socialist, Secular, and Integrity."
            )
        ),
        syllabus = listOf(
            SyllabusItem(
                id = "syl-1",
                paperCode = "HIS4300204MJ",
                title = "History of East Asia (China & Japan, 1839–1949)",
                subject = "History",
                semester = "3rd Semester",
                credits = "4 Credits (100 Marks)",
                date = "20 Nov 2026",
                units = listOf(
                    "Unit I: Canton System, First & Second Opium Wars, Taiping Rebellion.",
                    "Unit II: Boxer Uprising, 1911 Revolution, May Fourth Movement.",
                    "Unit III: Meiji Restoration (1868) & 1889 Constitution.",
                    "Unit IV: Rise of Imperial Japan & WWII."
                )
            ),
            SyllabusItem(
                id = "syl-2",
                paperCode = "MDC0302603N",
                title = "Land and People of Assam",
                subject = "History",
                semester = "3rd Semester",
                credits = "3 Credits (75 Marks)",
                date = "19 Nov 2026",
                units = listOf(
                    "Unit I: Physiography, River System, Climate of Assam.",
                    "Unit II: Major Ethnic & Linguistic Groups.",
                    "Unit III: Neo-Vaishnavism, Satra & Namghar, Folk Arts."
                )
            ),
            SyllabusItem(
                id = "syl-3",
                paperCode = "POL-MJ-101",
                title = "Constitutional Government & Democracy in India",
                subject = "Political Science",
                semester = "1st Semester",
                credits = "4 Credits (100 Marks)",
                date = "18 Nov 2026",
                units = listOf(
                    "Unit I: Constituent Assembly & Preamble.",
                    "Unit II: Fundamental Rights & Directive Principles.",
                    "Unit III: Parliament, President, PM & Supreme Court.",
                    "Unit IV: Federalism & Panchayati Raj."
                )
            )
        ),
        notices = listOf(
            ExamNotice(
                id = "not-1",
                title = "GU FYUGP BA 1st, 3rd & 5th Semester Final Exam Routine Released",
                date = "18 Nov 2026",
                category = "Exam Routine",
                urgent = true,
                authority = "Controller of Examinations, GU",
                description = "Morning (9 AM – 12 PM) and Afternoon (1:30 PM – 4:30 PM) subject-wise date sheet for BA Honours/Major, Minor, MDC & VAC papers."
            ),
            ExamNotice(
                id = "not-2",
                title = "Online Exam Form Fill-up Notice for Odd Semester BA Arts",
                date = "15 Nov 2026",
                category = "Form Fill-up",
                urgent = true,
                authority = "GU Portal",
                description = "Complete subject verification and admit card form submission before the deadline."
            )
        ),
        syllabusSemesters = allSemesters,
        subjects = allSubjects,
        festivals = listOf(
            CalendarEvent(
                id = "fest-1",
                title = "Magh Bihu / Bhogali Bihu",
                date = "14–15 January",
                description = "Harvest festival of Assam celebrated with Uruka feasts, community bonfires, and traditional Meji."
            ),
            CalendarEvent(
                id = "fest-2",
                title = "Bohag Bihu / Rongali Bihu",
                date = "14–16 April",
                description = "Assamese New Year and spring festival of joy, Husori performances, and traditional delicacies."
            ),
            CalendarEvent(
                id = "fest-3",
                title = "Saraswati Puja",
                date = "23 January",
                description = "Celebrated across colleges and universities in honour of Goddess Saraswati."
            )
        ),
        holidays = listOf(
            CalendarEvent(
                id = "hol-1",
                title = "Republic Day",
                date = "26 January",
                description = "National Holiday — All colleges and university administrative offices remain closed."
            ),
            CalendarEvent(
                id = "hol-2",
                title = "Bohag Bihu Vacation",
                date = "14–16 April",
                description = "Official University & College Bihu Holidays across Assam."
            ),
            CalendarEvent(
                id = "hol-3",
                title = "Independence Day",
                date = "15 August",
                description = "National Holiday — Flag hoisting and cultural programs."
            )
        )
    )
}
