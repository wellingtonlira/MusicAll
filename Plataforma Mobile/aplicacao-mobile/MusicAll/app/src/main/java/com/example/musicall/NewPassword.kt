package com.example.musicall

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.musicall.conexaoApi.ConexaoApiMusicall
import com.example.musicall.conexaoApi.modelos.UsuarioSenha
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPassword : AppCompatActivity() {
    var erro: Boolean = false
    lateinit var preferencias: SharedPreferences
    lateinit var token:String
    var idUsuario = -1
    val api = ConexaoApiMusicall.criar()
    val box = Toolbox()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        preferencias = getSharedPreferences("AUTENTICACAO", MODE_PRIVATE)
        token = preferencias.getString("token", null).toString()
        idUsuario = preferencias.getInt("idUsuario", -1)
    }

    fun alterar(view: View) {

        val etSenhaAtual: EditText = findViewById(R.id.et_senhaAtual)
        val etNovaSenha: EditText = findViewById(R.id.et_senhaNova)
        val etNovaSenhaConfirmacao: EditText = findViewById(R.id.et_confirmarSenhaNova)
        val usuarioSenha = UsuarioSenha(etNovaSenha.text.toString(), etSenhaAtual.text.toString())
        val etSenhaAtualTxt: String = etSenhaAtual.text.toString()
        val etNovaSenhaTxt: String = etNovaSenha.text.toString()
        val etNovaSenhaConfirmacaoTxt: String = etNovaSenhaConfirmacao.text.toString()

        if(etSenhaAtualTxt.isEmpty()){
            //etSenhaAtual.error = "Digite sua senha atual"
            Toast.makeText(baseContext,"Digite sua senha atual",Toast.LENGTH_SHORT).show()

            erro = true
        }else if (etSenhaAtualTxt == etNovaSenhaTxt) {
            //etNovaSenhaConfirmacao.error = "Senhas não correspodem"
            Toast.makeText(baseContext,"Nova senha não pode ser igual a atual",Toast.LENGTH_SHORT).show()

            erro = true
        } else if(etNovaSenhaTxt.length < 8){
            //etNovaSenha.error = "Senha curta: " + etNovaSenhaTxt.length + " caracteres."
            Toast.makeText(baseContext,"Senha curta: " + etNovaSenhaTxt.length + " caracteres.",Toast.LENGTH_SHORT).show()

            erro = true
        } else if (etNovaSenhaConfirmacaoTxt != etNovaSenhaTxt) {
            //etNovaSenhaConfirmacao.error = "Senhas não correspodem"
            Toast.makeText(baseContext,"Senhas não correspondem",Toast.LENGTH_SHORT).show()

            erro = true
        } else {
            api.alterarSenha(idUsuario, usuarioSenha, token).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                    when{
                        response.code() == 200 -> {
                            println("senha alterada")
                            Toast.makeText(baseContext,"Senha alterada com sucesso",Toast.LENGTH_SHORT).show()
                            finish()

                        }
                        response.code() == 400 -> {
                            println("iUsuario não corresponde a nenhum usuário ou senha errada")
                            Toast.makeText(baseContext,"Senha atual incorreta",Toast.LENGTH_SHORT).show()


                        }
                        response.code() == 403 -> {
                            println("token invalido")
                            val editor = preferencias.edit()
                            editor.clear()
                            editor.commit()
                            finish()
                            box.toastRelogar(baseContext)
                        }
                        else -> {
                            println("falha ao tentar alterar a senha do usuário")
                            Toast.makeText(baseContext,"Erro 500", Toast.LENGTH_SHORT).show()

                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    println("Erro ao tentar alterar a senha do usuário: ${t.message}")
                }

            })
        }



    }

}