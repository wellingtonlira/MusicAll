package com.example.musicall

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.fragments.ManagerInvitesFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home : AppCompatActivity() {
    lateinit var preferencias: SharedPreferences
    lateinit var cadastro: Intent
    lateinit var feed: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun irLogin(view: View) {
        startActivity(Intent(baseContext, Login::class.java))
    }


    fun irRegistrar(view: View) {
        startActivity(Intent(baseContext, ScreenRegistrar::class.java))
    }

    fun ajuda(view: View) {
        startActivity(Intent(baseContext, AboutMusicall::class.java))
    }


}