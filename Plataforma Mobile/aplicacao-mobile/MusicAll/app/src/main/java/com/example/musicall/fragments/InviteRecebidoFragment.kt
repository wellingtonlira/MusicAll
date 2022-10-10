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
import com.example.musicall.conexaoApi.modelos.ConviteRecebidoApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InviteRecebidoFragment (convite : ConviteRecebidoApi, preferences: SharedPreferences): Fragment() {

    val convite: ConviteRecebidoApi = convite
    val preferencias: SharedPreferences = preferences
    val box = Toolbox()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val apiMusicall = ConexaoApiMusicall.criar()
        val token = preferencias.getString("token",null).toString()
        var isAceito = convite.aceito
        val tvInfo: TextView = view.findViewById(R.id.tv_info)
        val tvStatus: TextView = view.findViewById(R.id.tv_recebidos_status)
        val tvAceitar: TextView = view.findViewById(R.id.tv_aceitar)
        val tvRecusar: TextView = view.findViewById(R.id.tv_recusar)
        var contexto = activity?.baseContext


        tvInfo.text = String.format("%s - %s - %s - %s | Estilo Musical - %s", convite.nome, convite.instrumento, convite.anos, convite.estado, convite.genero)

        if (isAceito) tvStatus.text = "Status: Aceito" else tvStatus.text = "Status: Pendente"

        tvAceitar.setOnClickListener{
            if (!isAceito){
                apiMusicall.alterarVisibilidadeConvite(convite.idConvite, token).enqueue(object : Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        when{
                            response.code() == 200 -> {
                                tvStatus.text = "Status: Aceito"
                                isAceito = true
                                println("convite aceito")
                                Toast.makeText(contexto, "Convite aceito", Toast.LENGTH_SHORT).show()
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
                                println("idUsuario não corresponde a nenhum usuário")
                            }
                            else -> {
                                println("Falha ao carregar os convites enviados")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        println("Erro ao carregar os convites enviados: ${t.message}")
                    }

                })
            } else {
                println("convite já está aceito")
                tvStatus.text = "Status: Aceito"

                Toast.makeText(contexto, "Convite já está aceito", Toast.LENGTH_SHORT).show()

            }

        }

        tvRecusar.setOnClickListener{
            if (isAceito){
                apiMusicall.alterarVisibilidadeConvite(convite.idConvite, token).enqueue(object : Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        when{
                            response.code() == 200 -> {
                                tvStatus.text = "Status: Pendente"
                                isAceito = false
                                println("convite recusado")
                                Toast.makeText(activity?.baseContext, "Convite recusado", Toast.LENGTH_SHORT).show()

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
                                println("idUsuario não corresponde a nenhum usuário")
                            }
                            else -> {
                                println("Falha ao carregar os convites enviados")
                                Toast.makeText(activity?.baseContext, "Erro 500", Toast.LENGTH_SHORT).show()

                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        println("Erro ao carregar os convites enviados: ${t.message}")
                    }

                })
            } else {
                println("convite já está recusado")
                tvStatus.text = "Status: Recusado"
                Toast.makeText(contexto, "Convite já está recusado", Toast.LENGTH_SHORT).show()


            }
        }
    }

}