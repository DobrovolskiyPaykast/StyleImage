package com.example.styleimage

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.styleandimage.SupportFunctions.SharedPreference
import com.example.styleimage.Constant.checkRecord
import com.example.styleimage.Constant.phoneEmployee
import com.example.styleimage.Constant.spinnerEmployee
import com.example.styleimage.dataBase.connectRecord
import com.example.styleimage.dataBase.recordDelete
import com.example.styleimage.dataBase.recordUpdateInRecord
import com.example.styleimage.dataClass.DataClassRecord
import com.example.styleimage.databinding.FragmentRecordBinding
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*

class RecordFragment : Fragment() {
    @SuppressLint("SimpleDateFormat", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val binding = FragmentRecordBinding.inflate(inflater)

        val sharedPreference = SharedPreference(requireContext())
        if (sharedPreference.getValueString("phone").toString() != phoneEmployee){
            binding.clCheckedRecord.visibility = View.GONE

            val constraintLayout: ConstraintLayout = binding.clMainRecord
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(binding.svRecord.id,
                ConstraintSet.TOP,binding.bDateRecord.id,
                ConstraintSet.BOTTOM,40)
            constraintSet.applyTo(constraintLayout)
        }

        var checkRecordStatus = "Выбрать статус записи"
        var employee = "Выбрать мастера"
        var date = binding.bDateRecord.text.toString()

        val mList = spinnerEmployee
        val mArrayAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_style, mList)
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        binding.sEmployeeRecord.adapter = mArrayAdapter
        binding.sEmployeeRecord.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                employee = parentView?.getItemAtPosition(position).toString()
                val listRecord = connectRecord()
                condition(employee, date, checkRecordStatus, listRecord, binding.tbRecord, binding.bDateRecord, binding.tvTbTimeRecord, binding.tvTbClientRecord, binding.tvTableNotRecord)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        val recordStatusList = checkRecord
        val recordStatusListAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_style, recordStatusList)
        recordStatusListAdapter.setDropDownViewResource(R.layout.spinner_style)
        binding.sCheckedRecord.adapter = recordStatusListAdapter
        binding.sCheckedRecord.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                checkRecordStatus = parentView?.getItemAtPosition(position).toString()
                val listRecord = connectRecord()
                condition(employee, date, checkRecordStatus, listRecord, binding.tbRecord, binding.bDateRecord, binding.tvTbTimeRecord, binding.tvTbClientRecord, binding.tvTableNotRecord)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        binding.ibClearRecord.setOnClickListener{
            binding.bDateRecord.text = "Выбрать дату"
            binding.ibClearRecord.visibility = View.INVISIBLE
            date = binding.bDateRecord.text.toString()
            val listRecord = connectRecord()
            condition(employee, date, checkRecordStatus, listRecord, binding.tbRecord, binding.bDateRecord, binding.tvTbTimeRecord, binding.tvTbClientRecord, binding.tvTableNotRecord)
        }

        binding.bDateRecord.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.bDateRecord.text = SimpleDateFormat("dd.MM.yyyy").format(cal.time)

                date = binding.bDateRecord.text.toString()
                val listRecord = connectRecord()
                condition(employee, date, checkRecordStatus, listRecord, binding.tbRecord, binding.bDateRecord, binding.tvTbTimeRecord, binding.tvTbClientRecord, binding.tvTableNotRecord)
                binding.ibClearRecord.visibility = View.VISIBLE
            }

            val dialog = DatePickerDialog(requireContext(),R.style.DatePickerStyle, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }

        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    fun condition(employee:String, date:String, recordStatus: String, listRecord:MutableList<DataClassRecord>,
                  viewTb: TableLayout, viewB: Button, viewTvC1: TextView, viewTvC2: TextView, viewTvNot: TextView){
        if (viewTb.childCount > 1) {
            viewTb.removeViews(1, viewTb.childCount - 1)
        }
        val sharedPreference = SharedPreference(requireContext())
        val spPhone = sharedPreference.getValueString("phone").toString()

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val current = formatter.format(time)
        val dayNow = current[0].toString() + current[1].toString()
        val monthNow = current[3].toString() + current[4].toString()
        val yearNow = current[8].toString() + current[9].toString()
        var count: Int
        viewTvNot.text = ""
        if (spPhone == phoneEmployee){
            viewTvC2.text = "Клиент"
            if (date == "Выбрать дату")
            {
                viewTvC1.text = "Дата/\nВремя"
                if (employee == "Выбрать мастера"){
                    if (recordStatus == "Выбрать статус записи"){
                        count = 0
                        for (i in listRecord){
                            val day = i.date[0].toString() + i.date[1].toString()
                            val month = i.date[3].toString() + i.date[4].toString()
                            val year = i.date[8].toString() + i.date[9].toString()
                            if (year.toInt() > yearNow.toInt()){
                                addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                count++
                            }
                            else if (year.toInt() == yearNow.toInt()){
                                if (month.toInt() > monthNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (month.toInt() == monthNow.toInt()){
                                    if (day.toInt() >= dayNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                    else if (recordStatus == "Ожидают подтверждения"){
                        count = 0
                        for (i in listRecord){
                            if (i.time == "0"){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                    else{
                        count = 0
                        for (i in listRecord){
                            if (i.time != "0"){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                }
                else{
                    if (recordStatus == "Выбрать статус записи"){
                        count = 0
                        for (i in listRecord){
                            if (i.employee == employee){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                    else if (recordStatus == "Ожидают подтверждения"){
                        count = 0
                        for (i in listRecord){
                            if (i.time == "0" && i.employee == employee){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                    else{
                        count = 0
                        for (i in listRecord){
                            if (i.time != "0" && i.employee == employee){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                }
            }
            else{
                viewTvC1.text = "Время"
                if (employee == "Выбрать мастера"){
                    if (recordStatus == "Выбрать статус записи"){
                        count = 0
                        for (i in listRecord){
                            if(i.date == date){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                    else if (recordStatus == "Ожидают подтверждения"){
                        count = 0
                        for (i in listRecord){
                            if (i.time == "0" && i.date == date){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                    else{
                        count = 0
                        for (i in listRecord){
                            if (i.time != "0" && i.date == date){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                }
                else{
                    if (recordStatus == "Выбрать статус записи"){
                        count = 0
                        for (i in listRecord){
                            if (i.employee == employee && i.date == date){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                    else if (recordStatus == "Ожидают подтверждения"){
                        count = 0
                        for (i in listRecord){
                            if (i.time == "0" && i.employee == employee && i.date == date){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                    else{
                        count = 0
                        for (i in listRecord){
                            if (i.time != "0" && i.employee == employee && i.date == date){
                                val day = i.date[0].toString() + i.date[1].toString()
                                val month = i.date[3].toString() + i.date[4].toString()
                                val year = i.date[8].toString() + i.date[9].toString()
                                if (year.toInt() > yearNow.toInt()){
                                    addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (year.toInt() == yearNow.toInt()){
                                    if (month.toInt() > monthNow.toInt()){
                                        addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                    else if (month.toInt() == monthNow.toInt()){
                                        if (day.toInt() >= dayNow.toInt()){
                                            addRow(i.time, i.date, i.clients, i.viewPrice, i.id, i.price, viewTb, viewB)
                                            count++
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0){
                            viewTvNot.text = "Записей не существует"
                        }
                    }
                }
            }
        }
        else{ //Клиент
            viewTvC2.text = "Мастер"
            if (date == "Выбрать дату")
            {
                viewTvC1.text = "Дата/\nВремя"
                if (employee == "Выбрать мастера"){
                    count = 0
                    for (i in listRecord){
                        if (i.number == spPhone && i.time != "0"){
                            val day = i.date[0].toString() + i.date[1].toString()
                            val month = i.date[3].toString() + i.date[4].toString()
                            val year = i.date[8].toString() + i.date[9].toString()
                            if (year.toInt() > yearNow.toInt()){
                                addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                count++
                            }
                            else if (year.toInt() == yearNow.toInt()){
                                if (month.toInt() > monthNow.toInt()){
                                    addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (month.toInt() == monthNow.toInt()){
                                    if (day.toInt() >= dayNow.toInt()){
                                        addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                }
                            }
                        }
                    }
                    if (count == 0){
                        viewTvNot.text = "Записей не существует"
                    }
                }
                else{
                    count = 0
                    for (i in listRecord){
                        if (i.employee == employee && i.number == spPhone && i.time != "0"){
                            val day = i.date[0].toString() + i.date[1].toString()
                            val month = i.date[3].toString() + i.date[4].toString()
                            val year = i.date[8].toString() + i.date[9].toString()
                            if (year.toInt() > yearNow.toInt()){
                                addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                count++
                            }
                            else if (year.toInt() == yearNow.toInt()){
                                if (month.toInt() > monthNow.toInt()){
                                    addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (month.toInt() == monthNow.toInt()){
                                    if (day.toInt() >= dayNow.toInt()){
                                        count++
                                        addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    }
                                }
                            }
                        }
                    }
                    if (count == 0){
                        viewTvNot.text = "Записей не существует"
                    }
                }
            }
            else{
                viewTvC1.text = "Время"
                if (employee == "Выбрать мастера"){
                    count = 0
                    for (i in listRecord){
                        if (i.date == date && i.number == spPhone && i.time != "0")
                        {
                            val day = i.date[0].toString() + i.date[1].toString()
                            val month = i.date[3].toString() + i.date[4].toString()
                            val year = i.date[8].toString() + i.date[9].toString()
                            if (year.toInt() > yearNow.toInt()){
                                addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                count++
                            }
                            else if (year.toInt() == yearNow.toInt()){
                                if (month.toInt() > monthNow.toInt()){
                                    addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (month.toInt() == monthNow.toInt()){
                                    if (day.toInt() >= dayNow.toInt()){
                                        addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                }
                            }
                        }
                    }
                    if (count == 0){
                        viewTvNot.text = "Записей не существует"
                    }
                }
                else{
                    count = 0
                    for (i in listRecord){
                        if (i.employee == employee && i.date == date && i.number == spPhone && i.time != "0"){
                            val day = i.date[0].toString() + i.date[1].toString()
                            val month = i.date[3].toString() + i.date[4].toString()
                            val year = i.date[8].toString() + i.date[9].toString()
                            if (year.toInt() > yearNow.toInt()){
                                addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                count++
                            }
                            else if (year.toInt() == yearNow.toInt()){
                                if (month.toInt() > monthNow.toInt()){
                                    addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                    count++
                                }
                                else if (month.toInt() == monthNow.toInt()){
                                    if (day.toInt() >= dayNow.toInt()){
                                        addRow(i.time, i.date, i.employee, i.viewPrice, i.id, i.price, viewTb, viewB)
                                        count++
                                    }
                                }
                            }
                        }
                    }
                    if (count == 0){
                        viewTvNot.text = "Записей не существует"
                    }
                }
            }
        }
    }



    @SuppressLint("SetTextI18n")
    fun addRow(column1: String, column11:String, column2: String, column3: String,sid: Int, price: Int, viewTb:TableLayout, viewB: Button) {
        val sharedPreference = SharedPreference(requireContext())

        val tableRow = TableRow(requireContext())
        tableRow.id = View.generateViewId() + 99999999
        tableRow.setBackgroundResource(R.drawable.tablerow)
        tableRow.layoutParams = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        (tableRow.layoutParams as TableLayout.LayoutParams).apply {
            topMargin=6
            bottomMargin=6
        }

        val textView1 = TextView(requireContext())
        textView1.id = View.generateViewId() + 99999
        if(viewB.text.toString() == "Выбрать дату")
        {
            if (column1 == "0"){
                textView1.text = "$column11\nОжидает"
            }
            else{
                textView1.text = "$column11\n$column1"
            }

        }
        else{
            if (column1 == "0"){
                textView1.text = "Ожидает"
            }
            else{
                textView1.text = column1
            }
        }

        textView1.setTextColor(Color.parseColor("#FFFFFF"))
        textView1.setPadding(4,4,4,4)
        textView1.textSize = 15F
        textView1.gravity = Gravity.CENTER
        textView1.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        textView1.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView1.layoutParams as TableRow.LayoutParams).apply {
            weight=6F
            column=0
        }

        val textView2 = TextView(requireContext())
        val list1: List<String> = listOf(*column2.split(" ").toTypedArray())
        var count1 = 0
        for (i in list1){
            if (count1 == 0){
                textView2.text = i
                count1 ++
            }else if (count1 == 1){
                textView2.text = textView2.text.toString() + "\n" + i
                count1 ++
            }
            else {
                if (i.length > 3){
                    textView2.text = textView2.text.toString() + "\n" + i
                }
                else{
                    textView2.text = textView2.text.toString() + " " + i
                    count1 ++
                }
            }
        }
        textView2.setTextColor(Color.parseColor("#FFFFFF"))
        textView2.setPadding(4,4,4,4)
        textView2.textSize = 15F
        textView2.gravity = Gravity.CENTER
        textView2.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        textView2.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView2.layoutParams as TableRow.LayoutParams).apply {
            weight=12F
            column = 1
        }

        val textView3 = TextView(requireContext())
        val column13: String
        if (sharedPreference.getValueString("phone").toString() == phoneEmployee) {
            column13 = if (price == 0) {
                "$column3*"
            } else {
                column3
            }
        }
        else{
            column13 = column3
        }

        val list: List<String> = listOf(*column13.split(" ").toTypedArray())
        var count = 0
        for (i in list){
            if (count == 0){
                textView3.text = i
                count ++
            }else{
                textView3.text = textView3.text.toString() + "\n" + i
            }
        }

        textView3.id = sid
        textView3.setTextColor(Color.parseColor("#FFFFFF"))
        textView3.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        textView3.setPadding(4,4,4,4)
        textView3.textSize = 15F
        textView3.gravity = Gravity.CENTER
        textView3.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView3.layoutParams as TableRow.LayoutParams).apply {
            weight=12F
            column = 2
        }

        if (sharedPreference.getValueString("phone").toString() == phoneEmployee){
            tableRow.setOnClickListener {
                showCustomDialog(tableRow, requireView().findViewById(tableRow.getChildAt(0).id),requireView().findViewById(tableRow.getChildAt(2).id))
            }
        }

        tableRow.addView(textView1)
        tableRow.addView(textView2)
        tableRow.addView(textView3)
        viewTb.addView(tableRow)
    }

    private lateinit var alertDialog: AlertDialog
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun showCustomDialog(tr: TableRow, tvTime: TextView, tvPrice: TextView) {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_record, null)

        val listRecord = connectRecord()
        var phone = ""
        var day = ""
        var month = ""
        var year = ""
        for(i in listRecord){
            if(tvPrice.id.toString() == i.id.toString()){
                phone = i.number
                day = i.date[0].toString() + i.date[1].toString()
                month = i.date[3].toString() + i.date[4].toString()
                year = i.date[8].toString() + i.date[9].toString()
            }
        }


        var dayLast = "0"
        var monthLast = "0"
        var yaerLast = "0"

        val tvbodyClient = dialogView.findViewById<TextView>(R.id.tvbodyClient)
        val tvbodyDate = dialogView.findViewById<TextView>(R.id.tvbodyDate)
        val tvbodyViewPrice = dialogView.findViewById<TextView>(R.id.tvbodyViewPrice)
        val tvbodyPrice = dialogView.findViewById<TextView>(R.id.tvbodyPrice)
        val tvbodyEmployee = dialogView.findViewById<TextView>(R.id.tvbodyEmployee)
        val tvbodyPhone = dialogView.findViewById<TextView>(R.id.tvbodyPhone)

        for (i in listRecord){
            if (phone == i.number){
                val dayNow = i.date[0].toString() + i.date[1].toString()
                val monthNow = i.date[3].toString() + i.date[4].toString()
                val yearNow = i.date[8].toString() + i.date[9].toString()
                var viewPriceStar: String
                var dayYear = ""
                var monthYear = ""
                var newYear = ""

                if (year.toInt() > yearNow.toInt()){ // отсеиваем более новые записи
                    dayYear = dayNow
                    monthYear = monthNow
                    newYear = yearNow
                }
                else if (year.toInt() == yearNow.toInt()){
                    if (month.toInt() > monthNow.toInt()){
                        dayYear = dayNow
                        monthYear = monthNow
                        newYear = yearNow
                    }
                    else if (month.toInt() == monthNow.toInt()){
                        if (day.toInt() >= dayNow.toInt()){
                            dayYear = dayNow
                            monthYear = monthNow
                            newYear = yearNow
                        }
                    }
                }


                if (dayYear != "" && monthYear != "" && newYear != ""){
                    viewPriceStar = if(tvPrice.text.toString().contains("*")){
                        val text = tvPrice.text.toString().dropLast(1)
                        text.replace("\n", " ")
                    } else {
                        val text = tvPrice.text.toString()
                        text.replace("\n", " ")
                    }
                    if (tvPrice.id.toString() != i.id.toString() && i.viewPrice.contains(viewPriceStar)){

                        if (newYear.toInt() > yaerLast.toInt()){
                            dayLast = dayYear
                            monthLast = monthYear
                            yaerLast = newYear
                            tvbodyClient.text = i.clients
                            tvbodyDate.text = ("$dayLast.$monthLast.$yaerLast")
                            tvbodyViewPrice.text = i.viewPrice
                            tvbodyPrice.text = i.price.toString()
                            tvbodyEmployee.text = i.employee
                            tvbodyPhone.text = i.number

                        }
                        else if (newYear.toInt() == yaerLast.toInt()){
                            if (monthYear.toInt() > monthLast.toInt()){
                                dayLast = dayYear
                                monthLast = monthYear
                                yaerLast = newYear

                                tvbodyClient.text = i.clients
                                tvbodyDate.text = ("$dayLast.$monthLast.$yaerLast")
                                tvbodyViewPrice.text = i.viewPrice
                                tvbodyPrice.text = i.price.toString()
                                tvbodyEmployee.text = i.employee
                                tvbodyPhone.text = i.number
                            }
                            else if (monthYear.toInt() == monthLast.toInt()){
                                if (dayYear.toInt() >= dayLast.toInt()){
                                    dayLast = dayYear
                                    monthLast = monthYear
                                    yaerLast = newYear

                                    tvbodyClient.text = i.clients
                                    tvbodyDate.text = ("$dayLast.$monthLast.$yaerLast")
                                    tvbodyViewPrice.text = i.viewPrice
                                    tvbodyPrice.text = i.price.toString()
                                    tvbodyEmployee.text = i.employee
                                    tvbodyPhone.text = i.number
                                }
                            }
                        }
                    }
                }
            }
        }

        var time = "0"
        var date = "0"
        val etAddPrice = dialogView.findViewById<TextView>(R.id.etNewPriceDialogRecord)
        val bAddPrice: Button = dialogView.findViewById(R.id.bAddPrice)
        val bNewPriceDialogRecord: Button = dialogView.findViewById(R.id.bNewPriceDialogRecord)
        val bNewDateDialogRecord: Button = dialogView.findViewById(R.id.bNewDateDialogRecord)
        val bDeleteRecord: Button = dialogView.findViewById(R.id.bDeleteRecord)

        var countClick = 0
        bDeleteRecord.setOnClickListener{
            if (countClick == 0){
                Toasty.info(requireContext(),"Нажмите еще раз для подтверждения", Toast.LENGTH_SHORT).show()
                countClick++
            }
            else{
                recordDelete(tvPrice.id.toString())
                tr.visibility = View.GONE
                Toasty.success(requireContext(),"Запись удалена", Toast.LENGTH_SHORT).show()
            }
        }

        bAddPrice.setOnClickListener {
            if (etAddPrice.text.toString() != "")
            {
                if (time != "0"){
                    if (date != "0"){
                        recordUpdateInRecord(tvPrice.id.toString(), etAddPrice.text.toString(), time, date)
                        val listRecordUpdate = connectRecord()
                        for (i in listRecordUpdate){
                            if (i.id == tvPrice.id){
                                tvPrice.text = i.viewPrice
                                tvTime.text = "${i.date}\n${i.time}"
                            }
                        }
                        Toasty.success(requireContext(),"Данные успешно изменены!", Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                    }
                    else{
                        recordUpdateInRecord(tvPrice.id.toString(), etAddPrice.text.toString(), time, "0")
                        val listRecordUpdate = connectRecord()
                        for (i in listRecordUpdate){
                            if (i.id == tvPrice.id){
                                tvPrice.text = i.viewPrice
                                tvTime.text = "${i.date}\n${i.time}"
                            }
                        }
                        Toasty.success(requireContext(),"Данные успешно изменены!", Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                    }
                }
                else{
                    if (date != "0"){
                        recordUpdateInRecord(tvPrice.id.toString(), etAddPrice.text.toString(), "0", date)
                        val listRecordUpdate = connectRecord()

                        for (i in listRecordUpdate){
                            if (i.id == tvPrice.id){
                                tvPrice.text = i.viewPrice
                            }
                        }
                        Toasty.success(requireContext(),"Данные успешно изменены!", Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                    }
                    else{
                        recordUpdateInRecord(tvPrice.id.toString(), etAddPrice.text.toString(), "0", "0")
                        val listRecordUpdate = connectRecord()

                        for (i in listRecordUpdate){
                            if (i.id == tvPrice.id){
                                tvPrice.text = i.viewPrice
                            }
                        }
                        Toasty.success(requireContext(),"Данные успешно изменены!", Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                    }

                }
            }
            else{
                if (time != "0"){
                    if (date != "0"){
                        recordUpdateInRecord(tvPrice.id.toString(), "0", time, date)
                        val listRecordUpdate = connectRecord()
                        for (i in listRecordUpdate){
                            if (i.id == tvPrice.id){
                                tvTime.text = "${i.date}\n${i.time}"
                            }
                        }
                        Toasty.success(requireContext(),"Данные успешно изменены!", Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                    }
                    else{
                        recordUpdateInRecord(tvPrice.id.toString(), "0", time, "0")
                        val listRecordUpdate = connectRecord()
                        for (i in listRecordUpdate){
                            if (i.id == tvPrice.id){
                                tvTime.text = "${i.date}\n${i.time}"
                            }
                        }
                        Toasty.success(requireContext(),"Данные успешно изменены!", Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                    }

                }
                else{
                    if (date != "0"){
                        recordUpdateInRecord(tvPrice.id.toString(), "0", "0", date)
                        val listRecordUpdate = connectRecord()
                        for (i in listRecordUpdate){
                            if (i.id == tvPrice.id){
                                tvTime.text = "${i.date}\n${i.time}"
                            }
                        }
                        Toasty.success(requireContext(),"Данные успешно изменены!", Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                    }
                    else{
                        Toasty.error(requireContext(),"Необходимо ввести стоимость или время!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setOnDismissListener { }
        dialogBuilder.setView(dialogView)


        bNewPriceDialogRecord.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                bNewPriceDialogRecord.text = SimpleDateFormat("HH:mm").format(cal.time)
                time = bNewPriceDialogRecord.text.toString()
            }
            TimePickerDialog(requireContext(), R.style.TimePickerStyle, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        alertDialog = dialogBuilder.create()
        alertDialog.show()


        bNewDateDialogRecord.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                bNewDateDialogRecord.text = SimpleDateFormat("dd.MM.yyyy").format(cal.time)
                date = bNewDateDialogRecord.text.toString()
            }
            val dialog = DatePickerDialog(requireContext(),R.style.DatePickerStyle, dateSetListener, cal.get(
                Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }
    }



    companion object {
        fun newInstance() = RecordFragment()}
}