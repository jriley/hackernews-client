package dev.jriley.nyt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jriley.nyt.data.Story
import dev.jriley.nyt.ui.enterRightExitLeft
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_best_story.*
import kotlinx.android.synthetic.main.fragment_new_story.*
import kotlinx.android.synthetic.main.fragment_top_story.*
import timber.log.Timber
import javax.inject.Inject

abstract class StoryFragment : Fragment() {

    abstract val layout: Int

    private lateinit var listAdapter: StoryListAdapter

    @Inject
    lateinit var viewModelFactory: FragmentViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layout, container, false)

    protected fun wireUpModelToList(clazz: Class<StoryFragmentViewModel>, storyTypes: StoryTypes, recyclerView: RecyclerView) {

        listAdapter = StoryListAdapter().apply {
            clickObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this@StoryFragment::startWebViewActivity)
                { t -> Timber.tag("@@").e(t, "StoryFragment.MainListObservableAdapter") }
        }

        ViewModelProviders.of(this, viewModelFactory).get(clazz).apply {
            storyTypesFilter = storyTypes
            observableListStory.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listAdapter::submitList) { t -> Timber.tag("@@@").e(t, "$clazz.observableListStory") }
        }

        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = listAdapter
    }

    private fun startWebViewActivity(story: Story) {
        Timber.tag("@@").i("Story clicked ${story.title}")
        if (story.url.isNotBlank()) {
            activity?.let {
                startActivity(Intent(it, WebContentActivity::class.java).apply {
                    putExtra(WebContentActivity.URL_TAG, story.url)
                    putExtra(WebContentActivity.ID_TAG, story.id)
                })
                it.enterRightExitLeft()
            } ?: Timber.tag("@@").e("There was no Activity some how for - ${story.id}:${story.title}")
        } else {
            Toast.makeText(activity, "No valid url for that story", Toast.LENGTH_SHORT).show()
        }
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NewsApp.component.inject(this)
        wireUpModelToList(StoryFragmentViewModel::class.java, StoryTypes.TOP, top_list)
    }

}

class NewFragment : StoryFragment() {
    override val layout: Int = R.layout.fragment_new_story

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NewsApp.component.inject(this)
        wireUpModelToList(StoryFragmentViewModel::class.java, StoryTypes.NEW, new_list)
    }
}

class BestFragment : StoryFragment() {
    override val layout: Int = R.layout.fragment_best_story

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NewsApp.component.inject(this)
        wireUpModelToList(StoryFragmentViewModel::class.java, StoryTypes.BEST, best_list)
    }
}