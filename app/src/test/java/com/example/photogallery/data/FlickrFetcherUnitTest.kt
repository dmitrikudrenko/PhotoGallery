package com.example.photogallery.data

import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.FlickrResponse
import com.example.photogallery.api.PhotoResponse
import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FlickrFetcherUnitTest {
    private lateinit var flickrApi: FlickrApi
    private lateinit var flickrFetcher: FlickrFetcher

    @Before
    fun setUp() {
        flickrApi = mockkClass(FlickrApi::class)
        flickrFetcher = FlickrFetcher(flickrApi)
    }

    @Test
    fun `should return empty list`() {
        coEvery { flickrApi.fetchContent() } returns Response.success(null)
        runBlocking {
            assert(flickrFetcher.fetchPhotos().isEmpty())
        }
    }

    @Test
    fun `should return data`() {
        val flickrResponse = FlickrResponse().apply {
            photos = PhotoResponse().apply {
                galleryItems = listOf(GalleryItem(url = "not_empty"))
            }
        }
        coEvery { flickrApi.fetchContent() } returns Response.success(flickrResponse)
        runBlocking {
            val photos = flickrFetcher.fetchPhotos()
            assert(photos.size == 1)
        }
    }
}