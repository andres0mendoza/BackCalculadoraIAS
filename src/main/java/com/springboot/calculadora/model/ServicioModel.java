package com.springboot.calculadora.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

@Repository
@Document(collection = "Servicios")
public class ServicioModel  {

	@Id
	private String id;
	
	@NotNull(message = "El id del técnico no puede ser nulo")
	private String idTecnico;
	
	@NotNull(message = "El id del servicio no puede ser nulo")
	private String idServicio;
	
	@NotNull(message = "La fecha inicial no puede ser nula")
	private Date fechaInicio;
	
	@NotNull(message = "La fecha final no puede ser nula")
	private Date fechaFin;
			
	public ServicioModel() {
		super();
	}
	
	public ServicioModel(String idTecnico, String idServicio, Date fechaInicio, Date fechaFin) {
		super();
		this.idTecnico = idTecnico;
		this.idServicio = idServicio;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdTecnico() {
		return idTecnico;
	}

	public void setIdTecnico(String idTecnico) {
		this.idTecnico = idTecnico;
	}

	public String getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(String idServicio) {
		this.idServicio = idServicio;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	@Override
	public String toString() {
		return "ServicioModel [id=" + id + ", idTecnico=" + idTecnico + ", idServicio=" + idServicio + ", fechaInicio="
				+ fechaInicio + ", fechaFin=" + fechaFin + "]";
	}
	
}
