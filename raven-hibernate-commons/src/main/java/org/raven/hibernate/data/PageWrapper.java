package org.raven.hibernate.data;


import org.raven.commons.contract.PageResponse;
import org.raven.commons.util.CollectionUtils;
import org.raven.commons.util.Lists;
import org.springframework.data.domain.Page;

import java.util.function.Function;
import java.util.stream.Collectors;

public class PageWrapper {

    public static <T, R> PageResponse<R> wrapper(Page<T> page, Function<? super T, ? extends R> mapper) {
        return PageResponse.of(
                page.getPageable().getPageNumber() + 1,
                page.getPageable().getPageSize(),
                page.getTotalElements(),
                CollectionUtils.isEmpty(page.getContent()) ?
                        Lists.newArrayList() :
                        page.getContent().stream().map(mapper).collect(Collectors.toList())
        );
    }

    public static <T> PageResponse<T> wrapper(Page<T> page) {
        return PageResponse.of(
                page.getPageable().getPageNumber() + 1,
                page.getPageable().getPageSize(),
                page.getTotalElements(),
                page.getContent()
        );
    }

}
