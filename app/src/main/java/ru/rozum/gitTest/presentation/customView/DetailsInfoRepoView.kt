package ru.rozum.gitTest.presentation.customView

import android.content.Context
import android.text.Spanned
import android.util.AttributeSet
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
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
            textViewForks.text = installString(
                repo.forks,
                R.color.green_forks,
                R.string.forks
            )
            textViewStars.text = installString(
                repo.starts,
                R.color.yellow_stars,
                R.string.stars
            )
            textViewWatchers.text = installString(
                repo.watchers,
                R.color.blue_watchers,
                R.string.watchers
            )
        }
    }

    private fun installString(count: Int, color: Int, string: Int): Spanned {
        val colorRGB = context.getString(color).replaceFirst(
            Regex(PATTERN_FIND_FIRST_TWO_F),
            DELETE_THEM
        )
        val gotString = context.getString(string)
        val formatted = String.format("<font color=%s>%d</font> %s", colorRGB, count, gotString)
        return HtmlCompat.fromHtml(formatted, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private companion object {
        const val PATTERN_FIND_FIRST_TWO_F = "f{2}"
        const val DELETE_THEM = ""
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