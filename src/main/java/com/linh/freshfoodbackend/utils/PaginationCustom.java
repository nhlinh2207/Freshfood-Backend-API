package com.linh.freshfoodbackend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationCustom {
    private int page;
    private int size;
    private String sort;

    private static Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    public static Pageable createPaginationCustom(int page, int size, String sortBy, String sortDir) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(getSortDirection(sortDir), sortBy));
        return PageRequest.of(page, size, Sort.by(orders));
    }
}
