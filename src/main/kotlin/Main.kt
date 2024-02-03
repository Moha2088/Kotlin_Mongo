import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import fuel.Fuel
import fuel.get
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

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
        }

        catch (ex: MongoException) {
            println(ex.message)
        }

        catch (exception: Exception) {
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
    val id: ObjectId,
    val name: String,
    val age: Int,
    val degree: String,
    val gender: String,
    val occupation: String,
    val stack: MutableList<String>,
    val city: String
)

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
            city = city
        )

    collection.insertOne(info).also {
        println("Inserted id: ${it.insertedId} $name into the collection")
    }
}

suspend fun readPerson(collection: MongoCollection<Person>) {
    val database = getDatabase()

    val query = Filters.or(
        listOf(
            Filters.eq(Person::name.name, "Mohamed"),
            Filters.eq(Person::gender.name, "Female")
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
        println("Updated ${it.modifiedCount} documents in the collection")
    }
}

suspend fun deletePerson(collection: MongoCollection<Person>) {

    println("Enter the name of the person you want to delete")
    val personToDelete = readln()
    val query = Filters.eq(Person::name.name, personToDelete)

    println("Enter 1 to delete one or 2 to delete many")
    val input = readln().toInt()

    if (input == 1) {
        collection.deleteOne(query).also {
            println("Deleted one document from the collection")
        }
    }

    else if (input == 2) {

        collection.deleteMany(query).also {
            println("Deleted ${it.deletedCount} documents from the collection")
        }
    }
}

suspend fun fetch(uri: String) {
    val response = Fuel.get(uri).body
    println(response)
}