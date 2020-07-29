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
package rrangelo.customersatisfaction.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionCreateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionFindSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.requests.satisfactions.SatisfactionUpdateSatisfactionRequestBean;
import rrangelo.customersatisfaction.beans.responses.satisfactions.SatisfactionFindSatisfactionResponseBean;
import rrangelo.customersatisfaction.exceptions.responses.SatisfactionResponseException;
import rrangelo.customersatisfaction.services.SatisfactionService;

/**
 *
 * @author Ramon Rangel Osorio <ramon.rangel@protonmail.com>
 */
@RestController
@RequestMapping("/satisfaction")
public class SatisfactionController {

    @Autowired
    private SatisfactionService service;

    @PostMapping
    public void create(@RequestBody SatisfactionCreateSatisfactionRequestBean request) {
        try {
            service.create(request);
        } catch (Exception e) {
            throw new SatisfactionResponseException(e.getMessage());
        }
    }

    @GetMapping
    public List<SatisfactionFindSatisfactionResponseBean> find(
            @RequestParam(value = "start") String startDate,
            @RequestParam(value = "end") String endDate
    ) {
        try {
            return service.find(
                    SatisfactionFindSatisfactionRequestBean.builder()
                            .startDate(LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .endDate(LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .build()
            );
        } catch (Exception e) {
            throw new SatisfactionResponseException(e.getMessage());
        }
    }

    @PutMapping
    public void update(@RequestBody SatisfactionUpdateSatisfactionRequestBean request) {
        try {
            service.update(request);
        } catch (Exception e) {
            throw new SatisfactionResponseException(e.getMessage());
        }
    }

}
