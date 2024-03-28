package com.example.photogallery

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import com.example.photogallery.databinding.ImageDetailDialogBinding

class PictureDialogFragment: DialogFragment() {

    private val args : PictureDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.image_detail_dialog, null)

            builder.setView(view)

            val photoFromGallery = view.findViewById<ImageView>(R.id.photo_from_gallery)

            photoFromGallery.load(args.photoPageUriDialog) {
                placeholder(R.drawable.zkzg)
            }
            builder.apply {
                setTitle(R.string.dialog_title_image)
                setNegativeButton(R.string.dialog_negative_button) {_, _ -> dialog?.cancel()}
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}