package com.jornah.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jornah.model.newP.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author licong
 * @date 2021/9/25 12:03
 */
@Repository
@Mapper
public interface TagDao extends BaseMapper<Tag> {

    @Select("select * from article_tag a join tag t on a.tag_id=t.id where a.article_id=#{arId}")
    List<Tag> findTagBy(@Param("arId") Long arId);

    @Insert(" insert article_tag(ar_id,tag_id) values(#{arId},#{tagId})")
    Long insertMap(@Param("arId") Long arId, @Param("tagId") Long tagId);

    @Select("select ar_id from article_tag where tag_id=#{tagId}")
    List<Long> findArIdsBy(@Param("tagId") Long tagId);

    @Delete("delete from article_tag where ar_id=#{arId} and tag_id=#{tagId}")
    void deleteMap(@Param("arId") Long arId, @Param("tagId") Long tagId);

    @Delete("delete from article_tag where ar_id=#{arId}")
    void deleteMapBy(@Param("arId") Long arId);
}
