package ru.tim.dictophone.player

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import ru.tim.dictophone.R

/** Отображает диалоговое оено с проигрывателем. */
class PlayerFragment : DialogFragment() {

    private lateinit var playerViewModel: PlayerViewModel
    private var itemPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemPath = arguments?.getString(ARG_ITEM_PATH)

        val application = requireNotNull(activity).application
        val playerViewModelFactory = PlayerViewModelFactory(itemPath, application)

        playerViewModel =
            ViewModelProvider(this, playerViewModelFactory).get(PlayerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playerView: PlayerControlView = view.findViewById(R.id.playerView)
        playerView.showTimeoutMs = 0

        playerViewModel.player.observe(viewLifecycleOwner) {
            playerView.player = it
        }
    }

    companion object {
        private const val ARG_ITEM_PATH = "recording_item_path"

        fun newInstance(itemPath: String?): PlayerFragment {
            val args = Bundle().apply { putString(ARG_ITEM_PATH, itemPath) }
            return PlayerFragment().apply { arguments = args }
        }
    }
}