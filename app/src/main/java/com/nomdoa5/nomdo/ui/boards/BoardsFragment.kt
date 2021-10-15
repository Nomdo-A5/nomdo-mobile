package com.nomdoa5.nomdo.ui.boards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.adapter.BoardAdapter
import com.nomdoa5.nomdo.databinding.FragmentBoardsBinding
import com.nomdoa5.nomdo.repository.model.Board

class BoardsFragment : Fragment() {
    //    private lateinit var myWorkspacesViewModel: MyWorkspacesViewModel
    private var _binding: FragmentBoardsBinding? = null
    private val binding get() = _binding!!
    private val boardAdapter = BoardAdapter()
    private var boards = arrayListOf<Board>()
    private lateinit var boardName: Array<String>
    private lateinit var createdAt: Array<String>
    private lateinit var rvBoard: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        myWorkspacesViewModel =
//            ViewModelProvider(this).get(MyWorkspacesViewModel::class.java)

        _binding = FragmentBoardsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        myWorkspacesViewModel.text.observe(viewLifecycleOwner, {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setData(){
        boardName = resources.getStringArray(R.array.name)
        createdAt = resources.getStringArray(R.array.creator)

        for(i in boardName.indices){
            val board = Board(
                i,
                boardName[i],
                "Owned by " + createdAt[i],
            )
            boards.add(board)
        }
    }

    fun setupRecyclerView(){
        rvBoard = requireView().findViewById(R.id.rv_boards)
        rvBoard.setHasFixedSize(true)
        rvBoard.addItemDecoration(BoardAdapter.MarginItemDecoration(15))
        rvBoard.layoutManager = LinearLayoutManager(context)
        boardAdapter.setData(boards)
        rvBoard.adapter = boardAdapter

        boardAdapter.setOnItemClickCallback(object : BoardAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Board) {
                Snackbar.make(requireView(), "Kamu mengklik #${data.idBoard}", Snackbar.LENGTH_SHORT).show()
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_boards_to_nav_tasks)
            }
        })
    }
}