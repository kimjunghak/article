### Spring UTF-8 설정
    -Dfile.encoding=UTF-8

### DDL

#### Member

```sql
CREATE TABLE Member
(
    member_id      BIGINT NOT NULL,
    userid         VARCHAR(255),
    username       VARCHAR(255),
    pw             VARCHAR(255),
    point          INT    NOT NULL,
    role           VARCHAR(255),
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    last_logged_at TIMESTAMP,
    CONSTRAINT pk_member PRIMARY KEY (member_id)
);
```

#### Article
```sql
CREATE TABLE Article
(
article_id       BIGINT NOT NULL,
article_title    VARCHAR(255),
article_contents VARCHAR(255),
view_count       INT    NOT NULL,
created_at       TIMESTAMP,
updated_at       TIMESTAMP,
member_id        BIGINT,
CONSTRAINT pk_article PRIMARY KEY (article_id)
);

ALTER TABLE Article
ADD CONSTRAINT FK_ARTICLE_ON_MEMBERID FOREIGN KEY (member_id) REFERENCES Member (member_id);
```

#### Comments
```sql
CREATE TABLE Comments
(
    comments_id       BIGINT NOT NULL,
    comments_contents VARCHAR(255),
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    member_id         BIGINT,
    article_id        BIGINT,
    CONSTRAINT pk_comments PRIMARY KEY (comments_id)
);

ALTER TABLE Comments
    ADD CONSTRAINT FK_COMMENTS_ON_ARTICLEID FOREIGN KEY (article_id) REFERENCES Article (article_id);

ALTER TABLE Comments
    ADD CONSTRAINT FK_COMMENTS_ON_MEMBERID FOREIGN KEY (member_id) REFERENCES Member (member_id);
```

#### Swagger

`localhost:8080/swagger-ui/index.html`

- `/signup` -> `/signin` 을 실행하고 `/signin`을 통해 전달받은 token을 Swagger Authorize value에 `Bearer`와 함께 입력 후 다른 API 실행