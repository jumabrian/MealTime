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
package com.kanyideveloper.presentation.home.mymeal

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanyideveloper.compose_ui.theme.Shapes
import com.kanyideveloper.core.model.Meal
import com.kanyideveloper.core.util.LottieAnim
import com.kanyideveloper.core.util.showDayCookMessage
import com.kanyideveloper.domain.model.MealCategory
import com.kanyideveloper.mealtime.core.R
import com.kanyideveloper.presentation.home.HomeNavigator
import com.kanyideveloper.presentation.home.HomeViewModel
import com.kanyideveloper.presentation.home.composables.MealItem
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun MyMealScreen(
    navigator: HomeNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val myMeals = viewModel.myMeals.observeAsState().value?.observeAsState()?.value

    MyMealScreenContent(
        myMeals = myMeals,
        viewModel = viewModel,
        navigator = navigator,
        openMealDetails = { meal ->
            navigator.openMealDetails(meal = meal)
        },
        addToFavorites = { localMealId, imageUrl, name ->
            viewModel.insertAFavorite(
                localMealId = localMealId,
                mealImageUrl = imageUrl,
                mealName = name
            )
        },
        removeFromFavorites = { id ->
            viewModel.deleteALocalFavorite(
                localMealId = id
            )
        },
        isSelected = { category ->
            viewModel.selectedCategory.value == category
        },
        onCategoryClick = { category ->
            viewModel.setSelectedCategory(category)
            viewModel.getMyMeals(viewModel.selectedCategory.value)
        }
    )
}

@Composable
private fun MyMealScreenContent(
    myMeals: List<Meal>?,
    viewModel: HomeViewModel,
    navigator: HomeNavigator,
    openMealDetails: (Meal) -> Unit = {},
    addToFavorites: (Int, String, String) -> Unit,
    removeFromFavorites: (Int) -> Unit,
    isSelected: (String) -> Boolean,
    onCategoryClick: (String) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item(span = { GridItemSpan(2) }) {
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                text = "Categories",
                style = MaterialTheme.typography.titleMedium
            )
        }
        item(span = { GridItemSpan(2) }) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mealCategories) { category ->
                    MyMealsCategoryItem(
                        category = category,
                        isSelected = isSelected,
                        onCategoryClick = onCategoryClick
                    )
                }
            }
        }
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(180.dp),
                shape = Shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(Modifier.fillMaxSize()) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = R.drawable.randomize_mealss),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = showDayCookMessage(),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Button(
                            onClick = {
                                navigator.openRandomMeals()
                            }
                        ) {
                            Text(
                                text = "Get a Random Meal",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (!myMeals.isNullOrEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    modifier = Modifier.padding(vertical = 3.dp),
                    text = "Meals",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        items(myMeals ?: emptyList()) { meal ->
            MealItem(
                modifier = Modifier.clickable {
                    openMealDetails(meal)
                },
                meal = meal,
                addToFavorites = addToFavorites,
                removeFromFavorites = removeFromFavorites,
                viewModel = viewModel
            )
        }

        if (myMeals.isNullOrEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("Empty State Component"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnim(
                        resId = R.raw.astronaut,
                        height = 120.dp
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "You don't have local meals, you can add some.",
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyMealsCategoryItem(
    category: MealCategory,
    isSelected: (String) -> Boolean,
    onCategoryClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .size(65.dp),
        shape = Shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected(category.name)) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        onClick = {
            onCategoryClick(category.name)
        }
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp),
                painter = painterResource(id = category.icon),
                contentDescription = null,
                tint = if (isSelected(category.name)) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = category.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected(category.name)) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

private val mealCategories = listOf(
    MealCategory(
        "All",
        R.drawable.fork_knife_thin
    ),
    MealCategory(
        "Food",
        R.drawable.ic_food
    ),
    MealCategory(
        "Breakfast",
        R.drawable.ic_breakfast
    ),
    MealCategory(
        "Drinks",
        R.drawable.ic_drinks
    ),
    MealCategory(
        "Fruits",
        R.drawable.ic_fruit
    ),
    MealCategory(
        "Fast Food",
        R.drawable.ic_pizza_thin
    )
)
