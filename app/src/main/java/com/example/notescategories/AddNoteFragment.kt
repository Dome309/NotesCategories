package com.example.notescategories

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.codingwithme.notesapp.BaseFragment
import com.example.notescategories.model.Note
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch
import java.util.*


class AddNoteFragment : BaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AddNoteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        addDateTime_et.text = currentDate

        imgback.setOnClickListener{
            addNote()
        }

        imgback.setOnClickListener{
            replaceFragment(HomeFragment.newInstance(), false)
        }
    }

    private fun addNote(){
        if(addTitle_et.text.isNullOrEmpty()){
            Toast.makeText(context, "Note title is required", Toast.LENGTH_SHORT).show()
        }
        if(addCategory_et.text.isNullOrEmpty()){
            Toast.makeText(context, "Note category is required", Toast.LENGTH_SHORT).show()
        }
        if(addNoteText_et.text.isNullOrEmpty()){
            Toast.makeText(context, "Note description is required", Toast.LENGTH_SHORT).show()
        }

        launch {
            var note = Note()
            note.title = addTitle_et.text.toString()
            note.category = addCategory_et.text.toString()
            note.title = addTitle_et.text.toString()

        }
    }

    fun replaceFragment(fragment: Fragment, istransition:Boolean){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if(istransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack(fragment.javaClass.simpleName)
    }
}