package com.neatplex.nightell.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.R

@Composable
fun CustomSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(value)) }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .height(50.dp)
            .fillMaxWidth()
            .background(color = Color.LightGray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.search), // Your pink search icon
                    contentDescription = "Search Icon",
                    modifier = Modifier
                        .size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (text.text.isEmpty()) {
                    Text(
                        text = "Text Here....",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                        onValueChange(newText.text)
                    },
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}