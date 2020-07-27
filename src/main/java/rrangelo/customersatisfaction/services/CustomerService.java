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
import java.util.Objects;
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
import rrangelo.customersatisfaction.entities.CustomerEntity;
import rrangelo.customersatisfaction.entities.SatisfactionEntity;
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
            log.error("{CustomerService::create}");
            throw new RuntimeException("Customer exists");
        }
        final CustomerEntity customer = repository.save(
                CustomerEntity.builder()
                        .code(System.currentTimeMillis())
                        .email(request.getEmail())
                        .names(request.getNames())
                        .build()
        );
        if (CollectionUtils.isEmpty(request.getSatisfactions())) {
            return;
        }
        customer.setSatisfactions(
                request.getSatisfactions().stream()
                        .map(satisfaction -> {
                            return satisfactionRepository.save(
                                    SatisfactionEntity.builder()
                                            .date(LocalDate.now())
                                            .code(System.currentTimeMillis())
                                            .codeCustomer(customer.getCode())
                                            .qualification(satisfaction.getQualification())
                                            .build()
                            );
                        })
                        .collect(Collectors.toList())
        );
        repository.save(customer);
    }

    public CustomerFindCustomerResponseBean find(CustomerFindCustomerRequestBean request) {
        CustomerFindCustomerResponseBean response = null;
        Optional<CustomerEntity> customer = null;
        if (!repository.existsByEmail(request.getEmail())) {
            log.error("{CustomerService::find} email: " + request.getEmail());
            throw new RuntimeException("Customer doesn't exists");
        }
        customer = repository.findByEmail(request.getEmail());
        response = CustomerFindCustomerResponseBean.builder()
                .code(customer.get().getCode())
                .email(customer.get().getEmail())
                .names(customer.get().getNames())
                .satisfactions(new ArrayList<>())
                .build();
        if (CollectionUtils.isEmpty(customer.get().getSatisfactions())) {
            return response;
        }
        response.setSatisfactions(customer.get().getSatisfactions().stream()
                        .map(satisfaction -> {
                            return SatisfactionFindCustomerResponseBean.builder()
                                    .date(LocalDate.now())
                                    .code(satisfaction.getCode())
                                    .codeCustomer(satisfaction.getCodeCustomer())
                                    .qualification(satisfaction.getQualification())
                                    .build();
                        })
                        .collect(Collectors.toList())
        );
        return response;
    }

    public void update(CustomerUpdateCustomerRequestBean request) {
        Optional<CustomerEntity> customer = null;
        if (!repository.existsByEmail(request.getEmail())) {
            log.error("{CustomerService::update} email: " + request.getEmail());
            throw new RuntimeException("Customer doesn't exists");
        }
        customer = repository.findByEmail(request.getEmail());
        customer.get().setEmail(request.getEmail());
        customer.get().setNames(request.getNames());
        customer.get().setSatisfactions(
                request.getSatisfactions().stream()
                        .map(satisfaction -> {
                            final List<SatisfactionEntity> satisfactions = repository.findByEmail(request.getEmail()).get().getSatisfactions();
                            SatisfactionEntity satisfact = null;
                            List<SatisfactionEntity> satisfacts = null;
                            satisfact = SatisfactionEntity.builder()
                                    .code(satisfaction.getCode())
                                    .date(satisfaction.getDate())
                                    .qualification(satisfaction.getQualification())
                                    .build();
                            satisfacts = new ArrayList<>();
                            for (SatisfactionEntity satis : satisfactions) {
                                if (Objects.equals(satisfact.getCode(), satis.getCode())
                                        && !Objects.equals(satisfact.getQualification(), satis.getQualification())) {
                                    satisfact.setQualification(satis.getQualification());
                                }
                            }
                            satisfaction.setDate(LocalDate.now());
                            return satisfact;
                        })
                        .collect(Collectors.toList())
        );
        customer.get().getSatisfactions().addAll(
                request.getSatisfactions().stream()
                        .filter(satisfaction -> !satisfactionRepository.existsByCode(satisfaction.getCode()))
                        .map(satisfaction -> {
                            return SatisfactionEntity.builder()
                                    .code(satisfaction.getCode())
                                    .date(satisfaction.getDate())
                                    .qualification(satisfaction.getQualification())
                                    .build();
                        })
                        .collect(Collectors.toList())
        );
        repository.save(customer.get());
    }

}
