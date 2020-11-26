package com.example.lab7_2

import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var ed_height:EditText; //宣告變數，繼承EditText
    private lateinit var ed_weight:EditText; //宣告變數，繼承EditText
    private lateinit var btn_boy:RadioButton; //宣告變數，繼承RadioButton
    private lateinit var tv_weight:TextView; //宣告變數，繼承TextView
    private lateinit var tv_bmi:TextView; //宣告變數，繼承TextView
    private lateinit var tv_progress:TextView; //宣告變數，繼承TextView
    private lateinit var ll_progress:LinearLayout; //宣告變數，繼承LinearLayout
    private lateinit var progressBar2:ProgressBar; //宣告變數，繼承ProgressBar
    lateinit var scope:CoroutineScope; //宣告變數,繼承coroutinScope
    //function calculate 初始化數值，執行其他function(mainthread)
    private fun calculate(){
        tv_weight.text = "標準體重\n無"
        tv_bmi.text = "體脂肪\n無"
        progressBar2.progress = 0 //Bar的初始值為0
        ll_progress.visibility = View.VISIBLE //show出來
        //執行其他coroutine的function
        scope.launch {
            wait()
            display()
        }
    }
    //可被擱置的wait function(再開一個thread)
    private suspend fun wait(){
        withContext(Dispatchers.IO){
            var progress = 0
            //判斷進度條
            while (progress <= 100) {
                //執行try，有錯就執行catch
                try {
                    Thread.sleep(50)//mainthread暫停
                    progress++
                    val msg1 = Message()
                    msg1.what = 1
                    update(msg1,progress)//傳達Message跟progress給update
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
    //可被擱置的update function(主要的thread)，進行畫面更新
    private suspend fun update(msg1:Message,progress:Int){
        withContext(Dispatchers.Main){
            //當wait傳達msg為1時,執行進度條更新
            when (msg1.what) {
                1 -> {progressBar2.progress = progress
                    tv_progress.setText("${progress}%")}
            }
        }
    }
    //可被擱置的display function(再開一個thread)，在背景後面跑
    private suspend fun display(){
        withContext(Dispatchers.IO){
            ll_progress.visibility = View.GONE
            val h = Integer.valueOf(ed_height.text.toString())
            val w = Integer.valueOf(ed_weight.text.toString())
            val standWeight: Double
            val bodyFat: Double
            //估算體脂肪跟標準體重
            if (btn_boy.isChecked) {
                standWeight = (h - 80) * 0.7
                bodyFat = (w - 0.88 * standWeight) / w * 100
            } else {
                standWeight = (h - 70) * 0.6
                bodyFat = (w - 0.82 * standWeight) / w * 100
            }
            tv_weight.text = String.format("標準體重\n%.2f", standWeight)
            tv_bmi.text = String.format("體脂肪\n%.2f", bodyFat)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ed_height = findViewById<EditText>(R.id.ed_height)//連接元件
        ed_weight = findViewById<EditText>(R.id.ed_weight)//連接元件
        btn_boy = findViewById<RadioButton>(R.id.btn_boy)//連接元件
        tv_bmi = findViewById<TextView>(R.id.tv_bmi)//連接元件
        tv_weight = findViewById<TextView>(R.id.tv_weight)//連接元件
        tv_progress = findViewById<TextView>(R.id.tv_progress)//連接元件
        ll_progress = findViewById<LinearLayout>(R.id.ll_progress)//連接元件
        progressBar2 = findViewById<ProgressBar>(R.id.progressBar2)//連接元件
        //按鈕觸發時
        findViewById<View>(R.id.btn_calculate).setOnClickListener {
            if (ed_height.length() < 1) {
                Toast.makeText(
                        this@MainActivity,
                        "請輸入身高", Toast.LENGTH_SHORT
                ).show()
            } else if (ed_weight.length() < 1) {
                Toast.makeText(
                        this@MainActivity,
                        "請輸入體重", Toast.LENGTH_SHORT
                ).show()
            } else {
                scope= CoroutineScope(Dispatchers.Main)//scope執行的範圍
                calculate()//執行calculate
            }
        }
    }
}