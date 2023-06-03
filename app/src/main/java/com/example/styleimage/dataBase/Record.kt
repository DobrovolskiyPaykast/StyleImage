package com.example.styleimage.dataBase

import com.example.styleimage.dataClass.DataClassRecord
import com.example.styleimage.Constant.pass
import com.example.styleimage.Constant.url
import com.example.styleimage.Constant.user
import java.sql.DriverManager
import kotlin.concurrent.thread

fun connectRecord() :MutableList<DataClassRecord>{
    val listRegistr: MutableList<DataClassRecord> = mutableListOf()
    thread {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url, user, pass)
            val search = "SELECT * FROM records;"
            val searchQuery = connection.prepareStatement(search)
            val result = searchQuery.executeQuery()

            while (result.next()) {
                val id: Int = result.getInt(1)
                val date: String = result.getString(2)
                val time: String = result.getString(3)
                val client: String = result.getString(4)
                val number: Long = result.getLong(5)
                val employee: String = result.getString(6)
                val viewPrice: String = result.getString(7)
                val price: Int = result.getInt(8)
                val stringRegistr = DataClassRecord(id, date, time, client, number.toString(), employee, viewPrice, price)
                listRegistr.add(stringRegistr)
            }
            connection.close()
        } catch (e: Exception) {
            print(e.message)
        }
    }.join()
    return listRegistr
}

