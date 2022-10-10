package com.example.musicall.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.musicall.R
import com.example.musicall.conexaoApi.modelos.ConviteEnviadoApi


class InviteEnviadoFragment  (convite : ConviteEnviadoApi) : Fragment() {

    val convite: ConviteEnviadoApi = convite

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvTitulo: TextView = view.findViewById(R.id.tv_convite_titulo)
        val tvStatus: TextView = view.findViewById(R.id.tv_convite_status)
        val tvInsta: TextView = view.findViewById(R.id.tv_convite_insta)
        val tvFace: TextView = view.findViewById(R.id.tv_convite_face)
        val tvTelef: TextView = view.findViewById(R.id.tV_convite_telef)

        tvTitulo.text = String.format("%s - %s - %s - %s | Estilo Musical - %s", convite.nome, convite.instrumento, convite.anos, convite.estado, convite.genero)
        if (convite.aceito) {
            tvStatus.text = "Status: Aceito"

            tvInsta.text = "Instagram: ${convite.instagram}"
            tvFace.text = "Facebook: ${convite.facebook}"
            tvTelef.text = "Telefone: ${convite.telefone}"
        } else {
            tvStatus.text = "Status: Pendente"
            tvInsta.text = "Instagram: Pendente"
            tvFace.text = "Facebook: Pendente"
            tvTelef.text = "Telefone: Pendente"
        }
    }
}