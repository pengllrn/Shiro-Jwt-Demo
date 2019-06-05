package com.jwt.demo.enity;

import lombok.Data;

/**
 * @author Pengllrn
 * @since <pre>2019/6/5 10:58</pre>
 */
@Data
public class ArticleEntity {
    private int articleId;

    private String title;
    private String author;
    private String content;
}
