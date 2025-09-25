package vm

import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.input.runUntilKeyPressed
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.textLine

class UI(private val vendingMachine: VendingMachine) {
    private val currencySymbol = "$"

    fun run() = session {
        var message by liveVarOf("")
        var display by liveVarOf(vendingMachine.display)
        var coinReturnDisplay by liveVarOf(vendingMachine.coinReturn.joinToString { it.name })
        var selectedCoinIndex by liveVarOf(0)

        section {
            textLine("Vending Machine")
            textLine("=============")
            textLine("Display: ${display}")
            textLine("Inserted: %s%.2f".format(currencySymbol, vendingMachine.insertedAmount / 100.0))
            textLine("Coin Return: ${coinReturnDisplay}")
            textLine("Dispensed: ${vendingMachine.dispensedProducts.joinToString { it.name }}")
            textLine()
            textLine("Products:")
            vendingMachine.productByShelf.forEach { (shelf, product) ->
                textLine("- Shelf ${shelf.ordinal + 1}: ${product.name} %s%.2f".format(currencySymbol, product.price / 100.0))
            }
            textLine()
            textLine("Controls:")
            textLine("  1-3 - Select Shelf")
            textLine("  UP/DOWN - Select Coin")
            textLine("  ENTER - Insert Selected Coin")
            textLine("  C - Cancel (return money)")
            textLine("  ESC - Exit")
            textLine()
            textLine("Insert Coins:")
            COINS.forEachIndexed { index, coin ->
                val prefix = if (index == selectedCoinIndex) "-> " else "   "
                textLine("$prefix${coin.name} (%s%.2f)".format(currencySymbol, coin.value / 100.0))
            }
            textLine()
            textLine(message)
        }.runUntilKeyPressed(Keys.ESC) {
            onKeyPressed {
                message = ""
                when (this.key) {
                    Keys.DIGIT_1 -> vendingMachine.selectShelf(Shelf.SHELF_1)
                    Keys.DIGIT_2 -> vendingMachine.selectShelf(Shelf.SHELF_2)
                    Keys.DIGIT_3 -> vendingMachine.selectShelf(Shelf.SHELF_3)
                    Keys.UP -> {
                        selectedCoinIndex = (selectedCoinIndex - 1 + COINS.size) % COINS.size
                    }
                    Keys.DOWN -> {
                        selectedCoinIndex = (selectedCoinIndex + 1) % COINS.size
                    }
                    Keys.ENTER -> {
                        if (COINS.isNotEmpty()) {
                            vendingMachine.insertCoin(COINS[selectedCoinIndex])
                        } else {
                            message = "No coins available to insert"
                        }
                    }
                    Keys.C -> vendingMachine.cancel()
                    Keys.ESC -> {}
                    else -> message = "Invalid input"
                }
                display = vendingMachine.display
                coinReturnDisplay = vendingMachine.coinReturn.joinToString { it.name }
            }
        }
    }

    companion object {
        private val COINS = Coin.entries.toTypedArray()
    }
}
