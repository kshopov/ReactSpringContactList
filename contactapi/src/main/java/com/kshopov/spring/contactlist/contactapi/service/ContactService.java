package com.kshopov.spring.contactlist.contactapi.service;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.Sort.by;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.management.RuntimeErrorException;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kshopov.spring.contactlist.contactapi.model.Contact;
import com.kshopov.spring.contactlist.contactapi.repo.ContactRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class ContactService {
	private final String SORT_BY_NAME = "name";
	
	private final String CONTACT_NOT_FOUND = "Contact not found";
	
	private final String IMAGE_EXTENSION_PNG =  ".png";
	
	private final ContactRepository contactRepository;
	
	public ContactService(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}
	
	public Page<Contact> getAllContact(int page, int size) {
		return contactRepository.findAll(of(page, size, by(SORT_BY_NAME)));
	}
	
	public Contact getContact(String id) {
		return contactRepository.findById(id)
				.orElseThrow(() -> new RuntimeException(CONTACT_NOT_FOUND));
	}
	
	public Contact createContact(Contact contact) {
		return contactRepository.save(contact);
	}
	
	public void deleteContact(Contact contact) {
		contactRepository.delete(contact);
	}
	
	public String uploadPhoto(String id, MultipartFile file) {
		Contact contact = getContact(id);
		String photoUrl = savePhoto.apply(id, file);
		contact.setPhotoUrl(photoUrl);
		contactRepository.save(contact);
		
		return "";
	}
	
	private final Function<String, String> fileExtension = filename -> Optional.of(filename)
			.filter(name -> name.contains("."))
			.map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(IMAGE_EXTENSION_PNG);
	
	
	private final BiFunction<String, MultipartFile, String> savePhoto = (id, image) -> {
		String fileName = id + fileExtension.apply(image.getOriginalFilename());
		try {
			Path path = Paths.get("/").toAbsolutePath().normalize();
			if(!Files.exists(path)) {
				Files.createDirectories(path);
			}
			Files.copy(image.getInputStream(), path.resolve(fileName), REPLACE_EXISTING);
			
			return ServletUriComponentsBuilder.fromCurrentContextPath().path("/contact/image/" + fileName).toUriString();
		} catch (Exception e) {
			throw new RuntimeException("Unable to save image");
		}
	};
}
