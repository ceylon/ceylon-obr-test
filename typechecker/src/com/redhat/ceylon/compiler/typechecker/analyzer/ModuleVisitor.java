package com.redhat.ceylon.compiler.typechecker.analyzer;

import static com.redhat.ceylon.compiler.typechecker.analyzer.Util.buildAnnotations;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.hasAnnotation;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.name;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

/**
 * Detect and populate the list of imports for modules.
 * In theory should only be called on module.ceylon and
 * package.ceylon files
 *
 * Put restrictions on how module.ceylon files are built today:
 *  - names and versions must be string literals or else the 
 *    visitor cannot extract them
 *  - imports must be "explicitly" defined, ie not imported as 
 *    List<Import> or else the module names cannot be extracted
 *
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 */
public class ModuleVisitor extends Visitor {
    
    /**
     * Instance of the visited module which will receive
     * the dependencies declaration
     */
    private Module mainModule;
    private final ModuleManager moduleManager;
    private final Package pkg;
    private Tree.CompilationUnit unit;
    private Phase phase = Phase.SRC_MODULE;

    public ModuleVisitor(ModuleManager moduleManager, Package pkg) {
        this.moduleManager = moduleManager;
        this.pkg = pkg;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    
    @Override
    public void visit(Tree.CompilationUnit that) {
        unit = that;
        super.visit(that);
    }
    
    private String getVersionString(Tree.QuotedLiteral that) {
        return that==null ? null : that.getText()
                .substring(1, that.getText().length() - 1);
    }
    
    @Override
    public void visit(Tree.ModuleDescriptor that) {
        super.visit(that);
        if (phase==Phase.SRC_MODULE) {
            String version = getVersionString(that.getVersion());
            List<String> name = getNameAsList(that.getImportPath());
            if (pkg.getNameAsString().isEmpty()) {
                that.addError("module descriptor encountered in root source directory");
            }
            else if (name.isEmpty()) {
                that.addError("missing module name");
            }
            else if (name.get(0).equals(Module.DEFAULT_MODULE_NAME)) {
                that.getImportPath().addError("reserved module name: default");
            }
            else if (name.size()==1 && name.get(0).equals("ceylon")) {
                that.getImportPath().addError("reserved module name: ceylon");
            }
            else {
                mainModule = moduleManager.getOrCreateModule(name, version);
                that.getImportPath().setModel(mainModule);
                mainModule.setUnit(unit.getUnit());
                mainModule.setVersion(version);
                String nameString = formatPath(that.getImportPath().getIdentifiers());
				if ( !pkg.getNameAsString().equals(nameString) ) {
                    that.getImportPath()
                        .addError("module name does not match descriptor location: " + 
                        		nameString + " should be " + pkg.getNameAsString(), 
                        		8000);
                }
                moduleManager.addLinkBetweenModuleAndNode(mainModule, unit);
                mainModule.setAvailable(true);
                buildAnnotations(that.getAnnotationList(), mainModule.getAnnotations());
            }
            HashSet<String> set = new HashSet<String>();
            Tree.ImportModuleList iml = that.getImportModuleList();
            if (iml!=null) {
                for (Tree.ImportModule im: iml.getImportModules()) {
                    Tree.ImportPath ip = im.getImportPath();
                    if (ip!=null) {
                        String mp = formatPath(ip.getIdentifiers());
                        if (!set.add(mp)) {
                            ip.addError("duplicate module import: " + mp);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
        super.visit(that);
        if (phase==Phase.REMAINING) {
            List<String> name = getNameAsList(that.getImportPath());
            if (pkg.getNameAsString().isEmpty()) {
                that.addError("package descriptor encountered in root source directory");
            }
            else if (name.isEmpty()) {
                that.addError("missing package name");
            }
            else if (name.get(0).equals(Module.DEFAULT_MODULE_NAME)) {
                that.getImportPath().addError("reserved module name: default");
            }
            else if (name.size()==1 && name.get(0).equals("ceylon")) {
                that.getImportPath().addError("reserved module name: ceylon");
            }
            else {
                that.getImportPath().setModel(pkg);
                pkg.setUnit(unit.getUnit());
                String nameString = formatPath(that.getImportPath().getIdentifiers());
				if ( !pkg.getNameAsString().equals(nameString) ) {
                    that.getImportPath()
                        .addError("package name does not match descriptor location: " + 
                        		nameString + " should be " + pkg.getNameAsString(), 
                                8000);
                }
                if (hasAnnotation(that.getAnnotationList(), "shared", unit.getUnit())) {
                    pkg.setShared(true);
                }
                else {
                    pkg.setShared(false);
                }
                buildAnnotations(that.getAnnotationList(), pkg.getAnnotations());
            }
        }
    }
    
    @Override
    public void visit(Tree.ImportModule that) {
        super.visit(that);
        if (phase==Phase.REMAINING) {
            if (that.getVersion()==null) {
                that.addError("missing module version");
            }
            String version = getVersionString(that.getVersion());
            List<String> name;
            Node node;
            if (that.getImportPath()!=null) {
            	name = getNameAsList(that.getImportPath());
            	node = that.getImportPath();
            }
            else if (that.getQuotedLiteral()!=null) {
            	name = asList(that.getQuotedLiteral().getText()
            			.replace("'", "").split("\\."));
            	node = that.getQuotedLiteral();
            }
            else {
            	name = Collections.emptyList();
            	node = null;
            }
            if (name.isEmpty()) {
                that.addError("missing module name");
            }
            else if (name.get(0).equals(Module.DEFAULT_MODULE_NAME)) {
            	if (that.getImportPath()!=null) {
            		node.addError("reserved module name: default");
            	}
            }
            else if (name.size()==1 && name.get(0).equals("ceylon")) {
                if (that.getImportPath()!=null) {
                    node.addError("reserved module name: ceylon");
                }
            }
            else if (name.size()>1 && name.get(0).equals("ceylon")
                    && name.get(1).equals("language")) {
                if (that.getImportPath()!=null) {
                    node.addError("the language module is imported implicitly");
                }
            }
            else {
                Module importedModule = moduleManager.getOrCreateModule(name,version);
                if (that.getImportPath()!=null) {
                	that.getImportPath().setModel(importedModule);
                }
                if (mainModule != null) {
                    if (importedModule.getVersion() == null) {
                        importedModule.setVersion(version);
                    }
                    ModuleImport moduleImport = moduleManager.findImport(mainModule, importedModule);
                    if (moduleImport == null) {
                        Tree.AnnotationList al = that.getAnnotationList();
                        boolean optional = hasAnnotation(al, "optional", unit.getUnit());
                        boolean export = hasAnnotation(al, "shared", unit.getUnit());
                        moduleImport = new ModuleImport(importedModule, optional, export);
                        buildAnnotations(al, moduleImport.getAnnotations());
                        mainModule.getImports().add(moduleImport);
                    }
                    moduleManager.addModuleDependencyDefinition(moduleImport, that);
                }
            }
        }
    }

    private List<String> getNameAsList(Tree.ImportPath that) {
        List<String> name = new ArrayList<String>();
        for (Tree.Identifier i: that.getIdentifiers()) {
           name.add(i.getText()); 
        }
        return name;
    }
    
    public enum Phase {
        SRC_MODULE,
        REMAINING
    }
    
    public Module getMainModule() {
        return mainModule;
    }
    
    @Override
    public void visit(Tree.Import that) {
        super.visit(that);
        Tree.ImportPath path = that.getImportPath();
        if (path!=null && 
                formatPath(path.getIdentifiers()).equals(Module.LANGUAGE_MODULE_NAME)) {
            Tree.ImportMemberOrTypeList imtl = that.getImportMemberOrTypeList();
            if (imtl!=null) {
                for (Tree.ImportMemberOrType imt: imtl.getImportMemberOrTypes()) {
                    if (imt.getAlias()!=null && imt.getIdentifier()!=null) {
                        String name = name(imt.getIdentifier());
                        String alias = name(imt.getAlias().getIdentifier());
                        Map<String, String> mods = unit.getUnit().getModifiers();
                        if (mods.containsKey(name)) {
                            mods.put(name, alias);
                        }
                    }
                }
            }
        }
    }
    
}
