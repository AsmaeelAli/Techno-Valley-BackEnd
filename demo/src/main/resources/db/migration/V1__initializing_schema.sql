-- ========== جدول المستخدمين ==========
CREATE TABLE users_entity
(
    id                BIGINT,
    name              VARCHAR(100)       NOT NULL,
    password          VARCHAR(300)       NOT NULL,
    email             VARCHAR(50) UNIQUE NOT NULL,
    verification_code VARCHAR(50)        NOT NULL,
    enable            BOOLEAN   DEFAULT TRUE,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT users_entity_pk PRIMARY KEY (id)
);

-- ========== جدول تجارب المستخدمين ==========
CREATE TABLE user_experiences
(
    id                BIGINT,
    user_id           BIGINT        NOT NULL UNIQUE,
    experience        VARCHAR(1000) NOT NULL,
    experience_vector tsvector, -- حقل للبحث

    CONSTRAINT user_experiences_pk PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES users_entity (id)
        ON DELETE CASCADE
);

-- ========== جدول المنشورات ==========
CREATE TABLE posts_entity
(
    id         UUID,
    user_id    BIGINT NOT NULL,
    content    TEXT   NOT NULL,
    image_url  VARCHAR(255),
    file_url   VARCHAR(255),
    enable     BOOLEAN   DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT posts_entity_pk PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES users_entity (id)
        ON DELETE CASCADE
);

-- ========== جدول هاشتاقات البوست ==========
CREATE TABLE post_hashtags
(
    id         BIGINT,
    post_id    UUID        NOT NULL,
    tag        VARCHAR(50) NOT NULL,
    tag_vector tsvector, -- حقل للبحث

    CONSTRAINT post_hashtags_pk PRIMARY KEY (id),
    CONSTRAINT fk_post FOREIGN KEY (post_id)
        REFERENCES posts_entity (id)
        ON DELETE CASCADE,
    CONSTRAINT unique_post_tag UNIQUE (post_id, tag)
);

-- ========== دالة تحديث experience_vector ==========
CREATE OR REPLACE FUNCTION update_experience_vector() RETURNS trigger AS
$$
BEGIN
    NEW.experience_vector := to_tsvector('simple', COALESCE(NEW.experience, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ========== دالة تحديث tag_vector ==========
CREATE OR REPLACE FUNCTION update_tag_vector() RETURNS trigger AS
$$
BEGIN
    NEW.tag_vector := to_tsvector('simple', COALESCE(NEW.tag, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ========== تريغر لتحديث experience_vector ==========
CREATE TRIGGER trg_update_experience_vector
    BEFORE INSERT OR UPDATE
    ON user_experiences
    FOR EACH ROW
EXECUTE FUNCTION update_experience_vector();

-- ========== تريغر لتحديث tag_vector ==========
CREATE TRIGGER trg_update_tag_vector
    BEFORE INSERT OR UPDATE
    ON post_hashtags
    FOR EACH ROW
EXECUTE FUNCTION update_tag_vector();


CREATE TABLE favorites
(
    id         BIGINT,
    user_id    BIGINT NOT NULL,                                                  -- معرّف المستخدم
    post_id    UUID NOT NULL,                                                  -- معرّف المنشور الذي تم حفظه
    enable     BOOLEAN   DEFAULT TRUE,                                        -- حالة الحفظ (مفعل أم لا)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                           -- تاريخ ووقت الحفظ
    CONSTRAINT favorites_pk PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users_entity (id), -- ربطه بجدول المستخدمين
    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES posts_entity (id)  -- ربطه بجدول المنشورات
);

CREATE TABLE likes
(
    id         BIGINT,
    user_id    BIGINT NOT NULL,                                                  -- معرّف المستخدم
    post_id    UUID NOT NULL,                                                  -- معرّف المنشور الذي تم الإعجاب به
    enable     BOOLEAN   DEFAULT TRUE,                                        -- حالة الإعجاب (مفعل أم لا)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                           -- تاريخ ووقت الإعجاب
    CONSTRAINT likes_pk PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users_entity (id), -- ربطه بجدول المستخدمين
    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES posts_entity (id)  -- ربطه بجدول المنشورات
);
