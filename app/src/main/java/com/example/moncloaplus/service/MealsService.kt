package com.example.moncloaplus.service

import com.example.moncloaplus.data.model.WeekMeals

interface MealsService {
    suspend fun saveWeekData(weekMeals: WeekMeals)
    suspend fun getWeekData(weekId: String): WeekMeals?
    suspend fun getUserTemplate(): WeekMeals?
    suspend fun saveTemplateData(template: WeekMeals)
}
