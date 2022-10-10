package com.example.musicall.fragments

import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.musicall.R
import com.example.musicall.Toolbox
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.conexaoApi.modelos.PublicacaoApi
import com.example.musicall.conexaoApi.modelos.PublicacaoUsuarioApi
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment(preferencias: SharedPreferences) : Fragment() {

    val preferencias =  preferencias
    val box = Toolbox()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var contexto = activity?.baseContext
        val tvNome: TextView = view.findViewById(R.id.name_user)
        val nome = preferencias.getString("nome",null).toString()
        val token = preferencias.getString("token",null).toString()
        val idUsuario = preferencias.getInt("idUsuario", -1)
        tvNome.text = nome

        val apiMusicall = ConexaoApiMusicall.criar()
        apiMusicall.getPublicacoesUsuario(idUsuario, token).enqueue(object : Callback<List<PublicacaoUsuarioApi>>{
            override fun onResponse(call: Call<List<PublicacaoUsuarioApi>>, response: Response<List<PublicacaoUsuarioApi>>) {
                when {
                    response.code() == 200 -> {
                        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()

                        response.body()?.forEach {
                            val fragment = CardPublicacoes(it.nome, it.texto, true, it.idPublicacao, idUsuario, token)
                            fragmentTransaction?.add(R.id.ll_perfil, fragment)
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

            override fun onFailure(call: Call<List<PublicacaoUsuarioApi>>, t: Throwable) {
                println("Erro ao pegar as publicações do usuário: ${t.message!!}")
            }

        })

        super.onViewCreated(view, savedInstanceState)

    }

}