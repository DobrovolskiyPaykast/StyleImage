package com.example.styleimage

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.example.styleimage.Constant.phoneEmployee
import com.example.styleimage.Constant.spinnerPriceView
import com.example.styleimage.dataBase.connectRecord
import com.example.styleimage.dataBase.recordUpdate
import com.example.styleimage.dataClass.DataClassRecord
import com.example.styleimage.databinding.FragmentHistoryBinding
import es.dmoral.toasty.Toasty

import java.text.SimpleDateFormat
import java.util.*


class HistoryFragment : Fragment() {
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHistoryBinding.inflate(inflater)

        val listRecord = connectRecord()

        val sharedPreference = SharedPreference(requireContext())
        if (sharedPreference.getValueString("phone").toString() != phoneEmployee){
            binding.etDataHistory.visibility = View.INVISIBLE
            binding.bOkHistory.visibility = View.INVISIBLE
            binding.tvHistory.visibility = View.INVISIBLE
            binding.tvClientHistory.visibility = View.INVISIBLE

            binding.clViewPriceHistory.visibility = View.VISIBLE
            binding.bDateHistory.visibility = View.VISIBLE

            binding.tvTbPriceHistory.text = "Мастер"

            val constraintLayout: ConstraintLayout = binding.clMainHistory
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(binding.clTbHistory.id,ConstraintSet.TOP,binding.bDateHistory.id,ConstraintSet.BOTTOM,0)
            constraintSet.applyTo(constraintLayout)
        }
        else{
            binding.tvTableNotHistory.text = "Введите данные клиента"
        }


