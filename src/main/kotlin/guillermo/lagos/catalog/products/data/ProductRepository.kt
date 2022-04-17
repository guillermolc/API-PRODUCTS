package guillermo.lagos.catalog.products.data

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductRepository : MongoRepository<Product, String> {
    fun findOneById(id: ObjectId): Product?
    override fun deleteAll()

}