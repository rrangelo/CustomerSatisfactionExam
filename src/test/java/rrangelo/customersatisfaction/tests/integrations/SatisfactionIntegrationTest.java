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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
import rrangelo.customersatisfaction.beans.requests.customers.SatisfactionCreateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.CustomerCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.CustomerUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.responses.customers.CustomerFindCustomerResponseBean;
import rrangelo.customersatisfaction.beans.responses.satisfactions.SatisfactionFindSatisfactionResponseBean;
import rrangelo.customersatisfaction.config.IntegrationTestConfig;
import rrangelo.customersatisfaction.controllers.SatisfactionController;
import rrangelo.customersatisfaction.repositories.CustomerRepository;
import rrangelo.customersatisfaction.repositories.SatisfactionRepository;
import rrangelo.customersatisfaction.services.SatisfactionService;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
public class SatisfactionIntegrationTest extends IntegrationTestConfig {

    private String ENDPOINT = "/satisfaction";

    private String ENDPOINT_CUSTOMER = "/customer";

    private List<CustomerFindCustomerResponseBean> documents;

    private ObjectMapper mapper;

    private String requestJson;

    @Autowired
    private SatisfactionController controller;

    @Autowired
    private SatisfactionService service;

    @Autowired
    private SatisfactionRepository repository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        repository.findAll().stream()
                .forEach(satisfaction -> repository.delete(satisfaction));
        customerRepository.findAll().stream()
                .forEach(customer -> customerRepository.delete(customer));
    }

    @Test
    public void testSuccesss() throws Exception {
        MvcResult result = null;
        List<SatisfactionFindSatisfactionResponseBean> satisfactions = null;
        List<SatisfactionCreateCustomerRequestBean> satisfactionsCustomer = null;
        SatisfactionFindSatisfactionResponseBean satisfaction = null;
        satisfactionsCustomer = new ArrayList<>();
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(7)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(3)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(5)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(7)
                        .build()
        );
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT_CUSTOMER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerCreateCustomerRequestBean.builder()
                                                .email("pepe@email.com")
                                                .names("pepe")
                                                .satisfactions(
                                                        Arrays.asList(
                                                                SatisfactionCreateCustomerRequestBean.builder()
                                                                        .qualification(7)
                                                                        .build(),
                                                                SatisfactionCreateCustomerRequestBean.builder()
                                                                        .qualification(3)
                                                                        .build(),
                                                                SatisfactionCreateCustomerRequestBean.builder()
                                                                        .qualification(5)
                                                                        .build(),
                                                                SatisfactionCreateCustomerRequestBean.builder()
                                                                        .qualification(7)
                                                                        .build()
                                                        )
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        result = mvc.perform(
                MockMvcRequestBuilders
                        .get(
                                ENDPOINT
                                + "?start="
                                + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now().minusDays(5))
                                + "&end="
                                + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now().plusDays(1))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                ).andReturn();
        log.info(result.getResponse().getContentAsString());
        satisfactions = (ArrayList<SatisfactionFindSatisfactionResponseBean>) mapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<ArrayList<SatisfactionFindSatisfactionResponseBean>>() {
        }
        ).stream().collect(Collectors.toList());
        satisfaction = (SatisfactionFindSatisfactionResponseBean) satisfactions.stream().findFirst().get();
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        SatisfactionCreateSatisfactionRequestBean.builder()
                                                .qualification(11)
                                                .customer(
                                                        CustomerCreateSatisfactionRequestBean.builder()
                                                                .email(satisfaction.getCustomer().getEmail())
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        mvc.perform(
                MockMvcRequestBuilders.put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        SatisfactionUpdateSatisfactionRequestBean.builder()
                                                .code(satisfaction.getCode())
                                                .qualification(2)
                                                .customer(
                                                        CustomerUpdateSatisfactionRequestBean.builder()
                                                                .email(satisfaction.getCustomer().getEmail())
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateUnexistsCustomer() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        SatisfactionCreateSatisfactionRequestBean.builder()
                                                .qualification(11)
                                                .customer(
                                                        CustomerCreateSatisfactionRequestBean.builder()
                                                                .email("pancho@email.com")
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testFindWrongPeriod() throws Exception {
        List<SatisfactionCreateCustomerRequestBean> satisfactionsCustomer = null;
        satisfactionsCustomer = new ArrayList<>();
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(7)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(3)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(5)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(7)
                        .build()
        );
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT_CUSTOMER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerCreateCustomerRequestBean.builder()
                                                .email("pepe@email.com")
                                                .names("pepe")
                                                .satisfactions(satisfactionsCustomer)
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        mvc.perform(
                MockMvcRequestBuilders
                        .get(
                                ENDPOINT
                                + "?start="
                                + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now())
                                + "&end="
                                + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now().minusDays(5))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateWrongCode() throws Exception {
        MvcResult result = null;
        List<SatisfactionFindSatisfactionResponseBean> satisfactions = null;
        List<SatisfactionCreateCustomerRequestBean> satisfactionsCustomer = null;
        SatisfactionFindSatisfactionResponseBean satisfaction = null;
        satisfactionsCustomer = new ArrayList<>();
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(7)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(3)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(5)
                        .build()
        );
        satisfactionsCustomer.add(
                SatisfactionCreateCustomerRequestBean.builder()
                        .qualification(7)
                        .build()
        );
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT_CUSTOMER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        CustomerCreateCustomerRequestBean.builder()
                                                .email("pepe@email.com")
                                                .names("pepe")
                                                .satisfactions(satisfactionsCustomer)
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        result = mvc.perform(
                MockMvcRequestBuilders
                        .get(
                                ENDPOINT
                                + "?start="
                                + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now().minusDays(5))
                                + "&end="
                                + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now().plusDays(1))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                ).andReturn();
        satisfactions = (ArrayList<SatisfactionFindSatisfactionResponseBean>) mapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<ArrayList<SatisfactionFindSatisfactionResponseBean>>() {
        }
        ).stream().collect(Collectors.toList());
        satisfaction = (SatisfactionFindSatisfactionResponseBean) satisfactions.stream().findFirst().get();
        mvc.perform(
                MockMvcRequestBuilders.put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        SatisfactionUpdateSatisfactionRequestBean.builder()
                                                .code(System.currentTimeMillis())
                                                .qualification(2)
                                                .customer(
                                                        CustomerUpdateSatisfactionRequestBean.builder()
                                                                .email(satisfaction.getCustomer().getEmail())
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateUnexistsCustomer() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        SatisfactionUpdateSatisfactionRequestBean.builder()
                                                .code(System.currentTimeMillis())
                                                .qualification(1)
                                                .customer(
                                                        CustomerUpdateSatisfactionRequestBean.builder()
                                                                .email("pancho@email.com")
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
