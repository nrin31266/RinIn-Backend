package com.linkedin.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageableDto <T> {
    private List<T> content;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentSize;
    private Integer currentPage;
    private Boolean first;
    private Boolean last;
    private Boolean empty;
    private Boolean hasMore;
}
