package com.example.styleimage.dataBase

import com.example.styleimage.Constant.pass
import com.example.styleimage.Constant.url
import com.example.styleimage.Constant.user
import java.sql.DriverManager
import kotlin.concurrent.thread

fun recordUpdate(id: String, price: String){
    thread {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url, user, pass)
            val newPrice: Int = price.toInt()
            val newId: Long = id.toLong()
            val update ="UPDATE records SET price = $newPrice WHERE id = $newId;"
            val queryUpdateAccount = connection.prepareStatement(update)
            queryUpdateAccount.executeUpdate()
            connection.close()
        }
        catch (e: Exception) {
            print(e.message)
        }
    }.join()
}