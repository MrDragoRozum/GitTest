package ru.rozum.gitTest.presentation.customView

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.DetailsInfoRepoViewBinding
import ru.rozum.gitTest.domain.entity.RepoDetails

class DetailsInfoRepoView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    var repoDetails: RepoDetails? = null
        set(value) {
            value?.let { installInfoInViews(it) }
            field = value
        }

    private fun installInfoInViews(repo: RepoDetails) {
        with(binding) {
            textViewUriRepo.text = repo.htmlUser
            textViewLicenceOfRepo.text = repo.license
            textViewForks.text = "${repo.forks}"
            textViewStars.text = "${repo.starts}"
            textViewWatchers.text = "${repo.watchers}"
        }
    }

    private val binding: DetailsInfoRepoViewBinding

    init {
        inflate(context, R.layout.details_info_repo_view, this)
        binding = DetailsInfoRepoViewBinding.bind(this)
        setPadding(16f.toPX().toInt())
    }
    private fun Float.toPX() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        resources.displayMetrics
    )
}