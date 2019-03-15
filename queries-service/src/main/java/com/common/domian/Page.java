package com.common.domian;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author wanghongen
 * 2018/5/23
 */
@NoArgsConstructor
@Data
public class Page<E> implements Serializable {
    private Integer number;
    private Integer size;
    private Long totalElements;
    private List<E> content = Collections.emptyList();

    public static final Page EMPTY = new Page<>();

    static {
        EMPTY.setNumber(0);
        EMPTY.setSize(0);
        EMPTY.setTotalElements(0L);
    }

    public Page(int page, int size) {
        this.number = page;
        this.size = size;
    }

    public static <E> Page<E> empty() {
        return EMPTY;
    }
}
