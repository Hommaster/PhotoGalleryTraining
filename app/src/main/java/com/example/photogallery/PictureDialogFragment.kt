package com.example.photogallery

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

class PictureDialogFragment: DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.image_detail_dialog, null)

            builder.setView(view)

            val photoGallery = view.findViewById<ImageView>(R.id.photo_from_gallery)

//            photoGallery.load()

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}