package dev.jriley.nyt

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jakewharton.rxbinding2.view.RxView
import dev.jriley.nyt.data.Story
import dev.jriley.nyt.ui.inflate
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class StoryListAdapter(diffCallback: StoryDiffCallback = StoryDiffCallback(),
                       private val clickSubscriptions: CompositeDisposable = CompositeDisposable(),
                       private val clickSubject: PublishSubject<Story> = PublishSubject.create(),
                       val clickObservable: Observable<Story> = clickSubject) : ListAdapter<Story, StoryViewHolder>(diffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
       return StoryViewHolder(parent.inflate(R.layout.list_item_main)).apply {
            Timber.tag("@@").d("onCreateViewHolder:StoryViewHolder")

            clickSubscriptions.add(RxView.clicks(itemView)
                    .takeUntil(RxView.detaches(parent))
                    .map { data }
                    .subscribe({ clickSubject.onNext(it) }, Timber::e))
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position).let {story ->
            holder.apply {
                data = story
                title.text = story.title
                by.text = story.by
            }
        }
    }

}
class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean = oldItem == newItem
}

