package com.abstergo.ui.apps.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CalculatorApp(
    viewModel: CalculatorViewModel = viewModel()
) {
    val display by viewModel.display.collectAsState()
    val expression by viewModel.expression.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            if (expression.isNotEmpty()) {
                Text(
                    text = expression,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = display,
                fontSize = if (display.length > 10) 28.sp else 40.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Button grid
        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "⌫", "=")
        )

        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { button ->
                        val isOperator = button in listOf("+", "-", "×", "÷", "=")
                        val isFunction = button in listOf("C", "±", "%")
                        val bgColor = when {
                            button == "=" -> Color(0xFF1A73E8)
                            isOperator -> Color(0xFFFF9500)
                            isFunction -> Color(0xFF505050)
                            else -> Color(0xFF333333)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .clickable {
                                    when (button) {
                                        "C" -> viewModel.onClearPressed()
                                        "±" -> viewModel.onToggleSign()
                                        "%" -> viewModel.onPercentPressed()
                                        "⌫" -> viewModel.onBackspacePressed()
                                        "=" -> viewModel.onEqualsPressed()
                                        "+", "-", "×", "÷" -> viewModel.onOperatorPressed(button)
                                        else -> viewModel.onDigitPressed(button)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = button,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
