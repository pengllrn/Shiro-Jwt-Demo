package com.jwt.demo.controller;

import com.jwt.demo.enity.ArticleEntity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Pengllrn
 * @since <pre>2019/6/5 10:57</pre>
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @GetMapping("/list")
    public ResponseEntity<List<ArticleEntity>> list(){

        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleEntity> read(@PathVariable int id){
        ArticleEntity article = new ArticleEntity();
        article.setArticleId(id);
        article.setAuthor("zhangsan");
        article.setContent("hdasihdosa");
        article.setTitle("标题");
        ResponseEntity<ArticleEntity> body = ResponseEntity.status(200).body(article);
        return body;
    }
}
