
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

fun main(args: Array<String>) {

    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
    val database = getDatabase()
    val collection: MongoCollection<Person> = database.getCollection<Person>("Person")

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

    try {
        runBlocking {

            addPerson(database, name, age, degree, gender, occupation)

            //val query = Filters.eq("name", "Mohamed")
            //collection.deleteOne(query)
                //.also { println("Deleted ${it.deletedCount} document from the collection")}
        }
    } catch (exception: Exception) {
        print(exception.message)
    }

    client.close()
}

fun getDatabase(): MongoDatabase {

    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
    return client.getDatabase(databaseName = "UCL")
}

data class Person(
    @BsonId
    val id: ObjectId,
    val name: String,
    val age: Int,
    val degree: String,
    val gender: String,
    val occupation: String
)

suspend fun addPerson(
    database: MongoDatabase,
    name: String,
    age: Int,
    degree: String,
    gender: String,
    occupation: String
) {
    val info =
        Person(
            id = ObjectId(),
            name = name,
            age = age,
            degree = degree,
            gender = gender,
            occupation = occupation
        )

    val collection = database.getCollection<Person>("Person")
    collection.insertOne(info).also {
        println("Inserted id: ${it.insertedId}")
    }
}