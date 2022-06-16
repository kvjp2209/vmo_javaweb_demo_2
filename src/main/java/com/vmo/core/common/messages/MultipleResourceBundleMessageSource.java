package com.vmo.core.common.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class MultipleResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String XML_SUFFIX = ".xml";
    private static Logger LOG = LoggerFactory.getLogger(MultipleResourceBundleMessageSource.class);

    private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    //PathMatchingResourcePatternResolver will find duplicate results from *.jar
    //no it because default locale will be load with other locales
    private final ConcurrentMap<String, PropertiesHolder> cachedLoadFiles = new ConcurrentHashMap();

    @Override
    protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
        if (filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
            return refreshClassPathProperties(filename, propHolder);
        } else {
            return super.refreshProperties(filename, propHolder);
        }
    }

    private PropertiesHolder refreshClassPathProperties(String filename, PropertiesHolder propHolder) {
        Properties properties = new Properties();
        long lastModified = -1;
        try {
            List<String> loadedProperties = new ArrayList<>();
            String resourcePath = filename + PROPERTIES_SUFFIX;

            LOG.debug("refreshClassPathProperties filename " + resourcePath);
            Resource[] resources = resolver.getResources(resourcePath);
            if (resources.length <= 0) {
                resolver.getResources(filename + XML_SUFFIX);
            }
            for (Resource resource : resources) {
                String sourcePath = resource.getURI().toString()
                        .replace(PROPERTIES_SUFFIX, "")
                        .replace(XML_SUFFIX, "");
                LOG.debug("refreshClassPathProperties sourcePath " + sourcePath);
                if (loadedProperties.contains(sourcePath)) {
                    LOG.debug("skipping");
                    continue;
                }
                PropertiesHolder holder = super.refreshProperties(sourcePath, propHolder);
                holder.getProperties().forEach((key, value) -> {
                    properties.putIfAbsent(key, value);
                });
                loadedProperties.add(sourcePath);
                if (lastModified < resource.lastModified()) {
                    lastModified = resource.lastModified();
                }
            }
        } catch (IOException ignored) {
        }
        return new PropertiesHolder(properties, lastModified);
    }

//    protected List<String> calculateAllFilenames(String basename, Locale locale) {
//        List<String> a = super.calculateAllFilenames(basename, locale);
//        LOG.info("calculateAllFilenames " +  a.stream().collect(Collectors.joining("-----")));
//        return a;
//    }
//
//    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
//        List<String> a = super.calculateFilenamesForLocale(basename, locale);
//        LOG.info("calculateFilenamesForLocale " + a.stream().collect(Collectors.joining("-----")));
//        return a;
//    }
}
