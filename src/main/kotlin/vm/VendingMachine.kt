package vm

import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

enum class Coin(val value: Int) {
    PENNY(1),
    NICKEL(5),
    DIME(10),
    QUARTER(25);
}

enum class Shelf {
    SHELF_1,
    SHELF_2,
    SHELF_3;
}

data class Product(val name: String, val price: Int)

class VendingMachine(
    val productByShelf: Map<Shelf, Product>,
    private var productInventory: MutableMap<Shelf, Int>,
    private var coinInventory: MutableMap<Coin, Int>,
) {
    private var insertedCoins: MutableList<Coin> = mutableListOf()
    private var lastSelectedShelf: Shelf? = null

    var display: String = "INSERT COIN"
        private set
    var coinReturn: MutableList<Coin> = mutableListOf()
        private set
    var dispensedProducts: MutableList<Product> = mutableListOf()
        private set
    val insertedAmount: Int get() = insertedCoins.sumOf { it.value }

    init {
        log.info { "Vending machine started." }
    }

    /* API ----------------------------------------------------------------- */

    fun insertCoin(coin: Coin) {
        insertedCoins.add(coin)
        display = "Inserted: %.2f".format(insertedAmount / 100.0)
    }

    fun selectShelf(shelf: Shelf) {
        val product = checkNotNull(productByShelf[shelf])

        if (insertedAmount == 0) {
            display = "Price: %.2f".format(product.price / 100.0)
            lastSelectedShelf = shelf
            return
        }

        val remaining = product.price - insertedAmount
        if (remaining > 0) {
            display = "Remaining: %.2f".format(remaining / 100.0)
        } else {
            purchase(shelf)
        }
        lastSelectedShelf = shelf
    }


    fun cancel() {
        coinReturn.addAll(insertedCoins)
        insertedCoins.clear()
        display = "INSERT COIN"
    }

    /* internal ------------------------------------------------------------ */

    private fun purchase(shelf: Shelf) {
        val product = productByShelf[shelf]!!
        val stock = productInventory.getOrDefault(shelf, 0)

        if (stock == 0) {
            display = "SOLD OUT"
            return
        }

        val changeAmount = insertedAmount - product.price
        if (!canDispenseChange(changeAmount)) {
            display = "EXACT CHANGE ONLY"
            coinReturn.addAll(insertedCoins)
            insertedCoins.clear()
            return
        }

        productInventory[shelf] = stock - 1
        dispenseProduct(product)
        insertedCoins.forEach { coin ->
            coinInventory[coin] = coinInventory.getOrDefault(coin, 0) + 1
        }
        insertedCoins.clear()

        dispenseChange(changeAmount)

        display = "THANK YOU"
        lastSelectedShelf = null
    }

    private fun dispenseProduct(product: Product) {
        dispensedProducts.add(product)
    }

    private fun dispenseChange(amount: Int) {
        if (amount == 0) return
        val change = calculateChange(amount)
        if (change == null) return

        coinReturn.addAll(change)
        change.forEach { coin ->
            coinInventory[coin] = coinInventory.getOrDefault(coin, 0) - 1
        }

    }

    private fun canDispenseChange(amount: Int): Boolean {
        if (amount == 0) return true
        return calculateChange(amount) != null
    }

    private fun calculateChange(amount: Int): List<Coin>? {
        var remaining = amount
        val change = mutableListOf<Coin>()
        val tempInventory = coinInventory.toMutableMap()

        for (coin: Coin in COINS_BY_DESCENDING_VALUE) {
            while (remaining >= coin.value && tempInventory.getOrDefault(coin, 0) > 0) {
                remaining -= coin.value
                tempInventory[coin] = tempInventory.getOrDefault(coin, 0) - 1
                change.add(coin)
            }
        }

        return if (remaining == 0) change else null
    }

    companion object {
        private val COINS_BY_DESCENDING_VALUE =
            Coin.entries.toTypedArray().sortedByDescending { it.value }
    }
}
