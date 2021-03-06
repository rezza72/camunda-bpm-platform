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
package org.camunda.bpm.engine.test.api.authorization;

import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.impl.cfg.auth.DefaultAuthorizationProvider;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.camunda.bpm.engine.task.Task;

/**
 * @author Johannes Heinemann
 */
public class MyExtendedPermissionDefaultAuthorizationProvider extends DefaultAuthorizationProvider{

  public AuthorizationEntity[] newTaskAssignee(Task task, String oldAssignee, String newAssignee) {
    AuthorizationEntity[] authorizations = super.newTaskAssignee(task, oldAssignee, newAssignee);
    authorizations[0].addPermission(Permissions.DELETE);
    return authorizations;
  }
}
