package Models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.*

data class Person(
    @BsonId
    val id: ObjectId,
    var name: String,
    val age: Int,
    val degree: String,
    val gender: String,
    val occupation: String,
    val stack: MutableList<String>,
    val city: String,
    var creationDate: String
) {
    init {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val currentTime = formatter.format(time)
        val timeBuilder = StringBuilder(currentTime)
        timeBuilder.insert(currentTime.indexOf(":") - 3, " --- ")
        creationDate = timeBuilder.toString()
        name = name.lowercase()
            .trim()
    }
}