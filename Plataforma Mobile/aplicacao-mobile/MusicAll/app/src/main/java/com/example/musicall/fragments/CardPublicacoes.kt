package com.example.musicall.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicall.Toolbox
import com.example.musicall.R
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.conexaoApi.modelos.PublicacaoUsuarioApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CardPublicacoes(txtTitulo: String, txtConteudo: String, boolProprio: Boolean, idCard: Int, idUsuario: Int, token: String) : Fragment() {

    lateinit var preferencias: SharedPreferences
    val titulo:String = txtTitulo
    val conteudo:String = txtConteudo
    val proprio: Boolean = boolProprio
    val idCard:Int = idCard
    val idUsuario:Int = idUsuario
    val token:String = token
    val box = Toolbox()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card_publicacao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apiMusicall = ConexaoApiMusicall.criar()
        val tvTitulo: TextView= view.findViewById(R.id.publicacao_titulo)
        val tvTexto: TextView= view.findViewById(R.id.publicacao_texto)
        val ibLixo: ImageButton = view.findViewById(R.id.ib_excluir)
        val ibInvite: ImageButton = view.findViewById(R.id.ib_invite)
        var contexto = activity?.baseContext

        ibLixo.setOnClickListener{
            apiMusicall.deletarPublicacao(idCard, token).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when {
                        response.code() == 200 -> {
                            println("publicacao deletada")
                            //Toast.makeText(contexto, "Publicação deletada", Toast.LENGTH_SHORT).show()
                            autoRemover()
                        }
                        response.code() == 400 -> {
                            println("Id da publicação não corresponde a nenhuma publicação")
                        }
                        response.code() == 403 -> {
                            val editor = preferencias.edit()
                            editor.clear()
                            editor.commit()
                            println("token inválido")
                            box.toastRelogar(contexto)
                            activity?.finish()
                        }
                        else -> {
                            println("Falha ao tentar excluir uma publicação")
                            Toast.makeText(contexto, "Erro 500", Toast.LENGTH_SHORT).show()

                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    println("Erro ao tentar excluir uma publicação: ${t.message}")
                }

            })
        }

        ibInvite.setOnClickListener {
            apiMusicall.enviarConvite(idUsuario, idCard, token).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when{
                        response.code() == 200 -> {
                            println("Convite enviado!")
                            Toast.makeText(contexto, "Convite enviado!", Toast.LENGTH_SHORT).show()

                        }
                        response.code() == 400 -> {
                            println("Um convite já foi enviado")
                            Toast.makeText(contexto, "Um convite já foi enviado", Toast.LENGTH_SHORT).show()

                        }
                        response.code() == 403 -> {
                            val editor = preferencias.edit()
                            editor.clear()
                            editor.commit()
                            println("token inválido")
                            box.toastRelogar(contexto)
                            activity?.finish()
                        }
                        else -> {
                            println("Falha ao tentar enviar um convite")
                            Toast.makeText(contexto, "Erro 500", Toast.LENGTH_SHORT).show()

                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    println("Erro ao tentar enviar convite: ${t.message}")
                }

            })
        }
        tvTitulo.text = titulo
        tvTexto.text = conteudo
        if (proprio){
            ibLixo.visibility = View.VISIBLE
            ibInvite.visibility = View.INVISIBLE
        } else{
            ibLixo.visibility = View.INVISIBLE
            ibInvite.visibility = View.VISIBLE
        }
    }

    fun autoRemover() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

}