package com.springboot.calculadora.repository;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.springboot.calculadora.model.ServicioModel;

@Component
public class CustomRepositoryImpl  {

	@Autowired
	MongoTemplate mongoTemplate;
	

}
