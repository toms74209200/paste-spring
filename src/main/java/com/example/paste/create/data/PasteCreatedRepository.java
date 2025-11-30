package com.example.paste.create.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasteCreatedRepository extends JpaRepository<PasteCreated, String> {}
