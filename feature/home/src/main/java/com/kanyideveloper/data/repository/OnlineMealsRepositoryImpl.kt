/*
 * Copyright 2022 Joel Kanyi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kanyideveloper.data.repository

import com.kanyideveloper.core.model.Meal
import com.kanyideveloper.core.util.Resource
import com.kanyideveloper.core.util.safeApiCall
import com.kanyideveloper.core_network.MealDbApi
import com.kanyideveloper.data.mapper.toCategory
import com.kanyideveloper.data.mapper.toMeal
import com.kanyideveloper.domain.model.Category
import com.kanyideveloper.domain.model.OnlineMeal
import com.kanyideveloper.domain.repository.OnlineMealsRepository
import kotlinx.coroutines.Dispatchers

class OnlineMealsRepositoryImpl(
    private val mealDbApi: MealDbApi
) : OnlineMealsRepository {

    override suspend fun getMealCategories(): Resource<List<Category>> {
        return safeApiCall(Dispatchers.IO) {
            val response = mealDbApi.getCategories()
            response.categories.map { it.toCategory() }
        }
    }

    override suspend fun getMeals(category: String): Resource<List<OnlineMeal>> {
        return safeApiCall(Dispatchers.IO) {
            val response = mealDbApi.getMeals(category = category)
            response.meals.map { it.toMeal() }
        }
    }

    override suspend fun getMealDetails(mealId: String): Resource<List<Meal>> {
        return safeApiCall(Dispatchers.IO) {
            val response = mealDbApi.getMealDetails(mealId = mealId)
            response.meals.map { it.toMeal() }
        }
    }

    override suspend fun getRandomMeal(): Resource<List<Meal>> {
        return safeApiCall(Dispatchers.IO) {
            val response = mealDbApi.getRandomMeal()
            response.meals.map { it.toMeal() }
        }
    }
}
