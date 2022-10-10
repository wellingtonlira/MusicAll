package com.example.musicall

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Toolbox : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teste_api)
    }

    fun addZeros(ano:Int, mes:Int, dia:Int, back: Boolean): String{
        var mesInt = mes
        var diaInt = dia

        var mesRetorno = "${++mesInt}"
        var diaRetorno = "$diaInt"

        if (mesInt in 1..9) mesRetorno = "0$mesInt"
        if (diaInt in 1..9) diaRetorno = "0$diaInt"

        return if (back) "$ano-$mesRetorno-$diaRetorno" else "$ano/$mesRetorno/$diaRetorno"
    }

    fun toastRelogar(teste: Context?){
        Toast.makeText(teste, "Sessão expirada, logue novamente", Toast.LENGTH_SHORT).show()
    }

}
