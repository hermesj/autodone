package de.uoc.dh.idh.autodone.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.uoc.dh.idh.autodone.entities.PostGroup;



public interface PostGroupRepository extends CrudRepository<PostGroup, Integer> {

    List<PostGroup> findByFacebookuserId(int userID);
    List<PostGroup> findAllByFbId(String fbId);

    PostGroup findByFbId(String fbId);
    PostGroup findByFbIdAndFacebookuser_Id(String fbId, int id);

    void deleteByFbId(String fbId);


}