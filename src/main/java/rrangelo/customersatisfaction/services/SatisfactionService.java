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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionFindSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.responses.satisfactions.CustomerFindSatisfactionResponseBean;
import rrangelo.customersatisfaction.beans.responses.satisfactions.SatisfactionFindSatisfactionResponseBean;
import rrangelo.customersatisfaction.documents.CustomerDocument;
import rrangelo.customersatisfaction.documents.SatisfactionDocument;
import rrangelo.customersatisfaction.exceptions.validations.SatisfactionValidationException;
import rrangelo.customersatisfaction.repositories.CustomerRepository;
import rrangelo.customersatisfaction.repositories.SatisfactionRepository;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@Slf4j
@Service
public class SatisfactionService {

    private final SatisfactionRepository repository;

    private final CustomerRepository customerRepository;

    @Autowired
    public SatisfactionService(SatisfactionRepository repository, CustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    public void create(SatisfactionCreateSatisfactionRequestBean request) {
        Optional<CustomerDocument> customer = null;
        if (!customerRepository.existsByEmail(request.getCustomer().getEmail())) {
            log.error("{SatisfactionService::create} email: " + request.getCustomer().getEmail());
            throw new SatisfactionValidationException("Customer doesn't exists");
        }
        customer = customerRepository.findByEmail(request.getCustomer().getEmail());
        repository.save(
                SatisfactionDocument.builder()
                        .date(LocalDate.now())
                        .code(System.currentTimeMillis())
                        .customer(customer.get())
                        .qualification(request.getQualification())
                        .build()
        );
    }

    public List<SatisfactionFindSatisfactionResponseBean> find(SatisfactionFindSatisfactionRequestBean request) {
        List<SatisfactionDocument> satisfactions = null;
        if (request.getStartDate().isAfter(request.getEndDate())) {
            log.error("{SatisfactionService::find} startDate: " + request.getStartDate() + " - endDate: " + request.getEndDate());
            throw new SatisfactionValidationException("Dates aren't a period");
        }
        satisfactions = repository.findByDateBetween(request.getStartDate(), request.getEndDate().plusDays(1));
        return satisfactions.stream()
                .map(satisfaction -> {
                    return SatisfactionFindSatisfactionResponseBean.builder()
                            .customer(
                                    CustomerFindSatisfactionResponseBean.builder()
                                            .email(satisfaction.getCustomer().getEmail())
                                            .names(satisfaction.getCustomer().getNames())
                                            .build()
                            )
                            .qualification(satisfaction.getQualification())
                            .date(satisfaction.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                            .code(satisfaction.getCode())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void update(SatisfactionUpdateSatisfactionRequestBean request) {
        Optional<SatisfactionDocument> satisfaction = null;
        if (!customerRepository.existsByEmail(request.getCustomer().getEmail())) {
            log.error("{SatisfactionService::update} email: " + request.getCustomer().getEmail());
            throw new SatisfactionValidationException("Customer doesn't exists");
        }
        if (!repository.existsByCode(request.getCode())) {
            log.error("{SatisfactionService::update} code: " + request.getCode());
            throw new SatisfactionValidationException("Satisfaction doesn't exists");
        }
        satisfaction = repository.findByCode(request.getCode());
        satisfaction.get().setQualification(request.getQualification());
        satisfaction.get().setDate(LocalDate.now());
        repository.save(satisfaction.get());
    }

}
