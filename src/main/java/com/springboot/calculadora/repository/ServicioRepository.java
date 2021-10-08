package com.springboot.calculadora.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.calculadora.model.ServicioModel;

@Repository
public interface ServicioRepository extends MongoRepository<ServicioModel, String> {

	List<ServicioModel> findByIdTecnico(String idTecnico);

	@Query(value = "{'idTecnico':?0, $or: [ {'fechaInicio': {$gte:?1, $lte:?2}}, {'fechaFin': {$gte:?1, $lte:?2} } ] }")
	List<ServicioModel> findByIdTecnicoAndDateBetween(String idTecnico, Date fechaInicio, Date fechaFin);
}
