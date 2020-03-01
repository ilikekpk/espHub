package com.example.esphub

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import kotlin.concurrent.thread


const val BROADCAST_CMD = "plakatinNSD"

class MainActivity : AppCompatActivity() {

    var spinnerData = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                textView.text = "$i"
                val data  = byteArrayOf(i.toByte())
                transmitUdp(data, InetAddress.getByName(spinner.selectedItem.toString().split(" ")[0]), 1025)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
                //Toast.makeText(applicationContext,"start tracking",Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
                //Toast.makeText(applicationContext,"stop tracking",Toast.LENGTH_SHORT).show()
            }
        })


        spinnerData.add("hello")
        button.setOnClickListener{
            Log.e("ccc", spinner.selectedItem.toString())
            refreshService()

        }



        spinner.prompt = "Devices"
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter



        refreshService()
        recieveUdp()

    }




    private fun transmitUdp(data: ByteArray, ip: InetAddress, port: Int){
        thread(start = true) {
            val socket = DatagramSocket()//? = null;
            socket.broadcast = true
            val packet = DatagramPacket(data, data.size, ip, port)
            socket.send(packet)

            socket.close()
        }
    }


    private fun recieveUdp() {
        thread(start = true) {
            val buffer = ByteArray(255)
            val socket = DatagramSocket(1024)//? = null
            while(true){
                socket.broadcast = false
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                val serviceName = String( packet.data, 0, packet.length, Charsets.UTF_8)

                Log.e("aa", serviceName)
                Log.e("bb", packet.length.toString())
                spinnerData.add(packet.address.hostAddress + " (" + serviceName + ")")
                spinnerData.sort()

                //println(String(buffer, 0, length))
            }
        }
    }

    private fun refreshService(){

       // Log.e("ds", InetAddress.)
        transmitUdp(BROADCAST_CMD.toByteArray(), InetAddress.getByName("192.168.1.255"), 1025)


        spinnerData.clear()
        spinnerData.add("192.168.1.255" + " (BROADCAST)")
    }



}
