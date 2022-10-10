package com.example.musicall.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.musicall.R
import com.example.musicall.Toolbox
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.conexaoApi.modelos.PublicacaoApi
import com.example.musicall.conexaoApi.modelos.PublicacaoUsuarioApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment(preferencias: SharedPreferences) : Fragment() {

    val preferencias =  preferencias
    val box = Toolbox()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var contexto = activity?.baseContext
        val nome = preferencias.getString("nome",null).toString()
        val token = preferencias.getString("token",null).toString()
        val idUsuario = preferencias.getInt("idUsuario", -1)

        val apiMusicall = ConexaoApiMusicall.criar()
        println(idUsuario)
        apiMusicall.getUltimasPublicacoes(token).enqueue(object : Callback<List<PublicacaoApi>>{
            override fun onResponse(call: Call<List<PublicacaoApi>>, response: Response<List<PublicacaoApi>>) {
                when {
                    response.code() == 200 -> {
                        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                        response.body()?.forEach {
                            if (it.idUsuario != idUsuario){
                                val titulo = "${it.nome} - ${it.instrumento} - ${it.idade} - ${it.estado} - ${it.genero}"
                                val fragment = CardPublicacoes(titulo,it.texto,false,it.idUsuario,idUsuario,token)
                                fragmentTransaction?.add(R.id.ll_feed, fragment)
                            }
                        }
                        fragmentTransaction?.commit()
                    }
                    response.code() == 403 -> {
                        val editor = preferencias.edit()
                        editor.clear()
                        editor.commit()
                        println("token inválido")
                        box.toastRelogar(contexto)
                        activity?.finish()
                    }
                    response.code() == 404 -> {
                        println("Usuário não existe")
                    }
                    else -> {
                        println("Falha ao pegar as publicações do usuário")
                        Toast.makeText(contexto,"Erro 500",Toast.LENGTH_SHORT)

                    }
                }
            }

            override fun onFailure(call: Call<List<PublicacaoApi>>, t: Throwable) {
                println("Erro ao pegar as publicações do usuário: ${t.message!!}")
            }

        })
        super.onViewCreated(view, savedInstanceState)
    }
}