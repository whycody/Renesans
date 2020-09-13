package pl.renesans.renesans.article

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment
import java.lang.StringBuilder

class ArticlePresenterImpl(private val article: Article,
                           private val articleFragmentView: ArticleContract.ArticleFragmentView):
    ArticleContract.ArticlePresenter {

    private var listOfPhotos: List<Photo>? = null
    private var numberOfPhotos = 1

    override fun loadContent() {
        listOfPhotos = article.listOfPhotos
        articleFragmentView.setToolbarTitle(article.title!!)
        loadMainPhoto()
        loadHeader()
        loadParagraphs()
        articleFragmentView.tryToAddRelatedRecyclerView()
    }

    override fun setArticleImageHeight(height: Int, objectType: Int) {
        articleFragmentView.setDefaultHeightOfArticleImage()
        var checkingType = article.typeOfScaling
        if (checkingType == null) checkingType = objectType
        when (checkingType) {
            DiscoverRecyclerFragment.ARTS ->
                articleFragmentView.setArticleImageHeight((height * 1.5).toInt())
            DiscoverRecyclerFragment.OTHER_ERAS, DiscoverRecyclerFragment.PHOTOS,
            DiscoverRecyclerFragment.EVENTS ->
                articleFragmentView.setArticleImageHeight((height * 0.8).toInt())
        }
    }

    override fun copyParagraph(paragraph: Paragraph) {
        val stringBuilder = StringBuilder()
        if(paragraph.subtitle != null) stringBuilder.append("${paragraph.subtitle}. ")
        stringBuilder.append(paragraph.content)
        articleFragmentView.copyTextToClipboard(stringBuilder.toString())
    }

    private fun loadMainPhoto() {
        if(listOfPhotos!=null) articleFragmentView.loadPhoto(0, listOfPhotos!![0].objectId!!)
        else articleFragmentView.loadPhoto(0, article.objectId + "_0")
    }

    private fun loadHeader() {
        articleFragmentView.addHeaderTitleView(article.title)
        if(article.header?.content != null)
            articleFragmentView.addHeaderContentView(getContentFromHeaderPairs())
    }

    private fun getContentFromHeaderPairs(): String {
        var content = ""
        article.header!!.content!!.keys.forEachIndexed { index, key ->
            if(index!=0) content += "\n"
            content += key + ": " + article.header!!.content!![key]
        }
        return content
    }

    private fun loadParagraphs() {
        article.listOfParagraphs?.forEachIndexed { index, paragraph ->
            loadImage(index-1)
            val subtitleIsAvailable = paragraph.subtitle!=null
            val invisibleView = articleFragmentView.getInvisibleView(!subtitleIsAvailable)
            articleFragmentView.saveInvisibleView(index, invisibleView)

            if(subtitleIsAvailable) addTitleOfParagraph(paragraph, index)
            articleFragmentView.addViewToArticleLayout(invisibleView)
            addContentOfParagraph(paragraph, index)
        }
        loadImage(article.listOfParagraphs?.size?.minus(1))
    }

    private fun addTitleOfParagraph(paragraph: Paragraph, index: Int) =
        articleFragmentView.addParagraphTitleView(paragraph.subtitle!!, index)

    private fun addContentOfParagraph(paragraph: Paragraph, index: Int) =
        articleFragmentView.addParagraphContentView(paragraph.content!!, index)

    private fun loadImage(index: Int?) =
        loadImageAsParagraph(listOfPhotos?.find { it.numberOfParagraph == index })

    private fun loadImageAsParagraph(photo: Photo?) {
        if(photo!=null) {
            articleFragmentView.addParagraphImageView(photo)
            if(photo.description!=null)
                articleFragmentView.addParagraphImageDescriptionView(photo.description!!)
            articleFragmentView.loadPhoto(numberOfPhotos, photo.objectId!!)
            numberOfPhotos++
        }
    }
}