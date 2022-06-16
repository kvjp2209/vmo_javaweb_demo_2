package com.vmo.core.common.utils.tuple;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Quadruple<T1, T2, T3, T4> extends Triple<T1, T2, T3> { //4-tuple
    private T4 fourth;

    public Quadruple(T1 t1, T2 t2, T3 t3, T4 t4) {
        super(t1, t2, t3);
        fourth = t4;
    }
}
