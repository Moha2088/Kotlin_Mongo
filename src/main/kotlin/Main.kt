import Models.Person
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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
import org.bson.types.ObjectId
import java.io.IOException
import okhttp3.Request.Builder as RequestBuilder

fun main(args: Array<String>) {

    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
    val database = getDatabase(client)
    val collection = getCollection(database)
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
                    6 -> println(getAllNames(collection))

                    else -> throw IllegalArgumentException("Invalid integer. Enter a number from 1-4")
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

fun getDatabase(client: MongoClient): MongoDatabase {
    return client.getDatabase(databaseName = "UCL")
}

fun getCollection(database: MongoDatabase): MongoCollection<Person> {
    return database.getCollection<Person>("Person")
}

suspend fun getAllNames(collection: MongoCollection<Person>):String {
    val namesList: MutableList<String> = mutableListOf()
    val query = Filters.empty()
    collection.find(query).collect{namesList.add(it.name)}

    return namesList.joinToString(separator = " - ", prefix = "[ ", postfix = " ]")
}

suspend fun addPerson(
    collection: MongoCollection<Person>
): String {
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
        return "Inserted id: ${it.insertedId} $name into the collection"
    }
}

suspend fun readPerson(collection: MongoCollection<Person>) {
    suspend fun readQueryFilter(arg: Any, case: String) {

        if (case.equals("_id")) {
            val input: String = arg.toString()
            val objId: ObjectId = ObjectId(input)
            val query = Filters.eq(case, objId)

            collection.find(query).collect { person ->
                println("\n\nName: ${person.name} \n\nId: ${person.id}\n\n Works as: ${person.occupation}\n\n Has a degree in: ${person.degree}")
            }
        } else {
            val query = Filters.or(listOf(Filters.eq(case, arg)))

            collection.find(query).collect { person ->
                println("\n\nName: ${person.name} \n\nId: ${person.id}\n\n Works as: ${person.occupation}\n\n Has a degree in: ${person.degree}")
            }
        }
    }

    println("Enter the field for the query")
    val filter = readln()

    when (filter) {

        "_id" -> println("Enter the id number").also {
            val id = readln()
            readQueryFilter(id, "_id")
        }

        "name" -> println("Enter the name").also {
            val name = readln()
            readQueryFilter(name, "name")
        }

        "age" -> println("Enter the age").also {
            val age = readln().toInt()
            readQueryFilter(age, "age")
        }

        "degree" -> println("Enter the degree").also {
            val degree = readln()
            readQueryFilter(degree, "degree")

        }

        "gender" -> println("Enter the gender").also {
            val gender = readln()
            readQueryFilter(gender, "gender")
        }

        "occupation" -> println("Enter the occupation").also {
            val occupation = readln()
            readQueryFilter(occupation, "occupation")
        }

        "city" -> println("Enter the city").also {
            val city = readln()
            readQueryFilter(city, "city")
        }
    }
}

suspend fun updatePerson(collection: MongoCollection<Person>) {

    println("Enter the name of the person to update")
    val name = readln()


    val query = Filters.eq(Person::name.name, name)
    println("Enter the new name")
    val newName = readln()

    val update = Updates.set(Person::name.name, newName)
    val options = UpdateOptions().upsert(true)

    try {
        collection.updateOne(query, update, options).also {
            if (it.modifiedCount.toInt() == 0) {
                println("No documents modified")
            } else {
                println("${it.modifiedCount} documents has been modified")
            }
        }
    } catch (ex: Exception) {
        println(ex.message)
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

data class Post(
    val userid: Int,
    val id: Int,
    val title: String,
    val body: String
)

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
                val gson = Gson()
                response.body?.string().let { jsonString ->
                    try {
                        val desObj = gson.fromJson(jsonString, Post::class.java)
                        val objArray = arrayOf(desObj)

                        for (item in objArray) {
                            println("Id: ${item.id}\n\nTitle: ${item.title}\n\nBody: ${item.body}")
                        }
                    } catch (illEx: IllegalStateException) {
                        println(illEx.message)
                    } catch (jsonEx: JsonSyntaxException) {
                        println("Error parsing JSON: ${jsonEx.message}")
                    } catch (ex: Exception) {
                        println(ex.message)
                    }
                }
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            TODO("Not yet implemented")
        }
    })
}