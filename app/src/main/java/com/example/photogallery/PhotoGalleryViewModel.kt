package com.example.photogallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.repository.PhotoRepository
import com.example.photogallery.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoGalleryViewModel : ViewModel() {

    private val photoRepository = PhotoRepository()

    private val preferencesRepository = PreferencesRepository.get()

    private val _uiState: MutableStateFlow<PhotoGalleryUiState> =
        MutableStateFlow(PhotoGalleryUiState())

    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()

    private val _suggestions: MutableList<String> = mutableListOf()
    val suggestions
        get() = _suggestions

    init {
        viewModelScope.launch {
            preferencesRepository.storedQuery.collectLatest { storedQuery ->
                try {
                    val items = searchPhotosQuery(storedQuery)
                    _uiState.update { oldState ->
                        oldState.copy(
                            images = items,
                            query = storedQuery
                        )
                    }
                    if(_suggestions.find { it == storedQuery } == null) {
                        _suggestions.add(storedQuery)
                    }
                } catch (ex:Exception) {
                    Log.e("PGViewModel", "Failed loading", ex)
                }
            }
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            preferencesRepository.setStoredQuery(query)
        }
    }

    private suspend fun searchPhotosQuery(query : String): List<GalleryItem> {
        return if(query.isNotEmpty()) {
            photoRepository.searchPhotos(query)
        } else {
            photoRepository.fetchPhotos()
        }
    }

}


data class PhotoGalleryUiState(
    val images: List<GalleryItem> = listOf(),
    val query: String = "",
)