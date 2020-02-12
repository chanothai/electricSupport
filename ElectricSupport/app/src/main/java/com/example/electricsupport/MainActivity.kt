package com.example.electricsupport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private const val TAG = "FireStore"

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()

    private var db = FirebaseFirestore.getInstance()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val user by lazy {
        return@lazy User(
            "ada",
            "Lovelace",
            1815,
            "0949948249"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAddUser.setOnClickListener {
            launch {
                Log.d(TAG, "UserId -> ${addUser()}")
            }
        }

        btnGetUser.setOnClickListener {
            launch {
                Log.d(TAG, "Get Users -> ${getUser()}")
            }
        }

        btnDeleteUser.setOnClickListener {
            launch {
                Log.d(TAG, "Delete User -> ${deleteUser(user)}")
            }
        }
    }

    private suspend fun addUser() {
        withContext(Dispatchers.IO) {
            Tasks.await(db.collection("users").document(user.phone).set(user))
        }
    }

    private suspend fun getUser(): List<User> {
        val users = mutableListOf<User>()
        val response = withContext(Dispatchers.IO) {
            Tasks.await(db.collection("users").get())
        }

        response.let {
            for (document in it) {
                val user = document.toObject(User::class.java)
                users.add(user)
            }
        }

        return users
    }

    private suspend fun deleteUser(user: User) {
        withContext(Dispatchers.IO) {
            Tasks.await(db.collection("users").document(user.phone).delete())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
