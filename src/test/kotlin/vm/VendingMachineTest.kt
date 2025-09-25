package vm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import java.util.Locale

class VendingMachineTest {
    private val products = mapOf(
        Shelf.SHELF_1 to Product("Cola", 100),
        Shelf.SHELF_2 to Product("Chips", 50),
        Shelf.SHELF_3 to Product("Candy", 65),
    )

    @BeforeEach
    fun setUp() {
        Locale.setDefault(Locale.US)
    }

    @Test
    fun `displays INSERT COIN when no coins are inserted`() {
        val shelfInventory =
            mutableMapOf(Shelf.SHELF_1 to 1, Shelf.SHELF_2 to 1, Shelf.SHELF_3 to 1)
        val vendingMachine =
            VendingMachine(products, shelfInventory, mutableMapOf(Coin.PENNY to 100))
        assertEquals("INSERT COIN", vendingMachine.display)
    }

    @Test
    fun `selects a shelf and displays the remaining amount`() {
        val shelfInventory = mutableMapOf(Shelf.SHELF_1 to 1)
        val vendingMachine = VendingMachine(products, shelfInventory, mutableMapOf())
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.selectShelf(Shelf.SHELF_1)
        assertEquals("Remaining: 0.75", vendingMachine.display)
    }

    @Test
    fun `purchases a product with exact change`() {
        val shelfInventory = mutableMapOf(Shelf.SHELF_1 to 1)
        val vendingMachine = VendingMachine(products, shelfInventory, mutableMapOf())
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.selectShelf(Shelf.SHELF_1)
        assertEquals("THANK YOU", vendingMachine.display)
        assertEquals(0, shelfInventory[Shelf.SHELF_1])
    }

    @Test
    fun `purchases a product and returns change`() {
        val shelfInventory = mutableMapOf(Shelf.SHELF_3 to 1)
        val cashInventory = mutableMapOf(Coin.DIME to 1, Coin.NICKEL to 1)
        val vendingMachine = VendingMachine(products, shelfInventory, cashInventory)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.selectShelf(Shelf.SHELF_3)
        assertEquals("THANK YOU", vendingMachine.display)
        assertEquals(0, shelfInventory[Shelf.SHELF_3])
        assertTrue(vendingMachine.coinReturn.contains(Coin.DIME))
    }

    @Test
    fun `displays SOLD OUT when product is out of stock`() {
        val shelfInventory = mutableMapOf(Shelf.SHELF_1 to 0)
        val vendingMachine = VendingMachine(products, shelfInventory, mutableMapOf())
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.selectShelf(Shelf.SHELF_1)
        assertEquals("SOLD OUT", vendingMachine.display)
    }

    @Test
    fun `displays EXACT CHANGE ONLY when machine cannot make change`() {
        val shelfInventory = mutableMapOf(Shelf.SHELF_2 to 1)
        val vendingMachine = VendingMachine(products, shelfInventory, mutableMapOf())
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.selectShelf(Shelf.SHELF_2)
        assertEquals("EXACT CHANGE ONLY", vendingMachine.display)
    }

    @Test
    fun `cancels purchase and returns inserted coins`() {
        val shelfInventory =
            mutableMapOf(Shelf.SHELF_1 to 1, Shelf.SHELF_2 to 1, Shelf.SHELF_3 to 1)
        val vendingMachine =
            VendingMachine(products, shelfInventory, mutableMapOf(Coin.PENNY to 100))
        vendingMachine.insertCoin(Coin.QUARTER)
        vendingMachine.insertCoin(Coin.DIME)
        vendingMachine.cancel()
        assertEquals("INSERT COIN", vendingMachine.display)
        assertTrue(vendingMachine.coinReturn.contains(Coin.QUARTER))
        assertTrue(vendingMachine.coinReturn.contains(Coin.DIME))
    }
}
