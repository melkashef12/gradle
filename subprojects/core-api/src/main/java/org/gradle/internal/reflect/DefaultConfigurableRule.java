/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.internal.reflect;

import org.gradle.api.Action;
import org.gradle.api.ActionConfiguration;
import org.gradle.api.artifacts.CacheableRule;
import org.gradle.api.internal.DefaultActionConfiguration;

import java.util.Arrays;

public class DefaultConfigurableRule<DETAILS> implements ConfigurableRule<DETAILS> {
    private final static Object[] NO_PARAMS = new Object[0];

    private final Class<? extends Action<DETAILS>> rule;
    private final Object[] ruleParams;
    private final boolean cacheable;

    private DefaultConfigurableRule(Class<? extends Action<DETAILS>> rule, Object[] ruleParams) {
        this.rule = rule;
        this.ruleParams = ruleParams;
        this.cacheable = hasCacheableAnnotation(rule);
    }

    private static <DETAILS> boolean hasCacheableAnnotation(Class<? extends Action<DETAILS>> rule) {
        return JavaReflectionUtil.getAnnotation(rule, CacheableRule.class) != null;
    }

    public static <DETAILS> ConfigurableRule<DETAILS> of(Class<? extends Action<DETAILS>> rule) {
        return new DefaultConfigurableRule<DETAILS>(rule, NO_PARAMS);
    }

    public static <DETAILS> ConfigurableRule<DETAILS> of(Class<? extends Action<DETAILS>> rule, Action<? super ActionConfiguration> action) {
        Object[] params = NO_PARAMS;
        if (action != null) {
            ActionConfiguration configuration = new DefaultActionConfiguration();
            action.execute(configuration);
            params = configuration.getParams();
        }
        return new DefaultConfigurableRule<DETAILS>(rule, params);
    }

    @Override
    public Class<? extends Action<DETAILS>> getRuleClass() {
        return rule;
    }

    @Override
    public Object[] getRuleParams() {
        return ruleParams;
    }

    @Override
    public boolean isCacheable() {
        return cacheable;
    }

    @Override
    public String toString() {
        return "DefaultConfigurableRule{" +
            "rule=" + rule +
            ", ruleParams=" + Arrays.toString(ruleParams) +
            '}';
    }
}
