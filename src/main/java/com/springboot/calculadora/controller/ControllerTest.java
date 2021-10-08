/*package com.springboot.calculadora.controller;

import com.springboot.calculadora.model.ServicioModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class ControllerTest {

    @Before
    public void init() {
        controller = new Controller();
        servicioModel = new ServicioModel();


    @Test
    void guardarServicio() {
        Controller controller = new Controller();
        ServicioModel servicion = new ServicioModel();

        when(controller.guardarServicio()).then(ResponseEntity());
    }

    @Test
    void calcularHoras() {

    }
}
*/

package com.springboot.calculadora.controller;

import com.springboot.calculadora.model.ServicioModel;
import com.springboot.calculadora.repository.ServicioRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class ControllerTest {

    private Controller controller;

    private ServicioModel servicioModel;
    private ServicioRepository servicioRepository;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setupMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    public void init() throws Exception {
        controller = new Controller();

        servicioModel = Mockito.mock(ServicioModel.class);
        servicioRepository = Mockito.mock(ServicioRepository.class);

        Mockito.when(controller.guardarServicio(servicioModel)).thenReturn(any());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("idTecnico", "pruebaUnitaria");
        params.add("year", "2021");
        params.add("week", "39");

        mockMvc.perform(MockMvcRequestBuilders.get("/calculo").params(params));

        when(controller.guardarServicio(servicioModel)).thenReturn(any());
        when(controller.calcularHoras("idTecnico", 1, 2)).thenReturn(any());
    }

    @Test
    public void guardarServicio() throws Exception {

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        String jsonStr = "{\"idTecnico\":\"prueba\",\"idServicio\":\"prueba\",\"fechaInicio\":\"2021-09-29T17:31\",\"fechaFin\":\"2021-09-30T17:31\"}";

        ServicioModel servicio = new ServicioModel();

        servicio.setIdServicio("PruebaUnitaria");
        servicio.setIdTecnico("PruebaUnitaria");
        servicio.setFechaInicio(new Date());
        servicio.setFechaFin(new Date());

        ServicioModel guarda = servicioRepository.save(servicio);

        System.out.println(guarda);

        when(servicioRepository.save(servicio)).thenReturn(any());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/servicio")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(jsonStr.getBytes()))
            .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void calcularHoras() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("idTecnico", "pruebaUnitaria");
        params.add("year", "2021");
        params.add("week", "39");

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/calculo")
                .params(params)).andDo(MockMvcResultHandlers.print());

        String json = mockMvc.toString();

        Assert.assertEquals(mockMvc.toString(), json);

    }


}
//