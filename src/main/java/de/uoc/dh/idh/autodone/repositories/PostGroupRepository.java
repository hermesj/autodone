package de.uoc.dh.idh.autodone.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.uoc.dh.idh.autodone.entities.PostGroup;



public interface PostGroupRepository extends CrudRepository<PostGroup, Integer> {

    List<PostGroup> findByMastodonuserId(int userID);
    List<PostGroup> findAllByMstdId(String mstdId);

    PostGroup findByMstdId(String mstdId);
    PostGroup findByMstdIdAndMastodonuserId(String mstdId, int id);

    void deleteByMstdId(String mstdId);


}