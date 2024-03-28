package ru.rozum.gitTest.presentation.fragment.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.rozum.gitTest.domain.entity.Repo

class RepoItemDiffCallback : DiffUtil.ItemCallback<Repo>() {
    override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean = oldItem == newItem
}