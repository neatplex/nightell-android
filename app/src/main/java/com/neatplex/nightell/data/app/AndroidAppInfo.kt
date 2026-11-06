package com.neatplex.nightell.data.app

import android.content.Context
import com.neatplex.nightell.domain.repository.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Reads the installed version from the package manager. */
@Singleton
class AndroidAppInfo @Inject constructor(
    @ApplicationContext context: Context,
) : AppInfo {

    override val versionName: String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
}
