package com.kshopov.spring.contactlist.contactapi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kshopov.spring.contactlist.contactapi.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, String>{
	Optional<Contact> findById(String id);
}
