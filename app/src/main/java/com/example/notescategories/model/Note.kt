package com.example.notescategories.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note_table")
class Note:Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name="title")
    var title:String? = null

    @ColumnInfo(name="date_time")
    var dateTime:String? = null

    @ColumnInfo(name="category")
    var category:String? = null

    @ColumnInfo(name="note_text")
    var noteText:String? = null

    @ColumnInfo(name="img_path")
    var imgPath:String? = null

    @ColumnInfo(name="web_link")
    var webLink:String? = null

    override fun toString(): String {
        return "$title : $dateTime"
    }
}