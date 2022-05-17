package com.example.notescategories.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.notescategories.R
import com.example.notescategories.model.Note
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter() :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var listener: OnItemClickListener? = null
    var arrList = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteAdapter.NoteViewHolder, position: Int) {
        holder.itemView.note_title_card.text = arrList[position].title
        holder.itemView.date_time_card.text = arrList[position].dateTime
        holder.itemView.note_text_card.text = arrList[position].noteText

        holder.itemView.cardView.setOnClickListener{
            listener!!.onClicked(arrList[position].id!!)
        }
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    fun setData(arrNotesList: List<Note>){
        arrList = arrNotesList as ArrayList<Note>
    }

    fun setOnClickListener(listener1:OnItemClickListener){
        listener = listener1
    }

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

    interface OnItemClickListener{
        fun onClicked(noteId:Int)
    }

}