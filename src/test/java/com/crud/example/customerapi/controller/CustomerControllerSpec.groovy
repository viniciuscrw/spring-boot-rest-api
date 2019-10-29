package com.crud.example.customerapi.controller


import com.crud.example.customerapi.model.Address
import com.crud.example.customerapi.model.Customer
import com.crud.example.customerapi.service.CustomerService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import java.time.LocalDate
import java.time.ZoneId

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import spock.lang.Specification

@AutoConfigureMockMvc
@WebMvcTest(controllers = [CustomerController])
class CustomerControllerSpec extends Specification {

    /*
        Demonstration of a unit test for the API using Spock BDD.
    */

    @Autowired
    MockMvc mvc

    Gson gson

    @SpringBean
    CustomerService customerService = Mock()

    def setup() {
        gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create()
    }

    def "get method performed on /customers endpoint"() {
        when: "fetching all customers"
        MvcResult result = mvc.perform(get('/customers')).andReturn()

        then: "the response status should be OK (200) and the response string should be empty since no customer is registered"
        result.getResponse().getStatus() == 200
        result.getResponse().getContentAsString() == ""
    }

    def "creating a new customer"() {
        given: "a customer being saved"
        customerService.save(_ as Customer) >> customer

        expect: "the status to be returned according to the customer in the request body"
        MvcResult result = mvc.perform(
                post('/customers')
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(gson.toJson(customer)))
                .andReturn()

        expectedStatus == result.getResponse().getStatus()

        where:
        customer                       | expectedStatus
        buildValidCustomer()           | 201
        buildInvalidCPFCustomer()      | 400
        buildEmptyFirstNameCustomer()  | 400
        buildNullDateOfBirthCustomer() | 400
    }


    Customer buildInvalidCPFCustomer() {
        Customer customer = buildValidCustomer()
        customer.setCpf("99999999999")

        customer
    }

    Customer buildEmptyFirstNameCustomer() {
        Customer customer = buildValidCustomer()
        customer.setFirstName("")

        customer
    }

    Customer buildNullDateOfBirthCustomer() {
        Customer customer = buildValidCustomer()
        customer.setDateOfBirth(null)

        customer
    }

    Customer buildValidCustomer() {
        Customer customer = new Customer()
        customer.setId(1L)
        customer.setFirstName("Joao Ricardo")
        customer.setLastName("Da Silva")
        customer.setEmail("jrdasilva@gmail.com")
        customer.setCpf("93350016006")
        customer.setDateOfBirth(Date.from(
                LocalDate.of(1989, 2, 22)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()))
        customer.setAddress(buildValidAddress())

        customer
    }

    Address buildValidAddress() {
        Address address = new Address()
        address.setId(1L)
        address.setStreet("Rua Jeronimo Pattaro")
        address.setComplement("AP 31")
        address.setNumber(71)
        address.setNeighbourhood("Vila Santa Isabel")
        address.setZipCode("13400111")
        address.setCity("Campinas")
        address.setUf("SP")

        return address
    }
}

