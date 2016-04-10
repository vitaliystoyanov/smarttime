package com.stoyanov.developer.apptracker.database.dao;

import java.util.List;

public interface CRUDInterface<E> {

    long create(E entity);

    List<E> retrieveAll();

    long update(E entity);

    int delete(E entity);

}
