package com.example.photogallery

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.databinding.ListItemGalleryBinding

class PhotoViewHolder(
    private val binding: ListItemGalleryBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(galleryItem: GalleryItem,
             isWebView: Boolean,
             onItemClicked: (Uri) -> Unit,
             onPhotoClicked: (String) -> Unit) {
        binding.itemImageView.load(galleryItem.uri) {
            // While waiting for the image to load, a standard image is inserted
            placeholder(R.drawable.zkzg)
        }
        binding.root.setOnClickListener { if(isWebView) {
            onItemClicked(galleryItem.photoPageUri)
        } else {
            onPhotoClicked(galleryItem.uri)
        }
        }
    }
}

class PhotoListAdapter(
    private val galleryItems: List<GalleryItem>,
    private val isWebView: Boolean,
    private val onItemClicked: (Uri) -> Unit,
    private val onPhotoClicked: (String) -> Unit
): ListAdapter<GalleryItem, PhotoViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflate, parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = galleryItems[position]
        holder.bind(item, isWebView, onItemClicked, onPhotoClicked)
    }

    override fun getItemCount() = galleryItems.size
}

class DiffCallback: DiffUtil.ItemCallback<GalleryItem>() {

    override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
        return oldItem == newItem
    }

}
