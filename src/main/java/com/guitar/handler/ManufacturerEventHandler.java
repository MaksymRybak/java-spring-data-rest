package com.guitar.handler;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.guitar.model.Manufacturer;

@Component
@RepositoryEventHandler(Manufacturer.class)
public class ManufacturerEventHandler {
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@HandleBeforeCreate
	public void HandleBeforeCreate(Manufacturer manufacturer) {
		// questo metodo viene chiamato prima di salvare oggetto di tipo Manufacturer
		// salvataggio nel livello di persistenza
		// possiamo validare il nostro oggetto in modo che vogliamo
		// etc...
		
		if (!manufacturer.getActive()) {
			throw new IllegalArgumentException("New Manufacture must be active");
		}
	}
}
