package com.bouchenna.rv_weather.service
import android.content.ContentValues.TAG
import android.util.Log
import com.bouchenna.rv_weather.Localisation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FireBase_db {

    private val db: FirebaseFirestore
    init {
         db = Firebase.firestore
    }

    suspend fun addLocalisation(loc: Localisation) {
        db.collection("Localisation")
            .add(loc)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    suspend fun getLocalisations(userId: String): ArrayList<Localisation> = withContext(Dispatchers.IO) {


        val locs: ArrayList<Localisation> = ArrayList()

        try {
            val querySnapshot = db.collection("Localisation")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                var loc = document.toObject<Localisation>()!!
                loc.documentId = document.id
                locs.add(loc)
                Log.d(TAG, "${document.id} => ${document.data}")
            }

        } catch (exception: Exception) {
            Log.e(TAG, "Error getting documents: ", exception)
        }

        locs
    }

    suspend fun deleteLocalisation(loc: Localisation){

        loc.documentId?.let {
            db.collection("Localisation").document(it)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                }
        }
    }
}