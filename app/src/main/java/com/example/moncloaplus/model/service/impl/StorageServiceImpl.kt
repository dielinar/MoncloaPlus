package com.example.moncloaplus.model.service.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.moncloaplus.model.User
import com.example.moncloaplus.model.WeekMeals
import com.example.moncloaplus.model.service.StorageService
import com.example.moncloaplus.utils.DATE_PATTERN
import com.example.moncloaplus.utils.WEEK_DAYS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    db: FirebaseFirestore
): StorageService {

    private val userCollection = db.collection("users")

    override suspend fun addUser(user: User) {
        userCollection.document(user.id)
            .set(user)
            .addOnSuccessListener {

                val defaultTemplate = WeekMeals(
                    id = "default",
                    meals = generateDefaultMeals()
                )
                userCollection.document(user.id)
                    .collection("template")
                    .document("default")
                    .set(defaultTemplate)
                    .addOnSuccessListener { Log.d(TAG, "Plantilla predeterminada creada.") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error al crear la plantilla.", e) }

                val currentWeekId = getStartOfWeek(Date())
                val defaultWeek = WeekMeals(
                    id = currentWeekId,
                    meals = generateDefaultMeals()
                )
                userCollection.document(user.id)
                    .collection("weeks")
                    .document(currentWeekId)
                    .set(defaultWeek)
                    .addOnSuccessListener { Log.d(TAG, "Semana inicial creada.") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error al crear la semana inicial.", e) }

            }
            .addOnFailureListener { e -> Log.w(TAG, "Error guardando usuario", e) }
    }

    override suspend fun deleteUser(userId: String) {
        userCollection.document(userId)
            .delete()
            .await()
    }

    override suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = userCollection.document(userId).get().await()
            snapshot.toObject<User>()
        } catch (e: Exception) {
            Log.e("Firestore", "Error getting user", e)
            null
        }
    }

    override suspend fun getUsers(): List<User> {
        return try {
            val result = userCollection.get().await()
            result.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener los usuarios", e)
            emptyList()
        }
    }

    override suspend fun updateUser(user: User) {
        userCollection.document(user.id)
            .set(user, SetOptions.merge())
            .await()
    }

    private fun generateDefaultMeals(): MutableMap<String, MutableMap<String, String>> {
        return WEEK_DAYS.associateWith {
            mutableMapOf(
                "Desayuno" to "-",
                "Comida" to "-",
                "Cena" to "-"
            )
        }.toMutableMap()
    }

    private fun getStartOfWeek(date: Date): String {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

}