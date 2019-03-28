package dev.jriley.nyt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class StoryFragment : Fragment() {

    abstract val layout: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layout, container, false)

    companion object {
        fun newInstance(storyType: StoryTypes): Fragment =
            when (storyType) {
                StoryTypes.TOP -> TopFragment()
                StoryTypes.NEW -> NewFragment()
                StoryTypes.BEST -> BestFragment()
            }
    }
}

class TopFragment : StoryFragment() {
    override val layout: Int = R.layout.fragment_top_story
}

class NewFragment : StoryFragment() {
    override val layout: Int = R.layout.fragment_new_story
}

class BestFragment : StoryFragment() {
    override val layout: Int = R.layout.fragment_best_story
}