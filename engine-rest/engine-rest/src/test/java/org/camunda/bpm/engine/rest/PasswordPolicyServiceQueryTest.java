/*
 * Copyright © 2013-2019 camunda services GmbH and various authors (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.pwpolicy.DefaultPasswordPolicyImpl;
import org.camunda.bpm.engine.rest.util.container.TestContainerRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Miklas Boskamp
 */
public class PasswordPolicyServiceQueryTest extends AbstractRestServiceTest {

  private static final String QUERY_URL = TEST_RESOURCE_ROOT_PATH + PasswordPolicyRestService.PATH;

  @ClassRule
  public static TestContainerRule rule = new TestContainerRule();

  @Before
  public void setUpRuntimeData() {
    ProcessEngineConfigurationImpl mockedConfig = mock(ProcessEngineConfigurationImpl.class);

    when(processEngine.getProcessEngineConfiguration()).thenReturn(mockedConfig);
    when(mockedConfig.getPasswordPolicy()).thenReturn(new DefaultPasswordPolicyImpl());
  }

  @Test
  public void testGetPolicy() {
    given()
    .then()
      .expect()
        .statusCode(Status.OK.getStatusCode())
        .body("rules[0].placeholder", equalTo("LENGTH"))
        .body("rules[0].parameter.minLength", equalTo("10"))

        .body("rules[1].placeholder", equalTo("LOWERCASE"))
        .body("rules[1].parameter.minLowerCase", equalTo("1"))

        .body("rules[2].placeholder", equalTo("UPPERCASE"))
        .body("rules[2].parameter.minUpperCase", equalTo("1"))

        .body("rules[3].placeholder", equalTo("DIGIT"))
        .body("rules[3].parameter.minDigit", equalTo("1"))

        .body("rules[4].placeholder", equalTo("SPECIAL"))
        .body("rules[4].parameter.minSpecial", equalTo("1"))
    .when()
      .get(QUERY_URL);
  }
  
  @Test
  public void testCheckBadPasswordAgainstDefaultPolicy() {
    Map<String, Object> json = new HashMap<String, Object>();
    json.put("password", "password");
    
    given()
      .header("accept", MediaType.APPLICATION_JSON)
      .contentType(POST_JSON_CONTENT_TYPE)
      .body(json)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())

        .body("rules[0].placeholder", equalTo("LENGTH"))
        .body("rules[0].parameter.minLength", equalTo("10"))

        .body("rules[1].placeholder", equalTo("LOWERCASE"))
        .body("rules[1].parameter.minLowerCase", equalTo("1"))

        .body("rules[2].placeholder", equalTo("UPPERCASE"))
        .body("rules[2].parameter.minUpperCase", equalTo("1"))

        .body("rules[3].placeholder", equalTo("DIGIT"))
        .body("rules[3].parameter.minDigit", equalTo("1"))

        .body("rules[4].placeholder", equalTo("SPECIAL"))
        .body("rules[4].parameter.minSpecial", equalTo("1"))
    .when()
      .post(QUERY_URL);
  }
  
  @Test
  public void testCheckGoodPasswordAgainstDefaultPolicy() {
    Map<String, Object> json = new HashMap<String, Object>();
    json.put("password", "SuperS4f3Pa$$word");
    
    given()
    .header("accept", MediaType.APPLICATION_JSON)
    .contentType(POST_JSON_CONTENT_TYPE)
    .body(json)
  .then()
    .expect()
      .statusCode(Status.NO_CONTENT.getStatusCode())
  .when()
    .post(QUERY_URL);
  }
}