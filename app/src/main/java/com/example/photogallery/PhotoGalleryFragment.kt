package com.example.photogallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.CursorAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import kotlinx.coroutines.launch

class PhotoGalleryFragment : Fragment(), MenuProvider {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels()

    private var searchView: SearchView? = null
    private var searchViewBooleanState: Boolean = false

    private var _binding : FragmentPhotoGalleryBinding? = null
    private var searchItem: MenuItem? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because is it null. It the view visible?"
        }

    // need add menuHost in onViewCreated
    // in Manifest themes need AppTheme
    // AppTheme need create in style.xml
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu)

        searchItem = menu.findItem(R.id.menu_item_search)
        searchView = searchItem!!.actionView as? SearchView

        searchView?.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)?.threshold =
            1

        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        val cursorAdapter = SimpleCursorAdapter(
            context,
            R.layout.search_item,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        searchView?.suggestionsAdapter = cursorAdapter

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                photoGalleryViewModel.setQuery(query ?: "")
                searchViewBooleanState = true
                searchItem?.isVisible = false
                binding.progressbar.visibility = View.VISIBLE
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val cursor =
                    MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                newText?.let {
                    photoGalleryViewModel.suggestions.forEachIndexed { index, suggestion ->
                        if (suggestion.contains(newText, true))
                            cursor.addRow(arrayOf(index, suggestion))
                    }
                }
                cursorAdapter.changeCursor(cursor)
                return true
            }
        })

        searchView?.setOnSuggestionListener(object : SearchView.OnSuggestionListener {

            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard()
                val cursor = searchView!!.suggestionsAdapter.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                searchView?.setQuery(selection, false)
                photoGalleryViewModel.setQuery(selection ?: "")
                return true
            }
        })
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.menu_item_clear -> {
                photoGalleryViewModel.setQuery("")
                true
            } else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerPermissionListener()
        checkPermissionPostNotification()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val workRequest = OneTimeWorkRequest
            .Builder(PollWorker::class.java)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(requireContext())
            .enqueue(workRequest)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        binding.photoGrid.layoutManager = GridLayoutManager(context, 3)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add menu host for create menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoGalleryViewModel.uiState.collect() {state ->
                   binding.photoGrid.adapter = PhotoListAdapter(state.images)
                    checkPermissionPostNotification()
                    binding.progressbar.visibility = View.GONE
                    searchView?.setQuery(state.query, false)
                    if(searchViewBooleanState) {
                        searchItem?.isVisible = true
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        searchView = null
    }

    private fun checkPermissionPostNotification() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun registerPermissionListener() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) {
                Toast.makeText(requireContext(), "Permission is good", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
            }
        }
    }
}