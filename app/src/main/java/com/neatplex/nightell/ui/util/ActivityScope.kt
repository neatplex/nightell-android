package com.neatplex.nightell.ui.util

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner

/**
 * The hosting activity as a [ViewModelStoreOwner], for ViewModels that must
 * outlive a single screen (for example a draft kept while switching tabs).
 */
@Composable
fun activityViewModelStoreOwner(): ViewModelStoreOwner = LocalContext.current.findActivity()

private tailrec fun Context.findActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> error("No ComponentActivity in the context chain")
}
