package com.example.photogallery

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import coil.load
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import com.example.photogallery.databinding.ImageDetailDialogBinding

class PictureDialogFragment: DialogFragment() {

    private var _binding : ImageDetailDialogBinding? = null

    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because is it null. It the view visible?"
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.image_detail_dialog, null)

            builder.setView(view)

//            binding.photoFromGallery.load()

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}