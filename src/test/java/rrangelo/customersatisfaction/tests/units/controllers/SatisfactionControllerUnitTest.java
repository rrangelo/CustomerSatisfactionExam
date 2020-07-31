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
package rrangelo.customersatisfaction.tests.units.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import rrangelo.customersatisfaction.beans.requests.satisfactions.CustomerCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.CustomerUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionFindSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.responses.satisfactions.CustomerFindSatisfactionResponseBean;
import rrangelo.customersatisfaction.beans.responses.satisfactions.SatisfactionFindSatisfactionResponseBean;
import rrangelo.customersatisfaction.config.ControllerUnitTestConfig;
import rrangelo.customersatisfaction.controllers.SatisfactionController;
import rrangelo.customersatisfaction.exceptions.responses.SatisfactionResponseException;
import rrangelo.customersatisfaction.services.SatisfactionService;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
@WebMvcTest(SatisfactionController.class)
public class SatisfactionControllerUnitTest extends ControllerUnitTestConfig {

    private String ENDPOINT = "/satisfaction";
    
    private ObjectMapper mapper;

    @MockBean
    private SatisfactionService service;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCreateSuccess() throws Exception {
        Mockito.doNothing()
                .when(service)
                .create(Mockito.any(SatisfactionCreateSatisfactionRequestBean.class));
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        SatisfactionCreateSatisfactionRequestBean.builder()
                                                .qualification(1)
                                                .customer(
                                                        CustomerCreateSatisfactionRequestBean.builder()
                                                                .email("pepe@email.com")
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateException() throws Exception {
        Mockito.doThrow(SatisfactionResponseException.class)
                .when(service)
                .create(Mockito.any(SatisfactionCreateSatisfactionRequestBean.class));
        mvc.perform(
                MockMvcRequestBuilders.post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        SatisfactionCreateSatisfactionRequestBean.builder()
                                                .qualification(1)
                                                .customer(
                                                        CustomerCreateSatisfactionRequestBean.builder()
                                                                .email("pepe@email.com")
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testFindSuccess() throws Exception {
        List<SatisfactionFindSatisfactionResponseBean> satisfactions = null;
        satisfactions = new ArrayList<>();
        satisfactions.add(
                SatisfactionFindSatisfactionResponseBean.builder()
                        .code(System.currentTimeMillis())
                        .date(LocalDate.now().minusDays(5).toString())
                        .qualification(1)
                        .customer(
                                CustomerFindSatisfactionResponseBean.builder()
                                        .email("pepe@email.com")
                                        .names("pepe")
                                        .build()
                        )
                        .build()
        );
        Mockito.doReturn(satisfactions)
                .when(service)
                .find(Mockito.any(SatisfactionFindSatisfactionRequestBean.class));
        mvc.perform(
                MockMvcRequestBuilders.get(
                        ENDPOINT
                        + "?start="
                        + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now().minusDays(5))
                        + "&end="
                        + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now().plusDays(1))
                ).contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFindException() throws Exception {
        Mockito.doThrow(SatisfactionResponseException.class)
                .when(service)
                .find(Mockito.any(SatisfactionFindSatisfactionRequestBean.class));
        mvc.perform(
                MockMvcRequestBuilders.get(
                        ENDPOINT
                        + "?start="
                        + LocalDate.now().minusDays(5)
                        + "&end="
                        + LocalDate.now()
                ).contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        Mockito.doNothing()
                .when(service)
                .update(Mockito.any(SatisfactionUpdateSatisfactionRequestBean.class));
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
                                                                .email("pepe@email.com")
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateException() throws Exception {
        Mockito.doThrow(SatisfactionResponseException.class)
                .when(service)
                .update(Mockito.any(SatisfactionUpdateSatisfactionRequestBean.class));
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
                                                                .email("pepe@email.com")
                                                                .build()
                                                )
                                                .build()
                                )
                        )
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
