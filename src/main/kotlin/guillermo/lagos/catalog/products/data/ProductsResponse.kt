package guillermo.lagos.catalog.products.data

data class ProductsResponse(
    val status: String,
    val statusMsg: String,
    val products: List<Product>
)