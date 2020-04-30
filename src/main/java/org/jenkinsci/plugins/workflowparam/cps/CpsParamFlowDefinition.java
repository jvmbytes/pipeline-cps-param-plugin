/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jenkinsci.plugins.workflowparam.cps;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.cps.CpsFlowFactoryAction2;
import org.jenkinsci.plugins.workflow.cps.persistence.PersistIn;
import org.jenkinsci.plugins.workflow.flow.DurabilityHintProvider;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinitionDescriptor;
import org.jenkinsci.plugins.workflow.flow.FlowDurabilityHint;
import org.jenkinsci.plugins.workflow.flow.FlowExecutionOwner;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

import static org.jenkinsci.plugins.workflow.cps.persistence.PersistenceContext.JOB;

@PersistIn(JOB)
public class CpsParamFlowDefinition extends FlowDefinition {

    private final String parameterName;

    @DataBoundConstructor
    public CpsParamFlowDefinition(String parameterName) {
        this.parameterName = parameterName.trim();
    }

    public String getParameterName() {
        return parameterName;
    }

    @Override
    public CpsFlowExecution create(
            FlowExecutionOwner owner, TaskListener listener, List<? extends Action> actions)
            throws Exception {

        // This little bit of code allows replays to work
        for (Action a : actions) {
            if (a instanceof CpsFlowFactoryAction2) {
                return ((CpsFlowFactoryAction2) a).create(this, owner, actions);
            }
        }

        Queue.Executable _build = owner.getExecutable();
        if (!(_build instanceof Run)) {
            throw new IOException("Can only pull a Jenkinsfile in a run");
        }
        Run<?, ?> build = (Run<?, ?>) _build;

        EnvVars envVars = build.getEnvironment(listener);
        String script = envVars.get(parameterName);

        FlowDurabilityHint hint = DurabilityHintProvider.suggestedFor(build.getParent());

        return new CpsFlowExecution(script, true, owner, hint);
    }

    @Extension
    public static class DescriptorImpl extends FlowDefinitionDescriptor {
        @Override
        @Nonnull
        public String getDisplayName() {
            return "Pipeline script from Parameter";
        }

    }
}
