/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 */
package rrangelo.customersatisfaction.tests.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerCreateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerUpdateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.customers.SatisfactionCreateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.responses.customers.CustomerFindCustomerResponseBean;
import rrangelo.customersatisfaction.config.IntegrationTestConfig;
import rrangelo.customersatisfaction.controllers.CustomerController;
import rrangelo.customersatisfaction.repositories.CustomerRepository;
import rrangelo.customersatisfaction.repositories.SatisfactionRepository;
import rrangelo.customersatisfaction.services.CustomerService;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
public class CustomerIntegrationTest extends IntegrationTestConfig {

    private String ENDPOINT = "/customer";
    
    private ObjectMapper mapper;

    @Autowired
    private CustomerController controller;

    @Autowired
    private CustomerService service;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private SatisfactionRepository satisfactionRepository;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        satisfactionRepository.findAll().stream()
                .forEach(satisfaction -> satisfactionRepository.delete(satisfaction));
        repository.findAll().stream()
                .forEach(customer -> repository.delete(customer));
    }

    @Test
    public void testSimpleSuccess() throws Exception {
        MvcResult result = null;
        CustomerFindCustomerResponseBean customer = null;
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerCreateCustomerRequestBean.builder()
                                                .email("pepe@email.com")
                                                .names("pancho")
                                                .satisfactions(new ArrayList<>())
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        result = mvc.perform(
                MockMvcRequestBuilders.get(ENDPOINT + "?email=pepe@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.content().json(
                        mapper.writeValueAsString(
                                CustomerFindCustomerResponseBean.builder()
                                        .email("pepe@email.com")
                                        .names("pancho")
                                        .satisfactions(new ArrayList<>())
                                        .build()
                        )
                )).andReturn();
        customer = mapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerFindCustomerResponseBean.class
        );
        mvc.perform(
                MockMvcRequestBuilders.put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerUpdateCustomerRequestBean.builder()
                                                .email(customer.getEmail())
                                                .names("pepe")
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSuccess() throws Exception {
        MvcResult result = null;
        CustomerFindCustomerResponseBean customer = null;
        List<SatisfactionCreateCustomerRequestBean> satisfactions = null;
        satisfactions = new ArrayList<>();
        satisfactions.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(2)
                        .build()
        );
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerCreateCustomerRequestBean.builder()
                                                .email("pepe@email.com")
                                                .names("pancho")
                                                .satisfactions(satisfactions)
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        result = mvc.perform(
                MockMvcRequestBuilders.get(ENDPOINT + "?email=pepe@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                ).andReturn();
        customer = mapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerFindCustomerResponseBean.class
        );
        mvc.perform(
                MockMvcRequestBuilders.put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerUpdateCustomerRequestBean.builder()
                                                .email(customer.getEmail())
                                                .names("pepe")
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateExistingCustomer() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerCreateCustomerRequestBean.builder()
                                                .email("pepe@email.com")
                                                .names("pepe")
                                                .satisfactions(new ArrayList<>())
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerCreateCustomerRequestBean.builder()
                                                .email("pepe@email.com")
                                                .names("pancho")
                                                .satisfactions(new ArrayList<>())
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testFindUnexistingCustomer() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.get(ENDPOINT + "?email=pancho@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateUnexistingCustomer() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerUpdateCustomerRequestBean.builder()
                                                .email("pancho@email.com")
                                                .names("pepe")
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
