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
package rrangelo.customersatisfaction.tests.units.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.CustomerCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.CustomerUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionFindSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.config.ServiceUnitTestConfig;
import rrangelo.customersatisfaction.documents.CustomerDocument;
import rrangelo.customersatisfaction.documents.SatisfactionDocument;
import rrangelo.customersatisfaction.exceptions.validations.SatisfactionValidationException;
import rrangelo.customersatisfaction.repositories.CustomerRepository;
import rrangelo.customersatisfaction.repositories.SatisfactionRepository;
import rrangelo.customersatisfaction.services.CustomerService;
import rrangelo.customersatisfaction.services.SatisfactionService;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
public class SatisfactionServiceUnitTest extends ServiceUnitTestConfig {

    @Autowired
    private SatisfactionService service;

    @Autowired
    private CustomerService customerService;

    @MockBean
    private SatisfactionRepository repository;

    @MockBean
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCreateSuccess() {
        CustomerDocument customer = null;
        Mockito.when(customerRepository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);
        customer = CustomerDocument.builder()
                .id(ObjectId.get().toString())
                .email("pepe@email.com")
                .names("pepe")
                .satisfactions(new ArrayList<>())
                .build();
        Mockito.when(customerRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(customer));
        Mockito.when(repository.save(Mockito.any(SatisfactionDocument.class)))
                .thenReturn(Mockito.any(SatisfactionDocument.class));
        Assertions.assertDoesNotThrow(
                () -> {
                    service.create(
                            SatisfactionCreateSatisfactionRequestBean.builder()
                                    .qualification(1)
                                    .customer(
                                            CustomerCreateSatisfactionRequestBean.builder()
                                                    .email("pepe@email.com")
                                                    .build()
                                    )
                                    .build()
                    );
                }
        );
    }

    @Test
    public void testCreateUnexistsCustomer() {
        SatisfactionValidationException assertThrows = null;
        Mockito.when(customerRepository.existsByEmail(Mockito.anyString()))
                .thenReturn(false);
        assertThrows = Assertions.assertThrows(
                SatisfactionValidationException.class,
                () -> {
                    service.create(
                            SatisfactionCreateSatisfactionRequestBean.builder()
                                    .qualification(1)
                                    .customer(
                                            CustomerCreateSatisfactionRequestBean.builder()
                                                    .email("pepe@email.com")
                                                    .build()
                                    )
                                    .build()
                    );
                }
        );
        Assertions.assertNotNull(assertThrows);
    }

    @Test
    public void testFindSuccess() {
        CustomerDocument customer = null;
        List<SatisfactionDocument> satisfactions = null;
        customer = CustomerDocument.builder()
                .id(ObjectId.get().toString())
                .email("pepe@email.com")
                .names("pepe")
                .satisfactions(new ArrayList<>())
                .build();
        satisfactions = new ArrayList<>();
        Mockito.when(
                repository.findByDateBetween(
                        Mockito.any(LocalDate.class),
                        Mockito.any(LocalDate.class)
                )
        ).thenReturn(satisfactions);
        Assertions.assertDoesNotThrow(
                () -> {
                    service.find(
                            SatisfactionFindSatisfactionRequestBean.builder()
                                    .startDate(LocalDate.now().minusDays(5))
                                    .endDate(LocalDate.now().plusDays(1))
                                    .build()
                    );
                }
        );
    }

    @Test
    public void testFindWrongPeriod() {
        SatisfactionValidationException assertThrows = null;
        assertThrows = Assertions.assertThrows(
                SatisfactionValidationException.class,
                () -> {
                    service.find(
                            SatisfactionFindSatisfactionRequestBean.builder()
                                    .startDate(LocalDate.now())
                                    .endDate(LocalDate.now().minusDays(5))
                                    .build()
                    );
                }
        );
        Assertions.assertNotNull(assertThrows);
    }

    @Test
    public void testUpdateSuccess() {
        SatisfactionDocument satisfaction = null;
        Mockito.when(customerRepository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);
        Mockito.when(repository.existsByCode(Mockito.anyLong()))
                .thenReturn(true);
        satisfaction = SatisfactionDocument.builder()
                .id(ObjectId.get().toString())
                .code(System.currentTimeMillis())
                .date(LocalDate.now().minusDays(2))
                .qualification(1)
                .customer(
                        CustomerDocument.builder()
                                .id(ObjectId.get().toString())
                                .email("pepe@email.com")
                                .names("pepe")
                                .satisfactions(new ArrayList<>())
                                .build()
                )
                .build();
        Mockito.when(repository.findByCode(Mockito.anyLong()))
                .thenReturn(Optional.of(satisfaction));
        Assertions.assertDoesNotThrow(
                () -> {
                    service.update(
                            SatisfactionUpdateSatisfactionRequestBean.builder()
                                    .code(System.currentTimeMillis())
                                    .qualification(1)
                                    .customer(
                                            CustomerUpdateSatisfactionRequestBean.builder()
                                                    .email("pepe@email.com")
                                                    .build()
                                    )
                                    .build()
                    );
                }
        );
    }

    @Test
    public void testUpdateUnexistsCustomer() {
        SatisfactionValidationException assertThrows = null;
        Mockito.when(customerRepository.existsByEmail(Mockito.anyString()))
                .thenReturn(false);
        assertThrows = Assertions.assertThrows(
                SatisfactionValidationException.class,
                () -> {
                    service.update(
                            SatisfactionUpdateSatisfactionRequestBean.builder()
                                    .code(System.currentTimeMillis())
                                    .qualification(1)
                                    .customer(
                                            CustomerUpdateSatisfactionRequestBean.builder()
                                                    .email("pepe@email.com")
                                                    .build()
                                    )
                                    .build()
                    );
                }
        );
        Assertions.assertNotNull(assertThrows);
    }

    @Test
    public void testUpdateUnexistsSatisfaction() {
        SatisfactionValidationException assertThrows = null;
        Mockito.when(customerRepository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);
        Mockito.when(repository.existsByCode(Mockito.anyLong()))
                .thenReturn(false);
        assertThrows = Assertions.assertThrows(
                SatisfactionValidationException.class,
                () -> {
                    service.update(
                            SatisfactionUpdateSatisfactionRequestBean.builder()
                                    .code(System.currentTimeMillis())
                                    .qualification(1)
                                    .customer(
                                            CustomerUpdateSatisfactionRequestBean.builder()
                                                    .email("pepe@email.com")
                                                    .build()
                                    )
                                    .build()
                    );
                }
        );
        Assertions.assertNotNull(assertThrows);
    }

}
