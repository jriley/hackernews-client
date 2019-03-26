package dev.jriley.nyt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_story.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> select(StoryTypes.TOP).let { true }
                R.id.navigation_dashboard -> select(StoryTypes.BEST).let { true }
                R.id.navigation_notifications -> select(StoryTypes.NEW).let { true }
                else -> false
            }
        }
        if (savedInstanceState == null) select(StoryTypes.TOP)
    }

    private fun select(storyType: StoryTypes) {
        supportFragmentManager.apply {
            val top = findOrInit(StoryTypes.TOP)
            val new = findOrInit(StoryTypes.NEW)
            val best = findOrInit(StoryTypes.BEST)
            beginTransaction().apply {
                when (storyType) {
                    StoryTypes.TOP -> hide(new).hide(best).show(top)
                    StoryTypes.NEW -> hide(best).hide(top).show(new)
                    StoryTypes.BEST -> hide(top).hide(new).show(best)
                }
            }.commit()
        }
    }

    private fun FragmentManager.findOrInit(storyType: StoryTypes) =
        findFragmentByTag(storyType.toString()) ?: StoryFragment.newInstance(storyType).also {
            beginTransaction().add(R.id.content, it, storyType.toString()).hide(it).commit()
        }
}

class StoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_story, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.text = StoryTypes.values()[arguments?.getInt(TYPE) ?: 0].name
    }

    companion object {
        const val TYPE = "TYPE"
        fun newInstance(storyType: StoryTypes): Fragment =
            StoryFragment().apply { arguments = Bundle().apply { putInt(TYPE, storyType.ordinal) } }
    }
}

enum class StoryTypes { TOP, BEST, NEW }