package com.livehealth.product;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.product.dto.request.category.CreateCategoryRequestDto;
import com.livehealth.product.dto.request.category.UpdateCategoryRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.category.CategoryResponseDto;
import com.livehealth.product.Category;
import com.livehealth.product.CategoryMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.product.CategoryRepository;
import com.livehealth.product.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    CategoryMapper categoryMapper;

    @Override
    public PaginationResponseDto<CategoryResponseDto> getAllCategories(PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryResponseDto> categoryResponseDtos = categoryPage.getContent()
                .stream()
                .map(categoryMapper::categoryToCategoryResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null,
                null);

        return new PaginationResponseDto<>(pagingMeta, categoryResponseDtos);
    }

    @Override
    public CategoryResponseDto getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));

        return categoryMapper.categoryToCategoryResponseDto(category);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CategoryResponseDto createCategory(CreateCategoryRequestDto request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.Category.ERR_CATEGORY_NAME_EXISTED);
        }

        Category category = categoryMapper.createCategoryRequestDtoToCategory(request);

        return categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CategoryResponseDto updateCategory(UUID id, UpdateCategoryRequestDto request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new VsException(HttpStatus.CONFLICT, ErrorMessage.Category.ERR_CATEGORY_NAME_EXISTED);
            }
        }

        categoryMapper.updateCategoryFromDto(request, category);

        return categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Category.ERR_CATEGORY_HAS_PRODUCTS);
        }

        categoryRepository.delete(category);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Category.DELETE_CATEGORY_SUCCESS);
    }
}