        var viewPrice = "Выбрать вид услуги"
        val viewPriceArray = spinnerPriceView
        val viewPriceArrayAdapterAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_style, viewPriceArray)
        viewPriceArrayAdapterAdapter.setDropDownViewResource(R.layout.spinner_style)
        binding.sViewPriceHistory.adapter = viewPriceArrayAdapterAdapter
        binding.sViewPriceHistory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?,selectedItemView: View?,position: Int,id: Long) {
                viewPrice = parentView?.getItemAtPosition(position).toString()
                condition(viewPrice, binding.bDateHistory.text.toString(), listRecord, binding.tbHistory, binding.tvTableNotHistory)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        binding.ibClearHistory.setOnClickListener{
            binding.bDateHistory.text = "Выбрать дату"
            binding.ibClearHistory.visibility = View.INVISIBLE
            condition(viewPrice, binding.bDateHistory.text.toString(), listRecord, binding.tbHistory, binding.tvTableNotHistory)
        }

        binding.bDateHistory.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.bDateHistory.text = SimpleDateFormat("dd.MM.yyyy").format(cal.time)

                condition(viewPrice, binding.bDateHistory.text.toString(), listRecord, binding.tbHistory, binding.tvTableNotHistory)
                binding.ibClearHistory.visibility = View.VISIBLE
            }

            val dialog = DatePickerDialog(requireContext(),
                R.style.DatePickerStyle, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.show()
        }


        binding.bOkHistory.setOnClickListener{
            var count = 0
            binding.tvTableNotHistory.text = ""
            var sorted = ""
            val client = binding.etDataHistory.text.toString()
            if (client != ""){
                sorted = if (client[0] == '8') {
                    "phone"
                } else {
                    "client"
                }
            }
            if (binding.tbHistory.childCount > 1) {
                binding.tbHistory.removeViews(1, binding.tbHistory.childCount - 1)
            }
            binding.tvClientHistory.text = ""

            val clientName = mutableListOf<String>()
            val clientPhone = mutableListOf<String>()
            if (binding.etDataHistory.text.toString() != "") {
                for (i in listRecord) {
                    if (sorted == "phone" && i.number.contains(binding.etDataHistory.text.toString()) && i.time != "0") {
                        if (!clientPhone.contains(i.number) && !clientName.contains(i.clients)) {
                            if (binding.tvClientHistory.text != "") {
                                binding.tvClientHistory.text = "${binding.tvClientHistory.text} \n ${i.clients} (${i.number})"
                            } else {
                                binding.tvClientHistory.text = "${binding.tvClientHistory.text} ${i.clients} (${i.number})"
                            }
                            clientName.add(i.clients)
                            clientPhone.add(i.number)
                        }
                        addRow(i.date, i.viewPrice, i.price.toString(),i.id, binding.tbHistory)
                        count++
                    } else if (sorted == "client" && i.clients.contains(binding.etDataHistory.text.toString()) && i.time != "0") {
                        if (!clientPhone.contains(i.number) && !clientName.contains(i.clients)) {
                            if (binding.tvClientHistory.text != "") {
                                binding.tvClientHistory.text = "${binding.tvClientHistory.text} \n ${i.clients} (${i.number})"
                            } else {
                                binding.tvClientHistory.text = "${binding.tvClientHistory.text} ${i.clients} (${i.number})"
                            }
                            clientName.add(i.clients)
                            clientPhone.add(i.number)
                        }
                        addRow(i.date, i.viewPrice, i.price.toString(),i.id, binding.tbHistory)
                        count++
                    }
                }
                if (count == 0){
                    binding.tvTableNotHistory.text = "Записей не существует"
                }
            }
            else Toasty.error(requireContext(),"Необходимо заполнить поле!", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    fun condition(viewPrice:String, date:String, listRecord:MutableList<DataClassRecord>, viewTb: TableLayout, viewTvNot: TextView){
        if (viewTb.childCount > 1) {
            viewTb.removeViews(1, viewTb.childCount - 1)
        }
        val sharedPreference = SharedPreference(requireContext())
        viewTvNot.text = ""
        var count: Int
        if (date == "Выбрать дату")
        {
            if (viewPrice == "Выбрать вид услуги"){
                count = 0
                for (i in listRecord){
                    if(i.number == sharedPreference.getValueString("phone").toString() && i.time != "0"){
                        addRow(i.date, i.viewPrice, i.employee,i.id, viewTb)
                        count++
                    }
                }
                if (count == 0){
                    viewTvNot.text = "Записей не существует"
                }
            }
            else{
                count = 0
                for (i in listRecord){
                    if(i.number == sharedPreference.getValueString("phone").toString() && i.viewPrice == viewPrice && i.time != "0"){
                        addRow(i.date, i.viewPrice, i.employee,i.id, viewTb)
                        count++
                    }
                }
                if (count == 0){
                    viewTvNot.text = "Записей не существует"
                }
            }
        }
        else{ //ДАТА ВЫБРАНА
            if (viewPrice == "Выбрать вид услуги"){
                count = 0
                for (i in listRecord){
                    if(i.number == sharedPreference.getValueString("phone").toString() && i.date == date && i.time != "0"){
                        addRow(i.date, i.viewPrice, i.employee, i.id, viewTb)
                        count++
                    }
                }
                if (count == 0){
                    viewTvNot.text = "Записей не существует"
                }
            }
            else{
                count = 0
                for (i in listRecord){
                    if(i.number == sharedPreference.getValueString("phone").toString() && i.viewPrice == viewPrice && i.date == date && i.time != "0"){
                        addRow(i.date, i.viewPrice, i.employee, i.id, viewTb)
                        count++
                    }
                }
                if (count == 0){
                    viewTvNot.text = "Записей не существует"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addRow(date: String, service: String, price: String, sid:Int, view:TableLayout) {
        val sharedPreference = SharedPreference(requireContext())
        val tableRow = TableRow(requireContext())
        tableRow.setBackgroundResource(R.drawable.tablerow)
        tableRow.layoutParams = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        (tableRow.layoutParams as TableLayout.LayoutParams).apply {
            topMargin=6
            bottomMargin=6
        }

        val textView1 = TextView(requireContext())
        textView1.text = date
        textView1.setTextColor(Color.parseColor("#FFFFFF"))
        textView1.setBackgroundColor(Color.parseColor("#9c78c3"))
        textView1.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        textView1.setPadding(4,4,4,4)
        textView1.textSize = 15F
        textView1.gravity = Gravity.CENTER
        textView1.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView1.layoutParams as TableRow.LayoutParams).apply {
            weight=1F
            column=0
        }

        val textView2 = TextView(requireContext())
        textView2.id = sid
        val list: List<String> = listOf(*service.split(" ").toTypedArray())
        var count = 0
        for (i in list){
            if (count == 0){
                textView2.text = i
                count ++
            }else{
                textView2.text = textView2.text.toString() + "\n" + i
            }
        }
        //textView2.text = service
        textView2.setTextColor(Color.parseColor("#FFFFFF"))
        textView2.setBackgroundColor(Color.parseColor("#9c78c3"))
        textView2.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        textView2.setPadding(4,4,4,4)
        textView2.textSize = 15F
        textView2.gravity = Gravity.CENTER
        textView2.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView2.layoutParams as TableRow.LayoutParams).apply {
            weight=8F
            column = 1
        }


        val textView3 = TextView(requireContext())
        var newPrice = ""
        if (sharedPreference.getValueString("phone").toString() == phoneEmployee) {
            if (price == "0") {
                newPrice = "$price*"
            } else {
                newPrice = price
            }
        }
        else{
            newPrice = price
        }

        val listPrice: List<String> = listOf(*newPrice.split(" ").toTypedArray())
        var countPrice = 0
        for (i in listPrice){
            if (countPrice == 0){
                textView3.text = i
                countPrice ++
            }else{
                textView3.text = textView3.text.toString() + "\n" + i
            }
        }

        textView3.id = sid + 100000000
        textView3.setTextColor(Color.parseColor("#FFFFFF"))
        textView3.setBackgroundColor(Color.parseColor("#9c78c3"))
        textView3.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        textView3.setPadding(4,4,4,4)
        textView3.textSize = 15F
        textView3.gravity = Gravity.CENTER
        textView3.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView3.layoutParams as TableRow.LayoutParams).apply {
            weight=8F
            column = 2
        }

        if (sharedPreference.getValueString("phone").toString() == phoneEmployee){
            tableRow.setOnClickListener {
                showCustomDialog(requireView().findViewById(tableRow.getChildAt(1).id), requireView().findViewById(tableRow.getChildAt(2).id))
            }
        }

        tableRow.addView(textView1)
        tableRow.addView(textView2)
        tableRow.addView(textView3)
        view.addView(tableRow)
    }

    private lateinit var alertDialog: AlertDialog
    @SuppressLint("SetTextI18n")
    private fun showCustomDialog(tv: TextView, tvPrice: TextView) {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_history, null)

        val listRecord = connectRecord()
        var phone = ""
        var day = ""
        var month = ""
        var year = ""
        for(i in listRecord){
            if(tv.id.toString() == i.id.toString()){
                phone = i.number
                day = i.date[0].toString() + i.date[1].toString()
                month = i.date[3].toString() + i.date[4].toString()
                year = i.date[8].toString() + i.date[9].toString()
            }
        }


        var dayLast = "00"
        var monthLast = "00"
        var yaerLast = "00"

        val tvbodyClient = dialogView.findViewById<TextView>(R.id.tvbodyClientDialogHistoty)
        val tvbodyDate = dialogView.findViewById<TextView>(R.id.tvbodyDateDialogHistoty)
        val tvbodyViewPrice = dialogView.findViewById<TextView>(R.id.tvbodyViewPriceDialogHistoty)
        val tvbodyPrice = dialogView.findViewById<TextView>(R.id.tvbodyPriceDialogHistoty)
        val tvbodyEmployee = dialogView.findViewById<TextView>(R.id.tvbodyEmployeeDialogHistoty)

        for (i in listRecord){
            if (phone == i.number){
                val dayNow = i.date[0].toString() + i.date[1].toString()
                val monthNow = i.date[3].toString() + i.date[4].toString()
                val yearNow = i.date[8].toString() + i.date[9].toString()
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
                    val viewPriceStar = if(tv.text.toString().contains("*")){
                        tv.text.toString().dropLast(1)
                    } else {
                        tv.text.toString()
                    }

                    if (tv.id.toString() != i.id.toString() && i.viewPrice.contains(viewPriceStar)){
                        if (newYear.toInt() > yaerLast.toInt()){

                            dayLast = dayYear
                            monthLast = monthYear
                            yaerLast = newYear
                            tvbodyClient.text = i.clients
                            tvbodyDate.text = ("$dayLast.$monthLast.$yaerLast")
                            tvbodyViewPrice.text = i.viewPrice
                            tvbodyPrice.text = i.price.toString()
                            tvbodyEmployee.text = i.employee
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
                            }
                            else if (monthYear.toInt() == monthLast.toInt()){
                                if (dayYear.toInt() >= dayLast.toInt() && i.price != 0){
                                    dayLast = dayYear
                                    monthLast = monthYear
                                    yaerLast = newYear

                                    tvbodyClient.text = i.clients
                                    tvbodyDate.text = ("$dayLast.$monthLast.$yaerLast")
                                    tvbodyViewPrice.text = i.viewPrice
                                    tvbodyPrice.text = i.price.toString()
                                    tvbodyEmployee.text = i.employee
                                }
                            }
                        }
                    }
                }
            }
        }


        val etAddPrice = dialogView.findViewById<TextView>(R.id.etAddPriceDialogHistoty)
        val bAddPrice: Button = dialogView.findViewById(R.id.bDialogHistoty)
        bAddPrice.setOnClickListener {
            if (etAddPrice.text.toString() != "")
            {
                recordUpdate(tv.id.toString(), etAddPrice.text.toString())
                tvPrice.text = if(tvPrice.text.toString().contains("*")){
                    etAddPrice.text.toString().dropLast(1)
                } else {
                    etAddPrice.text.toString()
                }
                Toasty.success(requireContext(),"Стоимость успешно добавлена!", Toast.LENGTH_SHORT).show()
                alertDialog.cancel()
            }
            else{
                Toasty.error(requireContext(),"Необходимо ввести новую стоимость!", Toast.LENGTH_SHORT).show()
            }
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setOnDismissListener { }
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    companion object {
        fun newInstance() = HistoryFragment()}
}