import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

fun main(args: Array<String>) {

    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))

    val database = getDatabase()

    runBlocking {
        addPerson(database)
    }

    try {
        runBlocking {
            database.listCollectionNames().collect(){
                println(it)
            }
        }
    }

    catch (exception:Exception)
    {
        print(exception.message)
    }

    client.close()

}

fun getDatabase():MongoDatabase{

    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
    return client.getDatabase(databaseName = "UCL")
}

data class Person(
    @BsonId
    val id: ObjectId,
    val name:String,
    val age:Int,
    val degree:String,
    val gender:String
)

suspend fun addPerson(database: MongoDatabase){
    val info = Person(id =ObjectId(),name ="Pali", age =23, degree = "PH.D : Physics",gender ="Woman")

    val collection = database.getCollection<Person>("Person")
    collection.insertOne(info).also {
        println("Inserted id: ${it.insertedId}")
    }
}