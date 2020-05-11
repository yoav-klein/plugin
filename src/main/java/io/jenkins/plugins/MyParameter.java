package io.jenkins.plugins;


import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Util;

import hudson.model.ParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.ParameterValue;


import edu.umd.cs.findbugs.annotations.NonNull;
import net.sf.json.JSONObject;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;

/**
 * Parameter whose value is a string value.
 */
public class MyParameter extends SimpleParameterDefinition {

    private String defaultValue;
    private final boolean trim;

    @DataBoundConstructor
    public MyParameter(String name, String defaultValue, String description, boolean trim) {
        super(name, description);
        this.defaultValue = defaultValue;
        this.trim = trim;
    }

    public MyParameter(String name, String defaultValue, String description) {
        this(name, defaultValue, description, false);
    }
    
    public MyParameter(String name, String defaultValue) {
        this(name, defaultValue, null, false);
    }

    @Override
    public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
        if (defaultValue instanceof StringParameterValue) {
            StringParameterValue value = (StringParameterValue) defaultValue;
            return new MyParameter(getName(), value.value, getDescription());
        } else {
            return this;
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * 
     * @return original or trimmed defaultValue (depending on trim)
     */
    @Restricted(DoNotUse.class) // Jelly
    public String getDefaultValue4Build() {
        if (isTrim()) {
            return Util.fixNull(defaultValue).trim();
        }
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 
     * @return trim - {@code true}, if trim options has been selected, else return {@code false}.
     *      Trimming will happen when creating {@link StringParameterValue}s,
     *      the value in the config will not be changed.
     * @since 2.90
     */
    public boolean isTrim() {
        return trim;
    }
    
    @Override
    public StringParameterValue getDefaultParameterValue() {
        StringParameterValue value = new StringParameterValue(getName(), defaultValue, getDescription());
        if (isTrim()) {
            value.doTrim();
        }
        return value;
    }

    @Extension @Symbol({"string","stringParam"})
    public static class DescriptorImpl extends ParameterDescriptor {
        @Override
        @NonNull
        public String getDisplayName() {
            return "MyParameter";
        }

        @Override
        public String getHelpFile() {
            return "/help/parameter/string.html";
        }
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
        if (isTrim() && value!=null) {
            value.doTrim();
        }
        value.setDescription(getDescription());
        return value;
    }

    public ParameterValue createValue(String str) {
        StringParameterValue value = new StringParameterValue(getName(), str, getDescription());
        if (isTrim()) {
            value.doTrim();
        }
        return value;
    }
}