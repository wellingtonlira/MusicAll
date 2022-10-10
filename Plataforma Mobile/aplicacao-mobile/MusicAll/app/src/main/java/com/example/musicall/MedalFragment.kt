package com.example.musicall

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class MedalFragment(medalhas: List<Any>) : Fragment() {

    val medalha : List<Any> = medalhas

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}