package dev.jriley.nyt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*

class BottomNavActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_top -> select(StoryTypes.TOP).let { true }
                R.id.navigation_best -> select(StoryTypes.BEST).let { true }
                R.id.navigation_new -> select(StoryTypes.NEW).let { true }
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

enum class StoryTypes { TOP, BEST, NEW }