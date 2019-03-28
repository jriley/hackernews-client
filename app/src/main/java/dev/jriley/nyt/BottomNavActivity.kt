package dev.jriley.nyt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

enum class StoryTypes { TOP, BEST, NEW }