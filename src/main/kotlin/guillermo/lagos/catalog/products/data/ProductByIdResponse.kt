package guillermo.lagos.catalog.products.data

data class ProductByIdResponse(
    val status: String,
    val statusMsg: String,
    val product: Product? = null
)