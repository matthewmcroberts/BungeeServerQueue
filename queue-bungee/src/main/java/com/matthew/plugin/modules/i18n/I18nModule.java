package com.matthew.plugin.modules.i18n;

import com.matthew.plugin.api.Module;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class I18nModule implements Module {

    private final Map<Locale, ResourceBundle> translations = new ConcurrentHashMap<>();

    private final Set<Locale> availableLocales = new HashSet<>();

    private final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    private final String baseName;

    public void addTranslation(Locale locale, ResourceBundle bundle, boolean overwrite) {
        if (overwrite || !translations.containsKey(locale)) {
            translations.put(locale, bundle);
        }
        availableLocales.add(locale);
    }

    public Set<Locale> getAvailableLocales() {
        return Collections.unmodifiableSet(availableLocales);
    }

    public String getTranslation(String key, Locale locale) {
        ResourceBundle bundle = translations.get(locale);
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }

        // Fallback to default locale
        Locale defaultLocale = Locale.getDefault();
        bundle = translations.get(defaultLocale);
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }

        // If no translation found, return key
        return key;
    }

    @Override
    public void setUp() {
        for (Locale locale : getAvailableLocales()) {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, loader, UTF8ResourceBundleControl.get());
            ResourceBundle processedBundle = processResourceBundle(bundle);
            addTranslation(locale, processedBundle, true);
        }
    }

    @Override
    public void teardown() {
        // No allocated resources in need of teardown
    }

    private ResourceBundle processResourceBundle(ResourceBundle bundle) {
        Map<String, String> processedEntries = bundle.keySet().stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> bundle.getString(key).replace('&', '§')
                ));

        return new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return processedEntries.get(key);
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.enumeration(processedEntries.keySet());
            }
        };
    }
}
