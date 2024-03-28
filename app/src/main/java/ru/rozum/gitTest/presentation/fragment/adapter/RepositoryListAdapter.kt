package ru.rozum.gitTest.presentation.fragment.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.rozum.gitTest.databinding.ItemRepoBinding
import ru.rozum.gitTest.domain.entity.Repo
import javax.inject.Inject

typealias OnClickRepoListener = ((repo: Repo) -> Unit)?

class RepositoryListAdapter @Inject constructor() :
    ListAdapter<Repo, RepoItemViewHolder>(RepoItemDiffCallback()) {

    var onClickRepo: OnClickRepoListener = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoItemViewHolder {
        val view = ItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoItemViewHolder, position: Int) {
        getItem(position).also { repo ->
            with(holder.binding) {
                textViewLanguage.setTextColor(Color.parseColor(repo.colorLanguageRGB))
                textViewLanguage.text = repo.language
                textViewRepoName.text = repo.name
                if (repo.description.isBlank()) {
                    textViewRepoDescription.visibility = View.GONE
                } else {
                    textViewRepoDescription.text = repo.description
                }
            }
            holder.itemView.setOnClickListener {
                onClickRepo?.invoke(repo)
            }
        }
    }
}