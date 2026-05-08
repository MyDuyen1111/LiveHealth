package com.livehealth.product;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.product.dto.request.tag.CreateTagRequestDto;
import com.livehealth.product.dto.request.tag.UpdateTagRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.tag.TagResponseDto;
import com.livehealth.product.Tag;
import com.livehealth.product.TagMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.product.TagRepository;
import com.livehealth.product.TagService;
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
public class TagServiceImpl implements TagService {

    TagRepository tagRepository;

    TagMapper tagMapper;

    @Override
    public PaginationResponseDto<TagResponseDto> getAllTags(PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());

        Page<Tag> tagPage = tagRepository.findAll(pageable);

        List<TagResponseDto> tagResponseDtos = tagPage.getContent()
                .stream()
                .map(tagMapper::tagToTagResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                tagPage.getTotalElements(),
                tagPage.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null,
                null);

        return new PaginationResponseDto<>(pagingMeta, tagResponseDtos);
    }

    @Override
    public TagResponseDto getTagById(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Tag.ERR_TAG_NOT_FOUND));

        return tagMapper.tagToTagResponseDto(tag);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TagResponseDto createTag(CreateTagRequestDto request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.Tag.ERR_TAG_NAME_EXISTED);
        }

        Tag tag = tagMapper.createTagRequestDtoToTag(request);

        return tagMapper.tagToTagResponseDto(tagRepository.save(tag));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TagResponseDto updateTag(UUID id, UpdateTagRequestDto request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Tag.ERR_TAG_NOT_FOUND));

        if (request.getName() != null && !request.getName().equals(tag.getName())) {
            if (tagRepository.existsByName(request.getName())) {
                throw new VsException(HttpStatus.CONFLICT, ErrorMessage.Tag.ERR_TAG_NAME_EXISTED);
            }
        }

        tagMapper.updateTagFromDto(request, tag);

        return tagMapper.tagToTagResponseDto(tagRepository.save(tag));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto deleteTag(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Tag.ERR_TAG_NOT_FOUND));

        // When tag is deleted, remove it from all products first since it's ManyToMany
        if (tag.getProducts() != null) {
            tag.getProducts().forEach(p -> p.getTags().remove(tag));
        }

        tagRepository.delete(tag);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Tag.DELETE_TAG_SUCCESS);
    }
}
