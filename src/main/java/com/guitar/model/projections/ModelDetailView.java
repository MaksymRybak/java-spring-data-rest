package com.guitar.model.projections;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.guitar.model.Model;
import com.guitar.model.ModelType;

@Projection(name="modelDetailView", types = {Model.class})
public interface ModelDetailView {
	@Value(value = "#{target.name}")
	String getModelName();
	BigDecimal getPrice();
	@Value(value ="#{target.manufacturer.name}")
	String getManufacturer();
	ModelType getModelType();
	int getFrets();
	String getWoodType();
	
	@Value("#{target.manufacturer.name} #{target.name}")
	String getFullName();
}
