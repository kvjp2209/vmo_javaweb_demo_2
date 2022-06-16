package com.vmo.core.common.utils.tuple;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Quintuple<T1, T2, T3, T4, T5> extends Quadruple<T1, T2, T3, T4> { //5-tuple
    private T5 fifth;

    public Quintuple(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        super(t1, t2, t3, t4);
        fifth = t5;
    }
}
