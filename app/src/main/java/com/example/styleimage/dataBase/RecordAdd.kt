package com.example.styleimage.dataBase

import com.example.styleimage.Constant.pass
import com.example.styleimage.Constant.url
import com.example.styleimage.Constant.user
import java.sql.DriverManager
import kotlin.concurrent.thread

fun recordsAddContent(date: String, time: String, client: String, number: String, employee: String, viewPrice: String, price: String){
    thread {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url, user, pass)
            val newPrice: Int = price.toInt()
            val numberToLong = number.toLong()
            val insert ="INSERT INTO records (id, date, time, client, number, employee, viewPrice, price) VALUES " +
                    "(NULL, '$date', '$time', '$client', '$numberToLong', '$employee','$viewPrice', $newPrice);"
            val queryInsertAccount = connection.prepareStatement(insert)
            queryInsertAccount.execute()
            connection.close()
        }
        catch (e: Exception) {
            print(e.message)
        }
    }.join()
}