import kotlinx.html.*
import kotlinx.html.table
import kotlinx.html.stream.*
import org.litote.kmongo.*
import java.io.File
import java.io.FileWriter
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

val database = mongoDatabase.getCollection<Lesson>().apply { drop() }

class Controller {
    private val lessonPeriod = listOf(
        "08:00 - 09:30",
        "09:45 - 11:15",
        "11:30 - 13:00",
        "13:55 - 15:25",
        "15:40 - 17:10"
    )

    fun fillDatabase() {
        database.insertMany(
            listOf(
                Lesson(
                    LessonType.LECTURE,
                    LessonPosition(WeekType.EVEN, DayOfWeek.MONDAY, lessonPeriod[0]),
                    "ООП",
                    "доц. Альтман Е.А.",
                    "1-112",
                    listOf("20з", "20м")
                ),
                Lesson(
                    LessonType.LAB,
                    LessonPosition(WeekType.EVEN, DayOfWeek.TUESDAY, lessonPeriod[4]),
                    "ТОАПС",
                    "доц. Окишев А.С.",
                    "1-329",
                    listOf("20м")
                ),
                Lesson(
                    LessonType.LAB,
                    LessonPosition(WeekType.EVEN, DayOfWeek.WEDNESDAY, lessonPeriod[1]),
                    "ООП",
                    "доц. Альтман Е.А.",
                    "1-325",
                    listOf("20з")
                ),
                Lesson(
                    LessonType.LAB,
                    LessonPosition(WeekType.ODD, DayOfWeek.MONDAY, lessonPeriod[1]),
                    "Базы данных",
                    "доц. Тихонова Н.А.",
                    "1-330",
                    listOf("20з")
                ),
                Lesson(
                    LessonType.PRACTICE,
                    LessonPosition(
                        WeekType.ODD, DayOfWeek.WEDNESDAY,
                        lessonPeriod[1]
                    ),
                    "ООП",
                    "доц. Альтман Е.А.",
                    "1-112",
                    listOf("20м")
                ),
                Lesson(
                    LessonType.KSR,
                    LessonPosition(WeekType.ODD, DayOfWeek.TUESDAY, lessonPeriod[1]),
                    "Экономика",
                    "доц. Севостьянова Е.В.",
                    "1-421",
                    listOf("20м")
                ),
                Lesson(
                    LessonType.KRB,
                    LessonPosition(WeekType.ODD, DayOfWeek.THURSDAY, lessonPeriod[1]),
                    "Схемотехника",
                    "ст.пр. Циркин В.С.",
                    "1-210",
                    listOf("20з", "20м")
                )
            )
        )
    }

    fun printTable() {
        printTable(getData())
    }

    private fun getData(): Pair<String, List<PrintableLesson>?> {
        println("Выберите список критериев")
        println("1. Преподаватели\n2. Предметы\n3. Группы\n4. Аудитории\n")
        val lessons = database.find()
        val classrooms = lessons.map { it.classroom }.distinct()
        val teachers = lessons.map { it.teacher }.distinct()
        val lessonNames = lessons.map { it.name }.distinct()
        val groups = lessons.map { it.groups }.flatten().distinct()
        val readLine = readLine()?.toIntOrNull()
        var count = 1

        when (readLine) {
            1 -> for (i in teachers.indices) {
                (println(" ${count++}. ${teachers[i]}"))
            }
            2 -> for (i in lessonNames.indices) {
                (println(" ${count++}. ${lessonNames[i]}"))
            }
            3 -> for (i in groups.indices) {
                (println(" ${count++}. ${groups[i]}"))
            }
            4 -> for (i in classrooms.indices) {
                (println(" ${count++}. ${classrooms[i]}"))
            }
            else -> println("Введено неверное значение")
        }

        var title = ""
        var printableLessons: List<PrintableLesson>? = null
        readLine()?.toIntOrNull()?.let {
            when (readLine) {
                1 -> {
                    val teacher = teachers[it - 1]
                    title = teacher
                    printableLessons = database.find(Lesson::teacher eq teacher).toList().map { lesson ->
                        PrintableLesson.make(lesson, Lesson::teacher)
                    }
                }
                2 -> {
                    val lessonName = lessonNames[it - 1]
                    title = lessonName
                    printableLessons = database.find(Lesson::name eq lessonName).toList().map { lesson ->
                        PrintableLesson.make(lesson, Lesson::name)
                    }
                }
                3 -> {
                    val group = groups[it - 1]
                    title = group
                    printableLessons = database.find(Lesson::groups contains group).toList().map { lesson ->
                        PrintableLesson.make(lesson, Lesson::groups)
                    }
                }
                4 -> {
                    val classroom = classrooms[it - 1]
                    title = classroom
                    printableLessons =
                        database.find(Lesson::classroom eq classroom).toList().map { lesson ->
                            PrintableLesson.make(lesson, Lesson::classroom)
                        }
                }
                else -> println("Введено неверное значение")
            }
        }
        return Pair(title, printableLessons)
    }

    private fun printTable(printableData: Pair<String, List<PrintableLesson>?>) {
        val weekDays = DayOfWeek.values().dropLast(1)
        val file = File("./src/main/resources/qwe.html")
        val writer = FileWriter(file)
        file.createNewFile()
        writer.appendHTML().html {
            body {
                WeekType.values().forEach { weekType ->
                    header {
                        +"$weekType. Критерий: ${printableData.first}"
                        table {
                            style = "border: 10px groove;"
                            tr {
                                th { +"" }
                                weekDays.forEach { weekDay ->
                                    th { +weekDay.getDisplayName(TextStyle.FULL, Locale("ru", "RU")).capitalize() }
                                }
                            }
                            lessonPeriod.forEach { lessonPeriod ->
                                tr {
                                    td {
                                        +lessonPeriod
                                    }
                                    weekDays.forEach { weekDay ->
                                        val lesson = printableData.second?.firstOrNull { printable ->
                                            printable.lesson.position.weekType == weekType &&
                                                    printable.lesson.position.dayOfWeek == weekDay &&
                                                    printable.lesson.position.lessonPeriod == lessonPeriod
                                        }
                                            ?: PrintableLesson.empty()
                                        td {
                                            style = "border: 10px groove;"
                                            +lesson.info
                                            br {
                                                +lesson.fullName
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        writer.close()
    }
}