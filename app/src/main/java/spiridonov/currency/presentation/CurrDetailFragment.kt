package spiridonov.currency.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import spiridonov.currency.databinding.FragmentCurrDetailBinding

private lateinit var viewModel: MainViewModel

private var _binding: FragmentCurrDetailBinding? = null
val binding get() = _binding ?: throw RuntimeException("FragmentCurrDetailBinding is null")

class CurrDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currCode = parseArguments()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getDetailCurrInfo(currCode).observe(viewLifecycleOwner) {
            println(it)
        }
    }


    private fun parseArguments() = requireArguments().getString(ARG_PARAM_ITEM) ?: EMPTY_STRING


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ARG_PARAM_ITEM = "curr_code"
        private const val EMPTY_STRING = ""
        fun newInstance(currCode: String) =
            CurrDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_ITEM, currCode)
                }
            }
    }
}