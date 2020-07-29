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
package rrangelo.customersatisfaction.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rrangelo.customersatisfaction.Application;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerCreateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerFindCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.CustomerCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.CustomerUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionFindSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.responses.customers.CustomerFindCustomerResponseBean;
import rrangelo.customersatisfaction.beans.responses.satisfactions.SatisfactionFindSatisfactionResponseBean;
import rrangelo.customersatisfaction.repositories.CustomerRepository;
import rrangelo.customersatisfaction.repositories.SatisfactionRepository;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
@SpringBootTest(classes = {Application.class})
public class SatisfactionServiceTest {

    List<CustomerFindCustomerResponseBean> documents;

    @Autowired
    private SatisfactionService service;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SatisfactionRepository repository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        documents = new ArrayList<>();
    }

    @AfterEach
    public void tearDown() {
        repository.findAll().stream()
                .forEach(satisfaction -> repository.delete(satisfaction));
        customerRepository.findAll().stream()
                .forEach(customer -> customerRepository.delete(customer));
    }

    /**
     * Successfully est of create method, of class CustomerService.
     */
    @Test
    public void testCreateSuccess() {
        System.out.println("create");
        customerService.create(
                CustomerCreateCustomerRequestBean.builder()
                        .email("pepe@email.com")
                        .names("Pepe")
                        .satisfactions(new ArrayList<>())
                        .build()
        );
        service.create(
                SatisfactionCreateSatisfactionRequestBean.builder()
                        .qualification(2)
                        .customer(
                                CustomerCreateSatisfactionRequestBean.builder()
                                        .email("pepe@email.com")
                                        .build()
                        )
                        .build()
        );
        log.info("[SatisfactionServiceTest::testCreateSuccess] Finished");
    }

    /**
     * Test of find method, of class CustomerService.
     */
    @Test
    public void testFindSuccess() {
        System.out.println("find");
        CustomerFindCustomerRequestBean request = null;
        customerService.create(
                CustomerCreateCustomerRequestBean.builder()
                        .email("pepe@email.com")
                        .names("Pepe")
                        .satisfactions(new ArrayList<>())
                        .build()
        );
        service.create(
                SatisfactionCreateSatisfactionRequestBean.builder()
                        .qualification(2)
                        .customer(
                                CustomerCreateSatisfactionRequestBean.builder()
                                        .email("pepe@email.com")
                                        .build()
                        )
                        .build()
        );
        Assertions.assertNotNull(
                service.find(
                        SatisfactionFindSatisfactionRequestBean.builder()
                                .startDate(LocalDate.of(2020,01,01))
                                .endDate(LocalDate.of(2022,01,01))
                                .build()
                )
        );
    }

    /**
     * Test of update method, of class CustomerService.
     */
    @Test
    public void testUpdate() {
        List<SatisfactionFindSatisfactionResponseBean> list = null;
        System.out.println("update");
        customerService.create(
                CustomerCreateCustomerRequestBean.builder()
                        .email("pepe@email.com")
                        .names("Pepe")
                        .satisfactions(new ArrayList<>())
                        .build()
        );
        service.create(
                SatisfactionCreateSatisfactionRequestBean.builder()
                        .qualification(2)
                        .customer(
                                CustomerCreateSatisfactionRequestBean.builder()
                                        .email("pepe@email.com")
                                        .build()
                        )
                        .build()
        );
        list = service.find(
                SatisfactionFindSatisfactionRequestBean.builder()
                        .startDate(LocalDate.of(2020,01,01))
                        .endDate(LocalDate.of(2022,01,01))
                        .build()
        );
        service.update(
                SatisfactionUpdateSatisfactionRequestBean.builder()
                        .code(list.get(0).getCode())
                        .qualification(3)
                        .customer(
                                CustomerUpdateSatisfactionRequestBean.builder()
                                        .email(list.get(0).getCustomer().getEmail())
                                        .build()
                        )
                        .build()
        );
    }

}
