package com.example.mcittask.viewModel

import android.app.*
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mcittask.BuildConfig
import com.example.mcittask.constants.AppConstants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class MainViewModel(application: Application) : AndroidViewModel(application) {

    fun createToken(context: Context) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            task.let {
                if (!task.isSuccessful) {
                    Log.d("TAG", "Fetching FCM registration token failed" + task.exception)
                    return@OnCompleteListener
                }

                val tokenResult = task.result!!
                val map = mapOf(
                    "id" to FirebaseDatabase.getInstance().reference.push().key,
                    "token" to tokenResult
                )

                FirebaseDatabase.getInstance().reference.child("Token")
                        .updateChildren(map).addOnSuccessListener {
                        createNotification(context)
                    }.addOnFailureListener {
                        Log.d("TAG", "OnFailureListener" + it.localizedMessage)
                    }
            }
        })
    }

    private fun createNotification(context: Context) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Token")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.child("token").value.toString()

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("message", "Welcome")

                    to.put("to", token)
                    to.put("data", data)

                    sendNotification(to, context)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: ${error.message}")
            }
        })
    }

    private fun sendNotification(to: JSONObject, context: Context) {
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, AppConstants.NOTIFICATION_URL, to,

            Response.Listener { response: JSONObject ->
                Log.d("TAG", "onResponse: $response")
            },
            Response.ErrorListener {
                Log.d("TAG", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + BuildConfig.serverKey
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)
    }

}