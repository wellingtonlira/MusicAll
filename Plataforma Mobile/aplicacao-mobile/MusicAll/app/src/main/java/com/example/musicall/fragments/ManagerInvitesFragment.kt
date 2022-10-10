package com.example.musicall.fragments

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.musicall.R
import com.example.musicall.Toolbox
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.conexaoApi.modelos.ConviteEnviadoApi
import com.example.musicall.conexaoApi.modelos.ConviteRecebidoApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerInvitesFragment(preferencias: SharedPreferences) : Fragment() {

    val preferencias = preferencias
    val token = preferencias.getString("token", null).toString()
    val idUsuario = preferencias.getInt("idUsuario", -1)
    val apiMusicall = ConexaoApiMusicall.criar()
    val box = Toolbox()
    lateinit var tvVazio: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_screeninvites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvEnviados: TextView = view.findViewById(R.id.tv_enviados)
        val tvRecebidos: TextView = view.findViewById(R.id.tv_recebidos)
        tvVazio = view.findViewById(R.id.convite_vazio)

        tvRecebidos.setOnClickListener {
            listarRecebidos(tvEnviados, tvRecebidos)
        }
        tvEnviados.setOnClickListener {
            listarEnviados(tvEnviados, tvRecebidos)
        }

        listarRecebidos(tvEnviados, tvRecebidos)
    }

    fun listarRecebidos(tvEnviados: TextView, tvRecebidos: TextView) {
        tvRecebidos.setTextColor(Color.WHITE)
        tvEnviados.setTextColor(0x5EFFFFFF)

        apiMusicall.getConvitesRecebidos(idUsuario, token)
            .enqueue(object : Callback<List<ConviteRecebidoApi>> {
                override fun onResponse(
                    call: Call<List<ConviteRecebidoApi>>,
                    response: Response<List<ConviteRecebidoApi>>
                ) {
                    limparInvites()
                    when {
                        response.code() == 200 -> {
                            val fragmentTransaction =
                                activity?.supportFragmentManager?.beginTransaction()
                            if (response.body()!!.isEmpty()) {
                                tvVazio.visibility = View.VISIBLE
                            } else {
                                tvVazio.visibility = View.GONE
                                response.body()?.forEach {
                                    val fragment = InviteRecebidoFragment(it, preferencias)
                                    fragmentTransaction?.add(R.id.ll_container_invite, fragment)
                                }
                            }
                            fragmentTransaction?.commit()
                        }
                        response.code() == 403 -> {
                            println("token inválido")
                            val editor = preferencias.edit()
                            editor.clear()
                            editor.commit()
                            box.toastRelogar(activity?.baseContext)
                            activity?.finish()

                        }
                        response.code() == 404 -> {
                            println("idUsuario não corresponde a nenhum usuário")
                        }
                        else -> {
                            Toast.makeText(activity?.baseContext, "Erro 500", Toast.LENGTH_SHORT)
                        }
                    }
                }

                override fun onFailure(call: Call<List<ConviteRecebidoApi>>, t: Throwable) {
                    println("Erro ao carregar os convites recebidos: ${t.message}")
                }

            })
    }

    fun listarEnviados(tvEnviados: TextView, tvRecebidos: TextView) {
        tvEnviados.setTextColor(Color.WHITE)
        tvRecebidos.setTextColor(0x5EFFFFFF)

        apiMusicall.getConvitesEnviados(idUsuario, token)
            .enqueue(object : Callback<List<ConviteEnviadoApi>> {
                override fun onResponse(
                    call: Call<List<ConviteEnviadoApi>>,
                    response: Response<List<ConviteEnviadoApi>>
                ) {
                    limparInvites()
                    when {
                        response.code() == 200 -> {
                            val fragmentTransaction =
                                activity?.supportFragmentManager?.beginTransaction()
                            if (response.body()!!.isEmpty()) {
                                tvVazio.visibility = View.VISIBLE
                            } else {
                                tvVazio.visibility = View.GONE
                                response.body()?.forEach {
                                    val fragment = InviteEnviadoFragment(it)
                                    fragmentTransaction?.add(R.id.ll_container_invite, fragment)
                                }
                            }
                            fragmentTransaction?.commit()
                        }
                        response.code() == 403 -> {
                            println("token inválido")
                            val editor = preferencias.edit()
                            editor.clear()
                            editor.commit()
                            box.toastRelogar(activity?.baseContext)

                        }
                        response.code() == 404 -> {
                            println("idUsuario não corresponde a nenhum usuário")
                        }
                        else -> {
                            println("Falha ao carregar os convites enviados")
                            Toast.makeText(activity?.baseContext, "Erro 500", Toast.LENGTH_SHORT)

                        }
                    }
                }

                override fun onFailure(call: Call<List<ConviteEnviadoApi>>, t: Throwable) {
                    println("Erro ao carregar os convites enviados: ${t.message}")
                }

            })
    }

    fun limparInvites() {
        val listaInvites: LinearLayout? = view?.findViewById(R.id.ll_container_invite)
        listaInvites?.removeAllViews()
    }
}