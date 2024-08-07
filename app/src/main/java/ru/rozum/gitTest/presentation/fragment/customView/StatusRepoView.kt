package ru.rozum.gitTest.presentation.fragment.customView

import android.content.Context
import android.text.Spanned
import android.util.AttributeSet
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.core.view.setPadding
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.StatusRepoViewBinding
import ru.rozum.gitTest.domain.entity.RepoDetails

class StatusRepoView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    private val binding: StatusRepoViewBinding

    var repoDetails: RepoDetails? = null
        set(value) {
            value?.let { installInfoInViews(it) }
            field = value
        }

    private fun installInfoInViews(repo: RepoDetails) {
        with(binding) {
            textViewUriRepo.text = repo.htmlUser
            textViewLicenceOfRepo.text = repo.license
            textViewForks.text = installStatusRepo(
                repo.forks,
                R.color.green_forks,
                R.string.forks
            )
            textViewStars.text = installStatusRepo(
                repo.stars,
                R.color.yellow_stars,
                R.string.stars
            )
            textViewWatchers.text = installStatusRepo(
                repo.watchers,
                R.color.blue_watchers,
                R.string.watchers
            )
        }
    }

    init {
        inflate(context, R.layout.status_repo_view, this)
        binding = StatusRepoViewBinding.bind(this)
        setPadding(16f.toPX().toInt())
    }

    private fun installStatusRepo(count: Int, color: Int, string: Int): Spanned {
        val colorRGB = context.getString(color).replaceFirst(
            PATTERN_FIND_FIRST_TWO_F,
            DELETE_THEM
        )
        val status = context.getString(string)
        val formattedStatus = COLORFUL_STATUS_FORMATTED.format(colorRGB, count, status)
        return HtmlCompat.fromHtml(formattedStatus, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun Float.toPX() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        resources.displayMetrics
    )

    private companion object {
        const val PATTERN_FIND_FIRST_TWO_F = "ff"
        const val DELETE_THEM = ""
        const val COLORFUL_STATUS_FORMATTED = "<font color=%s>%d</font> %s"
    }
}