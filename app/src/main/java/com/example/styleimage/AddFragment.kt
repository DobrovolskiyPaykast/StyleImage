package com.example.styleimage

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.styleandimage.SupportFunctions.SharedPreference
import com.example.styleimage.Constant.*
import com.example.styleimage.dataBase.connectRecord
import com.example.styleimage.dataBase.connectUser
import com.example.styleimage.dataBase.recordsAddContent
import com.example.styleimage.databinding.FragmentAddBinding
import es.dmoral.toasty.Toasty

import java.text.SimpleDateFormat
import java.util.*


class AddFragment : Fragment() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val binding = FragmentAddBinding.inflate(inflater)

        binding.sEmployeeAdd.isEnabled = false
        val sharedPreference = SharedPreference(requireContext())
        if (sharedPreference.getValueString("phone").toString() != phoneEmployee){
            binding.etPhoneAdd.visibility = View.GONE
            binding.etClientAdd.visibility = View.GONE
            binding.etPriceAdd.visibility = View.GONE
            binding.bTimeAdd.visibility = View.GONE
            binding.bOkAdd.text = "Заказать обратный звонок"

            val constraintLayout: ConstraintLayout = binding.clMainAdd
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(binding.clViewPriceAdd.id,
                ConstraintSet.TOP,binding.clMainAdd.id,
                ConstraintSet.TOP,0)

            constraintSet.connect(binding.bOkAdd.id,
                ConstraintSet.TOP,binding.bDateAdd.id,
                ConstraintSet.BOTTOM,80)
            constraintSet.applyTo(constraintLayout)
        }

        var time = "0"
        binding.bTimeAdd.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.bTimeAdd.text = SimpleDateFormat("HH:mm").format(cal.time)
                time = binding.bTimeAdd.text.toString()
            }
            TimePickerDialog(requireContext(), R.style.TimePickerStyle, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        var date: String = binding.bDateAdd.text.toString()
        binding.bDateAdd.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.bDateAdd.text = SimpleDateFormat("dd.MM.yyyy").format(cal.time)
                date = binding.bDateAdd.text.toString()
            }
            val dialog = DatePickerDialog(requireContext(),R.style.DatePickerStyle, dateSetListener, cal.get(
                Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }

        var employee = "Выбрать мастера"
        var employeeArray = spinnerEmployee
        var employeeArrayAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_style, employeeArray)
        employeeArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        binding.sEmployeeAdd.adapter = employeeArrayAdapter
        binding.sEmployeeAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                employee = parentView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        var viewPrice = "Выбрать вид услуги"
        val viewPriceArray = spinnerPriceView
        val viewPriceArrayAdapterAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_style, viewPriceArray)
        viewPriceArrayAdapterAdapter.setDropDownViewResource(R.layout.spinner_style)
        binding.sViewPriceAdd.adapter = viewPriceArrayAdapterAdapter
        binding.sViewPriceAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                viewPrice = parentView?.getItemAtPosition(position).toString()

                if (viewPrice == "Маникюр" || viewPrice == "Педикюр"){
                    employeeArray = spinnerEmployeeNails
                    employeeArrayAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_style, employeeArray)
                    employeeArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
                    binding.sEmployeeAdd.adapter = employeeArrayAdapter
                    binding.sEmployeeAdd.isEnabled = true
                    binding.bErrorAdd.isEnabled = false
                }
                else if (viewPrice == "Выбрать вид услуги"){
                    binding.sEmployeeAdd.isEnabled = false
                    binding.bErrorAdd.isEnabled = true
                }
                else{
                    employeeArray = spinnerEmployeeParic
                    employeeArrayAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_style, employeeArray)
                    employeeArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
                    binding.sEmployeeAdd.adapter = employeeArrayAdapter
                    binding.sEmployeeAdd.isEnabled = true
                    binding.bErrorAdd.isEnabled = false
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        binding.bErrorAdd.setOnClickListener{
            Toasty.error(requireContext(),"Сначала нужно выбрать вид услуги!", Toast.LENGTH_SHORT).show()
        }


        binding.bOkAdd.setOnClickListener{
            val listRecords = connectRecord()
            val listUser = connectUser()
            if (sharedPreference.getValueString("phone").toString() == phoneEmployee){
                val client = binding.etClientAdd.text.toString()
                val phone = binding.etPhoneAdd.text.toString()
                if (client != "" && phone != "" && date != "Выбрать дату" && employee != "Выбрать мастера" && viewPrice != "Выбрать вид услуги" ){
                    if (phone.length == 11){
                        var check = true
                        for (i in listRecords){
                            if (i.date == date && i.time == time && time != "0" && i.employee == employee){
                                check = false
                            }
                        }
                        if (check){
                            if (binding.etPriceAdd.text.toString() == ""){
                                if (binding.bTimeAdd.text != "Выбрать время"){
                                    recordsAddContent(date, time, client, phone, employee, viewPrice, "0")
                                }
                                else{
                                    recordsAddContent(date, "0", client, phone, employee, viewPrice, "0")
                                }
                            }
                            else{
                                if (binding.bTimeAdd.text != "Выбрать время"){
                                    recordsAddContent(date, time, client, phone, employee, viewPrice, binding.etPriceAdd.text.toString())
                                }
                                else{
                                    recordsAddContent(date, "0", client, phone, employee, viewPrice, binding.etPriceAdd.text.toString())
                                }
                            }
                            Toasty.success(requireContext(),"Запись успешно добавлена!", Toast.LENGTH_SHORT).show()

                        }else Toasty.error(requireContext(),"Данное время занято!", Toast.LENGTH_SHORT).show()
                    }else Toasty.error(requireContext(),"Номер телефона должен содержать 11 цифр!", Toast.LENGTH_SHORT).show()
                }else Toasty.error(requireContext(),"Вы пропустили поле!", Toast.LENGTH_SHORT).show()
            }
            else{ //Client
                var name = ""
                var phone = ""
                for(i in listUser){
                    if(AESEncyption.decrypt(i.phone) == sharedPreference.getValueString("phone").toString()){
                        name = i.name
                        phone = AESEncyption.decrypt(i.phone)
                    }
                }
                if (name != "" && phone != "" && date != "Выбрать дату" && employee != "Выбрать мастера" && viewPrice != "Выбрать вид услуги" ){
                        recordsAddContent(date, "0", name, phone, employee, viewPrice, "0")
                        Toasty.success(requireContext(),"Ожидайте обратного звонка!", Toast.LENGTH_SHORT).show()
                }else Toasty.error(requireContext(),"Вы пропустили поле!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    companion object {
        fun newInstance() = AddFragment()}
}