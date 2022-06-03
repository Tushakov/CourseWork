import java.time.DayOfWeek
import kotlin.reflect.KProperty1

class PrintableLesson(
    val lesson: Lesson,
    val info: String,
    val fullName: String
) {
    companion object {
        fun empty(): PrintableLesson = PrintableLesson(
            Lesson(
                LessonType.NONE,
                LessonPosition(WeekType.EVEN, DayOfWeek.MONDAY, ""),
                "",
                "",
                "",
                listOf("")
            ), "", ""
        )

        fun <T> make(lesson: Lesson, excludingPath: KProperty1<Lesson, T>): PrintableLesson {
            return when (excludingPath) {
                Lesson::teacher -> {
                    val info: String
                    if (lesson.type == LessonType.NONE) {
                        info = ""
                    } else {
                        info = "${lesson.groups.joinToString("; ")} ауд.${lesson.classroom}"
                    }
                    PrintableLesson(lesson, info, lesson.fullName())
                }
                Lesson::name -> {
                    val meta: String
                    if (lesson.type == LessonType.NONE) {
                        meta = ""
                    } else {
                        meta =
                            "${lesson.type} ${lesson.groups.joinToString("; ")} ауд.${lesson.classroom} ${lesson.teacher}"
                    }
                    PrintableLesson(lesson, meta, "")
                }
                Lesson::groups -> {
                    val meta: String
                    if (lesson.type == LessonType.NONE) {
                        meta = ""
                    } else {
                        meta = "ауд.${lesson.classroom} ${lesson.teacher}"
                    }
                    PrintableLesson(lesson, meta, lesson.fullName())
                }
                Lesson::classroom -> {
                    val meta: String
                    if (lesson.type == LessonType.NONE) {
                        meta = ""
                    } else {
                        meta = "${lesson.groups.joinToString("; ")} ${lesson.teacher}"
                    }
                    PrintableLesson(lesson, meta, lesson.fullName())
                }
                else -> empty()
            }
        }
    }
}
