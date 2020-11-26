package com.example.hw7_1

import android.os.Bundle
import android.os.Message
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var rabprogress:Int=0;//宣告變數，給初始直
    private var turprogress:Int=0;//宣告變數，給初始直
    private lateinit var seekBar:SeekBar;//宣告變數
    private lateinit var seekBar2:SeekBar;//宣告變數
    private lateinit var btn_start:Button;//宣告變數
    lateinit var scope:CoroutineScope; //宣告變數
    //執行coroutine
    private fun runrabit(){
        scope.launch {
            calculate1()
        }
    }
    //兔子
    //可被擱置的function(另開一個thread)
    private suspend fun calculate1(){
        withContext(Dispatchers.IO){
            while (rabprogress <= 100 && turprogress <= 100) {
                try {
                    Thread.sleep(100) //thread暫停
                    rabprogress += (Math.random() * 3).toInt()
                    val msg1 = Message()
                    msg1.what = 1
                    update1(msg1)//傳達message給update
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
    //可被擱置的function(主thread)
    private suspend fun update1(msg1: Message){
        withContext(Dispatchers.Main){
            //當收到的message為2時執行
            when (msg1.what) {
                1 -> seekBar.progress = rabprogress
            }
            //當兔子抵達100%時執行
            if (rabprogress >= 100 && turprogress < 100) {
                Toast.makeText(this@MainActivity, "兔子勝利", Toast.LENGTH_SHORT).show()
                btn_start.isEnabled = true
            }
        }
    }

    private fun runturtle(){
        scope.launch {
            calculate2()
        }
    }
    //烏龜
    //可被擱置的function(另開一個thread)
    private suspend fun calculate2(){
        withContext(Dispatchers.IO){
            while (rabprogress <= 100 && turprogress <= 100) {
                try {
                    Thread.sleep(100) //thread暫停
                    turprogress += (Math.random() * 3).toInt()
                    val msg2 = Message()
                    msg2.what = 2
                    update2(msg2)//傳達message給update2
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
    //可被擱置的function(主thread)
    private suspend fun update2(msg2: Message){
        withContext(Dispatchers.Main){
            //當收到的message為2時執行
            when (msg2.what) {
                2 -> seekBar2.progress = turprogress
            }
            //當烏龜抵達100%時執行
            if (rabprogress < 100 && turprogress >= 100) {
                Toast.makeText(this@MainActivity, "烏龜勝利", Toast.LENGTH_SHORT).show()
                btn_start.isEnabled = true
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        seekBar=findViewById(R.id.seekBar);
        seekBar2=findViewById(R.id.seekBar2);
        btn_start=findViewById(R.id.btn_start);
        //按鈕觸發時
        btn_start.setOnClickListener {
            btn_start.isEnabled=false;
            rabprogress=0;
            turprogress=0;
            seekBar.setProgress(0);//設置初始直
            seekBar2.setProgress(0);//設置初始直
            scope= CoroutineScope(Dispatchers.Main)//執行範圍
            runturtle()
            runrabit()

        }
    }
}