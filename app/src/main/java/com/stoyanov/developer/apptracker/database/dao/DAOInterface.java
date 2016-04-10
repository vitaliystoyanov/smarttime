package com.stoyanov.developer.apptracker.database.dao;

public interface DAOInterface<E> extends CRUDInterface<E> {

    boolean deleteAll();

}
