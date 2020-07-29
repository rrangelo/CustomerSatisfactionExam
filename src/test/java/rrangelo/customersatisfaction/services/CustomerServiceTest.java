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
import rrangelo.customersatisfaction.beans.requests.customers.CustomerUpdateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.responses.customers.CustomerFindCustomerResponseBean;
import rrangelo.customersatisfaction.repositories.CustomerRepository;
import rrangelo.customersatisfaction.repositories.SatisfactionRepository;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
@SpringBootTest(classes = {Application.class})
public class CustomerServiceTest {

    List<CustomerFindCustomerResponseBean> documents;

    @Autowired
    private CustomerService service;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private SatisfactionRepository satisfactionRepository;

    @BeforeEach
    public void setUp() {
        documents = new ArrayList<>();
    }

    @AfterEach
    public void tearDown() {
        repository.findAll().stream()
                .forEach(customer -> repository.delete(customer));
    }

    /**
     * Successfully test of create method, of class CustomerService.
     */
    @Test
    public void testCreateSuccess() {
        System.out.println("create");
        service.create(
                CustomerCreateCustomerRequestBean.builder()
                        .email("pepe@email.com")
                        .names("Pepe")
                        .satisfactions(new ArrayList<>())
                        .build()
        );
        log.info("[CustomerServiceTest::testCreateSuccess] Finished");
    }

    /**
     * Wrong test of create method, of class CustomerService.
     */
//    @Test
//    public void testCreateErrorExistingCustomer() {
//        System.out.println("create");
//        testCreateSuccess();
//        Assertions.assertThrows(
//                CustomerValidationException.class,
//                () -> {
//                    service.create(
//                            CustomerCreateCustomerRequestBean.builder()
//                                    .email("pepe@email")
//                                    .names("Pepe")
//                                    .satisfactions(new ArrayList<>())
//                                    .build()
//                    );
//                }
//        );
//    }

    /**
     * Test of find method, of class CustomerService.
     */
    @Test
    public void testFindSuccess() {
        System.out.println("find");
        CustomerFindCustomerRequestBean request = null;
        service.create(
                CustomerCreateCustomerRequestBean.builder()
                        .email("pepe@email.com")
                        .names("Pepe")
                        .satisfactions(new ArrayList<>())
                        .build()
        );
        Assertions.assertEquals(
                CustomerFindCustomerResponseBean.builder()
                        .email("pepe@email.com")
                        .names("Pepe")
                        .satisfactions(new ArrayList<>())
                        .build(),
                service.find(
                        CustomerFindCustomerRequestBean.builder()
                                .email("pepe@email.com")
                                .build()
                )
        );
    }

    /**
     * Test of update method, of class CustomerService.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        service.create(
                CustomerCreateCustomerRequestBean.builder()
                        .email("pepe@email.com")
                        .names("Pancho")
                        .satisfactions(new ArrayList<>())
                        .build()
        );
        service.update(
                CustomerUpdateCustomerRequestBean.builder()
                        .email("pepe@email.com")
                        .names("Pepe")
                        .build()
        );
    }

}
