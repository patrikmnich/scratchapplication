package sk.o2.assignment.scratchapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sk.o2.assignment.scratchapplication.R
import sk.o2.assignment.scratchapplication.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by lazy { (requireActivity() as MainActivity).viewModel }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        Log.d("TAG", "viewModel.cardState.value: ${viewModel.cardState.value}")
        updateCardUI(viewModel.cardState.value!!)
        viewModel.cardState.observe(viewLifecycleOwner) { state ->
            updateCardUI(state)
        }
        viewModel.uid.observe(viewLifecycleOwner) {
            binding.card.uid.text = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonScratch.setOnClickListener {
            findNavController().navigate(R.id.ScratchFragment)
        }
        binding.buttonActivate.setOnClickListener {
            findNavController().navigate(R.id.ActivateFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateCardUI(state: CardState) {
        when(state) {
            CardState.UNSCRATCHED -> {3
                binding.card.overlay.visibility = View.VISIBLE
            }
            CardState.SCRATCHED -> {
                binding.card.overlay.visibility = View.GONE
                binding.card.activeText.visibility = View.GONE
                binding.card.uid.visibility = View.VISIBLE
            }
            CardState.ACTIVE -> {
                binding.card.overlay.visibility = View.GONE
                binding.card.activeText.visibility = View.VISIBLE
                binding.card.uid.visibility = View.GONE
            }
        }
    }
}