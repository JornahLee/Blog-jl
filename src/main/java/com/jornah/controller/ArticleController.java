package com.jornah.controller;


import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jornah.anno.AccessControl;
import com.jornah.cache.CacheService;
import com.jornah.model.DraftStatus;
import com.jornah.model.dto.ArticleSaveBo;
import com.jornah.model.entity.Article;
import com.jornah.model.entity.Config;
import com.jornah.model.qo.ArticleBatchQo;
import com.jornah.model.qo.ArticleQo;
import com.jornah.model.vo.ArticleMetaInfo;
import com.jornah.model.vo.ArticleVo;
import com.jornah.service.ConfigService;
import com.jornah.service.DraftService;
import com.jornah.service.article.ArticleService;
import com.jornah.utils.APIResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController()
@RequestMapping("/blog/article")
@CrossOrigin
@Validated
@Slf4j
@Api("文章")
public class ArticleController extends BaseController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private DraftService draftService;
    @Autowired
    private CacheService cacheService;

    @ApiOperation("保存或更新")
    @PostMapping(value = "/saveOrUpdate")
    @AccessControl
    public APIResponse<?> saveOrUpdate(@RequestBody ArticleSaveBo articleSaveBo) {
        long ret = articleService.saveOrUpdate(articleSaveBo);
        return APIResponse.success(ImmutableMap.of("id", ret));
    }

    @ApiOperation("查询单个文档")
    @GetMapping(value = "/{id}")
    public APIResponse<ArticleVo> getArticle(@PathVariable Long id, @RequestParam(required = false) String passphrase) {
        ArticleVo articleVo = articleService.getArticleBy(id, passphrase);
        return APIResponse.success(articleVo);
    }

    @ApiOperation("分页查询文档")
    @PostMapping(value = "/list")
    public APIResponse<PageInfo<ArticleVo>> getArticleList(@RequestBody @Validated ArticleQo qo) {
        Long tagId = qo.getQueryKeyColumns().get("byTag");
        Long cateId = qo.getQueryKeyColumns().get("byCate");
        PageInfo<ArticleVo> orderBy;
        if (Objects.nonNull(tagId)) {
            orderBy = articleService.getArticleByTag(tagId, qo.getPageNum(), qo.getPageSize());
        } else if (Objects.nonNull(cateId)) {
            orderBy = articleService.getArticleByCate(cateId, qo.getPageNum(), qo.getPageSize());
        } else {
            orderBy = articleService.getArticlesOrderBy(qo);
        }
        return APIResponse.success(orderBy);
    }


    @ApiOperation("推荐文章列表")
    @GetMapping(value = "/list/recommended")
    public APIResponse<List<ArticleVo>> listRecommendArticle(@RequestParam Integer size) {

        List<ArticleVo> list = articleService.getRecommendArticle(Objects.isNull(size) ? 10 : size);
        return APIResponse.success(list);
    }


    @ApiOperation("保存草稿")
    @PostMapping(value = "/draft/save")
    @AccessControl
    public APIResponse<?> saveDraft(Article article) {
        draftService.createDraft(article.getId(), article.getContent(), DraftStatus.getByString(article.getStatus()));
        return APIResponse.success();
    }

    @ApiOperation("删除文章")
    @DeleteMapping("/{id}")
    @AccessControl
    public APIResponse<?> deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteBy(id);
        return APIResponse.success();
    }

    @ApiOperation("获取文章信息，分类，标签，评论等")
    @GetMapping("/meta/{articleId}")
    public APIResponse<ArticleMetaInfo> getMetaInfo(@PathVariable("articleId") Long articleId) {
        return APIResponse.success(articleService.getArticleMetaInfo(articleId));
    }

    @ApiOperation("batch 获取文章信息，分类，标签，评论等")
    @PostMapping("/meta/batch")
    public APIResponse<List<ArticleMetaInfo>> getMetaInfo(@RequestBody ArticleBatchQo batchQo) {
        return APIResponse.success(articleService.batchGetArticleMetaInfo(batchQo.getArticleIdList()));
    }

    @ApiOperation("获取上一篇或下一篇文章 id")
    @GetMapping("/nextOrLast")
    public APIResponse<?> getNextOrLastArticle(@RequestParam Long articleId, @RequestParam boolean next,
                                               @RequestParam String byType) {
        return APIResponse.success(articleService.getNextOrLastArticle(articleId, next, byType));
    }
}
