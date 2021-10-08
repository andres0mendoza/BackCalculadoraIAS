package com.springboot.calculadora.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.Query;

import com.springboot.calculadora.model.ServicioModel;

public interface CustomRepository {
	
	//List<ServicioModel> findByDateRange(Date fechaInicio, Date fechaFin);
	
	@Query(value = "{'champsDate':{ $gte: ?0, $lte: ?1}}")
	List<ServicioModel> findByDateBetween(Date fechaInicio, Date fechaFin);
}
