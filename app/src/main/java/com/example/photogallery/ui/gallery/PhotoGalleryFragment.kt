package com.example.photogallery.ui.gallery

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.photogallery.R
import com.example.photogallery.api.ThumbnailLoader
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.GalleryItem
import com.example.photogallery.scheduler.PhotoWorker
import com.example.photogallery.ui.VisibleFragment
import com.example.photogallery.ui.web.PhotoPageActivity
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class PhotoGalleryFragment : VisibleFragment() {
    private val flickrFetcher by inject<FlickrFetcher>()
    private val viewModel: PhotoGalleryViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var thumbnailLoader: ThumbnailLoader<GalleryItemViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
        thumbnailLoader =
            ThumbnailLoader(flickrFetcher, Handler(Looper.getMainLooper())) { holder, bitmap ->
                holder.bind(BitmapDrawable(resources, bitmap))
            }
        lifecycle.addObserver(thumbnailLoader.fragmentLifecycleObserver)

        WorkManager.getInstance(requireContext()).cancelAllWork()
        val request = PeriodicWorkRequest.Builder(PhotoWorker::class.java, 1, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
            )
            .build()
        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork("loading", ExistingPeriodicWorkPolicy.KEEP, request)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.fetchPhotos(query ?: "")
                searchView.setQuery("", false)
                searchView.isIconified = true
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        searchView.setOnSearchClickListener {
            searchView.setQuery(viewModel.searchQuery, false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear -> {
                viewModel.fetchPhotos("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailLoader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(thumbnailLoader.viewLifecycleObserver)
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailLoader.viewLifecycleObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        /*viewModel.galleryItems.observe(
            viewLifecycleOwner
        ) {
            submitData(it)
        }*/
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    PhotoGalleryUiState.Loading -> Toast.makeText(
                        requireContext(),
                        "Loading...",
                        Toast.LENGTH_SHORT
                    ).show()
                    PhotoGalleryUiState.Error -> Toast.makeText(
                        requireContext(),
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                    is PhotoGalleryUiState.Data -> submitData(state.items)
                }
            }
        }
    }

    private fun submitData(it: List<GalleryItem>) {
        recyclerView.adapter = GalleryItemsAdapter(it, thumbnailLoader) { uri ->
            startActivity(PhotoPageActivity.newIntent(requireActivity(), uri))
        }
    }

    class GalleryItemsAdapter(
        private val items: List<GalleryItem>,
        private val thumbnailLoader: ThumbnailLoader<GalleryItemViewHolder>,
        private val onPhotoClicked: (Uri) -> Unit
    ) : RecyclerView.Adapter<GalleryItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
            return GalleryItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_photo, parent, false)
            ).apply {
                itemView.setOnClickListener {
                    onPhotoClicked.invoke(items[bindingAdapterPosition].photoPageUri)
                }
            }
        }

        override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
            holder.bind(ColorDrawable(Color.RED))
            thumbnailLoader.queue(holder, items[position].url)
        }

        override fun getItemCount() = items.size
    }

    class GalleryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(drawable: Drawable) {
            (itemView as ImageView).setImageDrawable(drawable)
        }
    }

    companion object {
        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment()
        }
    }
}