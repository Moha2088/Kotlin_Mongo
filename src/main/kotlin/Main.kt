import com.mongodb.MongoException
import com.mongodb.client.model.Filters
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

    val running:Boolean = true

//    while (running){
//        println("Enter your name: ")
//        val name = readln()
//        println("Enter your age: ")
//        val age = readln().toInt()
//        println("Enter your degree: ")
//        val degree = readln()
//        println("Enter your gender")
//        val gender = readln()
//        println("Enter your occupation: ")
//        val occupation = readln()
//        println("Enter your first skill")
//        val stack1 = readln()
//        println("Enter your second skill")
//        val stack2 = readln()
//        println("Enter your third skill")
//        val stack3 = readln()
//
//        val stackList:MutableList<String> = mutableListOf(stack1,stack2,stack3)

        try {
            runBlocking {

                println("Press 1 to read and 2 to delete")
                val choice = readln().toInt()

                when(choice){
                    1 -> readPerson(collection)
                    2-> deletePerson(collection)
                }
//            addPerson(database, name, age, degree, gender, occupation, stackList)
            }
        }

        catch (ex:MongoException){
            println(ex.message)
        }

        catch (exception: Exception) {
            print(exception.message)
        }

        client.close()
    }
//}

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
    val stack: MutableList<String>
)

suspend fun addPerson(
    database: MongoDatabase, name: String, age: Int, degree: String,
    gender: String, occupation: String, stack: MutableList<String>
) {
    val info =
        Person(
            id = ObjectId(),
            name = name,
            age = age,
            degree = degree,
            gender = gender,
            occupation = occupation,
            stack = stack
        )

    val collection = database.getCollection<Person>("Person")
    collection.insertOne(info).also {
        println("Inserted id: ${it.insertedId}")
    }
}

suspend fun readPerson(collection: MongoCollection<Person>){
    val database = getDatabase()

    val query = Filters.or(
        listOf(
            Filters.eq(Person::name.name, "Mohamed")
        )
    )

    collection.find(filter = query).collect{
        person ->
        print("Found person with name: ${person.name}, degree in: ${person.degree} and works as an ${person.occupation}")
    }
}

suspend fun deletePerson(collection: MongoCollection<Person>) {
    val query = Filters.eq(Person::name.name, "")

    if (query.equals(0)) {
       println("No document found with the given query!")
    }

    else
    {
        collection.deleteMany(filter = query).also {
            println("Deleted ${it.deletedCount} documents from the database")
        }
    }
}