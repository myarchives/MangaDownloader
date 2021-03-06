/*
 * The MIT License
 *
 * Copyright 2015 Andreas Giemza <andreas@giemza.net>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.andreasgiemza.mangadownloader.sites;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Andreas Giemza <andreas@giemza.net>
 */
public final class SiteHelper {

    private final static String implementationsPackage = "de.andreasgiemza.mangadownloader.sites.implementations";

    private SiteHelper() {
    }

    public static List<Site> getSites() {
        List<Site> sites = new LinkedList<>();

        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();

            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(implementationsPackage)) {
                    sites.add((Site) info.load().newInstance());
                }
            }
        } catch (InstantiationException | IllegalAccessException | IOException ex) {
        }

        Collections.sort(sites, new Comparator<Site>() {
            @Override
            public int compare(Site site1, Site site2) {
                return site1.getName().compareTo(site2.getName());
            }
        });

        return sites;
    }

    public static Site getInstance(String source) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(implementationsPackage)) {
                    final Class<?> clazz = info.load();

                    if (clazz.getSimpleName().equals(source)) {
                        try {
                            return (Site) clazz.newInstance();
                        } catch (InstantiationException | IllegalAccessException ex) {
                        }
                    }
                }
            }
        } catch (IOException ex) {
        }

        return null;
    }
}
