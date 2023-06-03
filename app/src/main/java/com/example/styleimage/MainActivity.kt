package com.example.styleimage

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.styleandimage.SupportFunctions.SharedPreference
import com.example.styleimage.Constant.checkSA
import com.example.styleimage.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ibExit.layoutParams = TableRow.LayoutParams(Resources.getSystem().displayMetrics.widthPixels / 6, Resources.getSystem().displayMetrics.widthPixels / 6)
        binding.ibMe.layoutParams = TableRow.LayoutParams(Resources.getSystem().displayMetrics.widthPixels / 6, Resources.getSystem().displayMetrics.widthPixels / 6)
        binding.tbBottom.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, Resources.getSystem().displayMetrics.widthPixels / 6)

        val constraintLayout: ConstraintLayout = binding.main
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(binding.tbBottom.id,
            ConstraintSet.BOTTOM,binding.main.id,
            ConstraintSet.BOTTOM,0)
        constraintSet.applyTo(constraintLayout)

        record()

        binding.ivRecord.setOnClickListener{
            record()
            binding.tvHead.text = "ЗАПИСИ"
        }

        binding.ivAdd.setOnClickListener{
            add()
            binding.tvHead.text = "ДОБАВИТЬ"
        }

        binding.ivHistory.setOnClickListener{
            history()
            binding.tvHead.text = "ИСТОРИЯ"
        }

        binding.ivPrice.setOnClickListener{
            price()
            binding.tvHead.text = "ЦЕНЫ"
        }

        binding.ibExit.setOnClickListener{
            checkSA = false
            val sharedPreference = SharedPreference(this)
            sharedPreference.clearSharedPreference()
            startActivity(Intent(this, AutoRegisrActivity::class.java))
            finish()
        }
    }

    private fun record(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fMain, RecordFragment.newInstance())
            .commit()

        binding.ivRecord.setBackgroundColor(Color.parseColor("#6b5b89"))

        binding.ivAdd.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivHistory.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivPrice.setBackgroundColor(Color.parseColor("#00FFFFFF"))

    }

    private fun add(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fMain, AddFragment.newInstance())
            .commit()

        binding.ivRecord.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivAdd.setBackgroundColor(Color.parseColor("#6b5b89"))

        binding.ivHistory.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivPrice.setBackgroundColor(Color.parseColor("#00FFFFFF"))
    }

    private fun history(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fMain, HistoryFragment.newInstance())
            .commit()

        binding.ivRecord.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivAdd.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivHistory.setBackgroundColor(Color.parseColor("#6b5b89"))

        binding.ivPrice.setBackgroundColor(Color.parseColor("#00FFFFFF"))
    }

    private fun price(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fMain, PriceFragment.newInstance())
            .commit()

        binding.ivRecord.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivAdd.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivHistory.setBackgroundColor(Color.parseColor("#00FFFFFF"))

        binding.ivPrice.setBackgroundColor(Color.parseColor("#6b5b89"))
    }
}