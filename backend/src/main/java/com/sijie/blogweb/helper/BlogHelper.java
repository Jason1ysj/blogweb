package com.sijie.blogweb.helper;

import com.sijie.blogweb.exception.InvalidParameterException;
import com.sijie.blogweb.exception.ResourceNotFoundException;
import com.sijie.blogweb.model.Blog;
import com.sijie.blogweb.model.Category;
import com.sijie.blogweb.model.Tag;
import com.sijie.blogweb.repository.CategoryRepository;
import com.sijie.blogweb.repository.TagRepository;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BlogHelper {
    private static Logger logger = LoggerFactory.getLogger(BlogHelper.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TagRepository tagRepository;

    public Blog validateAndBuildNewBlog(Blog inputBlog) {
        Blog newBlog = new Blog();

        if (Strings.isEmpty(inputBlog.getTitle())) {
            throw new InvalidParameterException("Invalid parameter: title can not be empty");
        }
        if (Strings.isEmpty(inputBlog.getDescription())) {
            throw new InvalidParameterException("Invalid parameter: description can not be empty");
        }
        if (inputBlog.getContent() == null) {
            throw new InvalidParameterException("Invalid parameter: content can not be null");
        }
        newBlog.setTitle(inputBlog.getTitle());
        newBlog.setDescription(inputBlog.getDescription());
        newBlog.setContent(inputBlog.getContent());

        // TODO: validate author and category
        if (Strings.isNotEmpty(inputBlog.getCategoryId())) {
            Category category = categoryRepository.findByCid(inputBlog.getCategoryId());
            if (category == null) {
                throw new ResourceNotFoundException("Category with cid: " + inputBlog.getCategoryId() + " not found");
            }
            newBlog.setCategoryId(inputBlog.getCategoryId());
        } else {
            logger.debug("New blog does not have a category Id");
        }

        // attach tags
        Set<Tag> resultTags = new HashSet<>();
        for (Tag tagExternal: inputBlog.getTags()) {
            if (Strings.isNotEmpty(tagExternal.getName())) {
                Tag tagInternal = tagRepository.findByName(tagExternal.getName());
                if (tagInternal == null) {
                    // create new tag
                    tagExternal.setBlogs(new HashSet<>());
                    tagInternal = tagRepository.save(tagExternal);
                }
                resultTags.add(tagInternal);
            }
        }
        newBlog.setTags(resultTags);

        newBlog.setBid(UUID.randomUUID().toString());
        newBlog.setViews(0);
        newBlog.setLikes(0);
        Date now = new Date();
        newBlog.setGmtCreate(now);
        newBlog.setGmtUpdate(now);

        return newBlog;
    }

    public Blog validateAndUpdateBlog(Blog inputBlog, Blog internalBlog) {
        if (Strings.isNotEmpty(inputBlog.getTitle())
                && !inputBlog.getTitle().equals(internalBlog.getTitle())) {
            internalBlog.setTitle(inputBlog.getTitle());
        }
        if (Strings.isNotEmpty(inputBlog.getDescription())
                && !inputBlog.getDescription().equals(internalBlog.getDescription())) {
            internalBlog.setDescription(inputBlog.getDescription());
        }
        if (inputBlog.getContent() != null
                && !inputBlog.getContent().equals(internalBlog.getContent())) {
            internalBlog.setContent(inputBlog.getContent());
        }

        // update category
        if (Strings.isNotEmpty(inputBlog.getCategoryId())
                && !inputBlog.getCategoryId().equals(internalBlog.getCategoryId())) {
            Category category = categoryRepository.findByCid(inputBlog.getCategoryId());
            if (category == null) {
                throw new ResourceNotFoundException("Category with cid: " + inputBlog.getCategoryId() + " not found");
            }
            internalBlog.setCategoryId(inputBlog.getCategoryId());
        }

        // update tags
        Set<Tag> newTags = new HashSet<>();
        for (Tag tagExternal: inputBlog.getTags()) {
            if (Strings.isNotEmpty(tagExternal.getName())) {
                Tag tagInternal = tagRepository.findByName(tagExternal.getName());
                if (tagInternal == null) {
                    // create new tag
                    tagExternal.setBlogs(new HashSet<>());
                    tagInternal = tagRepository.save(tagExternal);
                }
                newTags.add(tagInternal);
            }
        }
        internalBlog.setTags(newTags);

        internalBlog.setGmtUpdate(new Date());
        return internalBlog;
    }

}