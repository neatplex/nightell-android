package com.neatplex.nightell.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.theme.myLinearGradiant

@Composable
fun MyTitleOutlinedTextField() {
    var title by remember { mutableStateOf("") }

    OutlinedTextField(
        value = title,
        onValueChange = {
            title = it.take(25) // Limiting input to 25 characters
        },
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Color.White)
            )
            .padding(bottom = 1.dp), // Add padding to the bottom to create the appearance of a bottom border
        label = {
            Text("Title", color = Color.Black) // Changing label color
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = Color.White.copy(alpha = 0.5f), // Changing background color
            textColor = Color.Black, // Changing text color
            focusedBorderColor = Color.White
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text
        )
    )
}

//@Composable
//fun CustomSearchField() {
//    var text by remember { mutableStateOf(TextFieldValue("")) }
//    val linearGradientBrush = myLinearGradiant()
//
//    Box(
//        modifier = Modifier
//            .padding(16.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .background(Color.White)
//            .height(50.dp)
//            .fillMaxWidth(),
//        contentAlignment = Alignment.CenterStart
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(end = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(50.dp)
//                    .clip(CircleShape)
//                    .background(Color.LightGray.copy(alpha = 0.5f))
//                    .padding(4.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_search_24), // Your pink search icon
//                    contentDescription = "Search Icon",
//                    modifier = Modifier
//                        .size(32.dp)
//                        .graphicsLayer(alpha = 0.99f)
//                        .drawWithCache {
//                            onDrawWithContent {
//                                drawContent()
//                                drawRect(linearGradientBrush, blendMode = BlendMode.SrcAtop)
//                            }
//                        },
//                )
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Box(modifier = Modifier.weight(1f)) {
//                if (text.text.isEmpty()) {
//                    Text(
//                        text = "Text Here....",
//                        color = Color(0xFFD3A4D3),
//                        fontSize = 16.sp,
//                        modifier = Modifier.align(Alignment.CenterStart)
//                    )
//                }
//                BasicTextField(
//                    value = text,
//                    onValueChange = { newText -> text = newText },
//                    textStyle = LocalTextStyle.current.copy(
//                        color = Color.Black,
//                        fontSize = 16.sp,
//                        textAlign = TextAlign.Start
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                )
//            }
//        }
//    }
//}

@Composable
fun CustomSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(value)) }
    val linearGradientBrush = myLinearGradiant()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .height(50.dp)
            .fillMaxWidth(),
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
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_search_24), // Your pink search icon
                    contentDescription = "Search Icon",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(linearGradientBrush, blendMode = BlendMode.SrcAtop)
                            }
                        },
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (text.text.isEmpty()) {
                    Text(
                        text = "Text Here....",
                        color = Color(0xFFD3A4D3),
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