package com.example.styleimage.dataBase

import com.example.styleimage.Constant.pass
import com.example.styleimage.Constant.url
import com.example.styleimage.Constant.user
import java.sql.DriverManager
import kotlin.concurrent.thread

fun recordDelete(id: String){
    thread {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url, user, pass)
            val newId: Long = id.toLong()
            val delete ="DELETE FROM records WHERE id = $newId;"
            val queryDeleteAccount = connection.prepareStatement(delete)
            queryDeleteAccount.executeUpdate()
            connection.close()
        }
        catch (e: Exception) {
            print(e.message)
        }
    }.join()
}