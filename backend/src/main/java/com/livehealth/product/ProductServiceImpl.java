package com.livehealth.product;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.product.dto.request.product.CreateProductRequestDto;
import com.livehealth.product.dto.request.product.UpdateProductRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.product.ProductResponseDto;
import com.livehealth.product.Brand;
import com.livehealth.product.Category;
import com.livehealth.product.Product;
import com.livehealth.product.Tag;
import com.livehealth.product.ProductMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.product.BrandRepository;
import com.livehealth.product.CategoryRepository;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.TagRepository;
import com.livehealth.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.pagination.Sort;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import com.livehealth.shared.base.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

  ProductRepository productRepository;
  BrandRepository brandRepository;
  CategoryRepository categoryRepository;
  TagRepository tagRepository;
  ProductMapper productMapper;
  Cloudinary cloudinary;

  private static final Set<String> PRODUCT_SORT_FIELDS = Set.of("oldPrice", "name", "createdAt");

  @Override
  public PaginationResponseDto<ProductResponseDto> getAllProducts(PaginationRequestDto request) {
    Sort sort = buildProductSort(request.getSortBy(), request.getSortType());
    Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), sort);

    Page<Product> productPage = productRepository.findWithFilters(
        request.getCategoryId(),
        request.getBrandId(),
        request.getKeyword(),
        pageable);

    List<ProductResponseDto> dtos = productPage.getContent()
        .stream()
        .map(productMapper::toResponseDto)
        .collect(Collectors.toList());

    PagingMeta pagingMeta = new PagingMeta(
        productPage.getTotalElements(),
        productPage.getTotalPages(),
        request.getPageNum() + 1,
        request.getPageSize(),
        sort.isSorted() ? request.getSortBy() : null,
        sort.isSorted() ? normalizeSortType(request.getSortType()) : null);

    return new PaginationResponseDto<>(pagingMeta, dtos);
  }

  private Sort buildProductSort(String sortBy, String sortType) {
    if (sortBy == null || !PRODUCT_SORT_FIELDS.contains(sortBy)) {
      return Sort.unsorted();
    }

    Sort.Direction direction = "DESC".equalsIgnoreCase(sortType)
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;

    return Sort.by(direction, sortBy);
  }

  private String normalizeSortType(String sortType) {
    return "DESC".equalsIgnoreCase(sortType) ? "DESC" : "ASC";
  }


  @Override
  public ProductResponseDto getProductById(UUID id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

    return productMapper.toResponseDto(product);
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public ProductResponseDto createProduct(CreateProductRequestDto request) {
    Brand brand = brandRepository.findById(request.getBrandId())
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Brand.ERR_BRAND_NOT_FOUND));

    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));

    List<Tag> tags = new ArrayList<>();
    if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
      tags = tagRepository.findAllById(request.getTagIds());
    }

    Product product = productMapper.toEntity(request);
    product.setBrand(brand);
    product.setCategory(category);
    product.setTags(tags);
    product.setImageUrl(new ArrayList<>()); // Images will be uploaded later
    product.setCreatedAt(com.livehealth.shared.utils.TimeUtil.today().toString());
    product.setUpdatedAt(com.livehealth.shared.utils.TimeUtil.today().toString());

    return productMapper.toResponseDto(productRepository.save(product));
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public ProductResponseDto updateProduct(UUID id, UpdateProductRequestDto request) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

    if (request.getBrandId() != null && !product.getBrand().getId().equals(request.getBrandId())) {
      Brand brand = brandRepository.findById(request.getBrandId())
          .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Brand.ERR_BRAND_NOT_FOUND));
      product.setBrand(brand);
    }

    if (request.getCategoryId() != null && !product.getCategory().getId().equals(request.getCategoryId())) {
      Category category = categoryRepository.findById(request.getCategoryId())
          .orElseThrow(
              () -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));
      product.setCategory(category);
    }

    if (request.getTagIds() != null) {
      List<Tag> tags = tagRepository.findAllById(request.getTagIds());
      product.setTags(tags);
    }

    if (request.getSku() != null) {
      product.setSku(request.getSku());
    }

    productMapper.updateEntityFromDto(request, product);
    product.setUpdatedAt(com.livehealth.shared.utils.TimeUtil.today().toString());

    return productMapper.toResponseDto(productRepository.save(product));
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public ProductResponseDto uploadImages(UUID id, List<MultipartFile> files) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

    List<String> imageUrls = product.getImageUrl() != null ? product.getImageUrl() : new ArrayList<>();

    for (MultipartFile file : files) {
      try {
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) result.get("secure_url");
        imageUrls.add(imageUrl);
      } catch (Exception e) {
        throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.ERR_UPLOAD_IMAGE_FAIL);
      }
    }

    product.setImageUrl(imageUrls);
    product.setUpdatedAt(com.livehealth.shared.utils.TimeUtil.today().toString());

    return productMapper.toResponseDto(productRepository.save(product));
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public CommonResponseDto deleteProduct(UUID id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

    productRepository.delete(product);

    return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Product.DELETE_PRODUCT_SUCCESS);
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public ProductResponseDto addTagsToProduct(UUID id, List<UUID> tagIds) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

    List<Tag> existingTags = product.getTags();
    List<Tag> newTags = tagRepository.findAllById(tagIds);

    for (Tag tag : newTags) {
      if (!existingTags.contains(tag)) {
        existingTags.add(tag);
      }
    }

    product.setTags(existingTags);
    product.setUpdatedAt(com.livehealth.shared.utils.TimeUtil.today().toString());

    return productMapper.toResponseDto(productRepository.save(product));
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public ProductResponseDto removeTagFromProduct(UUID id, UUID tagId) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

    Tag tagToRemove = tagRepository.findById(tagId)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Tag.ERR_TAG_NOT_FOUND));

    List<Tag> tags = product.getTags();
    tags.remove(tagToRemove);

    product.setTags(tags);
    product.setUpdatedAt(com.livehealth.shared.utils.TimeUtil.today().toString());

    return productMapper.toResponseDto(productRepository.save(product));
  }
}
