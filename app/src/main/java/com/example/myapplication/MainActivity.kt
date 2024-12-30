package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private var input: String = ""
    private var resultCalculated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.tvDisplay)

        val buttonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide,
            R.id.btnOpenParenthesis, R.id.btnCloseParenthesis, R.id.btnDot
        )

        for (id in buttonIds) {
            findViewById<Button>(id).setOnClickListener { onButtonClick((it as Button).text.toString()) }
        }

        findViewById<Button>(R.id.btnAC).setOnClickListener { clearDisplay() }
        findViewById<Button>(R.id.btnDel).setOnClickListener { deleteLast() }
        findViewById<Button>(R.id.btnEqual).setOnClickListener { calculateResult() }
    }

    private fun onButtonClick(value: String) {
        if (resultCalculated) {
            input = ""
            resultCalculated = false
        }
        input += value
        updateDisplay()
    }

    private fun clearDisplay() {
        input = ""
        updateDisplay()
    }

    private fun deleteLast() {
        if (input.isNotEmpty()) {
            input = input.dropLast(1)
            updateDisplay()
        }
    }

    private fun calculateResult() {
        try {
            val result = eval(input)
            input = result.toString()
            resultCalculated = true
            updateDisplay()
        } catch (e: Exception) {
            display.text = "Error"
        }
    }

    private fun updateDisplay() {
        display.text = if (input.isEmpty()) "0" else input
    }

    // Basic evaluator for math expressions
    private fun eval(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else -1
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat.code) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("Unexpected: ${ch.toChar()}")
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+') -> x += parseTerm()
                        eat('-') -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*') -> x *= parseFactor()
                        eat('/') -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // unary plus
                if (eat('-')) return -parseFactor() // unary minus

                var x: Double
                val startPos = pos
                if (eat('(')) { // parentheses
                    x = parseExpression()
                    eat(')')
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) { // numbers
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = expression.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: ${ch.toChar()}")
                }

                return x
            }
        }.parse()
    }
}
