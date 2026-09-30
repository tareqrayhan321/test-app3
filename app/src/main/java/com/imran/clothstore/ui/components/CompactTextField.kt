package com.imran.clothstore.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.theme.AppColors

/**
 * কম হাইটের টেক্সট ফিল্ড — OutlinedTextField ৫৬dp এর নিচে নামতে পারে না, তাই
 * BasicTextField + OutlinedTextFieldDefaults.DecorationBox দিয়ে নিজস্ব হাইট/প্যাডিং সেট করা হয়েছে।
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    fieldHeight: Dp = 40.dp,
    shape: Shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    fontSize: TextUnit = 14.sp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    borderColor: Color = Color(0xFFD9D2C0),
    focusedBorderColor: Color = AppColors.HeaderTeal,
    containerColor: Color = Color.White
) {
    val interaction = remember { MutableInteractionSource() }
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = borderColor
    )
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = fontSize, color = Color(0xFF141413)),
        cursorBrush = SolidColor(AppColors.HeaderTeal),
        keyboardOptions = keyboardOptions,
        interactionSource = interaction,
        modifier = modifier.height(fieldHeight),
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interaction,
                placeholder = if (placeholder.isNotEmpty()) {
                    { Text(placeholder, color = Color(0xFF8E8B82), fontSize = fontSize) }
                } else null,
                colors = colors,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                container = {
                    OutlinedTextFieldDefaults.ContainerBox(
                        enabled = true,
                        isError = false,
                        interactionSource = interaction,
                        colors = colors,
                        shape = shape
                    )
                }
            )
        }
    )
}
