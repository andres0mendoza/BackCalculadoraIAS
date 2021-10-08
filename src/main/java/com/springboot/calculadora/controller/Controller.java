package com.springboot.calculadora.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.calculadora.model.ServicioModel;
import com.springboot.calculadora.repository.ServicioRepository;

@CrossOrigin(origins = "*")
@RestController
public class Controller {

	private static final Logger log = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private ServicioModel servicioModel;

	@Autowired
	private ServicioRepository servicioRepository;
	
	@PostMapping("/servicio")
	public ResponseEntity<ServicioModel> guardarServicio(@RequestBody ServicioModel servicio) {
		servicioModel = servicioRepository.save(servicio);
		log.info("Saved quote=" + servicioModel.toString());

		if (servicioModel != null)
			return ResponseEntity.status(HttpStatus.CREATED).body(servicioModel);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@GetMapping("/calculo")
	public ResponseEntity<Map<String, String>> calcularHoras(@RequestParam("idTecnico") String idTecnico,
															 @RequestParam("year") int year, @RequestParam("week") int week) {

		Date inicioSemana = getFechaInicio(year, week, "UTC");
		Date finSemana = getFechaFin(year, week, "UTC");

		// Obtenemos todos los servicios realizados en la semana
		List<ServicioModel> lstServicios = servicioRepository.findByIdTecnicoAndDateBetween(idTecnico,
				inicioSemana, finSemana);

		// Cambiamos nuevamente la zona horaria
		inicioSemana = getFechaInicio(year, week, "America/Bogota");
		finSemana = getFechaFin(year, week, "America/Bogota");

		// Ordenamos la lista por fecha inicial para calcular las fechas en orden
		lstServicios.sort(Comparator.comparing(ServicioModel::getFechaInicio));

		// Guardadas en minutos
		int minutosNormales = 0;
		int minutosNocturnos = 0;
		int minutosDominicales = 0;

		int minutosNormalesExtra = 0;
		int minutosNocturnosExtra = 0;
		int minutosDominicalesExtra = 0;

		int minutosTotales = 0;
		int minutosActuales = 0;

		int minsNormales;
		int minsExtra;
		int minsNocturnos;

		DateTime fechaInicial;
		DateTime fechaFinal;

		DateTime fechaInicialDia;
		DateTime fechaFinalDia;

		// Recorremos la lista de servicios encontrados en la semana
		for (ServicioModel servicio : lstServicios) {

			//Convertimos a instant para comparar
			Instant instantIni = servicio.getFechaInicio().toInstant();
			Instant instantFin = servicio.getFechaFin().toInstant();
			Instant instantIniSemana = inicioSemana.toInstant().minus(5, ChronoUnit.HOURS);
			Instant instantFinSemana = finSemana.toInstant().minus(5, ChronoUnit.HOURS);

			// Si la fecha inicial del servicio es menor a la fecha inicial de la semana
			if (instantIni.isBefore(instantIniSemana)) {
				fechaInicial = new DateTime(instantIniSemana.plus(5, ChronoUnit.HOURS).toString());
			} else {
				fechaInicial = new DateTime(instantIni.plus(5, ChronoUnit.HOURS).toString());
			}

			// Si la fecha final del servicio es mayor a la fecha final de la semana
			if (instantFin.isBefore(instantFinSemana)) {
				fechaFinal = new DateTime(instantFin.plus(5, ChronoUnit.HOURS).toString());
			} else {
				fechaFinal = new DateTime(instantFinSemana.plus(5, ChronoUnit.HOURS).toString());
			}

			/*VALIDACION HORAS*/
			//Obtenemos la fecha inicial y final del día
			fechaInicialDia = fechaInicial.withHourOfDay(7).withMinuteOfHour(0);
			fechaFinalDia = fechaFinal.withHourOfDay(20).withMinuteOfHour(0);

			//Si es domingo
			if(fechaInicial.getDayOfWeek() == 7) {

				minutosActuales = Minutes.minutesBetween(fechaInicial, fechaFinal).getMinutes();

				//Si ya excedió las 48 horas (2880 mins)
				if(minutosActuales+minutosTotales > 2880) {
					minsNormales = 2880 - minutosTotales;
					minsExtra = minutosActuales + minutosTotales - 2880;

					minutosDominicales += minsNormales;
					minutosDominicalesExtra += minsExtra;
				} else {
					minutosDominicales += minutosActuales;
				}

			} else { //De lunes a sábado

				DateTime fechaTempo = fechaFinal.withHourOfDay(7).withMinuteOfHour(0);
				DateTime fechaTempo1 = fechaInicial.withHourOfDay(20).withMinuteOfHour(0);

				if (fechaInicial.isBefore(fechaInicialDia)) { //Hora inicial antes de las 7am

					if (fechaFinal.isBefore(fechaInicialDia)) { //Hora final antes de las 7am
						minutosActuales = Minutes.minutesBetween(fechaInicial, fechaFinal).getMinutes();

						//Si ya excedió las 48 horas (2880 mins)
						if (minutosActuales + minutosTotales > 2880) {
							minsNormales = 2880 - minutosTotales;
							minsExtra = minutosActuales + minutosTotales - 2880;

							minutosNocturnos += minsNormales;
							minutosNocturnosExtra += minsExtra;
						} else {
							minutosNocturnos += minutosActuales;
						}
					} else { //Hora final despues de las 7am
						minutosActuales = Minutes.minutesBetween(fechaInicial, fechaInicialDia).getMinutes();

						//Si ya excedió las 48 horas (2880 mins)
						if (minutosActuales + minutosTotales > 2880) {
							minsNormales = 2880 - minutosTotales;
							minsExtra = minutosActuales + minutosTotales - 2880;

							minutosNocturnos += minsNormales;
							minutosNocturnosExtra += minsExtra;
						} else {
							minutosNocturnos += minutosActuales;
						}

						if (fechaFinal.isBefore(fechaFinalDia)) { //Hora final entre las 7am y las 8pm
							minutosActuales = Minutes.minutesBetween(fechaInicialDia, fechaFinal).getMinutes();

							//Si ya excedió las 48 horas (2880 mins)
							if (minutosActuales + minutosTotales > 2880) {
								minsNormales = 2880 - minutosTotales;
								minsExtra = minutosActuales + minutosTotales - 2880;

								minutosNormales += minsNormales;
								minutosNormalesExtra += minsExtra;
							} else {
								minutosNormales += minutosActuales;
							}
						} else { //Hora final despues de las 8pm
							minutosActuales = Minutes.minutesBetween(fechaInicialDia, fechaFinalDia).getMinutes();

							//Si ya excedió las 48 horas (2880 mins)
							if (minutosActuales + minutosTotales > 2880) {
								minsNormales = 2880 - minutosTotales;
								minsExtra = minutosActuales + minutosTotales - 2880;

								minutosNormales += minsNormales;
								minutosNormalesExtra += minsExtra;
							} else {
								minutosNormales += minutosActuales;
							}

							minutosActuales = Minutes.minutesBetween(fechaFinalDia, fechaFinal).getMinutes();

							//Si ya excedió las 48 horas (2880 mins)
							if (minutosActuales + minutosTotales > 2880) {
								minsNormales = 2880 - minutosTotales;
								minsExtra = minutosActuales + minutosTotales - 2880;

								minutosNocturnos += minsNormales;
								minutosNocturnosExtra += minsExtra;
							} else {
								minutosNocturnos += minutosActuales;
							}
						}
					}

				}else if(fechaFinal.isBefore(fechaTempo) || fechaFinal.isEqual(fechaTempo)){

					minutosActuales = Minutes.minutesBetween(fechaInicial, fechaFinal).getMinutes();

					if(minutosActuales+minutosTotales > 2880) {
						minsNormales = 2880 - minutosTotales;
						minsExtra = minutosActuales + minutosTotales - 2880;

						minutosNocturnos += minsNormales;
						minutosNocturnosExtra += minsExtra;
					} else {
						minutosNocturnos += minutosActuales;
					}

				} else if(fechaInicial.isAfter(fechaInicialDia) && fechaInicial.isBefore(fechaFinalDia)) {	//Hora inicial entre las 7am y 8pm
			//	} else if(fechaInicialDia.isAfter(fechaInicial) && fechaFinalDia.isBefore(fechaInicial)) {	//Hora inicial entre las 7am y 8pm

					if(fechaFinal.isBefore(fechaFinalDia)) { //Hora final entre las 7am y las 8pm
						minutosActuales = Minutes.minutesBetween(fechaInicial, fechaFinal).getMinutes();

						//Si ya excedió las 48 horas (2880 mins)
						if(minutosActuales+minutosTotales > 2880) {

							minsNocturnos = Minutes.minutesBetween(fechaTempo1, fechaTempo).getMinutes();
							minsNormales = minutosActuales - minsNocturnos;

							minutosNormales += minsNormales;
							minutosNocturnos += minsNocturnos;
/*
							minsNormales = 2880 - minutosTotales;
							minsExtra = minutosActuales + minutosTotales - 2880;

							minutosNormales += minsNormales;
							minutosNormalesExtra += minsExtra;
*/


						} else {
							int dias = minutosActuales/1440;
							int sobrante = minutosActuales % 1440;

							if(sobrante > 780)
								dias += 1;

							if(minutosActuales > 780){
								minutosNormales += minutosActuales;
								minutosNocturnos += ((dias) * 11) * 60;
								minutosNormales = minutosNormales - ((dias) * 11) * 60;
							}else{
								minutosNormales += minutosActuales;
							}

							//minutosNormales += minutosActuales;
						}
					} else { //Hora final despues de las 8pm
						minutosActuales = Minutes.minutesBetween(fechaInicial, fechaFinalDia).getMinutes();

						//Si ya excedió las 48 horas (2880 mins)
						if(minutosActuales+minutosTotales > 2880) {
							minsNormales = 2880 - minutosTotales;
							minsExtra = minutosActuales + minutosTotales - 2880;

							minutosNormales += minsNormales;
							minutosNormalesExtra += minsExtra;
						} else {
							minutosNormales += minutosActuales;
						}

						minutosActuales = Minutes.minutesBetween(fechaFinalDia, fechaFinal).getMinutes();

						//Si ya excedió las 48 horas (2880 mins)
						if(minutosActuales+minutosTotales > 2880) {
							minsNormales = 2880 - minutosTotales;
							minsExtra = minutosActuales + minutosTotales - 2880;

							minutosNocturnos += minsNormales;
							minutosNocturnosExtra += minsExtra;
						} else {
							minutosNocturnos += minutosActuales;
							//minutosNocturnos += minutosActuales;
						}
					}

				} else { //Hora inicial despues de las 8pm, por lo tanto, la fecha final también está después de las 8pm

					minutosActuales = Minutes.minutesBetween(fechaInicial, fechaFinal).getMinutes();

					//Si ya excedió las 48 horas (2880 mins)
					if(minutosActuales+minutosTotales > 2880) {
						minsNormales = 2880 - minutosTotales;
						minsExtra = minutosActuales + minutosTotales - 2880;

						minutosNocturnos += minsNormales;
						minutosNocturnosExtra += minsExtra;
					} else {
						DateTime fechaTemp = fechaInicial.withHourOfDay(20).withMinuteOfHour(0);
						if(fechaInicial.isBefore(fechaTemp)){
							minutosNormales += minutosActuales;
						}else{
							minutosNocturnos += minutosActuales;
						}

						//minutosNocturnos += minutosActuales;
					}

				}

			}

			minutosTotales += Minutes.minutesBetween(fechaInicial, fechaFinal).getMinutes();
		}

		//Verificar que no sean negativos
		if(minutosDominicales < 0) {
			minutosDominicalesExtra = minutosDominicalesExtra + minutosDominicales;
			minutosDominicales = 0;
		}

		if(minutosNormales < 0){
			minutosNormalesExtra = minutosNormalesExtra + minutosNormales;
			minutosNormales = 0;
		}

		if(minutosNocturnos < 0){
			minutosNocturnosExtra = minutosNocturnosExtra + minutosNocturnos;
			minutosNocturnos = 0;
		}

		int cont=0;
		while(minutosNocturnos + minutosNormales > 2880) {

			minutosNormalesExtra = minutosNormalesExtra + 60;
			minutosNormales = minutosNormales - 60;
			if (cont > 5) {
		//	if (cont > 2) {
				minutosNocturnosExtra = minutosNocturnosExtra + 60;
				minutosNocturnos = minutosNocturnos - 60;
			}
			cont++;
		}

		Map<String, String> resultado = new HashMap<String, String>();

		resultado.put("horasNormales", formatHoras(minutosNormales));
		resultado.put("horasNocturnas", formatHoras(minutosNocturnos));
		resultado.put("horasDominicales", formatHoras(minutosDominicales));

		resultado.put("horasNormalesExtra", formatHoras(minutosNormalesExtra));
		resultado.put("horasNocturnasExtra", formatHoras(minutosNocturnosExtra));
		resultado.put("horasDominicalesExtra", formatHoras(minutosDominicalesExtra));

		resultado.put("horasTotales", formatHoras(minutosTotales));

		return new ResponseEntity<Map<String, String>>(resultado, HttpStatus.OK);
	}
	
	private String formatHoras(int totalMinutos) {				
		int horas = totalMinutos / 60; 
		int minutos = totalMinutos % 60;
		
		return String.format("%02d:%02d", horas, minutos);		
	}

	private Date getFechaInicio(int year, int week, String tz) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.WEEK_OF_YEAR, week);

		// Cambiamos la zona horaria para consultar correctamente en mongo
		TimeZone timeZone = TimeZone.getTimeZone(tz);
		calendar.setTimeZone(timeZone);

		// Lunes es el primer día de la semana
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date fechaInicio = calendar.getTime();

		return fechaInicio;
	}

	private Date getFechaFin(int year, int week, String tz) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.WEEK_OF_YEAR, week);

		// Cambiamos la zona horaria para consultar correctamente en mongo
		TimeZone timeZone = TimeZone.getTimeZone(tz);
		calendar.setTimeZone(timeZone);

		// Lunes es el primer día de la semana
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.add(Calendar.DATE, 6);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date fechaFin = calendar.getTime();

		return fechaFin;
	}

}

