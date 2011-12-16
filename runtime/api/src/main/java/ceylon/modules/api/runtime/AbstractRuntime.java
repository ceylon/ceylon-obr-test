/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package ceylon.modules.api.runtime;

import java.util.logging.Logger;

import ceylon.language.descriptor.Module;
import ceylon.modules.Configuration;
import ceylon.modules.api.util.CeylonToJava;
import ceylon.modules.spi.Constants;

import com.redhat.ceylon.cmr.api.Repository;

/**
 * Abstract Ceylon Modules runtime.
 * Useful for potential extension.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractRuntime implements ceylon.modules.spi.runtime.Runtime {

    public static final String MODULE_INFO_CLASS = ".module";
    public static final String RUN_INFO_CLASS = ".run";

    /**
     * Load module instance.
     *
     * @param cl         the classloader used to load the module descriptor.
     * @param moduleName the module name
     * @return new module instance or null if no such descriptor
     * @throws Exception for any error
     */
    public static Module loadModule(ClassLoader cl, String moduleName) throws Exception {
        final String moduleClassName = moduleName + MODULE_INFO_CLASS;
        final Class<?> moduleClass;
        try {
            moduleClass = cl.loadClass(moduleClassName);
        } catch (ClassNotFoundException ignored) {
            return null; // looks like no such module class is available
        }

        return SecurityActions.getModule(moduleClass);
    }

    protected static void invokeRun(ClassLoader cl, String moduleName, final String[] args) throws Exception {
        final String runClassName = moduleName + RUN_INFO_CLASS;
        final Class<?> runClass;
        try {
            runClass = cl.loadClass(runClassName);
        } catch (ClassNotFoundException ignored) {
            Logger.getLogger("ceylon.runtime").warning("No " + runClassName + " available, nothing to run!");
            return; // looks like no such run class is available
        }

        SecurityActions.invokeRun(runClass, args);
    }

    public void execute(Configuration conf) throws Exception {
        String exe = conf.module;
        // FIXME: argument checks could be done earlier
        if (exe == null)
            throw new IllegalArgumentException("No initial module defined");

        int p = exe.indexOf("/");
        if (p == 0)
            throw new IllegalArgumentException("Missing runnable info: " + exe);
        if (p == exe.length() - 1)
            throw new IllegalArgumentException("Missing version info: " + exe);

        String name = exe.substring(0, p > 0 ? p : exe.length());
        String mv = (p > 0 ? exe.substring(p + 1) : Repository.NO_VERSION);

        ClassLoader cl = createClassLoader(name, mv, conf);
        Module runtimeModule = loadModule(cl, name);
        if (runtimeModule == null)
            throw new IllegalArgumentException("Something went very wrong, missing runtime module!"); // TODO -- dump some more useful msg

        String mn = CeylonToJava.toString(runtimeModule.getName());
        if (name.equals(mn) == false)
            throw new IllegalArgumentException("Input module name doesn't match module's name: " + name + " != " + mn);
        String version = CeylonToJava.toString(runtimeModule.getVersion());
        if (mv.equals(version) == false && Constants.DEFAULT.toString().equals(name) == false)
            throw new IllegalArgumentException("Input module version doesn't match module's version: " + mv + " != " + version);

        invokeRun(cl, name, conf.arguments);
    }
}
