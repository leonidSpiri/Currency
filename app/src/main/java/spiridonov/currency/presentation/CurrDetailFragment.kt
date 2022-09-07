package spiridonov.currency.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import spiridonov.currency.CurrencyApp
import spiridonov.currency.databinding.FragmentCurrDetailBinding
import javax.inject.Inject

class CurrDetailFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel

    private val component by lazy {
        (requireActivity().application as CurrencyApp).component
    }

    private var _binding: FragmentCurrDetailBinding? = null
    private val binding: FragmentCurrDetailBinding
        get() = _binding ?: throw RuntimeException("FragmentCurrDetailBinding is null")

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

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
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.getDetailCurrInfo(currCode).observe(viewLifecycleOwner) {
            binding.currItem = it
            val valueState = it.value.toDouble() - it.previous.toDouble()
            binding.tvPrice.setTextColor(viewModel.getColorByState(valueState))
            binding.tvLogoCoin.setTextColor(viewModel.getColorByState(valueState))
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