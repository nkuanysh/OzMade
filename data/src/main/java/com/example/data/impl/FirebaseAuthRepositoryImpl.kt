package com.example.data.impl

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class OtpSession(val phone: String, val verificationId: String)

interface FirebaseAuthRepository {
    fun requestOtp(activity: Activity, phone: String): Flow<Result<OtpSession>>
    fun verifyOtp(verificationId: String, code: String): Flow<Result<Unit>>
}

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthRepository {

    override fun requestOtp(activity: Activity, phone: String): Flow<Result<OtpSession>> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Авто-верификация (иногда код вводить не надо)
                Log.d("AUTH", "onVerificationCompleted: auto verified")

                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener { trySend(Result.success(OtpSession(phone, "AUTO"))) }
                    .addOnFailureListener { e -> trySend(Result.failure(e)) }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("AUTH", "onVerificationFailed", e)

                trySend(Result.failure(e))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("AUTH", "onCodeSent: verificationId=$verificationId")

                trySend(Result.success(OtpSession(phone, verificationId)))
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone) // формат: +7XXXXXXXXXX
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose { /* nothing */ }
    }

    override fun verifyOtp(verificationId: String, code: String): Flow<Result<Unit>> = callbackFlow {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { trySend(Result.success(Unit)); close() }
            .addOnFailureListener { e -> trySend(Result.failure(e)); close() }
        awaitClose { }
    }
}
