package com.vmo.core.common.utils.tuple;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Triple<T1, T2, T3> extends Pair<T1, T2> {
    private T3 third;

    public Triple(T1 t1, T2 t2, T3 t3) {
        super(t1, t2);
        this.third = t3;
    }
}
