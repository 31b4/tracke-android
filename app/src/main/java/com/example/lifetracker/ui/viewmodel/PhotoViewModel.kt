package com.example.lifetracker.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracker.data.model.Photo
import com.example.lifetracker.data.model.PhotoCategory
import com.example.lifetracker.data.model.PhotoMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel responsible for managing photos and their metadata.
 * Handles loading, saving, updating, and deleting photos with their associated categories.
 */
class PhotoViewModel : ViewModel() {
    // State properties
    var photos by mutableStateOf<List<Photo>>(emptyList())
        private set
    
    var filteredPhotos by mutableStateOf<List<Photo>>(emptyList())
        private set
        
    var selectedCategory by mutableStateOf<PhotoCategory?>(null)
        
    private var isLoading by mutableStateOf(false)

    // Directory names
    private companion object {
        const val PHOTOS_DIR = "photos"
        const val METADATA_DIR = "photo_metadata"
        const val DATE_FORMAT = "yyyyMMdd_HHmmss"
    }

    /**
     * Loads all photos from the device storage and updates the state.
     * @param context The application context
     */
    fun loadPhotos(context: Context) {
        if (isLoading) return
        
        viewModelScope.launch {
            isLoading = true
            delay(300) // Prevent immediate loading when quickly swiping
            
            withContext(Dispatchers.IO) {
                val photosDir = ensureDirectoryExists(context, PHOTOS_DIR)
                val metadataDir = ensureDirectoryExists(context, METADATA_DIR)
                
                val photoList = photosDir.listFiles()?.mapNotNull { file ->
                    createPhotoFromFile(file, metadataDir)
                }?.sortedByDescending { it.timestamp } ?: emptyList()
                
                photos = photoList
                applyFilter()
            }
            isLoading = false
        }
    }

    /**
     * Saves a new photo with its category to the device storage.
     * @param context The application context
     * @param uri The URI of the photo to save
     * @param category The category of the photo (defaults to OTHER)
     */
    fun savePhoto(context: Context, uri: Uri, category: PhotoCategory = PhotoCategory.OTHER) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val photosDir = ensureDirectoryExists(context, PHOTOS_DIR)
                val metadataDir = ensureDirectoryExists(context, METADATA_DIR)

                val timestamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
                val fileName = "IMG_$timestamp.jpg"
                val photoFile = File(photosDir, fileName)

                savePhotoFile(context, uri, photoFile)
                savePhotoMetadata(metadataDir, fileName, category)
                
                loadPhotos(context)
            }
        }
    }
    
    /**
     * Updates the category of an existing photo.
     * @param context The application context
     * @param photo The photo to update
     * @param newCategory The new category for the photo
     */
    fun updatePhotoCategory(context: Context, photo: Photo, newCategory: PhotoCategory) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val file = File(photo.filePath)
                if (file.exists()) {
                    val metadataDir = ensureDirectoryExists(context, METADATA_DIR)
                    val metadataFile = File(metadataDir, "${file.name}.json")
                    
                    savePhotoMetadata(metadataDir, file.name, newCategory)
                    loadPhotos(context)
                }
            }
        }
    }

    /**
     * Deletes a photo and its metadata from the device storage.
     * @param context The application context
     * @param photo The photo to delete
     */
    fun deletePhoto(context: Context, photo: Photo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val file = File(photo.filePath)
                if (file.exists()) {
                    file.delete()
                    
                    val metadataDir = ensureDirectoryExists(context, METADATA_DIR)
                    val metadataFile = File(metadataDir, "${file.name}.json")
                    metadataFile.delete()
                    
                    loadPhotos(context)
                }
            }
        }
    }
    
    /**
     * Gets all photos of a specific category.
     * @param category The category to filter by
     * @return List of photos in the specified category
     */
    fun getPhotosOfCategory(category: PhotoCategory): List<Photo> {
        return photos.filter { it.category == category }
    }

    // Private helper methods
    private fun ensureDirectoryExists(context: Context, dirName: String): File {
        val dir = File(context.filesDir, dirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun createPhotoFromFile(file: File, metadataDir: File): Photo? {
        val uri = Uri.fromFile(file)
        val filePath = file.absolutePath
        val timestamp = Date(file.lastModified())
        
        val metadataFile = File(metadataDir, "${file.name}.json")
        val metadata = if (metadataFile.exists()) {
            try {
                val json = JSONObject(metadataFile.readText())
                val category = PhotoCategory.fromString(json.optString("category", PhotoCategory.OTHER.name))
                
                // Parse additional metadata
                val photoMetadata = PhotoMetadata(
                    weight = json.optDouble("weight", 0.0).takeIf { it > 0 }?.toFloat(),
                    bodyFatPercentage = json.optDouble("bodyFatPercentage", 0.0).takeIf { it > 0 }?.toFloat(),
                    notes = json.optString("notes", "")
                )
                
                // Parse measurements if they exist
                val measurementsObj = json.optJSONObject("measurements")
                val measurements = mutableMapOf<String, Float>()
                if (measurementsObj != null) {
                    measurementsObj.keys().forEach { key ->
                        measurements[key] = measurementsObj.optDouble(key, 0.0).toFloat()
                    }
                }
                
                Photo(uri, category, timestamp, filePath, photoMetadata.copy(measurements = measurements))
            } catch (e: Exception) {
                Photo(uri, PhotoCategory.OTHER, timestamp, filePath)
            }
        } else {
            Photo(uri, PhotoCategory.OTHER, timestamp, filePath)
        }
        
        return metadata
    }

    private fun savePhotoFile(context: Context, uri: Uri, photoFile: File) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(photoFile).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun savePhotoMetadata(metadataDir: File, fileName: String, category: PhotoCategory, metadata: PhotoMetadata = PhotoMetadata()) {
        val metadataFile = File(metadataDir, "$fileName.json")
        val json = JSONObject().apply {
            put("category", category.name)
            put("timestamp", Date().time)
            
            // Save additional metadata
            metadata.weight?.let { put("weight", it.toDouble()) }
            metadata.bodyFatPercentage?.let { put("bodyFatPercentage", it.toDouble()) }
            if (metadata.notes.isNotEmpty()) put("notes", metadata.notes)
            
            // Save measurements
            if (metadata.measurements.isNotEmpty()) {
                val measurementsObj = JSONObject()
                metadata.measurements.forEach { (key, value) ->
                    measurementsObj.put(key, value.toDouble())
                }
                put("measurements", measurementsObj)
            }
        }
        metadataFile.writeText(json.toString())
    }

    /**
     * Updates the photo metadata
     * @param context The application context
     * @param photo The photo to update
     * @param metadata The new metadata to save
     */
    fun updatePhotoMetadata(context: Context, photo: Photo, metadata: PhotoMetadata) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val file = File(photo.filePath)
                if (file.exists()) {
                    val metadataDir = ensureDirectoryExists(context, METADATA_DIR)
                    savePhotoMetadata(metadataDir, file.name, photo.category, metadata)
                    loadPhotos(context)
                }
            }
        }
    }

    fun applyFilter() {
        filteredPhotos = if (selectedCategory == null) {
            photos
        } else {
            photos.filter { it.category == selectedCategory }
        }
    }
} 