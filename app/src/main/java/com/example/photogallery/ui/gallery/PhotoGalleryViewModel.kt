package com.example.photogallery.ui.gallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.GalleryItem
import com.example.photogallery.data.QueryStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel(
    application: Application,
    private val flickrFetcher: FlickrFetcher
) : AndroidViewModel(application) {
    private val searchStateFlow = MutableStateFlow(QueryStorage.getQuery(application))

    private var _uiState: MutableStateFlow<PhotoGalleryUiState> =
        MutableStateFlow(PhotoGalleryUiState.Loading)
    val uiState: StateFlow<PhotoGalleryUiState> = _uiState

    init {
        update()
    }

    private fun update() {
        viewModelScope.launch {
            val query = searchStateFlow.value
            try {
                _uiState.value = PhotoGalleryUiState.Data(
                    if (query.isBlank()) {
                        flickrFetcher.fetchPhotos()
                    } else {
                        flickrFetcher.searchPhotos(query)
                    }
                )
            } catch (ex: Exception) {
                Log.e(TAG, "ex: $ex")
                _uiState.value = PhotoGalleryUiState.Error
            }
        }
    }

    private fun createGalleryItemsLiveData(): LiveData<List<GalleryItem>> {
        val liveData = MutableLiveData<List<GalleryItem>>()
        viewModelScope.launch {
            val query = searchStateFlow.value
            try {
                liveData.value = if (query.isBlank()) {
                    flickrFetcher.fetchPhotos()
                } else {
                    flickrFetcher.searchPhotos(query)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "ex: $ex")
            }
        }
        return liveData
    }

    val searchQuery: String
        get() = searchStateFlow.value

    fun fetchPhotos(query: String = "") {
        QueryStorage.saveQuery(getApplication(), query)
        searchStateFlow.value = query
        update()
    }
}

sealed class PhotoGalleryUiState {
    object Loading : PhotoGalleryUiState()
    data class Data(val items: List<GalleryItem>) : PhotoGalleryUiState()
    object Error : PhotoGalleryUiState()
}