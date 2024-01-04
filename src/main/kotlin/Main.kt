import com.mongodb.MongoException
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoCollection
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

fun main(args: Array<String>) {


val client:com.mongodb.reactivestreams.client.MongoClient = MongoClients.create(System.getenv("MONGO_URI"))
    val database:com.mongodb.reactivestreams.client.MongoDatabase = client.getDatabase("UCL")
    val collection: MongoCollection<Document> = database.getCollection("Person")

    val documentsInDatabase = mutableListOf(collection.find())

    try {
        data class Person(
            @BsonId
            val id: ObjectId,
            val name: String,
            val age: Int,
            val degree: String,
            val gender: String)


    }

    catch (exception:MongoException)
    {
        print("Error:  ${exception.message}")
    }

    catch (exception:Exception)
    {
        print("${exception.message}")
    }

    documentsInDatabase.forEach{i ->

    }

 

}
