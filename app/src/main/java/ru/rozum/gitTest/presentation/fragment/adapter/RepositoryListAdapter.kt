package ru.rozum.gitTest.presentation.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.rozum.gitTest.databinding.ItemRepoBinding
import ru.rozum.gitTest.domain.entity.Repo
import javax.inject.Inject

class RepositoryListAdapter @Inject constructor() :
    ListAdapter<Repo, RepoItemViewHolder>(RepoItemDiffCallback()) {

    var onClickRepo: ((repo: Repo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoItemViewHolder {
        val view = ItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoItemViewHolder, position: Int) {
        getItem(position).also { repo ->
            holder.onBind(repo)
            holder.itemView.setOnClickListener {
                onClickRepo?.invoke(repo)
            }
        }
    }
}