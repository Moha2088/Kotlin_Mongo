import Models.Person
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MainKtTest {

    @Test
    fun getDatabase_Should_ReturnDatabase() {

        val client: MongoClient
        if (System.getenv("MONGO_URI") != null) {
            client = MongoClient.create(System.getenv("MONGO_URI"))

            val result = getDatabase()

            Assertions.assertThat(result).isInstanceOf(MongoClient.javaClass)
            Assertions.assertThat(result).isEqualTo(client)
        }
    }

    @Test
    fun fetch_ShouldReturn_String() {

        // Assert
        val uri = "https://jsonplaceholder.typicode.com/posts/1"

        // Act
        runBlocking {

            val result = fetch(uri)

            //Assert
            Assertions.assertThat(result).isNotNull
            Assertions.assertThat(uri).startsWith("http")
            Assertions.assertThat(uri).isInstanceOf(String::class.java)
            Assertions.assertThat(uri).isNotNull()
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