package com.example.musicall

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.conexaoApi.modelos.Dados
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.Period
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    var erro: Boolean = false
    var caledario = Calendar.getInstance()
    val box = Toolbox()
    val apiMusicall = ConexaoApiMusicall.criar()

    lateinit var preferencias: SharedPreferences
    lateinit var token:String
    var idUsuario = -1

    lateinit var data: EditText
    lateinit var dataBack: String
    var ano:Int = -1
    lateinit var genero: Spinner
    lateinit var instrumento: Spinner
    lateinit var estado: Spinner
    lateinit var cidade: EditText
    lateinit var insta: TextView
    lateinit var face: TextView
    lateinit var telefone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_perfil)

        preferencias = getSharedPreferences("AUTENTICACAO", MODE_PRIVATE)
        token = preferencias.getString("token", null).toString()
        idUsuario = preferencias.getInt("idUsuario", -1)

        genero = findViewById(R.id.sp_spinner1)
        instrumento = findViewById(R.id.sp_spinner2)
        estado = findViewById(R.id.sp_spinner3)
        cidade = findViewById(R.id.et_cidade)
        insta = findViewById(R.id.et_instagram)
        face = findViewById(R.id.et_face)
        telefone = findViewById(R.id.et_celular)

        val dataIntent = intent.getStringExtra("data")
        val cidadeIntent = intent.getStringExtra("cidade")
        val instaIntent = intent.getStringExtra("instagram")
        val faceIntent = intent.getStringExtra("facebook")
        val telefoneIntent = intent.getStringExtra("telefone")

        dataBack = dataIntent.toString()
        cidade.text = Editable.Factory.getInstance().newEditable(cidadeIntent)
        insta.text = Editable.Factory.getInstance().newEditable(instaIntent)
        face.text = Editable.Factory.getInstance().newEditable(faceIntent)
        telefone.text = Editable.Factory.getInstance().newEditable(telefoneIntent)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun salvar(view: View) {

        val generoTxt: String = genero.selectedItem.toString()
        val instrumentoTxt: String = instrumento.selectedItem.toString()
        val estadoTxt = estado.selectedItem.toString()
        val cidadeTxt = cidade.text.toString()
        var instaTxt = insta.text.toString()
        var faceTxt = face.text.toString()
        var telefoneTxt = telefone.text.toString()

        if (instaTxt.length < 2) {
            //instaTxt = "Não informado"
            Toast.makeText(baseContext,"insta", Toast.LENGTH_SHORT).show()
        } else if (faceTxt.length < 2) {
            //faceTxt = "Não informado"
            Toast.makeText(baseContext,"face", Toast.LENGTH_SHORT).show()
        } else if (telefoneTxt.length < 2) {
            //telefoneTxt = "Não informado"
            Toast.makeText(baseContext,"telefone", Toast.LENGTH_SHORT).show()
        } else if (generoTxt.equals("Gênero Musical")) {
            Toast.makeText(baseContext,"Escolha um gênero", Toast.LENGTH_SHORT).show()
            erro = true
        } else if (instrumentoTxt.equals("Instrumento")) {
            Toast.makeText(baseContext,"Escolha um instrumento", Toast.LENGTH_SHORT).show()
            erro = true
        } else if (estadoTxt.equals("Estado")) {
            Toast.makeText(applicationContext, "Escolha um estado", Toast.LENGTH_SHORT).show()
            erro = true
        } else if (cidadeTxt.length < 3) {
            cidade.error = "Digite seu cidade"
            erro = true
        } else {
            val dados = Dados(dataBack, estadoTxt.toLowerCase(), cidadeTxt.toLowerCase(), faceTxt.toLowerCase(), instaTxt.toLowerCase(), telefoneTxt.toLowerCase(), instrumentoTxt.toLowerCase(), generoTxt.toLowerCase())
            apiMusicall.alterarDadosUsuario(idUsuario, dados, token).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when {
                        response.code() == 200 -> {
                            Toast.makeText(baseContext, "Dados alterados", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        response.code() == 400 -> {
                            Toast.makeText(baseContext, "Dados inválidos", Toast.LENGTH_SHORT).show()
                        }
                        response.code() == 403 -> {
                            val editor = preferencias.edit()
                            editor.clear()
                            editor.commit()
                            println("token inválido")
                            box.toastRelogar(baseContext)
                            finish()

                        }
                        response.code() == 404 -> {
                            println("idUsuário não corresponde a nenhum usuário")
                        }
                        else -> {

                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(baseContext, "Erro ao atualizar os dados do usuário ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    fun cancelar(view: View) {
        finish()
    }
}
