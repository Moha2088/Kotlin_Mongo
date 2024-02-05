import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.Request.Builder as RequestBuilder


fun main(args: Array<String>) {

    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
    val database = getDatabase()
    val collection: MongoCollection<Person> = database.getCollection<Person>("Person")

    val running = true

    while (running) {
        try {
            runBlocking {

                println("Press 1 to add, 2 to read, 3 to update or 4 to delete a person and 5 to fetch data")
                val choice = readln().toInt()

                when (choice) {
                    1 -> addPerson(collection)
                    2 -> readPerson(collection)
                    3 -> updatePerson(collection)
                    4 -> deletePerson(collection)
                    5 -> fetch("https://jsonplaceholder.typicode.com/posts/1")

                    else -> {
                        throw IllegalArgumentException("Invalid integer. Enter a number from 1-4")
                    }
                }
            }
        } catch (ex: MongoException) {
            println(ex.message)
        } catch (exception: Exception) {
            print(exception.message)
        }
    }

    client.close()
}

fun getDatabase(): MongoDatabase {

    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
    return client.getDatabase(databaseName = "UCL")
}

data class Person(
    @BsonId
    val id: ObjectId, var name: String, val age: Int, val degree: String, val gender: String,
    val occupation: String, val stack: MutableList<String>, val city: String, var creationDate: String
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

suspend fun addPerson(
    collection: MongoCollection<Person>
) {
    println("Enter your name: ")
    val name = readln()
    println("Enter your age: ")
    val age = readln().toInt()
    println("Enter your degree: ")
    val degree = readln()
    println("Enter your gender")
    val gender = readln()
    println("Enter your occupation: ")
    val occupation = readln()
    println("Enter your first skill")
    val stack1 = readln()
    println("Enter your second skill")
    val stack2 = readln()
    println("Enter your third skill")
    val stack3 = readln()
    println("Enter your city of residence")
    val city = readln()

    val stackList: MutableList<String> = mutableListOf(stack1, stack2, stack3)

    val info =
        Person(
            id = ObjectId(),
            name = name,
            age = age,
            degree = degree,
            gender = gender,
            occupation = occupation,
            stack = stackList,
            city = city,
            creationDate = ""
        )

    collection.insertOne(info).also {
        println("Inserted id: ${it.insertedId} $name into the collection")
    }
}

suspend fun readPerson(collection: MongoCollection<Person>) {
    val database = getDatabase()

    val query = Filters.or(
        listOf(
            Filters.eq(Person::name.name, "Mohamed")
        )
    )

    collection.find(filter = query).collect { person ->
        print("\n\nFound person with name: ${person.name}, degree in: ${person.degree} \nand works as an ${person.occupation} \nThe second skill is: ${person.stack[1]}\n\n")
    }
}

suspend fun updatePerson(collection: MongoCollection<Person>) {

    val query = Filters.eq(Person::degree.name, "Masters In Aerospace Engineering")
    val update = Updates.combine(

        Updates.set(Person::degree.name, "Masters Degree In Aerospace Engineering"),
    )

    val updateOptions = UpdateOptions().upsert(true)


    collection.updateMany(filter = query, update, updateOptions).also {
        if (it.modifiedCount.toInt() == 0) {
            println("No document found with the given query")
        } else {
            println("Updated ${it.modifiedCount} documents in the collection")
        }
    }
}

suspend fun deletePerson(collection: MongoCollection<Person>) {

    var personToDelete: String
    do {
        println("Enter the name of the person you want to delete")
        personToDelete = readln()
    } while (personToDelete.isEmpty())

    val query = Filters.eq(Person::name.name, personToDelete)

    println("Enter 1 to delete one or 2 to delete many")
    val input = readln().toInt()

    try {
        if (input == 1) {
            collection.deleteOne(query).also {
                if (it.deletedCount.toInt() == 0) {
                    println("No document found with the given query")
                } else {
                    println("Deleted one document from the collection")
                }
            }
        } else if (input == 2) {
            collection.deleteMany(query).also {
                if (it.deletedCount.toInt() == 0) {
                    println("No document found with the given query")
                } else {
                    println("Deleted ${it.deletedCount} from the collection")
                }
            }
        }

    } catch (ex: IllegalArgumentException) {
        println("Invalid Argument! ${ex.message}")
    } catch (ex: Exception) {
        println(ex.message)
    }
}

data class Post(val userid: Int, val id: Int, val title: String, val body: String)

suspend fun fetch(uri: String) {
    val client = OkHttpClient
        .Builder()
        .build()

    val request = RequestBuilder()
        .url(uri)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                println(response.body?.string())
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            TODO("Not yet implemented")
        }
    })
}
