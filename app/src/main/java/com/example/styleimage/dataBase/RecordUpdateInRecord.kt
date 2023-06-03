package com.example.styleimage.dataBase

import com.example.styleimage.Constant.pass
import com.example.styleimage.Constant.url
import com.example.styleimage.Constant.user
import java.sql.DriverManager
import kotlin.concurrent.thread

fun recordUpdateInRecord(id: String, price: String, time:String, date:String){
    thread {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url, user, pass)
            val newPrice: Int = price.toInt()
            val newId: Long = id.toLong()
            val update: Any = if (price != "0" && time != "0" && date != "0"){
                "UPDATE records SET price = $newPrice, time = '$time', date = '$date' WHERE id = $newId;"
            }
            else if (price != "0" && time != "0" && date == "0"){
                "UPDATE records SET price = $newPrice, time = '$time' WHERE id = $newId;"
            }
            else if (price != "0" && time == "0" && date != "0"){
                "UPDATE records SET price = $newPrice, date = '$date' WHERE id = $newId;"
            }
            else if (price != "0" && time == "0" && date == "0"){
                "UPDATE records SET price = $newPrice WHERE id = $newId;"
            }
            else if (price == "0" && time != "0" && date != "0"){
                "UPDATE records SET time = '$time', date = '$date' WHERE id = $newId;"
            }
            else if (price == "0" && time != "0" && date == "0"){
                "UPDATE records SET time = '$time', WHERE id = $newId;"
            }
            else if (price == "0" && time == "0" && date != "0"){
                "UPDATE records SET date = '$date' WHERE id = $newId;"
            }
            else{
                "Ok"
            }


            val queryUpdateAccount = connection.prepareStatement(update.toString())
            queryUpdateAccount.executeUpdate()
            connection.close()
        }
        catch (e: Exception) {
            print(e.message)
        }
    }.join()
}