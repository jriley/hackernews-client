package dev.jriley.nyt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_best_story.*
import kotlinx.android.synthetic.main.fragment_new_story.*
import kotlinx.android.synthetic.main.fragment_top_story.*
import timber.log.Timber

abstract class StoryFragment : Fragment() {

    abstract val layout: Int
    protected lateinit var listAdapter: StoryListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = StoryListAdapter().apply {
            clickObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ st ->
                    Toast.makeText(this@StoryFragment.context, "${st.title}", Toast.LENGTH_SHORT).show()
                })
                { t -> Timber.tag("@@").e(t, "StoryFragment.MainListObservableAdapter") }
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
        ViewModelProviders.of(this).get(StoryFragmentViewModel::class.java).apply {
            storyTypesFilter = StoryTypes.TOP
            observableListStory.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listAdapter::submitList) { t -> Timber.tag("@@@").e(t, "Ouch") }
        }

        top_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        top_list.adapter = listAdapter
    }

}

class NewFragment : StoryFragment() {
    override val layout: Int = R.layout.fragment_new_story

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewModelProviders.of(this).get(StoryFragmentViewModel::class.java).apply {
            storyTypesFilter = StoryTypes.NEW
            observableListStory.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listAdapter::submitList) { t -> Timber.tag("@@@").e(t, "Ouch") }
        }
        new_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        new_list.adapter = listAdapter
    }
}

class BestFragment : StoryFragment() {
    override val layout: Int = R.layout.fragment_best_story

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewModelProviders.of(this).get(StoryFragmentViewModel::class.java).apply {
            storyTypesFilter = StoryTypes.BEST
            observableListStory.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listAdapter::submitList) { t -> Timber.tag("@@@").e(t, "Ouch") }
        }

        best_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        best_list.adapter = listAdapter
    }
}