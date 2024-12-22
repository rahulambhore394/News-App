package com.rahul.newsnow.Database

import androidx.room.TypeConverter
import javax.xml.transform.Source

class Converters {

    @TypeConverter
    fun fromSource(source: com.rahul.newsnow.Models.Source): String{
        return source.name
    }
    @TypeConverter
    fun toSource(name: String) : com.rahul.newsnow.Models.Source{
        return com.rahul.newsnow.Models.Source(name,name)
    }
}