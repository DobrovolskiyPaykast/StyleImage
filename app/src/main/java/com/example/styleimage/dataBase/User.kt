package com.example.styleimage.dataBase

import com.example.styleimage.Constant.pass
import com.example.styleimage.Constant.url
import com.example.styleimage.Constant.user
import com.example.styleimage.dataClass.DataClassUser
import java.sql.DriverManager
import kotlin.concurrent.thread

fun connectUser() :MutableList<DataClassUser>{
    val listClient: MutableList<DataClassUser> = mutableListOf()
    thread {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url, user, pass)
            val search = "SELECT * FROM klient;"
            val searchQuery = connection.prepareStatement(search)
            val result = searchQuery.executeQuery()

            while (result.next()) {
                val id: Int = result.getInt(1)
                val name: String = result.getString(2)
                val phone: String = result.getString(3)
                val mail: String = result.getString(4)
                val password: String = result.getString(5)
                val stringClient = DataClassUser(id, name, phone, mail, password)
                listClient.add(stringClient)
            }
            connection.close()
        } catch (e: Exception) {
            print(e.message)
        }
    }.join()
    return listClient
}

