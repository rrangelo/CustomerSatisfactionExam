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
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerCreateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerFindCustomerRequestBean;
import rrangelo.customersatisfaction.beans.requests.customers.CustomerUpdateCustomerRequestBean;
import rrangelo.customersatisfaction.beans.responses.customers.CustomerFindCustomerResponseBean;
import rrangelo.customersatisfaction.beans.responses.customers.SatisfactionFindCustomerResponseBean;
import rrangelo.customersatisfaction.documents.CustomerDocument;
import rrangelo.customersatisfaction.documents.SatisfactionDocument;
import rrangelo.customersatisfaction.repositories.CustomerRepository;
import rrangelo.customersatisfaction.repositories.SatisfactionRepository;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
@Service
public class CustomerService {

    private final CustomerRepository repository;

    private final SatisfactionRepository satisfactionRepository;

    @Autowired
    public CustomerService(CustomerRepository repository, SatisfactionRepository satisfactionRepository) {
        this.repository = repository;
        this.satisfactionRepository = satisfactionRepository;
    }

    public void create(CustomerCreateCustomerRequestBean request) {
        if (repository.existsByEmail(request.getEmail())) {
            log.error("{CustomerService::create} email: " + request.getEmail());
            throw new RuntimeException("Customer exists");
        }
        final CustomerDocument customer = repository.save(
                CustomerDocument.builder()
                        .email(request.getEmail())
                        .names(request.getNames())
                        .satisfactions(new ArrayList<>())
                        .build()
        );
        if (CollectionUtils.isEmpty(request.getSatisfactions())) {
            return;
        }
        request.getSatisfactions().stream()
                .forEach(satisfaction -> {
                    satisfactionRepository.save(SatisfactionDocument.builder()
                            .date(LocalDate.now())
                            .code(System.currentTimeMillis())
                            .customer(customer)
                            .qualification(satisfaction.getQualification())
                            .build()
                    );
                });
    }

    public CustomerFindCustomerResponseBean find(CustomerFindCustomerRequestBean request) {
        Optional<CustomerDocument> customer = null;
        if (!repository.existsByEmail(request.getEmail())) {
            log.error("{CustomerService::find} email: " + request.getEmail());
            throw new RuntimeException("Customer doesn't exists");
        }
        customer = repository.findByEmail(request.getEmail());
        return CustomerFindCustomerResponseBean.builder()
                .email(customer.get().getEmail())
                .names(customer.get().getNames())
                .satisfactions(
                        satisfactionRepository.existsByCustomer(customer.get().getId())
                        ? satisfactionRepository.findAllByCustomer(customer.get().getId()).stream()
                                .map(satisfaction -> {
                                    return SatisfactionFindCustomerResponseBean.builder()
                                            .qualification(satisfaction.getQualification())
                                            .date(satisfaction.getDate())
                                            .code(satisfaction.getCode())
                                            .build();
                                }).collect(Collectors.toList())
                        : new ArrayList<>()
                )
                .build();
    }

    public void update(CustomerUpdateCustomerRequestBean request) {
        Optional<CustomerDocument> customer = null;
        if (!repository.existsByEmail(request.getEmail())) {
            log.error("{CustomerService::update} email: " + request.getEmail());
            throw new RuntimeException("Customer doesn't exists");
        }
        customer = repository.findByEmail(request.getEmail());
        customer.get().setEmail(request.getEmail());
        customer.get().setNames(request.getNames());
        repository.save(customer.get());
    }

}
