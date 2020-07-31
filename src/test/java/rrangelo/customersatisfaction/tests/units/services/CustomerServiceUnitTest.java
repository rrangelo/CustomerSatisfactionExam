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
import rrangelo.customersatisfaction.beans.requests.customers.CustomerCreateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerFindCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerUpdateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.customers.SatisfactionCreateCustomerRequestBean;
import rrangelo.customersatisfaction.config.ServiceUnitTestConfig;
import rrangelo.customersatisfaction.documents.CustomerDocument;
import rrangelo.customersatisfaction.documents.SatisfactionDocument;
import rrangelo.customersatisfaction.exceptions.validations.CustomerValidationException;
import rrangelo.customersatisfaction.repositories.CustomerRepository;
import rrangelo.customersatisfaction.repositories.SatisfactionRepository;
import rrangelo.customersatisfaction.services.CustomerService;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
public class CustomerServiceUnitTest extends ServiceUnitTestConfig {

    @Autowired
    private CustomerService service;

    @MockBean
    private CustomerRepository repository;

    @MockBean
    private SatisfactionRepository satisfactionRepository;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSimpleCreateSuccess() {
        Mockito.when(repository.save(Mockito.any(CustomerDocument.class)))
                .thenReturn(Mockito.any(CustomerDocument.class));
        Assertions.assertDoesNotThrow(
                () -> {
                    service.create(
                            CustomerCreateCustomerRequestBean.builder()
                                    .email("pepe@email.com")
                                    .names("Pepe")
                                    .satisfactions(new ArrayList<>())
                                    .build()
                    );
                }
        );
    }

    @Test
    public void testCreateSuccess() {
        Mockito.when(repository.save(Mockito.any(CustomerDocument.class)))
                .thenReturn(Mockito.any(CustomerDocument.class));
        Assertions.assertDoesNotThrow(
                () -> {
                    List<SatisfactionCreateCustomerRequestBean> satisfactions = null;
                    satisfactions = new ArrayList<>();
                    satisfactions.add(
                            SatisfactionCreateCustomerRequestBean.builder()
                                    .qualification(1)
                                    .build()
                    );
                    service.create(
                            CustomerCreateCustomerRequestBean.builder()
                                    .email("pepe@email.com")
                                    .names("Pepe")
                                    .satisfactions(satisfactions)
                                    .build()
                    );
                }
        );
    }

    @Test
    public void testCreateExistCustomer() {
        CustomerValidationException assertThrows = null;
        Mockito.when(repository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);
        assertThrows = Assertions.assertThrows(
                CustomerValidationException.class,
                () -> {
                    service.create(
                            CustomerCreateCustomerRequestBean.builder()
                                    .email("pepe@email.com")
                                    .names("Pepe")
                                    .satisfactions(new ArrayList<>())
                                    .build()
                    );
                }
        );
        Assertions.assertNotNull(assertThrows);
    }

    @Test
    public void testSimpleFindSuccess() {
        CustomerDocument customer = null;
        List<SatisfactionDocument> satisfactions = null;
        customer = CustomerDocument.builder()
                .email("pepe@email.com")
                .names("pepe")
                .id(ObjectId.get().toString())
                .satisfactions(new ArrayList<>())
                .build();
        satisfactions = new ArrayList<>();
        customer.setSatisfactions(satisfactions);
        Mockito.when(repository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);
        Mockito.when(repository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(customer));
        Mockito.when(satisfactionRepository.existsByCustomer(Mockito.anyString()))
                .thenReturn(false);
        Assertions.assertDoesNotThrow(
                () -> {
                    service.find(
                            CustomerFindCustomerRequestBean.builder()
                                    .email("pepe@email.com")
                                    .build()
                    );
                }
        );
    }

    @Test
    public void testFindSuccess() {
        CustomerDocument customer = null;
        List<SatisfactionDocument> satisfactions = null;
        Mockito.when(repository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);
        customer = CustomerDocument.builder()
                .email("pepe@email.com")
                .names("pepe")
                .id(ObjectId.get().toString())
                .satisfactions(new ArrayList<>())
                .build();
        satisfactions = new ArrayList<>();
        satisfactions.add(
                SatisfactionDocument.builder()
                        .code(System.currentTimeMillis())
                        .date(LocalDate.now().minusDays(5))
                        .id(ObjectId.get().toString())
                        .customer(customer)
                        .build()
        );
        customer.setSatisfactions(satisfactions);
        Mockito.when(repository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(customer));
        Mockito.when(satisfactionRepository.existsByCustomer(Mockito.anyString()))
                .thenReturn(true);
        Mockito.when(satisfactionRepository.findAllByCustomer(Mockito.anyString()))
                .thenReturn(satisfactions);
        Assertions.assertDoesNotThrow(
                () -> {
                    service.find(
                            CustomerFindCustomerRequestBean.builder()
                                    .email("pepe@email.com")
                                    .build()
                    );
                }
        );
    }

    @Test
    public void testFindUnexistCustomer() {
        CustomerValidationException assertThrows = null;
        Mockito.when(repository.existsByEmail(Mockito.anyString()))
                .thenReturn(false);
        assertThrows = Assertions.assertThrows(
                CustomerValidationException.class,
                () -> {
                    service.find(
                            CustomerFindCustomerRequestBean.builder()
                                    .email("pepe@email.com")
                                    .build()
                    );
                }
        );
        Assertions.assertNotNull(assertThrows);
    }

    @Test
    public void testUpdateSuccess() {
        CustomerDocument customer = null;
        List<SatisfactionDocument> satisfactions = null;
        Mockito.when(repository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);
        customer = CustomerDocument.builder()
                .email("pepe@email.com")
                .names("pepe")
                .id(ObjectId.get().toString())
                .satisfactions(new ArrayList<>())
                .build();
        satisfactions = new ArrayList<>();
        customer.setSatisfactions(satisfactions);
        Mockito.when(repository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(customer));
        Mockito.when(repository.save(Mockito.any(CustomerDocument.class)))
                .thenReturn(Mockito.any(CustomerDocument.class));
        Assertions.assertDoesNotThrow(
                () -> {
                    service.update(
                            CustomerUpdateCustomerRequestBean.builder()
                                    .email("pepe@email.com")
                                    .names("Pepe")
                                    .build()
                    );
                }
        );
    }

    @Test
    public void testUpdateUnexisrsCustomer() {
        CustomerValidationException assertThrows = null;
        Mockito.when(repository.existsByEmail(Mockito.anyString()))
                .thenReturn(false);
        assertThrows = Assertions.assertThrows(
                CustomerValidationException.class,
                () -> {
                    service.update(
                            CustomerUpdateCustomerRequestBean.builder()
                                    .email("pepe@email.com")
                                    .names("Pepe")
                                    .build()
                    );
                }
        );
        Assertions.assertNotNull(assertThrows);
    }

}
