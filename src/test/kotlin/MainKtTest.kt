
import Models.Person
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MainKtTest {

    @Test
    fun getDatabase_Should_ReturnDatabase() {

        val client: MongoClient
        val database: MongoDatabase
        if (System.getenv("MONGO_URI") != null) {
            client = MongoClient.create(System.getenv("MONGO_URI"))
            database = client.getDatabase(databaseName = "UCL")

            val result = getDatabase(client)

            Assertions.assertThat(result).isInstanceOf(MongoDatabase::class.java)
            Assertions.assertThat(result).isEqualTo(database)
        }
    }

    @Test
    fun getCollection_ShouldReturn_Collection() {
        // Arrange
        val client: MongoClient
        val database: MongoDatabase
        val expectedCollection: MongoCollection<Person>

        if (System.getenv("MONGO_URI") != null) {
            client = MongoClient.create(System.getenv("MONGO_URI"))
            database = client.getDatabase("UCL")
            expectedCollection = database.getCollection<Person>("Person")

            // Act
            val result = getCollection(database)

            // Assert
            Assertions.assertThat(result).isEqualTo(expectedCollection)
            Assertions.assertThat(result).isNotNull()
            Assertions.assertThat(result).isInstanceOf(MongoCollection::class.java)
        }
    }

    @Test
    fun getAllNames_ShouldReturnAllNames() {
        // Arrange
        val client: MongoClient

        if (System.getenv("MONGO_URI") != null) {
            client = MongoClient.create(System.getenv("MONGO"))
            val database = getDatabase(client)
            val collection = getCollection(database)

            // Act

            runBlocking {
                val result = getAllNames(collection)

                // Assert
                Assertions.assertThat(result).isNotNull()
                Assertions.assertThat(result).isInstanceOf(String::class.java)
                Assertions.assertThat(result).contains("["," - ","]")
            }
        }
    }

    @Test
    fun addPerson_Should_ReturnString() {

        //Arrange

        runBlocking {
            val client: MongoClient
            if (System.getenv("MONGO_URI") != null) {
                client = MongoClient.create(System.getenv("MONGO_URI"))
                val database = client.getDatabase("Person")
                val collection = database.getCollection<Person>("Person")

                // Act
                val result = addPerson(collection)

                Assertions.assertThat(result).isNotNull()
                Assertions.assertThat(result).isInstanceOf(String::class.java)
            }
        }
    }
}
