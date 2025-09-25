package vm

fun main() {
    val products = mapOf(
        Shelf.SHELF_1 to Product("Cola", 100),
        Shelf.SHELF_2 to Product("Chips", 50),
        Shelf.SHELF_3 to Product("Candy", 65),
    )

    val shelfInventory = mutableMapOf(
        Shelf.SHELF_1 to 5,
        Shelf.SHELF_2 to 5,
        Shelf.SHELF_3 to 5,
    )

    val cashInventory = mutableMapOf(
        Coin.PENNY to 10,
        Coin.NICKEL to 10,
        Coin.DIME to 10,
        Coin.QUARTER to 10,
    )

    val vendingMachine = VendingMachine(products, shelfInventory, cashInventory)
    val ui = UI(vendingMachine)
    ui.run()
}
