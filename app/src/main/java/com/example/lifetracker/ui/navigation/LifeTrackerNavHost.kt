package com.example.lifetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lifetracker.data.model.HistoryEntry
import com.example.lifetracker.ui.screens.dashboard.DashboardScreen
import com.example.lifetracker.ui.screens.dashboard.MainScreen
import com.example.lifetracker.ui.screens.health.AddMetricDataScreen
import com.example.lifetracker.ui.screens.health.EditMetricDataScreen
import com.example.lifetracker.ui.screens.health.EditMetricScreen
import com.example.lifetracker.ui.screens.nutrition.NutritionScreen
import com.example.lifetracker.ui.screens.photos.*
import com.example.lifetracker.ui.screens.progress.ViewProgressHistoryScreen
import com.example.lifetracker.ui.screens.settings.ProfileScreen
import com.example.lifetracker.ui.screens.settings.SettingsScreen
import com.example.lifetracker.ui.viewmodel.HealthViewModel

@Composable
fun LifeTrackerNavHost(
    navController: NavHostController,
    viewModel: HealthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController = navController, viewModel = viewModel)
        }

        composable(EDIT_WEIGHT_ROUTE) {
            EditMetricScreen(
                title = "Weight",
                metricName = "Weight",
                unit = "kg",
                navController = navController,
                viewModel = viewModel,
                onSave = { value, date -> viewModel.updateWeight(value.toString(), date) }
            )
        }

        composable(EDIT_HEIGHT_ROUTE) {
            EditMetricScreen(
                title = "Height",
                metricName = "Height",
                unit = "cm",
                navController = navController,
                viewModel = viewModel,
                onSave = { value, date -> viewModel.updateHeight(value.toString(), date) }
            )
        }

        composable(EDIT_BODY_FAT_ROUTE) {
            EditMetricScreen(
                title = "Body Fat",
                metricName = "Body Fat",
                unit = "%",
                navController = navController,
                viewModel = viewModel,
                onSave = { value, date -> viewModel.updateBodyFat(value.toString(), date) }
            )
        }

        composable(
            route = EDIT_METRIC_ROUTE,
            arguments = listOf(
                navArgument("metricName") { type = NavType.StringType },
                navArgument("unit") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val metricName = backStackEntry.arguments?.getString("metricName") ?: ""
            val unit = backStackEntry.arguments?.getString("unit") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            
            EditMetricScreen(
                title = title,
                metricName = metricName,
                unit = unit,
                navController = navController,
                viewModel = viewModel,
                onSave = { value, date ->
                    when (metricName) {
                        "Weight" -> viewModel.updateWeight(value.toString(), date)
                        "Height" -> viewModel.updateHeight(value.toString(), date)
                        "Body Fat" -> viewModel.updateBodyFat(value.toString(), date)
                        "Waist" -> viewModel.updateWaist(value.toString(), date)
                        "Bicep" -> viewModel.updateBicep(value.toString(), date)
                        "Chest" -> viewModel.updateChest(value.toString(), date)
                        "Thigh" -> viewModel.updateThigh(value.toString(), date)
                        "Shoulder" -> viewModel.updateShoulder(value.toString(), date)
                    }
                }
            )
        }

        composable(
            route = VIEW_BMI_HISTORY_ROUTE
        ) { backStackEntry ->
            ViewProgressHistoryScreen(
                navController = navController,
                viewModel = viewModel,
                metricName = "BMI",
                unit = ""
            )
        }

        composable(
            route = ADD_METRIC_DATA_ROUTE,
            arguments = listOf(
                navArgument("metricName") { type = NavType.StringType },
                navArgument("unit") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val metricName = backStackEntry.arguments?.getString("metricName") ?: ""
            val unit = backStackEntry.arguments?.getString("unit") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""

            AddMetricDataScreen(
                title = title,
                metricName = metricName,
                unit = unit,
                navController = navController,
                viewModel = viewModel,
                onSave = { value, date ->
                    when (metricName) {
                        "Weight" -> viewModel.updateWeight(value.toString(), date)
                        "Height" -> viewModel.updateHeight(value.toString(), date)
                        "Body Fat" -> viewModel.updateBodyFat(value.toString(), date)
                        "Waist" -> viewModel.updateWaist(value.toString(), date)
                        "Bicep" -> viewModel.updateBicep(value.toString(), date)
                        "Chest" -> viewModel.updateChest(value.toString(), date)
                        "Thigh" -> viewModel.updateThigh(value.toString(), date)
                        "Shoulder" -> viewModel.updateShoulder(value.toString(), date)
                    }
                }
            )
        }

        composable(
            route = EDIT_METRIC_DATA_ROUTE,
            arguments = listOf(
                navArgument("metricName") { type = NavType.StringType },
                navArgument("unit") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("value") { type = NavType.FloatType },
                navArgument("date") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val metricName = backStackEntry.arguments?.getString("metricName") ?: ""
            val unit = backStackEntry.arguments?.getString("unit") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val value = backStackEntry.arguments?.getFloat("value") ?: 0f
            val date = backStackEntry.arguments?.getLong("date") ?: System.currentTimeMillis()

            EditMetricDataScreen(
                title = title,
                metricName = metricName,
                unit = unit,
                initialValue = value,
                initialDate = date,
                navController = navController,
                viewModel = viewModel,
                onSave = { newValue, newDate ->
                    // Delete the old entry
                    val oldEntry = HistoryEntry(
                        value = value,
                        date = date,
                        metricName = metricName,
                        unit = unit
                    )
                    viewModel.deleteHistoryEntry(metricName, oldEntry)

                    // Add the updated entry
                    when (metricName) {
                        "Weight" -> viewModel.updateWeight(newValue.toString(), newDate)
                        "Height" -> viewModel.updateHeight(newValue.toString(), newDate)
                        "Body Fat" -> viewModel.updateBodyFat(newValue.toString(), newDate)
                        "Waist" -> viewModel.updateWaist(newValue.toString(), newDate)
                        "Bicep" -> viewModel.updateBicep(newValue.toString(), newDate)
                        "Chest" -> viewModel.updateChest(newValue.toString(), newDate)
                        "Thigh" -> viewModel.updateThigh(newValue.toString(), newDate)
                        "Shoulder" -> viewModel.updateShoulder(newValue.toString(), newDate)
                    }
                }
            )
        }
        
        composable(ADD_ENTRY_ROUTE) {
            AddMetricDataScreen(
                title = "Add Entry",
                metricName = "Weight",
                unit = "kg",
                navController = navController,
                viewModel = viewModel,
                onSave = { value, date -> viewModel.updateWeight(value.toString(), date) }
            )
        }

        composable(
            route = "photo_detail/{uri}",
            arguments = listOf(
                navArgument("uri") { 
                    type = NavType.StringType
                    nullable = false 
                }
            )
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            if (uri != null) {
                PhotoDetailScreen(
                    navController = navController,
                    viewModel = viewModel,
                    photoUri = uri
                )
            }
        }
        
        composable(
            route = PHOTO_COMPARE_ROUTE,
            arguments = listOf(
                navArgument("mainUri") { 
                    type = NavType.StringType
                    nullable = false 
                },
                navArgument("compareUri") { 
                    type = NavType.StringType
                    nullable = false 
                }
            )
        ) { backStackEntry ->
            val mainUri = backStackEntry.arguments?.getString("mainUri")
            val compareUri = backStackEntry.arguments?.getString("compareUri")
            if (mainUri != null && compareUri != null) {
                PhotoCompareScreen(
                    navController = navController,
                    viewModel = viewModel,
                    mainPhotoUri = mainUri,
                    comparePhotoUri = compareUri
                )
            }
        }
        
        composable(
            route = PHOTO_CATEGORY_ROUTE,
            arguments = listOf(
                navArgument("uri") { 
                    type = NavType.StringType
                    nullable = false 
                }
            )
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            if (uri != null) {
                PhotoCategoryScreen(
                    navController = navController,
                    viewModel = viewModel,
                    photoUri = uri
                )
            }
        }

        composable(
            route = VIEW_METRIC_HISTORY_ROUTE,
            arguments = listOf(
                navArgument("metricName") { type = NavType.StringType },
                navArgument("unit") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val metricName = backStackEntry.arguments?.getString("metricName") ?: ""
            val unit = backStackEntry.arguments?.getString("unit") ?: ""
            
            ViewProgressHistoryScreen(
                navController = navController,
                viewModel = viewModel,
                metricName = metricName,
                unit = unit
            )
        }
    }
} 
