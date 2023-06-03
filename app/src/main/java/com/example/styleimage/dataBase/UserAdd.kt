package com.example.styleimage.dataBase

import com.example.styleimage.Constant.pass
import com.example.styleimage.Constant.url
import com.example.styleimage.Constant.user
import java.sql.DriverManager
import kotlin.concurrent.thread

fun addUser(name:String, phone:String, mail:String, password:String){
    thread {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url, user, pass)
            val insert ="INSERT INTO klient (id, name, phone, mail, password) VALUES " +
                    "(NULL, '$name', '$phone','$mail', '$password');"
            val queryInsertAccount = connection.prepareStatement(insert)
            queryInsertAccount.execute()
            connection.close()
        }
        catch (e: Exception) {
            print(e.message)
        }
    }.join()
}