
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class MainKtTest {

//    @Test
//    fun getDatabase_Should_ReturnDatabase() {
//        // Arrange
//        val client =
//            MongoClient.create(connectionString = "mongodb+srv://maxamed14:V8AV4PCGop0TFMt8@cluster0.dd89cjv.mongodb.net/?retryWrites=true&w=majority")
//        val database = client.getDatabase("UCL")
//        val expected = database
//
//        // Act
//        val result = getDatabase()
//
//        // Assert
//        assertNotNull(result)
//    }

    @Test
    fun fetch_ShouldReturn_String() {

        // Act
        runBlocking {
            val result = fetch("https://jsonplaceholder.typicode.com/posts/1")

            // Assert
            assertNotNull(result)
        }
    }
}