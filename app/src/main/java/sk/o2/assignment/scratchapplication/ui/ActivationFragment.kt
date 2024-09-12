package sk.o2.assignment.scratchapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.o2.assignment.scratchapplication.databinding.FragmentActivateBinding
import sk.o2.assignment.scratchapplication.retrofit.ApiInterface
import sk.o2.assignment.scratchapplication.retrofit.RetrofitInstance

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ActivationFragment : Fragment() {

    private var _binding: FragmentActivateBinding? = null
    private val viewModel by lazy { (requireActivity() as MainActivity).viewModel }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var uid = ""

    private var jobRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentActivateBinding.inflate(inflater, container, false)

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

        binding.buttonActivate.setOnClickListener {
            if (!jobRunning)
                activate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun activate() {
        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()

        when (viewModel.cardState.value) {
            CardState.UNSCRATCHED -> {
                dialog.apply {
                    setMessage("Please scratch the card first")
                    show()
                }
                return
            }
            CardState.ACTIVE -> {
                dialog.apply {
                    setMessage("Card is already active")
                    show()
                }
                return
            }
            else -> {}
        }

        val retrofit = RetrofitInstance.getInstance().create(ApiInterface::class.java)
        jobRunning = true

        try {
            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    retrofit.getVersion(viewModel.uid.value
                        ?: throw Exception("Missing uid in viewmodel"))
                }

                Log.d("Activation", "activated with UID $uid, result: $result")
                Log.d("Activation", "result body: ${result.body()}")

                result.body()?.let {
                    val androidVersion = it.android

                    if (androidVersion.toInt() > 277028) {
                        viewModel.updateCardState(CardState.ACTIVE)
                    } else {
                        dialog.apply {
                            setMessage("Wrong version value")
                            show()
                        }
                    }
                } ?: {
                    dialog.apply {
                        setMessage("Invalid response")
                        show()
                    }
                }

                jobRunning = false
            }
        } catch (ex: Exception) {
            Log.d("Activation", "ex message: ${ex.message}")

            dialog.apply {
                setMessage("Error occurred during activation")
                show()
            }
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