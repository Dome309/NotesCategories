package com.example.notescategories.fragments

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.notescategories.R
import com.example.notescategories.data.NoteDatabase
import com.example.notescategories.entity.Note
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class AddNoteFragment : BaseFragment() {

    var currentDate:String? = null
    var noteId = -1
    private var selectedImgPath = ""
    private var webLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteId = requireArguments().getInt("noteId", -1)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    companion object {

        val IMAGE_REQUEST_CODE = 100

        @JvmStatic
        fun newInstance() =
            AddNoteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(noteId != -1){
            launch {
                context?.let {
                    var note = NoteDatabase.getDatabase(it).noteDao().getNote(noteId)

                    addTitle_et.setText(note.title)
                    addCategory_et.setText(note.category)
                    addNoteText_et.setText(note.noteText)

                    if(note.imgPath != ""){
                        galleryimg.setImageBitmap(BitmapFactory.decodeFile(note.imgPath))
                        galleryimg.visibility = View.VISIBLE
                    }else{
                        galleryimg.visibility = View.GONE
                    }

                    if (note.webLink != ""){
                        webLink = note.webLink!!
                        tvWebLink.text = note.webLink
                        layoutWebUrl.visibility = View.VISIBLE
                        etWebLink.setText(note.webLink)
                        imgUrlDelete.visibility = View.VISIBLE
                    }else{
                        imgUrlDelete.visibility = View.GONE
                        layoutWebUrl.visibility = View.GONE
                    }
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            broadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        val sdf = SimpleDateFormat("dd MMMM yyyy hh:mm")
        currentDate = sdf.format(Date())

        addDateTime_et.text = currentDate

        imgdone.setOnClickListener{
            if (noteId != -1){
                updateNote()
            }else{
                addNote()
            }
        }

        imgback.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        imgmore.setOnClickListener {
            var bottomSheetFragment = BottomSheetFragment.newIstance(noteId)
            bottomSheetFragment.show(requireActivity().supportFragmentManager, "Bottom Sheet Fragment")
        }


        btnOk.setOnClickListener {
            if(etWebLink.text.toString().trim().isNotEmpty()){
                checkWebUrl()
            }else{
                Toast.makeText(requireContext(),"Url required",Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            if (noteId != -1){
                tvWebLink.visibility = View.VISIBLE
                layoutWebUrl.visibility = View.GONE
            }else{
                layoutWebUrl.visibility = View.GONE
            }

        }

        galleryimg.setOnClickListener {
            pickImageGallery()
        }

        tvWebLink.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(etWebLink.text.toString()))
            startActivity(intent)
        }

        imgUrlDelete.setOnClickListener {
            webLink = ""
            tvWebLink.visibility = View.GONE
            imgUrlDelete.visibility = View.GONE
            layoutWebUrl.visibility = View.GONE
        }
    }

    private fun addNote(){
        if(addTitle_et.text.isNullOrEmpty()){
            Toast.makeText(context, "Note title is required", Toast.LENGTH_SHORT).show()
        }
        else if(addCategory_et.text.isNullOrEmpty()){
            Toast.makeText(context, "Note category is required", Toast.LENGTH_SHORT).show()
        }
        else if(addNoteText_et.text.isNullOrEmpty()){
            Toast.makeText(context, "Note description is required", Toast.LENGTH_SHORT).show()
        }
        else {
            launch {
                var note = Note()
                note.title = addTitle_et.text.toString()
                note.category = addCategory_et.text.toString()
                note.noteText = addNoteText_et.text.toString()
                note.dateTime = currentDate
                note.imgPath = selectedImgPath
                note.webLink = webLink
                context?.let{
                    NoteDatabase.getDatabase(it).noteDao().addNote(note)
                    addTitle_et.setText("")
                    addCategory_et.setText("")
                    addNoteText_et.setText("")
                    tvWebLink.visibility = View.GONE
                    galleryimg.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun updateNote(){
        launch {

            context?.let {
                var notes = NoteDatabase.getDatabase(it).noteDao().getNote(noteId)

                notes.title = addTitle_et.text.toString()
                notes.category = addCategory_et.text.toString()
                notes.noteText = addNoteText_et.text.toString()
                notes.dateTime = currentDate
                notes.imgPath = selectedImgPath
                notes.webLink = webLink

                NoteDatabase.getDatabase(it).noteDao().updateNote(notes)
                addTitle_et.setText("")
                addCategory_et.setText("")
                addNoteText_et.setText("")
                galleryimg.visibility = View.GONE
                tvWebLink.visibility = View.GONE

                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun deleteNote(){
        launch {
            context?.let{
                NoteDatabase.getDatabase(it).noteDao().deleteCurrentNote(noteId)

                requireActivity().supportFragmentManager.popBackStack()

                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    private fun getPathFromUri(contentUri: Uri) : String? {
        var filePath:String?
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if(cursor == null){
            filePath = contentUri.path
        }else{
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }

        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                var selectedImageUrl = data.data
                if (selectedImageUrl != null){
                    try {
                        var inputStream = requireActivity().contentResolver.openInputStream(selectedImageUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        galleryimg.setImageBitmap(bitmap)
                        galleryimg.visibility = View.VISIBLE

                        selectedImgPath = getPathFromUri(selectedImageUrl)!!

                    }catch (e:Exception){
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {

            var action = p1!!.getStringExtra("action")

            when(action!!){
                "DeleteNote" -> {
                    deleteNote()
                }
                "WebUrl" -> {
                    layoutWebUrl.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkWebUrl(){
        if (Patterns.WEB_URL.matcher(etWebLink.text.toString()).matches()){
            layoutWebUrl.visibility = View.GONE
            etWebLink.isEnabled = false
            webLink = etWebLink.text.toString()
            tvWebLink.visibility = View.VISIBLE
            tvWebLink.text = etWebLink.text.toString()
        }else{
            Toast.makeText(requireContext(),"Url is not valid",Toast.LENGTH_SHORT).show()
        }
    }
}