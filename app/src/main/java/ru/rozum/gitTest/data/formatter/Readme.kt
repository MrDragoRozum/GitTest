package ru.rozum.gitTest.data.formatter

object Readme {
    fun getFormattedWithLinksImage(
        value: StringBuilder,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String {
        /*
    * Ищет в README.md подобные подстроки: images/screenshot_1.png screenshots/image1.jpg и т.п.
    * дабы получилось в итоге https://raw.githubusercontent.com/owner/project/branch/images/screenshot_1.png
    * в противном случае Markwon не сможет загрузить графический файл
        */
        val regexParsingImagesFromFolderRepo =
            Regex("(?<=!\\[alt text]\\(|\\[logo]:)(\\s)*(\\w+)/(\\w+)[.](\\w{2,3})(?=[)]?)")

        regexParsingImagesFromFolderRepo
            .findAll(value)
            .map { it.value }
            .forEach { foundLinkImage ->
                addURLForLinkImage(
                    value,
                    foundLinkImage,
                    ownerName,
                    repositoryName,
                    branchName
                )
            }

        return value.toString()
    }

    private fun addURLForLinkImage(
        builder: StringBuilder,
        foundLinkImage: String,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        builder.replace(
            Regex(foundLinkImage), URI_RAW_GITHUB_WITH_FORMATTERS.format(
                ownerName,
                repositoryName,
                branchName,
                foundLinkImage
            )
        ).also { result ->
            builder.setRange(0, result.length, result)
        }
    }

    private const val URI_RAW_GITHUB_WITH_FORMATTERS =
        "https://raw.githubusercontent.com/%s/%s/%s/%s"
}