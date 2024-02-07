package de.uoc.dh.idh.autodone.base;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean()
public interface BaseRepository<T> extends CrudRepository<T, UUID>, PagingAndSortingRepository<T, UUID> {
}
