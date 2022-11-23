package com.example.cocos_demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cocos_demo.databinding.LayoutGameFragmentBinding

class CocosGameFragment : Fragment() {

    private var binding: LayoutGameFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val local = LayoutGameFragmentBinding.inflate(inflater, container, false)
        binding = local
        return local.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            btnEnterGame.setOnClickListener {
                requireActivity().finish()
            }
        }
    }
}