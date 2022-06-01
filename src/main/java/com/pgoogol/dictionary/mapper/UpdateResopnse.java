package com.pgoogol.dictionary.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateResopnse<T> {

    private T value;
    private boolean update;

}
