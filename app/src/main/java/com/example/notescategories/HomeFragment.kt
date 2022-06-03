package com.example.notescategories

import android.content.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import android.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notescategories.adapter.NoteAdapter
import com.example.notescategories.data.NoteDatabase
import com.example.notescategories.model.Note
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {

    var arrNote = ArrayList<Note>()
    var noteAdapter : NoteAdapter = NoteAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private val onClicked = object : NoteAdapter.OnItemClickListener{
        override fun onClicked(noteId: Int) {


            var fragment : Fragment
            var bundle = Bundle()
            bundle.putInt("noteId",noteId)
            fragment = AddNoteFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.setHasFixedSize(true)

        recycler_view.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        launch {
            context?.let {
                var note = NoteDatabase.getDatabase(it).noteDao().getAllNote()
                noteAdapter.setData(note)
                arrNote = note as ArrayList<Note>
                recycler_view.adapter = noteAdapter
            }
        }

        val swipegesture = object : SwipeGesture(requireContext()){

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when(direction){
                    ItemTouchHelper.LEFT -> {
                        noteAdapter.deleteItem(viewHolder.adapterPosition)
                    }
                }

            }
        }

        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView(recycler_view)

        noteAdapter!!.setOnClickListener(onClicked)

        addBtn.setOnClickListener{
            replaceFragment(AddNoteFragment.newInstance(), true)
        }

        val appSettingPrefs : SharedPreferences = requireActivity().getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit : SharedPreferences.Editor = appSettingPrefs.edit()
        val NightMode : Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if(NightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        switch_btn.setOnClickListener {
            if(NightMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()
            }

        }

        search_view.setOnQueryTextListener( object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                var tempArr = ArrayList<Note>()

                for (arr in arrNote){
                    if (arr.title!!.lowercase(Locale.getDefault()).contains(p0.toString())){
                        tempArr.add(arr)
                    }else if(arr.noteText!!.lowercase(Locale.getDefault()).contains(p0.toString())){

                        tempArr.add(arr)
                    }
                }

                noteAdapter.setData(tempArr)
                noteAdapter.notifyDataSetChanged()
                return true
            }

        })
    }



    fun replaceFragment(fragment: Fragment, istransition:Boolean){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if(istransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }
}