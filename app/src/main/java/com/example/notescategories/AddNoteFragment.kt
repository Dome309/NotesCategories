package com.example.notescategories

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.notescategories.data.NoteDatabase
import com.example.notescategories.model.Note
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class AddNoteFragment : BaseFragment() {

    var currentDate:String? = null
    var noteId = -1
    private var selectedImgPath = ""

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
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
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
            replaceFragment(HomeFragment.newInstance(), true)
        }

        imgphoto.setOnClickListener {
            pickImageGallery()
        }

        imgdelete.setOnClickListener{
            deleteNote()
        }

        imgnewnote.setOnClickListener{
            replaceFragment(newInstance(), true)
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
                context?.let{
                    NoteDatabase.getDatabase(it).noteDao().addNote(note)
                    addTitle_et.setText("")
                    addCategory_et.setText("")
                    addNoteText_et.setText("")
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

                NoteDatabase.getDatabase(it).noteDao().updateNote(notes)
                addTitle_et.setText("")
                addCategory_et.setText("")
                addNoteText_et.setText("")
                galleryimg.visibility = View.VISIBLE

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

    fun replaceFragment(fragment: Fragment, istransition:Boolean){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if(istransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }
}