package com.example.styleimage.dataBase

import com.example.styleimage.dataClass.DataClassPrice
import com.example.styleimage.Constant.pass
import com.example.styleimage.Constant.url
import com.example.styleimage.Constant.user
import java.sql.DriverManager
import kotlin.concurrent.thread

fun connectPrice() :MutableList<DataClassPrice>{
    val listPrice: MutableList<DataClassPrice> = mutableListOf()
    thread  {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url, user, pass)
            val search = "SELECT * FROM price;"
            val searchQuery = connection.prepareStatement(search)
            val result = searchQuery.executeQuery()
            while (result.next()) {
                val id: Int = result.getInt(1)
                val name: String = result.getString(2)
                val view: String = result.getString(3)
                val price: String = result.getString(4)
                val stringPrice = DataClassPrice(id, name, view, price)
                listPrice.add(stringPrice)
            }
            connection.close()
        } catch (e: Exception) {
            print(e.message)
        }
        }.join()
    return listPrice
}

