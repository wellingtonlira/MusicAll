package com.example.musicall

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.conexaoApi.modelos.Publicacao
import com.example.musicall.conexaoApi.modelos.PublicacaoApi
import com.example.musicall.conexaoApi.modelos.PublicacaoUsuarioApi
import com.example.musicall.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.ContextUtils.getActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var preferencias: SharedPreferences
    lateinit var token: String
    var idUsuario = -1
    val api = ConexaoApiMusicall.criar()
    val box = Toolbox()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferencias = getSharedPreferences("AUTENTICACAO", MODE_PRIVATE)
        token = preferencias.getString("token", null).toString()
        idUsuario = preferencias.getInt("idUsuario", -1)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val feedFragment = FeedFragment(preferencias)
        val profileFragment = ProfileFragment(preferencias)
        val invitesFragment = ManagerInvitesFragment(preferencias)
//        val invitesFragment2 = InvitesFragment2()
        val medalsFragment = MedalsFragment(preferencias)
        val settigsFragment = SettingsFragment()

        makecurrentFragment(feedFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_feed -> makecurrentFragment(feedFragment)
                R.id.ic_profile -> makecurrentFragment(profileFragment)
                R.id.ic_invite -> makecurrentFragment(invitesFragment)
                R.id.ic_medal -> makecurrentFragment(medalsFragment)
                R.id.ic_settings -> makecurrentFragment(settigsFragment)

            }
            true
        }
    }

    fun sair(view: View) {
        enDeslogar()
        finish()
    }

    fun alterarSenha(view: View) {
        startActivity(Intent(baseContext, NewPassword::class.java))
    }



    fun dialogDeletar(view: View) {

        val alert = View.inflate(this@MainActivity, R.layout.activity_dialog_view, null)
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(alert)
        val dialog = builder.create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val botaoCancelar:Button = alert.findViewById(R.id.bt_cancelar)

        botaoCancelar.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun deletar(view: View) {

        api.deletarUsuario(idUsuario, token).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when {
                    response.code() == 200 -> {
                        Toast.makeText(baseContext, "Conta deletada :(", Toast.LENGTH_SHORT).show()
                        println("Conta deletada")
                        sair(view)
                    }
                    response.code() == 403 -> {
                        println("token inválido")
                        sair(view)
                        box.toastRelogar(baseContext)
                        finish()


                    }
                    response.code() == 404 -> {
                        println("idUsuario não corresponde a nenhum usuário")
                    }
                    else -> {
                        println("Falha ao tentar deletar um usuário")
                        Toast.makeText(baseContext, "Erro 500", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Erro ao tetnar deletar um usuário: ${t.message}")
            }

        })
    }

    fun irVerPerfil(view: View) {
        startActivity(Intent(baseContext, InfoUser::class.java))
    }

    private fun makecurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_menu, fragment)
            commit()
        }

    fun enDeslogar() {
        val preferencias = getSharedPreferences("AUTENTICACAO", MODE_PRIVATE)
        val editor = preferencias.edit()
        editor.clear()
        editor.commit()
    }

    fun publicar(view: View) {
        val texto: EditText = findViewById(R.id.et_publicacao)
        val publicacao = Publicacao(texto.text.toString())

        api.fazerPublicacao(publicacao, token, idUsuario).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when {
                    response.code() == 200 -> {
                        //Toast.makeText(baseContext, "Publicação realizada", Toast.LENGTH_SHORT).show()
                        println("200 publicação realizada")
                        limparPublicacoes()
                        texto.text.clear()
                        val apiMusicall = ConexaoApiMusicall.criar()
                        apiMusicall.getPublicacoesUsuario(idUsuario, token).enqueue(object : Callback<List<PublicacaoUsuarioApi>> {
                                override fun onResponse(call: Call<List<PublicacaoUsuarioApi>>,response: Response<List<PublicacaoUsuarioApi>>
                                ) {
                                    when {
                                        response.code() == 200 -> {
                                            println("200 ok")
                                            val fragmentTransaction = supportFragmentManager?.beginTransaction()
                                            response.body()?.forEach {
                                                val fragment = CardPublicacoes(it.nome,it.texto,true,it.idPublicacao,idUsuario,token
                                                )
                                                fragmentTransaction?.add(R.id.ll_perfil, fragment)
                                            }
                                            fragmentTransaction?.commit()
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
                                            println("404 Usuário não existe")
                                        }
                                        else -> {
                                            println("Falha ao pegar as publicações do usuário")
                                            Toast.makeText(baseContext, "Erro 500", Toast.LENGTH_SHORT).show()

                                        }
                                    }
                                }

                                override fun onFailure(call: Call<List<PublicacaoUsuarioApi>>,t: Throwable) {
                                    println("Erro ao pegar as publicações do usuário: ${t.message!!}")
                                }

                            })

                    }
                    response.code() == 400 -> {
                        Toast.makeText(baseContext, "Publicação em branco", Toast.LENGTH_SHORT).show()
                    }
                    response.code() == 403 -> {
                        val editor = preferencias.edit()
                        editor.clear()
                        editor.commit()
                        println("token inválido")
                        box.toastRelogar(baseContext)
                        finish()

                    }
                    else -> {
                        Toast.makeText(baseContext,"Erro 500",Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(baseContext, "Erro ao realizar a publicacao", Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun pesquisar(view: View) {
        println("publicação iniciou")
        val etBusca: EditText = findViewById(R.id.et_busca)
        val txtBusca = etBusca.text.toString()
        val feed: LinearLayout = findViewById(R.id.ll_feed)
        feed.removeAllViews()

        api.getPublicacoesFeed(idUsuario, txtBusca.toLowerCase(), token).enqueue(object : Callback<List<PublicacaoApi>> {
                override fun onResponse(call: Call<List<PublicacaoApi>>,response: Response<List<PublicacaoApi>>) {
                    etBusca.text.clear()
                    when {
                        response.code() == 200 -> {
                            val fragmentTransaction = supportFragmentManager?.beginTransaction()
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
                            box.toastRelogar(baseContext)
                            finish()
                        }
                        response.code() == 404 -> {
                            Toast.makeText(baseContext,"Não foi encontrada publicações! Tente novamente",Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(baseContext,"Erro 500",Toast.LENGTH_SHORT).show()

                        }
                    }
                }

                override fun onFailure(call: Call<List<PublicacaoApi>>, t: Throwable) {
                    Toast.makeText(baseContext, "Erro ao pesquisar", Toast.LENGTH_SHORT).show()
                }

            })

    }

    fun limparPublicacoes(){
        val listaInvites: LinearLayout? = findViewById(R.id.ll_perfil)
        listaInvites?.removeAllViews()
    }

}
