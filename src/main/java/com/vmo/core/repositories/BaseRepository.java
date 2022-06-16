package com.vmo.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
//public interface BaseRepository<Entity extends BaseEntity, ID> extends PagingAndSortingRepository<Entity, ID> {
//public interface BaseRepository<Entity, ID> extends JpaRepository<Entity, ID> {
public interface BaseRepository<Entity, ID> extends PagingAndSortingRepository<Entity, ID> {
    Entity findOneNotDeleted(ID id);

    void softDelete(ID id);
}
