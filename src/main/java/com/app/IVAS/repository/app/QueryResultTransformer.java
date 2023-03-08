package com.app.IVAS.repository.app;

public interface QueryResultTransformer<E, T> {

    T transaform(E e);
}
