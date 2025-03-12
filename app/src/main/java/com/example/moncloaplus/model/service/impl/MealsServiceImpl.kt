package com.example.moncloaplus.model.service.impl

import com.example.moncloaplus.model.WeekMeals
import com.example.moncloaplus.model.service.MealsService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MealsServiceImpl @Inject constructor(
    db: FirebaseFirestore,
    accService: AccountServiceImpl
): MealsService {

    private val userId = accService.currentUserId

    private val usersCollection = db.collection("users")

    private val weeksCollection: CollectionReference
        get() = userId.let { usersCollection.document(it).collection("weeks") }

    private val templateCollection: CollectionReference
        get() = userId.let { usersCollection.document(it).collection("template") }

    override suspend fun saveWeekData(weekMeals: WeekMeals) {
        weeksCollection.document(weekMeals.id)
            .set(weekMeals)
            .await()
    }

    override suspend fun getWeekData(weekId: String): WeekMeals? {
        return weeksCollection.document(weekId)
            .get()
            .await()
            .toObject<WeekMeals>()
    }

    override suspend fun getUserTemplate(): WeekMeals? {
        return templateCollection
            .document("default")
            .get()
            .await()
            .toObject<WeekMeals>()
    }

    override suspend fun saveTemplateData(template: WeekMeals) {
        templateCollection
            .document("default")
            .set(template)
            .await()
    }

}
