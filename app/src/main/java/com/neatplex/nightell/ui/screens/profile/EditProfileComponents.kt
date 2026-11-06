package com.neatplex.nightell.ui.screens.profile

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.MainActivity
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.User

fun signOut(context: Context) {

    // Restart the application
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)

    if (context is ComponentActivity) {
        context.finish()
    }
}


@Composable
fun ProfileImageWithUploadIcon(
    imageResource: AsyncImagePainter,
    chooseImageLauncher: () -> Unit
) {
    Box(
        modifier = Modifier.size(120.dp).clickable {
            chooseImageLauncher()
        }
    ) {
        // Profile Image
        Image(
            painter = imageResource,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Upload Icon in a Circle at the Bottom Right
        Box(
            modifier = Modifier
                .size(32.dp)  // Size of the circular icon container
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.BottomEnd)
                .border(1.dp, Color.LightGray, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "Upload Icon",
                tint = Color.Gray,  // Set the color of the upload icon
                modifier = Modifier
                    .size(20.dp) // Size of the upload icon itself
                    .align(Alignment.Center)
            )
        }
    }
}


@Composable
fun getUserImagePainter(user: User?): AsyncImagePainter {
    return if (user?.image == null) {
        rememberAsyncImagePainter(model = R.drawable.default_profile_image)
    } else {
        rememberAsyncImagePainter(model = user.image?.url)
    }
}