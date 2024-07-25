package ru.rozum.gitTest.presentation.fragment.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.rozum.gitTest.databinding.ItemRepoBinding
import ru.rozum.gitTest.domain.entity.Repo

class RepoItemViewHolder(private val binding: ItemRepoBinding) : ViewHolder(binding.root) {
    fun onBind(repo: Repo) {
        with(binding) {
            textViewLanguage.setTextColor(Color.parseColor(repo.colorLanguageRGB))
            textViewLanguage.text = repo.language
            textViewRepoName.text = repo.name

            if(repo.description.isBlank()) {
                textViewRepoDescription.visibility = View.GONE
            } else {
                textViewRepoDescription.text = repo.description
            }
        }
    }
}
