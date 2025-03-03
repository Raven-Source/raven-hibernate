package org.raven.hibernate.data;


import org.raven.commons.contract.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableWrapper {

    public static Pageable wrapper(PageRequest pageRequest) {
        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage() <= 0 ? 0 : pageRequest.getPage() - 1,
                pageRequest.getSize() <= 0 ? 10 : pageRequest.getSize()
        );
    }

    public static Pageable wrapper(PageRequest pageRequest, Sort sort) {
        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage() <= 0 ? 0 : pageRequest.getPage() - 1,
                pageRequest.getSize() <= 0 ? 10 : pageRequest.getSize(),
                sort
        );
    }

}
