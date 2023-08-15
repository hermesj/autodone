package de.uoc.dh.idh.autodone.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.uoc.dh.idh.autodone.entities.ImageFile;

public interface ImageRepository extends JpaRepository<ImageFile, String> {


    List<ImageFile> findAllByFacebookpost_Id(int id);
}
