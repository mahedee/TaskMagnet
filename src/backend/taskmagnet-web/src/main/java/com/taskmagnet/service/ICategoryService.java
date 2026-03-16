package com.taskmagnet.service;

import com.taskmagnet.dto.CategoryRequest;
import com.taskmagnet.dto.CategoryResponse;

import java.util.List;

public interface ICategoryService {

    List<CategoryResponse> getAll();

    CategoryResponse getById(Long id);

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);
}
