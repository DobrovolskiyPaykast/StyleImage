package com.example.styleimage

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.example.styleimage.Constant.spinnerPriceView
import com.example.styleimage.dataBase.connectPrice
import com.example.styleimage.databinding.FragmentPriceBinding

class PriceFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPriceBinding.inflate(inflater)

        val listPrice = connectPrice()

        val arrayAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_style, spinnerPriceView)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        binding.sTypePrice.adapter = arrayAdapter
        binding.sTypePrice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                if (binding.tbPrice.childCount > 1) {
                    binding.tbPrice.removeViews(1, binding.tbPrice.childCount - 1)
                }
                if (parentView?.getItemAtPosition(position).toString() == "Выбрать вид услуги"){
                    for (i in listPrice) {
                        addRow("${i.name}\n(${i.view})", i.price, binding.tbPrice)
                    }
                } else {
                    for (i in listPrice) {
                        if (i.view == parentView?.getItemAtPosition(position).toString()){
                            addRow(i.name, i.price, binding.tbPrice)
                        }
                    }
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
                return
            }
        }
        return binding.root
    }

    fun addRow(name:String, price:String, view: TableLayout){
        val tableRow = TableRow(activity)
        tableRow.setBackgroundResource(R.drawable.tablerow)
        tableRow.layoutParams = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        (tableRow.layoutParams as TableLayout.LayoutParams).apply {
            topMargin=6
            bottomMargin=6
        }

        val textView1 = TextView(activity)
        textView1.text = name
        textView1.setTextColor(Color.parseColor("#FFFFFF"))
        textView1.setBackgroundColor(Color.parseColor("#9c78c3"))
        textView1.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        textView1.setPadding(4, 4, 4, 4)
        textView1.textSize = 15F
        textView1.gravity = Gravity.CENTER
        textView1.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT)
        (textView1.layoutParams as TableRow.LayoutParams).apply {
            weight = 8F
            column = 0
        }

        val textView2 = TextView(activity)
        textView2.text = price
        textView2.setTextColor(Color.parseColor("#FFFFFF"))
        textView2.setBackgroundColor(Color.parseColor("#9c78c3"))
        textView2.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        textView2.setPadding(4, 4, 4, 4)
        textView2.textSize = 15F
        textView2.gravity = Gravity.CENTER
        textView2.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT)
        (textView2.layoutParams as TableRow.LayoutParams).apply {
            weight = 4F
            column = 1
        }
        tableRow.addView(textView1)
        tableRow.addView(textView2)
        view.addView(tableRow)
    }

    companion object {
        fun newInstance() = PriceFragment()}
}