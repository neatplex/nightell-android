package com.neatplex.nightell.ui.screens.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.neatplex.nightell.R

/** Outcome of asking the user to pick a Google account. */
sealed interface GoogleSignInResult {
    /** The Google ID token to send to our backend. */
    data class Success(val idToken: String) : GoogleSignInResult

    /** The user closed the account picker; nothing to show. */
    data object Cancelled : GoogleSignInResult

    data class Failure(val message: String) : GoogleSignInResult
}

/**
 * Gets a Google ID token with Credential Manager's "Sign in with Google" flow,
 * the replacement for the deprecated One Tap API. The same token works for
 * signing in and signing up; the backend creates the account if needed.
 *
 * @param activityContext must be an Activity context, so the account picker can be shown.
 */
suspend fun requestGoogleIdToken(activityContext: Context): GoogleSignInResult {
    val option = GetSignInWithGoogleOption.Builder(activityContext.getString(R.string.web_client_id))
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(option)
        .build()

    return try {
        val credential = CredentialManager.create(activityContext)
            .getCredential(activityContext, request)
            .credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            GoogleSignInResult.Success(GoogleIdTokenCredential.createFrom(credential.data).idToken)
        } else {
            GoogleSignInResult.Failure("Unexpected sign-in response, please try again")
        }
    } catch (e: GetCredentialCancellationException) {
        GoogleSignInResult.Cancelled
    } catch (e: NoCredentialException) {
        GoogleSignInResult.Failure("No Google account available on this device")
    } catch (e: GoogleIdTokenParsingException) {
        GoogleSignInResult.Failure("Google sign-in failed, please try again")
    } catch (e: GetCredentialException) {
        GoogleSignInResult.Failure(e.message ?: "Google sign-in failed, please try again")
    }
}
