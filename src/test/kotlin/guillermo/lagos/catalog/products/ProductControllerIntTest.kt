package guillermo.lagos.catalog.products

import guillermo.lagos.catalog.products.data.Product
import guillermo.lagos.catalog.products.data.ProductRepository
import guillermo.lagos.catalog.products.request.ProductRequest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerIntTest @Autowired constructor(
    private val productRepository: ProductRepository,
    private val restTemplate: TestRestTemplate
) {
    private val defaultProductId = ObjectId.get()

    @LocalServerPort
    protected var port: Int = 8090

    @BeforeEach
    fun setUp() {
        productRepository.deleteAll()
    }


    private fun getRootUrl(): String? = "http://localhost:$port/products"

    private fun saveOnePatient() = productRepository.save(
        Product(
            id = defaultProductId,
            name = "Name",
            description = "Description"
        )
    )

    private fun productRequest() = ProductRequest(
        name = "Name",
        description = "Default description"
    )

    @Test
    fun `should return all patients`() {
        saveOnePatient()

        val response = restTemplate.getForEntity(
                getRootUrl(),
                List::class.java
        )

        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals(1, response.body?.size)
    }

    @Test
    fun `should return single patient by id`() {
        saveOnePatient()

        val response = restTemplate.getForEntity(
                getRootUrl() + "/$defaultProductId",
                Product::class.java
        )

        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals(defaultProductId, response.body?.id)
    }

    @Test
    fun `should delete existing patient`() {
        saveOnePatient()

        val delete = restTemplate.exchange(
                getRootUrl() + "/$defaultProductId",
                HttpMethod.DELETE,
                HttpEntity(null, HttpHeaders()),
                ResponseEntity::class.java
        )

        assertEquals(204, delete.statusCode.value())
        assertThrows(EmptyResultDataAccessException::class.java) { productRepository.findOneById(defaultProductId) }
    }

    @Test
    fun `should update existing patient`() {
        saveOnePatient()
        val patientRequest = productRequest()

        val updateResponse = restTemplate.exchange(
                getRootUrl() + "/$defaultProductId",
                HttpMethod.PUT,
                HttpEntity(patientRequest, HttpHeaders()),
                Product::class.java
        )
        val updatedTask = productRepository.findOneById(defaultProductId)

        assertEquals(200, updateResponse.statusCode.value())
        //assertEquals(defaultProductId, updatedTask.id)
        //assertEquals(patientRequest.description, updatedTask.description)
        //assertEquals(patientRequest.name, updatedTask.name)
    }

    @Test
    fun `should create new product`() {
        val productRequest = productRequest()

        val response = restTemplate.postForEntity(
                getRootUrl(),
            productRequest,
                Product::class.java
        )


        assertEquals(201, response.statusCode.value())
        assertNotNull(response.body)
        assertNotNull(response.body?.id)
        assertEquals(productRequest.description, response.body?.description)
        assertEquals(productRequest.name, response.body?.name)
    }

}