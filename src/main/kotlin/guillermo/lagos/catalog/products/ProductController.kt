package guillermo.lagos.catalog.products

import guillermo.lagos.catalog.products.data.*
import guillermo.lagos.catalog.products.request.ProductRequest
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/products")
class ProductController(
    private val patientsRepository: ProductRepository
) {

    @GetMapping("/delete")
    fun deleteAllProducts(): ResponseEntity<DefaultResponse> = patientsRepository.deleteAll()
        .let {
            ResponseEntity.ok(
                DefaultResponse(
                    status = "ok",
                    statusMsg = "Productos eliminados exitosamente"
                )
            )
        }


    @GetMapping("/all")
    fun getAllProducts(): ResponseEntity<ProductsResponse> {
        val products = patientsRepository.findAll()
        return ResponseEntity.ok(
            ProductsResponse(
                status = if (!products.isNullOrEmpty()) "ok" else "error",
                statusMsg = if (!products.isNullOrEmpty()) "Productos obtenidos exitosamente"
                else "No hay productos disponibles",
                products = products
            )
        )
    }

    @GetMapping("/get/{id}")
    fun getProductById(
        @PathVariable("id") id: String
    ): ResponseEntity<ProductByIdResponse> {
        val product = patientsRepository.findOneById(ObjectId(id))
        return ResponseEntity.ok(
            ProductByIdResponse(
                status = if (product != null) "ok" else "error",
                statusMsg = if (product != null) "Product obtenido exitosamente"
                else "No existe producto",
                product = product
            )
        )
    }

    @PostMapping("/create")
    fun createProduct(
        @RequestBody request: ProductRequest
    ): ResponseEntity<DefaultResponse> = when {

        request.name.isNullOrEmpty() -> ResponseEntity.ok(
            DefaultResponse(
                status = "error",
                statusMsg = "Nombre invalido"
            )
        )

        request.description.isNullOrEmpty() -> ResponseEntity.ok(
            DefaultResponse(
                status = "error",
                statusMsg = "Descripción invalida"
            )
        )

        else -> {
            patientsRepository.save(
                Product(
                    name = request.name,
                    description = request.description
                )
            )

            ResponseEntity(
                DefaultResponse(
                    status = "ok",
                    statusMsg = "Producto ${request.name} creado exitosamente"
                ),
                HttpStatus.CREATED
            )
        }
    }

    @PutMapping("/update/{id}")
    fun updateProduct(
        @RequestBody request: ProductRequest,
        @PathVariable("id") id: String
    ): ResponseEntity<DefaultResponse> {

        val product = patientsRepository
            .findOneById(ObjectId(id))
            ?.apply {
                name = request.name
                description = request.description
                modifiedDate = LocalDateTime.now()
            }


        val updatedProduct = if (product != null) patientsRepository.save(product)
        else null

        return ResponseEntity.ok(
            DefaultResponse(
                status = if (updatedProduct != null) "ok" else "error",
                statusMsg = if (updatedProduct != null) "Product actualizo exitosamente"
                else "No se pudo actualizar el producto",
            )
        )
    }

    @DeleteMapping("/delete/{id}")
    fun deleteProduct(
        @PathVariable("id") id: String
    ): ResponseEntity<DefaultResponse> {
        patientsRepository.deleteById(id)
        return ResponseEntity.ok(
            DefaultResponse(
                status = "ok",
                statusMsg = "Producto eliminado exitosamente"
            )
        )
    }
}