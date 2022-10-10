package com.example.musicall.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.musicall.R
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.conexaoApi.modelos.MedalhaApi
import com.example.musicall.MedalFragment
import com.example.musicall.Toolbox
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedalsFragment(preferencias: SharedPreferences) : Fragment() {

    val preferencias: SharedPreferences = preferencias
    val box = Toolbox()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medals, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val apiMusicall = ConexaoApiMusicall.criar()
        var contexto = activity?.baseContext
        val idUsuario = preferencias.getInt("idUsuario", -1)
        val token = preferencias.getString("token",null).toString()

        val tvHarmoniaDica: TextView = view.findViewById(R.id.tv_harmonia_dica)
        val ivHarmonia: ImageView = view.findViewById(R.id.iv_harmonia)
        val tvHarmoniaNivel: TextView = view.findViewById(R.id.tv_harmonia_nivel)

        val tvMusicDica: TextView = view.findViewById(R.id.tv_music_dica)
        val ivMusic: ImageView = view.findViewById(R.id.iv_music)
        val tvMusicNivel: TextView = view.findViewById(R.id.tv_music_nivel)

        val tvMelodiaDica: TextView = view.findViewById(R.id.tv_melodia_dica)
        val ivMelodia: ImageView = view.findViewById(R.id.iv_melodia)
        val tvMelodiaNivel: TextView = view.findViewById(R.id.tv_melodia_nivel)

        val tvRitmoDica: TextView = view.findViewById(R.id.tv_ritmo_dica)
        val ivRitmo: ImageView = view.findViewById(R.id.iv_ritmo)
        val tvRitmoNivel: TextView = view.findViewById(R.id.tv_ritmo_nivel)

        fun montarMedalhaMusica (nivel:Int){
            when (nivel) {
                1 -> {
                    tvMusicDica.text = "Troféu pode ser upado!"
                    ivMusic.setImageResource(R.drawable.ini)
                    tvMusicNivel.text = "Iniciante"
                }
                2 -> {
                    tvMusicDica.text = "Troféu pode ser upado!"
                    ivMusic.setImageResource(R.drawable.profi)
                    tvMusicNivel.text = "Profissional"
                }
                3 -> {
                    tvMusicDica.text = "Nível máximo atingido"
                    ivMusic.setImageResource(R.drawable.rock)
                    tvMusicNivel.text = "Rockstar"
                }
            }
        }

        fun montarMedalhaMelodia (nivel:Int) {
            when (nivel) {
                1 -> {
                    tvMelodiaDica.text = "Troféu pode ser upado!"
                    ivMelodia.setImageResource(R.drawable.ini)
                    tvMelodiaNivel.text = "Iniciante"
                }
                2 -> {
                    tvMelodiaDica.text = "Troféu pode ser upado!"
                    ivMelodia.setImageResource(R.drawable.profi)
                    tvMelodiaNivel.text = "Profissional"
                }
                3 -> {
                    tvMelodiaDica.text = "Nível máximo atingido"
                    ivMelodia.setImageResource(R.drawable.rock)
                    tvMelodiaNivel.text = "Rockstar"
                }
            }
        }

        fun montarMedalhaRitmo (nivel:Int){
            when (nivel) {
                1 -> {
                    tvRitmoDica.text = "Troféu pode ser upado!"
                    ivRitmo.setImageResource(R.drawable.ini)
                    tvRitmoNivel.text = "Iniciante"
                }
                2 -> {
                    tvRitmoDica.text = "Troféu pode ser upado!"
                    ivRitmo.setImageResource(R.drawable.profi)
                    tvRitmoNivel.text = "Profissional"
                }
                3 -> {
                    tvRitmoDica.text = "Nível máximo atingido"
                    ivRitmo.setImageResource(R.drawable.rock)
                    tvRitmoNivel.text = "Rockstar"
                }
            }
        }

        apiMusicall.getMedalhasUsuario(idUsuario, token).enqueue(object : Callback<MedalhaApi>{
            override fun onResponse(call: Call<MedalhaApi>, response: Response<MedalhaApi>) {
                when {
                    response.code() == 200 -> {
                        val medalha = response.body()!!

                        if (medalha.regTodasInfos){
                            tvHarmoniaDica.text = "Troféu pode ser upado!"
                            ivHarmonia.setImageResource(R.drawable.ini)
                            tvHarmoniaNivel.text = "Iniciante"
                        } else {
                            tvHarmoniaDica.text = "Nível máximo atingido"
                            ivHarmonia.setImageResource(R.drawable.rock)
                            tvHarmoniaNivel.text = "Rockstar"
                        }

                        montarMedalhaMusica(medalha.regNumPublicacoes)
                        montarMedalhaMelodia(medalha.regDataInicio)
                        montarMedalhaRitmo(medalha.regNumPesquisas)

                    }
                    response.code() == 403 -> {
                        println("token inválido")
                        val editor = preferencias.edit()
                        editor.clear()
                        editor.commit()
                        box.toastRelogar(contexto)
                        activity?.finish()
                    }
                    response.code() == 404 -> {
                        println("idUsuario não corresponde a nenhum usuário")
                    }
                    else -> {
                        println("Falha ao carregar as medalhas do usuário")
                        Toast.makeText(contexto,"Erro 500", Toast.LENGTH_SHORT)

                    }
                }
            }

            override fun onFailure(call: Call<MedalhaApi>, t: Throwable) {
                println("Erro ao carregar as medalhas do usuário: ${t.message}")
            }

        })
   }
}