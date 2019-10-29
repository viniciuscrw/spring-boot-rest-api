package com.crud.example.customerapi.controller;

import com.crud.example.customerapi.CustomerApiApplication;
import com.crud.example.customerapi.model.Address;
import com.crud.example.customerapi.model.Customer;
import com.crud.example.customerapi.service.CustomerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomerApiApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CustomerService customerService;

    private Address mockAddress;
    private Customer mockCustomer;
    private Gson gson;

    @Before
    public void setUp() {
        mockAddress = setUpAddress();
        mockCustomer = setUpCustomer(mockAddress);
        gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    }

    @Test
    public void retrieveAllCustomers() throws Exception {
        Mockito.when(customerService.findAll()).thenReturn(Collections.singletonList(mockCustomer));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/customers")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = gson.toJson(Collections.singletonList(mockCustomer));

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString() , true);
        Assert.assertEquals(HTTP_OK, result.getResponse().getStatus());
    }

    @Test
    public void retrieveACustomerByCPF() throws Exception {
        Mockito.when(customerService.findByDocument(Mockito.anyString()))
                .thenReturn(Optional.of(mockCustomer));

        String cpf = "93350016006";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/customers?cpf=" + cpf);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String jsonCustomer = gson.toJson(Collections.singletonList(mockCustomer));

        JSONAssert.assertEquals(jsonCustomer, result.getResponse().getContentAsString() , true);
        Assert.assertEquals(HTTP_OK, result.getResponse().getStatus());
    }

    @Test
    public void retrieveACustomerByID() throws Exception {
        Mockito.when(customerService.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(mockCustomer));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/customers/1")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String jsonCustomer = gson.toJson(mockCustomer);

        JSONAssert.assertEquals(jsonCustomer, result.getResponse().getContentAsString() , true);
        Assert.assertEquals(HTTP_OK, result.getResponse().getStatus());
    }

    @Test
    public void createANewCustomer() throws Exception {
        Mockito.when(customerService.save(Mockito.any(Customer.class)))
                .thenReturn(mockCustomer);

        String jsonCustomer = gson.toJson(mockCustomer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/customers/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonCustomer);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        JSONAssert.assertEquals(jsonCustomer, result.getResponse().getContentAsString() , true);
        Assert.assertEquals(HTTP_CREATED, result.getResponse().getStatus());
    }

    @Test
    public void createANewCustomerWithInvalidCPF() throws Exception {
        mockCustomer.setCpf("0000111000");
        Mockito.when(customerService.save(Mockito.any(Customer.class)))
                .thenReturn(mockCustomer);

        String jsonCustomer = gson.toJson(mockCustomer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/customers/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonCustomer);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.assertEquals(HTTP_BAD_REQUEST, result.getResponse().getStatus());
    }

    @Test
    public void updateAnExistingCustomer() throws Exception {
        Mockito.when(customerService.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(mockCustomer));

        Mockito.when(customerService.save(Mockito.any(Customer.class)))
                .thenReturn(mockCustomer);

        String jsonCustomer = gson.toJson(mockCustomer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonCustomer);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.assertEquals(HTTP_OK, result.getResponse().getStatus());
    }

    @Test
    public void updateANonExistingCustomer() throws Exception {
        Mockito.when(customerService.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        String jsonCustomer = gson.toJson(mockCustomer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonCustomer);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.assertEquals(HTTP_NOT_FOUND, result.getResponse().getStatus());
    }

    @Test
    public void deleteACustomerWithNoAuthenticationConfig() throws Exception {
        Mockito.when(customerService.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(mockCustomer));

        String jsonCustomer = gson.toJson(mockCustomer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/customers/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonCustomer);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.assertEquals(HTTP_UNAUTHORIZED, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void deleteACustomer() throws Exception {
        Mockito.when(customerService.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(mockCustomer));

        String jsonCustomer = gson.toJson(mockCustomer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/customers/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonCustomer);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.assertEquals(HTTP_OK, result.getResponse().getStatus());
    }
    
    private Address setUpAddress() {
        Address address = new Address();
        address.setId(1L);
        address.setStreet("Rua Jeronimo Pattaro");
        address.setComplement("AP 31");
        address.setNumber(71);
        address.setNeighbourhood("Vila Santa Isabel");
        address.setZipCode("13400111");
        address.setCity("Campinas");
        address.setUf("SP");
        
        return address;
    }
    
    private Customer setUpCustomer(Address address) {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Joao Ricardo");
        customer.setLastName("Da Silva");
        customer.setEmail("jrdasilva@gmail.com");
        customer.setCpf("93350016006");
        customer.setDateOfBirth(Date.from(
                LocalDate.of(1989, 2 ,22)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()));
        customer.setAddress(address);

        return customer;
    }

}