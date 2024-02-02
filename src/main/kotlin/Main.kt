import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
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

    while (running)
    {
        try {
            runBlocking {

                println("Press 1 to add, 2 to read, 3 to update or 4 to delete a person")
                val choice = readln().toInt()

                when(choice){
                    1 -> addPerson(collection)
                    2 -> readPerson(collection)
                    3 -> updatePerson(collection)
                    4 -> deletePerson(collection)

                    else -> {
                        throw IllegalArgumentException("Invalid integer. Enter a number from 1-4")
                    }
                }
            }
        }

        catch (ex:MongoException){
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
    val stack: MutableList<String>
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

    val stackList:MutableList<String> = mutableListOf(stack1,stack2,stack3)

    val info =
        Person(
            id = ObjectId(),
            name = name,
            age = age,
            degree = degree,
            gender = gender,
            occupation = occupation,
            stack = stackList
        )

    collection.insertOne(info).also {
        println("Inserted id: ${it.insertedId} $name into the collection")
    }
}

suspend fun readPerson(collection: MongoCollection<Person>){
    val database = getDatabase()

    val query = Filters.or(
        listOf(
            Filters.eq(Person::name.name, "Mohamed"),
            Filters.eq(Person::gender.name,"Female")
        )
    )

    collection.find(filter = query).collect{
        person ->
        print("Found person with name: ${person.name}, degree in: ${person.degree} and works as an ${person.occupation} The second skill is: ${person.stack[1]}")
    }
}

suspend fun updatePerson(collection: MongoCollection<Person>){

    val query = Filters.eq(Person::occupation.name, "Astrophycisist @ NASA")
    val update = Updates.combine(

        Updates.set(Person::name.name, "Neil Degrasse Tyson"),
        Updates.set(Person::degree.name, "PhD. Astrophysics"),
        Updates.set(Person::age.name, 65)
    )

    val updateOptions = UpdateOptions().upsert(true)

    collection.updateMany(filter = query,update,updateOptions).also {
        println("Updated ${it.modifiedCount} documents in the collection")
    }
}

suspend fun deletePerson(collection: MongoCollection<Person>) {

    println("Enter the name of the person you want to delete")
    val personToDelete = readln()
    val query = Filters.eq(Person::name.name, personToDelete)

    if (query.equals(0)) {
       println("No document found with the given query!")
    }

    else
    {
        collection.deleteMany(filter = query).also {
            println("Deleted ${it.deletedCount} documents from the collection")
        }
    }
}