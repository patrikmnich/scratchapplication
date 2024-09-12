package sk.o2.assignment.scratchapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sk.o2.assignment.scratchapplication.databinding.FragmentScratchBinding
import java.util.UUID

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ScratchFragment : Fragment() {

    private var _binding: FragmentScratchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by lazy { (requireActivity() as MainActivity).viewModel }

    private var currentJob: Job? = null
    private var jobRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScratchBinding.inflate(inflater, container, false)

        viewModel.cardState.observe(viewLifecycleOwner) { state ->
            Log.d("Scratch", "New value in cardState: $state")
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
            if (!jobRunning) {
                scratch()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()

        currentJob?.let {
            if (!it.isCompleted) {
                it.cancel()
                Log.d("Scratch", "Job cancelled")
            }
        }
    }

    private fun scratch() {
        if (viewModel.cardState.value != CardState.UNSCRATCHED) {
            Log.d("Scratch", "Card already scratched or active, do nothing")
            return
        }

        val uid = UUID.randomUUID().toString()
        jobRunning = true
        currentJob = lifecycleScope.launch {
            delay(2000)
            Log.d("Scratch", "scratched after delay")

            viewModel.updateUid(uid)
            viewModel.updateCardState(CardState.SCRATCHED)
            jobRunning = false
        }
    }

    private fun updateCardUI(state: CardState) {
        when(state) {
            CardState.UNSCRATCHED -> {
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