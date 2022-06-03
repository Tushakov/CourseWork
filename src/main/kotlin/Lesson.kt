import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable
class LessonPosition(
    val weekType: WeekType,
    val dayOfWeek: DayOfWeek,
    val lessonPeriod: String,
)

enum class LessonType(private val representation: String) {
    LECTURE("Лек."),
    PRACTICE("Пр."),
    LAB("Лаб."),
    KSR("КСР"),
    KRB("КРБ"),
    NONE("");
    override fun toString() = representation
}

@Serializable
class Lesson(
    val type: LessonType,
    val position: LessonPosition,
    val name: String,
    val teacher: String,
    val classroom: String,
    val groups: List<String>,
    val subgroup: String? = null
) {
    fun fullName(): String {
        return "$type $name"
    }
}

enum class WeekType(private val representation: String) {
    ODD("Нечетная неделя"), EVEN("Четная неделя");
    override fun toString() = representation
}
