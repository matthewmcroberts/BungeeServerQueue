package com.matthew.plugin.modules.i18n;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class UTF8ResourceBundleControl extends Control {
    public static Control get() {
        return new UTF8ResourceBundleControl();
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, java.io.IOException {
        // The below code is copied from the default Control implementation.
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(loader.getResourceAsStream(resourceName)), StandardCharsets.UTF_8)) {
            bundle = new PropertyResourceBundle(reader); // At this point bundle is never null
        }
        return bundle;
    }
}
