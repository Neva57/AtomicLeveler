package com.example.atomicleveler.ui.fragments

class HabitListFragment : Fragment() {
    private var _binding: FragmentHabitListBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitViewModel: HabitViewModel
    private lateinit var adapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        habitViewModel = ViewModelProvider(requireActivity()).get(HabitViewModel::class.java)

        // Set up RecyclerView
        adapter = HabitAdapter(
            onItemClick = { habit ->
                val intent = Intent(requireContext(), HabitDetailActivity::class.java)
                intent.putExtra("HABIT_ID", habit.id)
                startActivity(intent)
            },
            onCompleteClick = { habit ->
                habitViewModel.completeHabitToday(habit)
                showCompletionDialog(habit)
            }
        )

        binding.recyclerViewHabits.adapter = adapter
        binding.recyclerViewHabits.layoutManager = LinearLayoutManager(requireContext())

        // Observe habits
        habitViewModel.allHabits.observe(viewLifecycleOwner) { habits ->
            adapter.submitList(habits)
            binding.emptyView.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showCompletionDialog(habit: Habit) {
        // Implement Material Dialog here
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Habit Completed!")
            .setMessage("Great job completing \"${habit.title}\"! Keep up the good work.")
            .setPositiveButton("Thanks") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}